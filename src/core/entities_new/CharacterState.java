package core.entities_new;

public enum CharacterState {

	IDLE ("Idle", true),
	WALK ("Walk", true),
	RUN ("Run", true), 
	QUICKSTEP ("QuickStep", false),
	FALLING ("QuickStep", true), 
	LAND ("QuickStep", false), 
	ATTACK ("Attack", false), 
	DEFEND ("Defend", false), 
	HIT ("Hit", false);
	
	public String animation;
	public boolean loop;
	
	private String customAnimation;
	
	CharacterState(String animation, boolean loop) {
		this.animation = animation;
		this.loop = loop;
	}
	
	public boolean canMove() {
		return this == IDLE || this == WALK || this == RUN;
	}
	
	/**
	 * @return Whether quick-stepping, attacking, or defending
	 */
	public boolean isActing() {
		return this == QUICKSTEP || this == ATTACK || this == DEFEND;
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
