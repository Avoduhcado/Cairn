package core.scene;

import java.awt.geom.Rectangle2D;

import core.render.DrawUtils;
import core.setups.Stage;

public class HUD {

	public static void draw(Stage stage) {
		// Draw Player HP
		DrawUtils.fillRect(0f, 0f, 0f, 1f, new Rectangle2D.Double(100, 40, 400, 20));
		DrawUtils.fillRect(1f, 1f, 1f, 1f, new Rectangle2D.Double(102, 42,
				396 * (stage.getPlayer().getStats().getHealth().getCurrent() / stage.getPlayer().getStats().getHealth().getMax()), 16));

		// Draw Player Stamina
		DrawUtils.fillRect(0f, 0f, 0f, 1f, new Rectangle2D.Double(115, 80, 220, 20));
		DrawUtils.fillRect(1f, 1f, 1f, 1f, new Rectangle2D.Double(117, 82,
				216 * (stage.getPlayer().getStats().getStamina().getCurrent() / stage.getPlayer().getStats().getStamina().getMax()), 16));
	}

}
