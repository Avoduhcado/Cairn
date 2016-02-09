package core.ui;

import core.ui.event.ActionEvent;
import core.ui.event.MouseEvent;
import core.ui.event.MouseListener;

public class CheckBox extends Button {

	private boolean checked;
	
	public CheckBox(float x, float y, String frame, String text) {
		super(x, y, frame, text);
		
		addMouseListener(new DefaultCheckBoxAdapter());
	}
	
	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	/**
	 * Handle the default actions for mouse events on a button
	 */
	class DefaultCheckBoxAdapter implements MouseListener {

		public void mouseClicked(MouseEvent e) {
			checked = !checked;
			if(checked) {
				setTextColor("white");
			}
			CheckBox.this.fireEvent(new ActionEvent());
		}

		@Override
		public void mousePressed(MouseEvent e) {
		}
		
		@Override
		public void mouseReleased(MouseEvent e) {
		}
		
		public void mouseEntered(MouseEvent e) {
			setTextColor("lightGray");
		}
		
		public void mouseExited(MouseEvent e) {
			if(!checked) {
				setTextColor("gray");
			} else {
				setTextColor("white");
			}
		}
	}

}
