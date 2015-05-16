package core.entities.utils.stats;

import java.io.Serializable;

import core.utilities.MathFunctions;

public abstract class Statistic implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected float current;
	protected float max;
	
	public Statistic(float current, float max) {
		this.current = current;
		this.max = max;
	}
	
	public abstract void update();
	
	public void addCurrent(float toCurrent) {
		this.current = MathFunctions.clamp(this.current + toCurrent, 0, this.max);
	}
	
	public void setCurrent(float current) {
		this.current = current;
	}
	
	public float getCurrent() {
		return current;
	}
	
	public void addMax(float toMax) {
		this.max += toMax;
	}
	
	public void setMax(float max) {
		this.max = max;
	}
	
	public float getMax() {
		return max;
	}

}
