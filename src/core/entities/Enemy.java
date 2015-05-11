package core.entities;

import java.awt.Polygon;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import org.lwjgl.util.vector.Vector2f;

import com.esotericsoftware.spine.Animation;
import com.esotericsoftware.spine.Animation.EventTimeline;
import com.esotericsoftware.spine.Animation.Timeline;
import com.esotericsoftware.spine.Bone;
import com.esotericsoftware.spine.Event;
import com.esotericsoftware.spine.AnimationState.AnimationStateAdapter;
import com.esotericsoftware.spine.Slot;
import com.esotericsoftware.spine.attachments.Region;

import core.Camera;
import core.Theater;
import core.audio.Ensemble;
import core.entities.interfaces.Combatant;
import core.entities.interfaces.Intelligent;
import core.entities.utils.CharState;
import core.entities.utils.ai.Intelligence;
import core.entities.utils.ai.traits.Trait;
import core.entities.utils.stats.Stats;
import core.equipment.AttackType;
import core.equipment.Equipment;
import core.equipment.Weapon;
import core.render.DrawUtils;
import core.setups.Stage;
import core.utilities.MathFunctions;

public class Enemy extends Actor implements Combatant, Intelligent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Equipment equipment;
	private Intelligence intelligence;
	private Stats stats;
	
	private float animationSpeed = 1f;
	
	public Enemy(float x, float y, String ref, float scale) {
		super(x, y, ref, scale);
		
		this.stats = new Stats();
		this.stats.getHealth().setCurrent(20f);
		this.intelligence = new Intelligence(this);
		this.equipment = new Equipment();
		
		int index = 1;
		while(skeleton.getData().findAnimation("Attack" + index) != null) {
			setUpCombatData("Attack" + index);
			index++;
		}
	}
	
	@Override
	public void update() {
		if(intelligence.getApproachVector().length() != 0) {
			velocity = MathFunctions.limitVector(Vector2f.add(velocity,
					intelligence.getApproachVector(), intelligence.getApproachVector()), getMaxSpeed());
		}
		
		super.update();
		
		if(getState() == CharState.ATTACK && equipment.getEquippedWeapon().isDamaging()) {
			Polygon box = ((Region) skeleton.findSlot(equipment.getEquippedWeapon().getSlot()).getAttachment())
					.getRotatedBox(skeleton.findSlot(equipment.getEquippedWeapon().getSlot()), equipment.getEquippedWeapon().getDamageHitbox());
			box.translate((int) ((Region) skeleton.findSlot(equipment.getEquippedWeapon().getSlot()).getAttachment()).getWorldX(),
					(int) ((Region) skeleton.findSlot(equipment.getEquippedWeapon().getSlot()).getAttachment()).getWorldY());

			for(Rectangle2D r : ((Stage) Theater.get().getSetup()).getPlayer().getHitBoxes(this)) {
				if(box.intersects(r)
						&& Point2D.distance(0, this.getYPlane(), 0, ((Actor) ((Stage) Theater.get().getSetup()).getPlayer()).getYPlane())
						< (((Region) skeleton.findSlot(equipment.getEquippedWeapon().getSlot()).getAttachment()).getHeight() / 2f)) {
					((Stage) Theater.get().getSetup()).getPlayer().hit(this);
					break;
				}
			}
		}
		
		for(Trait t : intelligence.getTraits()) {
			t.process();
		}
	}
	
	@Override
	public void draw() {
		super.draw();
		
		if(Theater.get().debug) {
			DrawUtils.drawShape(0, 0, intelligence.getSight().getPathIterator(null, 10));
			//DrawUtils.setColor(new Vector3f(0f, 0f, 1f));
			//DrawUtils.drawShape(0, 0, intelligence.getHearing().getPathIterator(null, 10));
		}
	}
	
	@Override
	public void buildAnimationEvents() {
		animState.addListener(new AnimationStateAdapter() {
			@Override
			public void event(int trackIndex, Event event) {
				switch(event.getData().getName()) {
				case "Shake":
					String[] shakeData = event.getString().split(";");
					Camera.get().setShake(new Vector2f(Float.parseFloat(shakeData[0]), Float.parseFloat(shakeData[1])),
							5.5f, Float.parseFloat(shakeData[3]));
					break;
				case "Speed":
					setMaxSpeed(getMaxSpeed() * event.getFloat());
					//setMaxSpeed(event.getFloat());
					break;
				case "Footstep":
					String step = event.getString() + ((int)(Math.random() * 6) + 1);
					Ensemble.get().playSoundEffect(step);
					break;
				case "Damage":
					equipment.getEquippedWeapon().setDamaging(event.getInt() == 1);
					equipment.getEquippedWeapon().setKnockback(event.getFloat() == 1);
					equipment.getEquippedWeapon().setSlot(event.getString());
					break;
				case "SuperArmor":
					equipment.setSuperArmor(Boolean.parseBoolean(event.getString()));
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
			animState.update(Theater.getDeltaSpeed(0.016f * animationSpeed));
		animState.apply(skeleton);
	}
	
	@Override
	public void updateState() {
		switch(state) {
		case IDLE:
			if(velocity.length() != 0) {
				setState(CharState.WALK);
			}
			break;
		case WALK:
		case RUN:
			if(velocity.length() == 0) {
				setState(CharState.IDLE);
			}
			break;
		case ATTACK:
		case DEFEND:
		case RECOIL:
		case HIT:
		case QUICKSTEP:
			if(animState.getCurrent(0).isComplete()) {
				if(state == CharState.ATTACK) {
					equipment.equipRandomWeapon();
				}
				setState(CharState.IDLE);
			}
			break;
		case DEATH:
			if(animState.getCurrent(0).isComplete()) {
				setState(CharState.DEAD);
			}
			break;
		default:
			setState(CharState.IDLE);
		}
	}

	@Override
	public void endCombat() {
		equipment.setSuperArmor(false);
		equipment.setInvulnerable(false);
		equipment.setBlock(false);
		equipment.getEquippedWeapon().setDamaging(false);
	}
	
	@Override
	public void attack() {
		if(getState() == CharState.IDLE || getVelocity().length() <= getMaxSpeed() / 2f) {
			setState(CharState.ATTACK);
		}
	}

	@Override
	public void defend() {
		if(getState() == CharState.IDLE || getVelocity().length() <= getMaxSpeed() / 2f) {
			setState(CharState.DEFEND);
		}
	}

	@Override
	public void hit(Combatant attacker) {
		this.intelligence.attacked(attacker);
		
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
				((Actor) attacker).velocity.scale(2f);
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
			intelligence.setApproachVector(0, 0);
			if(attacker.getEquipment().getEquippedWeapon().isReversedKnockback()) {
				Vector2f.sub(new Vector2f(((Entity) attacker).getX(), ((Actor) attacker).getYPlane()),
						new Vector2f(getX(), getYPlane()), this.velocity);
			} else {
				Vector2f.sub(new Vector2f(getX(), getYPlane()),
						new Vector2f(((Entity) attacker).getX(), ((Actor) attacker).getYPlane()), this.velocity);
			}
			this.velocity.normalise();
			
			// TODO Scale to damage
			this.velocity.scale(2.5f * damageMod);
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
	public void setUpCombatData(String attackName) {
		Animation attack = skeleton.getData().findAnimation(attackName);
		Weapon weapon = new Weapon(attackName, AttackType.UNARMED, 10f);
		for(Timeline t : attack.getTimelines()) {
			if(t instanceof EventTimeline) {
				Region region = null;
				Bone bone = null;
				for(int i = 0; i<((EventTimeline) t).getFrameCount(); i++) {
					if(((EventTimeline) t).getEvents()[i].getData().getName().matches("Damage")) {
						// Apply animation transform to receive proper positioning
						attack.apply(skeleton, 0, ((EventTimeline) t).getFrames()[i], false, null);
						skeleton.updateWorldTransform();
						if(region == null) {
							region = (Region) skeleton.findSlot(((EventTimeline) t).getEvents()[i].getString()).getAttachment();
							bone = skeleton.findBone(((EventTimeline) t).getEvents()[i].getString());
						}
						weapon.setAttackRange(new Rectangle2D.Double(bone.getWorldX() + region.getOffsetX(),
								bone.getWorldY(), region.getBox().getWidth(), region.getBox().getHeight()));
						
						equipment.addWeapon(weapon);
						break;
					}
				}
			}
		}
	}

	@Override
	public Rectangle2D getAttackBox() {
		return equipment.getEquippedWeapon().getAttackRange();
	}

	@Override
	public ArrayList<Rectangle2D> getHitBoxes(Combatant attacker) {		
		ArrayList<Rectangle2D> hitboxes = new ArrayList<Rectangle2D>();
		for(Slot s : skeleton.getSlots()) {
			if(s.getAttachment() != null && !s.getData().getName().startsWith("WEAPON")) {
				Region region = (Region) s.getAttachment();
				Polygon p = region.getRotatedBox(s, null);
				p.translate((int) region.getWorldX(), (int) region.getWorldY());

				hitboxes.add(p.getBounds2D());
			}
		}
		
		return hitboxes;
	}
	
	public boolean canReach(Entity target) {
		Rectangle2D hitBox = new Rectangle2D.Double((target.getX() > this.getX() ?
				this.pos.x + getAttackBox().getX() : this.pos.x - getAttackBox().getMaxX()),
				this.pos.y + getAttackBox().getY(), getAttackBox().getWidth(), getAttackBox().getHeight());
		if(hitBox.intersectsLine(target.getX(), target.getYPlane(), target.getX(), target.getYPlane() - target.getBox().getHeight()) 
				&& Point2D.distance(0, this.getYPlane(), 0, ((Actor) target).getYPlane()) < getAttackBox().getHeight() / 2f) {
			return true;
		}
		
		return false;
	}
	
	public void lookAt(Entity target) {
		setDirection(target.getX() > this.getX() ? 0 : 1);
	}
	
	@Override
	public void updateBox() {
		super.updateBox();

		((RectangularShape) intelligence.getSight()).setFrameFromCenter(box.getCenterX(), box.getCenterY(), pos.x - 400, pos.y - 200);
	}
	
	@Override
	public void setDirection(int direction) {
		if(direction != this.direction) {
			intelligence.flipSight();
		}
		this.direction = direction;
	}
	
	@Override
	public void setState(CharState state) {
		animationSpeed = 1f;
		if(!intelligence.applyTraitStateModifier(state, animState))
			super.setState(state);
		
		if(this.state != state) {
			switch(state) {
			case ATTACK:
				animState.setAnimation(0, equipment.getEquippedWeapon().getAttackAnim(), false);
				//animState.setAnimation(0, "Attack2", false);
				break;
			case DEFEND:
				animState.setAnimation(0, "Defend", false);
				break;
			case RECOIL:
				animState.setAnimation(0, "Hit", false);
				animationSpeed = 0.25f;
				break;
			default:
				break;
			}
			
			this.state = state;
		}
	}

	@Override
	public void think(Stage stage) {
		switch(intelligence.getPersonality()) {
		case DOCILE:
			//setState(CharState.IDLE);
		case NEUTRAL:
			if(intelligence.isChasing()) {
				intelligence.setChase(false);
			}
			if(intelligence.getApproachVector().length() != 0) {
				intelligence.getApproachVector().set(0, 0);
			}
			break;
		case AGGRESSIVE:
			if(getState().canAct() && canReach(stage.getPlayer()) && getState() != CharState.ATTACK && intelligence.isChasing()) {
				lookAt(stage.getPlayer());
				velocity.scale(0.25f);
				attack();
				intelligence.getApproachVector().set(0, 0);
			} else if(getState().canAct()) {
				if(intelligence.getSight().intersects(stage.getPlayer().getBox())) {
					intelligence.setChase(true);
					intelligence.approach(stage, stage.getPlayer());
				} else if(intelligence.isChasing()) {
					intelligence.approach(stage, stage.getPlayer());
					intelligence.chase();
				}
			}
			break;
		default:
			break;
		}
	}
	
	@Override
	public Intelligence getIntelligence() {
		return intelligence;
	}

	public Equipment getEquipment() {
		return equipment;
	}
	
	public Stats getStats() {
		return stats;
	}
	
	public void changeWeapon(Weapon weapon) {
		if(state.canAct()) {
			equipment.equipWeapon(weapon);
			if(!weapon.getName().startsWith("Attack")) {
				skeleton.setAttachment("WEAPON", weapon.getName());
				//animStateOverlay.setAnimation(0, "ChangeWeapon", false);
			}
		}
	}

}
