package core.utilities.text;

import java.awt.Color;
import java.lang.reflect.Field;

import core.utilities.keyboard.Keybinds;

public class TextModifier {

	/** TODO Add delimiter for accessing variables $variable somehow?
	 */
	
	private Color color = Color.white;
	private Color dropColor = Color.black;
	private boolean dropShadow = true;
	private float size = GameFont.defaultSize;
	private boolean still = false;
	private String addIn = null;
	private String fontFace = "DEBUG";
	
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
					still = temp[x].charAt(1) == '+' ? true : false;
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
						addIn = Keybinds.valueOf(temp[x].substring(1).split(":")[1]).getKey();
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
				still = temp[x].charAt(1) == '+' ? true : false;
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
					addIn = Keybinds.valueOf(temp[x].substring(1).split(":")[1]).getKey();
					break;
				}
				break;
			}
		}
	}
	
	public void apply() {
		Text.getFont(fontFace).setColor(color);
		Text.getFont(fontFace).setDropShadow(dropShadow);
		Text.getFont(fontFace).setDropColor(dropColor);
		Text.getFont(fontFace).setSize(size);
		Text.getFont(fontFace).setStill(still);
	}
	
	public String getAddIn() {
		if(addIn == null)
			return "";
		
		return addIn;
	}

	public String getFontFace() {
		return fontFace;
	}
	
}
