package core.entities.utils.ai;

import core.entities.interfaces.Combatant;
import core.entities.interfaces.Intelligent;

public interface AIAction {
	
	public void act(Intelligent host, Combatant target);
	
}
