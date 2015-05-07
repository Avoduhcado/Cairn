package core.entities.utils.stats;

import java.io.Serializable;

public class Stats implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Health health;
	private Stamina stamina;
	private Magic magic;
	
	public Stats() {
		this.health = new Health(100, 100);
		this.stamina = new Stamina(100, 100);
		this.magic = new Magic(100, 100);
	}
	
	public Health getHealth() {
		return health;
	}
	
	public void setHealth(Health health) {
		this.health = health;
	}

	public Stamina getStamina() {
		return stamina;
	}

	public void setStamina(Stamina stamina) {
		this.stamina = stamina;
	}

	public Magic getMagic() {
		return magic;
	}

	public void setMagic(Magic magic) {
		this.magic = magic;
	}
	
}
