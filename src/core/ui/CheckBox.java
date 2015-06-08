package core.ui;

import java.awt.Color;
import org.lwjgl.util.vector.Vector3f;

import core.render.DrawUtils;
import core.utilities.text.Text;

public class CheckBox extends UIElement {

	private boolean checked;
	private String text;
	
	public CheckBox(float x, float y, String image, String text) {		
		this.text = text;
		setBounds(x, y, Text.getDefault().getWidth(text), Text.getDefault().getHeight(text));
	}
	
	@Override
	public void update() {
		super.update();
		if(isClicked()) {
			checked = !checked;
		}
	}

	@Override
	public void draw() {
		super.draw();

		if(isHovering()) {
			DrawUtils.setColor(new Vector3f(1f, 1f, 1f));
			DrawUtils.drawRect((float) bounds.getX(), (float) bounds.getY(), bounds);
		}
		Text.getDefault().setStill(still);
		Text.getDefault().setColor(checked ? Color.white : Color.gray);
		Text.getDefault().drawString(text, (float) bounds.getX(), (float) bounds.getY());
	}
	
	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

}
