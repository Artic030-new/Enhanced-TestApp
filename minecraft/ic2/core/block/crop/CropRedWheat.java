package ic2.core.block.crop;

import ic2.api.CropCard;
import ic2.api.TECrop;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class CropRedWheat extends CropCard {

   public String name() {
      return "Redwheat";
   }

   public String discoveredBy() {
      return "raa1337";
   }

   public int tier() {
      return 6;
   }

   public int stat(int n) {
      switch(n) {
      case 0:
         return 3;
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
      return new String[]{"Red", "Redstone", "Wheat"};
   }

   public int getSpriteIndex(TECrop crop) {
      return crop.size == 7?27:crop.size + 1;
   }

   public boolean canGrow(TECrop crop) {
      return crop.size < 7 && crop.getLightLevel() <= 10 && crop.getLightLevel() >= 5;
   }

   public boolean canBeHarvested(TECrop crop) {
      return crop.size == 7;
   }

   public float dropGainChance() {
      return 0.5F;
   }

   public ItemStack getGain(TECrop crop) {
      return !crop.worldObj.isBlockGettingPowered(crop.xCoord, crop.yCoord, crop.zCoord) && !crop.worldObj.rand.nextBoolean()?new ItemStack(Item.wheat, 1):new ItemStack(Item.redstone, 1);
   }

   public boolean emitRedstone(TECrop crop) {
      return crop.size == 7;
   }

   public int getEmittedLight(TECrop crop) {
      return crop.size == 7?7:0;
   }

   public int growthDuration(TECrop crop) {
      return 600;
   }

   public byte getSizeAfterHarvest(TECrop crop) {
      return (byte)2;
   }
}
