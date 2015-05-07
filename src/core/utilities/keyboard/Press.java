package core.utilities.keyboard;

import core.Theater;


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
	/** Time since last key press */
	private float pressDelay = -1f;
	
	/**
	 * Manage key mappings for button input.
	 * @param k int for this key mapping
	 */
	public Press(int k) {
		setKey(k);
		setPressed(Keyboard.isPressed(k));
		setHeld(Keyboard.isPressed(k));
	}
	
	/**
	 * Check for key interactions
	 */
	public void update() {
		if(Keyboard.isPressed(getKey())) {
			setPressed(true);
			setReleased(false);
		} else {
			if(isPressed())
				setReleased(true);
			else
				setReleased(false);
			setPressed(false);
			setHeld(false);
		}
		
		if(pressDelay >= 0f) {
			pressDelay += Theater.getDeltaSpeed(0.025f);
			if(pressDelay > 0.3f) {
				pressDelay = -1f;
			}
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
		if(pressed && pressDelay == -1f) {
			pressDelay = 0;
		}
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
	
	public float getPressDelay() {
		return pressDelay;
	}
}
