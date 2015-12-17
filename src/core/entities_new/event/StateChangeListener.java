package core.entities_new.event;

@FunctionalInterface
public interface StateChangeListener {

	public void stateChanged(StateChangeEvent e);
	
}
