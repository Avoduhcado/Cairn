package core.entities.interfaces;

import java.awt.geom.Point2D;

import core.entities.utils.ai.Intelligence;
import core.setups.Stage;

public interface Intelligent {

	public void alert(Combatant target);
	public void approach(Point2D target);
	public void think(Stage stage);
	
	public Intelligence getIntelligence();
	
}
