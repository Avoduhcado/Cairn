package core.entities_new.event;

import core.entities_new.State;

public class StateChangeEvent extends EntityEvent {

	private State state;
	private boolean forced;
	private int track;

	/**
	 * @param newState The new state to apply to the entity
	 * @param forced Whether or not this state should be forced to apply (will reset animation)
	 */
	public StateChangeEvent(State newState, boolean forced) {
		setState(newState);
		setForced(forced);
		setTrack(0);
	}

	/**
	 * Set a non forced, top level track State
	 * @param newState The new state to apply to the entity
	 */
	public StateChangeEvent(State newState) {
		this(newState, false);
	}
	
	public State getState() {
		return state;
	}

	public void setState(State newState) {
		this.state = newState;
	}

	public boolean isForced() {
		return forced;
	}

	public void setForced(boolean forced) {
		this.forced = forced;
	}

	public int getTrack() {
		return track;
	}
	
	public void setTrack(int track) {
		this.track = track;
	}
	
}
