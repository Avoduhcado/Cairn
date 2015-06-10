package core.utilities.scripts;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import core.Camera;
import core.Theater;
import core.entities.Entity;
import core.setups.Stage;
import core.ui.TextBox;
import core.ui.screen.ScreenText;

public class Script implements Serializable, ScriptEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private transient TextBox prompt;
	/** TODO Make flavortext an array for available text options like interruptions
	 * Add prompt text to setup UI and include a callback to kill/update it as necessary
	 */
	private String flavorText;
	private transient boolean active;
	
	private String event;
	private transient ScriptData data;
	
	public Script(String flavorText, String event) {
		this.flavorText = flavorText;
		this.prompt = new TextBox(this.flavorText, 0, 0, "Textbox", true);
		this.prompt.addEvent(null);
		this.prompt.setScriptEvent(this);
		//this.prompt.setOpacity(1f);
		setEvent(event);
	}
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		
		this.prompt = new TextBox(flavorText, 0, 0, "Textbox", true);
		this.prompt.setScriptEvent(this);
		//this.prompt.setOpacity(1f);
		
		data = new ScriptData(event);
	}

	public void update() {
		if(prompt.isEnabled()) {
			this.prompt.update();
		}
	}
	
	public void draw(Entity talker, boolean right) {
		if(prompt.isEnabled()) {
			if(right) {
				prompt.draw((float) talker.getBox().getMaxX(), (float) talker.getBox().getY());
			} else {
				prompt.draw((float) talker.getBox().getX() - prompt.getWidth((int) prompt.getTextFill()),
						(float) talker.getBox().getY());
			}
		}
	}
	
	public void readScript() {
		if(!data.isStarted()) {
			data.setStarted(true);
			prompt.setEnabled(false);
		}
		Theater.get().getSetup().getUI().clear();
		
		if(data.getCurrent() == null) {
			setActive(false);
			data.fillQueue();
			data.setStarted(false);
		} else {
			data.getCommand().parse((Stage) Theater.get().getSetup(), this);
		}
	}
	
	public void leave() {
		if(data.isStarted()) {
			data.fillQueue();
			data.setStarted(false);
			prompt.setEnabled(false);
			Theater.get().getSetup().getUI().clear();
			ScreenText leaveText = new ScreenText("Oi!;I was talking to you!",
					Camera.get().getDisplayWidth(0.5f), Camera.get().getDisplayHeight(0.8f));
			leaveText.setKillTimer(2.15f);
			Theater.get().getSetup().addUI(leaveText);
		} else {
			prompt.setEnabled(false);
		}
		
		setActive(false);
	}
	
	public boolean isActive() {
		return active;
	}
	
	public void setActive(boolean active) {
		this.active = active;
		if(active) {
			prompt.setTextFill(0f);
			prompt.setEnabled(true);
		}
	}

	public void setEvent(String event) {
		this.event = event;
		data = new ScriptData(this.event);
	}
	
	@Override
	public void processed() {
		readScript();
	}

	@Override
	public void processedResult(int result) {
		data.parseResult(result);
		readScript();
	}
	
}
