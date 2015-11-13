package com.esotericsoftware.spine.attachments;

import org.jbox2d.dynamics.Body;

public class Box2DRegion extends Region {

	private Body body;
	
	public Box2DRegion(String name) {
		super(name);
	}

	public Body getBody() {
		return body;
	}

	public void setBody(Body body) {
		this.body = body;
	}
	
}
