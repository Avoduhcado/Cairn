package core.equipment;

import java.awt.geom.Rectangle2D;
import java.io.Serializable;

public class Weapon implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private boolean damage;
	private boolean knockback;
	private String slot;
	private String name;
	private AttackType attackType = AttackType.LIGHT;
	private Rectangle2D damageHitbox;
	
	public Weapon(String name, AttackType attackType) {
		this.name = name;
		this.attackType = attackType;
	}
	
	public boolean isDamaging() {
		return damage;
	}
	
	public void setDamage(boolean damage) {
		this.damage = damage;
	}
	
	public boolean isReversedKnockback() {
		return knockback;
	}
	
	public void setKnockback(boolean knockback) {
		this.knockback = knockback;
	}
	
	public String getSlot() {
		return slot;
	}
	
	public void setSlot(String slot) {
		this.slot = slot;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public AttackType getAttackType() {
		return attackType;
	}

	public String getAttackAnim() {
		return attackType.animation;
	}
	
	public Rectangle2D getDamageHitbox() {
		return damageHitbox;
	}
	
	public void setDamageHitbox(Rectangle2D damageHitbox) {
		this.damageHitbox = damageHitbox;
	}
	
}
