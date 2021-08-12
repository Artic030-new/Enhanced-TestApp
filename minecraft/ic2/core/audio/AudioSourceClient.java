package ic2.core.audio;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.IC2;
import ic2.core.audio.AudioManagerClient;
import ic2.core.audio.AudioPosition;
import ic2.core.audio.AudioSource;
import ic2.core.audio.PositionSpec;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.net.URL;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import paulscode.sound.SoundSystem;

@SideOnly(Side.CLIENT)
public final class AudioSourceClient extends AudioSource implements Comparable {

   private SoundSystem soundSystem;
   private String sourceName;
   private boolean valid = false;
   private boolean culled = false;
   private Reference obj;
   private AudioPosition position;
   private PositionSpec positionSpec;
   private float configuredVolume;
   private float realVolume;
   private boolean isPlaying = false;


   public AudioSourceClient(SoundSystem soundSystem, String sourceName, Object obj, PositionSpec positionSpec, String initialSoundFile, boolean loop, boolean priorized, float volume) {
      this.soundSystem = soundSystem;
      this.sourceName = sourceName;
      this.obj = new WeakReference(obj);
      this.positionSpec = positionSpec;
      URL url = AudioSource.class.getClassLoader().getResource("ic2/sounds/" + initialSoundFile);
      if(url == null) {
         System.out.println("Invalid sound file: " + initialSoundFile);
      } else {
         this.position = AudioPosition.getFrom(obj, positionSpec);
         soundSystem.newSource(priorized, sourceName, url, initialSoundFile, loop, this.position.x, this.position.y, this.position.z, 0, ((AudioManagerClient)IC2.audioManager).fadingDistance * Math.max(volume, 1.0F));
         this.valid = true;
         this.setVolume(volume);
      }
   }

   public int compareTo(AudioSourceClient x) {
      return this.culled?(int)((this.realVolume * 0.9F - x.realVolume) * 128.0F):(int)((this.realVolume - x.realVolume) * 128.0F);
   }

   public void remove() {
      if(this.valid) {
         this.stop();
         this.soundSystem.removeSource(this.sourceName);
         this.sourceName = null;
         this.valid = false;
      }
   }

   public void play() {
      if(this.valid) {
         if(!this.isPlaying) {
            this.isPlaying = true;
            this.soundSystem.play(this.sourceName);
         }
      }
   }

   public void pause() {
      if(this.valid) {
         if(this.isPlaying) {
            this.isPlaying = false;
            this.soundSystem.pause(this.sourceName);
         }
      }
   }

   public void stop() {
      if(this.valid) {
         if(this.isPlaying) {
            this.isPlaying = false;
            this.soundSystem.stop(this.sourceName);
         }
      }
   }

   public void flush() {
      if(this.valid) {
         if(this.isPlaying) {
            this.soundSystem.flush(this.sourceName);
         }
      }
   }

   public void cull() {
      if(this.valid && !this.culled) {
         this.soundSystem.cull(this.sourceName);
         this.culled = true;
      }
   }

   public void activate() {
      if(this.valid && this.culled) {
         this.soundSystem.activate(this.sourceName);
         this.culled = false;
      }
   }

   public float getVolume() {
      return !this.valid?0.0F:this.soundSystem.getVolume(this.sourceName);
   }

   public float getRealVolume() {
      return this.realVolume;
   }

   public void setVolume(float volume) {
      this.configuredVolume = volume;
      this.soundSystem.setVolume(this.sourceName, 0.001F);
   }

   public void setPitch(float pitch) {
      if(this.valid) {
         this.soundSystem.setPitch(this.sourceName, pitch);
      }
   }

   public void updatePosition() {
      if(this.valid) {
         this.position = AudioPosition.getFrom(this.obj.get(), this.positionSpec);
         if(this.position != null) {
            this.soundSystem.setPosition(this.sourceName, this.position.x, this.position.y, this.position.z);
         }
      }
   }

   public void updateVolume(EntityPlayer player) {
      if(this.valid && this.isPlaying) {
         float maxDistance = ((AudioManagerClient)IC2.audioManager).fadingDistance * Math.max(this.configuredVolume, 1.0F);
         float rolloffFactor = 1.0F;
         float referenceDistance = 1.0F;
         float x = (float)player.posX;
         float y = (float)player.posY;
         float z = (float)player.posZ;
         float distance;
         float gain;
         float newRealVolume;
         float dx;
         if(this.position.world == player.worldObj) {
            gain = this.position.x - x;
            newRealVolume = this.position.y - y;
            dx = this.position.z - z;
            distance = (float)Math.sqrt((double)(gain * gain + newRealVolume * newRealVolume + dx * dx));
         } else {
            distance = Float.POSITIVE_INFINITY;
         }

         if(distance > maxDistance) {
            this.realVolume = 0.0F;
            this.cull();
         } else {
            if(distance < referenceDistance) {
               distance = referenceDistance;
            }

            gain = 1.0F - rolloffFactor * (distance - referenceDistance) / (maxDistance - referenceDistance);
            newRealVolume = gain * this.configuredVolume * IC2.audioManager.getMasterVolume();
            dx = (this.position.x - x) / distance;
            float dy = (this.position.y - y) / distance;
            float dz = (this.position.z - z) / distance;
            if((double)newRealVolume > 0.1D) {
               for(int i = 0; (float)i < distance; ++i) {
                  int blockId = player.worldObj.getBlockId((int)x, (int)y, (int)z);
                  if(blockId != 0) {
                     if(Block.opaqueCubeLookup[blockId]) {
                        newRealVolume *= 0.6F;
                     } else {
                        newRealVolume *= 0.8F;
                     }
                  }

                  x += dx;
                  y += dy;
                  z += dz;
               }
            }

            if((double)Math.abs(this.realVolume / newRealVolume - 1.0F) > 0.06D) {
               this.soundSystem.setVolume(this.sourceName, IC2.audioManager.getMasterVolume() * Math.min(newRealVolume, 1.0F));
            }

            this.realVolume = newRealVolume;
         }
      } else {
         this.realVolume = 0.0F;
      }
   }

@Override
public int compareTo(Object arg0) {
	// TODO Auto-generated method stub
	return 0;
}
}
