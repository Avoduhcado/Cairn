package core.entities.utils.ai;

import java.io.Serializable;

import core.entities.interfaces.Combatant;
import core.entities.interfaces.Intelligent;

public abstract class AIAction implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public abstract void act(Intelligent host, Combatant target);
	
}
