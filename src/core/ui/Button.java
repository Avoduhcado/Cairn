package core.ui;

import java.awt.Color;

import core.render.DrawUtils;
import core.render.SpriteIndex;
import core.ui.utils.Align;
import core.utilities.text.Text;

public class Button extends UIElement {
	
	private String text;
	private String icon;
	
	public Button(String text) {		
		this.text = text;
		setBounds(0, 0, text != null ? Text.getDefault().getWidth(text) : 1, text != null ? Text.getDefault().getHeight(text) : 1);
	}
	
	public Button(String text, float x, float y, float width, String image) {		
		this.text = text;
		setBounds(x, y, width == 0 ? Text.getDefault().getWidth(text) : width, Text.getDefault().getHeight(text));
		setFrame(image);
	}
	
	@Override
	public void draw() {
		super.draw();

		if(icon != null) {
			SpriteIndex.getSprite(icon).setStill(still);
			SpriteIndex.getSprite(icon).setFixedSize((float) bounds.getWidth(), (float) bounds.getHeight());
			SpriteIndex.getSprite(icon).draw((float) bounds.getX(), (float) bounds.getY());
		} else if(text != null) {
			Text.getDefault().setStill(still);
			Text.getDefault().setColor(isHovering() ? (enabled ? Color.white : Color.gray) : (enabled ? Color.gray : Color.darkGray));
			Text.getDefault().drawString(text, (float) bounds.getX(), (float) bounds.getY());
		} else {
			DrawUtils.setStill(still);
			DrawUtils.drawRect((float) bounds.getX(), (float) bounds.getY(), bounds);
		}
	}
	
	@Override
	public void draw(float x, float y) {
		super.draw(x, y);

		if(text != null) {
			Text.getDefault().setStill(still);
			Text.getDefault().setColor(isHovering() ? (enabled ? Color.white : Color.gray) : (enabled ? Color.gray : Color.darkGray));
			Text.getDefault().drawString(text, x, y);
		}
	}

	@Override
	public void setAlign(Align border) {
		switch(border) {
		case RIGHT:
			setBounds((float) bounds.getMaxX(), (float) bounds.getY(), (float) bounds.getWidth(), (float) bounds.getHeight());
			break;
		case LEFT:
			setBounds((float) (bounds.getX() - bounds.getWidth()), (float) bounds.getY(), (float) bounds.getWidth(), (float) bounds.getHeight());
			break;
		case CENTER:
			bounds.setFrameFromCenter(bounds.getX(), bounds.getCenterY(), 
					bounds.getX() - (bounds.getWidth() / 2f), bounds.getY());
			break;
		default:
			break;
		}
	}
	
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public String getIcon() {
		return icon;
	}
	
	public void setIcon(String icon) {
		this.icon = icon;
	}

}
