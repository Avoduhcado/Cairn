package core.entities_new.components;

import core.entities_new.Entity;
import core.entities_new.EntityComponent;
import core.entities_new.event.InteractEvent;

public abstract class Interaction implements EntityComponent {

	protected Entity entity;
	protected Script script;
	
	public Interaction(Entity entity, Script script) {
		this.entity = entity;
		this.script = script;
	}
	
	public void interact(InteractEvent e) {
		if(!script.isBusyReading()) {
			script.startReading(e.getInteractor());
		}
	}
	
}
