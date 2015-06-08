package core.ui;

import java.awt.geom.Rectangle2D;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

import core.render.textured.Sprite;
import core.utilities.MathFunctions;
import core.utilities.mouse.MouseInput;

public class Slider extends UIElement {

	private float value;
	private Rectangle2D valueBox;
	private boolean held;
	private boolean valueChanged;
	
	private Sprite background;
	private Sprite valueIcon;
	
	public Slider(float x, float y, float scale, float value, String background, String valueIcon) {		
		this.value = value;
		// TODO Remove fixed values
		setBounds(x, y, 100f * scale, 15f * scale);
		valueBox = new Rectangle2D.Double(bounds.getX() + (bounds.getWidth() * value) - 5f, bounds.getY(), 10f * scale, 15f * scale);
		
		this.background = new Sprite(background);
		this.background.setScale(new Vector3f(scale, scale, 1f));
		this.valueIcon = new Sprite(valueIcon);
		this.valueIcon.setScale(new Vector3f(scale, scale, 1f));
	}
	
	public void update() {
		super.update();
		
		if(isClicked() || held) {
			held = true;
			setValue(MathFunctions.clamp((float) ((MouseInput.getMouseX() - bounds.getX()) / (bounds.getMaxX() - bounds.getX())), 0f, 1f));

			valueBox.setFrame(bounds.getX() + (bounds.getWidth() * value) - (valueBox.getWidth() / 2f),
					valueBox.getY(), valueBox.getWidth(), valueBox.getHeight());
		}
		
		if(!Mouse.isButtonDown(0)) {
			held = false;
			valueChanged = false;
		}
	}
	
	@Override
	public void draw() {
		background.draw((float) bounds.getX(), (float) bounds.getY());
		valueIcon.draw((float) valueBox.getX(), (float) valueBox.getY());
	}

	@Override
	public boolean isClicked() {
		return bounds.contains(MouseInput.getMouse()) && Mouse.isButtonDown(0);
	}
	
	public float getValue() {
		return value;
	}
	
	public void setValue(float value) {
		if(this.value != value) {
			this.value = value;
			valueChanged = true;
		} else {
			valueChanged = false;
		}
	}
	
	@Override
	public boolean isValueChanged() {
		return valueChanged;
	}
	
	@Override
	public void setStill(boolean still) {
		super.setStill(still);
		background.setStill(still);
		valueIcon.setStill(still);
	}
	
}
