package core.ui;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

import core.Camera;
import core.utilities.text.Text;

public class Button extends UIElement {
	
	private String text;
	private boolean hover;
	private boolean clicked;
	private boolean held;
	
	public Button(String text) {
		super(0, 0, null);
		
		this.text = text;
		this.box = new Rectangle2D.Double(this.x, y, Text.getDefault().getWidth(text), Text.getDefault().getHeight(text));
	}
	
	public Button(String text, float x, float y, float width, String image) {
		super(x, y, image);
		
		this.text = text;
		if(Float.isNaN(x))
			this.x = Camera.get().getDisplayWidth(0.5f) - (Text.getDefault().getWidth(text) / 2f);
		else
			this.x = x;
		
		if(text != null) {
			this.box = new Rectangle2D.Double(this.x, y, width == 0 ? Text.getDefault().getWidth(text) : width,
					Text.getDefault().getHeight(text));
		} else
			this.box = new Rectangle2D.Double(this.x, y, width == 0 ? frame.getWidth() : width, frame.getHeight());
	}

	@Override
	public void update() {
		if(isHovering())
			hover = true;
		else
			hover = false;
		
		if(clicked)
			clicked = false;
		if(isClicked()) {
			if(!held) {
				held = true;
			}
		} else {
			if(held) {
				held = false;
				clicked = true;
			}
		}
	}
	
	@Override
	public void draw() {
		super.draw();

		if(text != null) {
			Text.getDefault().setStill(still);
			Text.getDefault().setColor(hover ? (enabled ? Color.white : Color.gray) : (enabled ? Color.gray : Color.darkGray));
			Text.getDefault().drawString(text, x, y);
		}
	}
	
	@Override
	public void draw(float x, float y) {
		super.draw(x, y);

		if(text != null) {
			Text.getDefault().setStill(still);
			Text.getDefault().setColor(hover ? (enabled ? Color.white : Color.gray) : (enabled ? Color.gray : Color.darkGray));
			Text.getDefault().drawString(text, x, y);
		}
	}
	
	public void highlight() {
		this.hover = true;
	}
	
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	@Override
	public void setPosition(float x, float y) {
		if(Float.isNaN(x)) {
			this.x = Camera.get().getDisplayWidth(0.5f) - (Text.getDefault().getWidth(text) / 2f) - 15f;
		} else {
			this.x = x;
		}
		this.y = y;
		updateBox();
	}

}
