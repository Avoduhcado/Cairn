package core.utilities.keyboard;

import org.lwjgl.input.Keyboard;

public class Press {

	/** Integer value of key mapping */
	private int key;
	/** True if key was pressed */
	private boolean pressed;
	/** True if key is currently pressed */
	private boolean held;
	/** True if key was just released */
	private boolean released;
	/** True if key presses are to be ignored */
	private boolean disabled;
		
	/**
	 * Manage key mappings for button input.
	 * @param k int for this key mapping
	 */
	public Press(int k) {
		setKey(k);
		setPressed(Keyboard.isKeyDown(k));
		setHeld(Keyboard.isKeyDown(k));
	}
	
	/**
	 * Check for key interactions
	 */
	public void update() {
		if(Keyboard.isKeyDown(getKey())) {
			setPressed(true);
			setReleased(false);
		} else {
			if(isPressed()) {
				setReleased(true);
			} else {
				setReleased(false);
			}
			setPressed(false);
			setHeld(false);
		}
	}

	/**
	 * 
	 * @return Integer for this key mapping
	 */
	public int getKey() {
		return key;
	}

	/**
	 * Set new key mapping.
	 * 
	 * @param key integer for key mapping
	 */
	public void setKey(int key) {
		this.key = key;
	}

	/**
	 * 
	 * @return True if key was pressed.
	 */
	public boolean isPressed() {
		if(isDisabled())
			return false;
		return pressed;
	}

	/**
	 * 
	 * @param pressed if key was pressed
	 */
	public void setPressed(boolean pressed) {
		this.pressed = pressed;
	}

	/**
	 * 
	 * @return True if key is being held
	 */
	public boolean isHeld() {
		if(isDisabled())
			return false;
		return held;
	}

	/**
	 * 
	 * @param held if key is held
	 */
	public void setHeld(boolean held) {
		this.held = held;
	}

	/**
	 * 
	 * @return True if key was released
	 */
	public boolean isReleased() {
		if(isDisabled())
			return false;
		return released;
	}

	/**
	 * 
	 * @param released if key was released
	 */
	public void setReleased(boolean released) {
		this.released = released;
	}
	
	/**
	 * 
	 * @return True if key is disabled
	 */
	public boolean isDisabled() {
		return disabled;
	}

	/**
	 * 
	 * @param disabled ignore key presses from this key
	 */
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

}
