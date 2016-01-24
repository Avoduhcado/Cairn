package core.ui.event;

@FunctionalInterface
public interface TimeListener {

	public void timeStep(TimeEvent e);
	
}
