package core.ui.overlays;

import java.util.LinkedList;

import org.lwjgl.input.Keyboard;
import org.newdawn.slick.openal.SoundStore;

import core.Camera;
import core.ui.Button;
import core.ui.CheckBox;
import core.ui.ElementGroup;
import core.ui.InputBox;
import core.ui.Label;
import core.ui.Slider;
import core.ui.UIElement;
import core.ui.utils.Align;
import core.utilities.keyboard.Keybind;

public class OptionsMenu extends MenuOverlay {

	public OptionsMenu() {
		super();
		/*try {
			displayModes = Display.getAvailableDisplayModes();
			for(DisplayMode d : displayModes)
				modes += d.toString() + "\n";
		} catch (LWJGLException e) {
			e.printStackTrace();
		}*/
		
		Label optionsLabel = new Label(Float.NaN, Camera.get().getDisplayHeight(0.1f), null, "Options");
		optionsLabel.setAlign(Align.CENTER);
		add(optionsLabel);

		Slider musicSlider = new Slider(Camera.get().getDisplayWidth(0.35f), 
				Camera.get().getDisplayHeight(0.1667f), 
				SoundStore.get().getMusicVolume());
		musicSlider.addValueChangeListener(e -> {
			if(SoundStore.get().getMusicVolume() != musicSlider.getValue()) {
				SoundStore.get().setMusicVolume(musicSlider.getValue());
				SoundStore.get().setCurrentMusicVolume(musicSlider.getValue());
			}
		});
		add(musicSlider);

		Label musicLabel = new Label(Camera.get().getDisplayWidth(0.35f), 
				(float) (musicSlider.getBounds().getY() - (musicSlider.getBounds().getHeight() / 2f)),
				null, "Music Volume: ");
		musicLabel.setAlign(Align.LEFT);
		add(musicLabel);

		Slider sfxSlider = new Slider(Camera.get().getDisplayWidth(0.35f),
				(float) (musicSlider.getBounds().getMaxY() + musicSlider.getBounds().getHeight()), 
				SoundStore.get().getSoundVolume());
		sfxSlider.addValueChangeListener(e -> {
			if(SoundStore.get().getSoundVolume() != sfxSlider.getValue()) {
				SoundStore.get().setSoundVolume(sfxSlider.getValue());
			}
		});
		add(sfxSlider);
		
		Label sfxLabel = new Label(Camera.get().getDisplayWidth(0.35f), 
				(float) (sfxSlider.getBounds().getY() - (sfxSlider.getBounds().getHeight() / 2f)),
				null, "Sound Volume: ");
		sfxLabel.setAlign(Align.LEFT);
		add(sfxLabel);
		
		CheckBox fullscreenCheck = new CheckBox(Camera.get().getDisplayWidth(0.6f),
				Camera.get().getDisplayHeight(0.1667f), 
				null, "Fullscreen");
		fullscreenCheck.setChecked(Camera.get().isFullscreen());
		fullscreenCheck.addActionListener(e -> {
			Camera.get().setFullscreen(fullscreenCheck.isChecked());
		});
		add(fullscreenCheck);
		
		CheckBox vsyncCheck = new CheckBox(Camera.get().getDisplayWidth(0.6f),
				(float) fullscreenCheck.getBounds().getMaxY(), null, "VSync");
		vsyncCheck.setChecked(Camera.get().isVSyncEnabled());
		vsyncCheck.addActionListener(e -> {
			Camera.get().setVSync(vsyncCheck.isChecked());
		});
		add(vsyncCheck);
		
		LinkedList<ElementGroup<UIElement>> keybinds = new LinkedList<ElementGroup<UIElement>>();
		float keyX = Camera.get().getDisplayWidth(0.25f);
		float keyY = 0;
		for(int i = 0; i<Keybind.values().length; i++) {
			ElementGroup<UIElement> key = new ElementGroup<UIElement>();
			
			Label keyLabel = new Label(keyX, 
					Camera.get().getDisplayHeight(0.285f) + keyY,
					null, Keybind.values()[i].toString() + ": ");
			keyLabel.setStill(true);
			keyLabel.setAlign(Align.LEFT);
			key.add(keyLabel);
			
			InputBox keyBox = new InputBox(keyX, 
					Camera.get().getDisplayHeight(0.285f) + keyY,
					 null, Keybind.values()[i].getKey(), -1, 0);
			keyBox.setState(DISABLED);
			keyBox.setCentered(false);
			keyBox.addActionListener(e -> OptionsMenu.this.setFocus(keyBox));
			keyBox.addValueChangeListener(e -> {
				Keybind.valueOf(keyLabel.getPlainText().split(":")[0]).setKey(Keyboard.getKeyIndex(keyBox.getText()));
				keyBox.setState(DISABLED);
			});
			key.add(keyBox);
			
			keybinds.add(key);
			add(keybinds.getLast());
			
			keyY += keyLabel.getBounds().getHeight();
			if(Camera.get().getDisplayHeight(0.285f) + keyY > Camera.get().getDisplayHeight(0.8f)) {
				keyX += Camera.get().getDisplayWidth(0.25f);
				keyY = 0;
			}
		}
		
		Button close = new Button(Float.NaN, Camera.get().getDisplayHeight(0.85f), null, "Close");
		close.setAlign(Align.CENTER);
		close.addActionListener(e -> setState(KILL_FLAG));
		add(close);
		
		setBounds();
		setFrame("Menu2");
		setStill(true);
	}

}
