package core.audio;

import java.util.ArrayList;

@Deprecated
public class Ensemble {

	/** Array of all loaded sound effects */
	private ArrayList<SoundEffect> soundEffects = new ArrayList<SoundEffect>();
	/** Array of all loaded tracks */
	private ArrayList<Track> tracks = new ArrayList<Track>();
	/** Background track */
	private Track background;
	/** Master volume, can be set from 0.0f - 2.0f */
	private float masterVolume = 1.0f;
	/** Boolean to notify ensemble to adjust volume settings */
	private boolean adjustVolume;

	/** Ensemble singleton */
	private static Ensemble ensemble;

	/**
	 * Initialize singleton.
	 */
	public static void init() {
		ensemble = new Ensemble();
	}

	/**
	 * 
	 * @return Singleton
	 */
	public static Ensemble get() {
		return ensemble;
	}

	/**
	 * Create a new Ensemble.
	 */
	private Ensemble() {
		// Fail safe method, potentially unneeded
		setMasterVolume(1f);
	}

	/**
	 * Update all music and sound effects.
	 */
	public void update() {
		// If master volume has been changed, update all volumes
		if(adjustVolume) {
			// Sound effects
			for(SoundEffect s : soundEffects)
				s.adjustVolume(masterVolume);
			
			// Tracks
			for(Track t : tracks)
				t.adjustVolume(masterVolume);
			if(background != null)
				background.adjustVolume(masterVolume);

			// Reset volume adjustment variable
			adjustVolume = false;
		}

		// Handling sound effect clean up
		for(int x = 0; x<soundEffects.size(); x++) {
			// Remove sound effect if it has ended
			if(soundEffects.get(x).getClip().stopped()) {
				soundEffects.get(x).getClip().close();
				soundEffects.remove(x);
				x--;
			}
		}

		// Handling track clean up
		for(int x = 0; x<tracks.size(); x++) {
			tracks.get(x).update();
			// Remove track if it has ended
			if(tracks.get(x).getClip().stopped()) {
				tracks.get(x).getClip().close();
				tracks.remove(x);
				x--;
			}
		}

		if(background != null) {
			background.update();
		}
	}

	/**
	 * Pause all current tracks and sound effects.
	 */
	public void pause() {
		for(SoundEffect s : soundEffects)
			s.getClip().pause();
		for(Track t : tracks)
			t.getClip().pause();
		background.getClip().pause();
	}

	/**
	 * Resume playing of all current tracks and sound effects.
	 */
	public void resume() {
		for(SoundEffect s : soundEffects)
			s.getClip().resume();
		for(Track t : tracks)
			t.getClip().resume();
		background.getClip().resume();
	}

	/**
	 * Mute or unmute the master volume.
	 */
	public void mute() {
		// Unmute
		if(masterVolume == 0f)
			masterVolume = 1f;
		// Mute
		else
			masterVolume = 0f;

		// Notify ensemble to update volumes
		adjustVolume = true;
	}

	/**
	 * Close all current tracks and sound effects.
	 */
	public void close() {
		if(background != null) {
			background.getClip().stop();
			background.getClip().close();
		}
		for(SoundEffect s : soundEffects) {
			s.getClip().stop();
			s.getClip().close();
		}
		for(Track t : tracks) {
			t.getClip().stop();
			t.getClip().close();
		}
	}

	/**
	 * 
	 * @return background track
	 */
	public Track getBackground() {
		return background;
	}

	/**
	 * Set a new background track. If it fails to load, track will be set to null.
	 * 
	 * @param track new background track
	 */
	public void setBackground(Track track) {
		this.background = track;
		if(this.background != null && this.background.getClip() == null)
			this.background = null;
	}

	/**
	 * Swap background tracks. If the track to swap to is the same as previous, nothing happens.
	 * If it's a new track, this stops the previous track and plays the new one.
	 * 
	 * @param track new background track
	 * @param timeOut value in seconds for old track to fade out
	 * @param timeIn value in seconds for new track to fade in
	 */
	public void swapBackground(Track track, float timeOut, float timeIn) {
		if(background != null) {
			background.setFade(-timeOut);
			tracks.add(background);
			/*this.background.getClip().pause();
			this.background.getClip().stop();
			this.background.getClip().close();*/
		}
		track.setFade(timeIn);
		this.background = track;
		if(this.background != null)
			this.background.play();
	}

	/**
	 * Get a currently loaded track by name.
	 * 
	 * @param name of current track
	 * @return Track of same name, if one exists, else returns null
	 */
	public Track getTrack(String name) {
		for(Track t : tracks) {
			if(t.getName().matches(name))
				return t;
		}

		return null;
	}

	/**
	 * Add a new track.
	 * 
	 * @param track to be added
	 */
	public void addTrack(Track track) {
		tracks.add(track);
	}

	/**
	 * Play a track by name.
	 * 
	 * @param track to be played
	 */
	public void playTrack(String name) {
		if(getTrack(name) != null)
			getTrack(name).play();
	}

	/**
	 * Remove a currently loaded track by name.
	 * 
	 * @param name of track to remove
	 */
	public void removeTrack(String name) {
		getTrack(name).pause();
		getTrack(name).stop();
		getTrack(name).getClip().close();
		tracks.remove(getTrack(name));
	}

	/**
	 * Get a currently loaded sound effect by name.
	 * 
	 * @param name of sound effect
	 * @return sound effect of same name, if one exists, else returns null
	 */
	public SoundEffect getSoundEffect(String name) {
		for(SoundEffect s : soundEffects) {
			if(s.getName().matches(name))
				return s;
		}

		return null;
	}

	/**
	 * Add and play a new sound effect.
	 * 
	 * @param soundEffect to be played
	 */
	public void playSoundEffect(SoundEffect soundEffect) {
		soundEffects.add(soundEffect);
		if(soundEffects.get(soundEffects.size() - 1).getClip() != null)
			soundEffects.get(soundEffects.size() - 1).play();
		else
			soundEffects.remove(soundEffects.size() - 1);
	}
	
	public void playSoundEffect(String ref) {
		soundEffects.add(new SoundEffect(ref, masterVolume, false));
		if(soundEffects.get(soundEffects.size() - 1).getClip() != null)
			soundEffects.get(soundEffects.size() - 1).play();
		else
			soundEffects.remove(soundEffects.size() - 1);
	}

	/**
	 * Remove a currently loaded sound effect by name.
	 * 
	 * @param name of sound effect to be removed
	 */
	public void removeSoundEffect(String name) {
		getSoundEffect(name).getClip().pause();
		getSoundEffect(name).getClip().stop();
		getSoundEffect(name).getClip().close();
		soundEffects.remove(getSoundEffect(name));
	}

	/**
	 * 
	 * @return Master volume
	 */
	public float getMasterVolume() {
		return masterVolume;
	}

	/**
	 * Volume can be set between 0.0f and 2.0f.
	 * 
	 * @param masterVolume new master volume level
	 */
	public void setMasterVolume(float masterVolume) {
		this.masterVolume = masterVolume;
		adjustVolume = true;
	}

}
