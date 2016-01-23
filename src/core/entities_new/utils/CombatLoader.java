package core.entities_new.utils;

import core.entities_new.State;
import core.entities_new.components.Combatant;

import org.jbox2d.common.Vec2;

import core.entities_new.Entity;
import core.entities_new.event.CombatEvent;
import core.entities_new.event.CombatListener;
import core.entities_new.event.StateChangeEvent;

public final class CombatLoader {

	public static Combatant plainCombatant() {
		return new Combatant() {
			private Entity prevAttacker;

			@Override
			public void hit(CombatEvent e) {
				if(prevAttacker != e.getAttacker()) {
					Entity target = e.getTarget();
					Entity attacker = e.getAttacker();
					
					Vec2 direction = target.getBody().getPosition().sub(attacker.getBody().getPosition());
					
					target.fireEvent(new StateChangeEvent(State.HIT, true));
					target.getBody().applyLinearImpulse(new Vec2(5f * (direction.x / Math.abs(direction.x)), -1.5f * (direction.y / Math.abs(direction.y))),
							target.getBody().getWorldCenter());
					this.prevAttacker = e.getAttacker();
				}
			}

		};
	}
	
	public static CombatListener plainCombatant2() {
		return new CombatListener() {
			private Entity prevAttacker;

			@Override
			public void hit(CombatEvent e) {
				if(prevAttacker != e.getAttacker()) {
					Entity target = e.getTarget();
					Entity attacker = e.getAttacker();
					
					Vec2 direction = target.getBody().getPosition().sub(attacker.getBody().getPosition());
					
					target.fireEvent(new StateChangeEvent(State.HIT, true));
					target.getBody().applyLinearImpulse(new Vec2(5f * (direction.x / Math.abs(direction.x)), -1.5f * (direction.y / Math.abs(direction.y))),
							target.getBody().getWorldCenter());
					this.prevAttacker = e.getAttacker();
					
					/*Entity particleEmitter = new Entity("Bone Shards", 
							e.getTarget().getBody().getPosition().x * Stage_new.SCALE_FACTOR,
							e.getTarget().getBody().getPosition().y * Stage_new.SCALE_FACTOR,
							e.getTarget().getContainer());
					particleEmitter.setRender(new Particle("Bone Shards/Shard1",
							new Vector2f(e.getTarget().getBody().getPosition().x * Stage_new.SCALE_FACTOR,
									e.getTarget().getBody().getPosition().y * Stage_new.SCALE_FACTOR),
							new Vector2f(0.01f, 0.025f)));
					e.getTarget().getContainer().addEntity(particleEmitter);*/
				}
			}

		};
	}
	
}
