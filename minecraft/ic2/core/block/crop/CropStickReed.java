package ic2.core.block.crop;

import ic2.api.CropCard;
import ic2.api.TECrop;
import ic2.core.IC2;
import ic2.core.Ic2Items;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class CropStickReed extends CropCard {

   public String name() {
      return "Stickreed";
   }

   public String discoveredBy() {
      return "raa1337";
   }

   public int tier() {
      return 4;
   }

   public int stat(int n) {
      switch(n) {
      case 0:
         return 2;
      case 1:
         return 0;
      case 2:
         return 1;
      case 3:
         return 0;
      case 4:
         return 1;
      default:
         return 0;
      }
   }

   public String[] attributes() {
      return new String[]{"Reed", "Resin"};
   }

   public int getSpriteIndex(TECrop crop) {
      return crop.size + 27;
   }

   public boolean canGrow(TECrop crop) {
      return crop.size < 4;
   }

   public int weightInfluences(TECrop crop, float humidity, float nutrients, float air) {
      return (int)((double)humidity * 1.2D + (double)nutrients + (double)air * 0.8D);
   }

   public boolean canBeHarvested(TECrop crop) {
      return crop.size > 1;
   }

   public ItemStack getGain(TECrop crop) {
      return crop.size <= 3?new ItemStack(Item.reed, crop.size - 1):new ItemStack(Ic2Items.resin.getItem());
   }

   public byte getSizeAfterHarvest(TECrop crop) {
      return crop.size == 4?(byte)(3 - IC2.random.nextInt(3)):1;
   }

   public boolean onEntityCollision(TECrop crop, Entity entity) {
      return false;
   }

   public int growthDuration(TECrop crop) {
      return crop.size == 4?400:100;
   }
}
