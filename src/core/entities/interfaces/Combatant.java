package core.entities.interfaces;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import core.equipment.Equipment;

public interface Combatant {

	public void attack();
	public void defend();
	public void hit(Combatant attacker);
	public void takeDamage(Combatant attacker, float damageMod, boolean superArmor);
	public void endCombat();
	
	public void setUpCombatData(String attackName);
	public Rectangle2D getAttackBox();
	public ArrayList<Rectangle2D> getHitBoxes(Combatant attacker);
	public Equipment getEquipment();
	
}
