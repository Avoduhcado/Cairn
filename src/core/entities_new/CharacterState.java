package core.entities_new;

public enum CharacterState {

	IDLE ("Idle", true),
	WALK ("Walk", true),
	RUN ("Run", true), 
	QUICKSTEP ("QuickStep", false),
	FALLING ("QuickStep", true), 
	LAND ("QuickStep", false), 
	ATTACK ("Attack", false);
	
	public final String animation;
	public final boolean loop;
	
	CharacterState(String animation, boolean loop) {
		this.animation = animation;
		this.loop = loop;
	}
	
	public boolean canMove() {
		return this == IDLE || this == WALK || this == RUN;
	}
	
}
