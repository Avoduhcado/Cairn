package core.ui.overlays;

import java.awt.Polygon;
import java.util.ArrayList;

import core.Camera;
import core.Input;
import core.Theater;
import core.entities.Entity;
import core.scene.Map;
import core.setups.Stage;
import core.swing.EditorMain;
import core.swing.EntityList;
import core.ui.Button;
import core.ui.overlays.edit.Collisions;
import core.utilities.keyboard.Keybinds;

public class EditMenu extends MenuOverlay {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Map you're currently editing */
	private Map map;
	private Stage stage;
	
	private static ArrayList<Polygon> polys = new ArrayList<Polygon>();
	
	private Collisions collisions;

	private EditorMain editorMain;
	private EntityList entityList;
	
	private Button saveMap;
	
	private boolean closed;
	
	public EditMenu(Stage stage) {
		this.stage = stage;
		this.map = stage.getMap();
		EditMenu.polys = map.getCollisionPolys();

		collisions = new Collisions();

		editorMain = new EditorMain(this, (Stage) Theater.get().getSetup());
		entityList = new EntityList(stage, this);

		saveMap = new Button("Save Map", 20, Camera.get().getDisplayHeight(0.5f), 0, "Menu2");
		saveMap.setStill(true);
	}
	
	@Override
	public void update() {
		collisions.update();
		
		if(entityList.hasSelection() && Input.mouseHeld()) {
			for(Entity e : entityList.getSelection()) {
				map.getEntity(e).movePosition(Input.mouseDelta.x / Camera.get().getScale(),	Input.mouseDelta.y / Camera.get().getScale());
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
		collisions.draw();

		saveMap.draw();
	}

	public void setMap(Map map) {
		this.map = map;
		entityList.setMap(map);
	}
	
	public void close() {
		closeEntityList();
		
		editorMain.dispose();
		
		closed = true;
	}
	
	public void clearEntityList() {
		entityList.dispose();
		entityList = new EntityList(stage, this);
	}
	
	public void openEntityList() {
		if(entityList.isShowing()) {
			return;
		} else {
			entityList.setVisible(true);
		}
	}
	
	public void closeEntityList() {
		for(Entity e : map.getScenery()) {
			e.setDebug(false);
		}
		entityList.dispose();
		
		editorMain.getChckbxmntmEntityList().setSelected(false);
	}
	
	@Override
	public boolean isCloseRequest() {
		return Keybinds.EDIT.clicked() || closed;
	}

}
