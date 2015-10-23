package core.scene;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.contacts.Contact;

import core.entities_new.Entity;

public class BoneWorld implements ContactListener {
	
	// TODO Enable multiple types of sensors

	@Override
	public void beginContact(Contact contact) {
		if(contact.getFixtureA().isSensor() && !contact.getFixtureB().isSensor()) {
			((Entity) contact.getFixtureB().getBody().getUserData()).stepOnGround((Entity) contact.getFixtureA().getBody().getUserData());
		} else if(!contact.getFixtureA().isSensor() && contact.getFixtureB().isSensor()) {
			((Entity) contact.getFixtureA().getBody().getUserData()).stepOnGround((Entity) contact.getFixtureB().getBody().getUserData());
		}
	}

	@Override
	public void endContact(Contact contact) {
		if(contact.getFixtureA().isSensor() && !contact.getFixtureB().isSensor()) {
			((Entity) contact.getFixtureB().getBody().getUserData()).stepOffGround((Entity) contact.getFixtureA().getBody().getUserData());
		} else if(!contact.getFixtureA().isSensor() && contact.getFixtureB().isSensor()) {
			((Entity) contact.getFixtureA().getBody().getUserData()).stepOffGround((Entity) contact.getFixtureB().getBody().getUserData());
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

}
