package core.entities_new.utils;

import core.entities_new.Entity;
import core.entities_new.components.renders.GridRender;
import core.entities_new.components.renders.PlainRender;
import core.entities_new.components.renders.Renderable;
import core.entities_new.components.renders.SpineRender;
import core.utilities.Resources;

public class RenderLoader {

	public static Renderable loadRender(String name, Entity entity) {
		if(Resources.get().isDirectory(name + "/")) {
			if(Resources.get().resourceExists(name + "/" + name + ".json")) {
				return new SpineRender(name, entity);
			} else if(Resources.get().resourceExists(name + "/" + name + ".avl")) {
				return new GridRender(name, entity);
			}
		} else if(Resources.get().resourceExists(name + ".png")) {
			return new PlainRender(name, entity);
		}
		
		return null;
	}
	
}
