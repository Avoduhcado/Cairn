package core.scene.hud;

import org.lwjgl.util.vector.Vector2f;

import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonJson;
import com.esotericsoftware.spine.Slot;
import com.esotericsoftware.spine.attachments.Region;

import core.Theater;
import core.render.SpriteIndex;
import core.render.textured.Sprite;

public class HUDMagicBar {
	
	private Sprite bar;
	private Sprite cap;
	
	private Skeleton magicBell;
	private AnimationState animState;
	private AnimationStateData animStateData;
	
	private float scale;
	private Vector2f casePosition;
	
	public HUDMagicBar(String ref, Vector2f casePosition, float scale) {
		bar = new Sprite("HUD/" + ref + " Center");
		cap = new Sprite("HUD/" + ref + " Tip");
		
		this.casePosition = casePosition;
		this.scale = scale;
		
		SkeletonJson json = new SkeletonJson(null);
		json.setScale(scale);
		magicBell = new Skeleton(json.readSkeletonData("HUD"));
		magicBell.updateWorldTransform();
		animStateData = new AnimationStateData(magicBell.getData());
		animStateData.setDefaultMix(0.2f);
		animState = new AnimationState(animStateData);
		animState.setAnimation(0, "Idle", true);
		
	}
	
	public void update() {
		animState.update(Theater.getDeltaSpeed(0.016f));
		animState.apply(magicBell);
		
		magicBell.setX(casePosition.x);
		magicBell.setY(casePosition.y + 6.5f + (magicBell.getData().getCenterY() * scale));
		magicBell.setFlipY(true);
		magicBell.updateWorldTransform();
	}
	
	public void drawBell(float width) {
		for(Slot s : magicBell.drawOrder) {
			if(s.getAttachment() != null) {
				Region region = (Region) s.getAttachment();
				region.updateWorldVertices(s);
				String sprite = "HUD/" + s.getAttachment().getName();

				SpriteIndex.getSprite(sprite).setStill(true);
				SpriteIndex.getSprite(sprite).set2DScale(scale);
				if(magicBell.getFlipX()) {
					SpriteIndex.getSprite(sprite).set2DRotation(s.getBone().getWorldRotation() + region.getRotation(), 0f);
				} else {
					SpriteIndex.getSprite(sprite).set2DRotation(-s.getBone().getWorldRotation() - region.getRotation(), 0f);
				}
				SpriteIndex.getSprite(sprite).setColor(s.getColor());
				SpriteIndex.getSprite(sprite).draw(((bar.getWidth() * width) * scale) + region.getWorldX(),
						region.getWorldY());
			}
		}
	}
	
	public void drawCase(float width) {
		bar.setStill(true);
		bar.set2DScale(scale);
		bar.setSubRegion(0f, 0f, width, 1f);
		bar.draw(casePosition.x, casePosition.y);
		
		cap.setStill(true);
		cap.set2DScale(scale);
		cap.draw(casePosition.x + (bar.getWidth() * width) * scale, casePosition.y);
	}
	
}
