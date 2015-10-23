package core.entities_new;

import java.io.File;
import java.io.FilenameFilter;
import java.io.Serializable;
import java.util.ArrayList;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.EdgeShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.lwjgl.util.vector.Vector3f;

import core.Camera;
import core.render.DrawUtils;
import core.setups.WorldContainer;

public class Entity implements Drawable, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Render render;
	private Body body;
	private WorldContainer container;
	private Controller controller;
	private CharacterState state;
	
	private ArrayList<Entity> ground = new ArrayList<Entity>();
	private ArrayList<Entity> subEntities = new ArrayList<Entity>();
	
	private boolean fixDirection;
	
	public Entity(String name, float x, float y, WorldContainer container) {
		setRender(loadRender(name));
		loadBody(container.getWorld(), x, y);
		setContainer(container);
		setState(CharacterState.IDLE);
	}
	
	private Render loadRender(String name) {
		File dir = new File(System.getProperty("resources") + "/sprites/" + name);
		if(dir.exists() && dir.isDirectory()) {
			File[] files = dir.listFiles(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return name.endsWith(".avo") || name.endsWith(".json");
				}
			});
			if(files.length > 0) {
				if(files[0].getName().endsWith(".json")) {
					return new SpineRender(name, this);
				} else if(files[0].getName().endsWith(".avo")) {
					return new GridRender(name);
				}
			}
		} else if(dir.exists() && !dir.isDirectory()) {
			// TODO Implement static render
		}
		
		return null;
	}
	
	private void loadBody(World world, float x, float y) {
		if(body != null) {
			world.destroyBody(body);
		}
		
		BodyDef bodyDef = new BodyDef();
		bodyDef.position.set(x / 30f, y / 30f);
		bodyDef.type = BodyType.DYNAMIC;

		CircleShape bodyShape = new CircleShape();
		bodyShape.m_radius = 15f / 30f/ 2f;

		FixtureDef boxFixture = new FixtureDef();
		boxFixture.density = 1f;
		boxFixture.shape = bodyShape;

		body = world.createBody(bodyDef);
		body.createFixture(boxFixture);
		body.setFixedRotation(true);
		body.setLinearDamping(15f);
		body.setGravityScale(0f);
		body.setUserData(this);
	}
	
	@Override
	public void draw() {
		if(render != null) {
			render.draw();
		} else if(body != null) {
			switch(body.getFixtureList().getShape().m_type) {
			case CIRCLE:
				DrawUtils.setColor(new Vector3f(0f, 0f, 0.6f));
				DrawUtils.drawBox2DCircle(body.getPosition(), (CircleShape) body.m_fixtureList.m_shape);
				break;
			case EDGE:
				DrawUtils.setColor(new Vector3f(1f, 0f, 0f));
				DrawUtils.drawBox2DEdge(body.getPosition(), (EdgeShape) body.m_fixtureList.m_shape);
				break;
			case POLYGON:
				DrawUtils.setColor(new Vector3f(0f, 0.8f, 0f));
				DrawUtils.drawBox2DPoly(body.getPosition(), (PolygonShape) body.m_fixtureList.m_shape);
				break;
			case CHAIN:
				break;
			}
		}
	}
	
	public void update() {
		if(controller != null) {
			controller.collectInput();
			controller.resolveState();
		}
		
		if(render != null) {
			if(body.getLinearVelocity().x != 0 && !fixDirection) {
				render.setFlipped(body.getLinearVelocity().x < 0);
			}
			
			render.animate(1f, body.getPosition());
		}
	}
	
	public void changeState(CharacterState state) {
		if(this.state != state) {
			this.state = state;
			
			if(render != null) {
				if(state == CharacterState.ATTACK) {
					render.setAnimation("LightAttack1", state.loop);
				} else {
					render.setAnimation(state.animation, state.loop);
				}
			}
		}
	}
	
	public void stepOnGround(Entity ground) {
		this.ground.add(ground);
		if(this.ground.size() == 1) {
			this.changeState(CharacterState.LAND);
		} else {
			this.changeState(CharacterState.IDLE);
		}
		this.body.setGravityScale(0f);
		this.body.setLinearDamping(15f);
	}
	
	public void stepOffGround(Entity ground) {
		this.ground.remove(ground);
		if(this.ground.isEmpty()) {
			this.changeState(CharacterState.FALLING);
			this.body.setGravityScale(2f);
			this.body.setLinearDamping(2.5f);
		}
	}
	
	public ArrayList<Entity> getSubEntities() {
		return subEntities;
	}

	public void setSubEntities(ArrayList<Entity> subEntities) {
		this.subEntities = subEntities;
	}

	public float getWidth() {
		if(render != null) {
			return render.getWidth() * Camera.ASPECT_RATIO;
		}
		
		return 1f;
	}
	
	public Render getRender() {
		return render;
	}
	
	public void setRender(Render render) {
		this.render = render;
	}
	
	public Body getBody() {
		return body;
	}
	
	public void setBody(Body body) {
		container.getWorld().destroyBody(this.body);
		this.body = body;
	}

	public WorldContainer getContainer() {
		return container;
	}

	public void setContainer(WorldContainer container) {
		this.container = container;
	}

	public Controller getController() {
		return controller;
	}

	public void setController(Controller controller) {
		this.controller = controller;
	}

	public CharacterState getState() {
		return state;
	}

	public void setState(CharacterState state) {
		this.state = state;
	}

	public boolean isFixDirection() {
		return fixDirection;
	}

	public void setFixDirection(boolean fixDirection) {
		this.fixDirection = fixDirection;
	}
	
}
