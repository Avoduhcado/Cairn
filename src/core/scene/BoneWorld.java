package core.scene;

import java.util.HashSet;
import java.util.Set;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.WorldManifold;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.contacts.Contact;

import core.entities_new.Entity;
import core.entities_new.EntityData;
import core.entities_new.event.CombatEvent;
import core.entities_new.utils.SensorData;
import core.setups.Stage_new;
import core.setups.WorldContainer;

public class BoneWorld implements ContactListener {
	
	private WorldContainer container;
	
	// TODO Enable multiple types of sensors
	private SensorData sensor = null;
	private Entity entity = null;
	
	public Set<Contact> weaponVsBone = new HashSet<Contact>();

	@Override
	public void beginContact(Contact contact) {
		System.out.println(contact.getFixtureA().getUserData() + 
				"(" + contact.getFixtureA().getFilterData().categoryBits + ", " + contact.getFixtureA().getFilterData().maskBits + ")" +
				" vs. " + contact.getFixtureB().getUserData() + 
				"(" + contact.getFixtureB().getFilterData().categoryBits + ", " + contact.getFixtureB().getFilterData().maskBits + ")");
		
		if(sortSensors(contact)) {
			switch(sensor.getType()) {
			case SensorData.GROUND:
				//System.out.println("Stepping on ground");
				entity.getZBody().stepOnGround(sensor.getEntity());
				break;
			case SensorData.BODY:
				//System.out.println("Bodies colliding!! " + sensor.getEntity().toString() + " " + entity.toString());
				break;
			case SensorData.WEAPON:
				System.out.println("Weapon colliding: " + sensor.getEntity() + ", " + entity);
				// TODO Hitting ground/wall?
				if(sensor.getEntity() != entity) {
					System.out.println("Hit boys!!! " + sensor.getEntity() + ", " + entity);
					// TODO Pass in proper weapons, maybe include slots that were hit?
					entity.fireEvent(new CombatEvent(sensor.getEntity(), null, entity));
				}
				break;
			default:
				break;
			}
		}
		/*if(contact.getFixtureA().isSensor() && contact.getFixtureB().isSensor()) {
			if(contactIsWeaponVsBone(contact)) {
			//if(contactIsBoneVsBone(contact)) {
				Vec2 aPosition = contact.getFixtureA().getBody().getPosition().mul(Stage_new.SCALE_FACTOR);
				Vec2 bPosition = contact.getFixtureB().getBody().getPosition().mul(Stage_new.SCALE_FACTOR);
				Vec2 collisionPoint = new Vec2(aPosition.x - ((aPosition.x - bPosition.x) / 2f),
						aPosition.y + ((aPosition.y - bPosition.y) / 2f));
				
				//System.out.println("DICKS");
				
				container.queueEntity(new EntityData("Bone Shards",
						collisionPoint.x + (int) (Math.random() * 15f),
						collisionPoint.y - (int) (Math.random() * 15f),
						container, contact));
				//System.out.println("Added " + contact);
				weaponVsBone.add(contact);
			} else if(contactIsWeaponVsGround(contact)) {
				//weaponVsGround.add(contact);
			}
		}*/
	}

	@Override
	public void endContact(Contact contact) {
		System.out.println("Ended: " + contact.getFixtureA().getUserData() + " vs. " + contact.getFixtureB().getUserData());
		/*if(contact.getFixtureA().isSensor() && contact.getFixtureB().isSensor()) {
			if(contactIsWeaponVsBone(contact)) {
			//if(contactIsBoneVsBone(contact)) {
				//System.out.println("Removed " + contact);
				weaponVsBone.remove(contact);
			} else if(contactIsWeaponVsGround(contact)) {
				//weaponVsGround.remove(contact);
			}
		}
		
		if(sortSensors(contact)) {
			switch(sensor.getType()) {
			case SensorData.GROUND:
				//System.out.println("Stepping off ground");
				entity.getZBody().stepOffGround(sensor.getEntity());
				break;
			case SensorData.BODY:
				break;
			case SensorData.WEAPON:
				break;
			default:
				break;
			}
		}*/
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
			sensor = (SensorData) fixtureA.getUserData();
			entity = (Entity) ((SensorData) fixtureB.getUserData()).getEntity();
		} else {
			sensor = (SensorData) fixtureB.getUserData();
			entity = (Entity) ((SensorData) fixtureA.getUserData()).getEntity();
		}
		
		return true;
	}
	
	private boolean fixtureIsBone(Fixture fixture) {
		return fixture.getBody().getUserData() != null &&
				((SensorData) fixture.getBody().getUserData()).getType() == SensorData.BODY;
	}
	
	private boolean fixtureIsGround(Fixture fixture) {
		return fixture.getBody().getUserData() != null &&
				((SensorData) fixture.getBody().getUserData()).getType() == SensorData.GROUND;
	}

	private boolean fixtureIsWeapon(Fixture fixture) {
		return fixture.getBody().getUserData() != null &&
				((SensorData) fixture.getBody().getUserData()).getType() == SensorData.WEAPON;
	}
	
	private boolean contactIsBoneVsBone(Contact contact) {
		Fixture fA = contact.getFixtureA();
		Fixture fB = contact.getFixtureB();
		if(fixtureIsBone(fA) && fixtureIsBone(fB)) {
			return true;
		}
		/*if(fixtureIsWeapon(fB) && fixtureIsBone(fA)) {
			return true;
		}*/
		return false;
	}
	
	private boolean contactIsWeaponVsBone(Contact contact) {
		Fixture fA = contact.getFixtureA();
		Fixture fB = contact.getFixtureB();
		if(fixtureIsWeapon(fA) && fixtureIsBone(fB)) {
			return true;
		}
		if(fixtureIsWeapon(fB) && fixtureIsBone(fA)) {
			return true;
		}
		return false;
	}
	
	private boolean contactIsWeaponVsGround(Contact contact) {
		Fixture fA = contact.getFixtureA();
		Fixture fB = contact.getFixtureB();
		if(fixtureIsWeapon(fA) && fixtureIsGround(fB)) {
			return true;
		}
		if(fixtureIsWeapon(fB) && fixtureIsGround(fA)) {
			return true;
		}
		return false;
	}
	
	public void setContainer(WorldContainer container) {
		this.container = container;
	}
	
}
