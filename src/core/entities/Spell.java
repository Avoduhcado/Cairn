package core.entities;

import org.lwjgl.util.vector.Vector2f;

import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonJson;
import com.esotericsoftware.spine.Slot;
import com.esotericsoftware.spine.attachments.Region;

import core.Theater;
import core.render.SpriteIndex;
@Deprecated
public class Spell {

	private String name;
	private Vector2f position;
	private float scale;
	
	private Skeleton spell;
	private AnimationState animState;
	private AnimationStateData animStateData;
	
	private boolean finished;
	
	public Spell(String name, Vector2f position, float scale) {
		this.name = name;
		this.position = position;
		this.scale = scale;
		
		SkeletonJson json = new SkeletonJson(null);
		json.setScale(scale);
		spell = new Skeleton(json.readSkeletonData("actors/" + name, name));
		spell.updateWorldTransform();
		animStateData = new AnimationStateData(spell.getData());
		animStateData.setDefaultMix(0.1f);
		animState = new AnimationState(animStateData);
		animState.setAnimation(0, "cast", true);
	}
	
	public void update() {
		animState.update(Theater.getDeltaSpeed(0.016f));
		animState.apply(spell);
		
		if(animState.getCurrent(0).isComplete()) {
			finished = true;
		}
		
		spell.setX(position.x);
		spell.setY(position.y + (spell.getData().getCenterY() * scale));
		spell.setFlipY(true);
		spell.updateWorldTransform();
	}
	
	public void draw() {
		for(Slot s : spell.drawOrder) {
			if(s.getAttachment() != null) {
				Region region = (Region) s.getAttachment();
				region.updateWorldVertices(s);
				//String sprite = name + "/" + s.getAttachment().getName();
				String sprite = "actors/" + name + "/" + s.getAttachment().getName();

				SpriteIndex.getSprite(sprite).set2DScale(scale);
				if(spell.getFlipX()) {
					SpriteIndex.getSprite(sprite).set2DRotation(s.getBone().getWorldRotation() + region.getRotation(), 0f);
				} else {
					SpriteIndex.getSprite(sprite).set2DRotation(-s.getBone().getWorldRotation() - region.getRotation(), 0f);
				}
				//SpriteIndex.getSprite(sprite).setColor(s.getColor());
				SpriteIndex.getSprite(sprite).draw(region.getWorldX(), region.getWorldY());
			}
		}
	}
	
	public boolean isFinished() {
		return finished;
	}
	
}
