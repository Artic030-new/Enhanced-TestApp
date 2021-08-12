package gravisuite.audio;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gravisuite.audio.AudioManager;
import gravisuite.audio.AudioPosition;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.net.URL;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import paulscode.sound.SoundSystem;

@SideOnly(Side.CLIENT)
public final class AudioSource implements Comparable {

   private SoundSystem soundSystem;
   private String sourceName;
   private boolean valid = false;
   private boolean culled = false;
   private Reference obj;
   private AudioPosition position;
   private AudioManager.PositionSpec positionSpec;
   private float configuredVolume;
   private float realVolume;
   private boolean isPlaying = false;

   public AudioSource(SoundSystem var1, String var2, Object var3, AudioManager.PositionSpec var4, String var5, boolean var6, boolean var7, float var8) {
      this.soundSystem = var1;
      this.sourceName = var2;
      this.obj = new WeakReference(var3);
      this.positionSpec = var4;
      URL var9 = AudioSource.class.getClassLoader().getResource("gravisuite/audio/sounds/" + var5);
      if(var9 == null) {
         System.out.println("Invalid sound file: " + var5);
      } else {
         this.position = AudioPosition.getFrom(var3, var4);
         var1.newSource(var7, var2, var9, var5, var6, this.position.x, this.position.y, this.position.z, 0, AudioManager.fadingDistance * Math.max(var8, 1.0F));
         this.valid = true;
         this.setVolume(var8);
      }

   }

   public int compareTo(AudioSource var1) {
      return this.culled?(int)((this.realVolume * 0.9F - var1.realVolume) * 128.0F):(int)((this.realVolume - var1.realVolume) * 128.0F);
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
      if(this.valid && !this.isPlaying) {
         this.isPlaying = true;
         this.soundSystem.play(this.sourceName);
      }

   }

   public void pause() {
      if(this.valid && this.isPlaying) {
         this.isPlaying = false;
         this.soundSystem.pause(this.sourceName);
      }

   }

   public void stop() {
      if(this.valid && this.isPlaying) {
         this.isPlaying = false;
         this.soundSystem.stop(this.sourceName);
      }

   }

   public void flush() {
      if(this.valid && this.isPlaying) {
         this.soundSystem.flush(this.sourceName);
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

   public void setVolume(float var1) {
      this.configuredVolume = var1;
      this.soundSystem.setVolume(this.sourceName, 0.001F);
   }

   public void setPitch(float var1) {
      if(this.valid) {
         this.soundSystem.setPitch(this.sourceName, var1);
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

   public void updateVolume(EntityPlayer var1) {
      if(this.valid && this.isPlaying) {
         float var2 = AudioManager.fadingDistance * Math.max(this.configuredVolume, 1.0F);
         float var3 = 1.0F;
         float var4 = 1.0F;
         float var5 = (float)var1.posX;
         float var6 = (float)var1.posY;
         float var7 = (float)var1.posZ;
         float var8;
         float var9;
         float var10;
         float var11;
         if(this.position.world == var1.worldObj) {
            var9 = this.position.x - var5;
            var10 = this.position.y - var6;
            var11 = this.position.z - var7;
            var8 = (float)Math.sqrt((double)(var9 * var9 + var10 * var10 + var11 * var11));
         } else {
            var8 = Float.POSITIVE_INFINITY;
         }

         if(var8 > var2) {
            this.realVolume = 0.0F;
            this.cull();
         } else {
            if(var8 < var4) {
               var8 = var4;
            }

            var9 = 1.0F - var3 * (var8 - var4) / (var2 - var4);
            var10 = var9 * this.configuredVolume * AudioManager.masterVolume;
            var11 = (this.position.x - var5) / var8;
            float var12 = (this.position.y - var6) / var8;
            float var13 = (this.position.z - var7) / var8;
            if((double)var10 > 0.1D) {
               for(int var14 = 0; (float)var14 < var8; ++var14) {
                  int var15 = var1.worldObj.getBlockId((int)var5, (int)var6, (int)var7);
                  if(var15 != 0) {
                     if(Block.opaqueCubeLookup[var15]) {
                        var10 *= 0.6F;
                     } else {
                        var10 *= 0.8F;
                     }
                  }

                  var5 += var11;
                  var6 += var12;
                  var7 += var13;
               }
            }

            if((double)Math.abs(this.realVolume / var10 - 1.0F) > 0.06D) {
               this.soundSystem.setVolume(this.sourceName, AudioManager.masterVolume * Math.min(var10, 1.0F));
            }

            this.realVolume = var10;
         }
      } else {
         this.realVolume = 0.0F;
      }

   }

   public int compareTo(Object var1) {
      return this.compareTo((AudioSource)var1);
   }
}
