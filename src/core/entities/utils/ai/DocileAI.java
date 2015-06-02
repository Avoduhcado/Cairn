package core.entities.utils.ai;

import com.esotericsoftware.spine.AnimationState;

import core.entities.interfaces.Combatant;
import core.entities.utils.CharState;
import core.entities.utils.ai.traits.Trait;

public class DocileAI extends Intelligence {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DocileAI() {
		
	}

	@Override
	public void alert(Combatant target) {
		for(Trait t : traits) {
			t.alert(target);
		}
	}
	
	@Override
	public void pickAction() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean applyTraitStateModifier(CharState state, AnimationState animState) {
		switch(state) {
		case IDLE:
			animState.setAnimation(0, "Passive", true);
			return true;
		default:
			return false;
		}
	}
	
}
