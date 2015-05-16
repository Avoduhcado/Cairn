package core.entities.utils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class Reputation implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private ArrayList<Faction> allies = new ArrayList<Faction>();
	private ArrayList<Faction> enemies = new ArrayList<Faction>();
	
	public Reputation(Faction ally, Faction enemy) {
		this.allies.add(ally);
		this.enemies.add(enemy);
	}
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
	}
	
	public boolean isAlly(Reputation target) {
		for(Faction f : target.getAllies()) {
			if(allies.contains(f)) {
				return true;
			}
		}
		
		return false;
	}
	
	public boolean isEnemy(Reputation target) {
		for(Faction f : target.getAllies()) {
			if(enemies.contains(f)) {
				return true;
			}
		}
		
		return false;
	}
	
	public ArrayList<Faction> getAllies() {
		return allies;
	}
	
	public void setAllies(ArrayList<Faction> allies) {
		this.allies = allies;
	}
	
	/**
	 * Add an allied faction to this reputation.
	 * @param faction to ally with
	 * @return True if successfully added, false if already allied or enemies
	 */
	public boolean addAlly(Faction faction) {
		if(!this.allies.contains(faction) && !this.enemies.contains(faction)) {
			this.allies.add(faction);
			return true;
		}
		return false;
	}
	
	public ArrayList<Faction> getEnemies() {
		return enemies;
	}
	
	public void setEnemies(ArrayList<Faction> enemies) {
		this.enemies = enemies;
	}
	
	/**
	 * Add an enemy faction to this reputation.
	 * @param faction to enemy against
	 * @return True if successfully added, false if already allied or enemies
	 */
	public boolean addEnemy(Faction faction) {
		if(!this.enemies.contains(faction) && !this.allies.contains(faction)) {
			this.enemies.add(faction);
			return true;
		}
		return false;
	}
	
}
