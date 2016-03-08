package core.entities_new.components.interactions;

import core.entities_new.Entity;
import core.setups.GameSetup;
import core.ui.TextBox;

public class Script implements Scriptable {

	private Entity source;
	private Entity reader;
	private String data;
	private boolean reading;
	
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
		
		TextBox dialog = new TextBox(source.getZBody().getX(), source.getZBody().getY(), null, "We reading now", true);
		((GameSetup) source.getContainer()).addUI(dialog);
		
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
		TextBox dialog = new TextBox(reader.getZBody().getX(), reader.getZBody().getY(), null, "See ya later faaaag", true);
		((GameSetup) source.getContainer()).addUI(dialog);
		
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
