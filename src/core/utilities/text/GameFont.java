package core.utilities.text;

import java.awt.Color;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import core.render.textured.Glyph;

public class GameFont {

	private HashMap<Character, Glyph> glyphs = new HashMap<Character, Glyph>();
	private String fontName;
	
	public static final float defaultSize = 0.6f;
	public static final Color defaultColor = Color.white;
	
	public GameFont(String fontName) {
		this.fontName = fontName;

		// TODO Resource loading
		try (FileInputStream fis = new FileInputStream(System.getProperty("resources") + "/fonts/" + fontName + ".fnt")) {
			byte[] byteArray = new byte[fis.available()];
			int offset = 0;
			String[] images = null;
			
			offset += fis.read(byteArray, 0, 4);
			if(byteArray[0] == 66 && byteArray[1] == 77 && byteArray[2] == 70 && byteArray[3] == 3) {
				byte[] blockData = new byte[5];
				byte[] block = null;
				while(offset < byteArray.length) {
					offset += fis.read(blockData);
					int length = blockData[4] << 24 | blockData[3] << 16 | blockData[2] << 8 | blockData[1];
					
					switch(blockData[0]) {
					case 1:
						block = new byte[length];
						offset += fis.read(block);
						break;
					case 2:
						block = new byte[length];
						offset += fis.read(block);
						images = new String[block[8]];
						break;
					case 3:
						block = new byte[length];
						offset += fis.read(block);
						int pageIndex = 0;
						images[0] = "";
						
						for(byte bit : block) {
							if(bit == 0) {
								if(pageIndex < images.length - 1) {
									pageIndex++;
									images[pageIndex] = "";
								}
							} else {
								images[pageIndex] += (char) bit;
							}
						}
						break;
					case 4:
						block = new byte[20];
						for(int c = 0; c<length / block.length; c++) {
							offset += fis.read(block, 0, 20);
							
							char id = (char) (block[3] << 24 | block[2] << 16 | block[1] << 8 | block[0]);
							int x = block[5] << 8 | block[4];
							int y = block[7] << 8 | block[6];
							int width = block[9] << 8 | block[8];
							int height = block[11] << 8 | block[10];
							int xOffset = block[13] << 8 | block[12];
							int yOffset = block[15] << 8 | block[14];
							int xAdvance = block[17] << 8 | block[16];
							String page = images[block[18]];

							glyphs.put(id, new Glyph(page, x, y, width, height, xOffset, yOffset, xAdvance));
						}
						break;
					case 5:
						block = new byte[10];
						for(int k = 0; k<length / block.length; k++) {
							offset += fis.read(block);
						}
						break;
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*private void drawText(String text, float x, float y) {
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
	}*/
	
	public void drawString(String text, float x, float y, TextModifier modifier) {
		float advance = 0;
		
		for(int i = 0; i<text.length(); i++) {
			if(modifier.dropShadow) {
				getChar(text.charAt(i)).draw(x + advance + 2, y + 2, modifier, modifier.dropColor);
			}
						
			getChar(text.charAt(i)).draw(x + advance, y, modifier);
			advance += getChar(text.charAt(i)).getXAdvance();
		}
	}
	
	public void drawStringSegment(String text, float x, float y, int start, int end, TextModifier modifier) {
		drawString(text.substring(start, end), x, y, modifier);
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
			getChar(text.charAt(i)).setScale(defaultSize);
			width += getChar(text.charAt(i)).getXAdvance();
		}
		
		return width;
	}
	
	public float getHeight(String text) {
		float height = 0;
		for(int i = 0; i<text.length(); i++) {
			getChar(text.charAt(i)).setScale(defaultSize);
			if(getChar(text.charAt(i)).getLineHeight() > height) {
				height = getChar(text.charAt(i)).getLineHeight();
			}
		}
		
		return height;
	}
	
	public String getName() {
		return fontName;
	}

}
