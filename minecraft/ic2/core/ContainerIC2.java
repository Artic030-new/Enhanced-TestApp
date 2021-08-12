package ic2.core;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public abstract class ContainerIC2 extends Container {

   public abstract int guiInventorySize();

   public abstract int getInput();

   public int firstEmptyFrom(int start, int end, IInventory inv) {
      for(int i = start; i <= end; ++i) {
         if(inv.getStackInSlot(i) == null) {
            return i;
         }
      }

      return -1;
   }

   public final ItemStack transferStackInSlot(EntityPlayer player, int i) {
      ItemStack itemstack = null;
      Slot slot = (Slot)super.inventorySlots.get(i);
      if(slot != null && slot.getHasStack()) {
         ItemStack itemstack1 = slot.getStack();
         itemstack = itemstack1.copy();
         if(i < this.guiInventorySize()) {
            this.transferToSlots(itemstack1, this.guiInventorySize(), super.inventorySlots.size(), false);
         } else if(i >= this.guiInventorySize() && i < super.inventorySlots.size()) {
            boolean transferDone = false;

            for(int j = 0; j < this.guiInventorySize(); ++j) {
               Slot slot2 = this.getSlot(j);
               if(slot2 != null && slot2.isItemValid(itemstack1) && this.transferToSlots(itemstack1, j, j + 1, false)) {
                  transferDone = true;
                  break;
               }
            }
         }

         if(itemstack1.stackSize == 0) {
            slot.putStack((ItemStack)null);
         } else {
            slot.onSlotChanged();
         }

         if(itemstack1.stackSize == itemstack.stackSize) {
            return null;
         }

         slot.onPickupFromSlot(player, itemstack1);
      }

      return itemstack;
   }

   public boolean transferToSlots(ItemStack stack, int startIndex, int endIndex, boolean lookBackwards) {
      return this.mergeItemStack(stack, startIndex, endIndex, lookBackwards);
   }

   public abstract void updateProgressBar(int var1, int var2);

   public abstract boolean canInteractWith(EntityPlayer var1);
}
