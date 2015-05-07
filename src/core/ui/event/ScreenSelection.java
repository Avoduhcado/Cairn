package core.ui.event;

import java.awt.geom.Rectangle2D;

import core.Camera;
import core.render.DrawUtils;
import core.ui.TextBox;
import core.utilities.MathFunctions;
import core.utilities.keyboard.Keybinds;

public class ScreenSelection extends TextBox {
	
	private int selection = 0;
	
	public ScreenSelection(String text, float x, float y) {
		super("<t+>" + text, x, y, null, false);
		
		center();
	}
	
	@Override
	public void update() {
		if(Keybinds.MENU_RIGHT.clicked()) {
			selection = MathFunctions.clamp(selection + 1, 0, 1);
		} else if(Keybinds.MENU_LEFT.clicked()) {
			selection = MathFunctions.clamp(selection - 1, 0, 1);
		}
		
		if(Keybinds.CONFIRM.clicked() && event != null) {
			event.processedResult(selection);
		}
	}

	@Override
	public void draw() {
		DrawUtils.drawGradient(0f, 0f, 0f, new Rectangle2D.Double(0, Camera.get().getDisplayHeight(0.65f), 
				Camera.get().getDisplayWidth(), Camera.get().getDisplayHeight(0.35f)), true);
		
		lines.get(0).append(selection == 0 ? "cwhite" : "cgray");
		lines.get(1).append(selection == 1 ? "cwhite" : "cgray");
		
		lines.get(0).draw(Camera.get().getDisplayWidth(0.4f) - (lines.get(0).getWidth() / 2f), y, getLength());
		lines.get(1).draw(Camera.get().getDisplayWidth(0.6f) - (lines.get(1).getWidth() / 2f), y, getLength());
	}
	
}
