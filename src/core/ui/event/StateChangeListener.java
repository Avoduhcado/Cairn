package core.ui.event;

@FunctionalInterface
public interface StateChangeListener {

	public void changeState(StateChangeEvent e);
	
}
