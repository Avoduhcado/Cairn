package core.entities_new.utils;

import core.entities_new.Entity;

public class SensorData {

	private Entity entity;
	private SensorType type;
		
	public SensorData(Entity entity, SensorType type) {
		this.entity = entity;
		this.type = type;
	}
	
	public Entity getEntity() {
		return entity;
	}
	
	public void setEntity(Entity entity) {
		this.entity = entity;
	}
	
	public SensorType getType() {
		return type;
	}
	
	public void setType(SensorType type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return entity.toString() + ": " + type;
	}
	
}
