package core.scene;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.contacts.Contact;

import core.entities_new.Entity;
import core.entities_new.SensorData;
import core.entities_new.SensorType;

public class BoneWorld implements ContactListener {
	
	// TODO Enable multiple types of sensors
	private SensorData sensor = null;
	private Entity entity = null;

	@Override
	public void beginContact(Contact contact) {
		if(sortSensors(contact)) {
			switch(sensor.getType()) {
			case GROUND:
				//System.out.println("Stepping on ground");
				entity.stepOnGround(sensor.getEntity());
				break;
			case BODY:
				//System.out.println("Bodies colliding!! " + sensor.getEntity().toString() + " " + entity.toString());
				break;
			case WEAPON:
				//System.out.println("Weapon colliding: " + sensor.getEntity() + " " + entity);
				// TODO Hitting ground/wall?
				if(sensor.getEntity() != entity && sensor.getEntity() != entity.getSubEntity()) {
					System.out.println("Hit boys " + sensor.getEntity() + " " + entity);
					entity.hit(sensor.getEntity());
				}
				break;
			default:
				break;
			}
		}
	}

	@Override
	public void endContact(Contact contact) {
		if(sortSensors(contact)) {
			switch(sensor.getType()) {
			case GROUND:
				//System.out.println("Stepping off ground");
				entity.stepOffGround(sensor.getEntity());
				break;
			case BODY:
				break;
			case WEAPON:
				break;
			default:
				break;
			}
		}
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {

	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {

	}

	private boolean sortSensors(Contact contact) {
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();

		boolean sensorA = fixtureA.isSensor();
		boolean sensorB = fixtureB.isSensor();
		if(!(sensorA ^ sensorB)) {
			return false;
		}
				
		if(sensorA) {
			sensor = (SensorData) fixtureA.getBody().getUserData();
			entity = (Entity) fixtureB.getBody().getUserData();
			if(sensor.getType() == SensorType.WEAPON) {
				System.out.println("Weapon sensoring " + fixtureA.getUserData() + ", " + entity);
			}
		} else {
			sensor = (SensorData) fixtureB.getBody().getUserData();
			entity = (Entity) fixtureA.getBody().getUserData();
		}
		
		return true;
	}
	
}
