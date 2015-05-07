package core.equipment;

import java.awt.geom.Rectangle2D;
import java.io.Serializable;

public class Equipment implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Weapon weapon;
	public static Weapon lightMace = new Weapon("LIGHT MACE", AttackType.LIGHT);
	public static Weapon heavyMace = new Weapon("HEAVY MACE", AttackType.HEAVY);
	public static Weapon polearm = new Weapon("POLEARM", AttackType.THRUST);
	
	{
		lightMace.setDamageHitbox(new Rectangle2D.Double(0.55f, 0, 0.45f, 1));
		heavyMace.setDamageHitbox(new Rectangle2D.Double(0.65f, 0, 0.35f, 1));
		polearm.setDamageHitbox(new Rectangle2D.Double(0.75f, 0.15f, 0.23f, 0.7f));
	}
	
	private boolean block;
	private boolean superArmor;
	private boolean superInvulnerable;
	private boolean invulnerable;
	
	public Equipment() {
		// TODO Remove dependancy? Or at least make a better default "Unarmed" weapon
		weapon = new Weapon("LIGHT MACE", AttackType.LIGHT);
	}
	
	public Weapon getWeapon() {
		return weapon;
	}
	
	public void setWeapon(Weapon weapon) {
		this.weapon = weapon;
	}

	public boolean isBlock() {
		return block;
	}

	public void setBlock(boolean block) {
		this.block = block;
	}

	public boolean isSuperArmor() {
		return superArmor;
	}

	public void setSuperArmor(boolean superArmor) {
		this.superArmor = superArmor;
		if(superArmor) {
			superInvulnerable = false;
		}
	}

	public boolean isSuperInvulnerable() {
		return superInvulnerable;
	}

	public void setSuperInvulnerable(boolean superInvulnerable) {
		this.superInvulnerable = superInvulnerable;
	}

	public boolean isInvulnerable() {
		return invulnerable;
	}

	public void setInvulnerable(boolean invulnerable) {
		this.invulnerable = invulnerable;
	}

	
}
