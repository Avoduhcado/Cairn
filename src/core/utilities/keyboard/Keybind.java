package core.utilities.keyboard;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;

import core.utilities.Config;

public enum Keybind {
	
	CONFIRM (Keyboard.KEY_RETURN),
	CANCEL (Keyboard.KEY_F),
	RIGHT (Keyboard.KEY_RIGHT),
	LEFT (Keyboard.KEY_LEFT),
	UP (Keyboard.KEY_UP),
	DOWN (Keyboard.KEY_DOWN),
	RUN (Keyboard.KEY_LSHIFT),
	LIGHT_ATTACK (Keyboard.KEY_D),
	HEAVY_ATTACK (Keyboard.KEY_A),
	DEFEND (Keyboard.KEY_W),
	DODGE (Keyboard.KEY_S),
	MENU (Keyboard.KEY_E),
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
	Keybind(int k) {
		this.key = new Press(k);
	}
	
	/**
	 * Check for key interactions.
	 */
	public static void update() {
		for(Keybind keybinds : Keybind.values()) {
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
		if(key.isPressed() && !key.isHeld()) {
			return true;
		} else {
			return false;
		}
	}
	
	/** Key was released. */
	public boolean released() {
		return key.isReleased();
	}

	public static boolean movement() {
		return RIGHT.press() || LEFT.press() || UP.press() || DOWN.press();
	}
	
	/** Disable specific keys for menu navigation */
	public static void inMenu() {
		Keybind.MENU.key.setDisabled(true);
		Keybind.PAUSE.key.setDisabled(true);
		Keybind.SLOT1.key.setDisabled(true);
		Keybind.SLOT2.key.setDisabled(true);
		Keybind.SLOT3.key.setDisabled(true);
		Keybind.SLOT4.key.setDisabled(true);
		Keybind.SLOT5.key.setDisabled(true);
		Keybind.SLOT6.key.setDisabled(true);
		Keybind.SLOT7.key.setDisabled(true);
		Keybind.SLOT8.key.setDisabled(true);
	}
	
	/** Enable specific keys after closing menu */
	public static void closeMenu() {
		Keybind.MENU.key.setDisabled(false);
		Keybind.PAUSE.key.setDisabled(false);
		Keybind.SLOT1.key.setDisabled(false);
		Keybind.SLOT2.key.setDisabled(false);
		Keybind.SLOT3.key.setDisabled(false);
		Keybind.SLOT4.key.setDisabled(false);
		Keybind.SLOT5.key.setDisabled(false);
		Keybind.SLOT6.key.setDisabled(false);
		Keybind.SLOT7.key.setDisabled(false);
		Keybind.SLOT8.key.setDisabled(false);
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
			Keybind.valueOf(temp[0]).setKey(Keyboard.getKeyIndex(temp[1]));
		} catch(IllegalArgumentException e) {
			// Reload config
			// XXX This causes massive looping errors if a keybind is renamed/missing/extra/nonexistant in some way
			//Config.createConfig();
			//Config.loadConfig();			
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
		for(Keybind e : Keybind.values()) {
			e.key.setPressed(false);
		}
	}

}
