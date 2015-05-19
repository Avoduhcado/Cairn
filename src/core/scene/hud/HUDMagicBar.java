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
import core.utilities.MathFunctions;

enum BellState {
	CAST, REGEN, REGEN_END, IDLE;
}

public class HUDMagicBar {
	
	private Sprite bar;
	private Sprite cap;
	
	private Skeleton magicBell;
	private AnimationState animState;
	private AnimationStateData animStateData;
	private BellState state;
	
	private float scale;
	private Vector2f casePosition;
	private float tweenTimer;
	private float tweenPosition = 1f;
	private float lastBellPosition = 1f;
		
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
	
	public void update(float width) {
		animState.update(Theater.getDeltaSpeed(0.016f));
		animState.apply(magicBell);
		
		if(width < lastBellPosition) {
			setState(BellState.CAST);
			tweenTimer = MathFunctions.clamp(tweenTimer + Theater.getDeltaSpeed(0.025f), 0, 1);
			tweenPosition = MathFunctions.easeOut(tweenTimer, lastBellPosition, width - lastBellPosition, 1f);
			if(tweenPosition <= width) {
				tweenPosition = width;
				lastBellPosition = width;
				tweenTimer = 0;
			}
		} else if(width > lastBellPosition) {
			setState(BellState.REGEN);
			tweenTimer = MathFunctions.clamp(tweenTimer + Theater.getDeltaSpeed(0.025f), 0, 1);
			tweenPosition = MathFunctions.easeOut(tweenTimer, lastBellPosition, width - lastBellPosition, 1f);
			if(tweenPosition >= width) {
				tweenPosition = width;
				lastBellPosition = width;
				tweenTimer = 0;
				setState(BellState.REGEN_END);
			}
		} else {
			if(state != BellState.REGEN_END || (state == BellState.REGEN_END && animState.getCurrent(0).isComplete())) {
				setState(BellState.IDLE);
			}
		}
		
		magicBell.setX(((bar.getWidth() * tweenPosition) * scale) + casePosition.x);
		magicBell.setY(casePosition.y + 6.5f + (magicBell.getData().getCenterY() * scale));
		magicBell.setFlipY(true);
		magicBell.updateWorldTransform();
	}
	
	public void drawBell() {
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
				SpriteIndex.getSprite(sprite).draw(region.getWorldX(),
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
	
	public BellState getState() {
		return state;
	}
	
	private void setState(BellState state) {
		if(this.state != state) {
			switch(state) {
			case CAST:
				animState.setAnimation(0, "Cast", true);
				break;
			case REGEN:
				animState.setAnimation(0, "Regen", true);
				break;
			case REGEN_END:
				animState.setAnimation(0, "RegenEnd", false);
				break;
			case IDLE:
				animState.setAnimation(0, "Idle", true);
				break;
			}
		}
	}
	
}
