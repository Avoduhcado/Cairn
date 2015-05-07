package core.entities.utils;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import core.Theater;

@Deprecated
public class Animation {
	
	private String name;
	
	private float step;
	private float speed = 0.05758954f;
	private int frame = 0;
	private int maxFrame;
	
	private ArrayList<BoxData> hitBoxes = new ArrayList<BoxData>();
	
	public Animation(String ref) {
		if(ref.contains("^")) {
			String[] temp = ref.split("\\^");
			this.maxFrame = Integer.parseInt(temp[1]);
		}
		this.name = ref;		
	}
	
	public void loadHitBoxes(BoxData data) {
		hitBoxes.add(data);
	}
	
	public int animate() {
		if(maxFrame > 1) {
			step += Theater.getDeltaSpeed(0.025f);
			if (step >= speed) {
				step = 0f;
				frame++;
				if (frame >= maxFrame) {
					frame = 0;
				}
			}
		}
		
		return frame;
	}
	
	public String getName() {
		return name;
	}
	
	public int getFrame() {
		return frame;
	}
	
	public void resetFrame() {
		this.frame = 0;
	}
	
	public Rectangle2D getHitBox(Rectangle2D box) {
		if(!hitBoxes.isEmpty()) {
			return hitBoxes.get((int) (frame / (maxFrame / hitBoxes.size()))).getRectangle(box);
		}
		
		return null;
	}
	
}
