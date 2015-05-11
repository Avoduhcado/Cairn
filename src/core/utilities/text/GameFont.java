package core.utilities.text;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import core.render.textured.Glyph;

public class GameFont {

	private HashMap<Character, Glyph> glyphs = new HashMap<Character, Glyph>();
	private String fontName;
	
	public static final float defaultSize = 0.4f;
	private static final Color defaultColor = Color.white;
	
	private float size;
	private Color color;
	private Color dropColor;
	private boolean dropShadow = true;
	private boolean still = false;
	private boolean centered = false;
	
	public GameFont(String fontName) {
		this.fontName = fontName;
		this.size = defaultSize;
		this.color = defaultColor;
		this.dropColor = Color.black;
		
		try (BufferedReader reader = new BufferedReader(new FileReader(System.getProperty("resources") + "/fonts/" + fontName + ".fnt"))) {
			String line;
			String image = null;
			while((line = reader.readLine()) != null) {
				//System.out.println(line);
				String[] temp = line.split(" +");
				
				switch(temp[0]) {
				case "info":
					break;
				case "common":
					break;
				case "page":
					image = temp[2].split("=")[1].replaceAll("\"", "").substring(0, temp[2].split("=")[1].lastIndexOf('_') - 1);
					break;
				case "char":
					//for(String t : temp)
						//System.out.println(t);
					//System.out.println();
					glyphs.put((char) Integer.parseInt(temp[1].split("=")[1]), 
							new Glyph(image, Integer.parseInt(temp[2].split("=")[1]), Integer.parseInt(temp[3].split("=")[1]),
									Integer.parseInt(temp[4].split("=")[1]), Integer.parseInt(temp[5].split("=")[1]),
									Integer.parseInt(temp[6].split("=")[1]), Integer.parseInt(temp[7].split("=")[1]),
									Integer.parseInt(temp[8].split("=")[1]), Integer.parseInt(temp[9].split("=")[1])));
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void drawText(String text, float x, float y) {
		float advance = 0;
		if(centered) {
			x -= (getWidth(text) / 2f);
		}
		
		for(int i = 0; i<text.length(); i++) {
			// Apply text adjustments
			getChar(text.charAt(i)).setSize(size);
			getChar(text.charAt(i)).setStill(still);
			if(dropShadow) {
				getChar(text.charAt(i)).setColor(dropColor);
				getChar(text.charAt(i)).draw(x + advance + 2, y + 2);
			}
			getChar(text.charAt(i)).setColor(color);
			
			getChar(text.charAt(i)).draw(x + advance, y);
			advance += getChar(text.charAt(i)).getXAdvance();
		}
	}
	
	public void drawString(String text, float x, float y) {
		drawText(text, x, y);
		reset();
	}
	
	public void drawStringSegment(String text, float x, float y, int start, int end) {
		drawText(text.substring(start, end), x, y);
		reset();
	}
	
	private void reset() {
		setSize(defaultSize);
		setColor(defaultColor);
		setDropColor(Color.black);
		setDropShadow(true);
		setStill(false);
		setCentered(false);
	}

	public Glyph getChar(Character c) {
		if(glyphs.containsKey(c)) {
			return glyphs.get(c);
		}
		
		return glyphs.get(' ');
	}
	
	public float getWidth(String text) {
		float width = 0;
		for(int i = 0; i<text.length(); i++) {
			getChar(text.charAt(i)).setSize(size);
			width += getChar(text.charAt(i)).getXAdvance();
		}
		
		return width;
	}
	
	public float getHeight(String text) {
		float height = 0;
		for(int i = 0; i<text.length(); i++) {
			getChar(text.charAt(i)).setSize(size);
			if(getChar(text.charAt(i)).getLineHeight() > height) {
				height = getChar(text.charAt(i)).getLineHeight();
			}
		}
		
		return height;
	}
	
	public String getName() {
		return fontName;
	}
	
	public float getSize() {
		return size;
	}
	
	public void setSize(float size) {
		this.size = size;
	}

	public Color getColor() {
		return color;
	}
	
	public void setColor(Color color) {
		this.color = color;
	}
	
	public Color getDropColor() {
		return dropColor;
	}
	
	public void setDropColor(Color dropColor) {
		this.dropColor = dropColor;
	}
	
	public boolean hasDropShadow() {
		return dropShadow;
	}
	
	public void setDropShadow(boolean dropShadow) {
		this.dropShadow = dropShadow;
	}
	
	public void setStill(boolean still) {
		this.still = still;
	}
	
	public void setCentered(boolean centered) {
		this.centered = centered;
	}

}
