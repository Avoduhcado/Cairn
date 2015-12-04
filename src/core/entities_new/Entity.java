package core.entities_new;

import java.io.File;
import java.io.FilenameFilter;
import java.io.Serializable;
import java.util.ArrayList;

import org.jbox2d.collision.RayCastInput;
import org.jbox2d.collision.RayCastOutput;
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
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import core.Camera;
import core.Theater;
import core.inventory.Equipment;
import core.render.DrawUtils;
import core.setups.WorldContainer;
import core.utilities.Resources;
import net.lingala.zip4j.model.FileHeader;

public class Entity implements Drawable, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Render render;
	private Body body;
	private WorldContainer container;
	private CharacterState state;
	private Equipment equipment;

	private Controller controller;
	
	private ArrayList<Entity> ground = new ArrayList<Entity>();
	private Entity subEntity;
	private Entity prevAttacker;
	
	private boolean fixDirection;
	private float z, groundZ;
	
	public Entity(String name, float x, float y, WorldContainer container) {
		loadBody(container.getWorld(), x, y);
		setContainer(container);
		setRender(loadRender(name));
		setState(CharacterState.IDLE);
	}
	
	private Render loadRender(String name) {
		FileHeader dir = Resources.get().getResourceHeader(name + "/");
		if(dir != null && dir.isDirectory()) {
			if(Resources.get().getResourceHeader(name + "/" + name + ".json") != null) {
				return new SpineRender(name, this);
			} else if(Resources.get().getResourceHeader(name + "/" + name + ".avl") != null) {
				return new GridRender(name, this);
			}
		} else if(Resources.get().getResourceHeader(name + ".png") != null) {
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
			
		if(body.getGravityScale() > 0 && getGroundZ() != 0) {
			setZ(getGroundZ() - (body.getPosition().y * 30f));
			if(body.getLinearVelocity().y > 0 && getState() == CharacterState.JUMPING) {
				changeState(CharacterState.FALLING);
			}
		
			if(getZ() <= 0f && body.getLinearVelocity().y > 2) {
				if(body.getLinearVelocity().y > 8) {
					Camera.get().setShake(new Vector2f(5, 10), 5.5f, 0.65f);
				}
				body.setLinearVelocity(new Vec2(body.getLinearVelocity().x, -body.getLinearVelocity().y * 0.5f));
				body.applyAngularImpulse(10f);
				changeState(CharacterState.JUMPING);
			} else if(getZ() < 0f) {
				changeState(CharacterState.LAND);
				setZ(0);
				setGroundZ(0);
				body.setGravityScale(0);
				body.setLinearDamping(15f);
				body.setLinearVelocity(new Vec2());
				body.getFixtureList().getFilterData().categoryBits = 1;
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
			this.body.setGravityScale(1f);
			this.body.setLinearDamping(1f);
			body.getFixtureList().getFilterData().categoryBits = 0;
			
			float closestFraction = 10;
			for(Entity e : this.getContainer().getEntities()) {
				Fixture fixture = e.getBody().getFixtureList();
				if(fixture.isSensor() && fixture.getBody().getUserData() instanceof SensorData) {
					SensorData data = (SensorData) fixture.getBody().getUserData();
					if(data.getType() == SensorType.GROUND) {
						RayCastOutput output = new RayCastOutput();
						RayCastInput input = new RayCastInput();
						input.p1.set(this.body.getPosition());
						input.p2.set(this.body.getPosition().x, this.body.getPosition().y + (50 / 30f));
						input.maxFraction = closestFraction;
						
						if(!fixture.raycast(output, input, 0)) {
							continue;
						}
						if(output.fraction < closestFraction) {
							closestFraction = output.fraction;
							this.setGroundZ(fixture.getBody().getPosition().y * 30f);
							System.out.println(output.fraction + " " + output.normal);
						}
					}
				}
			}
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

	public Equipment getEquipment() {
		return equipment;
	}

	public void setEquipment(Equipment equipment) {
		this.equipment = equipment;
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
	
	public float getZ() {
		return z;
	}

	public void setZ(float z) {
		this.z = z;
	}

	public float getGroundZ() {
		return groundZ;
	}

	public void setGroundZ(float groundZ) {
		this.groundZ = groundZ;
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
