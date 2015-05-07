package core.ui;

import java.awt.geom.Rectangle2D;

import core.Camera;
import core.Input;
import core.render.textured.UIFrame;
import core.utilities.mouse.MouseInput;
import core.utilities.scripts.ScriptEvent;

public abstract class UIElement {
	
	// TODO Change box to a Dimension2D, change how still is handled? Also messes up click boxes but OH WELL

	protected float x, y;
	protected UIFrame frame;
	protected Rectangle2D box;
	protected boolean enabled = true;
	protected boolean still;
	
	protected float killTimer = 0f;
	protected boolean kill;
	
	protected ScriptEvent event;
	
	public UIElement(float x, float y, String image) {
		if(image != null) {
			this.frame = new UIFrame(image);
		}
		
		if(Float.isNaN(x))
			this.x = Camera.get().getDisplayWidth(0.5f);
		else
			this.x = x;
		this.y = y;
		
		box = new Rectangle2D.Double(x, y, 0, 0);
	}
	
	public void update() {
		// TODO Process events dawg
	}
	
	public void draw() {
		if(frame != null) {
			frame.draw(x, y, box);
		}
	}
	
	public void draw(float x, float y) {
		if(frame != null) {
			frame.draw(x, y, box);
		}
	}
	
	public void updateBox() {
		box = new Rectangle2D.Double(x, y, box.getWidth(), box.getHeight());
	}
	
	public Rectangle2D getBox() {
		return box;
	}
	
	public float getOpacity() {
		if(frame != null)
			return frame.getOpacity();
		return 0;
	}
	
	public void setOpacity(float opacity) {
		if(frame != null)
			this.frame.setOpacity(opacity);
	}
	
	/**
	 * @return true if input box is enabled.
	 */
	public boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * Enable or disable this input.
	 * @param enabled
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public void setStill(boolean still) {
		this.still = still;
		if(frame != null)
			frame.setStill(still);
	}
	
	public float getKillTimer() {
		return killTimer;
	}

	public void setKillTimer(float killTimer) {
		this.killTimer = killTimer;
	}

	public boolean isKill() {
		return kill;
	}
	
	public void setKill(boolean kill) {
		this.kill = kill;
	}
	
	public void setPosition(float x, float y) {
		this.x = x;
		this.y = y;
		updateBox();
	}
	
	public float getX() {
		return x;
	}
	
	public void setX(float x) {
		this.x = x;
	}
	
	public float getY() {
		return y;
	}
	
	public void setY(float y) {
		this.y = y;
	}

	public boolean isClicked() {
		return box.contains(MouseInput.getMouse()) && Input.mouseClicked();
	}
	
	public boolean isHovering() {
		return box.contains(MouseInput.getMouse());
	}
	
	public void setEvent(ScriptEvent event) {
		this.event = event;
	}
	
}
