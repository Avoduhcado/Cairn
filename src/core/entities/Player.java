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
import core.utilities.keyboard.Keybinds;
import core.audio.AudioSource;
import core.entities.interfaces.Combatant;
import core.entities.utils.CharState;
import core.entities.utils.Faction;
import core.entities.utils.Reputation;
import core.entities.utils.stats.Health;
import core.entities.utils.stats.Magic;
import core.entities.utils.stats.Stamina;
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
	private Reputation reputation;
	private boolean familiar;
	
	private Spell spell;
	
	private transient AnimationState animStateOverlay;
	private transient float overlayDelay;
	private transient int looking;
	
	public Player(float x, float y, String ref, float scale) {
		super(x, y, ref, scale);
		
		familiar = true;
		setDadSkull(familiar);
		setDadArmRight(false);
		setDadArmLeft(false);
				
		this.stats = new Stats();
		this.stats.setHealth(new Health(30f, 30f));
		this.stats.setStamina(new Stamina(20f, 20f));
		if(!familiar) {
			this.stats.getStamina().setCurrent(0);
			this.stats.getStamina().setRegainSpeed(0);
		} else {
			this.stats.getStamina().setRegainSpeed(7.5f);
		}
		this.stats.setMagic(new Magic(30f, 30f));
		this.equipment = new Equipment();
		this.equipment.addWeapon(Equipment.lightMace);
		this.equipment.addWeapon(Equipment.heavyMace);
		this.equipment.addWeapon(Equipment.polearm);
		this.changeWeapon(Equipment.lightMace);
		this.changeItem();
		this.equipment.setCurrentMilk(10);
		this.reputation = new Reputation(Faction.PLAYER, Faction.MONSTER);

		//setState(CharState.REVIVE);
	}

	@Override
	public void update() {
		if(getVelocity().length() > 0 && state == CharState.RUN) {
			stats.getStamina().addCurrent(-Theater.getDeltaSpeed(0.15f));
		}
		
		if(spell != null) {
			spell.update();
			if(spell.isFinished()) {
				spell = null;
			}
		}
		
		super.update();
		
		if(looking == 1) {
			if(!Camera.get().isPanning()) {
				Camera.get().setPan(new Vector2f(0f, -200), 1.25f, 0.85f);
			}
			if(animStateOverlay.getCurrent(0) == null) {
				if(overlayDelay < 0.35f) {
					overlayDelay += Theater.getDeltaSpeed(0.025f);
				} else {
					animStateOverlay.setAnimation(0, "LookUp", true);
				}
			}
		} else if(looking == -1) {
			if(!Camera.get().isPanning()) {
				Camera.get().setPan(new Vector2f(0f, 200), 1.25f, 0.85f);
			}
		} else {
			if(Camera.get().isPanning()) {
				Camera.get().setPan(new Vector2f(0, 0), 0, 0);
			}
			if(animStateOverlay.getCurrent(0) != null && animStateOverlay.getCurrent(0).getAnimation().getName().matches("LookUp")) {
				animStateOverlay.clearTrack(0);
				overlayDelay = 0f;
			}
		}
		
		
		
		stats.update();
	}
	
	@Override
	public void draw() {
		super.draw();
		
		if(spell != null) {
			spell.draw();
		}
			
		if(Theater.get().debug) {
			if(equipment.getEquippedWeapon().isDamaging()) {
				Slot weapon = skeleton.findSlot("WEAPON F");
				if(weapon.getAttachment() != null) {
					Region region = (Region) weapon.getAttachment();
					//region.updateWorldVertices(weapon);
					
					Polygon box = region.getRotatedBox(weapon, equipment.getEquippedWeapon().getDamageHitbox());
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

		this.box = new Rectangle2D.Double(pos.x - ((skeleton.getData().getWidth() * scale) / 2f),
				(pos.y + (skeleton.getData().getCenterY() * scale)) - ((skeleton.getData().getHeight() * scale)), 
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
					AudioSource footstep = new AudioSource(step, "SFX");
					footstep.getAudio().playAsSoundEffect(1f, 1f, false);
					break;
				case "SFX":
					AudioSource soundeffect = new AudioSource(event.getString(), "SFX");
					soundeffect.getAudio().playAsSoundEffect(1f, 1f, false);
					break;
				case "Damage":
					equipment.getEquippedWeapon().setDamaging(event.getInt() == 1);
					equipment.getEquippedWeapon().setKnockback(event.getFloat() == 1);
					equipment.getEquippedWeapon().setSlot(event.getString());
					break;
				case "SuperArmor":
					equipment.setSuperArmor(Boolean.parseBoolean(event.getString()));
					break;
				case "Parry":
					equipment.setBlock(event.getInt() == 1);
					break;
				case "Chug":
					equipment.setChugDrink(event.getInt() == 1);
					break;
				case "Combo":
					equipment.setCombo(event.getInt(), event.getString());
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
		case CAST:
			// TODO Wait how am I going to handle spells? Oh right, total rework with charging and stuff
			if(animState.getCurrent(0).getTime() >= 0.4f && spell == null) {
				spell = new Spell("Spell 1",
						new Vector2f(this.getX() + (300 * (getDirection() == 0 ? 1 : -1)), this.getYPlane()), Camera.ASPECT_RATIO);
			}
		case ATTACK:
			if(equipment.getEquippedWeapon().isDamaging()) {
				Polygon box = getDamageBox();
				
				for(Actor e : ((Stage) Theater.get().getSetup()).getCast()) {
					if(e instanceof Combatant && e != this) {
						for(Rectangle2D r : ((Enemy) e).getHitBoxes(this)) {
							if(box.intersects(r) && Point2D.distance(0, this.getYPlane(), 0, ((Actor) e).getYPlane()) <= 25) {
								((Combatant) e).hit(this);
								break;
							}
						}
					}
				}
			}
		case DEFEND:
			// TODO Animation listener
			if(animState.getCurrent(0).isComplete()) {
				endCombat();
				setState(CharState.IDLE);
			}
			break;
		case HEAL:
			if(animState.getCurrent(0).isComplete()) {
				setState(CharState.IDLE);
				if(equipment.hasBell()) {
					skeleton.setAttachment("ITEM", "BELL");
				}
			}
			break;
		case HIT:
		case REVIVE:
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
				if(!Keybinds.RIGHT.press() && !Keybinds.LEFT.press())
					velocity.set(0, 0);
				setState(CharState.IDLE);
			}
			break;
		default:
			setState(CharState.IDLE);
		}
	}
	
	@Override
	public void updateBox() {
		this.box.setFrame(pos.x - ((skeleton.getData().getWidth() * scale) / 2f),
				(pos.y + (skeleton.getData().getCenterY() * scale)) - ((skeleton.getData().getHeight() * scale)),
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
			skeleton.setAttachment("WEAPON F", equipment.getEquippedWeapon().getName() + " F");
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
			skeleton.setAttachment("LEFT HAND F", (state == CharState.CAST ? "LEFT HAND OPEN F" : "LEFT HAND F"));
			if(state == CharState.DEFEND) {
				skeleton.setAttachment("SHIELD F", "SHIELD F");
			}
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
	public boolean canRun() {
		if(getVelocity().length() >= 1f) {
			if(state == CharState.RUN && stats.getStamina().getCurrent() > 0) {
				return true;
			} else if(stats.getStamina().getCurrent() >= (stats.getStamina().getMax() * 0.15f)) {
				return true;
			}
		}
		
		return false;
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
	public void moveDown() {
		if(velocity.x != 0f) {
			looking = 0;
			
			velocity.y += Theater.getDeltaSpeed(0.45f);
			if(velocity.length() > getMaxSpeed()) {
				velocity = MathFunctions.limitVector(velocity, getMaxSpeed());
			}
		} else {
			looking = -1;
		}
	}
	
	@Override
	public boolean dodge(Vector2f direction) {
		if(stats.getStamina().getCurrent() > 0f) {
			if(super.dodge(direction)) {
				stats.getStamina().addCurrent(-4f);
				return true;
			}
		}
		
		return false;
	}

	@Override
	public void endCombat() {
		looking = 0;
		
		switch(getState()) {
		case ATTACK:
			setDadArmRight(false);
			equipment.setSuperArmor(false);
			equipment.getEquippedWeapon().setDamaging(false);
			equipment.setCombo(-1, null);
			break;
		case DEFEND:
			setDadArmLeft(false);
			equipment.setBlock(false);
			break;
		case CAST:
			setDadArmLeft(false);
			break;
		default:
			break;
		}
	}
	
	@Override
	public void attack() {
		// TODO Check if Dad skull is enabled
		if(stats.getStamina().getCurrent() > 0f) {
			if(getState() != CharState.ATTACK) {
				setState(CharState.ATTACK);
				setDadArmRight(familiar);
				stats.getStamina().addCurrent(-5f);
			} else if(getState() == CharState.ATTACK && equipment.canCombo()) {
				animState.setAnimation(0, equipment.getEquippedWeapon().getAttackAnim() 
						+ (equipment.getStep() > 0 ? equipment.getStep() : ""), false);
				equipment.setSuperArmor(false);
				equipment.getEquippedWeapon().setDamaging(false);
				equipment.setCombo(-1, null);
				stats.getStamina().addCurrent(-2.5f);
			}
		}
	}

	@Override
	public void defend() {
		if(stats.getStamina().getCurrent() > 0f) {
			setState(CharState.DEFEND);
			setDadArmLeft(true);
			stats.getStamina().addCurrent(-3.5f);
		}
	}

	@Override
	public void hit(Combatant attacker) {
		setLooking(0);
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
				AudioSource parry = new AudioSource("Parried", "SFX");
				parry.getAudio().playAsSoundEffect(1f, 1f, false);
				
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
	
	public Polygon getDamageBox() {
		Polygon box = ((Region) skeleton.findSlot(equipment.getEquippedWeapon().getSlot()).getAttachment())
				.getRotatedBox(skeleton.findSlot(equipment.getEquippedWeapon().getSlot()), equipment.getEquippedWeapon().getDamageHitbox());
		box.translate((int) ((Region) skeleton.findSlot(equipment.getEquippedWeapon().getSlot()).getAttachment()).getWorldX(),
				(int) ((Region) skeleton.findSlot(equipment.getEquippedWeapon().getSlot()).getAttachment()).getWorldY());
		
		return box;
	}
	
	@Override
	public ArrayList<Rectangle2D> getHitBoxes(Combatant attacker) {
		ArrayList<Rectangle2D> hitboxes = new ArrayList<Rectangle2D>();
		for(Slot s : skeleton.getSlots()) {
			if(s.getAttachment() != null && !s.getData().getName().matches("WEAPON") && !s.getData().getName().endsWith(" F")) {
				Region region = (Region) s.getAttachment();
				Polygon p = region.getRotatedBox(s, null);
				p.translate((int) region.getWorldX(), (int) region.getWorldY());

				hitboxes.add(p.getBounds2D());
			}
		}
		
		return hitboxes;
	}
	
	@Override
	public Reputation getReputation() {
		return reputation;
	}
	
	public void heal() {
		if(equipment.getCurrentMilk() > 0) {
			if(state == CharState.HEAL && getEquipment().canChugDrink()) {
				animState.setAnimation(0, "Drinkmilk", false);
			} else {
				setState(CharState.HEAL);
			}
			equipment.setChugDrink(false);
			stats.getHealth().addCurrent(5f);
			equipment.setCurrentMilk(equipment.getCurrentMilk() - 1);
		}
	}

	public void cast() {
		// TODO Magic
		if(stats.getMagic().getCurrent() > 0 && equipment.hasBell()) {
			setState(CharState.CAST);
			setDadArmLeft(true);
			stats.getMagic().addCurrent(-5f);
		} else {
			stats.getMagic().setCurrent(stats.getMagic().getMax());
		}
	}
	
	public boolean canAct() {
		switch(getState()) {
		case HEAL:
			return getEquipment().canChugDrink();
		case ATTACK:
			return getEquipment().canCombo();
		default:
			return getState().canAct();
		}
	}
	
	@Override
	public void setState(CharState state) {
		super.setState(state);
		
		if(this.state != state) {
			switch(state) {
			case ATTACK:
				animState.setAnimation(0, equipment.getEquippedWeapon().getAttackAnim(), false);
				break;
			case CAST:
				animState.setAnimation(0, "Castspell", false);
				break;
			case DEFEND:
				animState.setAnimation(0, "Defend", false);
				break;
			case HEAL:
				animState.setAnimation(0, "Drinkmilk", false);
				break;
			case REVIVE:
				animState.setAnimation(0, "Revive", false);
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
		equipment.equipWeapon(weapon);
		skeleton.setAttachment("WEAPON", weapon.getName());
		animStateOverlay.setAnimation(0, "ChangeWeapon", false);
	}
	
	public void changeWeapon() {
		equipment.cycleWeapon();
		skeleton.setAttachment("WEAPON", equipment.getEquippedWeapon().getName());
		if(getState() == CharState.ATTACK) {
			skeleton.setAttachment("WEAPON F", equipment.getEquippedWeapon().getName() + " F");
		}
		animStateOverlay.setAnimation(0, "ChangeWeapon", false);
	}
	
	// TODO Add items
	public void changeItem() {
		equipment.equipBell(true);
		skeleton.setAttachment("ITEM", "BELL");
	}
	
	public void changeArmor(int type) {
		switch(type) {
		case 0:
			skeleton.setAttachment("ARMOR CHEST", null);
			skeleton.setAttachment("ARMOR CHEST 2", null);
			skeleton.setAttachment("ARMOR SPINE", null);
			skeleton.setAttachment("ARMOR RIGHT ARM", null);
			skeleton.setAttachment("ARMOR LEFT ARM", null);
			skeleton.setAttachment("ARMOR GROIN", null);
			skeleton.setAttachment("ARMOR GROIN 2", null);
			skeleton.setAttachment("ARMOR RIGHT THIGH", null);
			skeleton.setAttachment("ARMOR LEFT THIGH", null);
			skeleton.setAttachment("ARMOR HEAD", null);
			skeleton.setAttachment("ARMOR HEAD 2", null);
			break;
		case 1:
			skeleton.setAttachment("ARMOR CHEST", "ARMOR FLOCK CHEST");
			skeleton.setAttachment("ARMOR SPINE", "ARMOR FLOCK SPINE");
			skeleton.setAttachment("ARMOR GROIN", "ARMOR FLOCK GROIN");
			skeleton.setAttachment("ARMOR HEAD", "ARMOR FLOCK HEAD");
			break;
		case 2:
			skeleton.setAttachment("ARMOR CHEST", "ARMOR PILGRIM CHEST");
			skeleton.setAttachment("ARMOR SPINE", "ARMOR PILGRIM SPINE");
			skeleton.setAttachment("ARMOR RIGHT ARM", "ARMOR PILGRIM RIGHT ARM");
			skeleton.setAttachment("ARMOR LEFT ARM", "ARMOR PILGRIM LEFT ARM");
			skeleton.setAttachment("ARMOR GROIN", "ARMOR PILGRIM GROIN");
			skeleton.setAttachment("ARMOR GROIN 2", "ARMOR PILGRIM GROIN CLOTH");
			skeleton.setAttachment("ARMOR RIGHT THIGH", "ARMOR PILGRIM RIGHT THIGH");
			skeleton.setAttachment("ARMOR LEFT THIGH", "ARMOR PILGRIM LEFT THIGH");
			skeleton.setAttachment("ARMOR CHEST 2", "ARMOR PILGRIM HOOD");
			break;
		}
	}
	
	public Stats getStats() {
		return stats;
	}
	
	public void setFamiliar(boolean familiar) {
		this.familiar = familiar;
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
