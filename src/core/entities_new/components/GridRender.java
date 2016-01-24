package core.entities_new.components;

import java.awt.Point;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import org.lwjgl.util.vector.Vector4f;

import core.Camera;
import core.entities_new.Entity;
import core.entities_new.event.EntityEvent;
import core.render.SpriteList;
import core.render.transform.Transform;
import core.setups.Stage_new;
import core.utilities.AvoFileDecoder;
import core.utilities.Resources;
import net.lingala.zip4j.model.FileHeader;

public class GridRender implements Renderable, Serializable {

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
		byte[] data = AvoFileDecoder.decodeAVLStream(Resources.get().getResource(ref + "/" + ref + ".avl"));
		width = ByteBuffer.wrap(data, 0, 4).getInt() * Camera.ASPECT_RATIO;
		height = ByteBuffer.wrap(data, 4, 4).getInt() * Camera.ASPECT_RATIO;
		
		List<FileHeader> files = Resources.get().getSubList(ref);
		
		for(FileHeader header : files) {
			String name = header.getFileName();
			if(name.endsWith(".png")) {
				name = name.split(".png")[0];
				String loc = name.substring(name.lastIndexOf('[') + 1, name.lastIndexOf(']'));
				Point coord = new Point(Integer.parseInt(loc.split(",")[0]), Integer.parseInt(loc.split(",")[1]));
				
				tiles.add(coord);
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
	public void animate(float speed) {
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
		transform.x = (entity.getBody().getPosition().x * Stage_new.SCALE_FACTOR) + (tiles.get(index).y * height);
		transform.y = (entity.getBody().getPosition().y * Stage_new.SCALE_FACTOR) - (tiles.get(index).x * width);
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
	public void drawShadow() {
	}

	public float getDepth() {
		return depth;
	}

	public void setDepth(float depth) {
		this.depth = depth;
	}

	@Override
	public void fireEvent(EntityEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getAnimation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasAnimation(String animation) {
		// TODO Auto-generated method stub
		return false;
	}

}
