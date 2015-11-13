package core.scene;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.contacts.Contact;

import core.entities_new.Entity;
import core.entities_new.SensorData;

public class BoneWorld implements ContactListener {
	
	// TODO Enable multiple types of sensors
	private Entity sensor = null;
	private Entity entity = null;

	@Override
	public void beginContact(Contact contact) {
		if(sortSensors(contact)) {
			SensorData data;
			if((data = sensor.getSensorData()) != null) {
				switch(data.getType()) {
				case GROUND:
					entity.stepOnGround(sensor);
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
	}

	@Override
	public void endContact(Contact contact) {
		if(sortSensors(contact)) {
			SensorData data;
			if((data = sensor.getSensorData()) != null) {
				switch(data.getType()) {
				case GROUND:
					entity.stepOnGround(sensor);
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
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		// TODO Auto-generated method stub

	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub

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
			sensor = (Entity) contact.getFixtureA().getBody().getUserData();
			entity = (Entity) contact.getFixtureB().getBody().getUserData();
		} else {
			sensor = (Entity) contact.getFixtureB().getBody().getUserData();
			entity = (Entity) contact.getFixtureA().getBody().getUserData();
		}
		
		return true;
	}
	
}
