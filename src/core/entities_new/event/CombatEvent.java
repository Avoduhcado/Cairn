package core.entities_new.event;

import core.entities_new.Entity;
import core.inventory.Weapon;

public class CombatEvent extends EntityEvent {

	private Entity attacker;
	private Weapon weapon;
	private Entity target;
	
	public CombatEvent(Entity attacker, Weapon weapon, Entity target) {
		this.setAttacker(attacker);
		this.setWeapon(weapon);
		this.setTarget(target);
	}

	public Entity getAttacker() {
		return attacker;
	}

	public void setAttacker(Entity attacker) {
		this.attacker = attacker;
	}

	public Weapon getWeapon() {
		return weapon;
	}

	public void setWeapon(Weapon weapon) {
		this.weapon = weapon;
	}

	public Entity getTarget() {
		return target;
	}

	public void setTarget(Entity target) {
		this.target = target;
	}
	
}
