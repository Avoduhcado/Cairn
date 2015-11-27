package core.entities_new;

import java.io.File;
import java.io.FilenameFilter;
import java.io.Serializable;
import java.util.ArrayList;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.EdgeShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.lwjgl.util.vector.Vector3f;

import core.Theater;
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
	private CharacterState state;

	private Controller controller;
	
	private ArrayList<Entity> ground = new ArrayList<Entity>();
	private Entity subEntity;
	private Entity prevAttacker;
	//private EntityAction actionQueue;
	//private ActionQueue actions;
	//private ArrayList<EntityAction> actionQueue = new ArrayList<EntityAction>();
	
	private boolean fixDirection;
	
	public Entity(String name, float x, float y, WorldContainer container) {
		loadBody(container.getWorld(), x, y);
		setContainer(container);
		setRender(loadRender(name));
		setState(CharacterState.IDLE);
	}
	
	private Render loadRender(String name) {
		File dir = new File(System.getProperty("resources") + "/sprites/" + name);
		if(dir.exists() && dir.isDirectory()) {
			File[] files = dir.listFiles(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return name.endsWith(".avl") || name.endsWith(".json");
				}
			});
			if(files.length > 0) {
				if(files[0].getName().endsWith(".json")) {
					return new SpineRender(name, this);
				} else if(files[0].getName().endsWith(".avl")) {
					return new GridRender(name, this);
				}
			}
		} else if(new File(System.getProperty("resources") + "/sprites/" + name + ".png").exists()) {
			return new PlainRender(name, this);
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
		bodyShape.m_radius = 15f / 30f / 2f;

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
		}
		
		if(Theater.get().debug) {
			for(Fixture f = body.getFixtureList(); f != null; f = f.getNext()) {
				switch(f.getShape().m_type) {
				case CIRCLE:
					DrawUtils.setColor(new Vector3f(0f, 0f, 0.6f));
					DrawUtils.drawBox2DCircle(body, (CircleShape) f.m_shape);
					break;
				case EDGE:
					DrawUtils.setColor(new Vector3f(1f, 0f, 0f));
					DrawUtils.drawBox2DEdge(body.getPosition(), (EdgeShape) f.m_shape);
					break;
				case POLYGON:
					DrawUtils.setColor(new Vector3f(0f, 0.8f, 0f));
					DrawUtils.drawBox2DPoly(body, (PolygonShape) f.m_shape);
					break;
				case CHAIN:
					break;
				}
			}
		}
	}
	
	public void update() {
		if(controller != null) {
			controller.collectInput();
			controller.resolveState();
		}
		
		
		
		if(render != null) {
			render.animate(1f, body.getPosition());
		}
		
		if(body != null && body.getFixtureList().getUserData() != null && body.getFixtureList().getUserData() instanceof Float) {
			body.getFixtureList().setUserData((float) body.getFixtureList().getUserData() - body.getLinearVelocity().y);
		
			if((float) body.getFixtureList().getUserData() <= 0f) {
				body.getFixtureList().setUserData(Math.abs(body.getLinearVelocity().y));
				body.setLinearVelocity(new Vec2(body.getLinearVelocity().x, -body.getLinearVelocity().y));
				body.applyAngularImpulse(10f);
			}
			
			if((float) body.getFixtureList().getUserData() <= 1f) {
				body.getFixtureList().setUserData(null);
				body.setGravityScale(0);
				body.setLinearDamping(5f);
				//body.getFixtureList().getFilterData().groupIndex = 0;
			}
		}
	}
	
	public void changeStateForced(CharacterState state) {
		this.state = state;
		
		if(render != null) {
			render.setAnimation(state.getCustomAnimation() != null ? state.getCustomAnimation() : state.animation, state.loop);
		}
	}
	
	public void changeState(CharacterState state) {
		if(this.state != state) {
			this.state = state;
			
			if(render != null) {
				render.setAnimation(state.getCustomAnimation() != null ? state.getCustomAnimation() : state.animation, state.loop);
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
			// TODO Interrupt() delete any sub entities/general state cleanup
			this.changeState(CharacterState.FALLING);
			this.body.setGravityScale(2f);
			this.body.setLinearDamping(2.5f);
		}
	}
	
	private void destroyBodies() {
		getContainer().getWorld().destroyBody(getBody());
		if(render instanceof SpineRender) {
			((SpineRender) render).destroyBodies();
		}
	}
	
	public Entity getSubEntity() {
		return subEntity;
	}

	public void setSubEntity(Entity subEntity) {
		if(getSubEntity() != null) {
			if(getContainer().removeEntity(getSubEntity())) {
				getSubEntity().destroyBodies();
			}
		}
		
		this.subEntity = subEntity;
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

	public CharacterState getState() {
		return state;
	}

	public void setState(CharacterState state) {
		this.state = state;
	}

	public Controller getController() {
		return controller;
	}

	public void setController(Controller controller) {
		this.controller = controller;
	}

	public boolean isFixDirection() {
		return fixDirection;
	}

	public void setFixDirection(boolean fixDirection) {
		this.fixDirection = fixDirection;
	}
	
	@Override
	public String toString() {
		if(render != null) {
			return this.render.getSprite();
		}
		
		return super.toString();
	}

	public void hit(Entity entity) {
		if(prevAttacker != entity) {
			this.changeStateForced(CharacterState.HIT);
			this.prevAttacker = entity;
		}
	}

}
