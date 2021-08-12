package ic2.core.network;

import ic2.core.IC2;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

public class DataEncoder {

   public static Object decode(DataInputStream is) throws IOException {
      byte type = is.readByte();
      short length;
      int i;
      int z;
      int var8;
      byte var9;
      int var11;
      switch(type) {
      case 0:
         return Integer.valueOf(is.readInt());
      case 1:
         length = is.readShort();
         int[] var21 = new int[length];

         for(i = 0; i < length; ++i) {
            var21[i] = is.readInt();
         }

         return var21;
      case 2:
         return Short.valueOf(is.readShort());
      case 3:
         length = is.readShort();
         short[] var19 = new short[length];

         for(i = 0; i < length; ++i) {
            var19[i] = is.readShort();
         }

         return var19;
      case 4:
         return Byte.valueOf(is.readByte());
      case 5:
         length = is.readShort();
         byte[] var20 = new byte[length];

         for(i = 0; i < length; ++i) {
            var20[i] = is.readByte();
         }

         return var20;
      case 6:
         return Long.valueOf(is.readLong());
      case 7:
         length = is.readShort();
         long[] var15 = new long[length];

         for(i = 0; i < length; ++i) {
            var15[i] = is.readLong();
         }

         return var15;
      case 8:
         return Boolean.valueOf(is.readBoolean());
      case 9:
         length = is.readShort();
         boolean[] var17 = new boolean[length];
         byte var23 = 0;

         for(z = 0; z < length; ++z) {
            if(z % 8 == 0) {
               var23 = is.readByte();
            }

            var17[z] = (var23 & 1 << z % 8) != 0;
         }

         return var17;
      case 10:
         length = is.readShort();
         char[] var13 = new char[length];

         for(i = 0; i < length; ++i) {
            var13[i] = is.readChar();
         }

         return new String(var13);
      case 11:
         length = is.readShort();
         String[] var14 = new String[length];

         for(i = 0; i < length; ++i) {
            short var16 = is.readShort();
            char[] sdata = new char[var16];

            for(int j = 0; j < var16; ++j) {
               sdata[j] = is.readChar();
            }

            var14[i] = new String(sdata);
         }

         return var14;
      case 12:
         length = is.readShort();
         if(length == 0) {
            return null;
         }

         byte var12 = is.readByte();
         short var22 = is.readShort();
         ItemStack var18 = new ItemStack(length, var12, var22);
         if(Item.itemsList[length].isDamageable()) {
            var18.setTagCompound(CompressedStreamTools.read(is));
         }

         return var18;
      case 13:
         return NBTBase.readNamedTag(is);
      case 14:
         var9 = is.readByte();
         var11 = is.readInt();
         switch(var9) {
         case 0:
            return Block.blocksList[var11];
         case 1:
            return Item.itemsList[var11];
         case 2:
            return AchievementList.achievementList.get(var11);
         case 3:
            return Potion.potionTypes[var11];
         case 4:
            return Enchantment.enchantmentsList[var11];
         }
      case 15:
         var9 = is.readByte();
         var11 = is.readInt();
         i = 0;
         if(var9 == 1 || var9 == 2) {
            i = is.readInt();
         }

         z = is.readInt();
         switch(var9) {
         case 0:
            return new ChunkCoordIntPair(var11, z);
         case 1:
            return new ChunkCoordinates(var11, i, z);
         case 2:
            return new ChunkPosition(var11, i, z);
         }
      case 16:
         var8 = is.readInt();
         var11 = is.readInt();
         i = is.readInt();
         z = is.readInt();
         return DimensionManager.getWorld(var8).getBlockTileEntity(var11, i, z);
      case 17:
         var8 = is.readInt();
         return DimensionManager.getWorld(var8);
      case 18:
         return Float.valueOf(is.readFloat());
      case 19:
         length = is.readShort();
         float[] var10 = new float[length];

         for(i = 0; i < length; ++i) {
            var10[i] = is.readFloat();
         }

         return var10;
      case 20:
         return Double.valueOf(is.readDouble());
      case 21:
         length = is.readShort();
         double[] data = new double[length];

         for(i = 0; i < length; ++i) {
            data[i] = is.readDouble();
         }

         return data;
      case 127:
         return null;
      default:
         IC2.platform.displayError("An unknown data type was received over multiplayer to be decoded.\nThis could happen due to corrupted data or a bug.\n\n(Technical information: type ID " + type + ")");
         return null;
      }
   }

   public static void encode(DataOutputStream os, Object o) throws IOException {
      if(o instanceof Integer) {
         os.writeByte(0);
         os.writeInt(((Integer)o).intValue());
      } else {
         int i;
         if(o instanceof int[]) {
            os.writeByte(1);
            int[] oa = (int[])((int[])o);
            os.writeShort(oa.length);

            for(i = 0; i < oa.length; ++i) {
               os.writeInt(oa[i]);
            }
         } else if(o instanceof Short) {
            os.writeByte(2);
            os.writeShort(((Short)o).shortValue());
         } else if(o instanceof short[]) {
            os.writeByte(3);
            short[] var5 = (short[])((short[])o);
            os.writeShort(var5.length);

            for(i = 0; i < var5.length; ++i) {
               os.writeShort(var5[i]);
            }
         } else if(o instanceof Byte) {
            os.writeByte(4);
            os.writeByte(((Byte)o).byteValue());
         } else if(o instanceof byte[]) {
            os.writeByte(5);
            byte[] var6 = (byte[])((byte[])o);
            os.writeShort(var6.length);

            for(i = 0; i < var6.length; ++i) {
               os.writeByte(var6[i]);
            }
         } else if(o instanceof Long) {
            os.writeByte(6);
            os.writeLong(((Long)o).longValue());
         } else if(o instanceof long[]) {
            os.writeByte(7);
            long[] var7 = (long[])((long[])o);
            os.writeShort(var7.length);

            for(i = 0; i < var7.length; ++i) {
               os.writeLong(var7[i]);
            }
         } else if(o instanceof Boolean) {
            os.writeByte(8);
            os.writeBoolean(((Boolean)o).booleanValue());
         } else if(o instanceof boolean[]) {
            os.writeByte(9);
            boolean[] var8 = (boolean[])((boolean[])o);
            os.writeShort(var8.length);
            byte var16 = 0;

            for(int i1 = 0; i1 < var8.length; ++i1) {
               if(i1 % 8 == 0 && i1 > 0) {
                  os.writeByte(var16);
                  var16 = 0;
               }

               var16 = (byte)(var16 | (var8[i1]?1:0) << i1 % 8);
            }

            os.writeByte(var16);
         } else if(o instanceof String) {
            os.writeByte(10);
            String var9 = (String)o;
            os.writeShort(var9.length());
            os.writeChars(var9);
         } else if(o instanceof String[]) {
            os.writeByte(11);
            String[] var10 = (String[])((String[])o);
            os.writeShort(var10.length);

            for(i = 0; i < var10.length; ++i) {
               os.writeShort(var10[i].length());
               os.writeChars(var10[i]);
            }
         } else if(o instanceof ItemStack) {
            os.writeByte(12);
            ItemStack var11 = (ItemStack)o;
            os.writeShort(var11.itemID);
            if(var11.itemID == 0) {
               return;
            }

            os.writeByte(var11.stackSize);
            os.writeShort(var11.getItemDamage());
            if(Item.itemsList[var11.itemID].isDamageable()) {
               NBTTagCompound var19 = var11.getTagCompound();
               if(var19 != null) {
                  CompressedStreamTools.write(var19, os);
               }
            }
         } else if(o instanceof NBTBase) {
            os.writeByte(13);
            NBTBase.writeNamedTag((NBTBase)o, os);
         } else if(o instanceof Block) {
            os.writeByte(14);
            os.writeByte(0);
            os.writeInt(((Block)o).blockID);
         } else if(o instanceof Item) {
            os.writeByte(14);
            os.writeByte(1);
            os.writeInt(((Item)o).itemID);
         } else if(o instanceof Achievement) {
            os.writeByte(14);
            os.writeByte(2);
            os.writeInt(((Achievement)o).statId);
         } else if(o instanceof Potion) {
            os.writeByte(14);
            os.writeByte(3);
            os.writeInt(((Potion)o).id);
         } else if(o instanceof Enchantment) {
            os.writeByte(14);
            os.writeByte(4);
            os.writeInt(((Enchantment)o).effectId);
         } else if(o instanceof ChunkCoordinates) {
            os.writeByte(15);
            os.writeByte(0);
            ChunkCoordinates var12 = (ChunkCoordinates)o;
            os.writeInt(var12.posX);
            os.writeInt(var12.posY);
            os.writeInt(var12.posZ);
         } else if(o instanceof ChunkCoordIntPair) {
            os.writeByte(15);
            os.writeByte(1);
            ChunkCoordIntPair var14 = (ChunkCoordIntPair)o;
            os.writeInt(var14.chunkXPos);
            os.writeInt(var14.getCenterZPosition() - 8 >> 4);
         } else if(o instanceof ChunkPosition) {
            os.writeByte(15);
            os.writeByte(1);
            ChunkPosition var13 = (ChunkPosition)o;
            os.writeInt(var13.x);
            os.writeInt(var13.y);
            os.writeInt(var13.z);
         } else if(o instanceof TileEntity) {
            os.writeByte(16);
            TileEntity var17 = (TileEntity)o;
            os.writeInt(var17.worldObj.provider.dimensionId);
            os.writeInt(var17.xCoord);
            os.writeInt(var17.yCoord);
            os.writeInt(var17.zCoord);
         } else if(o instanceof World) {
            os.writeByte(17);
            os.writeInt(((World)o).provider.dimensionId);
         } else if(o instanceof Float) {
            os.writeByte(18);
            os.writeFloat(((Float)o).floatValue());
         } else if(o instanceof float[]) {
            os.writeByte(19);
            float[] var15 = (float[])((float[])o);
            os.writeShort(var15.length);

            for(i = 0; i < var15.length; ++i) {
               os.writeFloat(var15[i]);
            }
         } else if(o instanceof Double) {
            os.writeByte(20);
            os.writeDouble(((Double)o).doubleValue());
         } else if(o instanceof double[]) {
            os.writeByte(21);
            double[] var18 = (double[])((double[])o);
            os.writeShort(var18.length);

            for(i = 0; i < var18.length; ++i) {
               os.writeDouble(var18[i]);
            }
         } else if(o == null) {
            os.writeByte(127);
         } else {
            IC2.platform.displayError("An unknown data type was attempted to be encoded for sending through\nmultiplayer.\nThis could happen due to a bug.\n\n(Technical information: " + o.getClass().getName() + ")");
         }
      }

   }
}
