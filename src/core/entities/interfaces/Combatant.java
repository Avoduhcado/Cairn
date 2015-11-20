package core.entities.interfaces;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import core.entities.Entity;
import core.entities.utils.Reputation;
import core.entities.utils.stats.Stats;
import core.equipment.Equipment;
@Deprecated
public interface Combatant {

	public void attack();
	public void defend();
	public void hit(Combatant attacker);
	public void takeDamage(Combatant attacker, float damageMod, boolean superArmor);
	public void endCombat();
	
	public Reputation getReputation();
	public Stats getStats();
	public boolean canReach(Entity target);
	public ArrayList<Rectangle2D> getHitBoxes(Combatant attacker);
	public Equipment getEquipment();
	
}
