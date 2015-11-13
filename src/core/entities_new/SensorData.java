package core.entities_new;

import org.jbox2d.dynamics.Fixture;

public class SensorData {

	private Entity entity;
	private SensorType type;
	
	private Fixture sensor;
	
	public SensorData(Entity entity, SensorType type) {
		this.entity = entity;
		this.type = type;
	}
	
	public Entity getEntity() {
		return entity;
	}
	
	public SensorType getType() {
		return type;
	}

	public Fixture getSensor() {
		return sensor;
	}

	public void setSensor(Fixture sensor) {
		this.sensor = sensor;
	}
	
}
