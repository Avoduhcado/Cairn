package core.entities_new;

import java.io.Serializable;
import java.util.HashMap;

import org.jbox2d.dynamics.Body;
import org.lwjgl.util.vector.Vector3f;

import core.Theater;
import core.entities_new.components.Combatant;
import core.entities_new.components.Controllable;
import core.entities_new.components.Inventory;
import core.entities_new.components.PlainStateManager;
import core.entities_new.components.Renderable;
import core.entities_new.components.StateManager;
import core.entities_new.components.ZBody;
import core.entities_new.event.ActionEvent;
import core.entities_new.event.CombatEvent;
import core.entities_new.event.EntityEvent;
import core.entities_new.event.InventoryEvent;
import core.entities_new.event.StateChangeEvent;
import core.entities_new.utils.BodyData;
import core.entities_new.utils.BodyLoader;
import core.entities_new.utils.DepthSort;
import core.entities_new.utils.RenderLoader;
import core.render.DrawUtils;
import core.setups.WorldContainer;

public class Entity implements DepthSort, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private WorldContainer container;
	private final String name;
	
	private Renderable render;
	private ZBody zBody;
	private Controllable controller;
	private StateManager stateManager;
	
	private HashMap<Class<?>, EntityComponent> components = new HashMap<Class<?>, EntityComponent>();
	
	private boolean fixDirection;
	
	public Entity(String name, BodyData bodyData, WorldContainer container) {
		this.name = name;
		setContainer(container);
		
		setZBody(new ZBody(BodyLoader.loadBody(bodyData, this, container.getWorld()), this));
		setRender(RenderLoader.loadRender(name, this));
		setStateManager(new PlainStateManager(this, State.IDLE));
	}
	
	// TODO Remove this and refactor controller.collapse()
	public Entity(String name, Body body, WorldContainer container) {
		this.name = name;
		setContainer(container);

		body.setUserData(this);
		setZBody(new ZBody(body, this));
		setRender(RenderLoader.loadRender(name, this));
		stateManager = new PlainStateManager(this, State.IDLE);
	}

	public void draw() {
		if(render()) {
			if(isWalkingBackwards()) {
				render.animate(-0.75f);
			} else {
				render.animate(1f);
			}
			
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
		return (int) (this.getZBody().getScreenY() - e.getZBody().getScreenY());
	}

	public void updateBodyAndState() {
		zBody.move();
		stateManager.resolveState();
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
		if(zBody != null) {
			getContainer().getWorld().destroyBody(getBody());
		}
		
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
	
	public HashMap<Class<?>, EntityComponent> getComponents() {
		return components;
	}

	public void setComponents(HashMap<Class<?>, EntityComponent> components) {
		this.components = components;
	}
	
	public void addComponent(Class<?> clazz, EntityComponent component) {
		components.put(clazz, component);
	}
	
	public EntityComponent removeComponent(Class<?> clazz) {
		return components.remove(clazz);
	}

	public State getState() {
		return stateManager.getState();
	}
	
	public boolean stateManager() {
		return stateManager != null;
	}
	
	public StateManager getStateManager() {
		return stateManager;
	}
	
	public void setStateManager(StateManager stateManager) {
		this.stateManager = stateManager;
	}
	
	public boolean isFixDirection() {
		return fixDirection;
	}

	public void setFixDirection(boolean fixDirection) {
		this.fixDirection = fixDirection;
	}
	
	public void fireEvent(EntityEvent e) {
		if(e instanceof ActionEvent) {
			
		} else if(e instanceof CombatEvent) {
			processCombatEvent((CombatEvent) e);
		} else if(e instanceof StateChangeEvent) {
			getStateManager().changeState(((StateChangeEvent) e).getNewState());
		} else if(e instanceof InventoryEvent) {
			processEquipmentEvent((InventoryEvent) e);
		}
	}
	
	protected void processCombatEvent(CombatEvent e) {
		if(components.containsKey(Combatant.class)) {
			((Combatant) components.get(Combatant.class)).hit(e);
		}
	}
	
	protected void processEquipmentEvent(InventoryEvent e) {
		if(components.containsKey(Inventory.class)) {
			switch(e.getEventType()) {
			case InventoryEvent.CYCLE:
				((Inventory) components.get(Inventory.class)).cycle(e);
				break;
			}
		}
	}

	@Override
	public String toString() {
		return name;
	}

}
