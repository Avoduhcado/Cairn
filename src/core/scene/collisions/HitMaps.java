package core.scene.collisions;

import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class HitMaps {

	private static final int cellSize = 128;
	
	private static HashMap<Point, ArrayList<Collidable>> collisionMap = new HashMap<Point, ArrayList<Collidable>>();
	
	public static void populateMap(HashMap<Point, ArrayList<Collidable>> map, ArrayList<Collidable> collidables) {
		for(int i = 0; i<collidables.size(); i++) {
			Point p = new Point((int) (collidables.get(i).getBounds().getX() / cellSize), 
					(int) (collidables.get(i).getBounds().getY() / cellSize));
			//System.out.println(p.getX() + " " + p.getY());
			if(map.containsKey(p)) {
				map.get(p).add(collidables.get(i));
			} else {
				map.put(p, new ArrayList<Collidable>(Arrays.asList(collidables.get(i))));
			}
			
			Point p2 = new Point((int) (collidables.get(i).getBounds().getMaxX() / cellSize),
					(int) (collidables.get(i).getBounds().getMaxY() / cellSize));
			if(p2.x > p.x || p2.y > p.y) {
				for(int j = p.x; j<=p2.x; j++) {
					for(int k = p.y; k<=p2.y; k++) {
						if(map.containsKey(new Point(j, k))) {
							map.get(new Point(j, k)).add(collidables.get(i));
						} else {
							map.put(new Point(j, k), new ArrayList<Collidable>(Arrays.asList(collidables.get(i))));
						}
					}
				}
			}
		}
	}

	public static HashMap<Point, ArrayList<Collidable>> getCollisionMap() {
		return collisionMap;
	}
	
	public static ArrayList<Collidable> getMapSector(HashMap<Point, ArrayList<Collidable>> map, Point p) {
		p = new Point((int) (p.getX() / cellSize), (int) (p.getY() / cellSize));
		
		if(map.containsKey(p)) {
			return map.get(p);
		} else {
			return new ArrayList<Collidable>();
		}
	}
	
	public static ArrayList<Collidable> getMapSectors(HashMap<Point, ArrayList<Collidable>> map, Rectangle2D rect) {
		ArrayList<Collidable> platforms = new ArrayList<Collidable>();
		
		Point p = new Point((int) (rect.getX() / cellSize), (int) (rect.getY() / cellSize));
		if(map.containsKey(p)) {
			platforms.addAll(map.get(p));
		}
		
		Point p2 = new Point((int) (rect.getMaxX() / cellSize), (int) (rect.getMaxY() / cellSize));
		if(p.x < p2.x) {
			for(int j = p.x; j<=p2.x; j++) {
				// Top Row
				if(map.containsKey(new Point(j, p.y))) {
					platforms.addAll(map.get(new Point(j, p.y)));
				}
				// Bottom Row
				if(map.containsKey(new Point(j, p2.y))) {
					platforms.addAll(map.get(new Point(j, p2.y)));
				}
			}
		}
		if(p.y < p2.y) {
			for(int j = p.y; j<=p2.y; j++) {
				// Left Side
				if(map.containsKey(new Point(p.x, j))) {
					platforms.addAll(map.get(new Point(p.x, j)));
				}
				// Right Side
				if(map.containsKey(new Point(p2.x, j))) {
					platforms.addAll(map.get(new Point(p2.x, j)));
				}
			}
		}
		
		//System.out.println(platforms.size());
		return platforms;
	}
	
}
