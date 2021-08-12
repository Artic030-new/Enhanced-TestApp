package ic2.core.block.crop;

import ic2.api.CropCard;
import ic2.api.TECrop;
import ic2.core.Ic2Items;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public class CropFerru extends CropCard {

   public String name() {
      return "Ferru";
   }

   public int tier() {
      return 6;
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
         return 1;
      case 4:
         return 0;
      default:
         return 0;
      }
   }

   public String[] attributes() {
      return new String[]{"Gray", "Leaves", "Metal"};
   }

   public int getSpriteIndex(TECrop crop) {
      return crop.size == 4?35:31 + crop.size;
   }

   public boolean canGrow(TECrop crop) {
      return crop.size < 3?true:crop.size == 3 && crop.isBlockBelow(Block.oreIron);
   }

   public boolean canBeHarvested(TECrop crop) {
      return crop.size == 4;
   }

   public ItemStack getGain(TECrop crop) {
      return new ItemStack(Ic2Items.smallIronDust.getItem());
   }

   public float dropGainChance() {
      return super.dropGainChance() / 2.0F;
   }

   public int growthDuration(TECrop crop) {
      return crop.size == 3?2000:800;
   }

   public byte getSizeAfterHarvest(TECrop crop) {
      return (byte)2;
   }
}
