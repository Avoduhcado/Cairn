package core.interactions;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import core.Camera;
import core.Theater;
import core.ui.screen.ScreenSelection;
import core.ui.screen.ScreenText;

public class Interpreter {

	public static InteractionListener parseInteraction(JsonObject element) {
		if(element.has("showText")) {
			return parseShowText(element);
		} else if(element.has("choose")) {
			return parseShowChoice(element.getAsJsonArray());
		}
		
		return null;
	}
	
	private static InteractionListener parseShowText(final JsonObject element) {
		return new InteractionAdapter() {
			public void keyPress(Script script) {
				Theater.get().getSetup().addUI(new ScreenText(element.get("showText").getAsString(), 
						Camera.get().getDisplayWidth(0.5f), Camera.get().getDisplayHeight(0.8f)));
				
				script.prepareNextElement();
			}
		};
	}
	
	private static InteractionListener parseShowChoice(final JsonArray element) {
		return new InteractionAdapter() {
			public void keyPress(Script script) {
				String options = element.get(0).getAsJsonObject().get("option").getAsString();
				for(int o = 1; o<element.size(); o++) {
					options += ";" + element.get(o).getAsJsonObject().get("option").getAsString();
				}
				Theater.get().getSetup().addUI(new ScreenSelection(options,
						Camera.get().getDisplayWidth(0.5f), Camera.get().getDisplayHeight(0.8f)));
				
				script.prepareNextElement();
			}
		};
	}
	
}
