package core.setups;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import org.jbox2d.callbacks.QueryCallback;
import org.jbox2d.collision.AABB;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.World;
import org.lwjgl.util.vector.Vector4f;

import core.Camera;
import core.entities_new.Entity;
import core.entities_new.components.Combatant;
import core.entities_new.components.Inventory;
import core.entities_new.components.controllers.FollowController;
import core.entities_new.components.controllers.PlayerController;
import core.entities_new.components.interactions.ActivateInteraction;
import core.entities_new.components.interactions.Script;
import core.entities_new.event.InteractEvent;
import core.entities_new.utils.BodyData;
import core.entities_new.utils.BodyLoader;
import core.entities_new.utils.CombatLoader;
import core.entities_new.utils.SensorData;
import core.scene.BoneWorld;
import core.scene.ShadowMap;
import core.swing.EntityDisplay;
import core.ui.event.MouseEvent;
import core.ui.event.MouseListener;
import core.ui.event.UIEvent;
import editor.EditableScene;

public class Stage extends GameSetup implements WorldContainer, EditableScene {

	public static final float SCALE_FACTOR = 30f;

	private ArrayList<Entity> background = new ArrayList<Entity>();
	private ArrayList<Entity> entities = new ArrayList<Entity>();
	private World world = new World(new Vec2(0, 15f));

	private BoneWorld boneWorld;
	
	private ArrayList<Entity> queuedEntities = new ArrayList<Entity>();
	private ArrayList<Entity> entitiesToRemove = new ArrayList<Entity>();
	
	private MouseListener mouseListener;
	
	private Point2D start;

	public Stage() {
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

		addPlayer(new Entity("Skelebones", new BodyData(490, 150, BodyLoader.PLAIN_ENTITY), this));

		Entity dad = new Entity("Skull", new BodyData(490, 150, BodyLoader.FLOATING_ENTITY), this);
		dad.setController(new FollowController(dad, getPlayer()));
		addEntity(dad);
		ShadowMap.get().addIllumination(dad, new Point(0, -105), 300f);
		
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

		Entity collector = new Entity("Collector", new BodyData(760, 160, BodyLoader.PLAIN_ENTITY), this);
		//collector.getBody().setGravityScale(2f);
		//collector.getBody().setLinearDamping(1f);
		//collector.getZBody().setGroundZ(455);
		//collector.addCombatListener(CombatLoader.plainCombatant());
		collector.addComponent(Combatant.class, CombatLoader.plainCombatant());
		//collector.addComponent(AutorunInteraction.class, new AutorunInteraction(collector, new Script(collector, "")));
		//collector.addComponent(TouchInteraction.class, new TouchInteraction(collector, new Script(collector, "")));
		collector.addComponent(ActivateInteraction.class, new ActivateInteraction(collector, new Script(collector, "")));
		//addEntity(collector);

		Entity digger = new Entity("Gravedigger", new BodyData(700, 160, BodyLoader.PLAIN_ENTITY), this);
		digger.addComponent(ActivateInteraction.class, new ActivateInteraction(digger, new Script(digger, "")));
		addEntity(digger);
		
		Entity light = new Entity("Hanging Light", new BodyData(990, 70, BodyLoader.FLOATING_ENTITY), this);
		addEntity(light);
		ShadowMap.get().addIllumination(light, null, 165f);

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
		
		mouseListener = new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				world.queryAABB(new QueryCallback() {
					@Override
					public boolean reportFixture(Fixture fixture) {
						SensorData data = (SensorData) fixture.m_userData;
						EntityDisplay entityDisplay = new EntityDisplay(data.getEntity());
						entityDisplay.setVisible(true);
						return false;
					}
				}, new AABB(new Vec2(Camera.get().getScreenMouseX() / SCALE_FACTOR, Camera.get().getScreenMouseY() / SCALE_FACTOR),
						new Vec2((Camera.get().getScreenMouseX() + 1) / SCALE_FACTOR, (Camera.get().getScreenMouseY() + 1) / SCALE_FACTOR)));
			}

			@Override
			public void mousePressed(MouseEvent e) {
				//start = new Point2D.Double(Camera.get().getScreenMouseX(), Camera.get().getScreenMouseY());
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				/*Point2D end = new Point2D.Double(Camera.get().getScreenMouseX(), Camera.get().getScreenMouseY());
				queueEntity(new Entity("Wall", new BodyData((float) start.getX(), (float) start.getY(),
						(float) (end.getX() - start.getX()), (float) (end.getY() - start.getY()), BodyLoader.WALL), Stage.this),
						true);*/
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}
		};
	}
	
	@Override
	public void update() {
		world.step(1 / 60f, 8, 3);

		for(Entity e : entities) {
			if(e.controller()) {
				e.getController().control();
			}
			e.updateBodyAndState();
		}
		
		queuedOperations();
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

		ShadowMap.get().drawIllumination();
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
	public void queueEntity(Entity entity, boolean add) {
		if(add) {
			queuedEntities.add(entity);
		} else {
			entitiesToRemove.add(entity);
		}
	}

	private void queuedOperations() {
		cleanUpEntities();
		addQueuedEntities();
	}

	private void addEntity(Entity entity) {
		entities.add(entity);
	}

	private boolean removeEntity(Entity entity) {
		return entities.remove(entity);
	}
	
	private void cleanUpEntities() {
		if(entitiesToRemove.isEmpty()) {
			return;
		}
		
		for(Entity e : entitiesToRemove) {
			removeEntity(e);
		}
		entitiesToRemove.clear();
	}
	
	private void addQueuedEntities() {
		if(queuedEntities.isEmpty()) {
			return;
		}
		
		for(Entity e : queuedEntities) {
			entities.add(e);
		}
		queuedEntities.clear();
	}

	public Entity getPlayer() {
		return entities.stream()
				.filter(e -> e.controller() && e.getController() instanceof PlayerController)
				.findFirst().get();
	}

	/** TODO
	 * Is this necessary? Could be useful for ensuring there's a single player character.
	 * @param player
	 */
	public void addPlayer(Entity player) {
		player.setController(new PlayerController(player));
		player.addComponent(Inventory.class, new Inventory(player));
		addEntity(player);
		
		Camera.get().setFocus(player);
	}
	
	@Override
	public void fireEvent(UIEvent e) {
		super.fireEvent(e);
		
		if(e instanceof MouseEvent) {
			processMouseEvent((MouseEvent) e);
		}
	}
	
	protected void processMouseEvent(MouseEvent event) {
		if(mouseListener != null) {
			switch(event.getEvent()) {
			case MouseEvent.CLICKED:
				mouseListener.mouseClicked(event);
				break;
			case MouseEvent.PRESSED:
				mouseListener.mousePressed(event);
				break;
			case MouseEvent.RELEASED:
				mouseListener.mouseReleased(event);
				break;
			case MouseEvent.MOVED:
				//getPlayer().getBody().applyForceToCenter(new Vec2(event.getDx(), event.getDy()).mul(15));
				break;
			default:
				break;
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		System.out.println("SDsdfsdjkfklj");
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}
