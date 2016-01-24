package core.ui;

import core.utilities.text.Text;

public class Label extends UIElement {

	private String text;
	
	public Label(float x, float y, String frame, String text) {
		this.text = text;
		setBounds(x, y, Text.getDefault().getWidth(text), Text.getDefault().getHeight(text));
		setFrame(frame);
	}
	
	@Override
	public void draw() {
		super.draw();
		
		Text.drawString(text, getX(), getY());
	}
	
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
}
