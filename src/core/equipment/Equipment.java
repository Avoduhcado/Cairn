package core.equipment;

import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.ArrayList;

public class Equipment implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int equipped;
	private ArrayList<Weapon> weapons = new ArrayList<Weapon>();
	private boolean bell;
	
	private boolean combo;
	private int step;
	
	public static final Weapon lightMace = new Weapon("LIGHT MACE", AttackType.LIGHT, 10f);
	public static final Weapon heavyMace = new Weapon("HEAVY MACE", AttackType.HEAVY, 15f);
	public static final Weapon polearm = new Weapon("POLEARM", AttackType.THRUST, 7.5f);
	
	{
		lightMace.setDamageHitbox(new Rectangle2D.Double(0.2f, 0, 0.8f, 1));
		lightMace.setCombos(3);
		heavyMace.setDamageHitbox(new Rectangle2D.Double(0.65f, 0, 0.35f, 1));
		heavyMace.setCombos(2);
		polearm.setDamageHitbox(new Rectangle2D.Double(0.75f, 0.15f, 0.23f, 0.7f));
		polearm.setCombos(3);
	}
	
	private boolean block;
	private boolean superArmor;
	private boolean superInvulnerable;
	private boolean invulnerable;
	
	private boolean chugDrink;
	private int totalMilk = 10;
	private int currentMilk;
	
	public Equipment() {
		
	}
	
	public Weapon getEquippedWeapon() {
		return weapons.get(equipped);
	}
	
	public void cycleWeapon() {
		if(equipped < weapons.size() - 1) {
			equipped++;
		} else {
			equipped = 0;
		}
	}
	
	public void equipWeapon(Weapon weapon) {
		for(int i = 0; i<weapons.size(); i++) {
			if(weapons.get(i) == weapon) {
				equipped = i;
				break;
			}
		}
	}
	
	public void equipRandomWeapon() {
		equipped = (int) (Math.random() * weapons.size());
	}
	
	/*public void equipBestWeapon(Entity target, Entity attacker) {
		int bestWeapon = 0;
		for(int i = 1; i<weapons.size(); i++) {
			if(Point2D.distance(target.getX(), target.getY(), attacker.getX(), attacker.getY()) > weapons.get(i).getAttackRange().getMaxX()
					&& weapons.get(i).getAttackRange().getMaxX() < weapons.get(bestWeapon).getAttackRange().getMaxX()) {
				System.out.println("sdfsdf");
				bestWeapon = i;
			}
		}
		
		if(bestWeapon != equipped) {
			equipWeapon(weapons.get(bestWeapon));
		}
	}*/
	
	public ArrayList<Weapon> getWeapons() {
		return weapons;
	}
	
	public void addWeapon(Weapon weapon) {
		this.weapons.add(weapon);
	}
	
	public boolean hasBell() {
		return bell;
	}
	
	public void equipBell(boolean bell) {
		this.bell = bell;
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

	public boolean canChugDrink() {
		return chugDrink;
	}

	public void setChugDrink(boolean chugDrink) {
		this.chugDrink = chugDrink;
	}

	public int getTotalMilk() {
		return totalMilk;
	}

	public void setTotalMilk(int totalMilk) {
		this.totalMilk = totalMilk;
	}

	public int getCurrentMilk() {
		return currentMilk;
	}

	public void setCurrentMilk(int currentMilk) {
		// TODO Make sure this doesn't exceed total Milk
		this.currentMilk = currentMilk;
	}

	public void setCombo(int step, String animation) {
		this.combo = (step >= 0);
		this.step = step;
	}
	
	public boolean canCombo() {
		return combo;
	}

	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}
	
	public int getNextCombo() {
		return 0;
	}
	
}
