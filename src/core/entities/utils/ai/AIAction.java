package core.entities.utils.ai;

import core.entities.interfaces.Combatant;
import core.entities.interfaces.Intelligent;

public abstract class AIAction {

	public abstract void act(Intelligent host, Combatant target);
	
}
