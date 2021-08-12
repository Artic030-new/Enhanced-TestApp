package gravisuite.audio;

import gravisuite.GraviSuite;
import gravisuite.audio.AudioPosition;
import gravisuite.audio.AudioSource;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Vector;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.src.ModLoader;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;
import paulscode.sound.SoundSystem;

public class AudioManager {

   public static Minecraft mc = ModLoader.getMinecraftInstance();
   public static float defaultVolume = mc.gameSettings.soundVolume;
   public static float fadingDistance = 16.0F;
   public static SoundSystem soundSystem = null;
   public static float masterVolume = 0.5F;
   public static boolean enabled = true;
   private static int maxSourceCount = 32;
   private int ticker = 0;
   private static int nextId = 0;
   private static Map objectToAudioSourceMap = new HashMap();

   public AudioManager() {
      defaultVolume = 1.2F;
   }

   public static void Initialize() {
      enabled = !GraviSuite.disableSounds;
   }

   public static void onTick() {
      if(enabled && soundSystem != null) {
         float var1 = Minecraft.getMinecraft().gameSettings.soundVolume;
         if(var1 != masterVolume) {
            masterVolume = var1;
         }

         EntityPlayer var2 = GraviSuite.proxy.getPlayerInstance();
         Vector var3 = new Vector();
         if(var2 == null) {
            var3.addAll(objectToAudioSourceMap.keySet());
         } else {
            PriorityQueue var9 = new PriorityQueue();
            Iterator var11 = objectToAudioSourceMap.entrySet().iterator();

            while(var11.hasNext()) {
               Entry var10 = (Entry)var11.next();
               if(((AudioManager.WeakObject)var10.getKey()).isEnqueued()) {
                  var3.add(var10.getKey());
               } else {
                  Iterator var7 = ((List)var10.getValue()).iterator();

                  while(var7.hasNext()) {
                     AudioSource var8 = (AudioSource)var7.next();
                     var8.updateVolume(var2);
                     if(var8.getRealVolume() > 0.0F) {
                        var9.add(var8);
                     }
                  }
               }
            }

            for(int var101 = 0; !var9.isEmpty(); ++var101) {
               if(var101 < maxSourceCount) {
                  ((AudioSource)var9.poll()).activate();
               } else {
                  ((AudioSource)var9.poll()).cull();
               }
            }
         }

         Iterator var81 = var3.iterator();

         while(var81.hasNext()) {
            AudioManager.WeakObject var91 = (AudioManager.WeakObject)var81.next();
            removeSources(var91);
         }
      }

   }

   public void playOnce(Object obj, String soundFile) {
      playOnce(obj, AudioManager.PositionSpec.Center, soundFile, false, defaultVolume);
   }

   public static void playOnce(Object obj, AudioManager.PositionSpec positionSpec, String soundFile, boolean priorized, float volume) {
      if(enabled) {
         if(soundSystem == null) {
            getSoundSystem();
         }

         if(soundSystem != null) {
            AudioPosition position = AudioPosition.getFrom(obj, positionSpec);
            if(position != null) {
               URL url = AudioSource.class.getClassLoader().getResource("gravisuite/audio/sounds/" + soundFile);
               if(url == null) {
                  System.out.println("[GraviSuite]: Invalid sound file: " + soundFile);
               } else {
                  String sourceName = soundSystem.quickPlay(priorized, url, soundFile, false, position.x, position.y, position.z, 2, fadingDistance * Math.max(volume, 1.0F));
                  soundSystem.setVolume(sourceName, masterVolume * Math.min(volume, 1.0F));
               }
            }
         }
      }
   }

   private static void getSoundSystem() {
      Field[] var1 = SoundManager.class.getDeclaredFields();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         Field var4 = var1[var3];
         if(var4.getType() == SoundSystem.class) {
            var4.setAccessible(true);

            try {
               Object var6 = var4.get((Object)null);
               if(var6 instanceof SoundSystem) {
                  soundSystem = (SoundSystem)var6;
               }
            } catch (Exception var5) {
               var5.printStackTrace();
               soundSystem = null;
            }
            break;
         }
      }

   }

   public AudioSource createSource(Object var1, String var2) {
      return createSource(var1, AudioManager.PositionSpec.Center, var2, false, false, defaultVolume);
   }

   public static AudioSource createSource(Object var1, AudioManager.PositionSpec var2, String var3, boolean var4, boolean var5, float var6) {
      if(!enabled) {
         return null;
      } else {
         if(soundSystem == null) {
            getSoundSystem();
         }

         if(soundSystem == null) {
            return null;
         } else {
            String var7 = getSourceName(nextId);
            ++nextId;
            AudioSource var8 = new AudioSource(soundSystem, var7, var1, var2, var3, var4, var5, var6);
            AudioManager.WeakObject var9 = new AudioManager.WeakObject(var1);
            if(!objectToAudioSourceMap.containsKey(var9)) {
               objectToAudioSourceMap.put(var9, new LinkedList());
            }

            ((List)objectToAudioSourceMap.get(var9)).add(var8);
            return var8;
         }
      }
   }

   public static void removeSources(Object var1) {
      if(soundSystem != null) {
         AudioManager.WeakObject var2;
         if(var1 instanceof AudioManager.WeakObject) {
            var2 = (AudioManager.WeakObject)var1;
         } else {
            var2 = new AudioManager.WeakObject(var1);
         }

         if(objectToAudioSourceMap.containsKey(var2)) {
            Iterator var3 = ((List)objectToAudioSourceMap.get(var2)).iterator();

            while(var3.hasNext()) {
               AudioSource var4 = (AudioSource)var3.next();
               var4.remove();
            }

            objectToAudioSourceMap.remove(var2);
         }
      }

   }

   private boolean testSourceCount(int n) {
      IntBuffer sourceBuffer = BufferUtils.createIntBuffer(n);

      try {
         AL10.alGenSources(sourceBuffer);
         if(AL10.alGetError() == 0) {
            AL10.alDeleteSources(sourceBuffer);
            return true;
         }
      } catch (Exception var4) {
         AL10.alGetError();
      } catch (UnsatisfiedLinkError var5) {
         ;
      }

      return false;
   }

   private static String getSourceName(int id) {
      return "asm_snd" + id;
   }


   public static enum PositionSpec {

      Center("Center", 0),
      Backpack("Backpack", 1),
      Hand("Hand", 2);
      // $FF: synthetic field
      private static final AudioManager.PositionSpec[] $VALUES = new AudioManager.PositionSpec[]{Center, Backpack, Hand};


      private PositionSpec(String var1, int var2) {}

   }

   public static class WeakObject extends WeakReference {

      public WeakObject(Object var1) {
         super(var1);
      }

      public boolean equals(Object var1) {
         return var1 instanceof AudioManager.WeakObject?((AudioManager.WeakObject)var1).get() == this.get():this.get() == var1;
      }

      public int hashCode() {
         Object var1 = this.get();
         return var1 == null?0:var1.hashCode();
      }
   }
}
