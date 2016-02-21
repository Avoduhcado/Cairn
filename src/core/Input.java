package core;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import core.setups.Stage;
import core.ui.UIElement;
import core.ui.event.KeyEvent;
import core.ui.event.KeybindEvent;
import core.ui.event.MouseEvent;
import core.ui.overlays.GameMenu;
import core.ui.utils.UIContainer;
import core.utilities.keyboard.Keybind;

public class Input {
	
	// TODO Create a proper SINGLETONNNNN out of Input class
	static {
		//Keyboard.enableRepeatEvents(true);
	}
	
	/**
	 * Main processing of any and all input depending on current setup.
	 * @param setup The current setup of the game
	 */
	public static void checkInput(UIContainer setup) {
		// TODO Menu Overlays isn't processed as ElementGroup
		if(!setup.getUI().isEmpty()) {
			while(setup.getElement(setup.getUI().size() - 1) != null && setup.getElement(setup.getUI().size() - 1) instanceof UIContainer) {
				setup = (UIContainer) setup.getElement(setup.getUI().size() - 1);
			}
		}
		
		// Detect any keyboard events
		processKeyboard(setup);
		
		// Detect any keybind events
		processKeybinds(setup);
		
		// Detect any mouse events
		processMouse(setup);
		
		// TODO Hide behind a keybind listener?
		// Enter debug mode
		if(Keybind.DEBUG.clicked()) {
			Theater.get().debug = !Theater.get().debug;
		}
		
		// Setup specific processing
		if(setup instanceof Stage) {
			if(Keybind.PAUSE.clicked()) {
				Theater.get().pause();
			} else if(Keybind.EXIT.clicked()) {
				setup.addUI(new GameMenu());
			}
		}
	}
	
	private static void processKeyboard(UIContainer setup) {
		while(Keyboard.next()) {
			if(Keyboard.getEventKeyState()) {
				for(int i = 0; i<setup.getUI().size(); i++) {
					UIElement ui = setup.getUI().get(i);
					
					if(ui.getState() == UIElement.ENABLED) {
						ui.fireEvent(new KeyEvent(Keyboard.getEventKey(), 
								Keyboard.getEventCharacter(), 
								Keyboard.getKeyName(Keyboard.getEventKey())));
					}
				}
			}
		}
	}
	
	private static void processKeybinds(UIContainer setup) {
		Keybind.update();
		for(Keybind k : Keybind.values()) {
			if(k.clicked()) {
				for(int i = 0; i<setup.getUI().size(); i++) {
					UIElement ui = setup.getUI().get(i);
					
					if(ui.getState() == UIElement.ENABLED) {
						ui.fireEvent(new KeybindEvent(k));
					}
				}
			}
		}
	}
	
	private static void processMouse(UIContainer setup) {
		while(Mouse.next()) {
			if(Mouse.getEventButton() != -1) {
				if(Mouse.getEventButtonState()) {
					processMouseEvent(setup,
							new MouseEvent(MouseEvent.PRESSED,
									Mouse.getEventX(), Camera.get().displayHeight - Mouse.getEventY()));
					//System.out.println(Mouse.getEventButton() + " " + Mouse.getEventButtonState());
				} else {
					processMouseEvent(setup,
							new MouseEvent(MouseEvent.CLICKED,
									Mouse.getEventX(), Camera.get().displayHeight - Mouse.getEventY()));

					//System.out.println(Mouse.getEventX() + " " + (Camera.get().displayHeight - Mouse.getEventY()));
					//System.out.println(Mouse.getEventButton() + " " + Mouse.getEventButtonState());
				}
			} else if(Mouse.getDX() != 0 || Mouse.getDY() != 0) {
				MouseEvent me = new MouseEvent(MouseEvent.MOVED,
						Mouse.getEventX(), Camera.get().displayHeight - Mouse.getEventY());
				me.setDx(Mouse.getEventDX());
				me.setDy(-Mouse.getEventDY());
				processMouseEvent(setup, me);
				//System.out.println(Mouse.getEventDX() + " " + Mouse.getEventDY());
				
				if(Mouse.isButtonDown(0)) {
					MouseEvent med = new MouseEvent(MouseEvent.DRAGGED,
							Mouse.getEventX(), Camera.get().displayHeight - Mouse.getEventY());
					med.setDx(Mouse.getEventDX());
					med.setDy(-Mouse.getEventDY());
					processMouseEvent(setup, med);
				}
			}
		}
		
		if(Mouse.hasWheel() && Mouse.getDWheel() != 0) {
			// TODO Implement mouseWheelListener
			System.out.println(Mouse.getEventDWheel());
		}
	}
	
	private static void processMouseEvent(UIContainer setup, MouseEvent e) {
		for(int i = 0; i<setup.getUI().size(); i++) {
			UIElement ui = setup.getUI().get(i);
			switch(e.getEvent()) {
			case MouseEvent.CLICKED:
			case MouseEvent.RELEASED:
			case MouseEvent.PRESSED:
				if(ui.getBounds().contains(e.getPosition())) {
					ui.fireEvent(e);
				}
				break;
			case MouseEvent.MOVED:
			case MouseEvent.DRAGGED:
				if(ui.getBounds().contains(e.getPosition()) || ui.getBounds().contains(e.getPrevPosition())) {
					ui.fireEvent(e);
				}
				break;
			}
		}
	}
	
}
