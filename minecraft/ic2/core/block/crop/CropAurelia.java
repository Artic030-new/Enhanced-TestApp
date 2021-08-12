package ic2.core.block.crop;

import ic2.api.CropCard;
import ic2.api.TECrop;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class CropAurelia extends CropCard {

   public String name() {
      return "Aurelia";
   }

   public int tier() {
      return 8;
   }

   public int stat(int n) {
      switch(n) {
      case 0:
         return 2;
      case 1:
         return 0;
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
      return new String[]{"Gold", "Leaves", "Metal"};
   }

   public int getSpriteIndex(TECrop crop) {
      return crop.size == 4?36:31 + crop.size;
   }

   public boolean canGrow(TECrop crop) {
      return crop.size < 3?true:crop.size == 3 && crop.isBlockBelow(Block.oreGold);
   }

   public boolean canBeHarvested(TECrop crop) {
      return crop.size == 4;
   }

   public ItemStack getGain(TECrop crop) {
      return new ItemStack(Item.goldNugget);
   }

   public int growthDuration(TECrop crop) {
      return crop.size == 3?2200:1000;
   }

   public byte getSizeAfterHarvest(TECrop crop) {
      return (byte)2;
   }
}
