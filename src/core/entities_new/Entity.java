package core.entities_new;

import java.io.Serializable;
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
import core.entities_new.components.PlainStateManager;
import core.entities_new.components.Renderable;
import core.entities_new.components.SpineRender;
import core.entities_new.components.StateManager;
import core.entities_new.components.ZBody;
import core.entities_new.event.ActionEvent;
import core.entities_new.event.CombatEvent;
import core.entities_new.event.CombatListener;
import core.entities_new.event.EntityEvent;
import core.entities_new.event.StateChangeEvent;
import core.entities_new.utils.DepthSort;
import core.entities_new.utils.RenderLoader;
import core.entities_new.utils.SensorData;
import core.inventory.Equipment;
import core.render.DrawUtils;
import core.setups.Stage_new;
import core.setups.WorldContainer;

public class Entity implements DepthSort, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private WorldContainer container;
	private String name;
	//private State state;
	
	private Renderable render;
	private ZBody zBody;
	private Controllable controller;
	
	// TODO Extract this
	private Equipment equipment;
	private CombatListener combatListener;
	private StateManager stateManager;
	
	private boolean fixDirection;
	
	public Entity(String name, float x, float y, WorldContainer container) {
		this.name = name;
		setContainer(container);
		
		loadBody(container.getWorld(), x, y);
		setRender(RenderLoader.loadRender(name, this));
		stateManager = new PlainStateManager(this, State.IDLE);
	}
	
	public Entity(String name, Body body, WorldContainer container) {
		this.name = name;
		setContainer(container);
		
		setZBody(new ZBody(body, this));
		setRender(RenderLoader.loadRender(name, this));
		stateManager = new PlainStateManager(this, State.IDLE);
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
		boxFixture.filter.categoryBits = 0b0011;
		boxFixture.filter.maskBits = 0b1110;
		System.out.println(getName() + " " + boxFixture.filter.categoryBits + " " + boxFixture.filter.maskBits + " " + boxFixture.filter.groupIndex);
		boxFixture.shape = bodyShape;
		boxFixture.userData = new SensorData(this, "Base", SensorData.CHARACTER);
		
		Body body = world.createBody(bodyDef);
		body.createFixture(boxFixture);
		body.setFixedRotation(true);
		body.setLinearDamping(15f);
		body.setGravityScale(0f);
		body.setUserData(this);
		body.setSleepingAllowed(false);
		setZBody(new ZBody(body, this));
	}
	
	public void draw() {
		if(render()) {
			render.draw();
		}
		
		if(Theater.get().debug) {
			if(render()) {
				render.debugDraw();
			} else {
				DrawUtils.setColor(new Vector3f(1, 0, 0));
				DrawUtils.drawBox2DShape(getBody(), getBody().getFixtureList().getShape());
			}
		}
	}
	
	@Override
	public int compareTo(Entity e) {
		return (int) ((this.getBody().getPosition().y * Stage_new.SCALE_FACTOR) - (e.getBody().getPosition().y * Stage_new.SCALE_FACTOR));
	}
	
	public void update() {
		if(controller()) {
			controller.control();
		}
		
		if(render()) {
			if(isWalkingBackwards()) {
				render.animate(-1f);
			} else {
				render.animate(1f);
			}
		}
		
		if(getBody().getGravityScale() > 0 && zBody.getGroundZ() != 0) {
			zBody.setZ(zBody.getGroundZ() - (getBody().getPosition().y * Stage_new.SCALE_FACTOR));
			if(getBody().getLinearVelocity().y > 0 && getState() == State.JUMPING) {
				fireEvent(new StateChangeEvent(State.FALLING));
			}

			if(zBody.getZ() <= 0f && getBody().getLinearVelocity().y > 2) {
				if(getBody().getLinearVelocity().y > 8) {
					Camera.get().setShake(new Vector2f(5, 10), 5.5f, 0.65f);
				}
				getBody().setLinearVelocity(new Vec2(getBody().getLinearVelocity().x, -getBody().getLinearVelocity().y * 0.5f));
				getBody().applyAngularImpulse(10f);
				fireEvent(new StateChangeEvent(State.JUMPING));
			} else if(zBody.getZ() < 0f) {
				fireEvent(new StateChangeEvent(State.LAND));
				zBody.setZ(0);
				zBody.setGroundZ(0);
				getBody().setGravityScale(0);
				getBody().setLinearDamping(15f);
				getBody().setLinearVelocity(new Vec2());
				//getBody().getFixtureList().getFilterData().categoryBits = 1;
				//body.getFixtureList().getFilterData().groupIndex = 0;
			}
		}
		
		stateManager.resolveState();
	}
	
	public void destroy() {
		container.getWorld().destroyBody(getBody());
		container.removeEntity(this);
	}
	
	public String getName() {
		return name;
	}

	public WorldContainer getContainer() {
		return container;
	}

	public void setContainer(WorldContainer container) {
		this.container = container;
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

	public boolean controller() {
		return controller != null;
	}
	
	public Controllable getController() {
		return controller;
	}

	public void setController(Controllable controller) {
		this.controller = controller;
	}
	
	public State getState() {
		return stateManager.getState();
	}
	
	private StateManager getStateManager() {
		return stateManager;
	}
	
	public void setStateManager(StateManager stateManager) {
		this.stateManager = stateManager;
	}
	
	public Equipment getEquipment() {
		return equipment;
	}

	public void setEquipment(Equipment equipment) {
		this.equipment = equipment;
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
	
	public boolean isFixDirection() {
		return fixDirection;
	}

	public void setFixDirection(boolean fixDirection) {
		this.fixDirection = fixDirection;
	}
	
	public boolean isWalkingBackwards() {
		if(isFixDirection() && getBody().getLinearVelocity().x != 0 && getState() == State.WALK && render()) {
			if(render.isFlipped()) {
				return getBody().getLinearVelocity().x > 0;
			}
			return getBody().getLinearVelocity().x < 0;
		}
		
		return false;
	}
	
	public void fireEvent(EntityEvent e) {
		if(e instanceof ActionEvent) {
			
		} else if(e instanceof CombatEvent) {
			processCombatEvent((CombatEvent) e);
		} else if(e instanceof StateChangeEvent) {
			getStateManager().changeState(((StateChangeEvent) e).getNewState());
		}
	}
	
	protected void processCombatEvent(CombatEvent e) {
		if(combatListener != null) {
			combatListener.hit(e);
		}
	}

	@Override
	public String toString() {
		return name;
	}

}
