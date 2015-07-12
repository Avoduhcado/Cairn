package core.scene;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.Line2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedList;

import core.Camera;
import core.scene.collisions.Collidable;
import core.scene.collisions.HitMaps;
import core.scene.collisions.PathPolygon;
import core.scene.collisions.Slope;
import core.utilities.scripts.Script;
import core.entities.Actor;
import core.entities.Ally;
import core.entities.Backdrop;
import core.entities.Enemy;
import core.entities.Entity;
import core.entities.Fog;
import core.entities.Interactable;
import core.entities.LightSource;
import core.entities.Prop;
import core.entities.utils.ai.DocileAI;

public class Map implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String mapName = null;
	
	private ArrayList<Polygon> collisionPolys = new ArrayList<Polygon>();
	private ArrayList<PathPolygon> paths = new ArrayList<PathPolygon>();
	private transient LinkedList<Entity> scenery = new LinkedList<Entity>();
	private LinkedList<Actor> cast = new LinkedList<Actor>();
	private LinkedList<Prop> props = new LinkedList<Prop>();
	private LinkedList<Interactable> interactions = new LinkedList<Interactable>();
	
	private Fog fog;
	private LinkedList<LightSource> lights = new LinkedList<LightSource>();
	
	private LinkedList<Backdrop> background = new LinkedList<Backdrop>();
	private LinkedList<Backdrop> ground = new LinkedList<Backdrop>();
	private LinkedList<Backdrop> foreground = new LinkedList<Backdrop>();
	
	public Map() {
		resetEntity();
		
		//loadBackdrop(0, 0, "Withered Hearthlands", 0f);
		//mapName = "Withered Hearthlands";
		
		/*loadBackdrop(400, 335, "Parallax", -0.1f);
		loadBackdrop(0, 0, "Graveyard", 0f);

		loadProp(190, 480, "Entrance");
		loadProp(333, 823, "Graves");
		loadProp(782, 518, "Posts");
		loadProp(1705, 990, "Cairn");
		loadProp(7688, 830, "Railing");
		
		scenery.addAll(props);

		cast.add(new Ally(8450, 710, "The Fool", Camera.ASPECT_RATIO, new Script("Et tu, Skelebones?", 
				"{event: [{showText: 'Congratulations.;You reached the end.'},{showText: 'Press <t+,$key:SLOT8> to restart.'}] }")));
		cast.add(new Ally(1875, 1000, "Gravedigger", Camera.ASPECT_RATIO, new Script("How curious.", 
				"{event: [{showText: 'Fair tidings, child.;Good to see you returned unharmed.'},"
				+ "{showText: 'Might you care for a release date?'}, {choose: [{option: 'YES!!',result: [{showText: 'Neato'}]},"
				+ "{option: No,result: [{showText: 'Oh'},{showText: 'Ok then...'}]}]},{showText: Goodbye} ] }")));
		cast.getLast().setDirection(1);
		
		cast.add(new Enemy(700, 705, "Flock", Camera.ASPECT_RATIO, new DocileAI()));
		((Enemy) cast.getLast()).getReputation().addAlly(Faction.FLOCK);
		((Enemy) cast.getLast()).getIntelligence().addTrait(new Minion(null));
		cast.add(new Enemy(1850, 570, "Flock 3", Camera.ASPECT_RATIO, new DocileAI()));
		cast.getLast().setDirection(1);
		((Enemy) cast.getLast()).getReputation().addAlly(Faction.FLOCK);
		((Enemy) cast.getLast()).getIntelligence().addTrait(new Minion(null));
		
		cast.add(new Enemy(3020, 410, "Shepherd", Camera.ASPECT_RATIO, new AggressiveAI(0.7f)));
		cast.getLast().setDirection(1);
		cast.getLast().setMaxSpeed(1.6f);
		((Enemy) cast.getLast()).getStats().getHealth().setCurrent(45f);
		((Enemy) cast.getLast()).getIntelligence().addTrait(new PackLeader(null));
		cast.add(new Enemy(2835, 470, "Flock", Camera.ASPECT_RATIO, new AggressiveAI(0.4f)));
		cast.getLast().setDirection(1);
		cast.getLast().setMaxSpeed(1.4f);
		((Enemy) cast.getLast()).getReputation().addAlly(Faction.FLOCK);
		((Enemy) cast.getLast()).getIntelligence().addTrait(new Minion((Intelligent) cast.get(cast.size() - 2)));
		cast.add(new Enemy(2905, 625, "Flock 2", Camera.ASPECT_RATIO, new AggressiveAI(0.4f)));
		cast.getLast().setDirection(1);
		cast.getLast().setMaxSpeed(1.4f);
		((Enemy) cast.getLast()).getReputation().addAlly(Faction.FLOCK);
		((Enemy) cast.getLast()).getIntelligence().addTrait(new Minion((Intelligent) cast.get(cast.size() - 3)));
		
		cast.add(new Enemy(3830, 805, "Acolyte", Camera.ASPECT_RATIO, new AggressiveAI(0.8f)));
		cast.getLast().setDirection(1);
		cast.getLast().setMaxSpeed(1.8f);
		((Enemy) cast.getLast()).getStats().getHealth().setCurrent(50f);
		((Enemy) cast.getLast()).getIntelligence().addTrait(new Opportunist(0.5f));
		
		cast.add(new Enemy(4900, 750, "Shepherd", Camera.ASPECT_RATIO, new AggressiveAI(0.7f)));
		cast.getLast().setMaxSpeed(1.6f);
		((Enemy) cast.getLast()).getStats().getHealth().setCurrent(40f);
		cast.add(new Enemy(5160, 775, "Shepherd", Camera.ASPECT_RATIO, new AggressiveAI(0.7f)));
		cast.getLast().setDirection(1);
		cast.getLast().setMaxSpeed(1.6f);
		((Enemy) cast.getLast()).getStats().getHealth().setCurrent(40f);
		
		cast.add(new Enemy(6000, 625, "Acolyte_2", Camera.ASPECT_RATIO, new AggressiveAI(0.825f)));
		cast.getLast().setMaxSpeed(1.75f);
		cast.getLast().setDirection(1);
		((Enemy) cast.getLast()).getStats().getHealth().setCurrent(50f);
		cast.add(new Enemy(6180, 630, "Acolyte_2", Camera.ASPECT_RATIO, new AggressiveAI(0.825f)));
		cast.getLast().setMaxSpeed(1.75f);
		cast.getLast().setDirection(1);
		((Enemy) cast.getLast()).getStats().getHealth().setCurrent(50f);
		
		scenery.addAll(cast);*/
		
		//fog = new Fog("Fog", 0.5f, 0.75f, new Vector2f(-1f, 0f));
		//fog.setOpacity(0.5f);
		
		//lights.add(new LightSource(1580, 1580, null, 1f, new Vector2f(250, 205), new Vector3f(1f, 1f, 1f)));
		//lights.add(new LightSource(415, 630, null, 1f, new Vector2f(150, 120), new Vector3f(1f, 0.225f, 0.1f)));
		
		/*LightMap.init();
		LightMap.lights = lights;
		LightMap.background = new Vector4f(0f,0f,0f,1f);*/
	}
	
	public void serialize() {
		try (FileOutputStream fileOut = new FileOutputStream(System.getProperty("resources") + "/maps/" + getMapName() + ".avo");
				ObjectOutputStream out = new ObjectOutputStream(fileOut)){
			out.writeObject(this);
			System.out.println("Serialized data is saved in /maps/" + getMapName() + ".avo");
		} catch(IOException i) {
			i.printStackTrace();
		}
	}
	
	public static Map deserialize(String mapName) {
		Map map = new Map();
		try(FileInputStream fileIn = new FileInputStream(System.getProperty("resources") + "/maps/" + mapName + ".avo");
				ObjectInputStream in = new ObjectInputStream(fileIn)) {
			map = (Map) in.readObject();
		} catch(IOException i) {
			i.printStackTrace();
		} catch(ClassNotFoundException c) {
			System.out.println("Map class not found");
			c.printStackTrace();
		}
				
		return map;
	}
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		
		if(mapName.endsWith(".avo")) {
			mapName = mapName.replace(".avo", "");
		}
		
		fillScenery();
		
		/*LightMap.init();
		LightMap.lights = lights;
		LightMap.background = new Vector4f(0f,0f,0f,1f);*/
		
		buildCollisions();
		//HitMaps.populateMap(HitMaps.getCollisionMap(), platforms);
	}
	
	public void update() {
		if(fog != null) {
			fog.update();
		}
		
		/*for(LightSource l : lights) {
			l.update();
		}*/
		
		for(Backdrop b : background) {
			b.update();
		}
		for(Backdrop b : foreground) {
			b.update();
		}
		
		if(interactions != null) {
			for(Interactable i : interactions) {
				i.update();
			}
		}
	}
	
	public ArrayList<Polygon> getCollisionPolys() {
		return collisionPolys;
	}
	
	public void setCollisionPolys(ArrayList<Polygon> polygons) {
		this.collisionPolys = polygons;
		//KongAlgo kong = new KongAlgo(polygons.get(0));
	}
	
	public ArrayList<PathPolygon> getPathPolys() {
		return paths;
	}
	
	public void setPathPolys(ArrayList<PathPolygon> polygons) {
		this.paths = polygons;
	}
	
	public void buildCollisions() {
		HitMaps.getCollisionMap().clear();
		ArrayList<Collidable> walls = new ArrayList<Collidable>();
		
		for(Polygon p : collisionPolys) {
			if(p.npoints > 1) {
				for(int i = 0; i<p.npoints; i++) {
					if(i == p.npoints - 1)
						walls.add(new Slope(new Line2D.Double(p.xpoints[i], p.ypoints[i], p.xpoints[0], p.ypoints[0])));
					else
						walls.add(new Slope(new Line2D.Double(p.xpoints[i], p.ypoints[i], p.xpoints[i+1], p.ypoints[i+1])));
				}
			}
		}
		
		HitMaps.populateMap(HitMaps.getCollisionMap(), walls);
	}
	
	public Entity getEntity(Entity entity) {
		if(scenery.contains(entity)) {
			if(entity instanceof Prop) {
				return props.get(props.indexOf(entity));
			} else if(entity instanceof Actor) {
				return cast.get(cast.indexOf(entity));
			} else if(entity instanceof Interactable) {
				return interactions.get(interactions.indexOf(entity));
			}
		} else if(entity instanceof Backdrop) {
			if(background.contains(entity)) {
				return background.get(background.indexOf(entity));
			} else if(ground.contains(entity)) {
				return ground.get(ground.indexOf(entity));
			} else if(foreground.contains(entity)) {
				return foreground.get(foreground.indexOf(entity));
			}
		} else if(entity instanceof Interactable) {
			return interactions.get(interactions.indexOf(entity));
		}
		
		return null;
	}
	
	public boolean removeEntity(Entity entity) {
		if(scenery.contains(entity)) {
			scenery.remove(entity);
			if(entity instanceof Prop) {
				return props.remove(entity);
			} else if(entity instanceof Actor) {
				return cast.remove(entity);
			}
		} else if(entity instanceof Backdrop) {
			if(background.contains(entity)) {
				return background.remove(entity);
			} else if(ground.contains(entity)) {
				return ground.remove(entity);
			} else if(foreground.contains(entity)) {
				return foreground.remove(entity);
			}
			return false;
		}
		
		return false;
	}

	public LinkedList<Prop> loadProp(int x, int y, String prop) {
		File propDirectory = new File(System.getProperty("resources") + "/sprites/props/" + prop);
		Dimension size = new Dimension();
		LinkedList<Prop> loadedProps = new LinkedList<Prop>();
		
		if(propDirectory.exists() && propDirectory.isDirectory()) {
			byte[] data = decodeAVLFile(new File(propDirectory.getAbsolutePath() + "/" + prop + ".avl"));
			size.width = ByteBuffer.wrap(data, 0, 4).getInt();
			size.height = ByteBuffer.wrap(data, 4, 4).getInt();
			
			String[] propNames = propDirectory.list();
			for(String n : propNames) {
				if(n.endsWith(".png")) {
					n = n.split(".png")[0];
					String loc = n.substring(n.lastIndexOf('[') + 1, n.lastIndexOf(']'));
					Point coord = new Point(Integer.parseInt(loc.split(",")[0]), Integer.parseInt(loc.split(",")[1]));
					/*props.add(new Prop(
							(int) Math.floor(((coord.y * SpriteIndex.getSprite(prop + "/" + n).getWidth()) * Camera.ASPECT_RATIO) + x),
							(int) Math.floor(((coord.x * SpriteIndex.getSprite(prop + "/" + n).getHeight()) * Camera.ASPECT_RATIO) + y),
							prop + "/" + n, Camera.ASPECT_RATIO));*/
					loadedProps.add(new Prop((int) Math.floor(((coord.y * size.width) * Camera.ASPECT_RATIO) + x),
							(int) Math.floor(((coord.x * size.height) * Camera.ASPECT_RATIO) + y), size.width, size.height, prop + "/" + n));
				}
			}
		} else {
			System.out.println(prop + " needs to be converted to a directory!");
			loadedProps.add(new Prop(x, y, prop, Camera.ASPECT_RATIO));
		}
		
		props.addAll(loadedProps);
		scenery.addAll(loadedProps);
		
		return loadedProps;
	}

	public LinkedList<Actor> loadActor(int x, int y, String actor, int type) {
		LinkedList<Actor> loadedActors = new LinkedList<Actor>();
		
		switch(type) {
		case 0:
			loadedActors.add(new Ally(x, y, actor, new Script("Hello", "{event: []}")));
			break;
		case 1:
			loadedActors.add(new Enemy(x, y, actor, new DocileAI()));
			break;
		default:
			break;
		}
		
		cast.addAll(loadedActors);
		scenery.addAll(loadedActors);
		
		return loadedActors;
	}
	
	public LinkedList<Backdrop> loadBackdrop(int x, int y, String backdrop, float depth) {
		File backdropDirectory = new File(System.getProperty("resources") + "/sprites/backdrops/" + backdrop);
		Dimension size = new Dimension();
		LinkedList<Backdrop> backdrops = new LinkedList<Backdrop>();
		
		if(backdropDirectory.exists() && backdropDirectory.isDirectory()) {
			byte[] data = decodeAVLFile(new File(backdropDirectory.getAbsolutePath() + "/" + backdrop + ".avl"));
			size.width = ByteBuffer.wrap(data, 0, 4).getInt();
			size.height = ByteBuffer.wrap(data, 4, 4).getInt();
			
			String[] backdropNames = backdropDirectory.list();
			for(String n : backdropNames) {
				if(n.endsWith(".png")) {
					n = n.split(".png")[0];
					String loc = n.substring(n.lastIndexOf('[') + 1, n.lastIndexOf(']'));
					Point coord = new Point(Integer.parseInt(loc.split(",")[0]), Integer.parseInt(loc.split(",")[1]));
	
					/*addBackdrop(new Backdrop((int) Math.floor(((coord.y * size.width) * Camera.ASPECT_RATIO) + x),
							(int) Math.floor(((coord.x * size.height) * Camera.ASPECT_RATIO) + y), size.width, size.height,
							backdrop + "/" + n, depth));*/
					backdrops.add(new Backdrop((int) Math.floor(((coord.y * size.width) * Camera.ASPECT_RATIO) + x),
							(int) Math.floor(((coord.x * size.height) * Camera.ASPECT_RATIO) + y), size.width, size.height,
							backdrop + "/" + n, depth));
				}
			}
		} else {
			System.out.println(backdrop + " needs to be converted to a directory!");
			addBackdrop(new Backdrop(x, y, backdrop, Camera.ASPECT_RATIO, depth));
		}
		
		for(int i = 0; i<backdrops.size(); i++) {
			backdrops.get(i).setID(backdrops.get(i).getID() + backdrops.get(i).getName() + i);
			addBackdrop(backdrops.get(i));
		}
		
		return backdrops;
	}

	private void addBackdrop(Backdrop backdrop) {
		if(backdrop.getDepth() < 0f) {
			if(background.isEmpty()) {
				background.add(backdrop);
				return;
			}
			for(int x = 0; x<background.size(); x++) {
				if(backdrop.getDepth() <= background.get(x).getDepth()) {
					background.add(x, backdrop);
					return;
				}
			}
		} else if(backdrop.getDepth() > 0f){
			if(foreground.isEmpty()) {
				foreground.add(backdrop);
				return;
			}
			for(int x = 0; x<foreground.size(); x++) {
				if(backdrop.getDepth() <= foreground.get(x).getDepth()) {
					foreground.add(x, backdrop);
					return;
				}
			}
		} else {
			ground.add(backdrop);
		}
	}
	
	private byte[] decodeAVLFile(File avl) {
		byte[] byteArray = null;
		try (FileInputStream fis = new FileInputStream(avl)) {
			byteArray = new byte[fis.available()];
			fis.read(byteArray);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return byteArray;
	}
	
	public void fillScenery() {
		scenery = new LinkedList<Entity>();
		
		scenery.addAll(cast);
		scenery.addAll(props);
		if(interactions == null) {
			interactions = new LinkedList<Interactable>();
		} else {
			//scenery.addAll(interactions);
		}
		
		Entity.count = scenery.size();
		// TODO Set proper Entity ID counts after deserialization
		// TODO Add the rest of the scenery
	}
	
	public String getMapName() {
		return mapName;
	}

	public void setMapName(String mapName) {
		this.mapName = mapName;
	}

	public LinkedList<Backdrop> getBackground() {
		return background;
	}
	
	public LinkedList<Backdrop> getGround() {
		return ground;
	}
	
	public LinkedList<Backdrop> getForeground() {
		return foreground;
	}
	
	public LinkedList<Entity> getScenery() {
		return scenery;
	}

	public LinkedList<Actor> getCast() {
		return cast;
	}

	public LinkedList<Prop> getProps() {
		return props;
	}
	
	public LinkedList<LightSource> getLights() {
		return lights;
	}
	
	public Fog getFog() {
		return fog;
	}
	
	// TODO
	public void addFog(Fog fog) {
		this.fog = fog;
	}
	
	public void resetEntity() {
		Entity.count = 0;
	}

	public void addEntity(Entity entity) {
		if(entity instanceof Prop) {
			props.add((Prop) entity);
		} else if(entity instanceof Actor) {
			cast.add((Actor) entity);
		} else if(entity instanceof Interactable) {
			interactions.add((Interactable) entity);
		}
		
		scenery.add(entity);
	}

	public LinkedList<Interactable> getInteractions() {
		return interactions;
	}
	
}
