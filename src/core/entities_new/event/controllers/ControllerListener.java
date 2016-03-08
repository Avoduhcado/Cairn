package core.entities_new.event.controllers;

import core.entities_new.components.controllers.Controllable;

public interface ControllerListener extends Controllable {

	public void move(MoveEvent e);
	public void dodge(ControllerEvent e);
	
	public void attack(AttackEvent e);
	public void defend(DefendEvent e);
	
	public void collapse(ControllerEvent e);
	public void jump(JumpEvent e);
	public void changeWeapon(ControllerEvent e);
	
}
