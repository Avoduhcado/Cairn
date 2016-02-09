package core.ui;

import core.utilities.text.Text;
import core.utilities.text.UIText;

public class Label extends UIElement {

	private UIText text;
	
	public Label(float x, float y, String frame, String text) {
		setText(text);
		setBounds(x, y, Text.getDefault().getWidth(text), Text.getDefault().getHeight(text));
		setFrame(frame);
	}
	
	@Override
	public void draw() {
		super.draw();
		
		text.draw(getX(), getY());
	}
	
	public String getPlainText() {
		return text.getLines().get(0).getLineText();
	}
	
	public UIText getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = new UIText(text);
	}
	
}
