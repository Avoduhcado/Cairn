package core.ui;

import core.Camera;
import core.render.SpriteList;
import core.render.Transform;

public class Icon extends UIElement {

	private String image;
	private float x, y;
	
	private Transform transform = new Transform();
	
	public Icon(String icon) {
		this.setIcon(icon);
	}

	@Override
	public void draw() {
		transform.x = this.x;
		transform.y = this.y;
		transform.still = true;
		
		SpriteList.get(image).draw(transform);
	}
	
	public String getIcon() {
		return image;
	}

	public void setIcon(String icon) {
		this.image = icon;
	}

	public void setPosition(float x, float y) {
		setX(x);
		setY(y);
	}
	
	public float getX() {
		return x;
	}

	public void setX(float x) {
		if(Float.isNaN(x)) {
			this.x = Camera.get().getDisplayWidth(0.5f) -
					((SpriteList.get(image).getWidth() * Camera.ASPECT_RATIO) / 2f);
		} else {
			this.x = x;
		}
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		if(Float.isNaN(y)) {
			this.y = Camera.get().getDisplayHeight(0.5f) - 
					((SpriteList.get(image).getHeight() * Camera.ASPECT_RATIO) / 2f);
		} else {
			this.y = y;
		}
	}
	
}
