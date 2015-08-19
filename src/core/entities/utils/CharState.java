package core.entities.utils;

public enum CharState {

	IDLE ("Idle"),
	WALK ("Walk"),
	RUN ("Run"),
	QUICKSTEP ("QuickStep"),
	ATTACK ("Attack"),
	CAST ("Castspell"),
	DEFEND ("Defend"),
	RECOIL ("Recoil"),
	HIT ("Hit"),
	HEAL ("Heal"),
	REVIVE ("Revive"),
	DEATH ("Death"),
	DEAD ("Dead");
	
	public String animation;
	
	CharState(String animation) {
		this.animation = animation;
	}
	
	public boolean canAct() {
		switch(this) {
		case WALK:
		case RUN:
		case IDLE:
			return true;
		default:
			return false;	
		}
	}
	
	public boolean canWalk() {
		switch(this) {
		case WALK:
		case RUN:
		case IDLE:
			return true;
		default:
			return false;	
		}
	}
	
	/**
	 * @return -1 for Hit/Dying, 1 for defending, 2 for recoiling, 3 for dodging
	 */
	public int getHitState() {
		switch(this) {
		case HIT:
		case DEATH:
		case DEAD:
			return -1;
		case DEFEND:
			return 1;
		case RECOIL:
			return 2;
		case QUICKSTEP:
			return 3;
		default:
			return 0;
		}
	}
	
	public boolean isDead() {
		switch(this) {
		case DEATH:
		case DEAD:
			return true;
		default:
			return false;
		}
	}
	
	@Override
	public String toString() {
		return this.name().toLowerCase();
	}
	
}