package core.equipment;

public enum AttackType {

	LIGHT ("LightAttack"),
	HEAVY ("HeavyAttack"),
	THRUST ("ThrustAttack");
	
	String animation;
	
	AttackType(String animation) {
		this.animation = animation;
	}
	
	public String getAnimation() {
		return animation;
	}
	
}
