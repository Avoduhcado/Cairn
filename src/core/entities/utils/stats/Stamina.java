package core.entities.utils.stats;

import core.Theater;
import core.utilities.MathFunctions;

public class Stamina extends Statistic {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private transient float regainCooldown = 0f;
	private float regainSpeed = 1f;
	
	public Stamina(float current, float max) {
		super(current, max);
	}

	@Override
	public void update() {
		if(current < max && regainCooldown == 0) {
			current = MathFunctions.clamp(current + (Theater.getDeltaSpeed(0.025f) * regainSpeed), 0, max);
		} else if(regainCooldown > 0) {
			regainCooldown = MathFunctions.clamp(regainCooldown - Theater.getDeltaSpeed(0.025f), 0, regainCooldown);
		}
	}
	
	@Override
	public void addCurrent(float toCurrent) {
		super.addCurrent(toCurrent);
		
		regainCooldown = (current == 0 ? 1.85f : 0.75f);
	}
	
	public void setRegainSpeed(float regainSpeed) {
		this.regainSpeed = regainSpeed;
	}

}
