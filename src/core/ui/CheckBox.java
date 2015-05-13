package core.ui;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

import org.lwjgl.util.vector.Vector3f;

import core.Camera;
import core.render.DrawUtils;
import core.utilities.text.Text;

public class CheckBox extends UIElement {

	private boolean checked;
	private String text;
	
	public CheckBox(float x, float y, String image, String text) {
		super(x, y, image);
		
		this.text = text;
		box = new Rectangle2D.Double(this.x, this.y, Text.getDefault().getWidth(text), Text.getDefault().getHeight(text));
	}
	
	@Override
	public void update() {
		if(isClicked()) {
			checked = !checked;
		}
	}

	@Override
	public void draw() {
		super.draw();

		if(isHovering()) {
			DrawUtils.setColor(new Vector3f(1f, 1f, 1f));
			DrawUtils.drawRect(0, 0, box);
		}
		Text.getDefault().setStill(still);
		Text.getDefault().setColor(checked ? Color.white : Color.gray);
		Text.getDefault().drawString(text, x, y);
	}
	
	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	
	@Override
	public void setPosition(float x, float y) {
		if(Float.isNaN(x))
			this.x = Camera.get().getDisplayWidth(2f) - (Text.getDefault().getWidth(text) / 2f);
		else
			this.x = x;
		this.y = y;
		updateBox();
	}

}
