package core.ui;

import core.render.SpriteList;
import core.render.transform.Transform;
import core.ui.event.MouseEvent;
import core.ui.event.MouseListener;
import core.ui.event.MouseMotionListener;
import core.ui.event.UIEvent;
import core.ui.event.ValueChangeEvent;
import core.ui.event.ValueChangeListener;
import core.utilities.MathUtils;

public class Slider extends UIElement {

	private float value;
	
	private String slideBar;
	private Transform slideTransform;
	private String sliderKnob;
	
	private ValueChangeListener valueChangeListener;
	
	public Slider(float x, float y, float value) {
		this.value = value;
		
		this.slideBar = "SliderBG";
		this.sliderKnob = "SliderValue";
		this.slideTransform = new Transform();
		setBounds(x, y, SpriteList.get(slideBar).getWidth(), SpriteList.get(slideBar).getHeight());
		
		addMouseListener(new DefaultSliderAdapter());
		addMouseMotionListener(new DefaultSliderMotionAdapter());
	}
	
	@Override
	public void draw() {
		super.draw();
		
		setTransform(true);
		SpriteList.get(slideBar).draw(slideTransform);
		setTransform(false);
		SpriteList.get(sliderKnob).draw(slideTransform);
	}

	public float getValue() {
		return value;
	}
	
	public void setValue(float value) {
		if(this.value != value) {
			fireEvent(new ValueChangeEvent(value, value - this.value));
			this.value = value;
		}
	}
	
	private void setTransform(boolean slide) {
		if(slide) {
			slideTransform.x = getX();
			slideTransform.y = getY();
		} else {
			slideTransform.x = (float) (getX() + (bounds.getWidth() * value) - (SpriteList.get(sliderKnob).getWidth() / 2f));
			slideTransform.y = getY();
		}
	}
	
	public void removeValueChangeListener(ValueChangeListener l) {
		if(l == null) {
			return;
		}
		
		valueChangeListener = null;
	}
	
	public void addValueChangeListener(ValueChangeListener l) {
		this.valueChangeListener = l;
	}
	
	@Override
	public void fireEvent(UIEvent e) {
		super.fireEvent(e);
		
		if(e instanceof ValueChangeEvent) {
			processValueChangeEvent((ValueChangeEvent) e);
		}
	}
	
	protected void processValueChangeEvent(ValueChangeEvent e) {
		if(valueChangeListener != null) {
			valueChangeListener.valueChanged(e);
		}
	}
	
	class DefaultSliderAdapter implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			setValue(MathUtils.clamp((float) ((e.getX() - bounds.getX()) / (bounds.getMaxX() - bounds.getX())), 0f, 1f));
		}

		@Override
		public void mousePressed(MouseEvent e) {
			setValue(MathUtils.clamp((float) ((e.getX() - bounds.getX()) / (bounds.getMaxX() - bounds.getX())), 0f, 1f));
		}

		@Override
		public void mouseReleased(MouseEvent e) {
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}

	}
	
	class DefaultSliderMotionAdapter implements MouseMotionListener {

		@Override
		public void mouseMoved(MouseEvent e) {
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			setValue(MathUtils.clamp((float) ((e.getX() - getX()) / bounds.getWidth()), 0f, 1f));
		}
	}

}
