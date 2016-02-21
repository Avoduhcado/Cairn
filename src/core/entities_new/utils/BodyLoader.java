package core.entities_new.utils;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.EdgeShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

import core.entities_new.Entity;
import core.setups.Stage;

public class BodyLoader {

	public static final int NULL_BODY = 0;
	public static final int GROUND = 1;
	public static final int PLAIN_ENTITY = 2;
	public static final int FLOATING_ENTITY = 3;
	public static final int WALL = 4;
	
	public static Body loadBody(BodyData bodyData, Entity entity, World world) {
		switch(bodyData.getBodyType()) {
		case NULL_BODY:
			return nullBody(entity, world, bodyData);
		case GROUND:
			return groundBody(entity, world, bodyData);
		case PLAIN_ENTITY:
			return plainEntity(entity, world, bodyData);
		case FLOATING_ENTITY:
			return floatingEntity(entity, world, bodyData);
		case WALL:
			return wallBody(entity, world, bodyData);
		default:
			System.out.println("Unspecified body type: " + bodyData.getBodyType());
			return null;
		}
	}
	
	private static Body nullBody(Entity entity, World world, BodyData bodyData) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.position.set(bodyData.getX() / Stage.SCALE_FACTOR, bodyData.getY() / Stage.SCALE_FACTOR);
		bodyDef.type = BodyType.STATIC;

		CircleShape bodyShape = new CircleShape();
		bodyShape.m_radius = bodyData.getWidth() / Stage.SCALE_FACTOR / 2f;

		FixtureDef boxFixture = new FixtureDef();
		boxFixture.density = 0f;
		boxFixture.shape = bodyShape;
		boxFixture.filter.categoryBits = 0;
		boxFixture.filter.maskBits = 0;
		boxFixture.isSensor = true;
		boxFixture.userData = new SensorData(entity, entity.getName(), SensorData.IGNORE);

		Body body = world.createBody(bodyDef);
		body.createFixture(boxFixture);
		body.setUserData(entity);
		
		return body;
	}

	private static Body groundBody(Entity entity, World world, BodyData bodyData) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.position.set(bodyData.getX() / Stage.SCALE_FACTOR, bodyData.getY() / Stage.SCALE_FACTOR);
		bodyDef.type = BodyType.STATIC;

		PolygonShape bodyShape = new PolygonShape();
		bodyShape.setAsBox(bodyData.getWidth() / Stage.SCALE_FACTOR, bodyData.getHeight() / Stage.SCALE_FACTOR);

		FixtureDef boxFixture = new FixtureDef();
		boxFixture.density = 1f;
		boxFixture.shape = bodyShape;
		boxFixture.isSensor = true;
		boxFixture.userData = new SensorData(entity, entity.getName(), SensorData.GROUND);

		Body body = world.createBody(bodyDef);
		body.createFixture(boxFixture);
		body.setUserData(entity);
		return body;
	}
	
	private static Body wallBody(Entity entity, World world, BodyData bodyData) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.position.set(bodyData.getX() / Stage.SCALE_FACTOR, bodyData.getY() / Stage.SCALE_FACTOR);
		bodyDef.type = BodyType.STATIC;

		EdgeShape bodyShape = new EdgeShape();
		bodyShape.set(new Vec2(0, 0), new Vec2(bodyData.getWidth() / Stage.SCALE_FACTOR, bodyData.getHeight() / Stage.SCALE_FACTOR));

		FixtureDef boxFixture = new FixtureDef();
		boxFixture.density = 1f;
		boxFixture.shape = bodyShape;
		boxFixture.userData = new SensorData(entity, entity.getName(), SensorData.WALL);

		Body body = world.createBody(bodyDef);
		body.createFixture(boxFixture);
		body.setUserData(entity);
		
		return body;
	}

	private static Body plainEntity(Entity entity, World world, BodyData bodyData) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.position.set(bodyData.getX() / Stage.SCALE_FACTOR, bodyData.getY() / Stage.SCALE_FACTOR);
		bodyDef.type = BodyType.DYNAMIC;

		CircleShape bodyShape = new CircleShape();
		bodyShape.m_radius = bodyData.getWidth() / Stage.SCALE_FACTOR / 2f;

		FixtureDef boxFixture = new FixtureDef();
		boxFixture.density = 1f;
		boxFixture.filter.categoryBits = 0b0011;
		boxFixture.filter.maskBits = 0b0111;
		boxFixture.shape = bodyShape;
		boxFixture.userData = new SensorData(entity, "Base", SensorData.CHARACTER);
		
		Body body = world.createBody(bodyDef);
		body.createFixture(boxFixture);
		body.setFixedRotation(true);
		body.setLinearDamping(15f);
		body.setGravityScale(0f);
		body.setUserData(entity);
		body.setSleepingAllowed(false);
		
		return body;
	}

	private static Body floatingEntity(Entity entity, World world, BodyData bodyData) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.position.set(bodyData.getX() / Stage.SCALE_FACTOR, bodyData.getY() / Stage.SCALE_FACTOR);
		bodyDef.type = BodyType.DYNAMIC;

		CircleShape bodyShape = new CircleShape();
		bodyShape.m_radius = bodyData.getWidth() / Stage.SCALE_FACTOR / 2f;

		FixtureDef boxFixture = new FixtureDef();
		boxFixture.density = 1f;
		boxFixture.filter.categoryBits = 0;
		boxFixture.filter.maskBits = 0;
		boxFixture.shape = bodyShape;
		boxFixture.userData = new SensorData(entity, "Base", SensorData.CHARACTER);
		
		Body body = world.createBody(bodyDef);
		body.createFixture(boxFixture);
		body.setFixedRotation(true);
		body.setLinearDamping(15f);
		body.setGravityScale(0f);
		body.setUserData(entity);
		body.setSleepingAllowed(false);
		
		return body;
	}

}
