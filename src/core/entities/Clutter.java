package core.entities;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector4f;

import core.Camera;
import core.entities.utils.BoxUserData;
import core.render.SpriteIndex;

public class Clutter {

	private Body body;
	
	public Clutter(Body body) {
		this.setBody(body);
	}

	public void update() {
		if(body.getUserData() instanceof BoxUserData) {
			if(((BoxUserData) body.getUserData()).isFalling()) {
				((BoxUserData) body.getUserData()).fall();
				System.out.println(((BoxUserData) body.getUserData()).getZ());
			} else {
				body.applyForce(body.m_world.getGravity().negate().mul(body.m_mass), body.getWorldCenter());
			}
		}
	}
	
	public void draw() {
		Vec2 bodyPosition = body.getPosition();

		if(body.getUserData() instanceof BoxUserData) {
			BoxUserData bud = (BoxUserData) body.getUserData();
			SpriteIndex.getSprite(bud.getSprite()).set2DScale(Camera.ASPECT_RATIO);
			SpriteIndex.getSprite(bud.getSprite()).setFlipped(bud.isFlipped());
			SpriteIndex.getSprite(bud.getSprite()).setColor(new Vector4f(1, 1, 1, 1));
			SpriteIndex.getSprite(bud.getSprite()).set2DRotation((float) -Math.toDegrees(body.getAngle()), 0f);
			SpriteIndex.getSprite(bud.getSprite()).draw(bodyPosition.x * 30, bodyPosition.y * 30);
		} else {
			CircleShape bodyShape = (CircleShape) body.m_fixtureList.getShape();

			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glPushMatrix();

			GL11.glColor3f(0f, 1f, 0f);
			GL11.glTranslatef((int) ((bodyPosition.x * 30) - Camera.get().frame.getX()),
					(int) ((bodyPosition.y * 30) - Camera.get().frame.getY()), 0);
			GL11.glRotated(Math.toDegrees(body.getAngle()), 0, 0, 1);
			
			GL11.glRectf(0, 0, bodyShape.m_radius, bodyShape.m_radius);

			GL11.glPopMatrix();
			GL11.glEnable(GL11.GL_TEXTURE_2D);
		}
	}
	
	public Body getBody() {
		return body;
	}

	public void setBody(Body body) {
		this.body = body;
	}
	
}
