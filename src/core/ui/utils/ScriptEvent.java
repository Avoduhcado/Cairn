package core.ui.utils;

import core.entities.interfaces.Scriptable;
import core.ui.UIElement;

enum InteractType {
	KEYPRESS, PLAYER_TOUCH, ENTITY_TOUCH, AUTO, PARALLEL;
}

public abstract class ScriptEvent extends UIEvent implements UIAction {

	private Scriptable scriptHost;
	private InteractType type;
	
	public ScriptEvent(UIElement parent, Scriptable scriptHost, InteractType type) {
		super(parent);
		this.scriptHost = scriptHost;
		this.type = type;
	}
	
	public abstract void read();
	
	public void actionPerformed() {
		switch(type) {
		case KEYPRESS:
			break;
		case PLAYER_TOUCH:
			read();
			break;
		default:
			break;
		}
	}

}
