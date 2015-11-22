package core.setups;

import java.util.ArrayList;

import org.jbox2d.collision.shapes.EdgeShape;
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
import core.entities_new.PlayerController;
import core.entities_new.SensorData;
import core.entities_new.SensorType;
import core.entities_new.SpineRender;
import core.scene.BoneWorld;
import core.scene.ShadowMap;

public class Stage_new extends GameSetup implements WorldContainer {
	
	private ArrayList<Entity> background = new ArrayList<Entity>();
	private ArrayList<Entity> entities = new ArrayList<Entity>();
	private World world = new World(new Vec2(0, 9.8f));
	
	public Stage_new() {
		Camera.get().setFade(-2.5f);
		Camera.get().frame.setFrame(0, 0, Camera.get().frame.getWidth(), Camera.get().frame.getHeight());
		
		ShadowMap.init();
		
		world.setContactListener(new BoneWorld());
		
		Entity dream = new Entity("Ruined Sepulcher", 0, 0, this);
		dream.getBody().setType(BodyType.STATIC);
		dream.getBody().getFixtureList().getFilterData().categoryBits = 0;
		background.add(dream);
		
		Camera.get().setFillColor(new Vector4f(0, 0, 0, 1));
		
		Entity player = new Entity("MC and Familiar", 495, 450, this);
		player.setController(new PlayerController(player, true));
		entities.add(player);
		
		Entity shp = new Entity("Shepherd", 900, 455, this);
		//((SpineRender) shp.getRender()).getSkeleton().findSlot("CROOK").setAttachment(null);
		//entities.add(shp);
		
		Entity light = new Entity("Hanging Light", 690, 185, this);
		light.getBody().setType(BodyType.STATIC);
		light.getBody().getFixtureList().getFilterData().categoryBits = 0;
		entities.add(light);
		ShadowMap.get().addIllumination(light, null, 225f);
		
		Entity wall = new Entity(null, 0, 300, this);
		{
			BodyDef bodyDef = new BodyDef();
			bodyDef.position.set(0 / 30f, 300 / 30f);
			bodyDef.type = BodyType.STATIC;

			EdgeShape bodyShape = new EdgeShape();
			bodyShape.set(new Vec2(0, 0), new Vec2(400f / 30f, 0));

			FixtureDef boxFixture = new FixtureDef();
			boxFixture.density = 1f;
			boxFixture.shape = bodyShape;
			
			Body body = world.createBody(bodyDef);
			body.createFixture(boxFixture);
			wall.setBody(body);
		}
		entities.add(wall);
		wall = new Entity(null, 100, 100, this);
		{
			BodyDef bodyDef = new BodyDef();
			bodyDef.position.set(100 / 30f, 100 / 30f);
			bodyDef.type = BodyType.STATIC;

			EdgeShape bodyShape = new EdgeShape();
			bodyShape.set(new Vec2(0, 0), new Vec2(400f / 30f, 1000f / 30f));

			FixtureDef boxFixture = new FixtureDef();
			boxFixture.density = 1f;
			boxFixture.shape = bodyShape;
			
			Body body = world.createBody(bodyDef);
			body.createFixture(boxFixture);
			wall.setBody(body);
		}
		entities.add(wall);
		
		Entity ground = new Entity(null, 100, 100, this);
		{
			BodyDef bodyDef = new BodyDef();
			bodyDef.position.set(100f / 30f, 100f / 30f);
			bodyDef.type = BodyType.STATIC;

			PolygonShape bodyShape = new PolygonShape();
			bodyShape.setAsBox(50f / 30f, 50f / 30f);
			//CircleShape bodyShape = new CircleShape();
			//bodyShape.setRadius(50f / 30f);

			FixtureDef boxFixture = new FixtureDef();
			boxFixture.density = 1f;
			boxFixture.shape = bodyShape;
			boxFixture.isSensor = true;
			
			Body body = world.createBody(bodyDef);
			body.createFixture(boxFixture);
			ground.setBody(body);
		}
		ground.setSensorData(new SensorData(ground, SensorType.GROUND));
		entities.add(ground);
		
		ground = new Entity(null, 100, 100, this);
		{
			BodyDef bodyDef = new BodyDef();
			bodyDef.position.set(150f / 30f, 150f / 30f);
			bodyDef.type = BodyType.STATIC;

			PolygonShape bodyShape = new PolygonShape();
			bodyShape.setAsBox(50f / 30f, 50f / 30f);

			FixtureDef boxFixture = new FixtureDef();
			boxFixture.density = 1f;
			boxFixture.shape = bodyShape;
			boxFixture.isSensor = true;
			
			Body body = world.createBody(bodyDef);
			body.createFixture(boxFixture);
			ground.setBody(body);
		}
		ground.setSensorData(new SensorData(ground, SensorType.GROUND));
		entities.add(ground);
		
		Camera.get().setFocus(player);
	}

	@Override
	public void update() {
		for(int i = 0; i<entities.size(); i++) {
			entities.get(i).update();
		}
		
		world.step(1 / 60f, 8, 3);
	}

	@Override
	public void draw() {
		for(int i = 0; i<background.size(); i++) {
			background.get(i).draw();
		}
		
		ShadowMap.get().drawShadows(entities);
		
		for(int i = 0; i<entities.size(); i++) {
			for(int j = i; j >= 0 && j > i - 5; j--) {
				if(entities.get(i).getBody().getPosition().y < entities.get(j).getBody().getPosition().y) {
					entities.add(j, entities.get(i));
					entities.remove(i + 1);
					i--;
				}
			}
		}
		
		for(int i = 0; i<entities.size(); i++) {
			entities.get(i).draw();
		}
		
		ShadowMap.get().drawIllumination();
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

}
