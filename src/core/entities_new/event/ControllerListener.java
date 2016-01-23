package core.entities_new.event;

import core.entities_new.components.Controllable;

public interface ControllerListener extends Controllable {

	public void move(ControllerEvent e);
	public void dodge(ControllerEvent e);
	
	public void attack(ControllerEvent e);
	public void defend(ControllerEvent e);
	
	public void collapse(ControllerEvent e);
	public void jump(ControllerEvent e);
	public void changeWeapon(ControllerEvent e);
	
}
