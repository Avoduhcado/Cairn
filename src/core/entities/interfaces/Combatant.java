package core.entities.interfaces;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import core.entities.utils.Reputation;
import core.equipment.Equipment;

public interface Combatant {

	public void attack();
	public void defend();
	public void hit(Combatant attacker);
	public void takeDamage(Combatant attacker, float damageMod, boolean superArmor);
	public void endCombat();
	
	public Reputation getReputation();
	public ArrayList<Rectangle2D> getHitBoxes(Combatant attacker);
	public Equipment getEquipment();
	
}
