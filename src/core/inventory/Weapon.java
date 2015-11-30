package core.inventory;

public class Weapon extends Item {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private float damage = 1;
	private String animation = "attack";
	
	public Weapon(String ID, String name) {
		super(ID, name);
	}

	public float getDamage() {
		return damage;
	}

	public void setDamage(float damage) {
		this.damage = damage;
	}

	public String getAnimation() {
		return animation;
	}

	public void setAnimation(String animation) {
		this.animation = animation;
	}

}
