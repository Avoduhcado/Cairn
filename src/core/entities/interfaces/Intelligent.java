package core.entities.interfaces;

import core.entities.utils.ai.Intelligence;
import core.setups.Stage;

public interface Intelligent {

	public void think(Stage stage);
	
	public Intelligence getIntelligence();
	
}
