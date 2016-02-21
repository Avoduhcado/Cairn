package core.entities_new.event;

public class ControllerEvent extends EntityEvent {

	public static final int MOVE = 1;
	public static final int DODGE = 2;
	public static final int ATTACK = 3;
	public static final int DEFEND = 4;
	public static final int COLLAPSE = 5;
	public static final int JUMP = 6;
	public static final int CHANGE_WEAPON = 7;
	public static final int REMOVE = 8;
	
	private int type;
	private Object data;
	
	public ControllerEvent(int type) {
		setType(type);
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	
	
}
