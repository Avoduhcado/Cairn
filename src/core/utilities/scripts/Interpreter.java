package core.utilities.scripts;

import com.google.gson.JsonArray;

import core.Camera;
import core.setups.Stage;
import core.ui.event.ScreenSelection;
import core.ui.event.ScreenText;

public class Interpreter {
	
	public static void showText(String text, Stage stage, ScriptEvent event) {
		ScreenText eventText = new ScreenText(text, Camera.get().getDisplayWidth(0.5f), Camera.get().getDisplayHeight(0.8f));
		eventText.setEvent(event);
		stage.addUI(eventText);
	}
	
	public static void choose(JsonArray choices, Stage stage, ScriptEvent event) {		
		String options = choices.get(0).getAsJsonObject().get("option").getAsString();
		for(int o = 1; o<choices.size(); o++) {
			options += ";" + choices.get(o).getAsJsonObject().get("option").getAsString();
		}
		
		ScreenSelection eventSelection =
				new ScreenSelection(options, Camera.get().getDisplayWidth(0.5f), Camera.get().getDisplayHeight(0.8f));
		eventSelection.setEvent(event);
		stage.addUI(eventSelection);
	}
	
}
