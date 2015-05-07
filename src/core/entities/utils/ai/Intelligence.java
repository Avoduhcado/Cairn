package core.entities.utils.ai;

import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RectangularShape;
import java.io.Serializable;
import java.util.ArrayList;

import org.lwjgl.util.vector.Vector2f;

import com.esotericsoftware.spine.AnimationState;

import core.Theater;
import core.entities.Actor;
import core.entities.Entity;
import core.entities.interfaces.Combatant;
import core.entities.interfaces.Intelligent;
import core.entities.utils.CharState;
import core.entities.utils.ai.traits.Trait;
import core.setups.Stage;

public class Intelligence implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Intelligent host;
	// TODO Factions/Alliances to determine who to attack
	private Combatant target;
	
	private Personality personality;
	private ArrayList<Trait> traits = new ArrayList<Trait>();
	
	private Shape sight;
	private Shape hearing;
	// TODO ViewAngle, ViewDistance parameters
	private Vector2f approachVector;
	private boolean chase;
	private float chaseTimer = 0f;
	
	public Intelligence(Intelligent host) {
		this.host = host;
		
		personality = Personality.NEUTRAL;
		
		buildSight();
		
		setApproachVector(new Vector2f());
	}
	
	public void approach(Stage stage, Entity target) {
		if(target.getPosition().getX() > ((Entity) host).getPosition().getX()) {
			approachVector.set(target.getPosition().getX() - (float) ((Combatant) host).getAttackBox().getX()
					- ((Entity) host).getPosition().getX(), target.getYPlane() - ((Entity) host).getYPlane());
		} else {
			approachVector.set(target.getPosition().getX() + (float) ((Combatant) host).getAttackBox().getX()
					- ((Entity) host).getPosition().getX(), target.getYPlane() - ((Entity) host).getYPlane());
		}
		
		approachVector.normalise();
	}
	
	public void chase() {
		setChaseTimer(getChaseTimer() + Theater.getDeltaSpeed(0.025f));
		if(getChaseTimer() >= 1.25f) {
			setChase(false);
			approachVector.set(0, 0);
		}	
	}
	
	public void attacked(Combatant attacker) {
		switch(personality) {
		case NEUTRAL:
		case DOCILE:
			break;
		case AGGRESSIVE:
			this.target = attacker;
			setChase(true);
			break;
		default:
			break;
		}
	}
	
	public boolean applyTraitStateModifier(CharState state, AnimationState animState) {
		switch(state) {
		case IDLE:
			if(personality == Personality.DOCILE) {
				animState.setAnimation(0, "Passive", true);
				return true;
			}
			break;
		default:
			return false;
		}
		
		return false;
	}
	
	public Personality getPersonality() {
		return personality;
	}
	
	public void setPersonality(Personality personality) {
		this.personality = personality;
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
		return chase;
	}
	
	public void setChase(boolean chase) {
		this.chase = chase;
		this.chaseTimer = 0f;
		if(chase && sight instanceof Arc2D) {
			sight = new Ellipse2D.Double(((RectangularShape) sight).getX(), ((RectangularShape) sight).getY(),
					((RectangularShape) sight).getWidth(), ((RectangularShape) sight).getHeight());
		} else if(!chase && sight instanceof Ellipse2D) {
			buildSight();
			if(((Actor) host).getDirection() != 0) {
				flipSight();
			}
		}
	}
	
	public float getChaseTimer() {
		return chaseTimer;
	}
	
	public void setChaseTimer(float chaseTimer) {
		this.chaseTimer = chaseTimer;
	}

	public void buildSight() {
		sight = new Arc2D.Double();
		((Arc2D) sight).setAngleStart(325);
		((Arc2D) sight).setAngleExtent(70);
		((Arc2D) sight).setArcType(Arc2D.PIE);
		((Arc2D) sight).setFrameFromCenter(((Entity) host).getBox().getCenterX(), ((Entity) host).getBox().getCenterY(),
				((Entity) host).getPosition().x - 400, ((Entity) host).getPosition().y - 200);

		hearing = new Arc2D.Double();
		((Arc2D) hearing).setAngleStart(210);
		((Arc2D) hearing).setAngleExtent(300);
		((Arc2D) hearing).setArcType(Arc2D.CHORD);
		((Arc2D) hearing).setFrameFromCenter(((Entity) host).getBox().getCenterX() + 175, ((Entity) host).getBox().getCenterY(),
				((Entity) host).getPosition().x - 275, ((Entity) host).getPosition().y - 150);
	}
	
	public void flipSight() {
		if(sight instanceof Arc2D) {
			((Arc2D) sight).setAngleStart(((Arc2D) sight).getAngleStart() + 180);
		}
	}
	
	public Shape getSight() {
		return sight;
	}
	
	public Shape getHearing() {
		return hearing;
	}
	
	public boolean isApproaching() {
		return approachVector.length() != 0;
	}
	
	public Vector2f getApproachVector() {
		return approachVector;
	}

	public void setApproachVector(Vector2f approachVector) {
		this.approachVector = approachVector;
	}

	public void setApproachVector(float x, float y) {
		this.approachVector.set(x, y);
	}
	
	
	
}
