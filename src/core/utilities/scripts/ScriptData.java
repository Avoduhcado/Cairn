package core.utilities.scripts;

import java.util.LinkedList;
import java.util.Queue;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ScriptData {

	private JsonObject root;
	private Queue<JsonElement> elements = new LinkedList<JsonElement>();
	private boolean started;
	
	public ScriptData(String data) {
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
	
	public JsonObject getCurrent() {
		if(elements.peek() != null)
			return elements.peek().getAsJsonObject();
		
		return null;
	}
	
	public JsonObject next() {
		return elements.poll().getAsJsonObject();
	}
	
	public ScriptCommand getCommand() {
		if(getCurrent().has("showText")) {
			return new ScriptCommand(1, next());
		} else if(getCurrent().has("choose")) {
			return new ScriptCommand(2, getCurrent());
		}
		
		// TODO Throw an exception or something
		return null;
	}
	
	public void parseResult(int result) {
		if(getCurrent().has("choose")) {
			addElements(next().get("choose").getAsJsonArray().get(result).getAsJsonObject().get("result").getAsJsonArray());
		}
	}
	
	public void addElements(JsonArray newElements) {
		Queue<JsonElement> tempElements = new LinkedList<JsonElement>();
		for(JsonElement e : newElements) {
			tempElements.offer(e);
		}
		tempElements.addAll(elements);
		elements = tempElements;
	}
	
	public boolean isStarted() {
		return started;
	}
	
	public void setStarted(boolean started) {
		this.started = started;
	}
	
}
