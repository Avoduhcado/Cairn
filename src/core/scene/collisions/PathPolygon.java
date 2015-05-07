package core.scene.collisions;

import java.awt.Polygon;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class PathPolygon extends Polygon {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Point2D center;
	private ArrayList<PathPolygon> paths = new ArrayList<PathPolygon>();
	private float f;
	private float g;
	
	public PathPolygon() {
		super();
	}
	
	public PathPolygon(Polygon poly) {
		super();
		if(poly.npoints == 3) {
			center = new Point2D.Double();
			for(int x = 0; x<poly.npoints; x++) {
				this.addPoint(poly.xpoints[x], poly.ypoints[x]);
				center.setLocation(center.getX() + poly.xpoints[x], center.getY() + poly.ypoints[x]);
			}
			center.setLocation(center.getX() / 3, center.getY() / 3);
		}
	}
	
	public boolean isTouching(Polygon poly) {
		boolean touching = false;
		for(int i = 0; i<this.npoints; i++) {
			Line2D side = new Line2D.Double(xpoints[i], ypoints[i], xpoints[i + 1 > 2 ? 0 : i + 1], ypoints[i + 1 > 2 ? 0 : i + 1]);
			for(int j = 0; j<poly.npoints; j++) {
				Line2D side2 = new Line2D.Double(poly.xpoints[j], poly.ypoints[j], 
						poly.xpoints[j + 1 > 2 ? 0 : j + 1], poly.ypoints[j + 1 > 2 ? 0 : j + 1]);
				if(side.intersectsLine(side2)) {
					touching = true;
				}
			}
		}
		
		return touching;
	}
	
	public void createPath(PathPolygon poly) {
		if(!paths.contains(poly)) {
			paths.add(poly);
			poly.createPath(this);
		}
	}
	
	public void findF(PathPolygon target) {
		f = (float) (g + center.distance(target.getCenter()));
	}
	
	public float getF() {
		return f;
	}
	
	public float getG() {
		return g;
	}
	
	public void setG(float g) {
		this.g = g;
	}
	
	public ArrayList<PathPolygon> getPaths() {
		return paths;
	}
	
	public Point2D getCenter() {
		return center;
	}

}
