package core.entities;

import core.entities.interfaces.Scriptable;
import core.setups.Stage;
import core.utilities.scripts.Script;

public class Ally extends Actor implements Scriptable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Script dialogue;
	
	public Ally(float x, float y, String ref, float scale, Script dialogue) {
		super(x, y, ref, scale);
		
		this.dialogue = dialogue;
	}
	
	@Override
	public void update() {
		super.update();
		
		if(dialogue != null && dialogue.isActive()) {
			dialogue.update();
		}
	}

	@Override
	public void draw() {
		super.draw();
		
		if(dialogue != null && dialogue.isActive()) {
			dialogue.draw((float) (getBox().getMaxX() + (getBox().getWidth() * 0.2f)),
					(float) (getBox().getY() - (getBox().getHeight() * 1.2f)));
		}
	}

	@Override
	public Script getScript() {
		return dialogue;
	}

	@Override
	public void setScript(Script dialogue) {
		this.dialogue = dialogue;
	}

	@Override
	public void activateScript(Entity player, Stage stage) {
		if(!dialogue.isActive() && player.getBox().intersects(getBox().getX() - getBox().getWidth(),
				getBox().getY(), getBox().getWidth() * 3f, getBox().getHeight())) {
			dialogue.setActive(true);
			animState.setAnimation(0, "Talking", true);
		} else if(dialogue.isActive() && !player.getBox().intersects(getBox().getX() - getBox().getWidth(),
				getBox().getY(), getBox().getWidth() * 3f, getBox().getHeight())) {
			dialogue.leave();
			animState.setAnimation(0, "Idle", true);
		}
	}
	
}
