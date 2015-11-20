package core.entities;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import core.Camera;
import core.entities.interfaces.Scriptable;
import core.interactions.InteractionListener;
import core.setups.Stage;
import core.utilities.scripts.Script;

@Deprecated
public class Ally extends Actor implements Scriptable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Script dialogue;
	
	public Ally(float x, float y, String ref, Script dialogue) {
		super(x, y, ref);
		
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
			dialogue.draw(this, this.getX() > Camera.get().frame.getCenterX());
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
		if(!dialogue.isActive() && Point2D.distance(player.getX(), player.getY(), this.getX(), this.getY()) <= 150
				&& Line2D.ptLineDist(0, this.getY(), 1, this.getY(), player.getX(), player.getY()) <= 100) {
			dialogue.setActive(true);
			animState.setAnimation(0, "Talking", true);
		} else if(dialogue.isActive() && (Point2D.distance(player.getX(), player.getY(), this.getX(), this.getY()) > 150
				|| Line2D.ptLineDist(0, this.getY(), 1, this.getY(), player.getX(), player.getY()) > 100)) {
			dialogue.leave();
			animState.setAnimation(0, "Idle", true);
		}
	}
	
}
