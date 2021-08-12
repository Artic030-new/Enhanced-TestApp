package ic2.core.network;

import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import ic2.api.network.INetworkClientTileEntityEventListener;
import ic2.api.network.INetworkDataProvider;
import ic2.api.network.INetworkItemEventListener;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.WorldData;
import ic2.core.item.IHandHeldInventory;
import ic2.core.item.armor.ItemArmorQuantumSuit;
import ic2.core.network.DataEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

public class NetworkManager implements IPacketHandler {

   public static final int updatePeriod = 2;


   public void onTick(World world) {
      WorldData worldData = WorldData.get(world);
      if(--worldData.ticksLeftToNetworkUpdate == 0) {
         this.sendUpdatePacket(world);
         worldData.ticksLeftToNetworkUpdate = 2;
      }

   }

   public void updateTileEntityField(TileEntity te, String field) {
      WorldData worldData = WorldData.get(te.worldObj);
      worldData.networkedFieldsToUpdate.add(new NetworkManager.TileEntityField(te, field));
      if(worldData.networkedFieldsToUpdate.size() > 10000) {
         this.sendUpdatePacket(te.worldObj);
      }

   }

   public void initiateTileEntityEvent(TileEntity te, int event, boolean limitRange) {
      int maxDistance = limitRange?400:MinecraftServer.getServer().getConfigurationManager().getEntityViewDistance() + 16;
      World world = te.worldObj;
      Packet250CustomPayload packet = null;
      Iterator i$ = world.playerEntities.iterator();

      while(i$.hasNext()) {
         Object obj = i$.next();
         EntityPlayerMP entityPlayer = (EntityPlayerMP)obj;
         int distanceX = te.xCoord - (int)entityPlayer.posX;
         int distanceZ = te.zCoord - (int)entityPlayer.posZ;
         int distance;
         if(limitRange) {
            distance = distanceX * distanceX + distanceZ * distanceZ;
         } else {
            distance = Math.max(Math.abs(distanceX), Math.abs(distanceZ));
         }

         if(distance <= maxDistance) {
            if(packet == null) {
               try {
                  ByteArrayOutputStream e = new ByteArrayOutputStream();
                  DataOutputStream os = new DataOutputStream(e);
                  os.writeByte(1);
                  os.writeInt(world.provider.dimensionId);
                  os.writeInt(te.xCoord);
                  os.writeInt(te.yCoord);
                  os.writeInt(te.zCoord);
                  os.writeInt(event);
                  os.close();
                  packet = new Packet250CustomPayload();
                  packet.channel = "ic2";
                  packet.isChunkDataPacket = false;
                  packet.data = e.toByteArray();
                  packet.length = e.size();
               } catch (IOException var15) {
                  throw new RuntimeException(var15);
               }
            }

            PacketDispatcher.sendPacketToPlayer(packet, (Player)entityPlayer);
         }
      }

   }

   public void initiateItemEvent(EntityPlayer player, ItemStack itemStack, int event, boolean limitRange) {
      if(player.username.length() <= 127) {
         int maxDistance = limitRange?400:MinecraftServer.getServer().getConfigurationManager().getEntityViewDistance() + 16;
         Packet250CustomPayload packet = null;
         Iterator i$ = player.worldObj.playerEntities.iterator();

         while(i$.hasNext()) {
            Object obj = i$.next();
            EntityPlayerMP entityPlayer = (EntityPlayerMP)obj;
            int distanceX = (int)player.posX - (int)entityPlayer.posX;
            int distanceZ = (int)player.posZ - (int)entityPlayer.posZ;
            int distance;
            if(limitRange) {
               distance = distanceX * distanceX + distanceZ * distanceZ;
            } else {
               distance = Math.max(Math.abs(distanceX), Math.abs(distanceZ));
            }

            if(distance <= maxDistance) {
               if(packet == null) {
                  try {
                     ByteArrayOutputStream e = new ByteArrayOutputStream();
                     DataOutputStream os = new DataOutputStream(e);
                     os.writeByte(2);
                     os.writeByte(player.username.length());
                     os.writeChars(player.username);
                     os.writeInt(itemStack.itemID);
                     os.writeInt(itemStack.getItemDamage());
                     os.writeInt(event);
                     os.close();
                     packet = new Packet250CustomPayload();
                     packet.channel = "ic2";
                     packet.isChunkDataPacket = false;
                     packet.data = e.toByteArray();
                     packet.length = e.size();
                  } catch (IOException var15) {
                     throw new RuntimeException(var15);
                  }
               }

               PacketDispatcher.sendPacketToPlayer(packet, (Player)entityPlayer);
            }
         }

      }
   }

   public void announceBlockUpdate(World world, int x, int y, int z) {
      Packet250CustomPayload packet = null;
      Iterator i$ = world.playerEntities.iterator();

      while(i$.hasNext()) {
         Object obj = i$.next();
         EntityPlayerMP entityPlayer = (EntityPlayerMP)obj;
         int distance = Math.min(Math.abs(x - (int)entityPlayer.posX), Math.abs(z - (int)entityPlayer.posZ));
         if(distance <= MinecraftServer.getServer().getConfigurationManager().getEntityViewDistance() + 16) {
            if(packet == null) {
               try {
                  ByteArrayOutputStream e = new ByteArrayOutputStream();
                  DataOutputStream os = new DataOutputStream(e);
                  os.writeByte(3);
                  os.writeInt(world.provider.dimensionId);
                  os.writeInt(x);
                  os.writeInt(y);
                  os.writeInt(z);
                  os.writeShort(world.getBlockId(x, y, z));
                  os.writeByte(world.getBlockMetadata(x, y, z));
                  os.close();
                  packet = new Packet250CustomPayload();
                  packet.channel = "ic2";
                  packet.isChunkDataPacket = true;
                  packet.data = e.toByteArray();
                  packet.length = e.size();
               } catch (IOException var12) {
                  throw new RuntimeException(var12);
               }
            }

            PacketDispatcher.sendPacketToPlayer(packet, (Player)entityPlayer);
         }
      }

   }

   public void requestInitialData(INetworkDataProvider dataProvider) {}

   public void initiateClientItemEvent(ItemStack itemStack, int event) {}

   public void initiateClientTileEntityEvent(TileEntity te, int event) {}

   public void initiateGuiDisplay(EntityPlayerMP entityPlayer, IHasGui inventory, int windowId) {
      try {
         ByteArrayOutputStream e = new ByteArrayOutputStream();
         DataOutputStream os = new DataOutputStream(e);
         os.writeByte(4);
         if(inventory instanceof TileEntity) {
            TileEntity packet = (TileEntity)inventory;
            os.writeByte(0);
            os.writeInt(packet.worldObj.provider.dimensionId);
            os.writeInt(packet.xCoord);
            os.writeInt(packet.yCoord);
            os.writeInt(packet.zCoord);
         } else if(entityPlayer.inventory.getCurrentItem() != null && entityPlayer.inventory.getCurrentItem().getItem() instanceof IHandHeldInventory) {
            os.writeByte(1);
            os.writeInt(entityPlayer.inventory.currentItem);
         } else {
            IC2.platform.displayError("An unknown GUI type was attempted to be displayed.\nThis could happen due to corrupted data from a player or a bug.\n\n(Technical information: " + inventory + ")");
         }

         os.writeInt(windowId);
         os.close();
         Packet250CustomPayload packet1 = new Packet250CustomPayload();
         packet1.channel = "ic2";
         packet1.isChunkDataPacket = false;
         packet1.data = e.toByteArray();
         packet1.length = e.size();
         PacketDispatcher.sendPacketToPlayer(packet1, (Player)entityPlayer);
      } catch (IOException var7) {
         throw new RuntimeException(var7);
      }
   }

   private void sendUpdatePacket(World world) {
      WorldData worldData = WorldData.get(world);
      if(!worldData.networkedFieldsToUpdate.isEmpty()) {
         Iterator i$ = world.playerEntities.iterator();

         while(i$.hasNext()) {
            Object obj = i$.next();
            EntityPlayerMP entityPlayer = (EntityPlayerMP)obj;

            try {
               ByteArrayOutputStream e = new ByteArrayOutputStream();
               e.write(0);
               GZIPOutputStream gzip = new GZIPOutputStream(e);
               DataOutputStream os = new DataOutputStream(gzip);
               os.writeInt(world.provider.dimensionId);
               Iterator packet = worldData.networkedFieldsToUpdate.iterator();

               while(packet.hasNext()) {
                  NetworkManager.TileEntityField tef = (NetworkManager.TileEntityField)packet.next();
                  if(!tef.te.isInvalid() && tef.te.worldObj == world && (tef.target == null || tef.target == entityPlayer)) {
                     int distance = Math.min(Math.abs(tef.te.xCoord - (int)entityPlayer.posX), Math.abs(tef.te.zCoord - (int)entityPlayer.posZ));
                     if(distance <= MinecraftServer.getServer().getConfigurationManager().getEntityViewDistance() + 16) {
                        os.writeInt(tef.te.xCoord);
                        os.writeInt(tef.te.yCoord);
                        os.writeInt(tef.te.zCoord);
                        os.writeShort(tef.field.length());
                        os.writeChars(tef.field);
                        Field field = null;

                        try {
                           Class e1 = tef.te.getClass();

                           do {
                              try {
                                 field = e1.getDeclaredField(tef.field);
                              } catch (NoSuchFieldException var15) {
                                 e1 = e1.getSuperclass();
                              }
                           } while(field == null && e1 != null);

                           if(field == null) {
                              throw new NoSuchFieldException(tef.field);
                           }

                           field.setAccessible(true);
                           DataEncoder.encode(os, field.get(tef.te));
                        } catch (Exception var16) {
                           throw new RuntimeException(var16);
                        }
                     }
                  }
               }

               os.close();
               gzip.close();
               if(e.size() > 1) {
                  Packet250CustomPayload packet1 = new Packet250CustomPayload();
                  packet1.channel = "ic2";
                  packet1.isChunkDataPacket = true;
                  packet1.data = e.toByteArray();
                  packet1.length = e.size();
                  PacketDispatcher.sendPacketToPlayer(packet1, (Player)entityPlayer);
               }
            } catch (IOException var17) {
               throw new RuntimeException(var17);
            }
         }

         worldData.networkedFieldsToUpdate.clear();
      }
   }

   public void onPacketData(INetworkManager network, Packet250CustomPayload packet, Player iplayer) {
      EntityPlayerMP player = (EntityPlayerMP)iplayer;
      ByteArrayInputStream isRaw = new ByteArrayInputStream(packet.data, 1, packet.data.length - 1);

      try {
         int clientNetworkProtocolVersion;
         int var21;
         DataInputStream var20;
         int var22;
         int var24;
         int var29;
         switch(packet.data[0]) {
         case 0:
            var20 = new DataInputStream(isRaw);
            var21 = var20.readInt();
            clientNetworkProtocolVersion = var20.readInt();
            var22 = var20.readInt();
            var24 = var20.readInt();
            WorldServer[] var28 = DimensionManager.getWorlds();
            int var27 = var28.length;

            for(var29 = 0; var29 < var27; ++var29) {
               WorldServer var30 = var28[var29];
               if(var21 == var30.provider.dimensionId) {
                  TileEntity var35 = var30.getBlockTileEntity(clientNetworkProtocolVersion, var22, var24);
                  if(var35 instanceof INetworkDataProvider) {
                     WorldData var33 = WorldData.get(var30);
                     Iterator i$1 = ((INetworkDataProvider)var35).getNetworkedFields().iterator();

                     while(i$1.hasNext()) {
                        String field = (String)i$1.next();
                        var33.networkedFieldsToUpdate.add(new NetworkManager.TileEntityField(var35, field, player));
                        if(var33.networkedFieldsToUpdate.size() > 10000) {
                           this.sendUpdatePacket(var30);
                        }
                     }
                  }

                  return;
               }
            }

            return;
         case 1:
            var20 = new DataInputStream(isRaw);
            var21 = var20.readInt();
            clientNetworkProtocolVersion = var20.readInt();
            var22 = var20.readInt();
            if(var21 < Item.itemsList.length) {
               Item var25 = Item.itemsList[var21];
               if(var25 instanceof INetworkItemEventListener) {
                  ((INetworkItemEventListener)var25).onNetworkEvent(clientNetworkProtocolVersion, player, var22);
               }
            }
            break;
         case 2:
            var20 = new DataInputStream(isRaw);
            var21 = var20.readInt();
            IC2.keyboard.processKeyUpdate(player, var21);
            break;
         case 3:
            var20 = new DataInputStream(isRaw);
            var21 = var20.readInt();
            clientNetworkProtocolVersion = var20.readInt();
            var22 = var20.readInt();
            var24 = var20.readInt();
            int var23 = var20.readInt();
            WorldServer[] var26 = DimensionManager.getWorlds();
            var29 = var26.length;

            for(int var32 = 0; var32 < var29; ++var32) {
               WorldServer var31 = var26[var32];
               if(var21 == var31.provider.dimensionId) {
                  TileEntity var34 = var31.getBlockTileEntity(clientNetworkProtocolVersion, var22, var24);
                  if(var34 instanceof INetworkClientTileEntityEventListener) {
                     ((INetworkClientTileEntityEventListener)var34).onNetworkEvent(player, var23);
                  }

                  return;
               }
            }

            return;
         case 4:
            GZIPInputStream e = new GZIPInputStream(isRaw, packet.data.length - 1);
            DataInputStream is = new DataInputStream(e);
            clientNetworkProtocolVersion = is.readInt();
            if(clientNetworkProtocolVersion != 1) {
               player.playerNetServerHandler.kickPlayerFromServer("IC2 network protocol version mismatch (expected 1 (1.115.207-lf), got " + clientNetworkProtocolVersion + ")");
            }

            boolean enableQuantumSpeedOnSprint = is.readByte() != 0;
            ItemArmorQuantumSuit.enableQuantumSpeedOnSprintMap.put(player, Boolean.valueOf(enableQuantumSpeedOnSprint));
            is.readInt();
            Properties clientRuntimeIdProperties = new Properties();
            clientRuntimeIdProperties.load(is);
            is.close();
            Iterator i$ = IC2.runtimeIdProperties.entrySet().iterator();

            while(i$.hasNext()) {
               Entry mapEntry = (Entry)i$.next();
               String key = (String)mapEntry.getKey();
               String value = (String)mapEntry.getValue();
               if(!clientRuntimeIdProperties.containsKey(key)) {
                  player.playerNetServerHandler.kickPlayerFromServer("IC2 id value missing (" + key + ")");
                  break;
               }

               int separatorPos = key.indexOf(46);
               if(separatorPos != -1) {
                  String section = key.substring(0, separatorPos);
                  key.substring(separatorPos + 1);
                  if((section.equals("block") || section.equals("item")) && !value.equals(clientRuntimeIdProperties.get(key))) {
                     player.playerNetServerHandler.kickPlayerFromServer("IC2 id mismatch (" + key + ": expected " + value + ", got " + clientRuntimeIdProperties.get(key) + ")");
                  }
               }
            }
         }
      } catch (IOException var19) {
         var19.printStackTrace();
      }

   }

   public void initiateKeyUpdate(int keyState) {}

   public void sendLoginData() {}

   public void initiateExplosionEffect(World world, double x, double y, double z) {
      try {
         ByteArrayOutputStream e = new ByteArrayOutputStream();
         DataOutputStream os = new DataOutputStream(e);
         os.writeByte(5);
         os.writeInt(world.provider.dimensionId);
         os.writeDouble(x);
         os.writeDouble(y);
         os.writeDouble(z);
         os.close();
         Packet250CustomPayload packet = new Packet250CustomPayload();
         packet.channel = "ic2";
         packet.isChunkDataPacket = false;
         packet.data = e.toByteArray();
         packet.length = e.size();
         Iterator i$ = world.playerEntities.iterator();

         while(i$.hasNext()) {
            Object player = i$.next();
            EntityPlayerMP entityPlayer = (EntityPlayerMP)player;
            if(entityPlayer.getDistanceSq(x, y, z) < 128.0D) {
               PacketDispatcher.sendPacketToPlayer(packet, (Player)entityPlayer);
            }
         }

      } catch (IOException var14) {
         throw new RuntimeException(var14);
      }
   }

   public class TileEntityField {

      TileEntity te;
      String field;
      EntityPlayerMP target = null;


      TileEntityField(TileEntity te, String field) {
         this.te = te;
         this.field = field;
      }

      TileEntityField(TileEntity te, String field, EntityPlayerMP target) {
         this.te = te;
         this.field = field;
         this.target = target;
      }

      public boolean equals(Object obj) {
         if(!(obj instanceof NetworkManager.TileEntityField)) {
            return false;
         } else {
            NetworkManager.TileEntityField tef = (NetworkManager.TileEntityField)obj;
            return tef.te == this.te && tef.field.equals(this.field);
         }
      }

      public int hashCode() {
         return this.te.hashCode() * 31 ^ this.field.hashCode();
      }
   }
}
