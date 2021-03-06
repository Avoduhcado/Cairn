package core.entities_new.utils;

import core.entities_new.Entity;

public class SensorData {

	public static final int IGNORE = -1;
	public static final int CHARACTER = 0;
	public static final int GROUND = 1;
	public static final int BODY = 2;
	public static final int WEAPON = 3;
	public static final int WALL = 4;
	public static final int INTERACTION = 5;
	
	private Entity entity;
	private String slot;
	private int type;
		
	public SensorData(Entity entity, String slot, int type) {
		this.entity = entity;
		this.slot = slot;
		this.type = type;
	}
	
	public Entity getEntity() {
		return entity;
	}
	
	public void setEntity(Entity entity) {
		this.entity = entity;
	}
	
	public int getType() {
		return type;
	}
	
	public void setType(int type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return entity.toString() + " [" + slot + "]: " + type;
	}
	
}
