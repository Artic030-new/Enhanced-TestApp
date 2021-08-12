package gravisuite.audio;

import gravisuite.audio.SoundsList;
import java.io.File;
import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.event.ForgeSubscribe;

public class SoundHandler {

   @ForgeSubscribe
   public void SoundLoadEvent(SoundLoadEvent event) {
      String[] arr$ = SoundsList.soundFiles;
      int len$ = arr$.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         String soundFile = arr$[i$];

         try {
            event.manager.addSound(soundFile, new File(this.getClass().getResource("/" + soundFile).toURI()));
         } catch (Exception var7) {
            System.out.println("Gravisuite: Can\'t load sound file: " + soundFile);
         }
      }

      System.out.println("Gravisuite: Sounds loaded");
   }
}
