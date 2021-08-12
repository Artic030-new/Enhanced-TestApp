package ic2.core.item;

import ic2.api.IBoxable;
import ic2.api.IElectricItem;
import ic2.core.item.ElectricItem;
import ic2.core.item.ItemIC2;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemBatterySU extends ItemIC2 implements IBoxable {

   public int capacity;
   public int tier;


   public ItemBatterySU(int id, int sprite, int capacity, int tier) {
      super(id, sprite);
      this.capacity = capacity;
      this.tier = tier;
   }

   public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer) {
      if(itemstack.itemID != super.itemID) {
         return itemstack;
      } else {
         int energy = this.capacity;

         for(int i = 0; i < 9 && energy > 0; ++i) {
            ItemStack stack = entityplayer.inventory.mainInventory[i];
            if(stack != null && Item.itemsList[stack.itemID] instanceof IElectricItem && stack != itemstack) {
               energy -= ElectricItem.charge(stack, energy, this.tier, true, false);
            }
         }

         if(energy != this.capacity) {
            --itemstack.stackSize;
         }

         return itemstack;
      }
   }

   public boolean canBeStoredInToolbox(ItemStack itemstack) {
      return true;
   }
}
