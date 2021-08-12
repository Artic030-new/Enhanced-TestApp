package ic2.core.audio;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.IC2;
import ic2.core.audio.AudioManager;
import ic2.core.audio.AudioPosition;
import ic2.core.audio.AudioSource;
import ic2.core.audio.AudioSourceClient;
import ic2.core.audio.PositionSpec;
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
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;
import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;

@SideOnly(Side.CLIENT)
public final class AudioManagerClient extends AudioManager {

   public float fadingDistance = 16.0F;
   private boolean enabled = true;
   private int maxSourceCount = 32;
   private int streamingSourceCount = 4;
   private boolean lateInitDone = false;
   private SoundSystem soundSystem = null;
   private float masterVolume = 0.5F;
   private int ticker = 0;
   private int nextId = 0;
   private Map objectToAudioSourceMap = new HashMap();


   public AudioManagerClient() {
      super.defaultVolume = 1.2F;
   }

   public void initialize(Configuration config) {
      if(config != null) {
         Property prop = config.get("general", "soundsEnabled", this.enabled);
         prop.comment = "Enable sounds";
         this.enabled = Boolean.parseBoolean(prop.value);
         prop = config.get("general", "soundSourceLimit", this.maxSourceCount);
         prop.comment = "Maximum number of audio sources, only change if you know what you\'re doing";
         this.maxSourceCount = Integer.parseInt(prop.value);
         config.save();
      }

      if(this.maxSourceCount <= 6) {
         IC2.log.info("Audio source limit too low to enable IC2 sounds.");
         this.enabled = false;
      }

      if(!this.enabled) {
         IC2.log.info("Sounds disabled.");
      } else if(this.maxSourceCount < 6) {
         this.enabled = false;
      } else {
         IC2.log.info("Using " + this.maxSourceCount + " audio sources.");
         SoundSystemConfig.setNumberStreamingChannels(this.streamingSourceCount);
         SoundSystemConfig.setNumberNormalChannels(this.maxSourceCount - this.streamingSourceCount);
      }
   }

   public void onTick() {
      if(this.enabled && this.soundSystem != null) {
         IC2.platform.profilerStartSection("UpdateMasterVolume");
         float configSoundVolume = Minecraft.getMinecraft().gameSettings.soundVolume;
         if(configSoundVolume != this.masterVolume) {
            this.masterVolume = configSoundVolume;
         }

         IC2.platform.profilerEndStartSection("UpdateSourceVolume");
         EntityPlayer player = IC2.platform.getPlayerInstance();
         Vector audioSourceObjectsToRemove = new Vector();
         if(player == null) {
            audioSourceObjectsToRemove.addAll(this.objectToAudioSourceMap.keySet());
         } else {
            PriorityQueue i$ = new PriorityQueue();
            Iterator obj = this.objectToAudioSourceMap.entrySet().iterator();

            while(obj.hasNext()) {
               Entry entry = (Entry)obj.next();
               if(((AudioManagerClient.WeakObject)entry.getKey()).isEnqueued()) {
                  audioSourceObjectsToRemove.add(entry.getKey());
               } else {
                  Iterator i$1 = ((List)entry.getValue()).iterator();

                  while(i$1.hasNext()) {
                     AudioSource audioSource = (AudioSource)i$1.next();
                     audioSource.updateVolume(player);
                     if(audioSource.getRealVolume() > 0.0F) {
                        i$.add(audioSource);
                     }
                  }
               }
            }

            IC2.platform.profilerEndStartSection("Culling");

            for(int var10 = 0; !i$.isEmpty(); ++var10) {
               if(var10 < this.maxSourceCount) {
                  ((AudioSource)i$.poll()).activate();
               } else {
                  ((AudioSource)i$.poll()).cull();
               }
            }
         }

         Iterator var9 = audioSourceObjectsToRemove.iterator();

         while(var9.hasNext()) {
            AudioManagerClient.WeakObject var11 = (AudioManagerClient.WeakObject)var9.next();
            this.removeSources(var11);
         }

         IC2.platform.profilerEndSection();
      }
   }

   public AudioSource createSource(Object obj, String initialSoundFile) {
      return this.createSource(obj, PositionSpec.Center, initialSoundFile, false, false, super.defaultVolume);
   }

   public AudioSource createSource(Object obj, PositionSpec positionSpec, String initialSoundFile, boolean loop, boolean priorized, float volume) {
      if(!this.enabled) {
         return null;
      } else {
         if(this.soundSystem == null) {
            this.getSoundSystem();
         }

         if(this.soundSystem == null) {
            return null;
         } else {
            String sourceName = this.getSourceName(this.nextId);
            ++this.nextId;
            AudioSourceClient audioSource = new AudioSourceClient(this.soundSystem, sourceName, obj, positionSpec, initialSoundFile, loop, priorized, volume);
            AudioManagerClient.WeakObject key = new AudioManagerClient.WeakObject(obj);
            if(!this.objectToAudioSourceMap.containsKey(key)) {
               this.objectToAudioSourceMap.put(key, new LinkedList());
            }

            ((List)this.objectToAudioSourceMap.get(key)).add(audioSource);
            return audioSource;
         }
      }
   }

   public void removeSources(Object obj) {
      if(this.soundSystem != null) {
         AudioManagerClient.WeakObject key;
         if(obj instanceof AudioManagerClient.WeakObject) {
            key = (AudioManagerClient.WeakObject)obj;
         } else {
            key = new AudioManagerClient.WeakObject(obj);
         }

         if(this.objectToAudioSourceMap.containsKey(key)) {
            Iterator i$ = ((List)this.objectToAudioSourceMap.get(key)).iterator();

            while(i$.hasNext()) {
               AudioSource audioSource = (AudioSource)i$.next();
               audioSource.remove();
            }

            this.objectToAudioSourceMap.remove(key);
         }
      }
   }

   public void playOnce(Object obj, String soundFile) {
      this.playOnce(obj, PositionSpec.Center, soundFile, false, super.defaultVolume);
   }

   public void playOnce(Object obj, PositionSpec positionSpec, String soundFile, boolean priorized, float volume) {
      if(this.enabled) {
         if(this.soundSystem == null) {
            this.getSoundSystem();
         }

         if(this.soundSystem != null) {
            AudioPosition position = AudioPosition.getFrom(obj, positionSpec);
            if(position != null) {
               URL url = AudioSource.class.getClassLoader().getResource("ic2/sounds/" + soundFile);
               if(url == null) {
                  IC2.log.warning("Invalid sound file: " + soundFile);
               } else {
                  String sourceName = this.soundSystem.quickPlay(priorized, url, soundFile, false, position.x, position.y, position.z, 2, this.fadingDistance * Math.max(volume, 1.0F));
                  this.soundSystem.setVolume(sourceName, this.masterVolume * Math.min(volume, 1.0F));
               }
            }
         }
      }
   }

   public float getMasterVolume() {
      return this.masterVolume;
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

   private void getSoundSystem() {
      Field[] arr$ = SoundManager.class.getDeclaredFields();
      int len$ = arr$.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         Field field = arr$[i$];
         if(field.getType() == SoundSystem.class) {
            field.setAccessible(true);

            try {
               Object e = field.get((Object)null);
               if(e instanceof SoundSystem) {
                  this.soundSystem = (SoundSystem)e;
               }
            } catch (Exception var6) {
               var6.printStackTrace();
               this.soundSystem = null;
            }
            break;
         }
      }

   }

   private String getSourceName(int id) {
      return "asm_snd" + id;
   }

   public static class WeakObject extends WeakReference {

      public WeakObject(Object referent) {
         super(referent);
      }

      public boolean equals(Object object) {
         return object instanceof AudioManagerClient.WeakObject?((AudioManagerClient.WeakObject)object).get() == this.get():this.get() == object;
      }

      public int hashCode() {
         Object object = this.get();
         return object == null?0:object.hashCode();
      }
   }
}
