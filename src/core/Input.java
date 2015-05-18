package core;

import java.awt.geom.Point2D;
import java.io.File;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector2f;

import core.entities.Actor;
import core.entities.Enemy;
import core.entities.Player;
import core.entities.interfaces.Intelligent;
import core.entities.utils.CharState;
import core.entities.utils.ai.Personality;
import core.equipment.Equipment;
import core.setups.GameSetup;
import core.setups.Stage;
import core.utilities.Screenshot;
import core.utilities.keyboard.Keybinds;

public class Input {
	
	private static boolean mouseHeld;
	public static Point2D mousePress;
	public static Point2D mouseCurrent = new Point2D.Double();
	public static Point2D mouseRelease;
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
		
		if(mousePress != null && !mouseHeld) {
			mouseHeld = true;
		}
		
		while(Mouse.next()) {
			if(Mouse.getEventButton() == 0) {
				if(Mouse.getEventButtonState()) {
					mousePress = new Point2D.Double(Mouse.getX(), Mouse.getY());
					mouseRelease = null;
				} else if(!Mouse.getEventButtonState()) {
					mousePress = null;
					mouseRelease = new Point2D.Double(Mouse.getX(), Mouse.getY());
					mouseHeld = false;
				}
			} else if(Mouse.getEventButton() == -1){
				mouseCurrent.setLocation(Mouse.getX(), Mouse.getY());
			}
		}
		
		// Camera zooming
		if(Mouse.hasWheel() && (mouseScroll = Mouse.getDWheel()) != 0) {
			float wheel = Theater.getDeltaSpeed(mouseScroll / 12000f);
			if(Camera.get().getScale() + wheel >= 0.25f && Camera.get().getScale() + wheel <= 3f) {
				if(Camera.get().getScale() > 1f && Camera.get().getScale() + wheel < 1f)
					Camera.get().setScale(1f);
				else
					Camera.get().setScale(Camera.get().getScale() + wheel);
			} else if(Camera.get().getScale() + wheel >= 3f) {
				Camera.get().setScale(3f);
			} else {
				Camera.get().setScale(0.25f);
			}
		}
		
		// Setup specific processing
		if(setup instanceof Stage) {
			if(((Stage) setup).getPlayer().getState().canWalk()) {
				if(Keybinds.RUN.held() && ((Stage) setup).getPlayer().canRun()) {
					((Actor) ((Stage) setup).getPlayer()).setState(CharState.RUN);
				} else if(Keybinds.RUN.released() || !((Stage) setup).getPlayer().canRun()) {
					if(((Actor) ((Stage) setup).getPlayer()).getVelocity().length() != 0) {
						((Actor) ((Stage) setup).getPlayer()).setState(CharState.WALK);
					} else {
						((Actor) ((Stage) setup).getPlayer()).setState(CharState.IDLE);
					}
				}
				
				if(Keybinds.DODGE.clicked()) {
					((Stage) setup).getPlayer().dodge(null);
				}
				
				if(Keybinds.RIGHT.press()) {
					((Stage) setup).getPlayer().moveRight();
				}
				if(Keybinds.LEFT.press()) {
					((Stage) setup).getPlayer().moveLeft();
				}
				
				if(Keybinds.UP.doubleClicked()) {
					((Stage) setup).getPlayer().dodge(new Vector2f(0f, -5f));
				} else if(Keybinds.UP.press()) {
					((Stage) setup).getPlayer().moveUp();
				} else if(Keybinds.UP.released()) {
					((Stage) setup).getPlayer().setLooking(0);
				}
				if(Keybinds.DOWN.doubleClicked()) {
					((Stage) setup).getPlayer().dodge(new Vector2f(0f, 5f));
				} else if(Keybinds.DOWN.press()) {
					((Stage) setup).getPlayer().moveDown();
				} else if(Keybinds.DOWN.released()) {
					((Stage) setup).getPlayer().setLooking(0);
				}
			}
			
			if(((Stage) setup).getPlayer().getState().canAct()) {
				if(Keybinds.ATTACK.clicked()) {
					((Player) ((Stage) setup).getPlayer()).attack();
				} else if(Keybinds.DEFEND.clicked()) {
					((Player) ((Stage) setup).getPlayer()).defend();
				} else if(Keybinds.MENU_RIGHT.clicked()) {
					((Player) ((Stage) setup).getPlayer()).cast();
				}
				
				if(Keybinds.MENU_UP.clicked()) {
					((Stage) setup).getPlayer().heal(false);
				}
				
				if(Keybinds.SLOT1.clicked()) {
					((Stage) setup).getPlayer().changeWeapon(Equipment.lightMace);
				} else if(Keybinds.SLOT2.clicked()) {
					((Stage) setup).getPlayer().changeWeapon(Equipment.heavyMace);
				} else if(Keybinds.SLOT3.clicked()) {
					((Stage) setup).getPlayer().changeWeapon(Equipment.polearm);
				}
			} else if(((Stage) setup).getPlayer().canHeal()) {
				if(Keybinds.MENU_UP.clicked()) {
					((Stage) setup).getPlayer().heal(true);
				}
			}
			
			if(Keybinds.SLOT7.clicked()) {
				for(Actor a : ((Stage) setup).getCast()) {
					if(a instanceof Intelligent) {
						if(((Enemy) a).getIntelligence().getPersonality().equals(Personality.NEUTRAL)) {
							((Enemy) a).getIntelligence().setPersonality(Personality.AGGRESSIVE);
						} else {
							((Enemy) a).getIntelligence().setPersonality(Personality.NEUTRAL);
						}
					}
				}
			}
			if(Keybinds.SLOT8.clicked()) {
				Theater.get().swapSetup(new Stage());
			}
			if(Keybinds.SLOT9.clicked()) {
				((Stage) setup).getPlayer().setPosition(8000, 700);
				Camera.get().centerOn((Stage) setup);
			}
			if(Keybinds.SLOT0.clicked()) {
				((Stage) setup).getPlayer().setPosition(900, 800);
				Camera.get().centerOn((Stage) setup);
			}
			
			if(Keybinds.MENU.clicked()) {
				Camera.get().setScale(1f);
			}
			
			if(Keybinds.PAUSE.clicked()) {
				Theater.get().pause();
			}
			
			if(Keybinds.CANCELTEXT.clicked()) {
				Screenshot.saveScreenshot(new File(System.getProperty("user.dir")), Camera.get().displayWidth, Camera.get().displayHeight);
			}
		}
	}
	
	public static boolean mouseClicked() {
		return mousePress != null && !mouseHeld;
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
