package core.utilities;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import core.entities.Actor;
import core.scene.collisions.PathPolygon;

public class Pathfinder {

	private static ArrayList<PathPolygon> paths = new ArrayList<PathPolygon>();
	private static ArrayList<PathPolygon> openList = new ArrayList<PathPolygon>();
	private static ArrayList<PathPolygon> closedList = new ArrayList<PathPolygon>();
	
	public static void init(ArrayList<PathPolygon> pathPolys) {
		paths = pathPolys;
	}
	
	public static void buildPath(Point2D destination, Actor actor) {
		openList.clear();
		closedList.clear();
		
		int startPath = 0;
		Point2D actorPoint = new Point2D.Double(actor.getBox().getCenterX(), actor.getBox().getCenterY());
		
		for(int i = 1; i < paths.size(); i++) {
			if(paths.get(i).getCenter().distance(actorPoint) < paths.get(startPath).getCenter().distance(actorPoint)) {
				startPath = i;
			}
		}
		
		int endPath = 0;
		for(int i = 1; i < paths.size(); i++) {
			if(paths.get(i).getCenter().distance(destination) < paths.get(endPath).getCenter().distance(destination)) {
				endPath = i;
			}
		}
		
		navigate(startPath, endPath);
		
		System.out.println(closedList.get(getLowestClosedF()).getCenter());
	}
	
	private static void navigate(int start, int end) {
		openList.add(paths.get(start));
		
		while(!closedList.contains(paths.get(end)) && !openList.isEmpty()) {
			getNodes(openList.get(getLowestF()), paths.get(end));
		}
	}
	
	private static void getNodes(PathPolygon path, PathPolygon target) {
		openList.remove(path);
		closedList.add(path);
		
		for(int i = 0; i<path.getPaths().size(); i++) {
			if(!closedList.contains(path.getPaths().get(i))) {
				if(!openList.contains(path.getPaths().get(i))) {
					path.getPaths().get(i).setG((float) (path.getG() + path.getCenter().distance(path.getPaths().get(i).getCenter())));
					path.getPaths().get(i).findF(target);
					openList.add(path.getPaths().get(i));
				} else if(path.getPaths().get(i).getG() > (path.getG() + path.getCenter().distance(path.getPaths().get(i).getCenter()))) {
					path.getPaths().get(i).setG((float) (path.getG() + path.getCenter().distance(path.getPaths().get(i).getCenter())));
					path.getPaths().get(i).findF(target);
				}
			}
		}
	}
	
	private static int getLowestF() {
		int hold = 0;
		if(openList.size() > 1) {
			for(int x = 1; x<openList.size(); x++) {
				//System.out.println(openList.get(x).getF());
				if(openList.get(x).getF() < openList.get(hold).getF()) {
					hold = x;
				}
			}
		}
		
		return hold;
	}
	
	private static int getLowestClosedF() {
		int hold = 0;
		if(closedList.size() > 1) {
			for(int x = 1; x<closedList.size(); x++) {
				//System.out.println(closedList.get(x).getF() + " " + hold);
				if(closedList.get(x).getF() < closedList.get(hold).getF() || closedList.get(hold).getF() == 0) {
					hold = x;
				}
			}
		}

		return hold;
	}
	
}
