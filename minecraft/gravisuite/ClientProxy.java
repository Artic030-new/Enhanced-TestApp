package gravisuite;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gravisuite.ClientTickHandler;
import gravisuite.GraviChestPlateClientProxy;
import gravisuite.ServerProxy;
import gravisuite.itemGraviToolRenderer;
import gravisuite.audio.AudioManager;
import gravisuite.audio.SoundsList;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.src.ModLoader;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.WorldEvent.Load;

@SideOnly(Side.CLIENT)
public class ClientProxy extends ServerProxy {

   public static Minecraft mc = ModLoader.getMinecraftInstance();

   public void initCore() {
      MinecraftForgeClient.preloadTexture("/gravisuite/graviChestPlate.png");
      MinecraftForgeClient.preloadTexture("/gravisuite/ultimate_lappack.png");
      MinecraftForgeClient.preloadTexture("/gravisuite/advanced_lappack.png");
      MinecraftForgeClient.preloadTexture("/gravisuite/ultimateSolarHelmet.png");
      MinecraftForgeClient.preloadTexture("/gravisuite/gravi_items.png");
      MinecraftForgeClient.preloadTexture("/gravisuite/advanced_jetpack.png");
      MinecraftForgeClient.preloadTexture("/gravisuite/advNanoChestPlate.png");
      TickRegistry.registerTickHandler(new ClientTickHandler(), Side.CLIENT);
      MinecraftForge.EVENT_BUS.register(this);
      registerKeys();
      AudioManager.Initialize();
   }

   public static void registerKeys() {}

   public static boolean sendMyPacket(String typePacket, int first_int) {
      ByteArrayOutputStream bytes = new ByteArrayOutputStream();
      DataOutputStream data = new DataOutputStream(bytes);

      try {
         data.writeUTF(typePacket);
         data.writeInt(first_int);
      } catch (IOException var5) {
         var5.printStackTrace();
      }

      Packet250CustomPayload packet = new Packet250CustomPayload();
      packet.channel = "gravisuite";
      packet.data = bytes.toByteArray();
      packet.length = packet.data.length;
      mc.thePlayer.sendQueue.addToSendQueue(packet);
      return true;
   }

   public EntityPlayer getPlayerInstance() {
      return Minecraft.getMinecraft().thePlayer;
   }

   public void registerSoundHandler() {}

   public void registerRenderers() {
      MinecraftForgeClient.registerItemRenderer(30482, new itemGraviToolRenderer());
   }

   public int addArmor(String armorName) {
      return RenderingRegistry.addNewArmourRendererPrefix(armorName);
   }

   public static void sendPlayerMessage(EntityPlayer player, String message) {
      if(!mc.theWorld.isRemote) {
         player.addChatMessage(message);
      }

   }

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

   @ForgeSubscribe
   public void onWorldLoad(Load event) {
      GraviChestPlateClientProxy.firstLoad = true;
   }

}
