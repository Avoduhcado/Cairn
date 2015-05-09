package core.entities;

import java.awt.Polygon;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.Event;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonJson;
import com.esotericsoftware.spine.AnimationState.AnimationStateAdapter;
import com.esotericsoftware.spine.Slot;
import com.esotericsoftware.spine.attachments.Region;

import core.Camera;
import core.Theater;
import core.render.DrawUtils;
import core.setups.Stage;
import core.utilities.MathFunctions;
import core.audio.Ensemble;
import core.entities.interfaces.Combatant;
import core.entities.utils.CharState;
import core.entities.utils.stats.Stats;
import core.equipment.Equipment;
import core.equipment.Weapon;

public class Player extends Actor implements Combatant {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Equipment equipment;
	private Stats stats;
	
	private transient AnimationState animStateOverlay;
	private transient float overlayDelay;
	private transient int looking;
	
	public Player(float x, float y, String ref, float scale) {
		super(x, y, ref, scale);
		
		//setDadSkull(false);
		setDadArmRight(false);
		setDadArmLeft(false);
				
		this.stats = new Stats();
		this.stats.getHealth().setCurrent(2000f);
		this.equipment = new Equipment();
		this.changeWeapon(Equipment.lightMace);
	}
	
	@Override
	public void update() {
		super.update();
		
		if(looking == 1) {
			Camera.get().setPanning(0, -1, 4.5f);
			if(animStateOverlay.getCurrent(0) == null) {
				if(overlayDelay < 0.35f) {
					overlayDelay += Theater.getDeltaSpeed(0.025f);
				} else {
					animStateOverlay.setAnimation(0, "LookUp", true);
				}
			}
		} else if(looking == -1) {
			Camera.get().setPanning(0, 1, 4.5f);
		} else {
			Camera.get().setPanning(0, 0, 0);
			if(animStateOverlay.getCurrent(0) != null && animStateOverlay.getCurrent(0).getAnimation().getName().matches("LookUp")) {
				animStateOverlay.clearTrack(0);
				overlayDelay = 0f;
			}
		}
		
		if(getState() == CharState.ATTACK && equipment.getWeapon().isDamaging()) {
			Polygon box = ((Region) skeleton.findSlot(equipment.getWeapon().getSlot()).getAttachment())
					.getRotatedBox(skeleton.findSlot(equipment.getWeapon().getSlot()), equipment.getWeapon().getDamageHitbox());
			box.translate((int) ((Region) skeleton.findSlot(equipment.getWeapon().getSlot()).getAttachment()).getWorldX(),
					(int) ((Region) skeleton.findSlot(equipment.getWeapon().getSlot()).getAttachment()).getWorldY());
			
			// TODO Change damage based on location of hit/Which segment got hit and how far from center it is
			for(Actor e : ((Stage) Theater.get().getSetup()).getCast()) {
				if(e instanceof Combatant) {
					for(Rectangle2D r : ((Enemy) e).getHitBoxes(this)) {
						if(box.intersects(r) && Point2D.distance(0, this.getYPlane(), 0, ((Actor) e).getYPlane()) 
								< (((Region) skeleton.findSlot(equipment.getWeapon().getSlot()).getAttachment()).getHeight() / 2f)) {
							((Combatant) e).hit(this);
							break;
						}
					}
				}
			}
		}
	}
	
	@Override
	public void draw() {
		super.draw();
		
		if(Theater.get().debug) {
			if(equipment.getWeapon().isDamaging()) {
				Slot weapon = skeleton.findSlot("WEAPON F");
				if(weapon.getAttachment() != null) {
					Region region = (Region) weapon.getAttachment();
					//region.updateWorldVertices(weapon);
					
					Polygon box = region.getRotatedBox(weapon, equipment.getWeapon().getDamageHitbox());
					DrawUtils.setColor(new Vector3f(1f, 0f, 0f));
					DrawUtils.drawPoly(region.getWorldX(), region.getWorldY(), box);
				}
			}
		}
	}
	
	@Override
	public void buildSkeleton() {
		SkeletonJson json = new SkeletonJson(null);
		json.setScale(scale);
		skeleton = new Skeleton(json.readSkeletonData(this.name));
		skeleton.updateWorldTransform();
				
		/*this.box = new Rectangle2D.Double(pos.x - ((skeleton.getData().getWidth() * scale) / 2f),
				pos.y + (skeleton.getData().getCenterY() * scale) - ((skeleton.getData().getHeight() * 0.3f) * scale),
				(skeleton.getData().getWidth() * 0.8f) * scale, (skeleton.getData().getHeight() * 0.3f) * scale);*/
		this.box = new Rectangle2D.Double(pos.x - ((skeleton.getData().getWidth() * scale) / 2f),
				pos.y - ((skeleton.getData().getHeight() * scale) / 2f), 
				skeleton.getData().getWidth() * scale, skeleton.getData().getHeight() * scale);
		
		animStateData = new AnimationStateData(skeleton.getData());
		animStateData.setDefaultMix(0.1f);
		animStateData.setMix("Idle", "Walk", 0.2f);
		animStateData.setMix("Walk", "Idle", 0.2f);
		if(animStateData.getSkeletonData().findAnimation("Attack") != null) {
			animStateData.setMix("Idle", "Attack", 0f);
		}
		if(animStateData.getSkeletonData().findAnimation("Defend") != null) {
			animStateData.setMix("Idle", "Defend", 0f);
		}
		if(animStateData.getSkeletonData().findAnimation("Hit") != null) {
			animStateData.setMix("Idle", "Hit", 0f);
			if(animStateData.getSkeletonData().findAnimation("Walk") != null)
				animStateData.setMix("Walk", "Hit", 0f);
		}
		animState = new AnimationState(animStateData);
		animState.setAnimation(0, "Idle", true);
		animStateOverlay = new AnimationState(animStateData);
		
		buildAnimationEvents();
	}
	
	@Override
	public void buildAnimationEvents() {		
		animState.addListener(new AnimationStateAdapter() {
			@Override
			public void event(int trackIndex, Event event) {
				switch(event.getData().getName()) {
				case "Shake":
					float[] shakeData = new float[4];
					for(int i = 0; i<event.getString().split(";").length; i++) {
						shakeData[i] = Float.parseFloat(event.getString().split(";")[i]);
					}
					Camera.get().setShake(new Vector2f(shakeData[0], shakeData[1]), shakeData[2], shakeData[3]);
					break;
				case "Speed":
					setMaxSpeed(event.getFloat());
					break;
				case "Footstep":
					String step = event.getString() + ((int)(Math.random() * 6) + 1);
					Ensemble.get().playSoundEffect(step);
					break;
				case "SFX":
					// TODO Stereoscopic sounds
					//Ensemble.get().playSoundEffect(event.getString());
					break;
				case "Damage":
					equipment.getWeapon().setDamage(event.getInt() == 1);
					equipment.getWeapon().setKnockback(event.getFloat() == 1);
					equipment.getWeapon().setSlot(event.getString());
					break;
				case "SuperArmor":
					equipment.setSuperArmor(Boolean.parseBoolean(event.getString()));
					break;
				case "Parry":
					equipment.setBlock(event.getInt() == 1);
					break;
				default:
					System.out.println("Unhandled event: " + event.getData());
				}
			}
		});
	}

	@Override
	public void updateAnimations() {
		if(state == CharState.WALK)
			animState.update(Theater.getDeltaSpeed(0.016f) * (velocity.length() == 0 ? 1 : velocity.length() / getMaxSpeed()));
		else
			animState.update(Theater.getDeltaSpeed(0.016f));
		animStateOverlay.update(Theater.getDeltaSpeed(0.016f));
		animState.apply(skeleton);
		animStateOverlay.apply(skeleton);
	}
	
	@Override
	public void updateState() {
		switch(state) {
		case IDLE:
			if(velocity.length() != 0) {
				setState(CharState.WALK);
			}
			break;
		case ATTACK:
		case DEFEND:
			// TODO Animation listener
			if(animState.getCurrent(0).isComplete()) {
				endCombat();
				setState(CharState.IDLE);
			}
			break;
		case HIT:
			if(animState.getCurrent(0).isComplete()) {
				setState(CharState.IDLE);
			}
			break;
		case DEATH:
			if(animState.getCurrent(0).isComplete()) {
				setState(CharState.DEAD);
			}
			break;
		case WALK:
		case RUN:
			if(velocity.length() == 0) {
				setState(CharState.IDLE);
			}
			break;
		case QUICKSTEP:
			if(animState.getCurrent(0).isComplete()) {
				setState(CharState.IDLE);
			}
			break;
		default:
			setState(CharState.IDLE);
		}
	}
	
	@Override
	public void updateBox() {
		/*this.box.setFrame(pos.x - (box.getWidth() / 2f),
				pos.y + (skeleton.getData().getCenterY() * scale) - ((skeleton.getData().getHeight() * 0.15f) * scale),
				box.getWidth(), box.getHeight());*/
		this.box.setFrame(pos.x - ((skeleton.getData().getWidth() * scale) / 2f),
				(pos.y - (skeleton.getData().getCenterY() * scale)) - ((skeleton.getData().getHeight() * scale) / 2f), 
				box.getWidth(), box.getHeight());
	}
	
	public void setDadSkull(boolean enabled) {
		if(enabled) {
			skeleton.setAttachment("HEAD F", "HEAD F");
			skeleton.setAttachment("HEAD STRING LEFT", "HEAD STRING LEFT");
			skeleton.setAttachment("HEAD STRING MIDDLE", "HEAD STRING MIDDLE");
			skeleton.setAttachment("HEAD STRING RIGHT", "HEAD STRING RIGHT");
		} else {
			skeleton.setAttachment("HEAD F", null);
			skeleton.setAttachment("HEAD STRING LEFT", null);
			skeleton.setAttachment("HEAD STRING MIDDLE", null);
			skeleton.setAttachment("HEAD STRING RIGHT", null);
		}
	}
	
	public void setDadArmRight(boolean enabled) {
		if(enabled) {
			skeleton.setAttachment("RIGHT ARM F", "RIGHT ARM F");
			skeleton.setAttachment("RIGHT ARM STRING", "RIGHT ARM STRING");
			skeleton.setAttachment("RIGHT FOREARM F", "RIGHT FOREARM F");
			skeleton.setAttachment("RIGHT FOREARM STRING", "RIGHT FOREARM STRING");
			skeleton.setAttachment("RIGHT HAND F", "RIGHT HAND F");
			skeleton.setAttachment("WEAPON F", equipment.getWeapon().getName() + " F");
		} else {
			skeleton.setAttachment("RIGHT ARM F", null);
			skeleton.setAttachment("RIGHT ARM STRING", null);
			skeleton.setAttachment("RIGHT FOREARM F", null);
			skeleton.setAttachment("RIGHT FOREARM STRING", null);
			skeleton.setAttachment("RIGHT HAND F", null);
			skeleton.setAttachment("WEAPON F", null);
		}
	}
	
	public void setDadArmLeft(boolean enabled) {
		if(enabled) {
			skeleton.setAttachment("LEFT ARM F", "LEFT ARM F");
			skeleton.setAttachment("LEFT ARM STRING", "LEFT ARM STRING");
			skeleton.setAttachment("LEFT FOREARM F", "LEFT FOREARM F");
			skeleton.setAttachment("LEFT FOREARM STRING", "LEFT FOREARM STRING");
			skeleton.setAttachment("LEFT HAND F", "LEFT HAND F");
			skeleton.setAttachment("SHIELD F", "SHIELD F");
		} else {
			skeleton.setAttachment("LEFT ARM F", null);
			skeleton.setAttachment("LEFT ARM STRING", null);
			skeleton.setAttachment("LEFT FOREARM F", null);
			skeleton.setAttachment("LEFT FOREARM STRING", null);
			skeleton.setAttachment("LEFT HAND F", null);
			skeleton.setAttachment("SHIELD F", null);
		}
	}
	
	@Override
	public void moveUp() {
		if(velocity.x != 0f) {
			looking = 0;
			
			velocity.y -= Theater.getDeltaSpeed(0.45f);
			if(velocity.length() > getMaxSpeed()) {
				velocity = MathFunctions.limitVector(velocity, getMaxSpeed());
			}
		} else {
			looking = 1;
		}
	}
	
	@Override
	public void endCombat() {
		switch(getState()) {
		case ATTACK:
			setDadArmRight(false);
			equipment.setSuperArmor(false);
			equipment.getWeapon().setDamage(false);
			break;
		case DEFEND:
			setDadArmLeft(false);
			equipment.setBlock(false);
			break;
		default:
			break;
		}
	}
	
	@Override
	public void attack() {
		// TODO Check if Dad skull is enabled
		if(getState() == CharState.IDLE || (getState() == CharState.WALK && getVelocity().length() <= getMaxSpeed() / 2f)) {
			setDadArmRight(true);
			setState(CharState.ATTACK);
		}
	}

	@Override
	public void defend() {
		if(getState() == CharState.IDLE || (getState() == CharState.WALK && getVelocity().length() <= getMaxSpeed() / 2f)) {
			setDadArmLeft(true);
			setState(CharState.DEFEND);
		}
	}

	@Override
	public void hit(Combatant attacker) {
		switch(state.getHitState()) {
		case 0:
			if(!equipment.isSuperArmor()) {
				takeDamage(attacker, 1f, true);
			} else if(equipment.isSuperArmor() && !equipment.isSuperInvulnerable()) {
				takeDamage(attacker, 0.5f, false);
			}
			break;
		case 1:
			if(equipment.isBlock() 
					&& (getDirection() == 0 ? ((Entity) attacker).getX() >= getX() : getX() >= ((Entity) attacker).getX())) {
				Ensemble.get().playSoundEffect("Parried");
				attacker.endCombat();
				((Actor) attacker).setState(CharState.RECOIL);
				Vector2f.sub(((Entity) attacker).getPosition(), getPosition(), ((Actor) attacker).velocity);
				((Actor) attacker).velocity.normalise();
				((Actor) attacker).velocity.scale(3.5f);
			} else {
				takeDamage(attacker, 1f, true);
			}
			break;
		case 2:
			takeDamage(attacker, 1.5f, true);
			break;
		case 3:
			if(!equipment.isInvulnerable()) {
				takeDamage(attacker, 1.15f, true);
			}
			break;
		default:
			break;
		}
	}
	
	@Override
	public void takeDamage(Combatant attacker, float damageMod, boolean knockBack) {
		// TODO Retrieve damage from enemy weapon/stats
		stats.getHealth().addCurrent(-10f * damageMod);
		
		if(knockBack) {
			endCombat();
			setState(CharState.HIT);
			if(attacker.getEquipment().getWeapon().isReversedKnockback()) {
				Vector2f.sub(((Entity) attacker).getPosition(), getPosition(), this.velocity);
			} else {
				Vector2f.sub(getPosition(), ((Entity) attacker).getPosition(), this.velocity);
			}
			this.velocity.normalise();
			
			// TODO Scale to damage
			this.velocity.scale(3.5f);
		} else {
			equipment.setSuperInvulnerable(true);
			System.out.println("SUPER DUPER " + ID);
		}
		
		if(stats.getHealth().getCurrent() <= 0f) {
			setState(CharState.DEATH);
			System.out.println("YOU DIED " + ID + " HP: " + stats.getHealth().getCurrent());
		}
	}

	@Override
	public void setUpCombatData(String attackName) {}

	@Override
	public Rectangle2D getAttackBox() {
		return null;
	}

	@Override
	public ArrayList<Rectangle2D> getHitBoxes(Combatant attacker) {
		ArrayList<Rectangle2D> hitboxes = new ArrayList<Rectangle2D>();
		for(Slot s : skeleton.getSlots()) {
			if(s.getAttachment() != null && !s.getData().getName().matches("WEAPON")) {
				Region region = (Region) s.getAttachment();
				Polygon p = region.getRotatedBox(s, null);
				p.translate((int) region.getWorldX(), (int) region.getWorldY());

				hitboxes.add(p.getBounds2D());
			}
		}
		
		return hitboxes;
	}

	@Override
	public void setState(CharState state) {
		super.setState(state);
		
		if(this.state != state) {
			switch(state) {
			case ATTACK:
				animState.setAnimation(0, equipment.getWeapon().getAttackAnim(), false);
				break;
			case DEFEND:
				animState.setAnimation(0, "Defend", false);
				break;
			default:
				break;
			}
			
			this.state = state;
		}
	}
	
	public Equipment getEquipment() {
		return equipment;
	}
	
	public void changeWeapon(Weapon weapon) {
		if(state.canAct()) {
			equipment.setWeapon(weapon);
			skeleton.setAttachment("WEAPON", weapon.getName());
			animStateOverlay.setAnimation(0, "ChangeWeapon", false);
		}
	}
	
	public void setLooking(int looking) {
		this.looking = looking;
	}
	
	@Override
	public void setID() {
		Actor.reset();
		this.ID = "PLAYER";
	}

}
