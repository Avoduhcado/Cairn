package core.entities_new;

import org.jbox2d.common.Vec2;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.Event;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonJson;
import com.esotericsoftware.spine.Skin;
import com.esotericsoftware.spine.AnimationState.AnimationStateAdapter;
import com.esotericsoftware.spine.attachments.AttachmentLoader;
import com.esotericsoftware.spine.attachments.BoundingBoxAttachment;
import com.esotericsoftware.spine.attachments.Box2dAttachment;
import com.esotericsoftware.spine.attachments.MeshAttachment;
import com.esotericsoftware.spine.attachments.RegionAttachment;
import com.esotericsoftware.spine.attachments.SkinnedMeshAttachment;

import core.Camera;
import core.Theater;
import core.render.DrawUtils;
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
		buildAnimationEvents();
		this.transform = new Transform();
	}
	
	private void buildSkeleton(String ref) {
		SkeletonJson json = new SkeletonJson(new AbstractLoader() {
			public RegionAttachment newRegionAttachment(Skin skin, String name, String path) {
				Box2dAttachment attachment = new Box2dAttachment(name);
				attachment.setRegion(SpriteList.get(sprite + "/" + name));
				return attachment;
			}
		});
		
		json.setScale(Camera.ASPECT_RATIO);
		
		System.out.println(ref);
		if(ref.contains("_")) {
			skeleton = new Skeleton(json.readSkeletonData(ref.split("_")[0], ref));
		} else {
			skeleton = new Skeleton(json.readSkeletonData(ref, ref));
		}
		skeleton.setFlipY(true);
		skeleton.updateWorldTransform();
		
		animStateData = new AnimationStateData(skeleton.getData());
		animStateData.setDefaultMix(0.2f);
		
		// TODO Add in custom animation data loading
		
		animState = new AnimationState(animStateData);
		setAnimation("Idle", true);
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
			if(skeleton.drawOrder.get(i).getAttachment() != null) {
				setTransform(i);
				SpriteList.get(sprite + "/" + skeleton.drawOrder.get(i).getAttachment().getName()).draw(transform);
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
		region.updateOffset();
		region.updateWorldVertices(skeleton.drawOrder.get(index), false);
		
		DrawUtils.setColor(new Vector3f(1, 0, 0));
		DrawUtils.drawVerts(region.getWorldVertices());
		
		transform.setX(region.getWorldVertices()[10]);
		transform.setY(region.getWorldVertices()[11]);
		if(skeleton.getFlipX()) {
			transform.setRotation(skeleton.drawOrder.get(index).getBone().getWorldRotation() + region.getRotation());
		} else {
			transform.setRotation(-skeleton.drawOrder.get(index).getBone().getWorldRotation() - region.getRotation());
		}
		transform.setFlipX(skeleton.getFlipX());
		transform.setScaleX(region.getScaleX());
		transform.setScaleY(region.getScaleY());
		transform.setColor(skeleton.drawOrder.get(index).getColor());
	}
	
	@Override
	public String getSprite() {
		return sprite;
	}
	
	public float getWidth() {
		return skeleton.getData().getWidth();
	}

	@Override
	public float getHeight() {
		return skeleton.getData().getHeight();
	}
	
	public Skeleton getSkeleton() {
		return skeleton;
	}

	@Override
	public void shadow() {
		for(int i = 0; i<skeleton.drawOrder.size(); i++) {
			if(skeleton.drawOrder.get(i).getAttachment() != null) {
				RegionAttachment region = (RegionAttachment) skeleton.drawOrder.get(i).getAttachment();
				region.updateWorldVertices(skeleton.drawOrder.get(i), false);
				
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
