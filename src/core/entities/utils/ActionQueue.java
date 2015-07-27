package core.entities.utils;

import java.util.LinkedList;
import java.util.Queue;

public class ActionQueue {

	private Queue<EntityAction> actions = new LinkedList<EntityAction>();
	
	public Queue<EntityAction> getActions() {
		return actions;
	}
	
	public void addAction(EntityAction action) {
		actions.add(action);
		System.out.println(actions.size());
	}
	
	public EntityAction peekAction() {
		return actions.peek();
	}
	
	public EntityAction pollAction() {
		return actions.poll();
	}
	
	static public abstract class EntityAction {
		private CharState type;
		
		public EntityAction(CharState type) {
			this.type = type;
		}
		
		public CharState getType() {
			return type;
		}
		
		public int getInt() {
			return 0;
		}
		
		public String getString() {
			return null;
		}
		
		public float getFloat() {
			return 0f;
		}
		
		public boolean getBoolean() {
			return false;
		}
	}
	
}
