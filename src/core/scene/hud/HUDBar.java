package core.scene.hud;

import org.lwjgl.util.vector.Vector2f;

import core.render.textured.Sprite;

public class HUDBar {
	
	private Sprite bar;
	private Sprite cap;
	private Sprite under;
	private Sprite over;
	
	private float caseWidth;
	
	private float scale;
	private Vector2f barPosition;
	private Vector2f casePosition;
	
	public HUDBar(String ref, Vector2f barPosition, Vector2f casePosition, float scale) {
		bar = new Sprite("HUD/" + ref + " Center");
		cap = new Sprite("HUD/" + ref + " Tip");
		under = new Sprite("HUD/" + ref + " Bar Black");
		over = new Sprite("HUD/" + ref + " Bar White");
		
		this.barPosition = barPosition;
		this.casePosition = casePosition;
		this.scale = scale;
		
		this.caseWidth = bar.getWidth();
	}
	
	public void update(float width) {
		caseWidth = bar.getWidth() * width;
	}
	
	public void drawBar(float width) {
		under.setStill(true);
		under.set2DScale(scale);
		//under.setSubRegion(0, 0, caseWidth / (bar.getWidth() - (casePosition.x - barPosition.x) - (cap.getWidth() / 2f)), 1f);
		under.draw(barPosition.x, barPosition.y);
		
		over.setStill(true);
		over.set2DScale(scale);
		//over.setSubRegion(0, 0, (caseWidth / (bar.getWidth() - (casePosition.x - barPosition.x)) * width), 1f);
		over.setSubRegion(0f, 0f, width, 1f);
		over.draw(barPosition.x, barPosition.y);
	}
	
	public void drawCase() {
		bar.setStill(true);
		bar.set2DScale(scale);
		bar.setSubRegion(0f, 0f, caseWidth / bar.getWidth(), 1f);
		bar.draw(casePosition.x, casePosition.y);
		
		cap.setStill(true);
		cap.set2DScale(scale);
		cap.draw(casePosition.x + (caseWidth * scale), casePosition.y);
	}
	
	public float getCaseWidth() {
		return caseWidth;
	}
	
}
