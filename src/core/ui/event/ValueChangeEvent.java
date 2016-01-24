package core.ui.event;

public class ValueChangeEvent extends UIEvent {

	private Object value;
	private Object change;
	
	public ValueChangeEvent(Object value, Object change) {
		this.setValue(value);
		this.setChange(change);
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public Object getChange() {
		return change;
	}

	public void setChange(Object change) {
		this.change = change;
	}
	
}
