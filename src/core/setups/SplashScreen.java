package core.setups;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import core.Camera;
import core.Theater;
import core.ui.Icon;
import core.utilities.keyboard.Keybinds;

public class SplashScreen extends GameSetup {

	/** Queued list of images to display */
	private Queue<Icon> splashImages = new LinkedList<Icon>();
	/** Time to display each image */
	private float timer = 5f;
	/** True if audio ding has played */
	private boolean dinged;
	
	/**
	 * Splash Screen
	 * Displays once game has been opened.
	 */
	public SplashScreen() {
		loadSplashes();
	}
	
	/**
	 * Read splash screen text file to load as many splash screens as detected.
	 */
	public void loadSplashes() {
		try (BufferedReader reader = new BufferedReader(new FileReader(System.getProperty("resources") + "/splash/splash text"))) {
			
			String line = null;
			while((line = reader.readLine()) != null) {
				// Load each screen
				Icon icon = new Icon(line);
				icon.setPosition(Float.NaN, Float.NaN);
				splashImages.add(icon);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Load a random assortment of splash screens
	 */
	public void loadRandomSplashes() {
		try {
			// Total number of screens to display
			int totalScreens = 1;
			// Used in display screens randomly
			// All of this is mostly unused
			List<Integer> usedInts = new LinkedList<Integer>();
			// Text file containing names of images for splash screens
			BufferedReader reader = new BufferedReader(new FileReader(System.getProperty("resources") + "/splash/splash text"));
			
			// Loads each screen
			String line = null;
			for(int x = 0; x<totalScreens; x++) {
				int tempNumber = 1;
				if(!usedInts.contains(tempNumber)) {
					usedInts.add(tempNumber);
					while(tempNumber > 0) {
						line = reader.readLine();
						tempNumber--;
					}
					
					Icon icon = new Icon(line);
					icon.setPosition(Float.NaN, Float.NaN);
					splashImages.add(icon);
					
					reader.close();
					reader = new BufferedReader(new FileReader(System.getProperty("resources") + "/splash/splash text"));
				} else {
					tempNumber = 1;
					x--;
				}
					
			}
			
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	/**
	 * Check if splash screen should be skipped.
	 * Play ding effect.
	 * Fade image in and out.
	 */
	public void update() {
		// If player wishes to skip screen
		if(Keybinds.CONFIRM.clicked()) {
			// Remove current screen
			splashImages.poll();
			// Stop any sound effects playing
			//if(Ensemble.get().getSoundEffect("Just Like Make Game") != null)
				//Ensemble.get().getSoundEffect("Just Like Make Game").getClip().stop();
			// Restart screen if a new one exists
			if(!splashImages.isEmpty()) {
				timer = 5f;
				Camera.get().setFade(-1f);
			// Proceed with setup swap
			} else {
				Theater.get().swapSetup(new TitleMenu());
				//Theater.get().swapSetup(new Stage());
			}
		}
		
		// Adjust fading and reduce timer
		if(timer > 0f) {
			timer -= Theater.getDeltaSpeed(0.025f);
			
			// If a sound effect is to be played
			if(timer < 4f && !dinged) {
				//Ensemble.get().playSoundEffect(new SoundEffect("Just Like Make Game", 0.75f, false));
				dinged = true;
			}

			// Start fading out
			if(timer <= 1f && !Camera.get().isFading()) {
				Camera.get().setFade(1f);
			}
		} else {
			// Remove current screen
			splashImages.poll();
			// Restart screen if a new one exists
			if(!splashImages.isEmpty()) {
				timer = 5f;
				Camera.get().setFade(-1f);
			// Proceed with setup swap
			} else {
				Theater.get().swapSetup(new TitleMenu());
				//Theater.get().swapSetup(new Stage());
			}
		}
	}
	
	/**
	 * Draw current splash screen in center of screen.
	 */
	@Override
	public void draw() {
		
	}

	@Override
	public void drawUI() {
		if(splashImages.peek() != null) {
			splashImages.peek().draw();
		}
	}
	
	@Override
	public void resizeRefresh() {
	}
	
}
