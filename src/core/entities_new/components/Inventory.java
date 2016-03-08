package core.entities_new.components;

import java.io.Serializable;
import core.entities_new.Entity;
import core.entities_new.EntityComponent;
import core.entities_new.State;
import core.entities_new.components.renders.SpineRender;
import core.entities_new.event.InventoryEvent;
import core.entities_new.event.StateChangeEvent;
import core.inventory.Equipment;

public class Inventory implements EntityComponent, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Entity owner;
	
	private Equipment equipment;
	
	public Inventory(Entity entity) {
		this.owner = entity;
		equipment = Equipment.testLoadout();
		//equipment = new Equipment();
	}
	
	public void cycle(InventoryEvent event) {
		equipment.cycleEquippedWeapon();
		
		changeRenderWeapon();
	}
	
	private void changeRenderWeapon() {
		owner.fireEvent(new StateChangeEvent(State.CHANGE_WEAPON));
		if(owner.render() && owner.getRender() instanceof SpineRender) {
			SpineRender render = (SpineRender) owner.getRender();
			render.setAttachment("WEAPON", equipment.getEquippedWeapon().getName().toUpperCase());
		}
	}
	
	public Equipment getEquipment() {
		return equipment;
	}
	
	public void setEquipment(Equipment equipment) {
		this.equipment = equipment;
	}
	
}
