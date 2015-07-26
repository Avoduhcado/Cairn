package core.interactions;

import java.io.Serializable;
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

public class Script implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Interactable host;
	private String data;
	
	private transient JsonObject root;
	private transient Queue<JsonElement> elements = new LinkedList<JsonElement>();
	
	private transient InteractionListener listener;
	
	public Script(Interactable host, String data) {
		this.host = host;
		this.data = data;
		
		JsonParser parser = new JsonParser();
		root = (JsonObject) parser.parse(data);
		
		fillQueue();	
	}
	
	public void fillQueue() {
		elements.clear();
		for(JsonElement e : root.get("event").getAsJsonArray()) {
			elements.add(e);
		}
		
		prepareNextElement();
	}
	
	public void read() {		
		if(listener != null) {
			if(Keybinds.CONFIRM.clicked()) {
				listener.keyPress(this);
			}
			
			if(((Stage) Theater.get().getSetup()).getPlayer().getBox().intersects(((Entity) host).getBox())) {
				listener.playerCollide(this);
			}
			
			for(Entity e : ((Stage) Theater.get().getSetup()).getMap().getScenery()) {
				if(e.getBox().intersects(((Entity) host).getBox()) && e != host) {
					listener.entityCollide(this, e);
				}
			}
			
			listener.autorun(this);
		} else if(!elements.isEmpty()) {
			prepareNextElement();
		} else {
			fillQueue();
		}
	}
	
	public void prepareNextElement() {
		if(elements.isEmpty()) {
			fillQueue();
		} else {
			listener = Interpreter.parseInteraction((JsonObject) elements.poll());
		}
	}
	
}
