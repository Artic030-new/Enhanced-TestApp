package ic2.core.block.crop;

import ic2.api.CropCard;
import ic2.api.TECrop;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class CropCocoa extends CropCard {

   public String name() {
      return "Cocoa";
   }

   public String discoveredBy() {
      return "Notch";
   }

   public int tier() {
      return 3;
   }

   public int stat(int n) {
      switch(n) {
      case 0:
         return 1;
      case 1:
         return 3;
      case 2:
         return 0;
      case 3:
         return 4;
      case 4:
         return 0;
      default:
         return 0;
      }
   }

   public String[] attributes() {
      return new String[]{"Brown", "Food", "Stem"};
   }

   public int getSpriteIndex(TECrop crop) {
      return crop.size == 4?26:crop.size + 15;
   }

   public boolean canGrow(TECrop crop) {
      return crop.size <= 3 && crop.getNutrients() >= 3;
   }

   public int weightInfluences(TECrop crop, float humidity, float nutrients, float air) {
      return (int)((double)humidity * 0.8D + (double)nutrients * 1.3D + (double)air * 0.9D);
   }

   public boolean canBeHarvested(TECrop crop) {
      return crop.size == 4;
   }

   public ItemStack getGain(TECrop crop) {
      return new ItemStack(Item.dyePowder, 1, 3);
   }

   public int growthDuration(TECrop crop) {
      return crop.size == 3?900:400;
   }

   public byte getSizeAfterHarvest(TECrop crop) {
      return (byte)3;
   }
}
