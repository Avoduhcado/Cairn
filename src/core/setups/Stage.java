package core.setups;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.jbox2d.callbacks.ContactFilter;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Filter;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import core.Camera;
import core.Input;
import core.Theater;
import core.audio.AudioSource;
import core.entities.Ally;
import core.entities.Clutter;
import core.entities.Enemy;
import core.entities.Backdrop;
import core.entities.Entity;
import core.entities.Player;
import core.entities.Actor;
import core.entities.Prop;
import core.entities.interfaces.Bonable;
import core.entities.utils.CharState;
import core.scene.BoneWorld;
import core.scene.Map;
import core.scene.ShadowMap;
import core.scene.hud.HUD;
import core.ui.UIElement;
import core.ui.overlays.EditMenu;
import core.ui.overlays.GameMenu;
import core.ui.overlays.Inventory;
import core.utilities.Pathfinder;
import core.utilities.keyboard.Keybinds;

public class Stage extends GameSetup {

	/** Stage relevant */
	private GameMenu gameMenu;
	private Inventory inventory;
	private EditMenu editMenu;
	private HUD hud;
	private Player player;
	private Map map;
	
	private AudioSource bgm;
	
	private World world = new World(new Vec2(0, 0f));
	private Set<Clutter> bodies = new HashSet<Clutter>();
	
	public Stage() {
		Camera.get().setFade(-2.5f);
		Camera.get().frame.setFrame(0, 0, Camera.get().frame.getWidth(), Camera.get().frame.getHeight());
		bgm = new AudioSource("CairnArea4", "BGM");
		
		player = new Player(0, 0, "MC and Familiar");
		loadMap(null, 0, 0);
		//loadMap("Withered Hearthlands", 1380, 1275);
		
		ContactListener boneWorld = new BoneWorld();
		world.setContactListener(boneWorld);
		/*world.setContactFilter(new ContactFilter() {
			public boolean shouldCollide(Fixture fixtureA, Fixture fixtureB) {
				return false;
			}
		});*/
		
		for(Actor a : getCast()) {
			a.setCollisionBox(buildBody(a));
		}
		
		/*BodyDef groundDef = new BodyDef();
		groundDef.position.set(0, 1f);
		groundDef.type = BodyType.STATIC;
		PolygonShape groundShape = new PolygonShape();
		groundShape.setAsBox(1000, 1f);
		Body ground = world.createBody(groundDef);
		FixtureDef groundFixture = new FixtureDef();
		groundFixture.density = 1;
		groundFixture.restitution = 0.3f;
		groundFixture.shape = groundShape;
		ground.createFixture(groundFixture);*/
		//bodies.add(ground);
		
		/*Interactable interactable = new Interactable(100, 0, null);
		interactable.setInteraction(new InteractionAdapter() {
			@Override
			public void playerCollide() {
				Stage.this.loadMap("Withered Hearthlands", 1380, 1275);
			}
		});
		map.addEntity(interactable);*/
		
		//Prop church = Prop.loadProp(100, 100, "ChurchLeft");
		//church.setInteraction("{event: [{showText: 'Congratulations.;You reached the end.'},{showText: 'Press <t+,$key:SLOT8> to restart.'}] }");
		//map.addEntity(church);
		
		//map.addEntity(Prop.loadProp(100, 100, "ChurchLeft"));
		
		//map.addBackdrop(Backdrop.loadBackdrop(0, 0, "Parallax", -0.1f));
		//map.addBackdrop(Backdrop.loadBackdrop(0, 0, "Graveyard", 0));
		
		hud = new HUD(player);
		
		bgm.setPosition(new Vector3f(0, 0, -5f));
		bgm.getAudio().playAsMusic(1f, 1f, true);
		
		AudioSource wind = new AudioSource("WindA", "SFX");
		wind.getAudio().playAsSoundEffect(1f, 1f, true);
				
		//map.getLights().get(0).setParent(player);
		//Camera.get().setRotate(0.25f, 7.5f, 7f);
	}

	@Override
	public void update() {
		if(gameMenu != null) {
			gameMenu.update();
			if(gameMenu.isCloseRequest()) {
				Keybinds.closeMenu();
				gameMenu = null;
			}
		} else {
			if(inventory != null) {
				inventory.update();
			}
			
			if(bgm != null) {
				bgm.update();
			}

			if(hud.isEnabled()) {
				hud.update(this);
			}

			for(int i = 0; i<uiElements.size(); i++) {
				if(uiElements.get(i).isDead()) {
					uiElements.remove(i);
					i--;
					continue;
				}
				uiElements.get(i).update();
			}
			
			for(Clutter c : bodies) {
				c.update();
			}

			player.update();
			if(player.getState() == CharState.DEAD) {
				player.collapse(world, bodies, null);
				//Theater.get().swapSetup(new Stage());
			}

			for(int i = 0; i<map.getCast().size(); i++) {
				Actor a = map.getCast().get(i);
				a.update();
				if(a instanceof Ally) {
					((Ally) a).activateScript(player, this);
				} else {
					if(a.getState() == CharState.DEAD) {
						Vector2f hitForce = new Vector2f();
						Vector2f.sub(new Vector2f(a.getX(), a.getYPlane()),
								new Vector2f(player.getX(), player.getYPlane()), hitForce);
						hitForce.normalise();
						hitForce.scale(200f);
						a.collapse(world, bodies, new Vec2(hitForce.x, hitForce.y));
						//i.remove();
						removeEntity(a);
						i--;
						continue;
					}
					if(a instanceof Enemy) {
						((Enemy) a).think(this);
					}
				}
			}
			
			for(int i = 0; i<map.getProps().size(); i++) {
				map.getProps().get(i).update();
			}

			map.update();
			
			world.step(1 / 60f, 8, 3);
			
			if(Keybinds.EXIT.clicked()) {
				Keybinds.inMenu();
				gameMenu = new GameMenu("Menu2");
			} else if(Keybinds.MENU.clicked()) {
				if(inventory == null) {
					Keybinds.inMenu();
					inventory = new Inventory();
				} else {
					Keybinds.closeMenu();
					inventory = null;
				}
			}

			if(editMenu != null) {
				// TODO Freeze AI and stuff during editing, enable cheats
				editMenu.update();
				if(editMenu.isCloseRequest()) {
					hud.setEnabled(true);
					Keybinds.closeMenu();
					editMenu.close();
					editMenu = null;
					
					// TODO
					/*for(Actor a : map.getCast()) {
						a.setCollisionBox(world);
					}*/
				}
			} else if(Keybinds.EDIT.clicked()) {
				hud.setEnabled(false);
				Keybinds.inMenu();
				editMenu = new EditMenu(this);
			}
		}

		if(Input.mouseClicked() && !map.getPathPolys().isEmpty()) {
			Pathfinder.buildPath(Input.mousePress, player);
		}
	}

	@Override
	public void draw() {		
		for(int x = 0; x<map.getBackground().size(); x++) {
			map.getBackground().get(x).toDraw();
		}

		for(int x = 0; x<map.getGround().size(); x++) {
			map.getGround().get(x).toDraw();
		}

		/*for(LightSource l : map.getLights()) {
			l.draw();
		}*/
		
		//ShadowMap.drawShadows(getCast());
		
		for(Clutter c : bodies) {
			c.draw();
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
		for(int i = 0; i<map.getScenery().size(); i++) {
			map.getScenery().get(i).toDraw();
		//for(Entity e : map.getScenery()) {
			//e.toDraw();
		}

		for(int x = 0; x<map.getForeground().size(); x++) {
			map.getForeground().get(x).toDraw();
		}

		if(map.getFog() != null)
			map.getFog().toDraw();

		/*if(!LightMap.lights.isEmpty()) {
			// Remember to re-init LightMap before using these
			//LightMap.drawFBOMax();
			LightMap.draw();
			//LightMap.drawNoFBO();
		}*/
	}
	
	@Override
	public void drawUI() {
		if(hud.isEnabled()) {
			hud.draw(this);
		}
		
		for(UIElement ui : uiElements) {
			ui.draw();
		}

		if(editMenu != null) {
			editMenu.draw();
		}
		
		if(inventory != null) {
			inventory.draw();
		}
		
		if(gameMenu != null) {
			gameMenu.draw();
		}
	}

	@Override
	public void resizeRefresh() {
	}

	public Map getMap() {
		return map;
	}
	
	public void loadMap(String mapName, float x, float y) {
		if(mapName != null) {
			map = Map.deserialize(mapName);
		} else {
			map = new Map();
		}
		
		player.setPosition(x, y);
		//Camera.get().setFocus(player);
		//Camera.get().centerOn(this);
		map.getScenery().add(player);
		
		Camera.get().setFade(-1.2f);
	}
	
	public HUD getHUD() {
		return hud;
	}
	
	public Player getPlayer() {
		return player;
	}

	/**
	 * @return Cast from map including the player.
	 */
	public LinkedList<Actor> getCast() {
		LinkedList<Actor> cast = new LinkedList<Actor>();
		cast.add(getPlayer());
		cast.addAll(map.getCast());
		return cast;
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
	
	public World getWorld() {
		return world;
	}
	
	public Set<Clutter> getBodies() {
		return bodies;
	}
	
	public Body buildBody(Actor actor) {
		BodyDef boxDef = new BodyDef();
		boxDef.position.set((float) actor.getBox().getCenterX() / 30, 
				(float) (actor.getBox().getMaxY() - (actor.getBox().getHeight() * 0.15f)) / 30);
		boxDef.type = BodyType.DYNAMIC;

		CircleShape boxShape = new CircleShape();
		boxShape.m_radius = (float) (actor.getYPlane() - actor.getPosition().y) / 30 / 2;

		FixtureDef boxFixture = new FixtureDef();
		boxFixture.density = 1f;
		boxFixture.shape = boxShape;

		Body box = world.createBody(boxDef);
		box.createFixture(boxFixture);
		box.setFixedRotation(true);
		box.setLinearDamping(1f);
		
		return box;
	}
	
}
