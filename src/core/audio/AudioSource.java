package core.audio;

import java.io.IOException;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.openal.SoundStore;
import org.newdawn.slick.util.ResourceLoader;

import core.Theater;
import core.utilities.MathFunctions;

enum AudioType {
	BGM,
	SFX;
}

public class AudioSource {

	private Audio audio;
	private AudioType type;
	private Vector3f position;
	private float fadeDistance;
	
	public AudioSource(String ref, String type) {
		this.type = AudioType.valueOf(type);
		
		try {
			if(this.type == AudioType.BGM) {
				audio = AudioLoader.getStreamingAudio("OGG",
							ResourceLoader.getResource(System.getProperty("resources") + "/music/" + ref + ".ogg"));
			} else {
				audio = AudioLoader.getAudio("OGG",
						ResourceLoader.getResourceAsStream(System.getProperty("resources") + "/soundeffects/" + ref + ".ogg"));
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void update() {
		if(position != null) {
			if(position.z != 0) {
				// Adjust fade value
				position.z = MathFunctions.clamp(position.z + (Theater.getDeltaSpeed(0.025f) * (fadeDistance > 0 ? -1f : 1f)),
						fadeDistance > 0 ? 0 : fadeDistance, fadeDistance < 0 ? 0 : fadeDistance);
				// Adjust volume
				if(type == AudioType.BGM) {
					SoundStore.get().setCurrentMusicVolume(MathFunctions.clamp(fadeDistance < 0 ?
							((fadeDistance - position.z) / fadeDistance) : 1 - ((fadeDistance - position.z) / fadeDistance),
							0, SoundStore.get().getMusicVolume()));
				}
				// Check if fading has ended
				if(position.z == 0) {
					fadeDistance = 0;
				}
			}
		}
	}
	
	public Audio getAudio() {
		return audio;
	}
	
	public Vector3f getPosition() {
		return position;
	}
	
	public void setPosition(Vector3f position) {
		this.position = position;
		if(position.z != 0) {
			fadeDistance = position.z;
			if(position.z < 0 && type == AudioType.BGM) {
				SoundStore.get().setCurrentMusicVolume(0);
			}
		}
	}
	
}
