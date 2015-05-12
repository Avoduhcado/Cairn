package core.ui;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import core.Theater;
import core.utilities.keyboard.Keybinds;
import core.utilities.text.Text;
import core.utilities.text.TextModifier;

public class TextBox extends UIElement {

	protected ArrayList<TextLine> lines = new ArrayList<TextLine>();

	protected float textFill;
	private float fillSpeed = 0.5f;
	
	/**
	 * A simple box to display a block of text.
	 * @param text The text to be written, including any text modifiers
	 * @param x X position
	 * @param y Y position
	 * @param image The textbox background, or null for no background
	 */
	public TextBox(String text, float x, float y, String image, boolean fill) {
		super(x, y, image);
		
		parseText(text);
		
		if(fill) {
			textFill = 0f;
		} else {
			textFill = getLength();
		}
		
		this.box = new Rectangle2D.Double(x, y, getWidth(), getHeight());
	}

	@Override
	public void update() {
		if(textFill < getLength()) {
			fill();
		}
		
		if(Keybinds.CONFIRM.clicked() && event != null) {
			if(textFill < getLength()) {
				textFill = getLength();
				box.setFrame(x, y, getWidth((int) textFill + 1), getHeight((int) textFill + 1));
			} else {
				event.processed();
			}
		}
		
		if(killTimer > 0) {
			killTimer -= Theater.getDeltaSpeed(0.025f);
			if(killTimer <= 0) {
				// TODO Extend to all UI classes, maybe add a fancy fade effect
				setKill(true);
			}
		}
	}
	
	@Override
	public void draw() {
		if(frame != null) {
			if(textFill < getLength()) {
				box.setFrame(x, y, getWidth((int) textFill + 1), getHeight((int) textFill + 1));
			}
			frame.draw(x, y, box);
		}

		if(!lines.isEmpty()) {
			// Draw first line
			lines.get(0).draw(x, y, (int) textFill);
			// Text limit for subsequent lines
			int limit = (int) textFill - lines.get(0).getLength();
			if(limit > 0) {
				// Text y offset for subsequent lines
				float yOffset = lines.get(0).getHeight();
				for(int i = 1; i < lines.size(); i++) {
					// Draw line
					lines.get(i).draw(x, y + yOffset, limit);
					// Increment y offset
					yOffset += lines.get(i).getHeight();
					// Decrement limit
					limit -= lines.get(i).getLength();
					// End draw if you've run out of available text
					if(limit <= 0)
						break;
				}
			}
		}
	}
	
	@Override
	public void draw(float x, float y) {
		if(frame != null) {
			if(textFill < getLength()) {
				box.setFrame(x, y, getWidth((int) textFill + 1), getHeight((int) textFill + 1));
			}
			frame.draw(x, y, box);
		}

		if(!lines.isEmpty()) {
			// Draw first line
			lines.get(0).draw(x, y, (int) textFill);
			// Text limit for subsequent lines
			int limit = (int) textFill - lines.get(0).getLength();
			if(limit > 0) {
				// Text y offset for subsequent lines
				float yOffset = lines.get(0).getHeight();
				for(int i = 1; i < lines.size(); i++) {
					// Draw line
					lines.get(i).draw(x, y + yOffset, limit);
					// Increment y offset
					yOffset += lines.get(i).getHeight();
					// Decrement limit
					limit -= lines.get(i).getLength();
					// End draw if you've run out of available text
					if(limit <= 0)
						break;
				}
			}
		}
	}

	@Override
	public void updateBox() {
		box.setFrame(x, y, getWidth(), getHeight());
	}
	
	private void parseText(String text) {
		if(text.contains(";")) {
			String[] lineArray = text.split(";");
			for(int i = 0; i<lineArray.length; i++) {
				if(i > 0 && !lineArray[i].startsWith("<") && lineArray[i - 1].contains("<")) {
					lineArray[i] = lineArray[i - 1].substring(lineArray[i - 1].lastIndexOf('<'),
							lineArray[i - 1].lastIndexOf('>') + 1) + lineArray[i];
				}
				lines.add(new TextLine(lineArray[i]));
			}
		} else {
			lines.add(new TextLine(text));
		}
	}

	public void center() {
		if(!lines.isEmpty()) {
			this.x = x - (getWidth() / 2f);
		}
	}
	
	public void fill() {
		textFill += Theater.getDeltaSpeed(fillSpeed);
	}
	
	public float getTextFill() {
		return textFill;
	}
	
	public void setTextFill(float textFill) {
		if(textFill == -1)
			this.textFill = getLength();
		else
			this.textFill = textFill;
	}

	public void setFillSpeed(float fillSpeed) {
		this.fillSpeed = fillSpeed;
	}

	public void setText(String text) {
		parseText(text);
		
		updateBox();
	}
	
	// TODO Provide support to add text to the same line, or more than one line
	public void addText(String text) {
		if(text.startsWith(";")) {
			lines.add(new TextLine(text));
		}
		
		updateBox();
	}
	
	public float getWidth() {
		float width = 0;
		for(TextLine l : lines) {
			if(l.getWidth() > width) {
				width = l.getWidth();
			}
		}
		
		return width;
	}
	
	public float getHeight() {
		float height = 0;
		for(TextLine l : lines) {
			height += l.getHeight();
		}
		
		return height;
	}

	public float getWidth(int limit) {
		float width = 0;
		for(TextLine l : lines) {
			if(l.getWidth(limit) > width) {
				width = l.getWidth(limit);
			}
			limit -= l.getLength();
			if(limit <= 0)
				break;
		}
		
		return width;
	}
	
	public float getHeight(int limit) {
		float height = 0;
		for(TextLine l : lines) {
			height += l.getHeight(limit);
			limit -= l.getLength();
			if(limit <= 0)
				break;
		}
		
		return height;
	}
	
	public int getLength() {
		int length = 0;
		for(TextLine l : lines) {
			length += l.getLength();
		}
		
		return length;
	}
	
	public class TextLine {
		
		private ArrayList<TextSegment> segments = new ArrayList<TextSegment>();
		
		public TextLine(String text) {
			if(text.contains("<")) {
				for(String segment : text.split("<")) {
					if(!segment.isEmpty()) {
						segments.add(new TextSegment(segment.split(">")[0], segment.split(">")[1]));
					}
				}
			} else {
				segments.add(new TextSegment(null, text));
			}
		}
		
		public void draw(float x, float y, int limit) {
			float xOffset = 0;
			for(int i = 0; i<segments.size(); i++) {
				segments.get(i).draw(x + xOffset, y, limit);
				xOffset += segments.get(i).getWidth();
				limit -= segments.get(i).getLength();
				if(limit <= 0)
					break;
			}
		}
		
		// TODO Per segment adjustment?
		public void append(String modifier) {
			for(TextSegment s : segments) {
				s.modifier.concat(modifier);
			}
		}
		
		public float getWidth() {
			float width = 0;
			for(TextSegment s : segments) {
				width += s.getWidth();
			}
			
			return width;
		}
		
		public float getHeight() {
			float height = 0;
			for(TextSegment s : segments) {
				if(s.getHeight() > height) {
					height = s.getHeight();
				}
			}
			
			return height;
		}
		
		public float getWidth(int limit) {
			float width = 0;
			for(int i = 0; i<segments.size(); i++) {
				width += segments.get(i).getWidth(limit);
				limit -= segments.get(i).getLength();
				if(limit <= 0)
					break;
			}
			
			return width;
		}
		
		public float getHeight(int limit) {
			float height = 0;
			for(int i = 0; i<segments.size(); i++) {
				if(segments.get(i).getHeight(limit) > height)
					height = segments.get(i).getHeight(limit);
				limit -= segments.get(i).getLength();
				if(limit <= 0)
					break;
			}
			
			return height;
		}
		
		public int getLength() {
			int length = 0;
			for(TextSegment s : segments) {
				length += s.getLength();
			}
			
			return length;
		}
		
		private class TextSegment {
			
			private TextModifier modifier;
			private String text;
			
			public TextSegment(String modifier, String text) {
				this.modifier = new TextModifier(modifier);
				this.text = text;
			}
			
			public void draw(float x, float y, int limit) {
				modifier.apply();
				Text.getFont(modifier.getFontFace()).drawStringSegment(modifier.getAddIn() + text, x, y, 0,
						limit > getLength() ? getLength() : limit);
			}
			
			// TODO
			@SuppressWarnings("unused")
			public void addModifier(String modifier) {
				this.modifier.concat(modifier);
			}
			
			public float getWidth() {
				modifier.apply();
				return Text.getFont(modifier.getFontFace()).getWidth(modifier.getAddIn() + text);
			}
			
			public float getHeight() {
				modifier.apply();
				return Text.getFont(modifier.getFontFace()).getHeight(modifier.getAddIn() + text);
			}
			
			public float getWidth(int limit) {
				modifier.apply();
				return Text.getFont(modifier.getFontFace()).getWidth((modifier.getAddIn() + text)
						.substring(0, limit > getLength() ? getLength() : limit));
			}
			
			public float getHeight(int limit) {
				modifier.apply();
				return Text.getFont(modifier.getFontFace()).getHeight((modifier.getAddIn() + text)
						.substring(0, limit > getLength() ? getLength() : limit));
			}
			
			public int getLength() {
				return text.length() + modifier.getAddIn().length();
			}
			
		}
		
	}
	
}
