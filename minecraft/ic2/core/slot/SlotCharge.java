package ic2.core.slot;

import ic2.api.IElectricItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotCharge extends Slot {

   int tier;


   public SlotCharge(IInventory par1iInventory, int tier, int par2, int par3, int par4) {
      super(par1iInventory, par2, par3, par4);
      this.tier = Integer.MAX_VALUE;
      this.tier = tier;
   }

   public SlotCharge(IInventory par1iInventory, int par2, int par3, int par4) {
      this(par1iInventory, Integer.MAX_VALUE, par2, par3, par4);
   }

   public boolean isItemValid(ItemStack stack) {
      return stack == null?false:stack.getItem() instanceof IElectricItem && ((IElectricItem)stack.getItem()).getTier() <= this.tier;
   }
}
