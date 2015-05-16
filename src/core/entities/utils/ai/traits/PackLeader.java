package core.entities.utils.ai.traits;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import core.entities.Actor;
import core.entities.Enemy;
import core.entities.interfaces.Combatant;
import core.entities.interfaces.Intelligent;
import core.entities.utils.CharState;

public class PackLeader extends Trait {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<Intelligent> minions = new ArrayList<Intelligent>();
	private Point2D rallyPoint;
	private int defaultFacing;
	private int wanderRange = 300;
	
	public PackLeader(ArrayList<Intelligent> minions) {
		if(minions != null)
			setMinions(minions);
	}
	
	@Override
	public void process() {
		for(int i = 0; i<minions.size(); i++) {
			if(((Actor) minions.get(i)).getState() == CharState.DEAD) {
				System.out.println(((Actor) minions.get(i)).getID() + " died!");
				minions.remove(i);
			}
		}
		
		if(rallyPoint != null && !((Enemy) host).getIntelligence().isChasing()
				&& Point2D.distance(rallyPoint.getX(), rallyPoint.getY(), ((Actor) host).getX(), ((Actor) host).getYPlane()) > wanderRange) {
			host.getIntelligence().approach(rallyPoint);
		} else if(rallyPoint != null && !((Enemy) host).getIntelligence().isChasing()
				&& Point2D.distance(rallyPoint.getX(), rallyPoint.getY(), ((Actor) host).getX(), ((Actor) host).getYPlane()) <= 50) {
			host.getIntelligence().setApproachVector(0, 0);
			((Actor) host).setDirection(defaultFacing);
		}
	}

	@Override
	public void alert(Combatant target) {
		host.getIntelligence().setTarget(target);
		
		for(Intelligent m : minions) {
			m.getIntelligence().setTarget(host.getIntelligence().getTarget());
		}
	}
	
	public ArrayList<Intelligent> getMinions() {
		return minions;
	}
	
	public void setMinions(ArrayList<Intelligent> minions) {
		this.minions = minions;
	}
	
	public void addMinion(Intelligent minion) {
		this.minions.add(minion);
	}

	public Point2D getRallyPoint() {
		return rallyPoint;
	}

	public void setRallyPoint(Point2D rallyPoint, int defaultFacing) {
		this.rallyPoint = rallyPoint;
		this.defaultFacing = defaultFacing;
	}

}
