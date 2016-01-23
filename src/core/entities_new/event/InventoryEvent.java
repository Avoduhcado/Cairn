package core.entities_new.event;

public class InventoryEvent extends EntityEvent {

	public static final int CYCLE = 1;
	
	private int eventType;
	
	public InventoryEvent(int eventType) {
		this.setEventType(eventType);
	}

	public int getEventType() {
		return eventType;
	}

	public void setEventType(int eventType) {
		this.eventType = eventType;
	}
	
}
