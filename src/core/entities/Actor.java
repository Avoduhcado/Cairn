package core.entities;

import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Set;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.lwjgl.opengl.GL11;
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
import core.audio.AudioSource;
import core.entities.interfaces.Bonable;
import core.entities.interfaces.Mobile;
import core.entities.utils.BoxUserData;
import core.entities.utils.CharState;
import core.render.DrawUtils;
import core.render.SpriteIndex;
import core.scene.collisions.Collidable;
import core.scene.collisions.HitMaps;
import core.utilities.Cheats;
import core.utilities.MathFunctions;
import core.utilities.text.Text;

public class Actor extends Entity implements Mobile, Bonable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		
	protected transient Skeleton skeleton;
	protected transient AnimationState animState;
	protected transient AnimationStateData animStateData;
	protected transient Body body;
	
	protected int direction;
	protected transient Vector2f velocity = new Vector2f(0, 0);
	private transient Vector2f speed;
	private float maxSpeed;
	private float maxRunSpeed;
	
	protected transient CharState state;
	
	public Actor(float x, float y, String ref) {
		this.pos = new Vector2f(x, y);
		this.sprite = "actors/" + ref;
		this.name = ref.split("_")[0];
		this.scale = Camera.ASPECT_RATIO;
		
		buildSkeleton();
		
		this.velocity = new Vector2f(0, 0);
		this.maxSpeed = 3.5f;
		this.maxRunSpeed = this.maxSpeed * 1.5f;
		//this.maxRunSpeed = 5.25f;
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
			body.setLinearDamping(1f);
			//checkCollision();
			speed = new Vector2f(Theater.getDeltaSpeed(velocity.x), Theater.getDeltaSpeed(velocity.y));
		
			if(this.equals(Camera.get().getFocus())) {
				Camera.get().follow();
			}
			move();
			
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
			
			if(velocity.x != 0 && state.canWalk()) {
				setDirection(velocity.x > 0 ? 0 : 1);
			}
		} else {
			body.setLinearDamping(8f);
		}
		
		updateState();
		
		//skeleton.setX(pos.x);
		//skeleton.setY(pos.y + (skeleton.getData().getCenterY() * scale));
		skeleton.setX(body.getPosition().x * 30);
		skeleton.setY((body.getPosition().y * 30) + (skeleton.getData().getCenterY() * scale));
		
		skeleton.setFlipX(direction == 1);
		skeleton.setFlipY(true);
		skeleton.updateWorldTransform();
		
		if(body != null) {
			//collisionBox.applyForce(collisionBox.getWorld().getGravity().mul(-collisionBox.getMass()), collisionBox.getWorldCenter());
		}
	}
	
	public void draw() {		
		for(Slot s : skeleton.drawOrder) {
			if(s.getAttachment() != null) {
				Region region = (Region) s.getAttachment();
				region.updateWorldVertices(s);
				String attachment = "/" + s.getAttachment().getName();
				//sprite = name + "/" + s.getAttachment().getName();

				SpriteIndex.getSprite(sprite + attachment).set2DScale(scale);
				SpriteIndex.getSprite(sprite + attachment).setFlipped(skeleton.getFlipX());
				if(skeleton.getFlipX()) {
					SpriteIndex.getSprite(sprite + attachment).set2DRotation(s.getBone().getWorldRotation() + region.getRotation(), 0f);
				} else {
					SpriteIndex.getSprite(sprite + attachment).set2DRotation(-s.getBone().getWorldRotation() - region.getRotation(), 0f);
				}
				SpriteIndex.getSprite(sprite + attachment).setColor(s.getColor());
				SpriteIndex.getSprite(sprite + attachment).draw(region.getWorldX(), region.getWorldY());
			}
		}

		drawDebug();
	}
	
	@Override
	public void drawDebug() {
		if(Theater.get().debug || this.debug) {
			for(Slot s : skeleton.drawOrder) {
				if(s.getAttachment() != null) {
					Region region = (Region) s.getAttachment();

					DrawUtils.drawShape(region.getWorldX(), region.getWorldY(),
							region.getBox().getPathIterator(region.getTransform(s, null)));
				}
			}
			
			DrawUtils.setColor(new Vector3f(1f, 0f, 1f));
			DrawUtils.drawRect((float) getBox().getX(), (float) getBox().getY(), getBox());
			DrawUtils.setColor(new Vector3f(0f, 0f, 1f));
			DrawUtils.drawLine(new Line2D.Double(getPosition().x - 5, getPosition().y, getPosition().x + 5, getPosition().y));
			DrawUtils.setColor(new Vector3f(0f, 0f, 1f));
			DrawUtils.drawLine(new Line2D.Double(getPosition().x, getPosition().y - 5, getPosition().x, getPosition().y + 5));
			Text.getDefault().drawString(state.toString() + ", " + getID(), pos.x, pos.y);
			
			if(body != null) {
				DrawUtils.setColor(new Vector3f(0f, 1f, 0f));
				DrawUtils.drawRect((body.getPosition().x * 30) - (body.m_fixtureList.getShape().m_radius * 30),
						(body.getPosition().y * 30) - (body.m_fixtureList.getShape().m_radius * 30), 
						new Rectangle2D.Double(0, 0,
								body.m_fixtureList.getShape().m_radius * 30 * 2, body.m_fixtureList.getShape().m_radius * 30 * 2));
			}
		}
	}

	@Override
	public Skeleton getSkeleton() {
		return skeleton;
	}

	@Override
	public AnimationState getAnimState() {
		return animState;
	}

	@Override
	public AnimationStateData getAnimStateData() {
		return animStateData;
	}

	public void buildSkeleton() {
		SkeletonJson json = new SkeletonJson(null);
		json.setScale(scale);
		if(sprite.contains("_")) {
			skeleton = new Skeleton(json.readSkeletonData(sprite.split("_")[0] + "/" + name));
		} else {
			skeleton = new Skeleton(json.readSkeletonData(sprite + "/" + name));
		}
		skeleton.updateWorldTransform();
				
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
					// TODO
					break;
				default:
					System.out.println("Unhandled event: " + event.getData());
				}
			}
		});
	}
	
	@Override
	public void updateAnimations() {
		animState.update(Theater.getDeltaSpeed(0.016f) * (velocity.length() == 0 ? 1 : velocity.length() / maxSpeed));
		animState.apply(skeleton);
	}

	@Override
	public void collapse(World world, Set<Clutter> bodies, Vec2 force) {
		AudioSource soundeffect = new AudioSource("Spring Rattle", "SFX");
		soundeffect.getAudio().playAsSoundEffect(1f, 1f, false);
		
		// TODO Auto-generated method stub
		for(Slot s : skeleton.drawOrder) {
			if(s.getAttachment() != null) {
				BodyDef boxDef = new BodyDef();
				boxDef.position.set(((Region) s.getAttachment()).getWorldX() / 30, ((Region) s.getAttachment()).getWorldY() / 30);
				boxDef.angle = (float) Math.toRadians(-s.getBone().getWorldRotation() - ((Region) s.getAttachment()).getRotation());
				boxDef.type = BodyType.DYNAMIC;
				
				PolygonShape boxShape = new PolygonShape();
				boxShape.setAsBox(((Region) s.getAttachment()).getWidth() / 30 / 2, ((Region) s.getAttachment()).getHeight() / 30 / 2);
				
				FixtureDef boxFixture = new FixtureDef();
				boxFixture.density = 1f;
				boxFixture.shape = boxShape;
				boxFixture.filter.maskBits = 0;
				Body box = world.createBody(boxDef);
				box.createFixture(boxFixture);
				//box.setUserData(sprite + "/" + s.getAttachment().getName());
				box.setUserData(
						new BoxUserData(0, (this.getYPlane() - ((Region) s.getAttachment()).getWorldY()) / 30,
						sprite + "/" + s.getAttachment().getName(), skeleton.getFlipX()));
				//box.applyForce(new Vec2((float) (Math.random() * 50f) - 25f, -25f), box.getWorldCenter());
				//box.applyForce(force, box.getWorldCenter());
				//box.applyLinearImpulse(new Vec2(100, 0), new Vec2(skeleton.getRootBone().getWorldX(), skeleton.getRootBone().getWorldY()));

				bodies.add(new Clutter(box));
			}
		}
	}

	@Override
	public Body getCollisionBox() {
		return body;
	}
	
	@Override
	public void setCollisionBox(Body box) {
		this.body = box;
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
		this.box.setFrameFromCenter(pos.x, pos.y, pos.x - (box.getWidth() / 2f), pos.y - (box.getHeight() / 2f));
		/*this.box.setFrame(pos.x - ((skeleton.getData().getWidth() * scale) / 2f),
				(pos.y + (skeleton.getData().getCenterY() * scale)) - ((skeleton.getData().getHeight() * scale)),
				box.getWidth(), box.getHeight());*/
		/*if(body != null) {
			body.setTransform(new Vec2((float) box.getCenterX() / 30, (float) (box.getMaxY() - (box.getHeight() * 0.15f)) / 30), 0);
		}*/
	}
	
	public void lookAt(Entity target) {
		setDirection(target.getX() > this.getX() ? 0 : 1);
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
		//Vector2f.add(speed, pos, pos);
		//body.applyLinearImpulse(new Vec2(speed.x, speed.y), body.getWorldCenter());
		//body.applyForceToCenter(new Vec2(speed.x, speed.y));
		if(body.getLinearVelocity().length() < getMaxSpeed()) {
			body.applyForceToCenter(new Vec2(velocity.x, velocity.y));
			System.out.println(body.getLinearVelocity() + " " + velocity);
		} else {
			System.out.println("TOO FAST!!!!" + body.getLinearVelocity() + " " + velocity);
		}
		
		pos.set(body.getPosition().x * 30, body.getPosition().y * 30);
		updateBox();
	}
	
	@Override
	public boolean canRun() {
		return false;
	}

	@Override
	public Vector2f getVelocity() {
		return velocity;
	}
	
	@Override
	public Vector2f getSpeed() {
		return new Vector2f(body.getLinearVelocity().x, body.getLinearVelocity().y);
		//return speed;
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

	// TODO Remove boolean and tag stamina drain to state change?
	public boolean dodge(Vector2f direction) {
		if(direction == null && state == CharState.IDLE) {
			this.velocity.setX(getDirection() == 0 ? -5f : 5f);
			this.setState(CharState.QUICKSTEP);
			return true;
		} else if(direction != null) {
			this.velocity.set(direction);
			this.setState(CharState.QUICKSTEP);
			return true;
		}
		
		return false;
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
			case RUN:
				animState.setAnimation(0, "Run", true);
				break;
			case WALK:
				animState.setAnimation(0, "Walk", true);
				break;
			case QUICKSTEP:
				if(animStateData.getSkeletonData().findAnimation("QuickStep") != null)
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
		//this.maxRunSpeed = this.maxSpeed * 1.5f;
	}
	
	public Point2D getPositionAsPoint() {
		return new Point2D.Double(pos.x, getYPlane());
	}
	
	public float getYPlane() {
		return getY() + (skeleton.getData().getCenterY() * scale);
	}
	
	@Override
	public void setID() {
		this.ID = this.getClass().getSimpleName() + count++;
	}

}
