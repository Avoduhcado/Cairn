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
import core.ui.utils.ClickEvent;
import core.ui.utils.ValueChangeEvent;
import core.utilities.keyboard.Keybinds;

public class OptionsMenu extends MenuOverlay {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//private DisplayMode[] displayModes;
	//private String modes = "";
		
	public OptionsMenu(String image) {
		/*try {
			displayModes = Display.getAvailableDisplayModes();
			for(DisplayMode d : displayModes)
				modes += d.toString() + "\n";
		} catch (LWJGLException e) {
			e.printStackTrace();
		}*/
		
		Label optionsLabel = new Label("Options", Float.NaN, Camera.get().getDisplayHeight(0.1f), null);
		optionsLabel.setAlign(Align.CENTER);
		optionsLabel.setStill(true);
		this.add(optionsLabel);

		final Slider musicSlider = new Slider(Camera.get().getDisplayWidth(0.35f), Camera.get().getDisplayHeight(0.1667f),
				1f, SoundStore.get().getMusicVolume(), "SliderBG", "SliderValue");
		musicSlider.setStill(true);
		musicSlider.addEvent(new ValueChangeEvent(musicSlider) {
			public void changeValue() {
				if(SoundStore.get().getMusicVolume() != musicSlider.getValue()) {
					SoundStore.get().setMusicVolume(musicSlider.getValue());
					SoundStore.get().setCurrentMusicVolume(musicSlider.getValue());
				}
			}
		});
		this.add(musicSlider);

		Label musicLabel = new Label("Music Volume: ", Camera.get().getDisplayWidth(0.35f), 
				(float) (musicSlider.getBounds().getY() - (musicSlider.getBounds().getHeight() / 2f)), null);
		musicLabel.setStill(true);
		musicLabel.setAlign(Align.LEFT);
		this.add(musicLabel);

		final Slider sfxSlider = new Slider(Camera.get().getDisplayWidth(0.35f),
				(float) (musicSlider.getBounds().getMaxY() + musicSlider.getBounds().getHeight()),
				1f, SoundStore.get().getSoundVolume(), "SliderBG", "SliderValue");
		sfxSlider.setStill(true);
		sfxSlider.addEvent(new ValueChangeEvent(sfxSlider) {
			public void changeValue() {
				if(SoundStore.get().getSoundVolume() != sfxSlider.getValue()) {
					SoundStore.get().setSoundVolume(sfxSlider.getValue());
				}
			}
		});
		this.add(sfxSlider);
		
		Label sfxLabel = new Label("Sound Volume: ", Camera.get().getDisplayWidth(0.35f), 
				(float) (sfxSlider.getBounds().getY() - (sfxSlider.getBounds().getHeight() / 2f)), null);
		sfxLabel.setStill(true);
		sfxLabel.setAlign(Align.LEFT);
		this.add(sfxLabel);
		
		final CheckBox fullscreenCheck = new CheckBox("Fullscreen", Camera.get().getDisplayWidth(0.6f),
				Camera.get().getDisplayHeight(0.1667f), null);
		fullscreenCheck.setStill(true);
		fullscreenCheck.setChecked(Camera.get().isFullscreen());
		fullscreenCheck.addEvent(new ClickEvent(fullscreenCheck) {
			public void click() {
				Camera.get().setFullscreen(fullscreenCheck.isChecked());
			}
		});
		this.add(fullscreenCheck);
		
		final CheckBox vsyncCheck = new CheckBox("VSync", Camera.get().getDisplayWidth(0.6f),
				(float) fullscreenCheck.getBounds().getMaxY(), null);
		vsyncCheck.setStill(true);
		vsyncCheck.setChecked(Camera.get().isVSyncEnabled());
		vsyncCheck.addEvent(new ClickEvent(vsyncCheck) {
			public void click() {
				Camera.get().setVSync(vsyncCheck.isChecked());
			}
		});
		this.add(vsyncCheck);
		
		LinkedList<ElementGroup<UIElement>> keybinds = new LinkedList<ElementGroup<UIElement>>();
		float keyX = Camera.get().getDisplayWidth(0.25f);
		float keyY = 0;
		for(int i = 0; i<Keybinds.values().length; i++) {
			ElementGroup<UIElement> key = new ElementGroup<UIElement>();
			
			final Label keyLabel = new Label(Keybinds.values()[i].toString() + ": ", keyX, Camera.get().getDisplayHeight(0.285f) + keyY, null);
			keyLabel.setStill(true);
			keyLabel.setAlign(Align.LEFT);
			key.add(keyLabel);
			
			final InputBox keyBox = new InputBox(Keybinds.values()[i].getKey(), keyX, Camera.get().getDisplayHeight(0.285f) + keyY, null, -1, 0);
			keyBox.setEnabled(false);
			keyBox.setStill(true);
			keyBox.setCentered(false);
			keyBox.addEvent(new ClickEvent(keyBox) {
				public void click() {
					OptionsMenu.this.setFocus(keyBox);
				}
			});
			keyBox.addEvent(new ValueChangeEvent(keyBox) {
				public void changeValue() {
					Keybinds.valueOf(keyLabel.getText().split(":")[0]).setKey(Keyboard.getKeyIndex(keyBox.getText()));
					keyBox.setEnabled(false);
				}
			});
			key.add(keyBox);
			
			keybinds.add(key);
			this.addAll(keybinds.getLast());
			
			keyY += keyLabel.getBounds().getHeight();
			if(Camera.get().getDisplayHeight(0.285f) + keyY > Camera.get().getDisplayHeight(0.8f)) {
				keyX += Camera.get().getDisplayWidth(0.25f);
				keyY = 0;
			}
		}
		
		Button close = new Button("Close", Float.NaN, Camera.get().getDisplayHeight(0.85f), 0, null);
		close.setAlign(Align.CENTER);
		close.setStill(true);
		close.addEvent(new ClickEvent(close) {
			public void click() {
				toClose = true;
			}
		});
		this.add(close);
		
		addFrame(image, 50, 30);
	}

}
