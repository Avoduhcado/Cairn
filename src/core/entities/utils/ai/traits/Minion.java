package core.entities.utils.ai.traits;

import java.awt.geom.Point2D;

import core.entities.Actor;
import core.entities.Enemy;
import core.entities.Entity;
import core.entities.interfaces.Combatant;
import core.entities.interfaces.Intelligent;
import core.entities.utils.CharState;
import core.entities.utils.ai.AIAction;
import core.entities.utils.ai.DocileAI;

public class Minion extends Trait {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Intelligent leader;
	private int wanderRange;
	
	public Minion(Intelligent leader) {
		this.leader = leader;
		setWanderRange(275);
	}
	
	@Override
	public void setHost(Intelligent host) {
		this.host = host;
		
		if(leader != null) {
			for(Trait t : this.leader.getIntelligence().getTraits()) {
				if(t instanceof PackLeader) {
					((PackLeader) t).addMinion(this.host);
					break;
				}
			}
		} else {
			this.host.changeIntelligence(new DocileAI());
			if(((Actor) this.host).getState().canAct()) {
				((Actor) this.host).setState(CharState.IDLE);
			}
		}
	}
	
	@Override
	public void process() {
		if(leader != null && ((Actor) host).getState().canAct()) {
			if(!((Enemy) host).getIntelligence().isChasing() && Point2D.distance(((Entity) leader).getX(), ((Entity) leader).getY(),
					((Entity) host).getX(), ((Entity) host).getY()) > wanderRange) {
				host.getIntelligence().setAction(new AIAction() {
					/**
					 * 
					 */
					private static final long serialVersionUID = 1L;

					public void act(Intelligent host, Combatant target) {
						host.approach(((Entity) leader).getPositionAsPoint());
						if(Point2D.distance(((Entity) leader).getX(), ((Entity) leader).getY(), 
								((Entity) host).getX(), ((Entity) host).getY()) <= wanderRange) {
							((Actor) host).setDirection(((Actor) leader).getDirection());
							host.getIntelligence().setAction(null);
						}
					}
				});
			}
			
			if(((Actor) leader).getState() == CharState.DEAD) {
				((Actor) host).setDirection(((Entity) leader).getX() > ((Entity) host).getX() ? 0 : 1);
				host.changeIntelligence(new DocileAI());
				return;
			}
		}
	}

	@Override
	public void alert(Combatant target) {
		if(leader != null) {
			host.getIntelligence().setTarget(target);
			
			leader.getIntelligence().alert(target);
		}
	}
	
	public void setWanderRange(int range) {
		this.wanderRange = range;
	}

}
