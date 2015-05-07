package core.scene;

import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class HitMaps_old {

	private static final int cellSize = 128;
	
	private static HashMap<Point, ArrayList<Platform>> collisionMap = new HashMap<Point, ArrayList<Platform>>();
	private static HashMap<Point, ArrayList<Platform>> eventMap = new HashMap<Point, ArrayList<Platform>>();
	private static HashMap<Point, ArrayList<Platform>> terrainMap = new HashMap<Point, ArrayList<Platform>>();
	
	public static void populateMap(HashMap<Point, ArrayList<Platform>> map, ArrayList<Platform> platforms) {
		for(int i = 0; i<platforms.size(); i++) {
			Point p = new Point((int) (platforms.get(i).getBox().getX() / cellSize), (int) (platforms.get(i).getBox().getY() / cellSize));
			//System.out.println(p.getX() + " " + p.getY());
			if(map.containsKey(p)) {
				map.get(p).add(platforms.get(i));
			} else {
				map.put(p, new ArrayList<Platform>(Arrays.asList(platforms.get(i))));
			}
			
			Point p2 = new Point((int) (platforms.get(i).getBox().getMaxX() / cellSize), (int) (platforms.get(i).getBox().getMaxY() / cellSize));
			if(p2.x > p.x) {
				for(int j = p.x; j<=p2.x; j++) {
					// Top Row
					if(map.containsKey(new Point(j, p.y))) {
						map.get(new Point(j, p.y)).add(platforms.get(i));
					} else {
						map.put(new Point(j, p.y), new ArrayList<Platform>(Arrays.asList(platforms.get(i))));
					}
					// Bottom Row
					if(map.containsKey(new Point(j, p2.y))) {
						map.get(new Point(j, p2.y)).add(platforms.get(i));
					} else {
						map.put(new Point(j, p2.y), new ArrayList<Platform>(Arrays.asList(platforms.get(i))));
					}
				}
			}
			if(p2.y > p.y) {
				for(int j = p.y; j<=p2.y; j++) {
					// Left Side
					if(map.containsKey(new Point(p.x, j))) {
						map.get(new Point(p.x, j)).add(platforms.get(i));
					} else {
						map.put(new Point(p.x, j), new ArrayList<Platform>(Arrays.asList(platforms.get(i))));
					}
					// Right Side
					if(map.containsKey(new Point(p2.x, j))) {
						map.get(new Point(p2.x, j)).add(platforms.get(i));
					} else {
						map.put(new Point(p2.x, j), new ArrayList<Platform>(Arrays.asList(platforms.get(i))));
					}
				}
			}
		}
	}

	public static HashMap<Point, ArrayList<Platform>> getCollisionMap() {
		return collisionMap;
	}
	
	public static HashMap<Point, ArrayList<Platform>> getEventMap() {
		return eventMap;
	}
	
	public static HashMap<Point, ArrayList<Platform>> getTerrainMap() {
		return terrainMap;
	}
	
	public static ArrayList<Platform> getMapSector(HashMap<Point, ArrayList<Platform>> map, Point p) {
		if(map.containsKey(p)) {
			return map.get(p);
		} else {
			return new ArrayList<Platform>();
		}
	}
	
	public static ArrayList<Platform> getMapSectors(HashMap<Point, ArrayList<Platform>> map, Rectangle2D r) {
		ArrayList<Platform> platforms = new ArrayList<Platform>();
		
		Point p = new Point((int) (r.getX() / cellSize), (int) (r.getY() / cellSize));
		if(map.containsKey(p)) {
			platforms.addAll(map.get(p));
		}
		
		Point p2 = new Point((int) (r.getMaxX() / cellSize), (int) (r.getMaxY() / cellSize));
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
