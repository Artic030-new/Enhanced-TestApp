package ic2.core.block.crop;

import ic2.api.CropCard;
import ic2.api.TECrop;
import ic2.core.IC2;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class CropMelon extends CropCard {

   public String name() {
      return "Melon";
   }

   public String discoveredBy() {
      return "Chao";
   }

   public int tier() {
      return 2;
   }

   public int stat(int n) {
      switch(n) {
      case 0:
         return 0;
      case 1:
         return 4;
      case 2:
         return 0;
      case 3:
         return 2;
      case 4:
         return 0;
      default:
         return 0;
      }
   }

   public String[] attributes() {
      return new String[]{"Green", "Food", "Stem"};
   }

   public int getSpriteIndex(TECrop crop) {
      return crop.size == 4?20:crop.size + 15;
   }

   public boolean canGrow(TECrop crop) {
      return crop.size <= 3;
   }

   public int weightInfluences(TECrop crop, float humidity, float nutrients, float air) {
      return (int)((double)humidity * 1.1D + (double)nutrients * 0.9D + (double)air);
   }

   public boolean canBeHarvested(TECrop crop) {
      return crop.size == 4;
   }

   public ItemStack getGain(TECrop crop) {
      return IC2.random.nextInt(3) == 0?new ItemStack(Block.melon):new ItemStack(Item.melon, IC2.random.nextInt(4) + 2);
   }

   public ItemStack getSeeds(TECrop crop) {
      return crop.statGain <= 1 && crop.statGrowth <= 1 && crop.statResistance <= 1?new ItemStack(Item.melonSeeds, IC2.random.nextInt(2) + 1):super.getSeeds(crop);
   }

   public int growthDuration(TECrop crop) {
      return crop.size == 3?700:250;
   }

   public byte getSizeAfterHarvest(TECrop crop) {
      return (byte)3;
   }
}
