package core.ui.event;

public class TimeEvent extends UIEvent {

	private float delta;
	
	public TimeEvent(float delta) {
		setDelta(delta);
	}

	public float getDelta() {
		return delta;
	}

	public void setDelta(float delta) {
		this.delta = delta;
	}

}
