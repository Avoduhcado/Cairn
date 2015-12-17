package core.entities_new.components;

import java.util.ArrayList;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

import com.esotericsoftware.spine.Slot;
import com.esotericsoftware.spine.attachments.Region;

import core.Camera;
import core.entities_new.CharacterState;
import core.entities_new.Entity;
import core.entities_new.event.ActionEventListener;
import core.entities_new.event.EntityAction;
import core.setups.Stage_new;
import core.utilities.keyboard.Keybinds;

public class PlayerController implements Controllable {

	private Entity player;
		
	private float speed = 20f;
	private float speedMod = 1f;
	private Vec2 movement = new Vec2();
	
	private EntityAction actionQueue;
		
	public PlayerController(Entity player) {
		this.player = player;
	}

	@Override
	public void collectInput() {
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
			//attack();
		} else if(Keybinds.DEFEND.clicked()) {
			//defend();
		} else if(Keybinds.SLOT1.clicked()) {
			changeWeapon();
		} else if(Keybinds.SLOT2.clicked()) {
			jump(new Vec2(0, -6f));
		}
		
		if(Keybinds.DEFEND.press()) {
			player.setFixDirection(true);
		}
		if(Keybinds.DEFEND.released()) {
			player.setFixDirection(false);
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
			player.getBody().getFixtureList().setSensor(!player.getBody().getFixtureList().isSensor());
			System.out.println("Is player a sensor? " + player.getBody().getFixtureList().isSensor());
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
	
	@Override
	public void resolveState() {
		switch(player.getState()) {
		case WALK:
		case RUN:
			if(player.getBody().getLinearVelocity().length() <= 0.25f) {
				player.changeState(CharacterState.IDLE);
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void move(Vec2 direction) {
		if(player.getState().canMove()) {
			direction.normalize();
			player.getBody().applyForceToCenter(direction.mul(speed * speedMod));
			player.changeState(speedMod > 1 ? CharacterState.RUN : CharacterState.WALK);
			
			if(player.getBody().getLinearVelocity().x != 0 && !player.isFixDirection() && player.getRender() != null) {
				player.getRender().setFlipped(player.getBody().getLinearVelocity().x < 0);
			}
		}
	}
	
	@Override
	public void dodge() {
		setActionQueue(new EntityAction(CharacterState.QUICKSTEP, player.getState()) {
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

				player.changeState(CharacterState.QUICKSTEP);
				player.setFixDirection(true);
			}
		});
	}

	@Override
	public void collapse(Vec2 force) {
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
	
	@Override
	public void attack() {
		setActionQueue(new EntityAction(CharacterState.ATTACK, player.getState()) {
			private String currentAnimation = ((SpineRender) player.getRender()).getAnimation();
			
			@Override
			public void act() {
				player.getBody().setLinearDamping(5f);
				CharacterState.ATTACK.setCustomAnimation(this.getString());
				player.changeState(CharacterState.ATTACK);
				
				/*Entity rightArm = new Entity("Right Arm", (player.getBody().getPosition().x * Stage_new.SCALE_FACTOR),
						(player.getBody().getPosition().y * Stage_new.SCALE_FACTOR), player.getContainer());
				((SpineRender) rightArm.getRender()).setAttachment("WEAPON",
						player.getEquipment().getEquippedWeapon().getName().toUpperCase());
				rightArm.getRender().setFlipped(player.getRender().isFlipped());
				rightArm.changeState(CharacterState.ATTACK);
				rightArm.getBody().getFixtureList().getFilterData().categoryBits = 0;
				rightArm.getBody().setLinearVelocity(player.getBody().getLinearVelocity().clone());
				rightArm.getBody().setLinearDamping(5f);
				player.setSubEntity(rightArm);

				player.getContainer().addEntity(player.getSubEntity());*/
			}
			
			@Override
			public String getString() {
				if(getPrevState() == CharacterState.ATTACK) {
					SpineRender render = (SpineRender) player.getRender();
					String[] prevAnimation = currentAnimation.split("-");
					int currentPhase = Integer.valueOf(prevAnimation[1]);
					
					if(render.getSkeleton().getData().findAnimation(prevAnimation[0] + "-" + (currentPhase + 1)) != null) {
						return prevAnimation[0] + "-" + (currentPhase + 1);
					} else {
						return prevAnimation[0] + "-0";
					}
				} else {
					return player.getEquipment().getEquippedWeapon().getAnimation() + "-0";
				}				
			}
		});
	}
	
	public void defend() {
		setActionQueue(new EntityAction(CharacterState.DEFEND, player.getState()) {
			@Override
			public void act() {
				player.changeState(CharacterState.DEFEND);

				player.setSubEntity(new Entity("Left Arm", player.getBody().getPosition().x * Stage_new.SCALE_FACTOR,
						(player.getBody().getPosition().y * Stage_new.SCALE_FACTOR) - 15f, player.getContainer()));
				player.getSubEntity().changeState(CharacterState.DEFEND);
				player.getSubEntity().getRender().setFlipped(player.getRender().isFlipped());

				player.getContainer().addEntity(player.getSubEntity());
			}
		});
	}

	private void changeWeapon() {
		setActionQueue(new EntityAction(CharacterState.CHANGE_WEAPON, player.getState()) {
			@Override
			public void act() {
				player.changeState(CharacterState.CHANGE_WEAPON);
				
				player.getEquipment().cycleEquippedWeapon();
			}
		});
	}
	
	public void jump(Vec2 impulse) {
		setActionQueue(new EntityAction(CharacterState.JUMPING, player.getState()) {
			@Override
			public void act() {
				player.changeState(getType());
				player.getBody().applyLinearImpulse(impulse, player.getBody().getWorldCenter());
				player.getBody().setGravityScale(1f);
				player.getBody().setLinearDamping(1f);
				player.getZBody().setGroundZ(player.getBody().getPosition().y * Stage_new.SCALE_FACTOR);
			}
		});
	}
	
	private void setActionQueue(EntityAction action) {
		actionQueue = action;
	}
	
}
