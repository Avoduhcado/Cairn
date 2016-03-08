package core.entities_new.event.controllers;

import core.inventory.Weapon;

public class AttackEvent extends ControllerEvent {

	private final Weapon weapon;
	private String animation;
	
	public AttackEvent(Weapon weapon) {
		super(ControllerEvent.ATTACK);
		
		this.weapon = weapon;
		this.animation = weapon.getAnimation();
	}
	
	public Weapon getWeapon() {
		return weapon;
	}

	public String getAnimation() {
		return animation;
	}
	
	public void setAnimation(String animation) {
		this.animation = animation;
	}
	
}