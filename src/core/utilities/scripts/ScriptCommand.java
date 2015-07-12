package core.utilities.scripts;

import com.google.gson.JsonObject;

import core.setups.Stage;

public class ScriptCommand {

	private int command;
	private JsonObject element;
	
	public ScriptCommand(int command, JsonObject element) {
		this.command = command;
		this.element = element;
	}
	
	public void parse(Stage stage, ScriptEvent event) {
		switch(command) {
		case 1:
			Interpreter.showText(element.get("showText").getAsString(), stage, event);
			break;
		case 2:
			Interpreter.choose(element.get("choose").getAsJsonArray(), stage, event);
			break;
		case 3:
			Interpreter.teleport(element.get("teleport").getAsJsonArray(), stage, event);
			break;
		}
	}
	
	public int getCommand() {
		return command;
	}
	
}
