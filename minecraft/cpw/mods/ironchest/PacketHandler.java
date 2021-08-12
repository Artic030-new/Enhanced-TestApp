package cpw.mods.ironchest;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import cpw.mods.ironchest.IronChest;
import cpw.mods.ironchest.TileEntityIronChest;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class PacketHandler implements IPacketHandler {

   public void onPacketData(INetworkManager network, Packet250CustomPayload packet, Player player) {
      ByteArrayDataInput dat = ByteStreams.newDataInput(packet.data);
      int x = dat.readInt();
      int y = dat.readInt();
      int z = dat.readInt();
      byte typDat = dat.readByte();
      byte typ = (byte)(typDat & 15);
      byte facing = (byte)(typDat >> 4 & 15);
      boolean hasStacks = dat.readByte() != 0;
      int[] items = new int[0];
      if(hasStacks) {
         items = new int[24];

         for(int world = 0; world < items.length; ++world) {
            items[world] = dat.readInt();
         }
      }

      World var16 = IronChest.proxy.getClientWorld();
      TileEntity te = var16.getBlockTileEntity(x, y, z);
      if(te instanceof TileEntityIronChest) {
         TileEntityIronChest icte = (TileEntityIronChest)te;
         icte.setFacing(facing);
         icte.handlePacketData(typ, items);
      }

   }

   public static Packet getPacket(TileEntityIronChest tileEntityIronChest) {
      ByteArrayOutputStream bos = new ByteArrayOutputStream(140);
      DataOutputStream dos = new DataOutputStream(bos);
      int x = tileEntityIronChest.xCoord;
      int y = tileEntityIronChest.yCoord;
      int z = tileEntityIronChest.zCoord;
      int typ = (tileEntityIronChest.getType().ordinal() | tileEntityIronChest.getFacing() << 4) & 255;
      int[] items = tileEntityIronChest.buildIntDataList();
      boolean hasStacks = items != null;

      try {
         dos.writeInt(x);
         dos.writeInt(y);
         dos.writeInt(z);
         dos.writeByte(typ);
         dos.writeByte(hasStacks?1:0);
         if(hasStacks) {
            for(int pkt = 0; pkt < 24; ++pkt) {
               dos.writeInt(items[pkt]);
            }
         }
      } catch (IOException var10) {
         ;
      }

      Packet250CustomPayload var11 = new Packet250CustomPayload();
      var11.channel = "IronChest";
      var11.data = bos.toByteArray();
      var11.length = bos.size();
      var11.isChunkDataPacket = true;
      return var11;
   }
}
