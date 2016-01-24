package core.ui.event;

import core.utilities.keyboard.Keybind;

public class KeybindEvent extends UIEvent {

	private Keybind keybind;
	
	public KeybindEvent(Keybind keybind) {
		this.setKeybind(keybind);
	}

	public Keybind getKeybind() {
		return keybind;
	}

	public void setKeybind(Keybind keybind) {
		this.keybind = keybind;
	}
	
}
