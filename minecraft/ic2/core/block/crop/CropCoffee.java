package ic2.core.block.crop;

import ic2.api.CropCard;
import ic2.api.TECrop;
import ic2.core.Ic2Items;
import net.minecraft.item.ItemStack;

public class CropCoffee extends CropCard {

   public String name() {
      return "Coffee";
   }

   public String discoveredBy() {
      return "Snoochy";
   }

   public int tier() {
      return 7;
   }

   public int stat(int n) {
      switch(n) {
      case 0:
         return 1;
      case 1:
         return 4;
      case 2:
         return 1;
      case 3:
         return 2;
      case 4:
         return 0;
      default:
         return 0;
      }
   }

   public String[] attributes() {
      return new String[]{"Leaves", "Ingrident", "Beans"};
   }

   public int getSpriteIndex(TECrop crop) {
      return crop.size == 5?43:(crop.size == 4?42:31 + crop.size);
   }

   public boolean canGrow(TECrop crop) {
      return crop.size < 5 && crop.getLightLevel() >= 9;
   }

   public int weightInfluences(TECrop crop, float humidity, float nutrients, float air) {
      return (int)(0.4D * (double)humidity + 1.4D * (double)nutrients + 1.2D * (double)air);
   }

   public int growthDuration(TECrop crop) {
      return crop.size == 3?(int)((double)super.growthDuration(crop) * 0.5D):(crop.size == 4?(int)((double)super.growthDuration(crop) * 1.5D):super.growthDuration(crop));
   }

   public boolean canBeHarvested(TECrop crop) {
      return crop.size >= 4;
   }

   public ItemStack getGain(TECrop crop) {
      return crop.size == 4?null:new ItemStack(Ic2Items.coffeeBeans.getItem());
   }

   public byte getSizeAfterHarvest(TECrop crop) {
      return (byte)3;
   }
}
