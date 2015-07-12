package core.entities;

import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;

import org.lwjgl.util.vector.Vector2f;

import core.Camera;
import core.Theater;
import core.interactions.InteractionListener;
import core.interactions.Script;
import core.render.SpriteIndex;
import core.setups.Stage;

public class Prop extends Entity implements core.entities.interfaces.Interactable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String interactData;
	private transient InteractionListener listener;
	private transient Script script;
	
	public Prop(int x, int y, String ref, float scale) {
		this.pos = new Vector2f(x, y);
		this.sprite = "props/" + ref;
		this.name = ref.contains("/") ? ref.split("/")[1] : ref;
		
		this.scale = scale;
		this.box = new Rectangle2D.Double(x, y,
				SpriteIndex.getSprite(sprite).getWidth() * scale, SpriteIndex.getSprite(sprite).getHeight() * scale);
	}
	
	public Prop(int x, int y, float width, float height, String ref) {
		this.pos = new Vector2f(x, y);
		this.sprite = "props/" + ref;
		this.name = ref.contains("/") ? ref.split("/")[1] : ref;
		
		this.scale = Camera.ASPECT_RATIO;
		this.box = new Rectangle2D.Double(x, y, width * scale, height * scale);
	}
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		
		if(name == null) {
			name = sprite.contains("/") ? sprite.split("/")[1] : sprite;
		}
	}
	
	@Override
	public void update() {
		if(script != null) {
			script.read();
		}
	}
	
	@Override
	public void draw() {
		SpriteIndex.getSprite(sprite).setFixedSize((int) Math.ceil(box.getWidth()), (int) Math.ceil(box.getHeight()));
		SpriteIndex.getSprite(sprite).draw((int) pos.x, (int) pos.y);

		drawDebug();
	}
	
	@Override
	public void setID() {
		this.ID = this.getClass().getSimpleName() + count++;
	}

	@Override
	public void setInteraction(String interactData) {
		this.interactData = interactData;
		buildInteractions();
	}

	@Override
	public void buildInteractions() {
		
	}

}
