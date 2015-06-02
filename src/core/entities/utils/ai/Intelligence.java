package core.entities.utils.ai;

import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RectangularShape;
import java.io.Serializable;
import java.util.ArrayList;

import com.esotericsoftware.spine.AnimationState;

import core.entities.Actor;
import core.entities.Entity;
import core.entities.interfaces.Combatant;
import core.entities.interfaces.Intelligent;
import core.entities.utils.CharState;
import core.entities.utils.ai.traits.Trait;

public abstract class Intelligence implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected Intelligent host;
	protected Combatant target;
	
	protected AIAction action;
	protected ArrayList<Trait> traits = new ArrayList<Trait>();
	
	protected Shape sight;
	protected Shape hearing;
	// TODO ViewAngle, ViewDistance parameters
	
	public abstract void pickAction();
	public abstract void alert(Combatant target);
	
	public void update() {
		for(Trait t : traits) {
			t.process();
		}
	}
	
	public Intelligent getHost() {
		return host;
	}
	
	public void setHost(Intelligent host) {
		this.host = host;
		buildSight(((Actor) host).getDirection());
	}
	
	public Combatant getTarget() {
		return target;
	}
	
	public void setTarget(Combatant target) {
		if(this.target != target) {
			this.target = target;
			convertSight(target != null);
			setAction(null);
		}
		if(target == null) {
			setAction(null);
		}
	}
	
	public AIAction getAction() {
		return action;
	}
	
	public void setAction(AIAction action) {
		this.action = action;
	}
	
	public boolean applyTraitStateModifier(CharState state, AnimationState animState) {
		return false;
	}

	public ArrayList<Trait> getTraits() {
		return traits;
	}
	
	public void setTraits(ArrayList<Trait> traits) {
		for(Trait t : traits) {
			t.setHost(host);
		}
		this.traits = traits;
	}
	
	public void addTrait(Trait trait) {
		trait.setHost(host);
		this.traits.add(trait);
	}
	
	public boolean isChasing() {
		return target != null;
	}
	
	public Shape getSight() {
		return sight;
	}

	public void buildSight(int direction) {
		sight = new Arc2D.Double();
		((Arc2D) sight).setAngleStart(direction == 0 ? 325 : 145);
		((Arc2D) sight).setAngleExtent(70);
		((Arc2D) sight).setArcType(Arc2D.PIE);
		((Arc2D) sight).setFrameFromCenter(((Entity) host).getBox().getCenterX(), ((Entity) host).getY(),
				((Entity) host).getPosition().x - 400, ((Entity) host).getPosition().y - 200);

		hearing = new Arc2D.Double();
		((Arc2D) hearing).setAngleStart(210);
		((Arc2D) hearing).setAngleExtent(300);
		((Arc2D) hearing).setArcType(Arc2D.CHORD);
		((Arc2D) hearing).setFrameFromCenter(((Entity) host).getBox().getCenterX() + 175, ((Entity) host).getY(),
				((Entity) host).getPosition().x - 275, ((Entity) host).getPosition().y - 150);
	}
	
	public void convertSight(boolean target) {
		if(target && sight instanceof Arc2D) {
			sight = new Ellipse2D.Double(((RectangularShape) sight).getX(), ((RectangularShape) sight).getY(),
					((RectangularShape) sight).getWidth(), ((RectangularShape) sight).getHeight());
		} else if(!target && sight instanceof Ellipse2D) {
			buildSight(((Actor) host).getDirection());
		}
	}
	
	public void flipSight(int direction) {
		if(sight instanceof Arc2D) {
			((Arc2D) sight).setAngleStart(direction == 0 ? 325 : 145);
		}
	}
	
	public Shape getHearing() {
		return hearing;
	}

}
