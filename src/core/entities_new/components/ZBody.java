package core.entities_new.components;

import java.util.ArrayList;

import org.jbox2d.collision.RayCastInput;
import org.jbox2d.collision.RayCastOutput;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;
import org.lwjgl.util.vector.Vector2f;

import core.entities_new.Entity;
import core.entities_new.State;
import core.entities_new.event.StateChangeEvent;
import core.entities_new.utils.SensorData;
import core.setups.Stage_new;

public class ZBody implements Geometric {

	private Entity entity;
	private Body body;
	private float z, groundZ;
	
	private ArrayList<Entity> ground = new ArrayList<Entity>();

	public ZBody(Body body, Entity entity) {
		this.body = body;
		this.entity = entity;
	}
	
	public void stepOnGround(Entity ground) {
		this.ground.add(ground);
		if(this.ground.size() == 1) {
			entity.fireEvent(new StateChangeEvent(State.LAND));
		} else {
			entity.fireEvent(new StateChangeEvent(State.IDLE));
		}
		getBody().setGravityScale(0f);
		getBody().setLinearDamping(15f);
	}
	
	public void stepOffGround(Entity ground) {
		this.ground.remove(ground);
		if(this.ground.isEmpty()) {
			// TODO Interrupt() delete any sub entities/general state cleanup
			fall();
		}
	}
	
	public static boolean isGround(Entity ground) {
		Fixture fixture = ground.getBody().getFixtureList();
		
		if(fixture.isSensor() && fixture.getBody().getUserData() instanceof SensorData) {
			SensorData data = (SensorData) fixture.getBody().getUserData();
			
			return (data.getType() == SensorData.GROUND);
		}
		
		return false;
	}
	
	private void fall() {
		entity.fireEvent(new StateChangeEvent(State.FALLING));
		getBody().setGravityScale(1f);
		getBody().setLinearDamping(1f);
		getBody().getFixtureList().getFilterData().categoryBits = 0;
		
		setGroundZ(searchForGround());
	}
	
	private float searchForGround() {
		final float fractionStep = 50;
		float closestFraction = 10;
		float closestGroundY = 0;
		
		for(Entity e : entity.getContainer().getEntities()) {
			if(ZBody.isGround(e)) {
				Fixture fixture = e.getBody().getFixtureList();
				
				RayCastOutput output = new RayCastOutput();
				RayCastInput input = new RayCastInput();
				input.p1.set(this.getBody().getPosition());
				input.p2.set(this.getBody().getPosition().x,
						this.getBody().getPosition().y + (fractionStep / Stage_new.SCALE_FACTOR));
				input.maxFraction = closestFraction;

				if(!fixture.raycast(output, input, 0)) {
					continue;
				}
				if(output.fraction < closestFraction) {
					closestFraction = output.fraction;
					closestGroundY = fixture.getBody().getPosition().y * Stage_new.SCALE_FACTOR;
					System.out.println(output.fraction + " " + output.normal);
				}
			}
		}
		
		return closestGroundY;
	}

	@Override
	public Vector2f getPosition() {
		return new Vector2f(body.getPosition().x * Stage_new.SCALE_FACTOR, body.getPosition().y * Stage_new.SCALE_FACTOR);
	}

	/**
	 * Not recommended usage for ZBody
	 */
	@Override
	public void setPosition(Vector2f position) {
		body.setTransform(new Vec2(position.x, position.y), 0);
	}

	@Override
	public float getX() {
		return body.getPosition().x * Stage_new.SCALE_FACTOR;
	}

	@Override
	public float getY() {
		return body.getPosition().y * Stage_new.SCALE_FACTOR;
	}
	
	public Body getBody() {
		return body;
	}

	public void setBody(Body body) {
		this.body = body;
	}

	public float getZ() {
		return z;
	}

	public void setZ(float z) {
		this.z = z;
	}

	public float getGroundZ() {
		return groundZ;
	}

	public void setGroundZ(float groundZ) {
		this.groundZ = groundZ;
	}
	
	public float getScreenY() {
		return (body.getPosition().y * Stage_new.SCALE_FACTOR) + z;
	}

}
