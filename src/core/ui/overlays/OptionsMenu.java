package core.ui.overlays;

import java.awt.geom.Rectangle2D;

import org.lwjgl.input.Keyboard;
import org.newdawn.slick.openal.SoundStore;

import core.Camera;
import core.ui.Button;
import core.ui.ElementGroup;
import core.ui.InputBox;
import core.ui.Slider;
import core.utilities.keyboard.Keybinds;
import core.utilities.text.Text;

public class OptionsMenu extends MenuOverlay {

	//private DisplayMode[] displayModes;
	//private String modes = "";
	private Slider musicSlider;
	private Slider sfxSlider;
	private Button close;
	private ElementGroup keybinds = new ElementGroup();
	
	public OptionsMenu(float x, float y, String image) {
		super(x, y, image);
		
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
		
		musicSlider = new Slider(Camera.get().getDisplayWidth(0.5f), Camera.get().getDisplayHeight(0.1667f), 1f, SoundStore.get().getMusicVolume(), "SliderBG", "SliderValue");
		//musicSlider.setPosition((float) (musicSlider.getX() - (musicSlider.getBox().getWidth() * 0.5f)), musicSlider.getY());
		musicSlider.setStill(true);
		
		sfxSlider = new Slider(Camera.get().getDisplayWidth(0.5f), (float) (musicSlider.getBox().getMaxY() + musicSlider.getBox().getHeight()),
				1f, SoundStore.get().getSoundVolume(), "SliderBG", "SliderValue");
		//sfxSlider.setPosition((float) (sfxSlider.getX() - (sfxSlider.getBox().getWidth() * 0.5f)),
			//	(float) (sfxSlider.getY() + sfxSlider.getBox().getHeight()));
		sfxSlider.setStill(true);
		
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
		
		musicSlider.update();
		if(SoundStore.get().getMusicVolume() != musicSlider.getValue()) {
			SoundStore.get().setMusicVolume(musicSlider.getValue());
			SoundStore.get().setCurrentMusicVolume(musicSlider.getValue());
		}
		
		sfxSlider.update();
		if(SoundStore.get().getSoundVolume() != sfxSlider.getValue()) {
			SoundStore.get().setSoundVolume(sfxSlider.getValue());
		}
		/*if(Ensemble.get().getMasterVolume() != volumeSlider.getValue()) {
			Ensemble.get().setMasterVolume(volumeSlider.getValue());
		}*/
	}
	
	@Override
	public void draw() {
		super.draw();
		
		Text.getDefault().setStill(true);
		Text.getDefault().setCentered(true);
		Text.getDefault().drawString("Options", Camera.get().getDisplayWidth(0.5f), y);
		
		Text.getDefault().setStill(true);
		//Text.getDefault().setCentered(true);
		Text.getDefault().drawString("Music Volume: ", (float) musicSlider.getX() - (Text.getDefault().getWidth("Music Volume: ")),
				musicSlider.getY() - (float) (musicSlider.getBox().getHeight() / 2f));
		musicSlider.draw();
		
		Text.getDefault().setStill(true);
		//Text.getDefault().setCentered(true);
		Text.getDefault().drawString("Sound Volume: ", (float) sfxSlider.getX() - (Text.getDefault().getWidth("Sound Volume: ")),
				sfxSlider.getY() - (float) (sfxSlider.getBox().getHeight() / 2f));
		sfxSlider.draw();
		
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
