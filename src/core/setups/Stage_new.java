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

import core.Camera;
import core.entities_new.Entity;
import core.entities_new.FollowController;
import core.entities_new.PlayerController;
import core.scene.BoneWorld;
import core.scene.ShadowMap;

public class Stage_new extends GameSetup implements WorldContainer {
	
	private ArrayList<Entity> entities = new ArrayList<Entity>();
	private World world = new World(new Vec2(0, 9.8f));
	
	public Stage_new() {
		Camera.get().setFade(-2.5f);
		Camera.get().frame.setFrame(0, 0, Camera.get().frame.getWidth(), Camera.get().frame.getHeight());
		
		world.setContactListener(new BoneWorld());
		
		Entity player = new Entity("MC and Familiar", 500, 100, this);
		player.setController(new PlayerController(player));
		entities.add(player);
		
		Entity dad = new Entity("Skull", 500, 100, this);
		FollowController dadController = new FollowController(dad, player);
		dadController.setOffset(0, 0f);
		dad.setController(dadController);
		dad.getBody().getFixtureList().getFilterData().categoryBits = 0;
		entities.add(dad);
		
		entities.add(new Entity("Shepherd", 900, 100, this));
		
		Entity wall = new Entity(null, 0, 200, this);
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
		ShadowMap.drawShadows(entities);
		
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
