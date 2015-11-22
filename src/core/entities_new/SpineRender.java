package core.entities_new;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.Event;
import com.esotericsoftware.spine.MathUtils;
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
import core.render.DrawUtils;
import core.render.Sprite;
import core.render.SpriteList;
import core.render.Transform;

public class SpineRender implements Render {

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

	public SpineRender(String ref, Entity entity) {
		this.entity = entity;

		this.sprite = ref;
		buildSkeleton(ref);
		buildBodies(entity.getContainer().getWorld());
		buildAnimationEvents();
		this.transform = new Transform();
	}

	private void buildSkeleton(String ref) {
		SkeletonJson json = new SkeletonJson(new AbstractLoader() {
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

		skeleton.setPosition(entity.getBody().getPosition().x * 30f, entity.getBody().getPosition().y * 30f);
		skeleton.setFlipY(true);
		skeleton.updateWorldTransform();

		animStateData = new AnimationStateData(skeleton.getData());
		animStateData.setDefaultMix(0.2f);

		// TODO Add in custom animation data loading

		animState = new AnimationState(animStateData);
		setAnimation("Idle", true);
	}

	private void buildBodies(World world) {
		for(Slot s : skeleton.drawOrder) {
			if(s.getAttachment() != null && s.getAttachment() instanceof Box2dAttachment) {
				Box2dAttachment attachment = (Box2dAttachment) s.getAttachment();
				if(attachment.getName().matches("HEAD")) {
					System.out.println(attachment.getWidth() + " " + attachment.getHeight() + " " + attachment.getX() + 
							" " + attachment.getY() + " " + attachment.getRotation());
				}
				attachment.updateWorldVertices(s, false);

				BodyDef bodyDef = new BodyDef();
				bodyDef.position.set(attachment.getWorldVertices()[Attachment.X3] / 30f,
						attachment.getWorldVertices()[Attachment.Y3] / 30f);
				bodyDef.type = BodyType.STATIC;

				PolygonShape bodyShape = new PolygonShape();
				Vec2[] verts = new Vec2[] {
						new Vec2(attachment.getWorldVertices()[Attachment.U2] / 30f, attachment.getWorldVertices()[Attachment.V2] / 30f),
						new Vec2(attachment.getWorldVertices()[Attachment.U3] / 30f, attachment.getWorldVertices()[Attachment.V3] / 30f),
						new Vec2(attachment.getWorldVertices()[Attachment.U4] / 30f, attachment.getWorldVertices()[Attachment.V4] / 30f),
						new Vec2(attachment.getWorldVertices()[Attachment.U1] / 30f, attachment.getWorldVertices()[Attachment.V1] / 30f),
				};
				bodyShape.set(verts, 4);

				FixtureDef boxFixture = new FixtureDef();
				boxFixture.density = 1f;
				boxFixture.shape = bodyShape;
				boxFixture.isSensor = true;
				//boxFixture.filter.categoryBits = 0;

				Body body = world.createBody(bodyDef);
				body.createFixture(boxFixture);
				body.setUserData(attachment);
				attachment.setBody(body);
			}
		}
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
					System.out.println(event.getFloat() + " " + event.getInt() + " " + event.getString() + " " + event.getData());
					switch(event.getInt()) {
					case 0:
						entity.setSensorData(null);
						break;
					case 1:

						break;
					default:
						break;
					}
					break;
				default:
					System.out.println("Unhandled event: " + event.getData());
				}
			}

			@Override
			public void complete(int trackIndex, int totalLoops) {
				switch(entity.getState()) {
				case ATTACK:
				case DEFEND:
					entity.setSubEntity(null);
				case QUICKSTEP:
				case LAND:
					entity.setFixDirection(false);
					entity.getBody().setLinearVelocity(new Vec2());
					entity.changeState(CharacterState.IDLE);
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

				if(Theater.get().debug) {
					DrawUtils.setColor(new Vector3f(0f, 0.8f, 0f));
					DrawUtils.drawBox2DPoly(((Box2dAttachment) attachment).getBody(),
							(PolygonShape) ((Box2dAttachment) attachment).getBody().m_fixtureList.m_shape);
				}
			}
		}
	}

	@Override
	public void debugDraw() {
		// TODO Auto-generated method stub

	}

	@Override
	public void animate(float speed, Vec2 position) {
		skeleton.setPosition(position.x * 30f, position.y * 30f);
		skeleton.updateWorldTransform();

		animState.update(Theater.getDeltaSpeed(0.016f) * speed);
		animState.apply(skeleton);

		for(Slot slot : skeleton.getSlots()) {
			if (!(slot.getAttachment() instanceof Box2dAttachment)) continue;
			Box2dAttachment attachment = (Box2dAttachment) slot.getAttachment();
			if (attachment.getBody() == null) continue;
			float x = attachment.getWorldVertices()[Attachment.X3] / 30f;
			float y = attachment.getWorldVertices()[Attachment.Y3] / 30f;
			float rotation = -slot.getBone().getWorldRotation() - attachment.getRotation();
			
			if(skeleton.getFlipX()) {
				// TODO
				//x -= attachment.getWidth() / 2f / 30f;
				x -= attachment.getWidth() / 2f / 30f;
				//y += attachment.getHeight() / 2f / 30f;
				x += (-MathUtils.cosDeg(180) + MathUtils.sinDeg(180) * 0.667f) / 30f;
				y -= (MathUtils.sinDeg(180) + MathUtils.cosDeg(180) * 0.667f) / 30f;
				x -= attachment.getWidth() / 2f / 30f;
				//y += attachment.getHeight() / 2f / 30f;
				rotation = -rotation;
			}
			attachment.getBody().setTransform(new Vec2(x, y), rotation);
		}
	}

	@Override
	public void setAnimation(String animation, boolean loop) {
		if(animState.getData().getSkeletonData().findAnimation(animation) != null) {
			animState.setAnimation(0, animation, loop);
		}
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

		transform.setX(region.getWorldVertices()[Attachment.X3]);
		transform.setY(region.getWorldVertices()[Attachment.Y3]);
		transform.setRotation(-skeleton.drawOrder.get(index).getBone().getWorldRotation() - region.getRotation());
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

	@Override
	public void shadow() {
		for(int i = 0; i<skeleton.drawOrder.size(); i++) {
			if(skeleton.drawOrder.get(i).getAttachment() != null) {
				RegionAttachment region = (RegionAttachment) skeleton.drawOrder.get(i).getAttachment();

				setTransform(i);
				transform.setY(skeleton.getY() - ((skeleton.getY() - region.getWorldVertices()[11]) * 0.175f));
				transform.setScaleY(0.175f);
				transform.color = new Vector4f(0, 0, 0, 1f);
				SpriteList.get(sprite + "/" + skeleton.drawOrder.get(i).getAttachment().getName()).draw(transform);
			}
		}
	}

	private class AbstractLoader implements AttachmentLoader {
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
