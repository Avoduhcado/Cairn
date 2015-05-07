package core.audio;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.newdawn.easyogg.OggClip;

import core.Theater;
import core.utilities.MathFunctions;

public class Track {

	/** .OGG audio clip */
	private OggClip clip;
	/** Name of audio clip. Used to identify in Ensemble */
	private String name;
	/** Current volume of clip */
	private float volume = 1f;
	/** If clip should loop */
	private boolean loop;
	/** Timer for fading in/out */
	private float fading;
	private float fadeValue;
	
	/**
	 * Track to be used for ambiance or background effects.
	 * 
	 * @param ref File name of audio clip to load
	 */
	public Track(String ref) {
		try {
			// Load clip
			clip = new OggClip(new FileInputStream(System.getProperty("resources") + "/music/" + ref + ".ogg"));
			name = ref;
			volume = getRealVolume();
			loop = true;
		} catch (FileNotFoundException e) {
			System.err.println("Audio track: " + ref + " was not found!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Track to be used for ambiance or background effects.
	 * Support for fading in, adjustable volume, and looping.
	 * 
	 * @param ref File name of audio clip to load
	 * @param volume Volume of audio clip
	 * @param fadeIn Time in seconds for track to fade in to full volume
	 * @param loop True if clip should loop
	 */
	public Track(String ref, float volume, float fadeIn, boolean loop) {
		try {
			// Load clip
			clip = new OggClip(new FileInputStream(System.getProperty("resources") + "/music/" + ref + ".ogg"));
			name = ref;
			this.volume = volume;
			// Set fade in timer
			if(fadeIn != 0) {
				// If fading in, it will use master volume to stop
				this.volume = 0f;
				fading = fadeIn;
			}
			this.loop = loop;
		} catch (FileNotFoundException e) {
			System.err.println("Audio track: " + ref + " was not found!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Update any fading settings
	 */
	public void update() {
		// If track should be fading and Ensemble isn't muted
		if(fading != 0) {
			fade();
		}
	}
	
	/**
	 * Play this track.
	 */
	public void play() {
		// If current volume is greater than master volume
		// Play at master volume instead
		clip.setGain(getRealVolume());
		
		if(loop)
			clip.loop();
		else
			clip.play();
	}
	
	/**
	 * Pause this track.
	 */
	public void pause() {
		clip.pause();
	}
	
	/**
	 * Resume this track.
	 */
	public void resume() {
		if(clip.isPaused())
			clip.resume();
	}
	
	/**
	 * Stop this tracks.
	 */
	public void stop() {
		clip.stop();
	}
	
	/**
	 * Called if master volume was changed.
	 * 
	 * @param masterVolume of Ensemble
	 */
	public void adjustVolume(float masterVolume) {
		// If current volume is greater than master volume
		// Use master volume instead
		if(masterVolume < volume) {
			clip.setGain(masterVolume);
		} else
			clip.setGain(volume);
	}
	
	public float getVolume() {
		return volume;
	}
	
	/**
	 * @return in game audio level of this track in relation to Master Volume
	 */
	public float getRealVolume() {
		if(Ensemble.get().getMasterVolume() < volume) {
			return Ensemble.get().getMasterVolume();
		}
		return volume;
	}
	
	public void fade() {
		if(Ensemble.get().getMasterVolume() > 0) {
			// Adjust fade value
			fadeValue += Theater.getDeltaSpeed(0.025f);
			// Adjust volume
			clip.setGain(MathFunctions.clamp(fading > 0 ? (fadeValue / fading) : 1 + (fadeValue / fading), 0, getRealVolume()));
			// Check if fading has ended
			if(fadeValue >= Math.abs(fading)) {
				clip.setGain(fading > 0 ? getRealVolume() : 0f);
				/*if(clip.getGain() == 0) {
					pause();
					//stop();
				}*/
				fading = 0;
				fadeValue = 0;
			}
		} else {
			fading = 0;
			fadeValue = 0;
		}
	}
	
	/**
	 * Set a track's time in seconds to fade in or out based on 
	 * whether a positive or negative value is passed in.
	 * @param fade time in seconds for track to fade in or out
	 */
	public void setFade(float fade) {
		fading = fade;
	}
	
	/**
	 * 
	 * @return Track's audio clip
	 */
	public OggClip getClip() {
		return clip;
	}
	
	/**
	 * 
	 * @return Track name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * 
	 * @return True if track is looping
	 */
	public boolean isLoop() {
		return loop;
	}
	
	/**
	 * 
	 * @param loop true to make track loop
	 */
	public void setLoop(boolean loop) {
		this.loop = loop;
	}
	
}
