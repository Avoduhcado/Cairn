package com.esotericsoftware.spine.attachments;

import org.jbox2d.dynamics.Body;

public class Box2dAttachment extends RegionAttachment {
	private Body body;

	public Box2dAttachment (String name) {
		super(name);
	}
	
	public Body getBody() {
		return body;
	}
	
	public void setBody(Body body) {
		this.body = body;
	}
}