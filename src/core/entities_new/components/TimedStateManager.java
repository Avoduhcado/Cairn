package core.entities_new.components;

import core.Theater;
import core.entities_new.Entity;
import core.entities_new.State;
import core.utilities.MathFunctions;

public class TimedStateManager implements StateManager {

	private Entity entity;
	private float life;
	
	public TimedStateManager(Entity entity, float life) {
		this.entity = entity;
		this.life = life;
	}
	
	@Override
	public void resolveState() {
		life = MathFunctions.clamp(life - Theater.getDeltaSpeed(0.025f), 0, life);
		if(life <= 0) {
			entity.destroy();
		}
	}

	@Override
	public State getState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void changeStateForced(State state) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void changeState(State state) {
		// TODO Auto-generated method stub
		
	}

}
