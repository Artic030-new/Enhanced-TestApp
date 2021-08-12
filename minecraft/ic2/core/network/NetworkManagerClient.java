package ic2.core.network;

import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.network.INetworkDataProvider;
import ic2.api.network.INetworkItemEventListener;
import ic2.api.network.INetworkTileEntityEventListener;
import ic2.api.network.INetworkUpdateListener;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.item.IHandHeldInventory;
import ic2.core.network.DataEncoder;
import ic2.core.network.NetworkManager;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

@SideOnly(Side.CLIENT)
public class NetworkManagerClient extends NetworkManager {

   public void requestInitialData(INetworkDataProvider dataProvider) {
      if(!IC2.platform.isSimulating()) {
         if(dataProvider instanceof TileEntity) {
            TileEntity te = (TileEntity)dataProvider;

            try {
               ByteArrayOutputStream e = new ByteArrayOutputStream();
               DataOutputStream os = new DataOutputStream(e);
               os.writeByte(0);
               os.writeInt(te.worldObj.provider.dimensionId);
               os.writeInt(te.xCoord);
               os.writeInt(te.yCoord);
               os.writeInt(te.zCoord);
               os.close();
               Packet250CustomPayload packet = new Packet250CustomPayload();
               packet.channel = "ic2";
               packet.isChunkDataPacket = false;
               packet.data = e.toByteArray();
               packet.length = e.size();
               PacketDispatcher.sendPacketToServer(packet);
            } catch (IOException var6) {
               throw new RuntimeException(var6);
            }
         } else {
            IC2.platform.displayError("An unknown network data provider attempted to request data from the\nmultiplayer server.\nThis could happen due to a bug.\n\n(Technical information: " + dataProvider + ")");
         }

      }
   }

   public void initiateClientItemEvent(ItemStack itemStack, int event) {
      try {
         ByteArrayOutputStream e = new ByteArrayOutputStream();
         DataOutputStream os = new DataOutputStream(e);
         os.writeByte(1);
         os.writeInt(itemStack.itemID);
         os.writeInt(itemStack.getItemDamage());
         os.writeInt(event);
         os.close();
         Packet250CustomPayload packet = new Packet250CustomPayload();
         packet.channel = "ic2";
         packet.isChunkDataPacket = false;
         packet.data = e.toByteArray();
         packet.length = e.size();
         PacketDispatcher.sendPacketToServer(packet);
      } catch (IOException var6) {
         throw new RuntimeException(var6);
      }
   }

   public void initiateKeyUpdate(int keyState) {
      try {
         ByteArrayOutputStream e = new ByteArrayOutputStream();
         DataOutputStream os = new DataOutputStream(e);
         os.writeByte(2);
         os.writeInt(keyState);
         os.close();
         Packet250CustomPayload packet = new Packet250CustomPayload();
         packet.channel = "ic2";
         packet.isChunkDataPacket = false;
         packet.data = e.toByteArray();
         packet.length = e.size();
         PacketDispatcher.sendPacketToServer(packet);
      } catch (IOException var5) {
         throw new RuntimeException(var5);
      }
   }

   public void initiateClientTileEntityEvent(TileEntity te, int event) {
      try {
         ByteArrayOutputStream e = new ByteArrayOutputStream();
         DataOutputStream os = new DataOutputStream(e);
         os.writeByte(3);
         os.writeInt(te.worldObj.provider.dimensionId);
         os.writeInt(te.xCoord);
         os.writeInt(te.yCoord);
         os.writeInt(te.zCoord);
         os.writeInt(event);
         os.close();
         Packet250CustomPayload packet = new Packet250CustomPayload();
         packet.channel = "ic2";
         packet.isChunkDataPacket = false;
         packet.data = e.toByteArray();
         packet.length = e.size();
         PacketDispatcher.sendPacketToServer(packet);
      } catch (IOException var6) {
         throw new RuntimeException(var6);
      }
   }

   public void sendLoginData() {
      try {
         ByteArrayOutputStream e = new ByteArrayOutputStream();
         e.write(4);
         GZIPOutputStream gzip = new GZIPOutputStream(e);
         DataOutputStream os = new DataOutputStream(gzip);
         os.writeInt(1);
         os.writeByte(IC2.enableQuantumSpeedOnSprint?1:0);
         ByteArrayOutputStream buffer2 = new ByteArrayOutputStream();
         IC2.runtimeIdProperties.store(buffer2, "");
         os.writeInt(buffer2.size());
         buffer2.writeTo(os);
         os.close();
         gzip.close();
         Packet250CustomPayload packet = new Packet250CustomPayload();
         packet.channel = "ic2";
         packet.isChunkDataPacket = false;
         packet.data = e.toByteArray();
         packet.length = e.size();
         PacketDispatcher.sendPacketToServer(packet);
      } catch (IOException var6) {
         throw new RuntimeException(var6);
      }
   }

   public void onPacketData(INetworkManager network, Packet250CustomPayload packet, Player iplayer) {
      ByteArrayInputStream isRaw = new ByteArrayInputStream(packet.data, 1, packet.data.length - 1);

      try {
         DataInputStream e;
         int dimensionId;
         int windowId;
         int z;
         WorldClient world;
         int var25;
         int var30;
         int var32;
         TileEntity var39;
         switch(packet.data[0]) {
         case 0:
            GZIPInputStream var23 = new GZIPInputStream(isRaw, packet.data.length - 1);
            DataInputStream var27 = new DataInputStream(var23);
            var25 = var27.readInt();
            WorldClient var34 = Minecraft.getMinecraft().theWorld;
            if(var34.provider.dimensionId != var25) {
               return;
            }

            while(true) {
               try {
                  var30 = var27.readInt();
               } catch (EOFException var21) {
                  var27.close();
                  return;
               }

               z = var27.readInt();
               var32 = var27.readInt();
               var39 = var34.getBlockTileEntity(var30, z, var32);
               short var38 = var27.readShort();
               char[] var42 = new char[var38];

               for(int var41 = 0; var41 < var38; ++var41) {
                  var42[var41] = var27.readChar();
               }

               String var44 = new String(var42);
               Field var43 = null;

               try {
                  if(var39 != null) {
                     Class e1 = var39.getClass();

                     do {
                        try {
                           var43 = e1.getDeclaredField(var44);
                        } catch (NoSuchFieldException var19) {
                           e1 = e1.getSuperclass();
                        }
                     } while(var43 == null && e1 != null);

                     if(var43 == null) {
                        IC2.log.warning("Can\'t find field " + var44 + " in te " + var39 + " at " + var30 + "/" + z + "/" + var32);
                     } else {
                        var43.setAccessible(true);
                     }
                  }

                  Object var45 = DataEncoder.decode(var27);
                  if(var43 != null && var39 != null) {
                     var43.set(var39, var45);
                  }
               } catch (Exception var20) {
                  throw new RuntimeException(var20);
               }

               if(var39 instanceof INetworkUpdateListener) {
                  ((INetworkUpdateListener)var39).onNetworkUpdate(var44);
               }
            }
         case 1:
            e = new DataInputStream(isRaw);
            dimensionId = e.readInt();
            var25 = e.readInt();
            windowId = e.readInt();
            var30 = e.readInt();
            z = e.readInt();
            WorldClient var35 = Minecraft.getMinecraft().theWorld;
            if(var35.provider.dimensionId != dimensionId) {
               return;
            }

            var39 = var35.getBlockTileEntity(var25, windowId, var30);
            if(var39 instanceof INetworkTileEntityEventListener) {
               ((INetworkTileEntityEventListener)var39).onNetworkEvent(z);
            }
            break;
         case 2:
            e = new DataInputStream(isRaw);
            byte var26 = e.readByte();
            char[] var28 = new char[var26];

            for(windowId = 0; windowId < var26; ++windowId) {
               var28[windowId] = e.readChar();
            }

            String var31 = new String(var28);
            var30 = e.readInt();
            z = e.readInt();
            var32 = e.readInt();
            world = Minecraft.getMinecraft().theWorld;
            Iterator var37 = world.playerEntities.iterator();

            EntityPlayer entityPlayer;
            do {
               if(!var37.hasNext()) {
                  return;
               }

               Object obj = var37.next();
               entityPlayer = (EntityPlayer)obj;
            } while(!entityPlayer.username.equals(var31));

            Item item = Item.itemsList[var30];
            if(item instanceof INetworkItemEventListener) {
               ((INetworkItemEventListener)item).onNetworkEvent(z, entityPlayer, var32);
            }
            break;
         case 3:
            e = new DataInputStream(isRaw);
            dimensionId = e.readInt();
            var25 = e.readInt();
            windowId = e.readInt();
            var30 = e.readInt();
            short var33 = e.readShort();
            byte var36 = e.readByte();
            world = Minecraft.getMinecraft().theWorld;
            if(world.provider.dimensionId != dimensionId) {
               return;
            }

            world.setBlockAndMetadataWithNotify(var25, windowId, var30, var33, var36);
            break;
         case 4:
            e = new DataInputStream(isRaw);
            EntityPlayer var24 = IC2.platform.getPlayerInstance();
            switch(e.readByte()) {
            case 0:
               var25 = e.readInt();
               windowId = e.readInt();
               var30 = e.readInt();
               z = e.readInt();
               var32 = e.readInt();
               world = Minecraft.getMinecraft().theWorld;
               if(world.provider.dimensionId != var25) {
                  return;
               }

               TileEntity var40 = world.getBlockTileEntity(windowId, var30, z);
               if(var40 instanceof IHasGui) {
                  IC2.platform.launchGuiClient(var24, (IHasGui)var40);
               }

               var24.openContainer.windowId = var32;
               return;
            case 1:
               var25 = e.readInt();
               windowId = e.readInt();
               if(var25 != var24.inventory.currentItem) {
                  return;
               }

               ItemStack var29 = var24.inventory.getCurrentItem();
               if(var29 != null && var29.getItem() instanceof IHandHeldInventory) {
                  IC2.platform.launchGuiClient(var24, ((IHandHeldInventory)var29.getItem()).getInventory(var24, var29));
               }

               var24.openContainer.windowId = windowId;
               return;
            default:
               return;
            }
         case 5:
            e = new DataInputStream(isRaw);
            dimensionId = e.readInt();
            double x = e.readDouble();
            double y = e.readDouble();
            double z1 = e.readDouble();
            WorldClient world1 = Minecraft.getMinecraft().theWorld;
            if(world1.provider.dimensionId != dimensionId) {
               return;
            }

            world1.playSoundEffect(x, y, z1, "random.explode", 4.0F, (1.0F + (world1.rand.nextFloat() - world1.rand.nextFloat()) * 0.2F) * 0.7F);
            world1.spawnParticle("hugeexplosion", x, y, z1, 0.0D, 0.0D, 0.0D);
         }
      } catch (IOException var22) {
         var22.printStackTrace();
      }

   }

   public void announceBlockUpdate(World world, int x, int y, int z) {
      if(IC2.platform.isSimulating()) {
         super.announceBlockUpdate(world, x, y, z);
      }

   }
}
