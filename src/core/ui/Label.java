package core.ui;

import core.utilities.text.Text;

public class Label extends UIElement {

	private String text;
	
	public Label(String text, float x, float y, String image) {
		this.text = text;
		setBounds(x, y, Text.getDefault().getWidth(text), Text.getDefault().getHeight(text));
		setFrame(image);
	}
	
	@Override
	public void draw() {
		super.draw();
		
		Text.getDefault().setStill(still);
		Text.getDefault().drawString(text, (float) bounds.getX(),  (float) bounds.getY());
	}
	
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
}
