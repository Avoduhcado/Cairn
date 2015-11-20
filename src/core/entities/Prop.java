package core.entities;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.LinkedList;

import org.lwjgl.util.vector.Vector2f;

import core.Camera;
import core.entities.interfaces.Interactable;
import core.interactions.Script;
import core.render.SpriteIndex;
import core.utilities.AvoFileDecoder;
@Deprecated
public class Prop extends Entity implements Interactable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Script script;
	
	private LinkedList<SpriteData> textures = new LinkedList<SpriteData>();
	
	public Prop(int x, int y, String ref, float scale) {
		this.pos = new Vector2f(x, y);
		this.sprite = "props/" + ref;
		this.name = ref.contains("/") ? ref.split("/")[1] : ref;
		
		this.scale = scale;
		this.box = new Rectangle2D.Double(x, y,
				SpriteIndex.getSprite(sprite).getWidth() * scale, SpriteIndex.getSprite(sprite).getHeight() * scale);
	}
	
	/*public Prop(int x, int y, float width, float height, String ref) {
		this.pos = new Vector2f(x, y);
		this.sprite = "props/" + ref;
		this.name = ref.contains("/") ? ref.split("/")[1] : ref;
		
		this.scale = Camera.ASPECT_RATIO;
		this.box = new Rectangle2D.Double(x, y, width * scale, height * scale);
	}*/
	
	public Prop(int x, int y, float width, float height, String ref) {
		this.pos = new Vector2f(x, y);
		//this.sprite = "props/" + ref;
		this.name = ref;
		
		this.scale = Camera.ASPECT_RATIO;
		this.box = new Rectangle2D.Double(x, y, width * scale, height * scale);
	}
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		
		if(name == null) {
			name = sprite.contains("/") ? sprite.split("/")[1] : sprite;
		}
		if(textures == null) {
			setTextures(loadTextures((int) pos.x, (int) pos.y, name));
		}
	}
	
	public static Prop loadProp(int x, int y, String ref) {
		File propDirectory = new File(System.getProperty("resources") + "/sprites/props/" + ref);
		Dimension size = new Dimension();
		
		if(propDirectory.exists() && propDirectory.isDirectory()) {
			byte[] data = AvoFileDecoder.decodeAVLFile(new File(propDirectory.getAbsolutePath() + "/" + ref + ".avl"));
			size.width = ByteBuffer.wrap(data, 0, 4).getInt();
			size.height = ByteBuffer.wrap(data, 4, 4).getInt();
			
			Prop prop = new Prop(x, y, size.width, size.height, ref);
			String[] propNames = propDirectory.list();
			for(String n : propNames) {
				if(n.endsWith(".png")) {
					n = n.split(".png")[0];
					String loc = n.substring(n.lastIndexOf('[') + 1, n.lastIndexOf(']'));
					Point coord = new Point(Integer.parseInt(loc.split(",")[0]), Integer.parseInt(loc.split(",")[1]));
					
					prop.addTexture(coord.x, coord.y, "props/" + ref + "/" + n);
				}
			}
			
			return prop;
		}
		
		return null;
	}
	
	@Override
	public void update() {
		if(script != null) {
			script.read();
		}
	}
	
	@Override
	public void draw() {
		for(SpriteData s : textures) {
			if(Point2D.distance(s.getDrawX(pos.x, (float) box.getWidth()), s.getDrawY(pos.y, (float) box.getHeight()),
					Camera.get().frame.getCenterX(), Camera.get().frame.getCenterY()) < Camera.DRAW_DISTANCE) {
				SpriteIndex.getSprite(s.getSprite()).setFixedSize((int) Math.ceil(box.getWidth()), (int) Math.ceil(box.getHeight()));
				SpriteIndex.getSprite(s.getSprite()).draw(s.getDrawX(pos.x, (float) box.getWidth()), s.getDrawY(pos.y, (float) box.getHeight()));
			}
		}
		
		//SpriteIndex.getSprite(sprite).setFixedSize((int) Math.ceil(box.getWidth()), (int) Math.ceil(box.getHeight()));
		//SpriteIndex.getSprite(sprite).draw((int) pos.x, (int) pos.y);

		drawDebug();
	}
	
	public void setTextures(LinkedList<SpriteData> sprites) {
		this.textures = sprites;
	}
	
	public void addTexture(int x, int y, String ref) {
		this.textures.add(new SpriteData(x, y, ref));
	}
	
	public LinkedList<SpriteData> loadTextures(int x, int y, String ref) {
		LinkedList<SpriteData> sprites = new LinkedList<SpriteData>();
		
		File propDirectory = new File(System.getProperty("resources") + "/sprites/props/" + ref);
		Dimension size = new Dimension();
		
		if(propDirectory.exists() && propDirectory.isDirectory()) {
			byte[] data = AvoFileDecoder.decodeAVLFile(new File(propDirectory.getAbsolutePath() + "/" + ref + ".avl"));
			size.width = ByteBuffer.wrap(data, 0, 4).getInt();
			size.height = ByteBuffer.wrap(data, 4, 4).getInt();
			this.box = new Rectangle2D.Double(x, y, size.width, size.height);
			
			String[] propNames = propDirectory.list();
			for(String n : propNames) {
				if(n.endsWith(".png")) {
					n = n.split(".png")[0];
					String loc = n.substring(n.lastIndexOf('[') + 1, n.lastIndexOf(']'));
					Point coord = new Point(Integer.parseInt(loc.split(",")[0]), Integer.parseInt(loc.split(",")[1]));
					
					sprites.add(new SpriteData(coord.x, coord.y, "props/" + ref + "/" + n));
					//prop.addTexture(coord.x, coord.y, "props/" + ref + "/" + n);
				}
			}
		}
		
		return sprites;
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
		script = new Script(this, interactData);
	}

	private class SpriteData implements Serializable {
	
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private int x, y;
		private String sprite;
		
		public SpriteData(int x, int y, String ref) {
			this.x = x;
			this.y = y;
			this.sprite = ref;
		}
		
		public int getDrawX(float posX, float width) {
			return (int) (posX + (width * y));
		}
		
		public int getDrawY(float posY, float height) {
			return (int) (posY - (height * x));
		}

		public String getSprite() {
			return sprite;
		}
	}

}
