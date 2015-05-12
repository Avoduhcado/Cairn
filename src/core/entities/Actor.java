package core.entities;

import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationState.AnimationStateAdapter;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.Event;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonJson;
import com.esotericsoftware.spine.Slot;
import com.esotericsoftware.spine.attachments.Region;

import core.Camera;
import core.Theater;
import core.audio.Ensemble;
import core.entities.interfaces.Mobile;
import core.entities.utils.CharState;
import core.render.DrawUtils;
import core.render.SpriteIndex;
import core.scene.collisions.Collidable;
import core.scene.collisions.HitMaps;
import core.utilities.Cheats;
import core.utilities.MathFunctions;
import core.utilities.text.Text;

public class Actor extends Entity implements Mobile {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static transient int count = 0;
	
	protected transient Skeleton skeleton;
	protected transient AnimationState animState;
	protected transient AnimationStateData animStateData;
	
	protected int direction;
	protected transient Vector2f velocity = new Vector2f(0, 0);
	private transient Vector2f speed;
	private float maxSpeed;
	private float maxRunSpeed;
	
	protected transient CharState state;
	
	public Actor(float x, float y, String ref, float scale) {
		this.pos = new Vector2f(x, y);
		this.name = ref;
		this.scale = scale;
		
		buildSkeleton();
		
		this.velocity = new Vector2f(0, 0);
		this.maxSpeed = 3.5f;
		this.maxRunSpeed = 8.5f;
		state = CharState.IDLE;		
	}
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		
		buildSkeleton();
		this.velocity = new Vector2f(0, 0);
		this.state = CharState.IDLE;
	}

	public void update() {
		updateAnimations();
		
		if(velocity.length() != 0) {
			if(velocity.x > 0) {
				velocity.setX(MathFunctions.clamp(velocity.x - Theater.getDeltaSpeed(0.15f), 0, velocity.x));
			} else if(velocity.x < 0) {
				velocity.setX(MathFunctions.clamp(velocity.x + Theater.getDeltaSpeed(0.15f), velocity.x, 0));
			}
			
			if(velocity.y > 0) {
				velocity.setY(MathFunctions.clamp(velocity.y - Theater.getDeltaSpeed(0.25f), 0, velocity.y));
			} else if(velocity.y < 0) {
				velocity.setY(MathFunctions.clamp(velocity.y + Theater.getDeltaSpeed(0.25f), velocity.y, 0));
			}
			
			checkCollision();
		
			if(this.equals(Camera.get().getFocus())) {
				Camera.get().follow();
			}
			move();
			
			if(velocity.x != 0 && state == CharState.WALK) {
				setDirection(velocity.x > 0 ? 0 : 1);
			}
		}
		
		updateState();
		
		skeleton.setX(pos.x);
		skeleton.setY(pos.y + (skeleton.getData().getCenterY() * scale));
		
		skeleton.setFlipX(direction == 1);
		skeleton.setFlipY(true);
		skeleton.updateWorldTransform();
	}
	
	public void draw() {
		for(Slot s : skeleton.drawOrder) {
			if(s.getAttachment() != null) {
				Region region = (Region) s.getAttachment();
				region.updateWorldVertices(s);
				sprite = name + "/" + s.getAttachment().getName();

				SpriteIndex.getSprite(sprite).set2DScale(scale);
				SpriteIndex.getSprite(sprite).setFlipped(skeleton.getFlipX());
				if(skeleton.getFlipX()) {
					SpriteIndex.getSprite(sprite).set2DRotation(s.getBone().getWorldRotation() + region.getRotation(), 0f);
				} else {
					SpriteIndex.getSprite(sprite).set2DRotation(-s.getBone().getWorldRotation() - region.getRotation(), 0f);
				}
				SpriteIndex.getSprite(sprite).setColor(s.getColor());
				SpriteIndex.getSprite(sprite).draw(region.getWorldX(), region.getWorldY());
			}
		}
		
		if(Theater.get().debug) {
			for(Slot s : skeleton.drawOrder) {
				if(s.getAttachment() != null) {
					Region region = (Region) s.getAttachment();

					DrawUtils.drawShape(region.getWorldX(), region.getWorldY(),
							region.getBox().getPathIterator(region.getTransform(s, null)));
				}
			}
			
			//DrawUtils.drawRect(pos.x, pos.y, getBox());
			DrawUtils.setColor(new Vector3f(1f, 0f, 1f));
			DrawUtils.drawRect((float) getBox().getX(), (float) getBox().getY(), getBox());
			DrawUtils.setColor(new Vector3f(0f, 0f, 1f));
			DrawUtils.drawLine(new Line2D.Double(getPosition().x - 5, getPosition().y, getPosition().x + 5, getPosition().y));
			DrawUtils.setColor(new Vector3f(0f, 0f, 1f));
			DrawUtils.drawLine(new Line2D.Double(getPosition().x, getPosition().y - 5, getPosition().x, getPosition().y + 5));
			Text.getDefault().drawString(state.toString() + ", " + getID(), pos.x, pos.y);
		}
	}
	
	public void buildSkeleton() {
		SkeletonJson json = new SkeletonJson(null);
		json.setScale(scale);
		if(name.contains("_")) {
			skeleton = new Skeleton(json.readSkeletonData(name.split("_")[0]));
		} else {
			skeleton = new Skeleton(json.readSkeletonData(name));
		}
		skeleton.updateWorldTransform();
				
		/*this.box = new Rectangle2D.Double(pos.x - ((skeleton.getData().getWidth() * scale) / 2f),
				pos.y - ((skeleton.getData().getHeight() * scale) / 2f), 
				skeleton.getData().getWidth() * scale, skeleton.getData().getHeight() * scale);*/
		this.box = new Rectangle2D.Double(pos.x - ((skeleton.getData().getWidth() * scale) / 2f),
				(pos.y + (skeleton.getData().getCenterY() * scale)) - ((skeleton.getData().getHeight() * scale)), 
				skeleton.getData().getWidth() * scale, skeleton.getData().getHeight() * scale);
		
		animStateData = new AnimationStateData(skeleton.getData());
		animStateData.setDefaultMix(0.2f);
		if(animStateData.getSkeletonData().findAnimation("Attack") != null) {
			animStateData.setMix("Idle", "Attack", 0f);
		}
		if(animStateData.getSkeletonData().findAnimation("Defend") != null) {
			animStateData.setMix("Idle", "Defend", 0f);
		}
		if(animStateData.getSkeletonData().findAnimation("QuickStep") != null) {
			animStateData.setMix("QuickStep", "Idle", 0.2f);
		}
		if(animStateData.getSkeletonData().findAnimation("Hit") != null) {
			animStateData.setMix("Idle", "Hit", 0f);
			if(animStateData.getSkeletonData().findAnimation("Walk") != null)
				animStateData.setMix("Walk", "Hit", 0f);
		}
		animState = new AnimationState(animStateData);
		animState.setAnimation(0, "Idle", true);
		
		buildAnimationEvents();
	}
	
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
					setMaxSpeed(event.getFloat());
					break;
				case "Footstep":
					Ensemble.get().playSoundEffect(event.getString());
					break;
				default:
					System.out.println("Unhandled event: " + event.getData());
				}
			}
		});
	}
	
	public void updateAnimations() {
		animState.update(Theater.getDeltaSpeed(0.016f) * (velocity.length() == 0 ? 1 : velocity.length() / maxSpeed));
		animState.apply(skeleton);
	}
	
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
		default:
			setState(CharState.IDLE);
		}
	}
	
	@Override
	public void updateBox() {
		/*this.box.setFrame(pos.x - ((skeleton.getData().getWidth() * scale) / 2f),
				pos.y - ((skeleton.getData().getHeight() * scale) / 2f), 
				box.getWidth(), box.getHeight());*/
		this.box.setFrame(pos.x - ((skeleton.getData().getWidth() * scale) / 2f),
				(pos.y + (skeleton.getData().getCenterY() * scale)) - ((skeleton.getData().getHeight() * scale)),
				box.getWidth(), box.getHeight());
	}
	
	@Override
	public void checkCollision() {
		// Movement velocity scaled to current frame rate
		speed = new Vector2f(Theater.getDeltaSpeed(velocity.x), Theater.getDeltaSpeed(velocity.y));
		// End because we're not moving
		if(speed.length() == 0)
			return;
				
		if(!Cheats.NO_CLIP) {
			// Temporary collision point to be used in collision detection
			//Point2D tempPos = new Point2D.Double(getBox().getCenterX(), getBox().getCenterY());
			Point2D tempPos = new Point2D.Double(getPosition().x, getYPlane());
			// Movement velocity's unit vector
			Vector2f unit = speed.normalise(null);
			// Total distance moved so far
			Vector2f totalMovementVector = new Vector2f();
			
			do {
				// Increment totalMovement
				totalMovementVector = MathFunctions.limitVector(Vector2f.add(totalMovementVector, unit, null),
						speed.length());
				// Translate position along unit vector
				tempPos.setLocation(getPosition().x + totalMovementVector.getX(),
						getYPlane() + totalMovementVector.getY());
				// Check for collisions against all relative collidables
				for(Collidable c : HitMaps.getMapSector(HitMaps.getCollisionMap(), new Point((int) tempPos.getX(), (int) tempPos.getY()))) {
					// Detect collision
					if(c.intersects(tempPos)) {
						//Vector2f.sub(totalMovementVector, (Vector2f) unit.scale(0.5f), totalMovementVector);
						// React to collision by dotting the magnitude and scaling the wall normal against the
						// current movement distance, and subtracting from the speed
						Vector2f.sub(totalMovementVector,
								(Vector2f) c.getNormal().scale(Vector2f.dot(totalMovementVector, c.getNormal())), speed);
						
						totalMovementVector.set(speed);
						tempPos.setLocation(getPosition().x + totalMovementVector.getX(),
								getYPlane() + totalMovementVector.getY());
						
						if(speed.length() < 0.5f)
							speed.set(0, 0);
					}
				}
			} while(speed.length() - totalMovementVector.length() > 0.0005f);
		}
	}

	@Override
	public void move() {
		Vector2f.add(speed, pos, pos);
		updateBox();
	}

	@Override
	// TODO THIS IS STTUUUUUUPID
	public Vector2f getVelocity() {
		return speed;
	}
	
	public void setVelocity(float x, float y) {
		this.velocity.x = x;
		this.velocity.y = y;
	}
	
	public void setVelocity(Vector2f velocity) {
		this.velocity.x = velocity.x;
		this.velocity.y = velocity.y;
	}

	public void moveRight() {
		velocity.x += Theater.getDeltaSpeed(0.5f);
		if(velocity.length() > getMaxSpeed()) {
			velocity = MathFunctions.limitVector(velocity, getMaxSpeed());
		}
	}
	
	public void moveLeft() {
		velocity.x -= Theater.getDeltaSpeed(0.5f);
		if(velocity.length() > getMaxSpeed()) {
			velocity = MathFunctions.limitVector(velocity, getMaxSpeed());
		}
	}
	
	public void moveUp() {
		if(velocity.x != 0f) {
			velocity.y -= Theater.getDeltaSpeed(0.45f);
			if(velocity.length() > getMaxSpeed()) {
				velocity = MathFunctions.limitVector(velocity, getMaxSpeed());
			}
		}
	}
	
	public void moveDown() {
		if(velocity.x != 0f) {			
			velocity.y += Theater.getDeltaSpeed(0.45f);
			if(velocity.length() > getMaxSpeed()) {
				velocity = MathFunctions.limitVector(velocity, getMaxSpeed());
			}
		}
	}

	public void dodge(Vector2f direction) {
		if(direction == null && state != CharState.WALK) {
			this.velocity.setX(getDirection() == 0 ? -5f : 5f);
			this.setState(CharState.QUICKSTEP);
		} else if(direction != null) {
			this.velocity.set(direction);
			this.setState(CharState.QUICKSTEP);
		}
	}
	
	public Skeleton getSkeleton() {
		return skeleton;
	}

	/**
	 * @return 0 when facing right, 1 when facing left
	 */
	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}
	
	public CharState getState() {
		return state;
	}
	
	public void setState(CharState state) {
		if(this.state != state) {
			switch(state) {
			case IDLE:
				animState.setAnimation(0, "Idle", true);
				break;
			case WALK:
			case RUN:
				animState.setAnimation(0, "Walk", true);
				break;
			case QUICKSTEP:
				animState.setAnimation(0, "QuickStep", false);
				break;
			case HIT:
				animState.setAnimation(0, "Hit", false);
				break;
			case DEATH:
				animState.setAnimation(0, "Death", false);
				break;
			default:
				return;
			}
			
			this.state = state;
		}
	}

	public float getMaxSpeed() {
		if(state == CharState.RUN) {
			return maxRunSpeed;
		}
		return maxSpeed;
	}

	public void setMaxSpeed(float maxSpeed) {
		this.maxSpeed = maxSpeed;
	}
	
	public float getYPlane() {
		return getY() + (skeleton.getData().getCenterY() * scale);
	}
	
	@Override
	public void setID() {
		this.ID = this.getClass().getSimpleName() + count++;
	}
	
	public static void reset() {
		count = 0;
	}

}
