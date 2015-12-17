package core.entities_new;

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
import core.entities_new.components.Controllable;
import core.entities_new.components.GridRender;
import core.entities_new.components.PlainRender;
import core.entities_new.components.Renderable;
import core.entities_new.components.SpineRender;
import core.entities_new.components.ZBody;
import core.entities_new.event.CombatEvent;
import core.entities_new.event.CombatListener;
import core.entities_new.event.EntityEvent;
import core.entities_new.event.StateChangeEvent;
import core.entities_new.utils.DepthSort;
import core.entities_new.utils.SensorData;
import core.entities_new.utils.SensorType;
import core.inventory.Equipment;
import core.render.DrawUtils;
import core.setups.Stage_new;
import core.setups.WorldContainer;
import core.utilities.Resources;
import net.lingala.zip4j.model.FileHeader;

public class Entity implements DepthSort, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private WorldContainer container;
	// TODO Extract somehow
	private CharacterState state;
	
	private Renderable render;
	private ZBody zBody;
	// TODO Extract this
	private Equipment equipment;

	private Controllable controller;
	private CombatListener combatListener;
	
	private ArrayList<Entity> ground = new ArrayList<Entity>();
	// TODO Delete this
	private Entity subEntity;
	
	private boolean fixDirection;
	
	public Entity(String name, float x, float y, WorldContainer container) {
		setContainer(container);
		loadBody(container.getWorld(), x, y);
		setRender(loadRender(name));
		setState(CharacterState.IDLE);
	}
	
	private Renderable loadRender(String name) {
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
		if(zBody != null) {
			world.destroyBody(getBody());
		}
		
		BodyDef bodyDef = new BodyDef();
		bodyDef.position.set(x / Stage_new.SCALE_FACTOR, y / Stage_new.SCALE_FACTOR);
		bodyDef.type = BodyType.DYNAMIC;

		CircleShape bodyShape = new CircleShape();
		bodyShape.m_radius = 15f / Stage_new.SCALE_FACTOR / 2f;

		FixtureDef boxFixture = new FixtureDef();
		boxFixture.density = 1f;
		boxFixture.shape = bodyShape;
		
		Body body = world.createBody(bodyDef);
		body.createFixture(boxFixture);
		body.setFixedRotation(true);
		body.setLinearDamping(15f);
		body.setGravityScale(0f);
		body.setUserData(this);
		setZBody(new ZBody(body));
	}
	
	public void draw() {
		if(render()) {
			render.draw();
		}
		
		if(Theater.get().debug) {
			for(Fixture f = zBody.getBody().getFixtureList(); f != null; f = f.getNext()) {
				switch(f.getShape().m_type) {
				case CIRCLE:
					DrawUtils.setColor(new Vector3f(0f, 0f, 0.6f));
					DrawUtils.drawBox2DCircle(zBody.getBody(), (CircleShape) f.m_shape);
					break;
				case EDGE:
					DrawUtils.setColor(new Vector3f(1f, 0f, 0f));
					DrawUtils.drawBox2DEdge(zBody.getBody().getPosition(), (EdgeShape) f.m_shape);
					break;
				case POLYGON:
					DrawUtils.setColor(new Vector3f(0f, 0.8f, 0f));
					DrawUtils.drawBox2DPoly(zBody.getBody(), (PolygonShape) f.m_shape);
					break;
				case CHAIN:
					break;
				}
			}
		}
	}
	
	@Override
	public int compareTo(Entity e) {
		return (int) ((this.getBody().getPosition().y * Stage_new.SCALE_FACTOR) - (e.getBody().getPosition().y * Stage_new.SCALE_FACTOR));
	}
	
	public void update() {
		if(controller()) {
			controller.collectInput();
			controller.resolveState();
		}

		if(render()) {
			render.animate(1f);
		}
		
		if(getBody().getGravityScale() > 0 && zBody.getGroundZ() != 0) {
			zBody.setZ(zBody.getGroundZ() - (getBody().getPosition().y * Stage_new.SCALE_FACTOR));
			if(getBody().getLinearVelocity().y > 0 && getState() == CharacterState.JUMPING) {
				changeState(CharacterState.FALLING);
			}

			if(zBody.getZ() <= 0f && getBody().getLinearVelocity().y > 2) {
				if(getBody().getLinearVelocity().y > 8) {
					Camera.get().setShake(new Vector2f(5, 10), 5.5f, 0.65f);
				}
				getBody().setLinearVelocity(new Vec2(getBody().getLinearVelocity().x, -getBody().getLinearVelocity().y * 0.5f));
				getBody().applyAngularImpulse(10f);
				changeState(CharacterState.JUMPING);
			} else if(zBody.getZ() < 0f) {
				changeState(CharacterState.LAND);
				zBody.setZ(0);
				zBody.setGroundZ(0);
				getBody().setGravityScale(0);
				getBody().setLinearDamping(15f);
				getBody().setLinearVelocity(new Vec2());
				getBody().getFixtureList().getFilterData().categoryBits = 1;
				//body.getFixtureList().getFilterData().groupIndex = 0;
			}
		}
	}
	
	public void changeStateForced(CharacterState state) {
		if(render()) {
			render.fireEvent(new StateChangeEvent(state, this.state));
		}
		
		this.state = state;
	}
	
	public void changeState(CharacterState state) {
		if(this.state != state) {
			changeStateForced(state);
		}
	}
	
	public void stepOnGround(Entity ground) {
		this.ground.add(ground);
		if(this.ground.size() == 1) {
			this.changeState(CharacterState.LAND);
		} else {
			this.changeState(CharacterState.IDLE);
		}
		getBody().setGravityScale(0f);
		getBody().setLinearDamping(15f);
	}
	
	public void stepOffGround(Entity ground) {
		this.ground.remove(ground);
		if(this.ground.isEmpty()) {
			// TODO Interrupt() delete any sub entities/general state cleanup
			this.changeState(CharacterState.FALLING);
			getBody().setGravityScale(1f);
			getBody().setLinearDamping(1f);
			getBody().getFixtureList().getFilterData().categoryBits = 0;
			
			float closestFraction = 10;
			for(Entity e : this.getContainer().getEntities()) {
				Fixture fixture = e.getBody().getFixtureList();
				if(fixture.isSensor() && fixture.getBody().getUserData() instanceof SensorData) {
					SensorData data = (SensorData) fixture.getBody().getUserData();
					if(data.getType() == SensorType.GROUND) {
						RayCastOutput output = new RayCastOutput();
						RayCastInput input = new RayCastInput();
						input.p1.set(getBody().getPosition());
						input.p2.set(getBody().getPosition().x, getBody().getPosition().y + (50 / Stage_new.SCALE_FACTOR));
						input.maxFraction = closestFraction;
						
						if(!fixture.raycast(output, input, 0)) {
							continue;
						}
						if(output.fraction < closestFraction) {
							closestFraction = output.fraction;
							zBody.setGroundZ(fixture.getBody().getPosition().y * Stage_new.SCALE_FACTOR);
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

	public boolean render() {
		return render != null;
	}
	
	public Renderable getRender() {
		return render;
	}
	
	public void setRender(Renderable render) {
		this.render = render;
	}
	
	public boolean geometric() {
		return zBody != null;
	}
	
	public ZBody getZBody() {
		return zBody;
	}
	
	public void setZBody(ZBody body) {
		this.zBody = body;
	}
	
	public Body getBody() {
		return zBody.getBody();
	}
	
	public void setBody(Body body) {
		container.getWorld().destroyBody(getBody());
		getZBody().setBody(body);
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

	public boolean isFixDirection() {
		return fixDirection;
	}

	public void setFixDirection(boolean fixDirection) {
		this.fixDirection = fixDirection;
	}

	public boolean controller() {
		return controller != null;
	}
	
	public Controllable getController() {
		return controller;
	}

	public void setController(Controllable controller) {
		this.controller = controller;
	}
	
	public void removeCombatListener(CombatListener l) {
		if(l == null) {
			return;
		}
		combatListener = null;
	}
	
	public void addCombatListener(CombatListener l) {
		this.combatListener = l;
	}
	
	public void fireEvent(EntityEvent e) {
		if(e instanceof CombatEvent) {
			processCombatEvent((CombatEvent) e);
		}
	}

	protected void processCombatEvent(CombatEvent e) {
		if(combatListener != null) {
			combatListener.hit(e);
		}
	}

	@Override
	public String toString() {
		if(render != null) {
			return this.render.getSprite();
		}
		
		return super.toString();
	}

}
