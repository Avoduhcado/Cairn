package core.audio;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.newdawn.easyogg.OggClip;

public class SoundEffect {

	/** .OGG audio clip */
	private OggClip clip;
	/** Name of audio clip. Used to identify in Ensemble */
	private String name;
	/** Current volume of clip */
	private float volume;
	/** If clip should loop */
	private boolean loop;
	
	/**
	 * Sound Effects typically meant to be played once and then removed.
	 * 
	 * @param ref File name of audio clip to load
	 * @param volume Volume clip should be played at
	 * @param loop True if the clip should loop
	 */
	public SoundEffect(String ref, float volume, boolean loop) {
		this.name = ref;
		
		try {
			// Load clip
			clip = new OggClip(new FileInputStream(System.getProperty("resources") + "/soundeffects/" + ref + ".ogg"));
			this.volume = volume;
			this.loop = loop;
		} catch (FileNotFoundException e) {
			System.err.println("Audio effect: " + ref + " was not found!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Play this sound effect.
	 */
	public void play() {
		// If current volume is greater than master volume
		// Play at master volume instead
		if(Ensemble.get().getMasterVolume() < volume)
			clip.setGain(Ensemble.get().getMasterVolume());
		else
			clip.setGain(volume);
		
		if(loop)
			clip.loop();
		else
			clip.play();
	}
	
	/**
	 * 
	 * @return Name of sound effect
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Volume can be set between 0.0f and 2.0f.
	 * 
	 * @param volume of sound effect
	 */
	public void setVolume(float volume) {
		this.volume = volume;
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
	
	/**
	 * 
	 * @param loop true if sound effect should loop
	 */
	public void setLoop(boolean loop) {
		this.loop = loop;
		if(loop)
			clip.loop();
	}
	
	/**
	 * 
	 * @return True if sound effect is looping
	 */
	public boolean isLoop() {
		return loop;
	}
	
	/**
	 * 
	 * @return Sound Effect's audio clip
	 */
	public OggClip getClip() {
		return clip;
	}
	
}
