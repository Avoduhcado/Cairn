package core.entities_new;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.contacts.Contact;

import com.esotericsoftware.spine.Slot;
import com.esotericsoftware.spine.attachments.Box2dAttachment;

import core.Camera;
import core.entities_new.components.geometrics.ZBody;
import core.entities_new.components.renders.PlainRender;
import core.entities_new.components.renders.SpineRender;
import core.entities_new.components.states.TimedStateManager;
import core.entities_new.utils.BodyData;
import core.entities_new.utils.BodyLoader;
import core.setups.Stage;
import core.setups.WorldContainer;

public class EntityData {

	private String name;
	private float x, y;
	private WorldContainer container;
	private Contact contact;

	public EntityData(String name, float x, float y, WorldContainer container, Contact contact) {
		this.name = name;
		this.x = x;
		this.y = y;
		this.container = container;
		this.contact = contact;
	}
	
	public Entity createEntity() {
		Entity entity = new Entity(name, new BodyData(x, y, BodyLoader.PLAIN_ENTITY), container);
		
		applyContact(entity);
		
		return entity;
	}

	private void applyContact(Entity entity) {
		if(contact != null) {
			if(entity.getRender() instanceof SpineRender) {
				SpineRender render = (SpineRender) entity.getRender();
				
				for(Slot s : render.getSkeleton().getSlots()) {
					if(!(s.getAttachment() instanceof Box2dAttachment)) {
						continue;
					}
					Box2dAttachment attachment = (Box2dAttachment) s.getAttachment();
					if(attachment == null) {
						continue;
					}
					//((SensorData) attachment.getBody().getUserData()).setType(SensorData.IGNORE);
				}
			}
			
			entity.getBody().setGravityScale(2f);
			entity.getBody().setLinearDamping(1f);
			entity.getZBody().setGroundZ((entity.getBody().getPosition().y * Stage.SCALE_FACTOR) +
					(float) ((Math.random() * 15f) + 15f));
		}
		if(entity.getRender() == null) {
			PlainRender render = new PlainRender(name + "/Shard" + ((int) (Math.random() * 4) + 1), entity);
			entity.setRender(render);
			{
				entity.getContainer().getWorld().destroyBody(entity.getBody());
				BodyDef bodyDef = new BodyDef();
				bodyDef.position.set(x / Stage.SCALE_FACTOR, y / Stage.SCALE_FACTOR);
				bodyDef.type = BodyType.DYNAMIC;

				PolygonShape bodyShape = new PolygonShape();
				Vec2[] verts = new Vec2[] {
						new Vec2(0, 0),
						new Vec2(15 / Stage.SCALE_FACTOR, 0),
						new Vec2(15 / Stage.SCALE_FACTOR, 15 / Stage.SCALE_FACTOR),
						new Vec2(0, 15 / Stage.SCALE_FACTOR),
				};
				for(Vec2 v : verts) {
					v.mulLocal(Camera.ASPECT_RATIO);
				}
				bodyShape.set(verts, 4);

				FixtureDef boxFixture = new FixtureDef();
				boxFixture.density = 1f;
				boxFixture.shape = bodyShape;
				
				Body body = entity.getContainer().getWorld().createBody(bodyDef);
				body.createFixture(boxFixture);
				body.setLinearDamping(5f);
				body.setAngularDamping(4.5f);
				body.setGravityScale(1f);
				body.setUserData(entity);
				
				entity.setZBody(new ZBody(body, entity));
			}
			entity.getBody().applyLinearImpulse(new Vec2(1.5f, -1.5f), entity.getBody().getWorldCenter());
			entity.getBody().applyTorque(15f);
			entity.setStateManager(new TimedStateManager(entity, 0.75f));
		}
	}
	
}
