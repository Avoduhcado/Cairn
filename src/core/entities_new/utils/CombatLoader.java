package core.entities_new.utils;

import core.entities_new.CharacterState;
import core.entities_new.Entity;
import core.entities_new.event.CombatEvent;
import core.entities_new.event.CombatListener;

public final class CombatLoader {

	public static CombatListener plainCombatant() {
		return new CombatListener() {
			private Entity prevAttacker;

			@Override
			public void hit(CombatEvent e) {
				if(prevAttacker != e.getAttacker()) {
					e.getTarget().changeStateForced(CharacterState.HIT);
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
