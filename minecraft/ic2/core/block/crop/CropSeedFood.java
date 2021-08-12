package ic2.core.block.crop;

import ic2.api.CropCard;
import ic2.api.TECrop;
import net.minecraft.item.ItemStack;

public class CropSeedFood extends CropCard {

   private String name;
   private int spriteIndex;
   private String color;
   private ItemStack gain;


   public CropSeedFood(String name, int spriteIndex, String color, ItemStack gain) {
      this.name = name;
      this.spriteIndex = spriteIndex;
      this.color = color;
      this.gain = gain;
   }

   public String name() {
      return this.name;
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
         return 0;
      case 4:
         return 2;
      default:
         return 0;
      }
   }

   public String[] attributes() {
      return new String[]{this.color, "Food", this.name};
   }

   public int getSpriteIndex(TECrop crop) {
      return crop.size < 3?47 + crop.size:this.spriteIndex;
   }

   public boolean canGrow(TECrop crop) {
      return crop.size < 3 && crop.getLightLevel() >= 9;
   }

   public boolean canBeHarvested(TECrop crop) {
      return crop.size == 3;
   }

   public ItemStack getGain(TECrop crop) {
      return this.gain.copy();
   }

   public byte getSizeAfterHarvest(TECrop crop) {
      return (byte)1;
   }
}
