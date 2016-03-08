package core.entities_new.event.controllers;

import org.jbox2d.common.Vec2;


public class MoveEvent extends ControllerEvent {

	private Vec2 movement;
	
	public MoveEvent(Vec2 movement) {
		super(ControllerEvent.MOVE);
		
		this.setMovement(movement);
	}

	public Vec2 getMovement() {
		return movement;
	}

	public void setMovement(Vec2 movement) {
		this.movement = movement;
	}
	
	
}