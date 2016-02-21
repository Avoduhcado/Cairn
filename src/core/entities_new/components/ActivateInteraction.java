package core.entities_new.components;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.dynamics.FixtureDef;

import core.entities_new.Entity;
import core.entities_new.utils.SensorData;
import core.setups.Stage;

public class ActivateInteraction extends Interaction {

	public ActivateInteraction(Entity entity, Script script) {
		super(entity, script);
		
		entity.getBody().createFixture(createActivationRange());
	}
	
	private FixtureDef createActivationRange() {
		CircleShape bodyShape = new CircleShape();
		bodyShape.setRadius(100f / Stage.SCALE_FACTOR);

		FixtureDef boxFixture = new FixtureDef();
		boxFixture.density = 0f;
		boxFixture.shape = bodyShape;
		boxFixture.isSensor = true;
		boxFixture.filter.categoryBits = 0b0001;
		boxFixture.filter.maskBits = 0b0110;
		boxFixture.userData = new SensorData(entity, "Activator", SensorData.INTERACTION);
		
		return boxFixture;
	}
	
}
