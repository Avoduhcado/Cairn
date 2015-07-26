package core.scene.hud;

import java.awt.Color;

import core.Camera;
import core.Theater;
import core.render.SpriteIndex;
import core.setups.Stage;
import core.utilities.MathFunctions;
import core.utilities.text.Text;

enum IconType {
	WEAPON, ITEM, SPELL;
}

public class HUDIcon_old {

	private final String background = "HUD/Item Box";
	private final IconType type;
	
	private String icon;
	private String name;
	private float scale = Camera.ASPECT_RATIO;
	
	private float transitionTime;
	private boolean swap;
	
	public HUDIcon_old(IconType type) {
		this.type = type;
		icon = "";
		name = "";
	}
	
	public void update(Stage stage) {
		if(swap) {
			transitionTime = MathFunctions.clamp(transitionTime + Theater.getDeltaSpeed(0.025f), 0, 0.15f);
			scale = MathFunctions.easeIn(transitionTime, 0.45f, Camera.ASPECT_RATIO - 0.45f, 0.15f);
			if(transitionTime >= 0.15f) {
				swap = false;
			}
		}
		
		switch(type) {
		case WEAPON:
			if(!icon.endsWith(stage.getPlayer().getEquipment().getEquippedWeapon().getName())) {
				transitionTime = 0f;
				swap = true;
			}
			icon = "HUD/" + stage.getPlayer().getEquipment().getEquippedWeapon().getName();
			name = stage.getPlayer().getEquipment().getEquippedWeapon().getName();
			break;
		case ITEM:
			if(stage.getPlayer().getEquipment().getCurrentMilk() > 0) {
				icon = "HUD/Milk";
			} else {
				icon = null;
			}
			name = "" + stage.getPlayer().getEquipment().getCurrentMilk();
			break;
		case SPELL:
			// TODO put spells in equipment 
			icon = "HUD/Spell 1";
			name = "Spell";
			break;
		}
	}
	
	public void draw() {
		/*
		 * 0.03125 , 0.8
		 * 0.115 , 0.8
		 * 0.03125 , 0.615
		 */
		SpriteIndex.getSprite(background).setStill(true);
		SpriteIndex.getSprite(background).set2DScale(Camera.ASPECT_RATIO);
		if(icon != null) {
			SpriteIndex.getSprite(icon).setStill(true);
			SpriteIndex.getSprite(icon).set2DScale(scale);
		}
		switch(type) {
		case WEAPON:
			SpriteIndex.getSprite(background).draw(Camera.get().getDisplayWidth(0.115f), Camera.get().getDisplayHeight(0.8f));
			if(icon != null) {
				SpriteIndex.getSprite(icon).draw(Camera.get().getDisplayWidth(0.115f), Camera.get().getDisplayHeight(0.8f));
			}
			if(name != null) {
				Text.getDefault().setAll(0.35f, Color.WHITE, true, Color.BLACK, true, false);
				Text.getDefault().drawString(name, Camera.get().getDisplayWidth(0.12875f),
						Camera.get().getDisplayHeight(0.8f) + (SpriteIndex.getSprite(background).getHeight() * Camera.ASPECT_RATIO));
			}
			break;
		case ITEM:
			SpriteIndex.getSprite(background).draw(Camera.get().getDisplayWidth(0.03125f), Camera.get().getDisplayHeight(0.8f));
			if(icon != null) {
				SpriteIndex.getSprite(icon).draw(Camera.get().getDisplayWidth(0.03125f), Camera.get().getDisplayHeight(0.8f));
			}
			if(name != null) {
				Text.getDefault().setAll(0.35f, Color.BLACK, true, Color.WHITE, true, false);
				Text.getDefault().drawString(name, Camera.get().getDisplayWidth(0.045f), 
						Camera.get().getDisplayHeight(0.8f) + (SpriteIndex.getSprite(background).getHeight() * Camera.ASPECT_RATIO));
			}
			break;
		case SPELL:
			SpriteIndex.getSprite(background).draw(Camera.get().getDisplayWidth(0.07f), Camera.get().getDisplayHeight(0.65f));
			if(icon != null) {
				SpriteIndex.getSprite(icon).draw(Camera.get().getDisplayWidth(0.075f), Camera.get().getDisplayHeight(0.65f));
			}
			if(name != null) {
				Text.getDefault().setAll(0.35f, Color.BLACK, true, Color.WHITE, true, false);
				Text.getDefault().drawString(name, Camera.get().getDisplayWidth(0.07f) +
						(SpriteIndex.getSprite(background).getWidth() * Camera.ASPECT_RATIO),
						Camera.get().getDisplayHeight(0.665f));
			}
			break;
		}
	}
	
}
