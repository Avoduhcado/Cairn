package core.scene.hud;

import java.awt.Color;

import org.lwjgl.util.vector.Vector2f;

import core.Camera;
import core.Theater;
import core.entities.Player;
import core.entities.interfaces.HUDController;
import core.render.SpriteIndex;
import core.utilities.MathFunctions;
import core.utilities.text.Text;

public abstract class HUDIcon implements HUDController {
	
	private final String box;
	private final String boxDecor1;
	private final String boxDecor2;
	
	private String icon;
	private String name;
	private float scale = Camera.ASPECT_RATIO;
	private Vector2f position = new Vector2f();
	
	private float transitionTime;
	private boolean swap;
	
	public HUDIcon(String type, Vector2f position) {
		this.box = "screen ui/" + type + " Box";
		this.boxDecor1 = box + " Top";
		this.boxDecor2 = box + " Bottom";
		
		this.position = position;
	}
	
	public void update() {
		if(swap) {
			transitionTime = MathFunctions.clamp(transitionTime + Theater.getDeltaSpeed(0.025f), 0, 0.15f);
			scale = MathFunctions.easeIn(transitionTime, 0.45f, Camera.ASPECT_RATIO - 0.45f, 0.15f);
			if(transitionTime >= 0.15f) {
				swap = false;
			}
		}
	}
	
	public void draw() {
		SpriteIndex.getSprite(box).setStill(true);
		SpriteIndex.getSprite(box).set2DScale(Camera.ASPECT_RATIO);
		SpriteIndex.getSprite(boxDecor1).setStill(true);
		SpriteIndex.getSprite(boxDecor1).set2DScale(Camera.ASPECT_RATIO);
		SpriteIndex.getSprite(boxDecor2).setStill(true);
		SpriteIndex.getSprite(boxDecor2).set2DScale(Camera.ASPECT_RATIO);
		if(getIcon() != null) {
			SpriteIndex.getSprite(getIcon()).setStill(true);
			SpriteIndex.getSprite(getIcon()).set2DScale(scale);
		}
		
		// TODO Draw at position
		//SpriteIndex.getSprite(box).draw(Camera.get().getDisplayWidth(0.115f), Camera.get().getDisplayHeight(0.8f));
		SpriteIndex.getSprite(boxDecor1).draw(position.x + (SpriteIndex.getSprite(box).getDrawWidth() * 0.4f),
				position.y - (SpriteIndex.getSprite(box).getDrawHeight() * 0.125f));
		SpriteIndex.getSprite(boxDecor2).draw(position.x - (SpriteIndex.getSprite(box).getDrawWidth() * 0.295f),
				position.y + (SpriteIndex.getSprite(box).getDrawHeight() * 0.215f));
		if(getName() != null) {
			Text.getDefault().setAll(0.45f, Color.BLACK, true, Color.WHITE, true, false);
			Text.getDefault().drawString(getName(), position.x + SpriteIndex.getSprite(box).getFinalWidth(),
					(position.y + SpriteIndex.getSprite(box).getFinalHeight()) - (Text.getDefault().getHeight(getName()) * 1.2f));
		}
		SpriteIndex.getSprite(box).draw(position.x, position.y);
		if(getIcon() != null) {
			SpriteIndex.getSprite(getIcon()).draw(position.x, position.y);
		}
	}
	
	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void swapEquipment(Player player) {
		transitionTime = 0f;
		swap = true;
	}
	
	public static HUDIcon weaponBox(Player player) {
		HUDIcon weapon = new HUDIcon("Weapon", new Vector2f(Camera.get().getDisplayWidth(0.03f), Camera.get().getDisplayHeight(0.7f))) {
			
			@Override
			public void swapEquipment(Player player) {
				super.swapEquipment(player);
				
				this.setIcon("HUD/" + player.getEquipment().getEquippedWeapon().getName());
				//this.setName(player.getEquipment().getEquippedWeapon().getName());
			}
		};
		
		weapon.swapEquipment(player);
		player.setHUDController(0, weapon);
		
		return weapon;
	}
	
	public static HUDIcon offhandBox(Player player) {
		HUDIcon offhand = new HUDIcon("Offhand", new Vector2f(Camera.get().getDisplayWidth(0.105f), Camera.get().getDisplayHeight(0.8f))) {
			
			@Override
			public void swapEquipment(Player player) {
				super.swapEquipment(player);
				
				this.setIcon("HUD/Milk");
				this.setName("" + player.getEquipment().getCurrentMilk());
			}
		};
		
		offhand.swapEquipment(player);
		player.setHUDController(1, offhand);
		
		return offhand;
	}

}
