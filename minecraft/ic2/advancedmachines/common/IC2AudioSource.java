package ic2.advancedmachines.common;

import ic2.core.IC2;
import ic2.core.audio.AudioSource;
import net.minecraft.tileentity.TileEntity;

public class IC2AudioSource {
	
   private final AudioSource audioSourceinstance;
   
   public IC2AudioSource(TileEntity tEnt, String soundfile) {
	  this.audioSourceinstance = IC2.audioManager.createSource(tEnt, soundfile);
	  if (this.audioSourceinstance == null) {
		 System.out.println("Advanced Machines failed to create AudioSource");
      }

   }

   public static void removeSource(Object audioSource) {
	  IC2.audioManager.removeSources(audioSource);
   }

   public static void playOnce(TileEntity tEnt, String soundFile) {
	  IC2.audioManager.playOnce(tEnt, soundFile);
   }

   public void play() {
	  if (this.audioSourceinstance != null) {
	     this.audioSourceinstance.play();
      }

   }

   public void stop() {
	  if (this.audioSourceinstance != null) {
         this.audioSourceinstance.stop();
      }

   }

   public void remove() {
	  if (this.audioSourceinstance != null) {
	     this.audioSourceinstance.remove();
      }

   }
}
