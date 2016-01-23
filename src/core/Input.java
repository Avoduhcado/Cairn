package core;

import java.awt.geom.Point2D;
import java.io.File;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector2f;

import core.entities.utils.CharState;
import core.scene.ShadowMap;
import core.setups.GameSetup;
import core.setups.Stage;
import core.utilities.Screenshot;
import core.utilities.keyboard.Keybinds;

public class Input {
	
	private static boolean mouseHeld;
	public static Point2D mousePress;
	public static Point2D mouseCurrent = new Point2D.Double();
	public static Point2D mouseRelease;
	public static Vector2f mouseDelta = new Vector2f();
	private static int mouseScroll;
		
	/**
	 * Main processing of any and all input depending on current setup.
	 * @param setup The current setup of the game
	 */
	public static void checkInput(GameSetup setup) {
		// Refresh key bind presses
		Keybinds.update();
		
		// Enter debug mode
		if(Keybinds.DEBUG.clicked()) {
			Theater.get().debug = !Theater.get().debug;
			//Cheats.SPEED_HACK = Theater.get().debug;
		}
		
		if(Keybinds.EDIT.clicked()) {
			Camera.get().setFullscreen(!Camera.get().isFullscreen());
			Camera.get().setVSync(true);
		}
		
		if(Keybinds.CONTROL.clicked()) {
			Camera.get().setZoom(0.5f, 0.3333f);
		} else if(Keybinds.MENU.clicked()) {
			Camera.get().setZoom(0.5f, -0.3333f);
		} else if(Keybinds.SLOT6.clicked()) {
			Camera.get().zoomTo(0.5f, 1f);
		}

		if(Keybinds.CANCELTEXT.clicked()) {
			Screenshot.saveScreenshot(new File(System.getProperty("user.dir")), Camera.get().displayWidth, Camera.get().displayHeight);
		}
		
		if(Keybinds.SLOT9.clicked()) {
			ShadowMap.get().getLightSources().get(0).setResize(1f);
		}
		if(Keybinds.SLOT8.clicked()) {
			ShadowMap.get().getLightSources().get(0).setResize(0.2f);
		}
		if(Keybinds.SLOT7.clicked()) {
			ShadowMap.get().getLightSources().get(0).setResize(-0.2f);
		}
		
		if(mousePress != null && !mouseHeld) {
			mouseHeld = true;
		}
		
		while(Mouse.next()) {
			if(Mouse.getEventButton() != -1) {
				if(Mouse.getEventButtonState()) {
					mousePress = new Point2D.Double(Mouse.getX(), Mouse.getY());
					mouseRelease = null;
				} else if(!Mouse.getEventButtonState()) {
					mousePress = null;
					mouseRelease = new Point2D.Double(Mouse.getX(), Mouse.getY());
					mouseHeld = false;
				}
			}
			mouseCurrent.setLocation(Mouse.getX(), Mouse.getY());
		}
		mouseDelta.set(Mouse.getDX(), -Mouse.getDY());
		
		// Camera zooming
		if(Mouse.hasWheel() && (mouseScroll = Mouse.getDWheel()) != 0) {
			float wheel = Theater.getDeltaSpeed(mouseScroll / 1200f);
			if(Camera.get().getScale() + wheel >= 0.1f && Camera.get().getScale() + wheel <= 3f &&
					!Camera.get().isZooming()) {
				if(Camera.get().getScale() > 1f && Camera.get().getScale() + wheel < 1f) {
					//Camera.get().zoomTo(1f, 1f);
					Camera.get().setScale(1f);
				} else {
					//Camera.get().zoomTo(Camera.get().getScale() + wheel, 0.01f);
					Camera.get().setScale(Camera.get().getScale() + wheel);
				}
			} else if(Camera.get().getScale() + wheel >= 3f) {
				Camera.get().setScale(3f);
			} else {
				Camera.get().setScale(0.1f);
			}
		}
	}
	
	public static boolean mouseClicked() {
		return mousePress != null && !mouseHeld;
	}
	
	public static boolean mouseClicked(int button) {
		return mouseClicked() && Mouse.isButtonDown(button);
	}
	
	public static boolean mousePressed() {
		return mousePress != null;
	}
	
	public static boolean mouseReleased() {
		return mouseRelease != null;
	}
	
	public static boolean mouseHeld() {
		return mouseHeld;
	}

}
