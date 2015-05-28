package core.entities.utils.ai.traits;

import java.awt.geom.Point2D;

import core.entities.Actor;
import core.entities.Enemy;
import core.entities.Entity;
import core.entities.interfaces.Combatant;
import core.entities.interfaces.Intelligent;
import core.entities.utils.CharState;
import core.entities.utils.ai.Personality;

public class Minion extends Trait {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Intelligent leader;
	private int wanderRange = 400;
	
	public Minion(Intelligent leader) {
		this.leader = leader;
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
			System.out.println("FEARLESS LEADER!");
			this.host.getIntelligence().setPersonality(Personality.DOCILE);
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
				host.approach(((Entity) leader).getPositionAsPoint());
			} else if(Point2D.distance(((Entity) leader).getX(), ((Entity) leader).getY(), 
					((Entity) host).getX(), ((Entity) host).getY()) <= wanderRange) {
				//host.getIntelligence().setApproachVector(0, 0);
			} else {
				((Actor) host).setDirection(((Actor) leader).getDirection());
			}
			
			if(((Actor) leader).getState() == CharState.DEAD) {
				System.out.println("FEARLESS LEADER!");
				host.getIntelligence().setPersonality(Personality.DOCILE);
				((Actor) host).setDirection(((Entity) leader).getX() > ((Entity) host).getX() ? 0 : 1);
				if(((Actor) host).getState().canAct()) {
					((Actor) host).setState(CharState.IDLE);
				}
				leader = null;
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

}
