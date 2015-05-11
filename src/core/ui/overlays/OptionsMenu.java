package core.ui.overlays;

import java.awt.geom.Rectangle2D;
import org.lwjgl.input.Keyboard;

import core.Camera;
import core.audio.Ensemble;
import core.ui.Button;
import core.ui.ElementGroup;
import core.ui.InputBox;
import core.ui.Slider;
import core.utilities.keyboard.Keybinds;
import core.utilities.text.Text;

public class OptionsMenu extends MenuOverlay {

	//private DisplayMode[] displayModes;
	//private String modes = "";
	private Slider volumeSlider;
	private Button close;
	private ElementGroup keybinds = new ElementGroup();
	
	public OptionsMenu(float x, float y, String image) {
		super(x, y, image);
		System.out.println("We have options");
		
		setStill(true);
		this.box = new Rectangle2D.Double(x, y, Camera.get().getDisplayWidth() - (this.frame.getWidth() * 0.667f),
				Camera.get().getDisplayHeight() - (this.frame.getWidth() * 0.667f));
		
		/*try {
			displayModes = Display.getAvailableDisplayModes();
			for(DisplayMode d : displayModes)
				modes += d.toString() + "\n";
		} catch (LWJGLException e) {
			e.printStackTrace();
		}*/
		
		volumeSlider = new Slider(Camera.get().getDisplayWidth(0.5f), Camera.get().getDisplayHeight(0.1667f), 1f, Ensemble.get().getMasterVolume(), "SliderBG", "SliderValue");
		volumeSlider.setPosition((float) (volumeSlider.getX() - (volumeSlider.getBox().getWidth() * 0.5f)), volumeSlider.getY());
		volumeSlider.setStill(true);
		
		float keyX = Camera.get().getDisplayWidth(0.25f);
		float keyY = 0;
		for(int i = 0; i<Keybinds.values().length; i++) {
			if(!keybinds.isEmpty())
				keyY += keybinds.get(keybinds.size() - 1).getBox().getHeight();
			if(Camera.get().getDisplayHeight(0.285f) + keyY > this.getBox().getHeight() * 0.85f) {
				keyX *= 3f;
				keyY = 0;
			}
				
			keybinds.add(new InputBox(keyX, Camera.get().getDisplayHeight(0.285f) + keyY, null, -1, Keybinds.values()[i].getKey(), 0));
			keybinds.get(keybinds.size() - 1).setEnabled(false);
			keybinds.get(keybinds.size() - 1).setStill(true);
			((InputBox) keybinds.get(keybinds.size() - 1)).setCentered(false);
		}
		
		close = new Button("Close", Float.NaN, Camera.get().getDisplayHeight(0.85f), 0, null);
		close.setStill(true);
	}
	
	@Override
	public void update() {
		close.update();
		for(int i = 0; i<keybinds.size(); i++) {
			keybinds.get(i).update();
			if(keybinds.get(i).isClicked()) {
				keybinds.setEnabledAllExcept(false, keybinds.get(i));
			}
			if(keybinds.get(i).isEnabled() && ((InputBox) keybinds.get(i)).input() != null) {
				Keybinds.values()[i].setKey(Keyboard.getKeyIndex(((InputBox) keybinds.get(i)).getText()));
				keybinds.get(i).setEnabled(false);
			}
		}
		
		volumeSlider.update();
		if(Ensemble.get().getMasterVolume() != volumeSlider.getValue()) {
			Ensemble.get().setMasterVolume(volumeSlider.getValue());
		}
	}
	
	@Override
	public void draw() {
		super.draw();
		
		Text.getDefault().setStill(true);
		Text.getDefault().setCentered(true);
		Text.getDefault().drawString("Options", Camera.get().getDisplayWidth(0.5f), y);
		Text.getDefault().setStill(true);
		Text.getDefault().setCentered(true);
		Text.getDefault().drawString("Volume", (float) volumeSlider.getBox().getCenterX(),
				volumeSlider.getY() - (Text.getDefault().getHeight("Volume") * 1.5f));
		volumeSlider.draw();
		for(int i = 0; i<keybinds.size(); i++) {
			Text.getDefault().setStill(true);
			Text.getDefault().drawString(Keybinds.values()[i].toString() + ":", 
					keybinds.get(i).getX() - Text.getDefault().getWidth(Keybinds.values()[i].toString() + ": "),
					keybinds.get(i).getY());
			keybinds.get(i).draw();
		}
		
		close.draw();
	}
	
	@Override
	public boolean isCloseRequest() {
		return close.isClicked() || Keybinds.EXIT.clicked();
	}

}
