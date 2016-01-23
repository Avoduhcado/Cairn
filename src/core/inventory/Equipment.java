package core.inventory;

import java.io.Serializable;
import java.util.ArrayList;

public class Equipment implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		
	private ArrayList<Weapon> weapons = new ArrayList<Weapon>();
	private int equippedWeapon;
	
	public ArrayList<Weapon> getWeapons() {
		return weapons;
	}

	public void setWeapons(ArrayList<Weapon> weapons) {
		this.weapons = weapons;
	}

	public void addWeapon(Weapon weapon) {
		this.weapons.add(weapon);
	}
	
	public Weapon getEquippedWeapon() {
		return weapons.get(equippedWeapon);
	}

	public void setEquippedWeapon(int equippedWeapon) {
		this.equippedWeapon = equippedWeapon;
	}
	
	public void cycleEquippedWeapon() {
		equippedWeapon++;
		if(equippedWeapon >= weapons.size()) {
			equippedWeapon = 0;
		}
	}

}
