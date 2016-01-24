package core.utilities.text;

import java.awt.Color;
import java.lang.reflect.Field;

import core.utilities.keyboard.Keybind;

public class TextModifier {

	/** TODO Add delimiter for accessing variables $variable somehow?
	 */
	
	public Color color = Color.white;
	public Color dropColor = Color.black;
	public boolean dropShadow = true;
	public float size = 0;
	public boolean still = false;
	public String addIn = "";
	public String fontFace = "DEBUG";
	
	public TextModifier(String modifier) {
		if(modifier != null) {
			String[] temp = modifier.split(",");
			for(int x = 0; x<temp.length; x++) {
				switch(temp[x].charAt(0)) {
				case 's':
					size = Float.parseFloat(temp[x].substring(1));
					break;
				case 'd':
					dropShadow = temp[x].charAt(1) == '-' ? false : true;
					break;
				case 'r':
					try {
						if(temp[x].contains("#")) {
							dropColor = Color.decode(temp[x].substring(1));
						} else {
							Field f = Color.class.getField(temp[x].substring(1));
							dropColor = (Color) f.get(null);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				case 't':
					still = temp[x].charAt(1) == '+';
					break;
				case 'c':
					try {
						if(temp[x].contains("#")) {
							color = Color.decode(temp[x].substring(1));
						} else {
							Field f = Color.class.getField(temp[x].substring(1));
							color = (Color) f.get(null);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				case 'f':
					fontFace = temp[x].substring(1);
					break;
				case '$':
					String type = temp[x].substring(1).split(":")[0];
					switch(type) {
					case "key":
						addIn = Keybind.valueOf(temp[x].substring(1).split(":")[1]).getKey();
						break;
					}
					break;
				}
			}
		}
	}
	
	public void concat(String modifier) {
		String[] temp = modifier.split(",");
		for(int x = 0; x<temp.length; x++) {
			switch(temp[x].charAt(0)) {
			case 's':
				size = Float.parseFloat(temp[x].substring(1));
				break;
			case 'd':
				dropShadow = temp[x].charAt(1) == '-' ? false : true;
				break;
			case 'r':
				try {
					if(temp[x].contains("#")) {
						dropColor = Color.decode(temp[x].substring(1));
					} else {
						Field f = Color.class.getField(temp[x].substring(1));
						dropColor = (Color) f.get(null);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case 't':
				still = temp[x].charAt(1) == '+';
				break;
			case 'c':
				try {
					if(temp[x].contains("#")) {
						color = Color.decode(temp[x].substring(1));
					} else {
						Field f = Color.class.getField(temp[x].substring(1));
						color = (Color) f.get(null);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case 'f':
				fontFace = temp[x].substring(1);
				break;
			case '$':
				String type = temp[x].substring(1).split(":")[0];
				switch(type) {
				case "key":
					addIn = Keybind.valueOf(temp[x].substring(1).split(":")[1]).getKey();
					break;
				}
				break;
			}
		}
	}
}
