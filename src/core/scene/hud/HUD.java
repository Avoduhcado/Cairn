package core.scene.hud;

import org.lwjgl.util.vector.Vector2f;

import core.Camera;
import core.render.textured.Sprite;
import core.setups.Stage;

public class HUD {

	private boolean enabled;
	
	private Sprite meroSkull;
	
	private HUDBar healthBar;
	private HUDBar staminaBar;
	
	private HUDMagicBar magicBar;
	
	private HUDIcon weaponIcon;
	private HUDIcon itemIcon;
	private HUDIcon spellIcon;
	
	public HUD() {
		enabled = true;
		
		meroSkull = new Sprite("HUD/Mero");
		
		healthBar = new HUDBar("Health", new Vector2f(103, 37),
				new Vector2f(meroSkull.getWidth() * Camera.ASPECT_RATIO, 33.5f), Camera.ASPECT_RATIO);
		staminaBar = new HUDBar("Stamina", new Vector2f(94, 71),
				new Vector2f(meroSkull.getWidth() * Camera.ASPECT_RATIO, 67.5f), Camera.ASPECT_RATIO);
		
		magicBar = new HUDMagicBar("Magic", new Vector2f(meroSkull.getWidth() * Camera.ASPECT_RATIO, 91f), Camera.ASPECT_RATIO);
		
		weaponIcon = new HUDIcon(IconType.WEAPON);
		itemIcon = new HUDIcon(IconType.ITEM);
		spellIcon = new HUDIcon(IconType.SPELL);
	}
	
	public void update(Stage stage) {
		magicBar.update(stage.getPlayer().getStats().getMagic().getCurrent() / stage.getPlayer().getStats().getMagic().getMax());
		healthBar.update(1f);
		
		weaponIcon.update(stage);
		itemIcon.update(stage);
		spellIcon.update(stage);
	}
	
	public void draw(Stage stage) {
		healthBar.drawBar(stage.getPlayer().getStats().getHealth().getCurrent() / stage.getPlayer().getStats().getHealth().getMax());
		staminaBar.drawBar(stage.getPlayer().getStats().getStamina().getCurrent() / stage.getPlayer().getStats().getStamina().getMax());
		
		meroSkull.setStill(true);
		meroSkull.set2DScale(Camera.ASPECT_RATIO);
		meroSkull.draw(0, 0);
		
		healthBar.drawCase();
		staminaBar.drawCase();
		magicBar.drawCase(1f);
		
		magicBar.drawBell();
		
		weaponIcon.draw();
		itemIcon.draw();
		spellIcon.draw();
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

}
