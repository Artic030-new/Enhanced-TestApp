package gravisuite;

import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import gravisuite.GraviChestPlateServerProxy;
import gravisuite.GraviSuite;
import gravisuite.ItemAdvancedJetPack;
import gravisuite.Keyboard;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;

public class ServerPacketHandler implements IPacketHandler {

   public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {
      DataInputStream dataStream = new DataInputStream(new ByteArrayInputStream(packet.data));
      EntityPlayerMP playerMP = (EntityPlayerMP)player;

      try {
         String e = dataStream.readUTF();
         int itemstack;
         if(e.equalsIgnoreCase("keyFLY")) {
            itemstack = dataStream.readInt();
            ItemStack itemstack1 = playerMP.inventory.armorInventory[2];
            if(itemstack1 != null && itemstack1.getItem() == GraviSuite.graviChestPlate) {
               GraviChestPlateServerProxy.switchFlyModeServer(playerMP, itemstack1);
            }

            if(itemstack1 != null && (itemstack1.getItem() == GraviSuite.advJetpack || itemstack1.getItem() == GraviSuite.advNanoChestPlate)) {
               ItemAdvancedJetPack.switchFlyState(playerMP, itemstack1);
            }
         }

         ItemStack itemstack2;
         if(e.equalsIgnoreCase("worldLoad")) {
            itemstack2 = playerMP.inventory.armorInventory[2];
            if(itemstack2 != null && itemstack2.getItem() == GraviSuite.graviChestPlate) {
               GraviChestPlateServerProxy.firstLoadServer(playerMP, itemstack2);
            }
         }

         if(e.equalsIgnoreCase("keyState")) {
            itemstack = dataStream.readInt();
            GraviSuite.keyboard.processKeyUpdate(playerMP, itemstack);
         }

         if(e.equalsIgnoreCase("keyModePressed")) {
            itemstack2 = playerMP.inventory.armorInventory[2];
            if(itemstack2 != null && (itemstack2.getItem() == GraviSuite.advJetpack || itemstack2.getItem() == GraviSuite.advNanoChestPlate) && Keyboard.isJumpKeyDown(playerMP)) {
               ItemAdvancedJetPack.switchWorkMode(playerMP, itemstack2);
            }
         }
      } catch (IOException var9) {
         var9.printStackTrace();
      }

   }
}
