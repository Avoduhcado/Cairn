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

import com.esotericsoftware.spine.Slot;
import com.esotericsoftware.spine.attachments.Region;

import core.Camera;
import core.entities.utils.ActionQueue.EntityAction;
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
	private Entity subEntity;
	private ArrayList<EntityAction> actionQueue = new ArrayList<EntityAction>();
	
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
		if(render != null && render instanceof SpineRender) {
			for(Slot s : ((SpineRender) render).getSkeleton().drawOrder) {
				if(s.getAttachment() != null) {
					Region region = (Region) s.getAttachment();
					region.updateWorldVertices(s);
					
					Vec2[] verts = new Vec2[4];
					verts[0] = new Vec2(0, 0);
					verts[1] = new Vec2(region.getWidth() / 30f, 0);
					verts[2] = new Vec2(region.getWidth() / 30f, region.getHeight() / 30f);
					verts[3] = new Vec2(0, region.getHeight() / 30f);
					//for(Vec2 v : verts) {
					//	v.addLocal(region.getWorldX() / 30f, region.getWorldY() / 30f);
					//}
					
					PolygonShape polyShape = new PolygonShape();
					polyShape.set(verts, 4);

					boxFixture.shape = polyShape;
					boxFixture.density = 0;
					boxFixture.userData = s;
					boxFixture.isSensor = true;
					
					body.createFixture(boxFixture);
				}
			}
		}
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
		
		for(Fixture f = body.getFixtureList(); f != null; f = f.getNext()) {
			switch(f.getShape().m_type) {
			case CIRCLE:
				DrawUtils.setColor(new Vector3f(0f, 0f, 0.6f));
				DrawUtils.drawBox2DCircle(body.getPosition(), (CircleShape) f.m_shape);
				break;
			case EDGE:
				DrawUtils.setColor(new Vector3f(1f, 0f, 0f));
				DrawUtils.drawBox2DEdge(body.getPosition(), (EdgeShape) f.m_shape);
				break;
			case POLYGON:
				DrawUtils.setColor(new Vector3f(0f, 0.8f, 0f));
				// TODO Not accurate placements
				if(f.getUserData() != null && f.getUserData() instanceof Slot) {
					Slot s = (Slot) f.getUserData();
					if(s.getAttachment() != null) {
						Region r = (Region) s.getAttachment();
						r.updateWorldVertices(s);
						DrawUtils.setTransform(r.getWorldX(), r.getWorldY(), 0,
								0, (render.isFlipped() ? 1 : 0), (render.isFlipped() ? s.getBone().getWorldRotation() + r.getRotation()
										: -s.getBone().getWorldRotation() - r.getRotation()),
								1, 1, 1);
						DrawUtils.drawBox2DPoly(null, (PolygonShape) f.m_shape);
					}
				} else {
					DrawUtils.drawBox2DPoly(body, (PolygonShape) f.m_shape);
				}
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
		
		if(body != null && body.getFixtureList().getUserData() != null && body.getFixtureList().getUserData() instanceof Float) {
			body.getFixtureList().setUserData((float) body.getFixtureList().getUserData() - body.getLinearVelocity().y);
		
			if((float) body.getFixtureList().getUserData() <= 0f) {
				body.getFixtureList().setUserData(Math.abs(body.getLinearVelocity().y));
				body.setLinearVelocity(new Vec2(body.getLinearVelocity().x, -body.getLinearVelocity().y));
				body.applyAngularImpulse(10f);
			}
			
			if((float) body.getFixtureList().getUserData() <= 1f) {
				//body.getFixtureList().setUserData(null);
				//body.setGravityScale(0);
				//body.setLinearDamping(5f);
				//body.getFixtureList().getFilterData().groupIndex = 0;
			}
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
	
	public Entity getSubEntity() {
		return subEntity;
	}

	public void setSubEntity(Entity subEntity) {
		this.subEntity = subEntity;
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
	
	@Override
	public String toString() {
		if(render != null) {
			return this.render.getSprite();
		}
		
		return super.toString();
	}
	
}
