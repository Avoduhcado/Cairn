package core.setups;

import java.util.Iterator;
import java.util.LinkedList;

import org.lwjgl.util.vector.Vector3f;

import core.Camera;
import core.Input;
import core.Theater;
import core.audio.AudioSource;
import core.entities.Ally;
import core.entities.Enemy;
import core.entities.Backdrop;
import core.entities.Entity;
import core.entities.LightSource;
import core.entities.Player;
import core.entities.Actor;
import core.entities.utils.CharState;
import core.render.LightMap;
import core.scene.HUD;
import core.scene.Map;
import core.ui.UIElement;
import core.ui.overlays.EditMenu;
import core.ui.overlays.GameMenu;
import core.utilities.Pathfinder;
import core.utilities.keyboard.Keybinds;

public class Stage extends GameSetup {

	/** Stage relevant */
	private GameMenu gameMenu;
	private EditMenu editMenu;
	private Player player;
	private Map map;
	
	private AudioSource bgm;
	
	public Stage() {
		Camera.get().setFadeTimer(-7.5f);
		Camera.get().frame.setFrame(0, 0, Camera.get().frame.getWidth(), Camera.get().frame.getHeight());
		bgm = new AudioSource("CairnArea4", "BGM");
		
		map = new Map();
		
		//map = Map.deserialize("Map001");
		
		for(Entity e : map.getScenery()) {
			System.out.println(e.getID());
		}
		
		player = new Player(550, 390, "MC and Familiar", Camera.ASPECT_RATIO);
		Camera.get().setFocus(player);
		Camera.get().centerOn(this);
		map.getScenery().add(player);
		
		bgm.setPosition(new Vector3f(0, 0, -5f));
		bgm.getAudio().playAsMusic(1f, 0, true);
				
		//map.getLights().get(0).setParent(player);
	}

	@Override
	public void update() {
		if(gameMenu != null) {
			gameMenu.update();
			if(gameMenu.isCloseRequest())
				gameMenu = null;
		} else {
			bgm.update();
			
			for(int i = 0; i<uiElements.size(); i++) {
				if(uiElements.get(i).isKill()) {
					uiElements.remove(i);
					i--;
					continue;
				}
				uiElements.get(i).update();
			}
			
			player.update();
			if(player.getState() == CharState.DEAD) {
				Theater.get().swapSetup(new Stage());
			}

			for(Iterator<Actor> i = map.getCast().iterator(); i.hasNext();) {
				Actor a = i.next();
				a.update();
				if(a instanceof Ally) {
					((Ally) a).activateScript(player, this);
				} else {
					if(a.getState() == CharState.DEAD) {
						i.remove();
						removeEntity(a);
						continue;
					}
					if(a instanceof Enemy) {
						((Enemy) a).think(this);
					}
				}
			}

			map.update();

			if(Keybinds.EXIT.clicked()) {
				gameMenu = new GameMenu(20f, 20f, "Menu2");
			}

			if(editMenu != null) {
				// TODO Freeze AI and stuff during editing, enable cheats
				editMenu.update();
				if(editMenu.isCloseRequest()) {
					editMenu = null;
				}
			} else if(Keybinds.EDIT.clicked()) {
				editMenu = new EditMenu(map);
			}
		}
		
		if(Input.mouseClicked() && !map.getPathPolys().isEmpty()) {
			Pathfinder.buildPath(Input.mousePress, player);
		}
	}

	@Override
	public void draw() {
		for(Backdrop b : map.getBackground()) {
			b.draw();
		}
		
		for(Backdrop g : map.getGround()) {
			g.draw();
		}

		for(LightSource l : map.getLights()) {
			l.draw();
		}

		for(int x = 0; x<map.getScenery().size(); x++) {
			for(int i = x; i>=0 && i>x-5; i--) {
				if(map.getScenery().get(x).getBox().getMaxY() < map.getScenery().get(i).getBox().getMaxY()) {
					map.getScenery().add(i, map.getScenery().get(x));
					map.getScenery().remove(x+1);
					x--;
				}
			}
		}

		// Draw the scenery
		for(Entity e : map.getScenery()) {
			e.draw();
		}

		for(Backdrop f : map.getForeground()) {
			f.draw();
		}

		if(map.getFog() != null)
			map.getFog().draw();

		if(!LightMap.lights.isEmpty()) {
			// Remember to re-init LightMap before using these
			//LightMap.drawFBOMax();
			LightMap.draw();
			//LightMap.drawNoFBO();
		}
	}
	
	@Override
	public void drawUI() {
		for(UIElement ui : uiElements) {
			ui.draw();
		}
		
		HUD.draw(this);

		if(editMenu != null) {
			editMenu.draw();
		}
		
		if(gameMenu != null)
			gameMenu.draw();
	}

	@Override
	public void resizeRefresh() {
	}

	public Player getPlayer() {
		return player;
	}

	public LinkedList<Actor> getCast() {
		return map.getCast();
	}
	
	public Entity findEntity(String ID) {
		for(Entity e : map.getScenery()) {
			if(e.getID().matches(ID)) {
				return e;
			}
		}

		return null;
	}

	public void removeEntity(Entity entity) {
		if(map.getScenery().contains(entity)) {
			map.getScenery().remove(entity);
			if(entity instanceof Actor) {
				map.getCast().remove(entity);
			}
		}
	}
	
}
