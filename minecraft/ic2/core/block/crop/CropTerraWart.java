package ic2.core.block.crop;

import ic2.api.CropCard;
import ic2.api.TECrop;
import ic2.core.Ic2Items;
import ic2.core.block.TileEntityCrop;
import ic2.core.block.crop.IC2Crops;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public class CropTerraWart extends CropCard {

   public String name() {
      return "Terra Wart";
   }

   public int tier() {
      return 5;
   }

   public int stat(int n) {
      switch(n) {
      case 0:
         return 2;
      case 1:
         return 4;
      case 2:
         return 0;
      case 3:
         return 3;
      case 4:
         return 0;
      default:
         return 0;
      }
   }

   public String[] attributes() {
      return new String[]{"Blue", "Aether", "Consumable", "Snow"};
   }

   public int getSpriteIndex(TECrop crop) {
      return crop.size == 1?37:crop.size + 38;
   }

   public boolean canGrow(TECrop crop) {
      return crop.size < 3;
   }

   public boolean canBeHarvested(TECrop crop) {
      return crop.size == 3;
   }

   public float dropGainChance() {
      return 0.8F;
   }

   public ItemStack getGain(TECrop crop) {
      return new ItemStack(Ic2Items.terraWart.getItem(), 1);
   }

   public void tick(TECrop crop) {
      TileEntityCrop te = (TileEntityCrop)crop;
      if(te.isBlockBelow(Block.blockSnow)) {
         if(this.canGrow(te)) {
            te.growthPoints = (int)((double)te.growthPoints + (double)te.calcGrowthRate() * 0.5D);
         }
      } else if(te.isBlockBelow(Block.slowSand) && crop.worldObj.rand.nextInt(300) == 0) {
         te.id = (short)IC2Crops.cropNetherWart.getId();
      }

   }
}
