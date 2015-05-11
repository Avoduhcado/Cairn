package core.equipment;

public enum AttackType {

	UNARMED ("Attack"),
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
