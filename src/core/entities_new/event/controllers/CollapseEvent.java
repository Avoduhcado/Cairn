package core.entities_new.event.controllers;

import org.jbox2d.common.Vec2;

public class CollapseEvent extends ControllerEvent {
	
	private Vec2 collapseVector;
	
	public CollapseEvent(Vec2 collapseVector) {
		super(COLLAPSE);
		
		this.setCollapseVector(collapseVector);
	}

	public Vec2 getCollapseVector() {
		return collapseVector;
	}

	public void setCollapseVector(Vec2 collapseVector) {
		this.collapseVector = collapseVector;
	}
	
}