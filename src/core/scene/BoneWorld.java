package core.scene;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.contacts.Contact;

import core.entities.utils.BoxUserData;

public class BoneWorld implements ContactListener {

	@Override
	public void beginContact(Contact contact) {
		// TODO Auto-generated method stub
		if(contact.getFixtureA().getBody().getUserData() instanceof BoxUserData) {
			((BoxUserData) contact.getFixtureA().getBody().getUserData()).setZ(1.5f);
			//System.out.println(contact.getFixtureA().getBody().getUserData().toString());
		}
	}

	@Override
	public void endContact(Contact contact) {
		// TODO Auto-generated method stub

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
