package core.setups;

import java.util.Iterator;
import java.util.LinkedList;

import core.Camera;
import core.Input;
import core.Theater;
import core.audio.Ensemble;
import core.audio.Track;
import core.entities.Ally;
import core.entities.Enemy;
import core.entities.Backdrop;
import core.entities.Entity;
import core.entities.LightSource;
import core.entities.Player;
import core.entities.Prop;
import core.entities.Actor;
import core.entities.interfaces.Intelligent;
import core.entities.utils.CharState;
import core.entities.utils.ai.Personality;
import core.entities.utils.ai.traits.Minion;
import core.render.LightMap;
import core.scene.Map;
import core.ui.UIElement;
import core.ui.overlays.EditMenu;
import core.ui.overlays.GameMenu;
import core.utilities.Pathfinder;
import core.utilities.keyboard.Keybinds;
import core.utilities.scripts.Script;

public class Stage extends GameSetup {

	/** Stage relevant */
	private GameMenu gameMenu;
	private EditMenu editMenu;
	private Player player;
	private Map map;
	
	public Stage() {
		Camera.get().setFadeTimer(-7.5f);
		Camera.get().frame.setFrame(0, 0, Camera.get().frame.getWidth(), Camera.get().frame.getHeight());
		if(Ensemble.get().getBackground() != null) {
			Ensemble.get().swapBackground(new Track("CairnArea4"), 5f, 5f);
		}
		
		//map = new Map();
		
		map = Map.deserialize("Map001");
		map.getScenery().add(new Prop(1705, 990, "Cairn", Camera.ASPECT_RATIO));
		map.getCast().add(new Enemy(6000, 625, "Acolyte_2", Camera.ASPECT_RATIO));
		map.getCast().getLast().setMaxSpeed(1.75f);
		map.getCast().getLast().setDirection(1);
		((Enemy) map.getCast().getLast()).getStats().getHealth().setCurrent(50f);
		map.getScenery().add(map.getCast().getLast());
		map.getCast().add(new Enemy(6180, 630, "Acolyte_2", Camera.ASPECT_RATIO));
		map.getCast().getLast().setMaxSpeed(1.75f);
		map.getCast().getLast().setDirection(1);
		((Enemy) map.getCast().getLast()).getStats().getHealth().setCurrent(50f);
		map.getScenery().add(map.getCast().getLast());

		for(Entity e : map.getScenery()) {
			System.out.println(e.getID());
		}
		
		player = new Player(1655, 1030, "MC and Familiar", Camera.ASPECT_RATIO);
		Camera.get().setFocus(player);
		Camera.get().centerOn(this);
		map.getScenery().add(player);
				
		((Ally) findEntity("Ally1")).setDirection(1);
		((Ally) findEntity("Ally0")).setScript(new Script("<s0.3>Et tu, Skelebones?", 
				"{event: [{showText: 'Congratulations.;You reached the end.'},{showText: 'Press " + Keybinds.SLOT8.getKey() + " to restart.'}] }"));
		
		((Enemy) findEntity("Enemy2")).getIntelligence().addTrait(new Minion(null));
		((Enemy) findEntity("Enemy3")).getIntelligence().addTrait(new Minion(null));
		((Enemy) findEntity("Enemy4")).getStats().getHealth().setCurrent(45f);
		((Enemy) findEntity("Enemy7")).getStats().getHealth().setCurrent(50f);
		((Actor) findEntity("Enemy7")).setMaxSpeed(1.8f);
		((Enemy) findEntity("Enemy8")).getStats().getHealth().setCurrent(40f);
		((Actor) findEntity("Enemy8")).setDirection(0);
		((Enemy) findEntity("Enemy9")).getStats().getHealth().setCurrent(40f);
		findEntity("Enemy9").setPosition(5160, 775);
		
		for(Actor a : getCast()) {
			if(a instanceof Intelligent) {
				if(((Enemy) a).getIntelligence().getPersonality().equals(Personality.NEUTRAL)) {
					((Enemy) a).getIntelligence().setPersonality(Personality.AGGRESSIVE);
				}
			}
		}
		
		/*((Actor) findEntity("Enemy2")).setMaxSpeed(1.75f);
		ArrayList<Intelligent> tempList = new ArrayList<Intelligent>();
		tempList.add((Intelligent) findEntity("Enemy3"));
		tempList.add((Intelligent) findEntity("Enemy4"));
		tempList.add((Intelligent) findEntity("Enemy5"));
		((Enemy) findEntity("Enemy2")).getIntelligence().addTrait(new PackLeader(tempList));
		((Actor) findEntity("Enemy3")).setMaxSpeed(1.2f);
		((Enemy) findEntity("Enemy3")).getIntelligence().addTrait(new Minion((Intelligent) findEntity("Enemy2")));
		((Actor) findEntity("Enemy4")).setMaxSpeed(1.2f);
		((Enemy) findEntity("Enemy4")).getIntelligence().addTrait(new Minion((Intelligent) findEntity("Enemy2")));
		((Actor) findEntity("Enemy5")).setMaxSpeed(1.6f);
		((Enemy) findEntity("Enemy5")).getIntelligence().addTrait(new Minion((Intelligent) findEntity("Enemy2")));*/

		//map.getLights().get(0).setParent(player);
	}

	@Override
	public void update() {
		if(gameMenu != null) {
			gameMenu.update();
			if(gameMenu.isCloseRequest())
				gameMenu = null;
		} else {
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
		
		for(Prop p : map.getProps()) {
			p.draw();
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

		for(Backdrop b : map.getForeground()) {
			b.draw();
		}

		if(map.getFog() != null)
			map.getFog().draw();

		if(!LightMap.lights.isEmpty()) {
			// Remember to re-init LightMap before using these
			//LightMap.drawFBOMax();
			LightMap.draw();
			//LightMap.drawNoFBO();
		}
		
		for(UIElement ui : uiElements) {
			ui.draw();
		}

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
