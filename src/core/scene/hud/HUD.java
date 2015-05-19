package core.scene.hud;

import java.awt.Color;

import org.lwjgl.util.vector.Vector2f;

import core.Camera;
import core.render.textured.Sprite;
import core.setups.Stage;
import core.utilities.text.Text;

public class HUD {

	private Sprite meroSkull;
	
	private HUDBar healthBar;
	private HUDBar staminaBar;
	
	private HUDMagicBar magicBar;
	
	public HUD() {
		meroSkull = new Sprite("HUD/Mero");
		
		healthBar = new HUDBar("Health", new Vector2f(103, 37),
				new Vector2f(meroSkull.getWidth() * Camera.ASPECT_RATIO, 33.5f), Camera.ASPECT_RATIO);
		staminaBar = new HUDBar("Stamina", new Vector2f(94, 71),
				new Vector2f(meroSkull.getWidth() * Camera.ASPECT_RATIO, 67.5f), Camera.ASPECT_RATIO);
		
		magicBar = new HUDMagicBar("Magic", new Vector2f(meroSkull.getWidth() * Camera.ASPECT_RATIO, 91f), Camera.ASPECT_RATIO);
	}
	
	public void update() {
		// TODO Tween movements and adjust animations on bell
		magicBar.update();
	}
	
	public void draw(Stage stage) {
		healthBar.drawBar(stage.getPlayer().getStats().getHealth().getCurrent() / stage.getPlayer().getStats().getHealth().getMax());
		staminaBar.drawBar(stage.getPlayer().getStats().getStamina().getCurrent() / stage.getPlayer().getStats().getStamina().getMax());
		
		meroSkull.setStill(true);
		meroSkull.set2DScale(Camera.ASPECT_RATIO);
		meroSkull.draw(0, 0);
		
		healthBar.drawCase(1f);
		staminaBar.drawCase(1f);
		magicBar.drawCase(1f);
		
		magicBar.drawBell(stage.getPlayer().getStats().getMagic().getCurrent() / stage.getPlayer().getStats().getMagic().getMax());
		
		Text.getDefault().setStill(true);
		Text.getDefault().setColor(Color.BLACK);
		Text.getDefault().setDropColor(Color.WHITE);
		Text.getDefault().drawString(stage.getPlayer().getEquipment().getCurrentMilk() + "", 110,
				(meroSkull.getHeight() * 0.7f) * Camera.ASPECT_RATIO);
	}

}
