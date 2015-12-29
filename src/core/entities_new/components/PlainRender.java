package core.entities_new.components;

import java.io.Serializable;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.dynamics.Body;
import org.lwjgl.util.vector.Vector4f;

import core.entities_new.Entity;
import core.entities_new.event.EntityEvent;
import core.render.SpriteList;
import core.render.Transform;
import core.setups.Stage_new;

public class PlainRender implements Renderable, Serializable {

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
	public void animate(float speed) {
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
		//PolygonShape shape = ((PolygonShape) body.getFixtureList().getShape());
		
		transform.x = (body.getPosition().x) * Stage_new.SCALE_FACTOR;
		transform.y = (body.getPosition().y) * Stage_new.SCALE_FACTOR;
		transform.flipX = isFlipped();
		transform.scaleY = 1f;
		transform.scaleX = 1f;
		transform.centerRotate = true;
		transform.rotation = (float) Math.toDegrees(body.getAngle());
		transform.color = new Vector4f(1f, 1f, 1f, 1f);
	}

	@Override
	public String getSprite() {
		return sprite;
	}

	@Override
	public void drawShadow() {
		//Body body = entity.getBody();
		//PolygonShape shape = ((PolygonShape) body.getFixtureList().getShape());
		
		setTransform(0);
		transform.setY(entity.getBody().getPosition().y - ((entity.getBody().getPosition().y - transform.getY()) * 0.175f));
		transform.setScaleY(0.175f);
		transform.color = new Vector4f(0, 0, 0, 1f);
		SpriteList.get(sprite).draw(transform);
	}

	@Override
	public void fireEvent(EntityEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getAnimation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasAnimation(String animation) {
		// TODO Auto-generated method stub
		return false;
	}

}
