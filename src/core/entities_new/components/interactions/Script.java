package core.entities_new.components.interactions;

import core.entities_new.Entity;
import core.setups.GameSetup;
import core.ui.TextBox;
import core.ui.UIElement;

public class Script implements Scriptable {

	private Entity source;
	private Entity reader;
	private String data;
	private boolean reading;
	
	private UIElement screenElement;
	
	public Script(Entity source, String data) {
		parseData();
		this.source = source;
	}

	private void parseData() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startReading(Entity reader) {
		setReading(true);
		setReader(reader);
		
		// TODO Add some kinda like "FACE COORDINATES" so that people can talk and look reasonable
		screenElement = new TextBox(source.getZBody().getX() + 50, source.getZBody().getY() - 140, "Textbox", "sup nerd", true);
		//screenElement = new TextBox(source.getZBody().getX() + 50, source.getZBody().getY() - 140, "Textbox", "sup nerd", true);
		((GameSetup) source.getContainer()).addUI(screenElement);
		
		System.out.println("We reading");
	}

	@Override
	public void read() {
		
	}

	@Override
	public void endReading() {
		setReading(false);
	}

	@Override
	public void interrupt() {
		if(screenElement != null) {
			((GameSetup) source.getContainer()).getUI().remove(screenElement);
		}
		screenElement = new TextBox(reader.getZBody().getX(), reader.getZBody().getY(), null, "See ya later faaaag", false);
		((TextBox) screenElement).setKillTimer(2.5f);
		((GameSetup) source.getContainer()).addUI(screenElement);
		
		setReading(false);
		setReader(null);
	}

	@Override
	public boolean isBusyReading() {
		return reading;
	}
	
	private void setReading(boolean reading) {
		this.reading = reading;
	}

	private void setReader(Entity reader) {
		this.reader = reader;
	}

}
