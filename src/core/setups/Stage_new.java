package core.setups;

import java.util.ArrayList;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.lwjgl.util.vector.Vector4f;

import core.Camera;
import core.entities_new.Entity;
import core.entities_new.EntityData;
import core.entities_new.components.ActivateInteraction;
import core.entities_new.components.AutorunInteraction;
import core.entities_new.components.Combatant;
import core.entities_new.components.Inventory;
import core.entities_new.components.PlayerController;
import core.entities_new.components.Script;
import core.entities_new.components.TouchInteraction;
import core.entities_new.event.InteractEvent;
import core.entities_new.utils.BodyData;
import core.entities_new.utils.BodyLoader;
import core.entities_new.utils.CombatLoader;
import core.entities_new.utils.SensorData;
import core.scene.BoneWorld;
import core.scene.ShadowMap;

public class Stage_new extends GameSetup implements WorldContainer {

	private ArrayList<Entity> background = new ArrayList<Entity>();
	private ArrayList<Entity> entities = new ArrayList<Entity>();
	private World world = new World(new Vec2(0, 15f));

	private BoneWorld boneWorld;
	private ArrayList<EntityData> queuedEntities = new ArrayList<EntityData>();

	public static final float SCALE_FACTOR = 30f;

	public Stage_new() {
		Camera.get().setFade(-2.5f);
		Camera.get().frame.setFrame(0, 0, Camera.get().frame.getWidth(), Camera.get().frame.getHeight());

		ShadowMap.init();

		boneWorld = new BoneWorld();
		boneWorld.setContainer(this);
		world.setContactListener(boneWorld);

		Entity dream = new Entity("Test Land", new BodyData(), this);
		//Entity dream = new Entity("Ruined Sepulcher", 0, 0, this);
		/** TODO addBackground function
		 * Create a body specific for backgrounds to pass in rather than do this
		 */
		background.add(dream);

		Camera.get().setFillColor(new Vector4f(0, 0, 0, 1));

		addPlayer(new Entity("Skelebones", new BodyData(495, 450, BodyLoader.PLAIN_ENTITY), this));

		/*Entity dad = new Entity("Skull",
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
		dad.getZBody().setWalkThrough(true);
		addEntity(dad);
		ShadowMap.get().addIllumination(dad, new Point(0, -105), 500f);*/

		//Entity shp = new Entity("Shepherd", 900, 455, this);
		//((SpineRender) shp.getRender()).getSkeleton().findSlot("CROOK").setAttachment(null);
		//addEntity(shp);

		Entity collector = new Entity("Collector", new BodyData(575, 455, BodyLoader.PLAIN_ENTITY), this);
		//collector.getBody().setGravityScale(2f);
		//collector.getBody().setLinearDamping(1f);
		//collector.getZBody().setGroundZ(455);
		//collector.addCombatListener(CombatLoader.plainCombatant());
		collector.addComponent(Combatant.class, CombatLoader.plainCombatant());
		//collector.addComponent(AutorunInteraction.class, new AutorunInteraction(collector, new Script(collector, "")));
		collector.addComponent(TouchInteraction.class, new TouchInteraction(collector, new Script(collector, "")));
		collector.addComponent(ActivateInteraction.class, new ActivateInteraction(collector, new Script(collector, "")));
		addEntity(collector);

		Entity light = new Entity("Hanging Light", new BodyData(690, 185, BodyLoader.FLOATING_ENTITY), this);
		addEntity(light);
		ShadowMap.get().addIllumination(light, null, 225f);

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

		addEntity(new Entity("Ground1", new BodyData(100f, 100f, 50f, 50f, BodyLoader.GROUND), this));
		addEntity(new Entity("Ground2", new BodyData(100f, 550f, 500f, 50f, BodyLoader.GROUND), this));
		
		// TODO Put inside of a loadlevel function
		for(Entity e : entities) {
			e.fireEvent(new InteractEvent(InteractEvent.AUTORUN, null));
		}
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
		
		entities.stream()
			.filter(e -> e.controller())
			.map(e -> e.getController())
			.forEach(e -> e.control());

		entities.stream()
			.forEach(e -> e.updateBodyAndState());
		
		uiElements.stream().forEach(e -> e.update());
	}

	@Override
	public void draw() {
		for(int i = 0; i<background.size(); i++) {
			background.get(i).draw();
		}

		ShadowMap.get().drawShadows(entities);

		entities.stream()
			.sorted()
			.forEach(e -> e.draw());

		/*boneWorld.weaponVsBone.stream()
		.forEach(e -> DrawUtils.fillRect(1, 0, 1, 1,
				new Rectangle2D.Double(e.getFixtureA().getBody().getPosition().mul(Stage_new.SCALE_FACTOR).x,
						e.getFixtureA().getBody().getPosition().mul(Stage_new.SCALE_FACTOR).y, 15, 15)));*/

		//ShadowMap.get().drawIllumination();
	}

	@Override
	public void drawUI() {
		for(int x = 0; x<uiElements.size(); x++) {
			getElement(x).draw();
		}
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

	/** TODO
	 * Is this necessary? Could be useful for insuring there's a single player character.
	 * @param player
	 */
	public void addPlayer(Entity player) {
		player.setController(new PlayerController(player));
		player.addComponent(Inventory.class, new Inventory(player));
		addEntity(player);
		
		Camera.get().setFocus(player);
	}

	@Override
	public void queueEntity(EntityData entityData) {
		queuedEntities.add(entityData);
	}

}
