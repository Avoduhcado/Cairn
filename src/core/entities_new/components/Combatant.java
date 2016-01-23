package core.entities_new.components;

import core.entities_new.EntityComponent;
import core.entities_new.event.CombatEvent;

public interface Combatant extends EntityComponent {

	public void hit(CombatEvent e);
	
}
