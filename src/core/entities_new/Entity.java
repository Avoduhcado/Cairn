package core.entities_new;

import java.io.Serializable;
import java.util.HashMap;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.EdgeShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;
import org.lwjgl.util.vector.Vector3f;

import core.Theater;
import core.entities_new.components.Combatant;
import core.entities_new.components.Inventory;
import core.entities_new.components.controllers.Controllable;
import core.entities_new.components.geometrics.ZBody;
import core.entities_new.components.interactions.ActivateInteraction;
import core.entities_new.components.interactions.AutorunInteraction;
import core.entities_new.components.interactions.Interaction;
import core.entities_new.components.interactions.TouchInteraction;
import core.entities_new.components.renders.Renderable;
import core.entities_new.components.renders.SpineRender;
import core.entities_new.components.states.PlainStateManager;
import core.entities_new.components.states.StateManager;
import core.entities_new.event.ActionEvent;
import core.entities_new.event.CombatEvent;
import core.entities_new.event.EntityEvent;
import core.entities_new.event.InteractEvent;
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
				for(Fixture f = getBody().getFixtureList(); f != null; f = f.getNext()) {
					switch(f.getShape().m_type) {
					case CIRCLE:
						DrawUtils.setColor(new Vector3f(0.2f, 0f, 1f));
						DrawUtils.drawBox2DCircle(getBody(), (CircleShape) f.m_shape);
						break;
					case EDGE:
						DrawUtils.setColor(new Vector3f(1f, 0f, 0.2f));
						DrawUtils.drawBox2DEdge(getBody().getPosition(), (EdgeShape) f.m_shape);
						break;
					case POLYGON:
						DrawUtils.setColor(new Vector3f(0f, 1f, 0.2f));
						DrawUtils.drawBox2DPoly(getBody(), (PolygonShape) f.m_shape);
						break;
					case CHAIN:
						break;
					}
				}
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
		container.queueEntity(this, false);
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
	
	/**
	 * Make sure this is only called on a Spine enabled entity
	 * @return render as a SpineRender or null
	 */
	public SpineRender getSpineRender() {
		if(render() && render instanceof SpineRender) {
			return (SpineRender) render;
		}
		
		return null;
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
	
	public EntityComponent getComponent(Class<?> clazz) {
		if(components.containsKey(clazz)) {
			return components.get(clazz);
		}
		return null;
	}
	
	public void addComponent(Class<? extends EntityComponent> clazz, EntityComponent component) {
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
			getStateManager().changeState(((StateChangeEvent) e).getState());
		} else if(e instanceof InventoryEvent) {
			processEquipmentEvent((InventoryEvent) e);
		} else if(e instanceof InteractEvent) {
			processInteractEvent((InteractEvent) e);
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
	
	protected void processInteractEvent(InteractEvent e) {
		switch(e.getInteractType()) {
		case InteractEvent.AUTORUN:
			if(components.containsKey(AutorunInteraction.class)) {
				((Interaction) components.get(AutorunInteraction.class)).interact(e);
			}
			break;
		case InteractEvent.ON_TOUCH:
			if(components.containsKey(TouchInteraction.class)) {
				((Interaction) components.get(TouchInteraction.class)).interact(e);
			}
			break;
		case InteractEvent.ON_ACTIVATE:
		case InteractEvent.INTERRUPT:
			if(components.containsKey(ActivateInteraction.class)) {
				getRender().lookAt(e.getInteractor());
				((Interaction) components.get(ActivateInteraction.class)).interact(e);
			}
			break;
		}
	}

	@Override
	public String toString() {
		return name;
	}

}
