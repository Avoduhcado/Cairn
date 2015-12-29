package core.entities_new.components;

import java.util.ArrayList;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;

import com.esotericsoftware.spine.Slot;
import com.esotericsoftware.spine.attachments.Region;

import core.Camera;
import core.entities_new.State;
import core.entities_new.Entity;
import core.entities_new.event.ActionEvent;
import core.entities_new.event.StateChangeEvent;
import core.setups.Stage_new;
import core.utilities.keyboard.Keybinds;

public class PlayerController implements Controllable {

	private Entity player;
		
	private float speed = 20f;
	private float speedMod = 1f;
	private Vec2 movement = new Vec2();
	
	private ActionEvent actionQueue;
		
	public PlayerController(Entity player) {
		this.player = player;
	}

	@Override
	public void control() {
		speedMod = 1f;
		if(Keybinds.RUN.held()) {
			speedMod = 1.5f;
		}
		
		movement.set(Keybinds.RIGHT.press() ? 1f : Keybinds.LEFT.press() ? -1f : 0f,
				Keybinds.UP.press() ? -0.6f : Keybinds.DOWN.press() ? 0.6f : 0f);
		
		if(movement.length() > 0) {
			move(movement);
			movement.setZero();
		}
		
		if(Keybinds.DODGE.clicked()) {
			dodge();
		} else if(Keybinds.ATTACK.clicked()) {
			attack();
		} else if(Keybinds.DEFEND.clicked()) {
			player.setFixDirection(true);
			defend(false);
		} else if(Keybinds.SLOT1.clicked()) {
			changeWeapon();
		} else if(Keybinds.SLOT2.clicked()) {
			jump(new Vec2(0, -6f));
		}
		
		if(Keybinds.DEFEND.released()) {
			player.setFixDirection(false);
			defend(true);
		}
		
		if(Keybinds.CONTROL.clicked()) {
			Camera.get().setZoom(0.5f, 0.3333f);
			//collapse(player.getBody().getLinearVelocity());
		} else if(Keybinds.MENU.clicked()) {
			Camera.get().setZoom(0.5f, -0.3333f);
		} else if(Keybinds.SLOT6.clicked()) {
			Camera.get().zoomTo(0.5f, 1f);
		}
		
		if(Keybinds.SLOT3.clicked()) {
			for(Fixture f = player.getBody().getFixtureList(); f != null; f = f.getNext()) {
					f.getFilterData().categoryBits = -1;
					f.getFilterData().maskBits = 0x0000;
			}
			System.out.println("PLAYER filter: " + player.getBody().getFixtureList().getFilterData().categoryBits);
			//player.getBody().getFixtureList().setSensor(!player.getBody().getFixtureList().isSensor());
			//System.out.println("Is player a sensor? " + player.getBody().getFixtureList().isSensor());
		}
		
		if(actionQueue != null && !player.getState().isActing()) {
			actionQueue.act();
			
			player.getContainer().getEntities()
				.stream()
				.filter(e -> e.getController() instanceof FollowController 
						&& ((FollowController) e.getController()).getLeader() == player)
				.forEach(e -> ((FollowController) e.getController()).fireEvent(actionQueue));
			
			setActionQueue(null);
		}
	}

	private void move(Vec2 direction) {
		if(player.getState().canMove()) {
			direction.normalize();
			player.getBody().applyForceToCenter(direction.mul(speed * speedMod));
			player.fireEvent(new StateChangeEvent(speedMod > 1 ? State.RUN : State.WALK));
			
			if(player.getBody().getLinearVelocity().x != 0 && !player.isFixDirection() && player.getRender() != null) {
				player.getRender().setFlipped(player.getBody().getLinearVelocity().x < 0);
			}
		}
	}
	
	private void dodge() {
		setActionQueue(new ActionEvent(State.QUICKSTEP, player.getState()) {
			@Override
			public void act() {
				player.getBody().setLinearDamping(5f);
				if(player.getBody().getLinearVelocity().length() == 0) {
					player.getBody().applyLinearImpulse(new Vec2(player.getRender().isFlipped() ? 3.5f : -3.5f, 0f),
							player.getBody().getWorldCenter());
				} else {
					player.getBody().getLinearVelocity().normalize();
					player.getBody().applyLinearImpulse(player.getBody().getLinearVelocity().mul(3.5f * speedMod),
							player.getBody().getWorldCenter());
				}

				player.fireEvent(new StateChangeEvent(State.QUICKSTEP));
				player.setFixDirection(true);
			}
		});
	}

	private void collapse(Vec2 force) {
		if(player.getRender() instanceof SpineRender) {
			SpineRender render = (SpineRender) player.getRender();
			
			//for(Slot s : render.getSkeleton().drawOrder) {
			Slot s = null;
			s = render.getSkeleton().findSlot("HEAD");
			/*do {
				s = render.getSkeleton().drawOrder.get((int) (Math.random() * render.getSkeleton().drawOrder.size()));
			} while(s.getAttachment() == null);*/
				if(s.getAttachment() != null) {
					Region region = (Region) s.getAttachment();
					Entity bone = new Entity(render.getSprite() + "/" + region.getName(),
							region.getWorldX(), region.getWorldY(), player.getContainer());
					bone.getRender().setFlipped(render.isFlipped());
					bone.setFixDirection(true);
					
					BodyDef bodyDef = new BodyDef();
					bodyDef.position.set((region.getWorldX() + (region.getWidth() / 2f)) / Stage_new.SCALE_FACTOR,
							(region.getWorldY() + (region.getHeight() / 2f)) / Stage_new.SCALE_FACTOR);
					bodyDef.type = BodyType.DYNAMIC;
					/*if(s.getSkeleton().getFlipX()) {
						bodyDef.angle = (float) Math.toRadians(s.getBone().getWorldRotation() + region.getRotation());
					} else {
						bodyDef.angle = (float) Math.toRadians(-s.getBone().getWorldRotation() - region.getRotation());
					}*/
					bodyDef.angle = (float) Math.toRadians(region.getRotation());

					PolygonShape bodyShape = new PolygonShape();
					bodyShape.setAsBox(region.getWidth() / Stage_new.SCALE_FACTOR / 2f, region.getHeight() / Stage_new.SCALE_FACTOR / 2f);

					FixtureDef boxFixture = new FixtureDef();
					boxFixture.density = 1f;
					boxFixture.shape = bodyShape;
					//boxFixture.filter.groupIndex = -2;
					boxFixture.filter.categoryBits = 0;
					boxFixture.userData = Math.abs(s.getBone().getWorldY());

					Body body = player.getContainer().getWorld().createBody(bodyDef);
					body.createFixture(boxFixture);
					body.setAngularDamping(1f);
					body.setGravityScale(1f);
					body.setLinearDamping(1f);
					body.setUserData(bone);
					body.setLinearVelocity(force);
					
					bone.setBody(body);
					
					System.out.println(body.getFixtureList().getUserData() + " " + body.getUserData().toString());
					s.setAttachment(null);
					
					FollowController hedF = new FollowController(bone, player);
					bone.setController(hedF);
					
					
					player.getContainer().addEntity(bone);
				}
			//}
			
			//player.getContainer().removeEntity(player);
		}
	}
	
	private void attack() {
		player.getContainer().getEntities().stream()
			.filter(e -> e.getController() instanceof FollowController 
					&& ((FollowController) e.getController()).getLeader() == player)
			.map(e -> (FollowController) e.getController())
			.forEach(e -> e.fireEvent(new ActionEvent(State.ATTACK, e.getFollower().getState())));
		
		/*ArrayList<Entity> entities = player.getContainer().getEntities();
		for(int x = 0; x < entities.size(); x++) {
			Entity e = entities.get(x);
			if(e.getController() instanceof FollowController && ((FollowController) e.getController()).getLeader() == player) {
				((FollowController) e.getController()).fireEvent(new ActionEvent(State.ATTACK, e.getState()));
			}
		}*/
	}
	
	private void defend(boolean release) {
		player.getContainer().getEntities().stream()
			.filter(e -> e.getController() instanceof FollowController 
					&& ((FollowController) e.getController()).getLeader() == player)
			.map(e -> (FollowController) e.getController())
			.forEach(e -> e.fireEvent(new ActionEvent(State.DEFEND, release ? State.IDLE : State.DEFEND)));
	}

	private void changeWeapon() {
		setActionQueue(new ActionEvent(State.CHANGE_WEAPON, player.getState()) {
			@Override
			public void act() {
				player.fireEvent(new StateChangeEvent(State.CHANGE_WEAPON));
				
				player.getEquipment().cycleEquippedWeapon();
			}
		});
	}
	
	private void jump(Vec2 impulse) {
		setActionQueue(new ActionEvent(State.JUMPING, player.getState()) {
			@Override
			public void act() {
				player.fireEvent(new StateChangeEvent(getState()));
				player.getBody().applyLinearImpulse(impulse, player.getBody().getWorldCenter());
				player.getBody().setGravityScale(1f);
				player.getBody().setLinearDamping(1f);
				player.getZBody().setGroundZ(player.getBody().getPosition().y * Stage_new.SCALE_FACTOR);
			}
		});
	}
	
	private void setActionQueue(ActionEvent action) {
		actionQueue = action;
	}
	
}
