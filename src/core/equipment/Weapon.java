package core.equipment;

import java.awt.geom.Rectangle2D;
import java.io.Serializable;

@Deprecated
public class Weapon implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private float damage;
	private boolean damaging;
	private boolean knockback;
	
	private String slot;
	private String name;
	private int combos;
	
	private AttackType attackType = AttackType.LIGHT;
	private Rectangle2D damageHitbox;
	private Rectangle2D attackRange;
	
	// TODO Static weapon loader that reads through spine files for animation/combo info
	
	public Weapon(String name, AttackType attackType, float damage) {
		this.name = name;
		this.attackType = attackType;
	}
	
	public float getDamage() {
		return damage;
	}

	public void setDamage(float damage) {
		this.damage = damage;
	}

	public boolean isDamaging() {
		return damaging;
	}
	
	public void setDamaging(boolean damaging) {
		this.damaging = damaging;
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
	
	public int getCombos() {
		return combos;
	}

	public void setCombos(int combos) {
		this.combos = combos;
	}

	public AttackType getAttackType() {
		return attackType;
	}

	public String getAttackAnim() {
		if(attackType == AttackType.UNARMED) {
			return name;
		}
		return attackType.animation;
	}
	
	public Rectangle2D getDamageHitbox() {
		return damageHitbox;
	}
	
	public void setDamageHitbox(Rectangle2D damageHitbox) {
		this.damageHitbox = damageHitbox;
	}
	
	public Rectangle2D getAttackRange() {
		return attackRange;
	}
	
	public void setAttackRange(Rectangle2D attackRange) {
		this.attackRange = attackRange;
	}
	
}
