package core.entities.interfaces;

import core.entities.Entity;
import core.setups.Stage;
import core.utilities.scripts.Script;
@Deprecated
public interface Scriptable {

	public Script getScript();
	public void setScript(Script script);
	
	public void activateScript(Entity player, Stage stage);
	
}
