package core.entities;

import java.awt.geom.Rectangle2D;

import org.lwjgl.util.vector.Vector2f;

import core.Theater;
import core.interactions.InteractionListener;
import core.setups.Stage;

public class InteractEntity extends Entity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private InteractionListener listener;
	
	public InteractEntity(int x, int y, String ref) {
		this.pos = new Vector2f(x, y);
		
		if(ref != null) {
			this.sprite = ref;
			this.name = ref;
		} else {
			this.name = "BlankInteraction";
			this.box = new Rectangle2D.Double(x, y, 50, 50);
		}
	}

	@Override
	public void update() {
		if(listener != null) {
			if(((Stage) Theater.get().getSetup()).getPlayer().getBox().intersects(getBox())) {
				//listener.playerCollide();
			}
		}
	}
	
	@Override
	public void toDraw() {
		if(sprite != null) {
			
		} else {
			drawDebug();
		}
	}
	
	public void setInteraction(InteractionListener listener) {
		this.listener = listener;
	}

	@Override
	public void setID() {
		this.ID = "Butts";
	}

}
