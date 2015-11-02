package core.entities_new;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

import core.render.SpriteList;
import core.render.Transform;

public class PlainRender implements Render {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String sprite;
	private Entity entity;
	
	private Transform transform;
	
	public PlainRender(String name, Entity entity) {
		this.sprite = name;
		this.entity = entity;
		
		this.transform = new Transform();
	}

	@Override
	public void draw() {
		setTransform(0);
		SpriteList.get(sprite).draw(transform);
	}

	@Override
	public void debugDraw() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void animate(float speed, Vec2 position) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setAnimation(String animation, boolean loop) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isFlipped() {
		return transform.flipX;
	}

	@Override
	public void setFlipped(boolean flipped) {
		transform.flipX = flipped;
	}

	@Override
	public Transform getTransform() {
		return transform;
	}

	@Override
	public void setTransform(int index) {
		Body body = entity.getBody();
		PolygonShape shape = ((PolygonShape) body.getFixtureList().getShape());
		
		transform.x = (body.getPosition().x + shape.getVertex(0).x) * 30f;
		transform.y = (body.getPosition().y + shape.getVertex(0).y) * 30f;
		transform.centerRotate = true;
		transform.rotation = (float) body.getAngle();
	}

	@Override
	public String getSprite() {
		return sprite;
	}

	@Override
	public float getWidth() {
		return SpriteList.get(sprite).getWidth();
	}

	@Override
	public float getHeight() {
		// TODO Auto-generated method stub
		return 0;
	}

}
