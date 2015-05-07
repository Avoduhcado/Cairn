package core.utilities.keyboard;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;

import core.utilities.Config;

public enum Keybinds {
	
	CONFIRM (Keyboard.KEY_RETURN),
	CANCEL (Keyboard.KEY_F),
	CONTROL (Keyboard.KEY_LCONTROL),
	RIGHT (Keyboard.KEY_RIGHT),
	LEFT (Keyboard.KEY_LEFT),
	UP (Keyboard.KEY_UP),
	DOWN (Keyboard.KEY_DOWN),
	MENU_RIGHT (Keyboard.KEY_D),
	MENU_LEFT (Keyboard.KEY_A),
	RUN (Keyboard.KEY_LSHIFT),
	ATTACK (Keyboard.KEY_E),
	DEFEND (Keyboard.KEY_Q),
	MENU (Keyboard.KEY_M),
	PAUSE (Keyboard.KEY_P),
	DEBUG (Keyboard.KEY_F3),
	EDIT (Keyboard.KEY_F4),
	SLOT1 (Keyboard.KEY_1),
	SLOT2 (Keyboard.KEY_2),
	SLOT3 (Keyboard.KEY_3),
	SLOT4 (Keyboard.KEY_4),
	SLOT5 (Keyboard.KEY_5),
	SLOT6 (Keyboard.KEY_6),
	SLOT7 (Keyboard.KEY_7),
	SLOT8 (Keyboard.KEY_8),
	SLOT9 (Keyboard.KEY_9),
	SLOT0 (Keyboard.KEY_0),
	CANCELTEXT (Keyboard.KEY_TAB),
	EXIT (Keyboard.KEY_ESCAPE);
	
	/** Enum's key mapping */
	private Press key;
	
	/**
	 * Create new key mapping.
	 * @param k integer to map key to
	 */
	Keybinds(int k) {
		this.key = new Press(k);
	}
	
	/**
	 * Check for key interactions.
	 */
	public static void update() {
		for(Keybinds keybinds : Keybinds.values()) {
			keybinds.key.setHeld(keybinds.key.isPressed());
			keybinds.key.update();
		}
	}
	
	/** Key has been pressed. */
	public boolean press() {
		return key.isPressed();
	}
	
	/** Key has been and is currently pressed. */
	public boolean held() {
		return key.isHeld();
	}
	
	/** Key was pressed and is no longer pressed. */
	public boolean clicked() {
		if(key.isPressed() && !key.isHeld())
			return true;
		else
			return false;
	}
	
	/** Key was pressed twice in quick succession */
	public boolean doubleClicked() {
		if(clicked() && (key.getPressDelay() > 0.02f && key.getPressDelay() < 0.2f))
			return true;
		else
			return false;
	}
	
	/** Key was released. */
	public boolean released() {
		return key.isReleased();
	}
	
	/** Disable specific keys for menu navigation */
	public static void inMenu() {
		Keybinds.MENU.key.setDisabled(true);
		Keybinds.PAUSE.key.setDisabled(true);
		Keybinds.SLOT1.key.setDisabled(true);
		Keybinds.SLOT2.key.setDisabled(true);
		Keybinds.SLOT3.key.setDisabled(true);
		Keybinds.SLOT4.key.setDisabled(true);
		Keybinds.SLOT5.key.setDisabled(true);
		Keybinds.SLOT6.key.setDisabled(true);
		Keybinds.SLOT7.key.setDisabled(true);
		Keybinds.SLOT8.key.setDisabled(true);
	}
	
	/** Enable specific keys after closing menu */
	public static void closeMenu() {
		Keybinds.MENU.key.setDisabled(false);
		Keybinds.PAUSE.key.setDisabled(false);
		Keybinds.SLOT1.key.setDisabled(false);
		Keybinds.SLOT2.key.setDisabled(false);
		Keybinds.SLOT3.key.setDisabled(false);
		Keybinds.SLOT4.key.setDisabled(false);
		Keybinds.SLOT5.key.setDisabled(false);
		Keybinds.SLOT6.key.setDisabled(false);
		Keybinds.SLOT7.key.setDisabled(false);
		Keybinds.SLOT8.key.setDisabled(false);
	}

	/**
	 * Change a key binding.
	 * 
	 * @param keybind to change key binding
	 */
	public static void changeBind(String keybind) {
		String[] temp = keybind.split("=");
		
		// Handled through Config file
		try {
			Keybinds.valueOf(temp[0]).setKey(Keyboard.getKeyIndex(temp[1]));
		} catch(IllegalArgumentException e) {
			// Reload config
			Config.createConfig();
			Config.loadConfig();			
		}
	}
	
	/**
	 * 
	 * @return The key name of this key press
	 */
	public String getKey() {
		return Keyboard.getKeyName(this.key.getKey());
	}
	
	/**
	 * Set key press to new key.
	 * 
	 * @param k new integer for key mapping
	 */
	public void setKey(int k) {
		this.key = new Press(k);
	}
	
	/**
	 * Destroy and recreate keyboard.
	 * Set all key presses to false.
	 */
	public static void clear() {
		Keyboard.destroy();
		try {
			Keyboard.create();
		} catch (LWJGLException e1) {
			e1.printStackTrace();
		}
		for(Keybinds e : Keybinds.values()) {
			e.key.setPressed(false);
		}
	}
}
