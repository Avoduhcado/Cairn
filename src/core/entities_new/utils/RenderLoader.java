package core.entities_new.utils;

import core.entities_new.Entity;
import core.entities_new.components.renders.GridRender;
import core.entities_new.components.renders.PlainRender;
import core.entities_new.components.renders.Renderable;
import core.entities_new.components.renders.SpineRender;
import core.utilities.Resources;
import net.lingala.zip4j.model.FileHeader;

public class RenderLoader {

	public static Renderable loadRender(String name, Entity entity) {
		FileHeader dir = Resources.get().getResourceHeader(name + "/");
		if(dir != null && dir.isDirectory()) {
			if(Resources.get().getResourceHeader(name + "/" + name + ".json") != null) {
				return new SpineRender(name, entity);
			} else if(Resources.get().getResourceHeader(name + "/" + name + ".avl") != null) {
				return new GridRender(name, entity);
			}
		} else if(Resources.get().getResourceHeader(name + ".png") != null) {
			return new PlainRender(name, entity);
		}
		
		return null;
	}
	
}
