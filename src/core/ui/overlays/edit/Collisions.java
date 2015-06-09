package core.ui.overlays.edit;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

import core.Camera;
import core.Input;
import core.render.DrawUtils;
import core.scene.collisions.PathPolygon;
import core.ui.Button;
import core.ui.CheckBox;
import core.ui.ElementGroup;
import core.ui.utils.ClickEvent;
import core.utilities.keyboard.Keybinds;
import core.utilities.mouse.MouseInput;

public class Collisions {

	private ArrayList<Polygon> polys = new ArrayList<Polygon>();
	private ArrayList<PathPolygon> paths = new ArrayList<PathPolygon>();
	private Polygon currentPoly;
	private Point editPoint;
	
	private ElementGroup stuff;
	
	private CheckBox buildPoly;
	private CheckBox editPolyPoints;
	private Button splitPolyPoint;
		
	public Collisions() {
		//EditMenu.polys = map.getCollisionPolys();
		
		buildPoly = new CheckBox("Build Poly", 20, 20, null);
		buildPoly.setStill(true);
		buildPoly.addEvent(new ClickEvent(buildPoly) {
			public void click() {
				if(buildPoly.isChecked()) {
					currentPoly = new Polygon();
				} else {
					if(currentPoly.npoints > 1)
						polys.add(currentPoly);
					currentPoly = null;
				}
			}
		});
		
		editPolyPoints = new CheckBox("Edit Points", 20, (float) buildPoly.getBounds().getMaxY(), null);
		editPolyPoints.setStill(true);
		editPolyPoints.setEnabled(!polys.isEmpty());
		editPolyPoints.addEvent(new ClickEvent(editPolyPoints) {
			public void click() {
				if(polys.isEmpty()) {
					editPolyPoints.setChecked(false);
				}
			}
		});
		
		splitPolyPoint = new Button("Split Point", 20, (float) editPolyPoints.getBounds().getMaxY(), 0, null);
		splitPolyPoint.setStill(true);
		splitPolyPoint.setEnabled(false);
		splitPolyPoint.addEvent(new ClickEvent(splitPolyPoint) {
			public void click() {
				splitPoint();
				splitPolyPoint.setEnabled(false);
			}
		});
		
		stuff = new ElementGroup();
		stuff.add(buildPoly);
		stuff.add(editPolyPoints);
		stuff.add(splitPolyPoint);
		stuff.addFrame("Menu2");
	}
	
	public void update() {
		if(currentPoly != null) {
			if(Mouse.isInsideWindow() && Input.mouseClicked()) {
				if(Input.mouseClicked(0)) {
					addPolyPoint();
				} else if(Input.mouseClicked(1)) {
					if(currentPoly.npoints > 1)
						polys.add(currentPoly);
					currentPoly = null;
					buildPoly.setChecked(false);
				}
			}
		}
		
		splitPolyPoint.update();
		
		// Only enable editing points if polygons exist
		if(editPolyPoints.isChecked()) {
			if(polys.isEmpty()) {
				editPolyPoints.setChecked(false);
			} else {
				if(Mouse.isInsideWindow() && Input.mouseClicked()) {
					selectPolyPoint();
					if(editPoint == null) {
						splitPolyPoint.setEnabled(false);
					}
				}
			}
		}
		
		if(editPoint != null) {
			splitPolyPoint.setEnabled(true);
			if(Mouse.isInsideWindow() && Input.mouseHeld()) {
				movePoint();
			}
			if(Keybinds.CANCEL.clicked()) {
				deletePoint();
				editPolyPoints.setChecked(!polys.isEmpty());
				editPolyPoints.setEnabled(!polys.isEmpty());
				splitPolyPoint.setEnabled(false);
			}
		} else {
			splitPolyPoint.setEnabled(false);
		}

		buildPoly.update();
		editPolyPoints.update();
	}
	
	public void draw() {
		for(Polygon p : polys) {
			
			DrawUtils.setColor(new Vector3f(0.8f, 0f, 0.4f));
			DrawUtils.applyCameraScale();
			DrawUtils.drawPoly(0, 0, p);

			if(editPolyPoints.isChecked()) {
				for(int i = 0; i<p.npoints; i++) {
					DrawUtils.setColor(new Vector3f(0f, 0f, 1f));
					DrawUtils.applyCameraScale();
					DrawUtils.drawRect(p.xpoints[i] - 10, p.ypoints[i] - 10, new Rectangle2D.Double(0, 0, 20, 20));
				}
			}
		}
		
		for(PathPolygon p : paths) {
			DrawUtils.setColor(new Vector3f(0.8f, 0f, 0.4f));
			DrawUtils.drawPoly(0, 0, p);
			for(PathPolygon p2 : p.getPaths()) {
				DrawUtils.setColor(new Vector3f(1f, 0f, 1f));
				DrawUtils.applyCameraScale();
				DrawUtils.drawLine(new Line2D.Double(p.getCenter(), p2.getCenter()));
			}
		}
		
		if(currentPoly != null) {
			DrawUtils.setColor(new Vector3f(1f, 0f, 0f));
			DrawUtils.applyCameraScale();
			DrawUtils.drawPoly(0, 0, currentPoly);
			
			if(currentPoly.npoints > 0) {
				DrawUtils.setColor(new Vector3f(0f, 0.2f, 1f));
				DrawUtils.applyCameraScale();
				DrawUtils.drawLine(new Line2D.Double(currentPoly.xpoints[currentPoly.npoints - 1],
						currentPoly.ypoints[currentPoly.npoints - 1],
						MouseInput.getScreenMouseX(),
						MouseInput.getScreenMouseY()));
				if(Keybinds.CONTROL.held()) {
					DrawUtils.setColor(new Vector3f(0f, 0.8f, 0.4f));
					if(Point.distance(MouseInput.getMouseX() + Camera.get().frame.getX(), 0, currentPoly.xpoints[currentPoly.npoints - 1], 0) < 
							Point.distance(0, Camera.get().frame.getY() - (Mouse.getY() - Camera.get().frame.getHeight()),
									0, currentPoly.ypoints[currentPoly.npoints - 1])) {
						DrawUtils.applyCameraScale();
						DrawUtils.drawLine(new Line2D.Double(currentPoly.xpoints[currentPoly.npoints - 1],
								currentPoly.ypoints[currentPoly.npoints - 1],
								currentPoly.xpoints[currentPoly.npoints - 1],
								Camera.get().frame.getY() - (Mouse.getY() - Camera.get().frame.getHeight())));
					} else {
						DrawUtils.applyCameraScale();
						DrawUtils.drawLine(new Line2D.Double(currentPoly.xpoints[currentPoly.npoints - 1],
								currentPoly.ypoints[currentPoly.npoints - 1], MouseInput.getMouseX() + Camera.get().frame.getX(),
								currentPoly.ypoints[currentPoly.npoints - 1]));
					}
					
				}
			}
		}
		
		if(editPoint != null) {
			DrawUtils.setColor(new Vector3f(1f, 0f, 1f));
			DrawUtils.applyCameraScale();
			DrawUtils.drawRect(polys.get(editPoint.x).xpoints[editPoint.y] - 10, polys.get(editPoint.x).ypoints[editPoint.y] - 10,
					new Rectangle2D.Double(0, 0, 20, 20));
		}
		
		stuff.draw();	
	}
	
	public void addPolyPoint() {
		Point polyPoint = new Point(MouseInput.getScreenMouseX(), MouseInput.getScreenMouseY());
		if(Keybinds.CONTROL.held() && currentPoly.npoints > 0) {
			if(Point.distance(polyPoint.x, 0, currentPoly.xpoints[currentPoly.npoints - 1], 0) < 
					Point.distance(0, polyPoint.y, 0, currentPoly.ypoints[currentPoly.npoints - 1])) {
				polyPoint.setLocation(currentPoly.xpoints[currentPoly.npoints - 1], polyPoint.y);
			} else {
				polyPoint.setLocation(polyPoint.x, currentPoly.ypoints[currentPoly.npoints - 1]);
			}
		}
		currentPoly.addPoint(polyPoint.x, polyPoint.y);
	}
	
	private void selectPolyPoint() {
		Rectangle2D polyPoint = new Rectangle2D.Double();
		for(int j = 0; j<polys.size(); j++) {
			for(int i = 0; i<polys.get(j).npoints; i++) {
				polyPoint.setFrame(polys.get(j).xpoints[i] - 10, polys.get(j).ypoints[i] - 10, 20, 20);
				if(polyPoint.contains(MouseInput.getScreenMouseX(), MouseInput.getScreenMouseY())) {
					editPoint = new Point(j, i);
					return;
				}
			}
		}
		
		editPoint = null;
	}
	
	private void movePoint() {
		polys.get(editPoint.x).xpoints[editPoint.y] = MouseInput.getScreenMouseX();
		polys.get(editPoint.x).ypoints[editPoint.y] = MouseInput.getScreenMouseY();
	}
	
	private void deletePoint() {
		Polygon editPoly = new Polygon();
		for(int i = 0; i<polys.get(editPoint.x).npoints; i++) {
			if(i != editPoint.y)
				editPoly.addPoint(polys.get(editPoint.x).xpoints[i], polys.get(editPoint.x).ypoints[i]);
		}
		
		if(editPoly.npoints == 1)
			polys.remove(editPoint.x);
		else
			polys.set(editPoint.x, editPoly);
		editPoint = null;
	}
	
	private void splitPoint() {
		Polygon editPoly = new Polygon();
		for(int i = 0; i<polys.get(editPoint.x).npoints; i++) {
			editPoly.addPoint(polys.get(editPoint.x).xpoints[i], polys.get(editPoint.x).ypoints[i]);
			if(i == editPoint.y) {
				editPoly.addPoint(polys.get(editPoint.x).xpoints[i] + 20, polys.get(editPoint.x).ypoints[i] + 20);
			}
		}
		
		polys.set(editPoint.x, editPoly);
		editPoint = null;
	}
	
}
