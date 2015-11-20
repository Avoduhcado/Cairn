package core.entities_new;

import java.awt.Point;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;

import com.esotericsoftware.spine.Slot;
import com.esotericsoftware.spine.attachments.Region;

import core.scene.ShadowMap;
import core.utilities.keyboard.Keybinds;

public class PlayerController implements Controller {

	private Entity player;
		
	private float speed = 20f;
	private float speedMod = 1f;
	private Vec2 movement = new Vec2();
	
	public PlayerController(Entity player, boolean spawnFollower) {
		this.player = player;
		
		if(spawnFollower) {
			Entity dad = new Entity("Skull",
					player.getBody().getPosition().x * 30f, player.getBody().getPosition().y * 30f, player.getContainer());
			dad.setController(new FollowController(dad, player));
			for(Fixture f = dad.getBody().getFixtureList(); f != null; f = f.getNext()) {
				f.getFilterData().categoryBits = 0;
			}
			ShadowMap.get().addIllumination(dad, new Point(0, -105), 500f);
			player.getContainer().addEntity(dad);
			
			/*Entity lantern = new Entity("Lantern", 500, 100, player.getContainer());
			FollowController lanternControl = new FollowController(lantern, player);
			lanternControl.setOffset(0, -105f);
			lantern.setController(lanternControl);
			lantern.getBody().getFixtureList().getFilterData().categoryBits = 0;
			ShadowMap.setIllumination(lantern, new Point(0, 0));
			player.getContainer().addEntity(lantern);*/
		}
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
		
		if(!player.getState().isActing()) {
			if(Keybinds.DODGE.clicked()) {
				dodge();
			} else if(Keybinds.ATTACK.clicked()) {
				attack();
			} else if(Keybinds.DEFEND.clicked()) {
				defend();
			}
		}
		
		if(Keybinds.CONTROL.clicked()) {
			collapse(player.getBody().getLinearVelocity());
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
		if(player.getBody().getLinearVelocity().length() == 0) {
			player.getBody().applyLinearImpulse(new Vec2(player.getRender().isFlipped() ? 10f : -10f, 0f),
					player.getBody().getWorldCenter());
		} else {
			player.getBody().getLinearVelocity().normalize();
			player.getBody().applyLinearImpulse(player.getBody().getLinearVelocity().mul(10f * speedMod),
					player.getBody().getWorldCenter());
		}
		
		player.changeState(CharacterState.QUICKSTEP);
		player.setFixDirection(true);
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
					bodyDef.position.set((region.getWorldX() + (region.getWidth() / 2f)) / 30f,
							(region.getWorldY() + (region.getHeight() / 2f)) / 30f);
					bodyDef.type = BodyType.DYNAMIC;
					/*if(s.getSkeleton().getFlipX()) {
						bodyDef.angle = (float) Math.toRadians(s.getBone().getWorldRotation() + region.getRotation());
					} else {
						bodyDef.angle = (float) Math.toRadians(-s.getBone().getWorldRotation() - region.getRotation());
					}*/
					bodyDef.angle = (float) Math.toRadians(region.getRotation());

					PolygonShape bodyShape = new PolygonShape();
					bodyShape.setAsBox(region.getWidth() / 30f / 2f, region.getHeight() / 30f / 2f);

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
		player.changeState(CharacterState.ATTACK);
		
		player.setSubEntity(new Entity("Right Arm", (player.getBody().getPosition().x * 30f),
				(player.getBody().getPosition().y * 30f), player.getContainer()));
		player.getSubEntity().changeState(CharacterState.ATTACK);
		player.getSubEntity().getRender().setFlipped(player.getRender().isFlipped());

		player.getContainer().addEntity(player.getSubEntity());
	}
	
	private void defend() {
		player.changeState(CharacterState.DEFEND);

		player.setSubEntity(new Entity("Left Arm", player.getBody().getPosition().x * 30f,
				(player.getBody().getPosition().y * 30f) - 15f, player.getContainer()));
		player.getSubEntity().changeState(CharacterState.DEFEND);
		player.getSubEntity().getRender().setFlipped(player.getRender().isFlipped());

		player.getContainer().addEntity(player.getSubEntity());
	}
	
}
