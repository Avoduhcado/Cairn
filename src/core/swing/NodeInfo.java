package core.swing;

public class NodeInfo {

	private String tag;
	private Object value;
	
	public NodeInfo(String tag, Object value) {
		setTag(tag);
		setValue(value);
	}
	
	public String getTag() {
		return tag;
	}
	
	public void setTag(String tag) {
		this.tag = tag;
	}
	
	public Object getValue() {
		return value;
	}
	
	public void setValue(Object value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return tag + ": " + value;
	}
	
}
