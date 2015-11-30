package core.inventory;

import java.io.Serializable;

public abstract class Item implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String ID;
	private String name;
	
	public Item(String ID, String name) {
		setID(ID);
		setName(name);
	}
	
	public String getID() {
		return ID;
	}
	
	public void setID(String iD) {
		ID = iD;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
}
