package core.entities_new;

import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FilenameFilter;
import java.io.Serializable;

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
	}
	
	@Override
	public void draw() {
		if(render != null) {
			render.draw();
		} else if(body != null) {
			switch(body.getFixtureList().getShape().m_type) {
			case CIRCLE:
				//DrawUtils.drawShape(body.getPosition().x * 30f, body.getPosition().y * 30f, body.getFixtureList().getShape());
				break;
			case EDGE:
				EdgeShape edge = (EdgeShape) body.m_fixtureList.m_shape;
				DrawUtils.setColor(new Vector3f(1f, 0f, 0f));
				DrawUtils.drawLine(body.getPosition().x * 30f, body.getPosition().y * 30f, 
						new Line2D.Double(edge.m_vertex1.x * 30f, edge.m_vertex1.y * 30f, edge.m_vertex2.x * 30f, edge.m_vertex2.y * 30f));
				break;
			case POLYGON:
				DrawUtils.setColor(new Vector3f(0f, 0.8f, 0f));
				DrawUtils.drawBox2DPoly(body.getPosition(), (PolygonShape) body.m_fixtureList.m_shape);
				/*PolygonShape poly = (PolygonShape) body.m_fixtureList.m_shape;
				DrawUtils.setColor(new Vector3f(0f, 0.8f, 0f));
				DrawUtils.drawRect((body.getPosition().x * 30f) + (poly.m_vertices[0].x * 30f),
						(body.getPosition().y * 30f) - (poly.m_vertices[0].y * 30f),
						new Rectangle2D.Float(0f, 0f, (poly.m_vertices[1].x * 30f) * 2f, (poly.m_vertices[1].y * 30f) * 2f));*/
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
				render.setAnimation(state.animation, state.loop);
			}
		}
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
