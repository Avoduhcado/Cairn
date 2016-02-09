package core.ui;

import core.ui.event.ActionEvent;
import core.ui.event.ActionListener;
import core.ui.event.MouseEvent;
import core.ui.event.MouseListener;
import core.ui.event.UIEvent;
import core.ui.utils.Align;
import core.utilities.text.Text;
import core.utilities.text.UIText;

public class Button extends UIElement {
	
	protected UIText text;
	protected String textColor;
	
	private ActionListener actionListener;
			
	public Button(String text) {
		setText(text);
		setBounds(0, 0,
				text != null ? Text.getDefault().getWidth(text) : 1,
				text != null ? Text.getDefault().getHeight(text) : 1);
		
		addMouseListener(new DefaultButtonAdapter());
	}
	
	public Button(float x, float y, String frame, String text) {		
		setText(text);
		setBounds(x, y,
				Text.getDefault().getWidth(text), 
				Text.getDefault().getHeight(text));
		setFrame(frame);
		
		addMouseListener(new DefaultButtonAdapter());
	}
	
	@Override
	public void draw() {
		super.draw();

		if(text != null) {
			text.draw(getX(), getY());
		}
	}

	@Override
	public void setAlign(Align border) {
		switch(border) {
		case RIGHT:
			setBounds((float) bounds.getMaxX(), (float) bounds.getY(), (float) bounds.getWidth(), (float) bounds.getHeight());
			break;
		case LEFT:
			setBounds((float) (bounds.getX() - bounds.getWidth()), (float) bounds.getY(), (float) bounds.getWidth(), (float) bounds.getHeight());
			break;
		case CENTER:
			bounds.setFrameFromCenter(bounds.getX(), bounds.getCenterY(), 
					bounds.getX() - (bounds.getWidth() / 2f), bounds.getY());
			break;
		default:
			break;
		}
	}
	
	@Override
	public void setSelected(boolean selected) {
		if(selected) {
			setTextColor("white");
		} else {
			setTextColor("gray");
		}
	}
	
	@Override
	public void setStill(boolean still) {
		super.setStill(still);
		
		if(still) {
			text.changeModifier("t+", true);
		} else {
			text.changeModifier("t\\+", false);
		}
	}
	
	public UIText getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = new UIText(text);
		setTextColor("gray");
	}
	
	public String getTextColor() {
		return textColor;
	}

	public void setTextColor(String textColor) {
		// TODO Sloppyyyy
		getText().changeModifier("c" + this.textColor, false);
		this.textColor = textColor;
		getText().changeModifier("c" + this.textColor, true);
	}
	
	/**
	 * Not recommended to override default <code>Button</code> behavior.
	 */
	@Override
	public void addMouseListener(MouseListener l) {
		this.mouseListener = l;
	}
	
	public void removeActionListener(ActionListener l) {
		if(l == null) {
			return;
		}
		
		actionListener = null;
	}
	
	public void addActionListener(ActionListener l) {
		this.actionListener = l;
	}
	
	@Override
	public void fireEvent(UIEvent e) {
		super.fireEvent(e);
		
		if(e instanceof ActionEvent) {
			processActionEvent((ActionEvent) e);
		}
	}
	
	protected void processActionEvent(ActionEvent e) {
		if(actionListener != null) {
			actionListener.actionPerformed(e);
		}
	}

	/**
	 * Handle the default actions for mouse events on a button
	 */
	class DefaultButtonAdapter implements MouseListener {

		public void mouseClicked(MouseEvent e) {
			Button.this.fireEvent(new ActionEvent());
		}

		@Override
		public void mousePressed(MouseEvent e) {
		}
		
		@Override
		public void mouseReleased(MouseEvent e) {
		}
		
		public void mouseEntered(MouseEvent e) {
			setTextColor("white");
		}
		
		public void mouseExited(MouseEvent e) {
			setTextColor("gray");
		}
	}
}

