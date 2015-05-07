package core.utilities.keyboard;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Keyboard implements KeyListener {
	
	/** Array of all key values */
	private static boolean[] keys = new boolean[1024];
	
	/**
	 * Check if a key was pressed.
	 * 
	 * @param key to check if pressed
	 * @return True if key was pressed
	 */
	public static boolean isPressed(int key) {
		//return keys[key];
		return org.lwjgl.input.Keyboard.isKeyDown(key);
	}

	/**
	 * Create a new key
	 * @param c bound to this component
	 */
	public static void init(Component c) {
		c.addKeyListener(new Keyboard());
	}
	
	public void keyPressed(KeyEvent e) {
		keys[e.getKeyCode()] = true;
	}

	public void keyReleased(KeyEvent e) {
		keys[e.getKeyCode()] = false;
	}

	public void keyTyped(KeyEvent e) {
	}

}
