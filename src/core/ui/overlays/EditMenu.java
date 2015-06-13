package core.ui.overlays;

import java.awt.Polygon;
import java.util.ArrayList;

import core.Camera;
import core.scene.Map;
import core.ui.Button;
import core.ui.overlays.edit.Collisions;
import core.ui.overlays.edit.Entities;
import core.utilities.keyboard.Keybinds;

public class EditMenu extends MenuOverlay {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Map you're currently editing */
	private Map map;
	
	private static ArrayList<Polygon> polys = new ArrayList<Polygon>();
	
	private Collisions collisions;
	private Entities entities;
	
	private Button saveMap;
	
	public EditMenu(Map map) {
		this.map = map;
		EditMenu.polys = map.getCollisionPolys();

		collisions = new Collisions();
		entities = new Entities(map);

		saveMap = new Button("Save Map", 20, Camera.get().getDisplayHeight(0.5f), 0, "Menu2");
		saveMap.setStill(true);
	}
	
	@Override
	public void update() {
		collisions.update();
		
		entities.update();
		
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
		collisions.draw();
		
		entities.draw();
		
		saveMap.draw();
	}

	@Override
	public boolean isCloseRequest() {
		return Keybinds.EDIT.clicked();
	}
	
}
