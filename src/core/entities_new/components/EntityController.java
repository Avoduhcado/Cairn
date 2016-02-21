package core.entities_new.components;

import java.util.ArrayList;
import java.util.List;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.Slot;
import com.esotericsoftware.spine.attachments.Attachment;
import com.esotericsoftware.spine.attachments.Box2dAttachment;

import core.entities_new.Entity;
import core.entities_new.State;
import core.entities_new.event.ControllerEvent;
import core.entities_new.event.ControllerListener;
import core.entities_new.event.InventoryEvent;
import core.entities_new.event.StateChangeEvent;
import core.entities_new.utils.ControllerValidator;
import core.entities_new.utils.SensorData;
import core.setups.Stage;

public abstract class EntityController implements ControllerListener {

	protected Entity entity;
	
	protected float speed = 20f;
	protected float speedMod = 1f;
	protected Vec2 movement = new Vec2();
	
	protected List<FollowController> followers = new ArrayList<FollowController>();
	
	protected ControllerEvent eventQueue;
	
	public EntityController(Entity entity) {
		this.entity = entity;
	}

	@Override
	public void control() {
		controlMovement();
		controlActions();
		
		processEventQueue();
	}

	protected abstract void controlMovement();
	protected abstract void controlActions();

	@Override
	public void move(ControllerEvent e) {
		if(entity.getState().canMove()) {
			Vec2 movement = (Vec2) e.getData();
			movement.normalize();
			if(entity.isWalkingBackwards()) {
				speedMod -= 0.25f;
			}
			entity.getBody().applyForceToCenter(movement.mul(speed * speedMod));
			entity.fireEvent(new StateChangeEvent(speedMod > 1 ? State.RUN : State.WALK));
			
			if(entity.getBody().getLinearVelocity().x != 0 && !entity.isFixDirection() && entity.getRender() != null) {
				entity.getRender().setFlipped(entity.getBody().getLinearVelocity().x < 0);
			}
		}
	}

	@Override
	public void dodge(ControllerEvent e) {
		entity.getBody().setLinearDamping(2.5f);
		if(entity.getBody().getLinearVelocity().length() <= 0.25f) {
			entity.getBody().applyLinearImpulse(new Vec2(entity.getRender().isFlipped() ? 2.5f : -2.5f, 0f),
					entity.getBody().getWorldCenter());
		} else {
			entity.getBody().getLinearVelocity().normalize();
			entity.getBody().applyLinearImpulse(entity.getBody().getLinearVelocity().mul(2.5f * (speedMod * 0.75f)),
					entity.getBody().getWorldCenter());
		}

		entity.fireEvent(new StateChangeEvent(State.QUICKSTEP));
		entity.setFixDirection(true);
	}

	@Override
	public void attack(ControllerEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void defend(ControllerEvent e) {
		entity.setFixDirection((boolean) e.getData());
	}

	@Override
	public void collapse(ControllerEvent e) {
		if(entity.render() && entity.getRender() instanceof SpineRender) {
			SpineRender render = (SpineRender) entity.getRender();
			
			for(Slot slot : render.getSkeleton().drawOrder) {
				if(slot.getAttachment() == null) {
					continue;
				}
				Skeleton skeleton = render.getSkeleton();
				Box2dAttachment attachment = (Box2dAttachment) slot.getAttachment();
				float[] attVerts = attachment.getWorldVertices();
				
				BodyDef bodyDef = new BodyDef();
				bodyDef.position.set(attVerts[Attachment.X3] / Stage.SCALE_FACTOR,
						attVerts[Attachment.Y3] / Stage.SCALE_FACTOR);
				bodyDef.angle = (float) Math.toRadians(-slot.getBone().getWorldRotation() - attachment.getRotation());
				bodyDef.type = BodyType.DYNAMIC;

				PolygonShape bodyShape = new PolygonShape();
				bodyShape.setAsBox(attachment.getWidth() / Stage.SCALE_FACTOR / 2f, attachment.getHeight() / Stage.SCALE_FACTOR / 2f);

				FixtureDef boxFixture = new FixtureDef();
				boxFixture.density = 1f;
				boxFixture.shape = bodyShape;
				boxFixture.filter.categoryBits = 0b0011;
				boxFixture.filter.maskBits = 0b0111;

				Body body = entity.getContainer().getWorld().createBody(bodyDef);
				body.createFixture(boxFixture);
				body.setAngularDamping(1f);
				body.setGravityScale(1f);
				//body.setLinearDamping(1f);
				//body.setLinearVelocity(e.getVector());
				
				Entity bone = new Entity(entity.getName() + "/" + attachment.getName(), body, entity.getContainer());
				bone.getBody().getFixtureList().setUserData(new SensorData(bone, "Base", SensorData.CHARACTER));
				bone.getZBody().setGroundZ(skeleton.getY());
				
				bone.getRender().setFlipped(entity.getRender().isFlipped());
				
				entity.getBody().destroyFixture(attachment.getFixture());
				slot.setAttachment(null);
				
				entity.getContainer().queueEntity(bone, true);
			}
		}
	}

	@Override
	public void jump(ControllerEvent e) {
		if(entity.getZBody().getGroundZ() == 0) {
			entity.fireEvent(new StateChangeEvent(State.JUMPING));
			entity.getBody().applyLinearImpulse((Vec2) e.getData(), entity.getBody().getWorldCenter());
			entity.getBody().setGravityScale(1f);
			entity.getBody().setLinearDamping(1f);
			entity.getZBody().setGroundZ(entity.getBody().getPosition().y * Stage.SCALE_FACTOR);
		}
	}

	@Override
	public void changeWeapon(ControllerEvent e) {
		entity.fireEvent(new InventoryEvent(InventoryEvent.CYCLE));
	}
	
	public List<FollowController> getFollowers() {
		return followers;
	}
	
	public void addFollower(FollowController follower) {
		this.followers.add(follower);
	}
	
	public boolean removeFollower(FollowController follower) {
		return this.followers.remove(follower);
	}

	public void fireEvent(ControllerEvent e) {
		switch(e.getType()) {
		case ControllerEvent.MOVE:
			move(e);
			break;
		case ControllerEvent.DODGE:
			dodge(e);
			break;
		case ControllerEvent.ATTACK:
			attack(e);
			break;
		case ControllerEvent.DEFEND:
			defend(e);
			break;
		case ControllerEvent.COLLAPSE:
			collapse(e);
			break;
		case ControllerEvent.JUMP:
			jump(e);
			break;
		case ControllerEvent.CHANGE_WEAPON:
			changeWeapon(e);
			break;
		case ControllerEvent.REMOVE:
			entity.getContainer().queueEntity(entity, false);
			break;
		}
	}

	protected void processEventQueue() {
		if(eventQueue != null && !entity.getState().isActing()) {
			fireEvent(eventQueue);
			
			// Notify any followers of your actions, oh Lord
			followers.stream().forEach(e -> e.setEventQueue(eventQueue));
			
			eventQueue = null;
		}
	}
	
	protected ControllerEvent validateEvent(ControllerEvent event) {
		switch(event.getType()) {
		default:
			return event;
		case ControllerEvent.ATTACK:
			return ControllerValidator.validateAttack(event, entity);
		}
	}
	
	public void setEventQueue(ControllerEvent event) {
		this.eventQueue = validateEvent(event);
	}
	
}
