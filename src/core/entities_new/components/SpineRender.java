package core.entities_new.components;

import java.io.Serializable;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.EdgeShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Filter;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.Bone;
import com.esotericsoftware.spine.Event;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonJson;
import com.esotericsoftware.spine.Skin;
import com.esotericsoftware.spine.Slot;
import com.esotericsoftware.spine.AnimationState.AnimationStateAdapter;
import com.esotericsoftware.spine.attachments.Attachment;
import com.esotericsoftware.spine.attachments.AttachmentLoader;
import com.esotericsoftware.spine.attachments.BoundingBoxAttachment;
import com.esotericsoftware.spine.attachments.Box2dAttachment;
import com.esotericsoftware.spine.attachments.MeshAttachment;
import com.esotericsoftware.spine.attachments.RegionAttachment;
import com.esotericsoftware.spine.attachments.SkinnedMeshAttachment;

import core.Camera;
import core.Theater;
import core.entities_new.State;
import core.entities_new.Entity;
import core.entities_new.event.EntityEvent;
import core.entities_new.event.StateChangeEvent;
import core.entities_new.event.StateChangeListener;
import core.entities_new.utils.SensorData;
import core.render.DrawUtils;
import core.render.Sprite;
import core.render.SpriteList;
import core.render.transform.Transform;
import core.setups.Stage_new;

public class SpineRender implements Renderable, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String sprite;
	private transient Skeleton skeleton;
	private transient AnimationState animState;
	private transient AnimationStateData animStateData;
	
	private Entity entity;

	private Transform transform;

	private StateChangeListener stateChangeListener;

	public SpineRender(String ref, Entity entity) {
		this.entity = entity;

		this.stateChangeListener = e -> {
			String animation = e.getNewState().getAnimation();
			boolean loop = e.getNewState().loop;

			if(animState.getData().getSkeletonData().findAnimation(animation) != null) {
				animState.setAnimation(0, animation, loop);
			}
		};
		
		this.sprite = ref;
		buildSkeleton(ref);
		buildBodies();
		buildAnimationEvents();
		this.transform = new Transform();
	}

	private void buildSkeleton(String ref) {
		SkeletonJson json = new SkeletonJson(new AttachmentLoaderAdapter() {
			public RegionAttachment newRegionAttachment(Skin skin, String name, String path) {
				Box2dAttachment attachment = new Box2dAttachment(name);
				Sprite region = SpriteList.get(sprite + "/" + name);
				attachment.setRegion(region);

				return attachment;
			}
		});

		json.setScale(Camera.ASPECT_RATIO);

		if(ref.contains("_")) {
			skeleton = new Skeleton(json.readSkeletonData(ref.split("_")[0], ref));
		} else {
			skeleton = new Skeleton(json.readSkeletonData(ref, ref));
		}

		skeleton.setPosition(entity.getBody().getPosition().x * Stage_new.SCALE_FACTOR, entity.getBody().getPosition().y * Stage_new.SCALE_FACTOR);
		skeleton.setFlipY(true);
		skeleton.updateWorldTransform();

		animStateData = new AnimationStateData(skeleton.getData());
		animStateData.setDefaultMix(0.2f);

		// TODO Add in custom animation data loading

		animState = new AnimationState(animStateData);
		animState.setAnimation(0, "Idle", true);
	}

	private void buildBodies() {
		for(Slot s : skeleton.drawOrder) {
			if(s.getAttachment() != null && s.getAttachment() instanceof Box2dAttachment) {
				buildFixture(s, entity.getBody());
			}
		}
	}
	
	private void buildFixture(Slot slot, Body body) {
		Box2dAttachment attachment = (Box2dAttachment) slot.getAttachment();
		attachment.updateWorldVertices(slot, false);
		float[] attVerts = attachment.getWorldVertices();
		
		PolygonShape bodyShape = new PolygonShape();
		Vec2[] verts = new Vec2[] {
				new Vec2((attVerts[Attachment.X1] - skeleton.getX()) / Stage_new.SCALE_FACTOR,
						(attVerts[Attachment.Y1] - skeleton.getY()) / Stage_new.SCALE_FACTOR),
				new Vec2((attVerts[Attachment.X2] - skeleton.getX()) / Stage_new.SCALE_FACTOR,
						(attVerts[Attachment.Y2] - skeleton.getY()) / Stage_new.SCALE_FACTOR),
				new Vec2((attVerts[Attachment.X3] - skeleton.getX()) / Stage_new.SCALE_FACTOR,
						(attVerts[Attachment.Y3] - skeleton.getY()) / Stage_new.SCALE_FACTOR),
				new Vec2((attVerts[Attachment.X4] - skeleton.getX()) / Stage_new.SCALE_FACTOR,
						(attVerts[Attachment.Y4] - skeleton.getY()) / Stage_new.SCALE_FACTOR)	
		};
		bodyShape.set(verts, 4);

		FixtureDef boxFixture = new FixtureDef();
		boxFixture.density = 0f;
		boxFixture.shape = bodyShape;
		boxFixture.filter.categoryBits = 0b1000;
		boxFixture.filter.maskBits = 0b0110;
		boxFixture.userData = new SensorData(entity, slot.getData().getName(), SensorData.BODY);
		//boxFixture.userData = slot;
				
		attachment.setFixture(body.createFixture(boxFixture));
	}

	private void buildAnimationEvents() {
		animState.addListener(new AnimationStateAdapter() {
			@Override
			public void event(int trackIndex, Event event) {
				switch(event.getData().getName()) {
				case "Shake":
					String[] shakeData = event.getString().split(";");
					Camera.get().setShake(new Vector2f(Float.parseFloat(shakeData[0]), Float.parseFloat(shakeData[1])),
							5.5f, Float.parseFloat(shakeData[3]));
					break;
				case "Footstep":
					// TODO
					break;
				case "Damage":
					//System.out.println(event.getFloat() + " " + event.getInt() + " " + event.getString() + " " + event.getData());
					if(event.getString() == null) {
						break;
					}
					
					String[] slots = event.getString().split(";");
					for(String slot : slots) {
						if(skeleton.findSlot(slot) == null) {
							continue;
						}
						Box2dAttachment attachment = (Box2dAttachment) skeleton.findSlot(slot).getAttachment();
						if(attachment == null) {
							continue;
						}
						
						if(event.getInt() == 1) {
							attachment.getFixture().getFilterData().categoryBits = 0b0011;
							attachment.getFixture().getFilterData().maskBits = 0xFFFF;
							attachment.getFixture().setSensor(true);
							((SensorData) attachment.getFixture().getUserData()).setType(SensorData.WEAPON);
						} else {
							attachment.getFixture().getFilterData().categoryBits = 0b1000;
							attachment.getFixture().getFilterData().maskBits = 0b0110;
							attachment.getFixture().setSensor(false);
							((SensorData) attachment.getFixture().getUserData()).setType(SensorData.BODY);
						}
					}
					break;
				case "SFX":
					/*entity.getBody().applyLinearImpulse(new Vec2(entity.getRender().isFlipped() ? -1.5f : 1.5f, -6f),
							entity.getBody().getWorldCenter());
					entity.getBody().setGravityScale(1f);
					entity.getBody().setLinearDamping(1f);
					System.out.println("Starting y: " + entity.getBody().getPosition().y * Stage_new.SCALE_FACTOR);
					entity.setGroundZ(entity.getBody().getPosition().y * Stage_new.SCALE_FACTOR);*/
					break;
				default:
					//System.out.println("Unhandled event: " + event.getData());
				}
			}

			@Override
			public void complete(int trackIndex, int totalLoops) {
				switch(entity.getState()) {
				case DEFEND:
					if(animState.getCurrent(trackIndex).getAnimation().getName().matches(State.DEFEND.animation)) {
						animState.setAnimation(trackIndex, "Defending", true);
					}
					break;
				case ATTACK:
				case QUICKSTEP:
					//entity.getBody().setLinearDamping(15f);
				case LAND:
					entity.getBody().setLinearDamping(15f);
				case HIT:
					entity.setFixDirection(false);
					//entity.getBody().setLinearVelocity(new Vec2());
				case CHANGE_WEAPON:
					entity.fireEvent(new StateChangeEvent(State.IDLE));
					break;
				default:
					break;
				}
			}
		});
	}

	@Override
	public void draw() {
		for(int i = 0; i<skeleton.drawOrder.size(); i++) {
			RegionAttachment attachment = (RegionAttachment) skeleton.drawOrder.get(i).getAttachment();
			if(attachment != null) {
				setTransform(i);
				SpriteList.get(sprite + "/" + attachment.getName()).draw(transform);
			}
		}
	}

	@Override
	public void drawShadow() {
		ZBody zBody = entity.getZBody();
		float scale = 0.175f * (zBody.getGroundZ() != 0 ? (zBody.getGroundZ() - zBody.getZ()) / zBody.getGroundZ() : 1);
		
		for(int i = 0; i<skeleton.drawOrder.size(); i++) {
			if(skeleton.drawOrder.get(i).getAttachment() != null) {
				RegionAttachment region = (RegionAttachment) skeleton.drawOrder.get(i).getAttachment();

				setTransform(i);
				transform.setY(skeleton.getY() - ((skeleton.getY() - region.getWorldVertices()[Attachment.Y3]) * scale) + zBody.getZ());
				transform.setScaleY(scale);
				if(zBody.getGroundZ() != 0) {
					transform.setScaleX((zBody.getGroundZ() - zBody.getZ()) / zBody.getGroundZ());
				}
				transform.color = new Vector4f(0, 0, 0, 1f);
				SpriteList.get(sprite + "/" + skeleton.drawOrder.get(i).getAttachment().getName()).draw(transform);
			}
		}
	}

	@Override
	public void debugDraw() {
		for(Fixture f = entity.getBody().getFixtureList(); f != null; f = f.getNext()) {
			switch(f.getShape().m_type) {
			case CIRCLE:
				DrawUtils.setColor(new Vector3f(0f, 0f, 0.6f));
				DrawUtils.drawBox2DCircle(entity.getBody(), (CircleShape) f.m_shape);
				break;
			case EDGE:
				DrawUtils.setColor(new Vector3f(1f, 0f, 0f));
				DrawUtils.drawBox2DEdge(entity.getBody().getPosition(), (EdgeShape) f.m_shape);
				break;
			case POLYGON:
				DrawUtils.setColor(new Vector3f(0f, 0.8f, 0f));
				DrawUtils.drawBox2DPoly(entity.getBody(), (PolygonShape) f.m_shape);
				break;
			case CHAIN:
				break;
			}
		}
	}

	@Override
	public void animate(float speed) {
		animState.update(Theater.getDeltaSpeed(0.016f) * speed);
		animState.apply(skeleton);
		
		skeleton.setPosition(entity.getBody().getPosition().x * Stage_new.SCALE_FACTOR,
				entity.getBody().getPosition().y * Stage_new.SCALE_FACTOR);
		skeleton.updateWorldTransform();

		for(Slot slot : skeleton.getDrawOrder()) {
			if(slot.getAttachment() != null) {
				Box2dAttachment attachment = (Box2dAttachment) slot.getAttachment();
				Fixture f = attachment.getFixture();
				float[] attVerts = attachment.getWorldVertices();

				if(f.getShape() instanceof PolygonShape) {
					PolygonShape shape = (PolygonShape) f.getShape();
					for(int i = 0; i < shape.getVertexCount(); i++) {
						shape.getVertex(i).set((attVerts[Attachment.X2 * i] - skeleton.getX()) / Stage_new.SCALE_FACTOR,
								(attVerts[(Attachment.X2 * i) + 1] - skeleton.getY()) / Stage_new.SCALE_FACTOR);
					}
				}
			}
		}
	}
	
	@Override
	public String getAnimation() {
		return animState.getCurrent(0).getAnimation().getName();
	}
	
	@Override
	public boolean hasAnimation(String animation) {
		return animState.getData().getSkeletonData().findAnimation(animation) != null;
	}

	@Override
	public boolean isFlipped() {
		return skeleton.getFlipX();
	}

	@Override
	public void setFlipped(boolean flipped) {
		skeleton.setFlipX(flipped);
	}

	@Override
	public Transform getTransform() {
		return transform;
	}

	@Override
	public void setTransform(int index) {
		RegionAttachment region = (RegionAttachment) skeleton.drawOrder.get(index).getAttachment();
		region.updateWorldVertices(skeleton.drawOrder.get(index), false);
		Bone bone = skeleton.drawOrder.get(index).getBone();

		transform.setX(region.getWorldVertices()[Attachment.X3]);
		transform.setY(region.getWorldVertices()[Attachment.Y3]);
		transform.setRotation(-bone.getWorldRotation() - region.getRotation());
		transform.setFlipX(skeleton.getFlipX());
		transform.setScaleX(region.getScaleX());
		transform.setScaleY(region.getScaleY());
		transform.setColor(skeleton.drawOrder.get(index).getColor());
	}

	@Override
	public String getSprite() {
		return sprite;
	}

	public Skeleton getSkeleton() {
		return skeleton;
	}

	public AnimationState getAnimState() {
		return animState;
	}
	
	public void setAttachment(String slotName, String attachmentName) {
		getSkeleton().setAttachment(slotName, attachmentName);
		/*if(skeleton.findSlot(slotName).getAttachment() != null) {
			Box2dAttachment attachment = (Box2dAttachment) skeleton.findSlot(slotName).getAttachment();
			if(attachment.getFixture() != null) {
				entity.getBody().destroyFixture(attachment.getFixture());
			}
		}*/
		buildFixture(getSkeleton().findSlot(slotName), entity.getBody());
		
	}
	
	@Override
	public void fireEvent(EntityEvent e) {
		if(e instanceof StateChangeEvent) {
			processStateChangeEvent((StateChangeEvent) e);
		}
	}
	
	protected void processStateChangeEvent(StateChangeEvent e) {
		if(stateChangeListener != null) {
			stateChangeListener.stateChanged(e);
		}
	}

	private class AttachmentLoaderAdapter implements AttachmentLoader {
		@Override
		public RegionAttachment newRegionAttachment(Skin skin, String name, String path) {
			return null;
		}

		@Override
		public MeshAttachment newMeshAttachment(Skin skin, String name, String path) {
			return null;
		}

		@Override
		public SkinnedMeshAttachment newSkinnedMeshAttachment(Skin skin, String name, String path) {
			return null;
		}

		@Override
		public BoundingBoxAttachment newBoundingBoxAttachment(Skin skin, String name) {
			return null;
		}

		@Override
		public Box2dAttachment newBox2dAttachment(Skin skin, String name, String path) {
			return null;
		}
	}

}
