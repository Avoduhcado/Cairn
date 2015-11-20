package com.esotericsoftware.spine.attachments;

import org.jbox2d.dynamics.Body;

public class Box2dAttachment extends RegionAttachment {
	Body body;

	public Box2dAttachment (String name) {
		super(name);
	}
}