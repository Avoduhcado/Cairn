package core.entities.utils.ai.traits;

import java.io.Serializable;

import core.entities.interfaces.Combatant;
import core.entities.interfaces.Intelligent;

public abstract class Trait implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected Intelligent host;
	
	public abstract void process();
	public abstract void alert(Combatant target);

	public Intelligent getHost() {
		return host;
	}
	
	public void setHost(Intelligent host) {
		this.host = host;
	}
	
}
