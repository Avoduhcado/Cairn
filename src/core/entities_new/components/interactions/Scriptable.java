package core.entities_new.components.interactions;

import core.entities_new.Entity;

public interface Scriptable {

	public void startReading(Entity reader);
	public void read();
	public void endReading();
	public void interrupt();
	
	public boolean isBusyReading();
	
}
