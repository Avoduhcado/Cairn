package core.entities.utils.ai;

import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RectangularShape;
import java.io.Serializable;
import java.util.ArrayList;

import com.esotericsoftware.spine.AnimationState;

import core.Theater;
import core.entities.Actor;
import core.entities.Entity;
import core.entities.interfaces.Combatant;
import core.entities.interfaces.Intelligent;
import core.entities.utils.CharState;
import core.entities.utils.ai.traits.Trait;
import core.setups.Stage;

enum AIState {
	IDLE, PROVOKED, ENRAGED;
}

public class Intelligence implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Intelligent host;
	private Combatant target;
	
	private Personality personality;
	private AIState state;
	private ArrayList<Trait> traits = new ArrayList<Trait>();
	
	private Shape sight;
	private Shape hearing;
	// TODO ViewAngle, ViewDistance parameters
	//private Vector2f approachVector;
	private float chaseTimer = 0f;
	private float chaseLimit = 1.25f;
	
	public Intelligence(Intelligent host) {
		this.host = host;
		
		personality = Personality.NEUTRAL;
		state = AIState.IDLE;
		
		buildSight();		
	}
	
	public void update() {
		for(Trait t : traits) {
			t.process();
		}
		
		switch(state) {
		case IDLE:
			searchForTarget();
			break;
		case PROVOKED:
			if(sight.intersects(((Entity) target).getBox())) {
				alert(target);
				host.approach(((Actor) target).getPositionAsPoint());
			} else {
				host.approach(((Actor) target).getPositionAsPoint());
				chase();
			}
			break;
		case ENRAGED:
			break;
		}
		
		
		/*switch(personality) {
		case DOCILE:
		case NEUTRAL:
			if(isChasing()) {
				setChase(false);
			}
			if(getApproachVector().length() != 0) {
				getApproachVector().set(0, 0);
			}
			break;
		case AGGRESSIVE:
			if(target == null) {
				searchForTarget(stage);
			} else if(((Actor) host).getState().canAct()) {
				if(((Actor) target).getState() == CharState.ATTACK) {
					//((Enemy) host).dodge(new Vector2f(0, (((Actor) target).getYPlane() > ((Actor) host).getYPlane() ? -5f : 5f)));
				}
				
				// TODO Devise timing for attacks and dodging
				if(((Enemy) host).canReach((Entity) target)) {
					((Enemy) host).lookAt((Entity) target);
					((Enemy) host).attack();
					getApproachVector().set(0, 0);
				} else {
					if(getSight().intersects(((Entity) target).getBox())) {
						alert(target);
						approach(((Actor) target).getPositionAsPoint());
					} else {
						approach(((Actor) target).getPositionAsPoint());
						chase();
					}
				}
			}
			break;
		default:
			break;
		}*/
	}
	
	public void searchForTarget() {
		switch(personality) {
		default:
			break;
		case AGGRESSIVE:
			for(Actor a : ((Stage) Theater.get().getSetup()).getCast()) {
				if(a instanceof Combatant && host != a && ((Combatant) host).getReputation().isEnemy(((Combatant) a).getReputation())
						&& getSight().intersects(a.getBox())) {
					alert((Combatant) a);
					break;
				}
			}
			break;
		}
	}

	/*public void approach(Point2D target) {
		if(target.getX() > ((Entity) host).getPosition().getX()) {
			approachVector.set((float) (target.getX() - ((Enemy) host).getAttackBox().getX()
					- ((Entity) host).getPosition().getX()), (float) (target.getY() - ((Entity) host).getYPlane()));
		} else {
			approachVector.set((float) (target.getX() +  ((Enemy) host).getAttackBox().getX()
					- ((Entity) host).getPosition().getX()), (float) (target.getY() - ((Entity) host).getYPlane()));
		}
		
		approachVector.normalise();
	}*/
	
	public void chase() {
		setChaseTimer(getChaseTimer() + Theater.getDeltaSpeed(0.025f));
		if(getChaseTimer() >= chaseLimit) {
			setTarget(null);
		}	
	}
	
	public void alert(Combatant target) {
		switch(personality) {
		default:
			break;
		case AGGRESSIVE:
			setTarget(target);
			
			for(Trait t : traits) {
				t.alert(target);
			}
			break;
		}
	}

	public Combatant getTarget() {
		return target;
	}
	
	public void setTarget(Combatant target) {
		this.target = target;
		setChase(target != null);
		if(target == null) {
			state = AIState.IDLE;
		} else {
			state = AIState.PROVOKED;
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
	
	public AIState getState() {
		return state;
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
		return state == AIState.PROVOKED || state == AIState.ENRAGED && target != null;
	}
	
	public void setChase(boolean chase) {
		//this.chase = chase;
		this.chaseTimer = 0f;
		
		if(chase && sight instanceof Arc2D) {
			sight = new Ellipse2D.Double(((RectangularShape) sight).getX(), ((RectangularShape) sight).getY(),
					((RectangularShape) sight).getWidth(), ((RectangularShape) sight).getHeight());
		} else if(!chase && sight instanceof Ellipse2D) {
			buildSight();
			//approachVector.set(0, 0);
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
	
	public float getChaseLimit() {
		return chaseLimit;
	}
	
	public void setChaseLimit(float chaseLimit) {
		this.chaseLimit = chaseLimit;
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
	
	/*public boolean isApproaching() {
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
	}*/
	
}
