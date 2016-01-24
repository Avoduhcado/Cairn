package core.ui;

import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import org.lwjgl.input.Keyboard;

import core.ui.event.ActionEvent;
import core.ui.event.ActionListener;
import core.ui.event.KeyEvent;
import core.ui.event.KeyListener;
import core.ui.event.MouseEvent;
import core.ui.event.MouseListener;
import core.ui.event.TimeEvent;
import core.ui.event.TimeListener;
import core.ui.event.UIEvent;
import core.ui.event.ValueChangeEvent;
import core.ui.event.ValueChangeListener;
import core.ui.utils.Accessible;
import core.utilities.text.Text;

public class InputBox extends UIElement implements Accessible {

	private static final String CARET = "/";
	
	private int style; // 0 = plain text; 1 = Integers; -1 = Keybinds;
	private String text;
	private int textLimit = 100;
	private String textColor = "white";
	private float flash = 0.0f;
	//private boolean centered = true;
	
	private ActionListener actionListener;
	private KeyListener keyListener;
	private ValueChangeListener valueChangeListener;
	
	/**
	 * @param text Preset text
	 * @param x coordinate of box
	 * @param y coordinate of box
	 * @param style Type of input accepted
	 * @param image Background frame
	 * @param textLimit Total number of characters accepted
	 */
	public InputBox(float x, float y, String frame, String text, int style, int textLimit) {
		this.style = style;
		this.text = text != null ? text : "";
		if(textLimit != 0) {
			this.textLimit = textLimit;
			trimText();
		}
		
		setBounds(x, y, 
				text != null ? Text.getDefault().getWidth(text) : Text.getDefault().getWidth(CARET),
				text != null ? Text.getDefault().getHeight(text) : Text.getDefault().getHeight(CARET));
		setFrame(frame);
		
		addMouseListener(new DefaultInputMouseAdapter());
		addTimeListener(new DefaultInputTimeAdapter());
		addKeyListener(createKeyListener(style));
	}
	
	@Override
	public void draw() {
		super.draw();
		
		Text.drawString(flash > 0.5f ? text + CARET : text, getX(), getY(), "c" + textColor);
	}

	/**
	 * Fit bounds to included text
	 */
	public void resizeBounds() {
		setBounds(bounds.getX(), bounds.getY(), Text.getDefault().getWidth(text + CARET), Text.getDefault().getHeight(text + CARET));
	}
	
	/**
	 * Paste contents of clipboard into text.
	 */
	private void paste() {
		try {
			switch(style) {
			case 1: 
				text += Integer.parseInt((String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor));
				break;
			default: 
				text += (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
				break;
			}
		} catch (NumberFormatException e) {
		} catch (IOException | HeadlessException | UnsupportedFlavorException  e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Add the next character from the keyboard to text.
	 */
	private void addCharacter(char c) {
		switch(style) {
		case 0: 
			text = text + c;
			break;
		case 1:
			try {
				Integer.parseInt(text + c);
				text = text + c;
			} catch (NumberFormatException e) {}
			break;
		}
	}
	
	/**
	 * Trim the text to fit limit constraints.
	 */
	private void trimText() {
		if(text.length() > textLimit) {
			text = text.substring(0, textLimit);
			resizeBounds();
		}
	}
	
	/**
	 * @return true if the next key is a modifier
	 */
	private boolean isModifierKey(int key) {
		switch(key) {
		case Keyboard.KEY_LSHIFT:
		case Keyboard.KEY_RSHIFT:
		case Keyboard.KEY_RIGHT:
		case Keyboard.KEY_LEFT:
		case Keyboard.KEY_DOWN:
		case Keyboard.KEY_UP:
		case Keyboard.KEY_LCONTROL:
		case Keyboard.KEY_RCONTROL:
			return true;
		default:
			return false;
		}
	}
	
	@Override
	public void setState(int state) {
		super.setState(state);
		
		if(state == ENABLED) {
			textColor = "white";
		} else {
			flash = 0;
			textColor = "gray";
		}
	}

	/**
	 * Center the text in this input.
	 * @param centered
	 */
	public void setCentered(boolean centered) {
		//this.centered = centered;
	}
	
	/**
	 * @return text from this input.
	 */
	public String getText() {
		return text;
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
	
	private KeyListener createKeyListener(int style) {
		switch(style) {
		case 0:
		case 1:
			return (e -> {
				if(!isModifierKey(e.getKey())) {
					if(e.getKey() == Keyboard.KEY_BACK && text.isEmpty()) {
						return;
					} else if(e.getKey() == Keyboard.KEY_BACK && !text.isEmpty()) {
						// Remove the last character from text
						text = text.substring(0, text.length() - 1);
					} else if(text.length() <= textLimit) {
						// If the user is pasting text
						if(e.getKey() == Keyboard.KEY_V && (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) 
								|| Keyboard.isKeyDown(Keyboard.KEY_RCONTROL))) {
							paste();
						} else if(e.getKeyChar() != Keyboard.CHAR_NONE && e.getKey() != Keyboard.KEY_RETURN) {
							// Add a regular character
							addCharacter(e.getKeyChar());
						}
						// Make sure text is still inside limit
						trimText();
					}
					resizeBounds();
				}
				
				if(e.getKey() == Keyboard.KEY_RETURN) {
					InputBox.this.fireEvent(new ValueChangeEvent(text, null));
					InputBox.this.setState(DISABLED);
				}
			});
		case -1:
			return (e -> {
				text = e.getKeyName();
				resizeBounds();

				InputBox.this.fireEvent(new ValueChangeEvent(text, null));
				InputBox.this.setState(DISABLED);
			});
		}
		
		return null;
	}
	
	public void removeKeyListener(KeyListener l) {
		if(l == null) {
			return;
		}
		this.keyListener = null;
	}

	public void addKeyListener(KeyListener l) {
		this.keyListener = l;
	}
	
	public void removeValueChangeListener(ValueChangeListener l) {
		if(l == null) {
			return;
		}
		this.valueChangeListener = null;
	}

	public void addValueChangeListener(ValueChangeListener l) {
		this.valueChangeListener = l;
	}
	
	@Override
	public void fireEvent(UIEvent e) {
		super.fireEvent(e);
		
		if(e instanceof ActionEvent) {
			processActionEvent((ActionEvent) e);
		} else if(e instanceof KeyEvent) {
			processKeyEvent((KeyEvent) e);
		} else if(e instanceof ValueChangeEvent) {
			processValueChangeEvent((ValueChangeEvent) e);
		}
	}
	
	protected void processActionEvent(ActionEvent e) {
		if(actionListener != null) {
			actionListener.actionPerformed(e);
		}
	}
	
	protected void processKeyEvent(KeyEvent e) {
		if(keyListener != null) {
			keyListener.keyPress(e);
		}
	}

	protected void processValueChangeEvent(ValueChangeEvent e) {
		if(valueChangeListener != null) {
			valueChangeListener.valueChanged(e);
		}
	}
	
	class DefaultInputMouseAdapter implements MouseListener {

		public void mouseClicked(MouseEvent e) {
			setState(getState() == ENABLED ? DISABLED : ENABLED);
			InputBox.this.fireEvent(new ActionEvent());
		}

		@Override
		public void mousePressed(MouseEvent e) {
		}
		
		@Override
		public void mouseReleased(MouseEvent e) {
		}
		
		public void mouseEntered(MouseEvent e) {
			if(getState() == ENABLED) {
				textColor = "white";
			} else {
				textColor = "lightGray";
			}
		}
		
		public void mouseExited(MouseEvent e) {
			if(getState() == ENABLED) {
				textColor = "white";
			} else {
				textColor = "gray";
			}
		}
	}

	class DefaultInputTimeAdapter implements TimeListener {
		@Override
		public void timeStep(TimeEvent e) {
			if(getState() == ENABLED) {
				flash += e.getDelta();
				if(flash > 1f) {
					flash = 0;
				}
			}
		}
	}
	
}
