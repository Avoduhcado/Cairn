package core.render;

import java.util.HashMap;

public class SpriteList {

private static HashMap<String, Sprite> sprites = new HashMap<String, Sprite>();
	
	public static Sprite get(String ref) {
		if(sprites.containsKey(ref)) {
			return sprites.get(ref);
		}
		
		if(ref != null) {
			sprites.put(ref, new Sprite(ref));
		}
		
		return sprites.get(ref);
	}
	
}
