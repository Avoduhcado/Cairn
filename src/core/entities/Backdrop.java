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
import core.render.SpriteIndex;
import core.utilities.AvoFileDecoder;
import core.utilities.MathFunctions;

public class Backdrop extends Entity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		
	private float depth;
	
	private LinkedList<SpriteData> textures = new LinkedList<SpriteData>();
	
	public Backdrop(float x, float y, String ref, float scale, float depth) {
		this.pos = new Vector2f(x, y);
		this.sprite = "backdrops/" + ref;
		this.name = ref;
		
		this.scale = scale;
		this.box = new Rectangle2D.Double(x, y,
				SpriteIndex.getSprite(sprite).getWidth() * scale, SpriteIndex.getSprite(sprite).getHeight() * scale);
		
		this.depth = depth;
	}
	
	public Backdrop(float x, float y, float width, float height, String ref, float depth) {
		this.pos = new Vector2f(x, y);
		//this.sprite = "backdrops/" + ref;
		this.name = ref;
		
		this.scale = Camera.ASPECT_RATIO;
		this.box = new Rectangle2D.Double(x, y, width * scale, height * scale);
		
		this.depth = depth;
	}
	
	private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
		in.defaultReadObject();
		
		if(textures == null) {
			setTextures(loadTextures((int) pos.x, (int) pos.y, name));
		}
	}

	public static Backdrop loadBackdrop(int x, int y, String ref, float depth) {
		File backdropDirectory = new File(System.getProperty("resources") + "/sprites/backdrops/" + ref);
		Dimension size = new Dimension();
		
		if(backdropDirectory.exists() && backdropDirectory.isDirectory()) {
			byte[] data = AvoFileDecoder.decodeAVLFile(new File(backdropDirectory.getAbsolutePath() + "/" + ref + ".avl"));
			size.width = ByteBuffer.wrap(data, 0, 4).getInt();
			size.height = ByteBuffer.wrap(data, 4, 4).getInt();
			
			Backdrop backdrop = new Backdrop(x, y, size.width, size.height, ref, depth);
			String[] backdropNames = backdropDirectory.list();
			for(String n : backdropNames) {
				if(n.endsWith(".png")) {
					n = n.split(".png")[0];
					String loc = n.substring(n.lastIndexOf('[') + 1, n.lastIndexOf(']'));
					Point coord = new Point(Integer.parseInt(loc.split(",")[0]), Integer.parseInt(loc.split(",")[1]));
					
					backdrop.addTexture(coord.x, coord.y, "backdrops/" + ref + "/" + n);
				}
			}
			
			return backdrop;
		}
		
		return null;
	}

	@Override
	public void draw() {
		for(SpriteData t : textures) {
			if(Point2D.distance(t.getClipX(pos.x, getWidth()), t.getClipY(pos.y, getHeight()),
					Camera.get().frame.getCenterX(), Camera.get().frame.getCenterY()) < Camera.DRAW_DISTANCE) {
				SpriteIndex.getSprite(t.getSprite()).setStill(true);
				SpriteIndex.getSprite(t.getSprite()).setFixedSize(getWidth(), getHeight());
				SpriteIndex.getSprite(t.getSprite()).draw(t.getDrawX(pos.x, getWidth()), t.getDrawY(pos.y, getHeight()));
			}
		}
		
		/*SpriteIndex.getSprite(sprite).setStill(true);
		//SpriteIndex.getSprite(sprite).setIntScale(true);
		if(depth == -1f) {
			SpriteIndex.getSprite(sprite).setFixedSize(Camera.get().displayWidth, Camera.get().displayHeight);
		} else {
			SpriteIndex.getSprite(sprite).setFixedSize((int) Math.ceil(box.getWidth()), (int) Math.ceil(box.getHeight()));
			//SpriteIndex.getSprite(sprite).setFixedSize((int) box.getWidth(), (int) box.getHeight());
		}
		//SpriteIndex.getSprite(sprite).draw(pos.x + offset.x, pos.y + offset.y);
		SpriteIndex.getSprite(sprite).draw((float) (pos.x - Camera.get().frame.getX() - (Camera.get().frame.getX() * depth)),
				(float)  (pos.y - Camera.get().frame.getY() - (Camera.get().frame.getY() * depth)));*/

		drawDebug();
	}
	
	@Override
	public void drawDebug() {
		//DrawUtils.setColor(new Vector3f(1f, 0, 0.25f));
		//DrawUtils.drawRect(pos.x + offset.x, pos.y + offset.y, getBox());
		//DrawUtils.drawRect((float) (pos.x + (Camera.get().frame.getX() * depth)),
			//	(float)  (pos.y + (Camera.get().frame.getY() * depth)), getBox());
	}

	public void setTextures(LinkedList<SpriteData> sprites) {
		this.textures = sprites;
	}
	
	public void addTexture(int x, int y, String ref) {
		this.textures.add(new SpriteData(x, y, ref));
	}
	
	public LinkedList<SpriteData> loadTextures(int x, int y, String ref) {
		LinkedList<SpriteData> sprites = new LinkedList<SpriteData>();
		File backdropDirectory = new File(System.getProperty("resources") + "/sprites/backdrops/" + ref);
		Dimension size = new Dimension();
		
		if(backdropDirectory.exists() && backdropDirectory.isDirectory()) {
			byte[] data = AvoFileDecoder.decodeAVLFile(new File(backdropDirectory.getAbsolutePath() + "/" + ref + ".avl"));
			size.width = ByteBuffer.wrap(data, 0, 4).getInt();
			size.height = ByteBuffer.wrap(data, 4, 4).getInt();
			this.box = new Rectangle2D.Double(x, y, size.width, size.height);
			
			String[] backdropNames = backdropDirectory.list();
			for(String n : backdropNames) {
				if(n.endsWith(".png")) {
					n = n.split(".png")[0];
					String loc = n.substring(n.lastIndexOf('[') + 1, n.lastIndexOf(']'));
					Point coord = new Point(Integer.parseInt(loc.split(",")[0]), Integer.parseInt(loc.split(",")[1]));
					
					sprites.add(new SpriteData(coord.x, coord.y, "backdrops/" + ref + "/" + n));
				}
			}
		}
		
		return sprites;
	}
	
	public void update() {
		
	}
	
	public float getDepth() {
		return depth;
	}
	
	public void setDepth(float depth) {
		if(this.depth <= 0) {
			this.depth = MathFunctions.clamp(depth, -1, -0.1f);
		} else {
			this.depth = MathFunctions.clamp(depth, 0.1f, 1);
		}
	}
	
	private int getWidth() {
		if(depth == -1) {
			return Camera.get().displayWidth;
		} else {
			return (int) Math.ceil(box.getWidth());
		}
	}
	
	private int getHeight() {
		if(depth == -1) {
			return Camera.get().displayHeight;
		} else {
			return (int) Math.ceil(box.getHeight());
		}
	}

	@Override
	public void setID() {
		this.ID = this.getClass().getSimpleName();
		//this.ID = this.getClass().getSimpleName() + count++;
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
		
		public int getClipX(float posX, float width) {
			return (int) (posX + (width * y));
		}
		
		public int getClipY(float posY, float height) {
			return (int) (posY - (height * x));
		}
		
		public int getDrawX(float posX, float width) {
			return (int) ((posX + (width * y)) - Camera.get().frame.getX() - (Camera.get().frame.getX() * depth));
		}
		
		public int getDrawY(float posY, float height) {
			return (int) ((posY - (height * x)) - Camera.get().frame.getY() - (Camera.get().frame.getY() * depth));
		}

		public String getSprite() {
			return sprite;
		}
	}
	
}
