package core.entities.utils;

public enum CharState {

	IDLE ("Idle"),
	WALK ("Walk"),
	RUN ("Run"),
	QUICKSTEP ("QuickStep"),
	ATTACK ("Attack"),
	DEFEND ("Defend"),
	RECOIL ("Recoil"),
	HIT ("Hit"),
	REVIVE ("Revive"),
	DEATH ("Death"),
	DEAD ("Dead");
	
	public String animation;
	
	CharState(String animation) {
		this.animation = animation;
	}
	
	public boolean canAct() {
		switch(this) {
		case ATTACK:
		case DEFEND:
		case DEATH:
		case DEAD:
		case HIT:
		case RECOIL:
		case REVIVE:
			return false;
		default:
			return true;
		}
	}
	
	/**
	 * @return -1 for Hit/Dying, 1 for defending, 2 for recoiling, 3 for dodging
	 */
	public int getHitState() {
		if(this == HIT || this == DEATH || this == DEAD) {
			return -1;
		} else if(this == DEFEND) {
			return 1;
		} else if(this == RECOIL) {
			return 2;
		} else if(this == QUICKSTEP) {
			return 3;
		}
		
		return 0;
	}
	
	@Override
	public String toString() {
		return this.name().toLowerCase();
	}
	
}