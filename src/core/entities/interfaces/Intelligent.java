package core.entities.interfaces;

import java.awt.geom.Point2D;

import core.entities.utils.ai.Intelligence;
import core.setups.Stage;
@Deprecated
public interface Intelligent {

	public void approach(Point2D target);
	public void think(Stage stage);
	
	public Intelligence getIntelligence();
	public void changeIntelligence(Intelligence intelligence);
	
}
