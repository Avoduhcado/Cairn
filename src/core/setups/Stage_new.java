package core.setups;

import java.awt.Point;
import java.util.ArrayList;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.lwjgl.util.vector.Vector4f;

import core.Camera;
import core.entities_new.Entity;
import core.entities_new.EntityData;
import core.entities_new.components.FollowController;
import core.entities_new.components.PlayerController;
import core.entities_new.utils.CombatLoader;
import core.entities_new.utils.SensorData;
import core.inventory.Equipment;
import core.inventory.Weapon;
import core.scene.BoneWorld;
import core.scene.ShadowMap;

public class Stage_new extends GameSetup implements WorldContainer {
	
	private ArrayList<Entity> background = new ArrayList<Entity>();
	private ArrayList<Entity> entities = new ArrayList<Entity>();
	private World world = new World(new Vec2(0, 15f));
	
	private ArrayList<EntityData> queuedEntities = new ArrayList<EntityData>();
	
	public static final float SCALE_FACTOR = 30f;
		
	public Stage_new() {
		Camera.get().setFade(-2.5f);
		Camera.get().frame.setFrame(0, 0, Camera.get().frame.getWidth(), Camera.get().frame.getHeight());
		
		ShadowMap.init();
		
		BoneWorld boneWorld = new BoneWorld();
		boneWorld.setContainer(this);
		world.setContactListener(boneWorld);
		
		Entity dream = new Entity("Test Land", 0, 0, this);
		//Entity dream = new Entity("Ruined Sepulcher", 0, 0, this);
		dream.getBody().setType(BodyType.STATIC);
		dream.getBody().getFixtureList().getFilterData().categoryBits = 0;
		// TODO addBackground function
		background.add(dream);
		
		Camera.get().setFillColor(new Vector4f(0, 0, 0, 1));
		
		Entity player = new Entity("Skelebones", 495, 450, this);
		player.setController(new PlayerController(player));
		addEntity(player);
		
		Entity dad = new Entity("Skull",
				player.getBody().getPosition().x * Stage_new.SCALE_FACTOR,
				player.getBody().getPosition().y * Stage_new.SCALE_FACTOR,
				player.getContainer());
		dad.setController(new FollowController(dad, player));
		Equipment equipment = new Equipment(dad);
		Weapon weapon = new Weapon("001", "Light Mace");
		weapon.setAnimation("LightAttack");
		equipment.addWeapon(weapon);
		weapon = new Weapon("002", "Heavy Mace");
		weapon.setAnimation("HeavyAttack");
		equipment.addWeapon(weapon);
		weapon = new Weapon("003", "Polearm");
		weapon.setAnimation("ThrustAttack");
		equipment.addWeapon(weapon);
		dad.setEquipment(equipment);
		for(Fixture f = dad.getBody().getFixtureList(); f != null; f = f.getNext()) {
			f.getFilterData().categoryBits = 0;
		}
		dad.getZBody().setZ(player.getBody().getPosition().y);
		//addEntity(dad);
		//ShadowMap.get().addIllumination(dad, new Point(0, -105), 500f);
		
		//Entity shp = new Entity("Shepherd", 900, 455, this);
		//((SpineRender) shp.getRender()).getSkeleton().findSlot("CROOK").setAttachment(null);
		//addEntity(shp);
		
		Entity collector = new Entity("Collector", 775, -455, this);
		collector.getBody().setGravityScale(2f);
		collector.getBody().setLinearDamping(1f);
		collector.getZBody().setGroundZ(455);
		collector.addCombatListener(CombatLoader.plainCombatant());
		//addEntity(collector);
		
		Entity light = new Entity("Hanging Light", 690, 185, this);
		//light.getBody().setType(BodyType.STATIC);
		light.getBody().getFixtureList().getFilterData().categoryBits = 0;
		//addEntity(light);
		//ShadowMap.get().addIllumination(light, null, 225f);
		
		/*Entity wall = new Entity(null, 0, 300, this);
		{
			BodyDef bodyDef = new BodyDef();
			bodyDef.position.set(0 / Stage_new.SCALE_FACTOR, 300 / Stage_new.SCALE_FACTOR);
			bodyDef.type = BodyType.STATIC;

			EdgeShape bodyShape = new EdgeShape();
			bodyShape.set(new Vec2(0, 0), new Vec2(400f / Stage_new.SCALE_FACTOR, 0));

			FixtureDef boxFixture = new FixtureDef();
			boxFixture.density = 1f;
			boxFixture.shape = bodyShape;
			
			Body body = world.createBody(bodyDef);
			body.createFixture(boxFixture);
			wall.setBody(body);
		}
		addEntity(wall);
		wall = new Entity(null, 100, 100, this);
		{
			BodyDef bodyDef = new BodyDef();
			bodyDef.position.set(100 / Stage_new.SCALE_FACTOR, 100 / Stage_new.SCALE_FACTOR);
			bodyDef.type = BodyType.STATIC;

			EdgeShape bodyShape = new EdgeShape();
			bodyShape.set(new Vec2(0, 0), new Vec2(400f / Stage_new.SCALE_FACTOR, 1000f / Stage_new.SCALE_FACTOR));

			FixtureDef boxFixture = new FixtureDef();
			boxFixture.density = 1f;
			boxFixture.shape = bodyShape;
			
			Body body = world.createBody(bodyDef);
			body.createFixture(boxFixture);
			wall.setBody(body);
		}
		addEntity(wall);*/
		
		Entity ground = null;
		{
			BodyDef bodyDef = new BodyDef();
			bodyDef.position.set(100f / Stage_new.SCALE_FACTOR, 100f / Stage_new.SCALE_FACTOR);
			bodyDef.type = BodyType.STATIC;

			PolygonShape bodyShape = new PolygonShape();
			bodyShape.setAsBox(50f / Stage_new.SCALE_FACTOR, 50f / Stage_new.SCALE_FACTOR);

			FixtureDef boxFixture = new FixtureDef();
			boxFixture.density = 1f;
			boxFixture.shape = bodyShape;
			boxFixture.isSensor = true;
			
			Body body = world.createBody(bodyDef);
			body.createFixture(boxFixture);
			ground = new Entity(null, body, this);
			ground.getBody().setUserData(new SensorData(ground, SensorData.GROUND));
		}
		addEntity(ground);
		
		{
			BodyDef bodyDef = new BodyDef();
			bodyDef.position.set(100f / Stage_new.SCALE_FACTOR, 550f / Stage_new.SCALE_FACTOR);
			bodyDef.type = BodyType.STATIC;

			PolygonShape bodyShape = new PolygonShape();
			bodyShape.setAsBox(500f / Stage_new.SCALE_FACTOR, 50f / Stage_new.SCALE_FACTOR);

			FixtureDef boxFixture = new FixtureDef();
			boxFixture.density = 1f;
			boxFixture.shape = bodyShape;
			boxFixture.isSensor = true;
			
			Body body = world.createBody(bodyDef);
			body.createFixture(boxFixture);
			ground = new Entity(null, body, this);
			ground.getBody().setUserData(new SensorData(ground, SensorData.GROUND));
		}
		addEntity(ground);
		
		Camera.get().setFocus(player);
	}

	@Override
	public void update() {
		world.step(1 / 60f, 8, 3);
		
		if(!queuedEntities.isEmpty()) {
			for(EntityData e : queuedEntities) {
				entities.add(e.createEntity());
			}
			queuedEntities.clear();
		}
		
		for(int i = 0; i<entities.size(); i++) {
			entities.get(i).update();
		}
	}

	@Override
	public void draw() {
		for(int i = 0; i<background.size(); i++) {
			background.get(i).draw();
		}
		
		ShadowMap.get().drawShadows(entities);
		
		entities.sort((o1, o2) -> (int) (o1.getZBody().getScreenY() - o2.getZBody().getScreenY()));
		for(int i = 0; i<entities.size(); i++) {
			entities.get(i).draw();
		}
		
		//ShadowMap.get().drawIllumination();
	}

	@Override
	public void drawUI() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resizeRefresh() {
		// TODO Auto-generated method stub

	}

	@Override
	public World getWorld() {
		return world;
	}

	@Override
	public ArrayList<Entity> getEntities() {
		return entities;
	}

	@Override
	public void addEntity(Entity entity) {
		entities.add(entity);
	}

	@Override
	public boolean removeEntity(Entity entity) {
		return entities.remove(entity);
	}
	
	public void addPlayer(Entity player) {
		entities.add(player);
		// TODO Link assorted player controllers
	}

	@Override
	public void queueEntity(EntityData entityData) {
		queuedEntities.add(entityData);
	}

}
