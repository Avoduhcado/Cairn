package core.ui.screen;

import java.awt.geom.Rectangle2D;

import core.Camera;
import core.render.DrawUtils;
import core.ui.TextBox;
import core.ui.utils.Align;

public class ScreenText extends TextBox {
		
	public ScreenText(String text, float x, float y) {
		super("<t+>" + text, x, y, null, true);
		
		setAlign(Align.CENTER);
	}
	
	@Override
	public void draw() {
		DrawUtils.drawGradient(0f, 0f, 0f, new Rectangle2D.Double(0, Camera.get().getDisplayHeight(0.65f), 
				Camera.get().getDisplayWidth(), Camera.get().getDisplayHeight(0.35f)), true);
		
		super.draw();
	}

}
