package ic2.core.slot;

import ic2.api.IElectricItem;
import ic2.core.item.ItemBatterySU;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class SlotDischarge extends Slot {

   public int tier = Integer.MAX_VALUE;


   public SlotDischarge(IInventory par1iInventory, int tier, int par2, int par3, int par4) {
      super(par1iInventory, par2, par3, par4);
      this.tier = tier;
   }

   public SlotDischarge(IInventory par1iInventory, int par2, int par3, int par4) {
      super(par1iInventory, par2, par3, par4);
   }

   public boolean isItemValid(ItemStack stack) {
      if(stack == null) {
         return false;
      } else if(stack.itemID != Item.redstone.itemID && !(stack.getItem() instanceof ItemBatterySU)) {
         if(stack.getItem() instanceof IElectricItem) {
            IElectricItem iee = (IElectricItem)stack.getItem();
            if(iee.canProvideEnergy() && iee.getTier() <= this.tier) {
               return true;
            }
         }

         return false;
      } else {
         return true;
      }
   }
}
