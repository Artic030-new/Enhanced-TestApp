package gravisuite;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet250CustomPayload;

public class ServerProxy {

   public void initCore() {}

   public static void sendPlayerMessage(EntityPlayer player, String message) {
      player.addChatMessage(message);
   }

   public static boolean sendPacket(EntityPlayer player, String typePacket, int first_int) {
      ByteArrayOutputStream bytes = new ByteArrayOutputStream();
      DataOutputStream data = new DataOutputStream(bytes);

      try {
         data.writeUTF(typePacket);
         data.writeInt(first_int);
      } catch (IOException var6) {
         var6.printStackTrace();
      }

      Packet250CustomPayload packet = new Packet250CustomPayload();
      packet.channel = "gravisuite";
      packet.data = bytes.toByteArray();
      packet.length = packet.data.length;
      PacketDispatcher.sendPacketToPlayer(packet, (Player)player);
      return true;
   }

   public static boolean isSimulating() {
      return !FMLCommonHandler.instance().getEffectiveSide().isClient();
   }

   public void playSoundSp(String sound, float var2, float var3) {}

   public void registerSoundHandler() {}

   public void registerRenderers() {}

   public EntityPlayer getPlayerInstance() {
      return null;
   }

   public int addArmor(String armorName) {
      return 0;
   }
}
