package ic2.core.block.crop;

import ic2.api.CropCard;
import ic2.api.TECrop;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class CropColorFlower extends CropCard {

   public String name;
   public String[] attributes;
   public int sprite;
   public int color;


   public CropColorFlower(String n, String[] a, int s, int c) {
      this.name = n;
      this.attributes = a;
      this.sprite = s;
      this.color = c;
   }

   public String discoveredBy() {
      return !this.name.equals("Dandelion") && !this.name.equals("Rose")?"Alblaka":"Notch";
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
         return 1;
      case 1:
         return 1;
      case 2:
         return 0;
      case 3:
         return 5;
      case 4:
         return 1;
      default:
         return 0;
      }
   }

   public String[] attributes() {
      return this.attributes;
   }

   public int getSpriteIndex(TECrop crop) {
      switch(crop.size) {
      case 1:
         return 12;
      case 2:
         return 13;
      case 3:
         return 14;
      case 4:
         return this.sprite;
      default:
         return 0;
      }
   }

   public boolean canGrow(TECrop crop) {
      return crop.size <= 3 && crop.getLightLevel() >= 12;
   }

   public boolean canBeHarvested(TECrop crop) {
      return crop.size == 4;
   }

   public ItemStack getGain(TECrop crop) {
      return new ItemStack(Item.dyePowder, 1, this.color);
   }

   public byte getSizeAfterHarvest(TECrop crop) {
      return (byte)3;
   }

   public int growthDuration(TECrop crop) {
      return crop.size == 3?600:400;
   }
}
