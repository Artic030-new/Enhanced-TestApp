package ic2.core.audio;

import ic2.core.audio.AudioSource;
import ic2.core.audio.PositionSpec;
import net.minecraftforge.common.Configuration;

public class AudioManager {

   public float defaultVolume;


   public void initialize(Configuration config) {}

   public void playOnce(Object obj, String soundFile) {}

   public void playOnce(Object obj, PositionSpec positionSpec, String soundFile, boolean priorized, float volume) {}

   public void removeSources(Object obj) {}

   public AudioSource createSource(Object obj, String initialSoundFile) {
      return null;
   }

   public AudioSource createSource(Object obj, PositionSpec positionSpec, String initialSoundFile, boolean loop, boolean priorized, float volume) {
      return null;
   }

   public void onTick() {}

   public float getMasterVolume() {
      return 0.0F;
   }
}
