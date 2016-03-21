package core.entities_new;

public enum State {
	
	// Top Level States
	IDLE ("Idle", true),
	WALK ("Walk", true),
	RUN ("Run", true), 
	QUICKSTEP ("QuickStep", false),
	JUMPING ("QuickStep", true),
	FALLING ("QuickStep", true), 
	LAND ("QuickStep", false), 
	ATTACK ("Attack", false), 
	HEAVY_ATTACK ("HeavyAttack", false), 
	DEFEND_START ("DefendStart", false),
	DEFEND ("Defend", true), 
	DEFEND_END ("DefendEnd", false),
	HIT ("Hit", false),
	CHANGE_WEAPON ("ChangeWeapon", false);
	
	public String animation;
	public boolean loop;
	
	private String customAnimation;
	
	State(String animation, boolean loop) {
		this.animation = animation;
		this.loop = loop;
	}
	
	public boolean canMove() {
		return this == IDLE || this == WALK || this == RUN;
	}
	
	/**
	 * @return Whether quick-stepping, attacking
	 */
	public boolean isActing() {
		return this == QUICKSTEP || this == ATTACK;
	}
	
	public String getAnimation() {
		return customAnimation != null ? customAnimation : animation;
	}

	/**
	 * @return the customAnimation
	 */
	public String getCustomAnimation() {
		return customAnimation;
	}

	/**
	 * @param customAnimation the customAnimation to set
	 */
	public void setCustomAnimation(String customAnimation) {
		this.customAnimation = customAnimation;
	}
	
}
