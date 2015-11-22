package core.entities_new;

import java.awt.Point;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import org.jbox2d.common.Vec2;
import org.lwjgl.util.vector.Vector4f;

import core.Camera;
import core.render.SpriteList;
import core.render.Transform;
import core.utilities.AvoFileDecoder;

public class GridRender implements Render {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String sprite;
	private Entity entity;
	private List<Point> tiles = new LinkedList<Point>();
	private float width, height;
	
	private float depth = 0;
	
	private Transform transform;
	
	public GridRender(String name, Entity entity) {
		this.sprite = name;
		this.entity = entity;
		
		this.transform = new Transform();
		
		loadTiles(name);
	}
	
	private void loadTiles(String ref) {
		File backdropDirectory = new File(System.getProperty("resources") + "/sprites/" + ref);
		
		if(backdropDirectory.exists() && backdropDirectory.isDirectory()) {
			byte[] data = AvoFileDecoder.decodeAVLFile(new File(backdropDirectory.getAbsolutePath() + "/" + ref + ".avl"));
			width = ByteBuffer.wrap(data, 0, 4).getInt() * Camera.ASPECT_RATIO;
			height = ByteBuffer.wrap(data, 4, 4).getInt() * Camera.ASPECT_RATIO;
			
			String[] backdropNames = backdropDirectory.list();
			for(String n : backdropNames) {
				if(n.endsWith(".png")) {
					n = n.split(".png")[0];
					String loc = n.substring(n.lastIndexOf('[') + 1, n.lastIndexOf(']'));
					Point coord = new Point(Integer.parseInt(loc.split(",")[0]), Integer.parseInt(loc.split(",")[1]));
					
					tiles.add(coord);
				}
			}			
		}
	}

	@Override
	public void draw() {
		for(int i = 0; i<tiles.size(); i++) {
			setTransform(i);
			SpriteList.get(sprite + "/" + getSprite(i)).draw(transform);
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
		transform.x = (entity.getBody().getPosition().x * 30f) + (tiles.get(index).y * height);
		transform.y = (entity.getBody().getPosition().y * 30f) - (tiles.get(index).x * width);
		transform.flipX = isFlipped();
		transform.scaleY = 1f;
		transform.scaleX = 1f;
		transform.rotation = 0;
		transform.color = new Vector4f(1f, 1f, 1f, 1f);
	}

	@Override
	public String getSprite() {
		return sprite;
	}

	public String getSprite(int index) {
		return sprite + "[" + tiles.get(index).x + "," + tiles.get(index).y + "]";
	}

	@Override
	public void shadow() {
	}

	public float getDepth() {
		return depth;
	}

	public void setDepth(float depth) {
		this.depth = depth;
	}

}
