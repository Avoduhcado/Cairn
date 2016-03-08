package core.entities_new.components.interactions;

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
		if(!script.isBusyReading() && e.getInteractType() != InteractEvent.INTERRUPT) {
			script.startReading(e.getInteractor());
		} else if(script.isBusyReading() && e.getInteractType() == InteractEvent.INTERRUPT) {
			script.interrupt();
		}
	}
	
}
