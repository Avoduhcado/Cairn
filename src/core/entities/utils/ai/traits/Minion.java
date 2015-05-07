package core.entities.utils.ai.traits;

import java.awt.geom.Point2D;

import core.entities.Actor;
import core.entities.Enemy;
import core.entities.Entity;
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
	public void process() {
		if(leader != null && ((Actor) host).getState().canAct()) {
			if(!((Enemy) host).getIntelligence().isChasing() && Point2D.distance(((Entity) leader).getX(), ((Entity) leader).getY(),
					((Entity) host).getX(), ((Entity) host).getY()) > wanderRange) {
				host.getIntelligence().approach(null, (Entity) leader);
			} else if(Point2D.distance(((Entity) leader).getX(), ((Entity) leader).getY(), 
					((Entity) host).getX(), ((Entity) host).getY()) <= wanderRange && host.getIntelligence().isApproaching()) {
				host.getIntelligence().setApproachVector(0, 0);
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
		} else if(!((Actor) host).getState().canAct()) {
			host.getIntelligence().setApproachVector(0, 0);
		}
	}

}