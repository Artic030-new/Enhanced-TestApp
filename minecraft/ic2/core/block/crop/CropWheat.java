package ic2.core.block.crop;

import ic2.api.CropCard;
import ic2.api.TECrop;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class CropWheat extends CropCard {

   public String name() {
      return "Wheat";
   }

   public String discoveredBy() {
      return "Notch";
   }

   public int tier() {
      return 1;
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
         return 0;
      case 4:
         return 2;
      default:
         return 0;
      }
   }

   public String[] attributes() {
      return new String[]{"Yellow", "Food", "Wheat"};
   }

   public int getSpriteIndex(TECrop crop) {
      return crop.size + 1;
   }

   public boolean canGrow(TECrop crop) {
      return crop.size < 7 && crop.getLightLevel() >= 9;
   }

   public boolean canBeHarvested(TECrop crop) {
      return crop.size == 7;
   }

   public ItemStack getGain(TECrop crop) {
      return new ItemStack(Item.wheat, 1);
   }

   public ItemStack getSeeds(TECrop crop) {
      return crop.statGain <= 1 && crop.statGrowth <= 1 && crop.statResistance <= 1?new ItemStack(Item.seeds):super.getSeeds(crop);
   }

   public byte getSizeAfterHarvest(TECrop crop) {
      return (byte)2;
   }
}
