package core.ui.overlays;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import core.Camera;
import core.Input;
import core.render.DrawUtils;
import core.scene.Map;
import core.scene.collisions.PathPolygon;
import core.ui.Button;
import core.ui.CheckBox;
import core.ui.EmptyFrame;
import core.utilities.keyboard.Keybinds;
import core.utilities.mouse.MouseInput;

public class EditMenu extends MenuOverlay {
	
	/** Map you're currently editing */
	private Map map;
	
	private static ArrayList<Polygon> polys = new ArrayList<Polygon>();
	private ArrayList<PathPolygon> paths = new ArrayList<PathPolygon>();
	private Polygon currentPoly;
	private Point editPoint;
	
	private EmptyFrame emptyFrame;
	
	private CheckBox buildPoly;
	private Button addPoly;
	private CheckBox editPolyPoints;
	private Button splitPolyPoint;
	
	private Button saveMap;
	
	public EditMenu(Map map) {
		super(0, 0, null);
		this.map = map;
		EditMenu.polys = map.getCollisionPolys();
		
		buildPoly = new CheckBox(20, 20, null, "Build Poly");
		buildPoly.setStill(true);
		addPoly = new Button("Save Poly", 20, (float) buildPoly.getBox().getMaxY(), 0, null);
		addPoly.setStill(true);
		addPoly.setEnabled(false);
		
		editPolyPoints = new CheckBox(20, (float) addPoly.getBox().getMaxY(), null, "Edit Points");
		editPolyPoints.setStill(true);
		editPolyPoints.setEnabled(!polys.isEmpty());
		splitPolyPoint = new Button("Split Point", 20, (float) editPolyPoints.getBox().getMaxY(), 0, null);
		splitPolyPoint.setStill(true);
		splitPolyPoint.setEnabled(false);
		
		saveMap = new Button("Save Map", 20, (float) splitPolyPoint.getBox().getMaxY() + 10, 0, null);
		saveMap.setStill(true);
		
		emptyFrame = new EmptyFrame(0, 0, "Menu2");
		emptyFrame.setStill(true);
		emptyFrame.setBox(0, 0, (float) buildPoly.getBox().getMaxX(),
				(float) saveMap.getBox().getMaxY(), false);
	}
	
	@Override
	public void update() {
		// Build a new hitmap polygon
		if(buildPoly.isChecked()) {
			if(currentPoly == null) {
				currentPoly = new Polygon();
			} else {
				if(Mouse.isInsideWindow() && Input.mouseClicked() && !emptyFrame.isClicked()) {
					addPolyPoint();
				}
			}
			
			// End editing polygon by right clicking
			if(Mouse.isButtonDown(1)) {
				buildPoly.setChecked(false);
				if(currentPoly.npoints > 1)
					polys.add(currentPoly);
				currentPoly = null;
				addPoly.setEnabled(false);
			}
		}
		buildPoly.update();
		
		// Only enable adding the polygon if a polygon exists
		if(currentPoly != null && currentPoly.npoints > 1) {
			addPoly.setEnabled(true);
		}
		if(addPoly.isEnabled()) {
			addPoly.update();
			if(addPoly.isClicked()) {
				buildPoly.setChecked(false);
				if(currentPoly != null && currentPoly.npoints > 1)
					polys.add(currentPoly);
				currentPoly = null;
				addPoly.setEnabled(false);
			}
		}
		
		// Only enable editing points if polygons exist
		if(editPolyPoints.isEnabled()) {
			if(editPolyPoints.isChecked()) {
				if(Mouse.isInsideWindow() && Input.mouseClicked() && !emptyFrame.isClicked()) {
					selectPolyPoint();
					if(editPoint == null) {
						splitPolyPoint.setEnabled(false);
					}
				}
			}
			editPolyPoints.update();
		} else {
			editPolyPoints.setEnabled(!polys.isEmpty());
		}
		// Enable splitting points
		if(splitPolyPoint.isEnabled()) {
			splitPolyPoint.update();
			if(splitPolyPoint.isClicked()) {
				splitPoint();
				splitPolyPoint.setEnabled(false);
			}
		} else {
			splitPolyPoint.setEnabled(editPolyPoints.isChecked() && editPoint != null);
		}
		
		if(editPoint != null) {
			if(Mouse.isInsideWindow() && Input.mouseHeld() && !emptyFrame.isClicked()) {
				movePoint();
			}
			if(Keybinds.CANCEL.clicked()) {
				deletePoint();
				editPolyPoints.setChecked(!polys.isEmpty());
				editPolyPoints.setEnabled(!polys.isEmpty());
				splitPolyPoint.setEnabled(false);
			}
		}
		
		saveMap.update();
		if(saveMap.isClicked()) {
			if(!polys.isEmpty()) {
				/*KongAlgo kong = new KongAlgo(polys.get(0));
				kong.runKong(false, 0);
				
				//polys.clear();
				//polys.addAll(kong.getTrianglesAsPolygons());
				paths = kong.buildPathPolygons();
				map.setPathPolys(paths);
				Pathfinder.init(map.getPathPolys());*/
			}
			
			map.setCollisionPolys(polys);
			map.buildCollisions();
			map.serialize();
		}
	}
	
	@Override
	public void draw() {
		for(Polygon p : polys) {
			
			DrawUtils.setColor(new Vector3f(0.8f, 0f, 0.4f));
			DrawUtils.applyCameraScale();
			DrawUtils.drawPoly(0, 0, p);
			
			/*for(int i = 0; i<p.npoints - 1; i++) {
				Vector2f vec = new Vector2f(p.xpoints[i + 1] - p.xpoints[i], p.ypoints[i + 1] - p.ypoints[i]);
				float magnitude = (float) Math.sqrt(Math.pow(vec.x, 2) + Math.pow(vec.y, 2));
				vec.set(vec.x / magnitude, vec.y / magnitude);
				vec.scale(10f);
				Line2D line = new Line2D.Double(p.xpoints[i] + ((p.xpoints[i + 1] - p.xpoints[i]) / 2),
						p.ypoints[i] + ((p.ypoints[i + 1] - p.ypoints[i]) / 2),
						p.xpoints[i] + ((p.xpoints[i + 1] - p.xpoints[i]) / 2) + vec.y,
						p.ypoints[i] + ((p.ypoints[i + 1] - p.ypoints[i]) / 2) + (vec.x * -1f));
				DrawUtils.setColor(new Vector3f(0f, 0f, 1f));
				DrawUtils.drawLine(line);
			}*/
			
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
						//Mouse.getX() + Camera.get().frame.getX(),
						MouseInput.getScreenMouseX(),
						MouseInput.getScreenMouseY()));
						//Camera.get().frame.getY() - (Mouse.getY() - Camera.get().frame.getHeight())));
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
		
		emptyFrame.draw();
		
		buildPoly.draw();
		addPoly.draw();
		editPolyPoints.draw();
		splitPolyPoint.draw();
		
		saveMap.draw();
	}

	@Override
	public boolean isCloseRequest() {
		return Keybinds.EDIT.clicked();
	}
	
	public void addPolyPoint() {
		/*Point polyPoint = new Point((int) (Mouse.getX() + Camera.get().frame.getX()),
				(int) (Camera.get().frame.getY() - (Mouse.getY() - Camera.get().frame.getHeight())));*/
		
		Point polyPoint = new Point(MouseInput.getScreenMouseX(),
				(int) (Camera.get().frame.getY() - (Mouse.getY() - Camera.get().frame.getHeight())));
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
	
	public void selectPolyPoint() {
		Rectangle2D polyPoint = new Rectangle2D.Double();
		for(int j = 0; j<polys.size(); j++) {
			for(int i = 0; i<polys.get(j).npoints; i++) {
				polyPoint.setFrame(polys.get(j).xpoints[i] - 10, polys.get(j).ypoints[i] - 10, 20, 20);
				if(polyPoint.contains((Mouse.getX() + Camera.get().frame.getX()),
						(Camera.get().frame.getY() - (Mouse.getY() - Camera.get().frame.getHeight())))) {
					editPoint = new Point(j, i);
					return;
				}
			}
		}
		
		editPoint = null;
	}
	
	public void movePoint() {
		polys.get(editPoint.x).xpoints[editPoint.y] = (int) (Mouse.getX() + Camera.get().frame.getX());
		polys.get(editPoint.x).ypoints[editPoint.y] = (int) (Camera.get().frame.getY() 
				- (Mouse.getY() - Camera.get().frame.getHeight()));
	}
	
	public void deletePoint() {
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
	
	public void splitPoint() {
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
