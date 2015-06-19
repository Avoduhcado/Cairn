package core.ui.overlays;

import java.awt.Polygon;
import java.util.ArrayList;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import core.Camera;
import core.Input;
import core.entities.Entity;
import core.entities.Prop;
import core.scene.Map;
import core.swing.EntityList;
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
	
	private EntityList entityList;
	
	private Button saveMap;
	
	public EditMenu(Map map) {
		this.map = map;
		EditMenu.polys = map.getCollisionPolys();

		collisions = new Collisions();
		entities = new Entities(map);
		
		entityList = new EntityList(map);

		saveMap = new Button("Save Map", 20, Camera.get().getDisplayHeight(0.5f), 0, "Menu2");
		saveMap.setStill(true);
	}
	
	@Override
	public void update() {
		collisions.update();
		
		if(entityList.getPropTree().getSelectionPath() != null && Input.mouseHeld()) {
			for(TreePath t : entityList.getPropTree().getSelectionPaths()) {
				if(((DefaultMutableTreeNode) t.getLastPathComponent()).getUserObject() instanceof Prop) {
					Prop prop = (Prop) ((DefaultMutableTreeNode) t.getLastPathComponent()).getUserObject();
					map.getProps().get(map.getProps().indexOf(prop)).movePosition(Input.mouseDelta.x / Camera.get().getScale(),
							Input.mouseDelta.y / Camera.get().getScale());
				}
			}
		}
		
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

	public void close() {
		for(Entity e : map.getScenery()) {
			e.setDebug(false);
		}
		entityList.dispose();
	}
	
	@Override
	public boolean isCloseRequest() {
		return Keybinds.EDIT.clicked();
	}
	
}
