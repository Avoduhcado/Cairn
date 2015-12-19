package core.entities_new;

import org.jbox2d.dynamics.contacts.Contact;

import com.esotericsoftware.spine.Slot;
import com.esotericsoftware.spine.attachments.Box2dAttachment;

import core.entities_new.components.SpineRender;
import core.entities_new.utils.SensorData;
import core.setups.Stage_new;
import core.setups.WorldContainer;

public class EntityData {

	private String name;
	private float x, y;
	private WorldContainer container;
	private Contact contact;

	public EntityData(String name, float x, float y, WorldContainer container, Contact contact) {
		this.name = name;
		this.x = x;
		this.y = y;
		this.container = container;
		this.contact = contact;
	}
	
	public Entity createEntity() {
		Entity entity = new Entity(name, x, y, container);
		
		applyContact(entity);
		
		return entity;
	}

	private void applyContact(Entity entity) {
		if(contact != null) {
			if(entity.getRender() instanceof SpineRender) {
				SpineRender render = (SpineRender) entity.getRender();
				
				for(Slot s : render.getSkeleton().getSlots()) {
					if(!(s.getAttachment() instanceof Box2dAttachment)) {
						continue;
					}
					Box2dAttachment attachment = (Box2dAttachment) s.getAttachment();
					if(attachment == null) {
						continue;
					}
					((SensorData) attachment.getBody().getUserData()).setType(SensorData.IGNORE);
				}
			}
			
			entity.getBody().setGravityScale(2f);
			entity.getBody().setLinearDamping(1f);
			entity.getZBody().setGroundZ((entity.getBody().getPosition().y * Stage_new.SCALE_FACTOR) +
					(float) ((Math.random() * 15f) + 15f));
		}
	}
	
}
