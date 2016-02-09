package core.ui;

import core.ui.event.KeybindEvent;
import core.ui.event.KeybindListener;
import core.ui.event.TimeEvent;
import core.ui.event.TimeListener;
import core.ui.event.UIEvent;
import core.ui.utils.Align;
import core.utilities.keyboard.Keybind;
import core.utilities.text.UIText;

public class TextBox extends UIElement {

	protected UIText uiText;

	protected float textFill;
	protected float fillSpeed;
	
	private KeybindListener keybindListener;
		
	/**
	 * A simple box to display a block of text.
	 * @param x X position
	 * @param y Y position
	 * @param image The text box background, or null for no background
	 * @param text The text to be written, including any text modifiers
	 * @param fill Whether or not the text should appear letter by letter
	 */
	public TextBox(float x, float y, String frame, String text, boolean fill) {
		setUIText(text);
		
		if(fill) {
			setFillSpeed(12.5f);
		} else {
			setTextFill(-1);
		}
		
		setBounds(x, y, uiText.getWidth(), uiText.getHeight());
		setFrame(frame);
		
		addKeybindListener(new DefaultKeybindAdapter());
	}
	
	@Override
	public void draw() {
		if(frame != null) {
			if(textFill < uiText.getLength()) {
				bounds.setFrame(bounds.getX(), bounds.getY(), uiText.getWidth((int) textFill + 1), uiText.getHeight((int) textFill + 1));
			}
			frame.draw(this);
		}

		uiText.draw((float) bounds.getX(), (float) bounds.getY(), (int) textFill);
	}
	
	@Override
	public void setAlign(Align border) {
		if(!uiText.getLines().isEmpty()) {
			super.setAlign(border);
		}
	}
	
	@Override
	public void setStill(boolean still) {
		super.setStill(still);
		
		if(still) {
			uiText.changeModifier("t+", true);
		} else {
			uiText.changeModifier("t\\+", false);
		}
	}
	
	public UIText getUIText() {
		return uiText;
	}
	
	public void setUIText(String text) {
		this.uiText = new UIText(text);
	}
	
	public float getTextFill() {
		return textFill;
	}
	
	public void setTextFill(float textFill) {
		if(textFill == -1) {
			this.textFill = uiText.getLength();
		} else {
			this.textFill = textFill;
		}
	}

	public void setFillSpeed(float fillSpeed) {
		this.fillSpeed = fillSpeed;
		
		addTimeListener(new DefaultTimeAdapter());
	}
	
	public void setKillTimer(float countdown) {
		addTimeListener(new KillTimeAdapter(countdown));
	}

	public void removeKeybindListener(KeybindListener l) {
		if(l == null) {
			return;
		}
		keybindListener = null;
	}
	
	public void addKeybindListener(KeybindListener l) {
		keybindListener = l;
	}
	
	@Override
	public void fireEvent(UIEvent e) {
		super.fireEvent(e);
		
		if(e instanceof KeybindEvent) {
			processKeybindEvent((KeybindEvent) e);
		}
	}

	protected void processKeybindEvent(KeybindEvent e) {
		if(keybindListener != null) {
			keybindListener.KeybindTouched(e);
		}
	}
	
	class DefaultKeybindAdapter implements KeybindListener {
		@Override
		public void KeybindTouched(KeybindEvent e) {
			if(e.getKeybind().equals(Keybind.CONFIRM) && e.getKeybind().clicked()) {
				if(textFill < uiText.getLength()) {
					textFill = uiText.getLength();
					bounds.setFrame(bounds.getX(), bounds.getY(), uiText.getWidth((int) textFill + 1), uiText.getHeight((int) textFill + 1));
					
					removeTimeListener(timeListener);
				}
			}
		}
	}
	
	class DefaultTimeAdapter implements TimeListener {
		@Override
		public void timeStep(TimeEvent e) {
			textFill += e.getDelta() * fillSpeed;
			
			if(textFill >= uiText.getLength()) {
				TextBox.this.removeTimeListener(this);
			}
		}
	}
	
	class KillTimeAdapter implements TimeListener {
		private float killTimer;
		
		KillTimeAdapter(float countdown) {
			killTimer = countdown;
		}

		@Override
		public void timeStep(TimeEvent e) {
			killTimer -= e.getDelta();

			if(killTimer <= 0) {
				setState(UIElement.KILL_FLAG);
			}
		}
	}

}
