package core.utilities.text;

import java.util.HashMap;

public class Text {

	private static HashMap<String, GameFont> fonts = new HashMap<String, GameFont>();
	
	/**
	 * Loads a font to be used. If no other fonts exist, will load given font under "DEFAULT" tag.
	 * @param key to identify given font. eg. "DEFAULT", "SYSTEM", "DIALOGUE"
	 * @param ref the name of the font to load
	 */
	public static void loadFont(String key, String ref) {
		if(fonts.get(ref) == null) {
			if(fonts.isEmpty())
				fonts.put("DEFAULT", new GameFont(ref));
			
			if(!key.matches("DEFAULT")) {
				fonts.put(key, new GameFont(ref));
			}
		}
	}
	
	/**
	 * Get a specific GameFont defined by font
	 * @param font key name to search for
	 * @return GameFont "font"
	 */
	public static GameFont getFont(String font) {
		if(fonts.containsKey(font))
			return fonts.get(font);
		
		return fonts.get("DEFAULT");
	}
	
	/**
	 * Get the Default game font
	 * @return GameFont "DEFAULT"
	 */
	public static GameFont getDefault() {
		return fonts.get("DEFAULT");
	}
	
}
