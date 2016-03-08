package core.ui.event;

public abstract class UIEvent {

	private boolean consumed;
	
	public boolean isConsumed() {
		return consumed;
	}
	
	public void consume() {
		this.consumed = true;
	}
	
}
