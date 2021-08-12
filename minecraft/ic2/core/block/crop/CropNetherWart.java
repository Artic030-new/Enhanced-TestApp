package ic2.core.block.crop;

import ic2.api.CropCard;
import ic2.api.TECrop;
import ic2.core.block.TileEntityCrop;
import ic2.core.block.crop.IC2Crops;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class CropNetherWart extends CropCard {

   public String name() {
      return "Nether Wart";
   }

   public String discoveredBy() {
      return "Notch";
   }

   public int tier() {
      return 5;
   }

   public int stat(int n) {
      switch(n) {
      case 0:
         return 4;
      case 1:
         return 2;
      case 2:
         return 0;
      case 3:
         return 2;
      case 4:
         return 1;
      default:
         return 0;
      }
   }

   public String[] attributes() {
      return new String[]{"Red", "Nether", "Ingredient", "Soulsand"};
   }

   public int getSpriteIndex(TECrop crop) {
      return crop.size + 36;
   }

   public boolean canGrow(TECrop crop) {
      return crop.size < 3;
   }

   public boolean canBeHarvested(TECrop crop) {
      return crop.size == 3;
   }

   public float dropGainChance() {
      return 2.0F;
   }

   public ItemStack getGain(TECrop crop) {
      return new ItemStack(Item.netherStalkSeeds, 1);
   }

   public void tick(TECrop crop) {
      TileEntityCrop te = (TileEntityCrop)crop;
      if(te.isBlockBelow(Block.slowSand)) {
         if(this.canGrow(te)) {
            te.growthPoints = (int)((double)te.growthPoints + (double)te.calcGrowthRate() * 0.5D);
         }
      } else if(te.isBlockBelow(Block.blockSnow) && crop.worldObj.rand.nextInt(300) == 0) {
         te.id = (short)IC2Crops.cropTerraWart.getId();
      }

   }
}
