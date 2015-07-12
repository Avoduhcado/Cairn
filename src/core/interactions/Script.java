package core.interactions;

import java.util.LinkedList;
import java.util.Queue;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import core.Theater;
import core.entities.Entity;
import core.entities.interfaces.Interactable;
import core.setups.Stage;
import core.utilities.keyboard.Keybinds;

public class Script {

	private Interactable host;
	
	private JsonObject root;
	private Queue<JsonElement> elements = new LinkedList<JsonElement>();
	
	private InteractionListener listener;
	
	public Script(String data) {
		JsonParser parser = new JsonParser();
		root = (JsonObject) parser.parse(data);
		
		fillQueue();	
	}
	
	public void fillQueue() {
		elements.clear();
		for(JsonElement e : root.get("event").getAsJsonArray()) {
			elements.add(e);
		}
	}
	
	public void read() {
		if(listener != null) {
			if(Keybinds.CONFIRM.clicked()) {
				listener.keyPress();
			}
			
			if(((Stage) Theater.get().getSetup()).getPlayer().getBox().intersects(((Entity) host).getBox())) {
				listener.playerCollide();
			}
			
			for(Entity e : ((Stage) Theater.get().getSetup()).getMap().getScenery()) {
				if(e.getBox().intersects(((Entity) host).getBox()) && e != host) {
					listener.entityCollide(e);
				}
			}
			
			listener.autorun();
		}
	}
	
	private void prepareNextElement() {
		
	}
	
}
