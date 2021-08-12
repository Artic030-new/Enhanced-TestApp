package ic2.core.item.tool;

import ic2.core.IC2;
import ic2.core.audio.PositionSpec;
import ic2.core.item.ElectricItem;
import ic2.core.util.Util;
import java.util.Map;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemScanner extends ElectricItem {

   public ItemScanner(int id, int index, int t) {
      super(id, index);
      super.maxCharge = 10000;
      super.transferLimit = 50;
      super.tier = t;
   }

   public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer) {
      if((super.tier != 1 || ElectricItem.use(itemstack, 50, entityplayer)) && (super.tier != 2 || ElectricItem.use(itemstack, 250, entityplayer))) {
         IC2.audioManager.playOnce(entityplayer, PositionSpec.Hand, "Tools/ODScanner.ogg", true, IC2.audioManager.defaultVolume);
         int value;
         if(super.tier == 2) {
            value = valueOfArea(world, Util.roundToNegInf(entityplayer.posX), Util.roundToNegInf(entityplayer.posY), Util.roundToNegInf(entityplayer.posZ), true);
            IC2.platform.messagePlayer(entityplayer, "SCAN RESULT: Ore value in this area is " + value);
         } else {
            value = valueOfArea(world, Util.roundToNegInf(entityplayer.posX), Util.roundToNegInf(entityplayer.posY), Util.roundToNegInf(entityplayer.posZ), false);
            IC2.platform.messagePlayer(entityplayer, "SCAN RESULT: Ore density in this area is " + value);
         }

         return itemstack;
      } else {
         return itemstack;
      }
   }

   public static int valueOfArea(World worldObj, int x, int y, int z, boolean advancedMode) {
      int totalScore = 0;
      int blocksScanned = 0;
      int range = advancedMode?4:2;

      for(int blockY = y; blockY > 0; --blockY) {
         for(int blockX = x - range; blockX <= x + range; ++blockX) {
            for(int blockZ = z - range; blockZ <= z + range; ++blockZ) {
               int blockId = worldObj.getBlockId(blockX, blockY, blockZ);
               int metaData = worldObj.getBlockMetadata(blockX, blockY, blockZ);
               if(advancedMode) {
                  totalScore += valueOf(blockId, metaData);
               } else if(isValuable(blockId, metaData)) {
                  ++totalScore;
               }

               ++blocksScanned;
            }
         }
      }

      return (blocksScanned > 0?Integer.valueOf((int)(1000.0D * (double)totalScore / (double)blocksScanned)):null).intValue();
   }

   public static boolean isValuable(int blockId, int metaData) {
      return valueOf(blockId, metaData) > 0;
   }

   public static int valueOf(int blockId, int metaData) {
      if(IC2.valuableOres.containsKey(Integer.valueOf(blockId))) {
         Map metaMap = (Map)IC2.valuableOres.get(Integer.valueOf(blockId));
         if(metaMap.containsKey(Integer.valueOf(-1))) {
            return ((Integer)metaMap.get(Integer.valueOf(-1))).intValue();
         }

         if(metaMap.containsKey(Integer.valueOf(metaData))) {
            return ((Integer)metaMap.get(Integer.valueOf(metaData))).intValue();
         }
      }

      return 0;
   }

   public int startLayerScan(ItemStack itemStack) {
      return ElectricItem.use(itemStack, 50, (EntityPlayer)null)?2:0;
   }
}
