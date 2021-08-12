package ic2.core;

import ic2.core.IC2;
import ic2.core.Ic2Items;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class CreativeTabIC2 extends CreativeTabs {

   private static ItemStack laser;
   private static ItemStack a;
   private static ItemStack b;
   private static ItemStack z;
   private int ticker;


   public CreativeTabIC2() {
      super("IC2");
   }

   public ItemStack getIconItemStack() {
      if(laser == null) {
         laser = Ic2Items.miningLaser.copy();
      }

      if(IC2.seasonal) {
         if(a == null) {
            a = new ItemStack(Item.skull, 1, 2);
         }

         if(b == null) {
            b = new ItemStack(Item.skull, 1, 0);
         }

         if(z == null) {
            z = Ic2Items.nanoBodyarmor.copy();
         }

         if(++this.ticker >= 5000) {
            this.ticker = 0;
         }

         return this.ticker < 2500?laser:(this.ticker < 3000?a:(this.ticker < 4500?b:z));
      } else {
         return laser;
      }
   }
}
