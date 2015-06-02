package core.entities.utils.ai.traits;

import core.entities.Actor;
import core.entities.interfaces.Combatant;
import core.entities.utils.CharState;

public class Opportunist extends Trait {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private float opportunityRating;
	
	public Opportunist(float opportunityRating) {
		setOpportunityRating(opportunityRating);
	}

	@Override
	public void process() {
		if(((Actor) host).getState().canAct() && host.getIntelligence().getTarget() != null) {
			if(((Actor) host).canRun() && ((Actor) host).getVelocity().length() > 0
					&& ((Combatant) host).getStats().getStamina().getCurrentPercent() > 0.5f) {
				((Actor) host).setState(CharState.RUN);
			}
			if(((Actor) host.getIntelligence().getTarget()).getState() == CharState.ATTACK) {
				System.out.println(Math.random() < getOpportunityRating());
			}
		}
	}

	@Override
	public void alert(Combatant target) {
		// TODO Auto-generated method stub

	}

	public float getOpportunityRating() {
		return opportunityRating;
	}

	public void setOpportunityRating(float opportunityRating) {
		this.opportunityRating = opportunityRating;
	}

}
