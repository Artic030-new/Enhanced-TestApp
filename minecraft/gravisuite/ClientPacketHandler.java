package gravisuite;

import cpw.mods.fml.common.network.Player;
import gravisuite.ClientProxy;
import gravisuite.GraviChestPlateClientProxy;
import gravisuite.GraviSuite;
import gravisuite.ServerPacketHandler;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;

public class ClientPacketHandler extends ServerPacketHandler {

   public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {
      DataInputStream dataStream = new DataInputStream(new ByteArrayInputStream(packet.data));

      try {
         String e = dataStream.readUTF();
         if(e.equalsIgnoreCase("setFlyStatus")) {
            ItemStack itemstack = ClientProxy.mc.thePlayer.inventory.armorItemInSlot(2);
            if(itemstack != null && itemstack.getItem() == GraviSuite.graviChestPlate) {
               GraviChestPlateClientProxy.firstLoadClient(ClientProxy.mc.thePlayer, itemstack);
            }
         }
      } catch (IOException var7) {
         var7.printStackTrace();
      }

   }
}
