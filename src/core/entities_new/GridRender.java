package core.entities_new;

import java.awt.Dimension;
import java.awt.Point;
import java.io.File;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import org.jbox2d.common.Vec2;
import org.lwjgl.util.vector.Vector4f;

import core.entities.Backdrop;
import core.render.SpriteList;
import core.render.Transform;
import core.utilities.AvoFileDecoder;

public class GridRender implements Render {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String sprite;
	private List<Tile> tiles = new LinkedList<Tile>();
	
	private float depth = 0;
	
	private Transform transform;
	
	public GridRender(String name) {
		this.sprite = name;
		
		this.transform = new Transform();
		
		loadTiles(name);
	}
	
	private void loadTiles(String ref) {
		File backdropDirectory = new File(System.getProperty("resources") + "/sprites/" + ref);
		Dimension size = new Dimension();
		
		if(backdropDirectory.exists() && backdropDirectory.isDirectory()) {
			byte[] data = AvoFileDecoder.decodeAVLFile(new File(backdropDirectory.getAbsolutePath() + "/" + ref + ".avl"));
			size.width = ByteBuffer.wrap(data, 0, 4).getInt();
			size.height = ByteBuffer.wrap(data, 4, 4).getInt();
			
			//Backdrop backdrop = new Backdrop(x, y, size.width, size.height, ref, depth);
			String[] backdropNames = backdropDirectory.list();
			for(String n : backdropNames) {
				if(n.endsWith(".png")) {
					n = n.split(".png")[0];
					String loc = n.substring(n.lastIndexOf('[') + 1, n.lastIndexOf(']'));
					Point coord = new Point(Integer.parseInt(loc.split(",")[0]), Integer.parseInt(loc.split(",")[1]));
					
					tiles.add(new Tile(coord.x, coord.y));
					//backdrop.addTexture(coord.x, coord.y, "backdrops/" + ref + "/" + n);
				}
			}			
		}
	}

	@Override
	public void draw() {
		for(Tile t : tiles) {
			SpriteList.get(sprite + "/" + sprite + "[" + t.x + "," + t.y + "]").draw(transform);
		}
	}

	@Override
	public void debugDraw() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void animate(float speed, Vec2 position) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setAnimation(String animation, boolean loop) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public boolean isFlipped() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public void setFlipped(boolean flipped) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Transform getTransform() {
		return transform;
	}

	@Override
	public void setTransform(int index) {
		transform.x = transform.x;
		transform.y = transform.y;
		transform.flipX = isFlipped();
		transform.scaleY = 1f;
		transform.scaleX = 1f;
		transform.rotation = 0;
		transform.color = new Vector4f(1f, 1f, 1f, 1f);
	}

	@Override
	public String getSprite() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public float getWidth() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getHeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void shadow() {
		// TODO Auto-generated method stub
		
	}
	
	private class Tile implements Serializable {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private int x, y;
		
		public Tile(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
	}

}
