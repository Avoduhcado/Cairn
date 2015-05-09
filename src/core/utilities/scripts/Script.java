package core.utilities.scripts;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import core.Camera;
import core.Theater;
import core.entities.Entity;
import core.setups.Stage;
import core.ui.TextBox;
import core.ui.event.ScreenText;

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
	
	private String event = "{event: [{showText: 'Fair tidings, child.;Good to see you returned unharmed.'},{showText: 'Might you care for a super cool playable demo?'}, {choose: [{option: 'YES!!',result: [{showText: 'Neato'}]},{option: No,result: [{showText: 'Oh'},{showText: 'Ok then...'}]}]},{showText: Goodbye} ] }";
	private transient ScriptData data;
	
	public Script(String flavorText) {
		this.flavorText = flavorText;
		this.prompt = new TextBox(this.flavorText, 0, 0, "Textbox", true);
		this.prompt.setEvent(this);
		this.prompt.setOpacity(1f);
		
		data = new ScriptData(event);
	}
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		
		this.prompt = new TextBox(flavorText, 0, 0, "Textbox", true);
		this.prompt.setEvent(this);
		this.prompt.setOpacity(1f);
		
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
				prompt.draw((float) talker.getBox().getMaxX(), talker.getY());
			} else {
				prompt.draw((float) talker.getBox().getX() - prompt.getWidth((int) prompt.getTextFill()), talker.getY());
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

	@Override
	public void processed() {
		System.out.println("PRocessing bruh :DD");
		readScript();
	}

	@Override
	public void processedResult(int result) {
		System.out.println("PRocessing result DDDD:");
		data.parseResult(result);
		readScript();
	}
	
}
