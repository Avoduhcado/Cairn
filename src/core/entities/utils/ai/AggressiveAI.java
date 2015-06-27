package core.entities.utils.ai;

import core.Theater;
import core.entities.Actor;
import core.entities.Entity;
import core.entities.interfaces.Combatant;
import core.entities.interfaces.Intelligent;
import core.entities.utils.ai.traits.Trait;
import core.setups.Stage;

public class AggressiveAI extends Intelligence {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public AggressiveAI(float aggression) {
		this.setRating(aggression);
	}
	
	@Override
	public void update() {
		super.update();

		if(((Actor) host).getState().canAct()) {
			if(this.action == null) {
				pickAction();
			} else {
				action.act(host, target);
			}
		}
	}

	@Override
	public void alert(Combatant target) {
		setTarget(target);

		for(Trait t : traits) {
			t.alert(target);
		}
	}

	@Override
	public void pickAction() {
		if(target == null) {
			search();
		} else {
			if(((Combatant) host).canReach((Entity) target)) {
				planAttack();
			} else {
				if(((Combatant) host).getStats().getStamina().getCurrentPercent() < 0.65f && Math.random() > getRating()) {
					rechargeStamina();
				} else {
					approach();
				}
			}
		}
	}
	
	private void search() {
		setAction(new AIAction() {
			public void act(Intelligent host, Combatant target) {
				for(Actor a : ((Stage) Theater.get().getSetup()).getCast()) {
					if(a instanceof Combatant && host != a && ((Combatant) host).getReputation().isEnemy(((Combatant) a).getReputation())
							&& getSight().intersects(a.getBox())) {
						alert((Combatant) a);
						host.getIntelligence().setAction(null);
						break;
					}
				}
			}
		});
	}
	
	private void planAttack() {
		if(((Combatant) host).getStats().getStamina().getCurrentPercent() < 0.35f && Math.random() > getRating()) {
			setAction(new AIAction() {
				float timer = 1.5f;
				public void act(Intelligent host, Combatant target) {
					timer -= Theater.getDeltaSpeed(0.025f);
					if(timer <= 0) {
						host.getIntelligence().setAction(null);
					}
				}
			});
		} else {
			setAction(new AIAction() {					
				public void act(Intelligent host, Combatant target) {
					((Actor) host).lookAt((Entity) target);
					((Combatant) host).attack();
				}
			});
		}
	}
	
	private void rechargeStamina() {
		setAction(new AIAction() {
			float timer = 2.5f;
			public void act(Intelligent host, Combatant target) {
				timer -= Theater.getDeltaSpeed(0.025f);
				if(timer <= 0 || (((Combatant) host).canReach((Entity) target) 
						&& ((Combatant) host).getStats().getStamina().getCurrentPercent() > 0.5f)) {
					host.getIntelligence().setAction(null);
				}
			}
		});
	}

	private void approach() {
		setAction(new AIAction() {
			float timer = 5.25f;
			
			public void act(Intelligent host, Combatant target) {
				if(host.getIntelligence().getSight().intersects(((Entity) target).getBox())) {
					alert(target);
					host.approach(((Actor) target).getPositionAsPoint());
					timer = 5.25f;
					if(((Combatant) host).canReach((Entity) target)) {
						host.getIntelligence().setAction(null);
					}
				} else {
					host.approach(((Actor) target).getPositionAsPoint());
					timer -= Theater.getDeltaSpeed(0.025f);
					if(timer <= 0) {
						host.getIntelligence().setTarget(null);
					}
				}
			}
		});
	}
	
}
