package ic2.core.slot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotDisplay extends Slot {

   public SlotDisplay(IInventory par1iInventory, int par2, int par3, int par4) {
      super(par1iInventory, par2, par3, par4);
   }

   public boolean isItemValid(ItemStack stack) {
      return false;
   }

   public void onPickupFromSlot(EntityPlayer player, ItemStack stack) {}

   public boolean canTakeStack(EntityPlayer player) {
      return false;
   }

   public ItemStack decrStackSize(int par1) {
      return this.getStack();
   }
}
