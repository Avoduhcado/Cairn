package core.entities_new.event.controllers;

import org.jbox2d.common.Vec2;

public class JumpEvent extends ControllerEvent {
	
	private Vec2 jump;
	
	public JumpEvent(Vec2 jump) {
		super(JUMP);
		this.jump = jump;
	}

	public Vec2 getJump() {
		return jump;
	}
	
}