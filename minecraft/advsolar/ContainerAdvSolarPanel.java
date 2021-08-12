package advsolar;

import advsolar.TileEntitySolarPanel;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerAdvSolarPanel extends Container {

   private TileEntitySolarPanel tileentity;
   private int storage = 0;
   private int fuel = 0;
   private boolean sunIsUp;
   private boolean skyIsVisible;
   private int generating;

   public ContainerAdvSolarPanel(InventoryPlayer inventoryplayer, TileEntitySolarPanel tileentitysolarpanel) {
      this.tileentity = tileentitysolarpanel;

      int j;
      for(j = 0; j < 4; ++j) {
         this.addSlotToContainer(new Slot(this.tileentity, j, 17 + j * 18, 59));
      }

      for(j = 0; j < 3; ++j) {
         for(int k = 0; k < 9; ++k) {
            this.addSlotToContainer(new Slot(inventoryplayer, k + j * 9 + 9, 17 + k * 18, 86 + j * 18));
         }
      }

      for(j = 0; j < 9; ++j) {
         this.addSlotToContainer(new Slot(inventoryplayer, j, 17 + j * 18, 144));
      }

   }

   public void addCraftingToCrafters(ICrafting icrafting) {
      super.addCraftingToCrafters(icrafting);
      icrafting.sendProgressBarUpdate(this, 0, this.tileentity.sunIsUp?1:0);
      icrafting.sendProgressBarUpdate(this, 1, this.tileentity.skyIsVisible?1:0);
      icrafting.sendProgressBarUpdate(this, 2, this.tileentity.generating);
      icrafting.sendProgressBarUpdate(this, 3, this.tileentity.storage & '\uffff');
      icrafting.sendProgressBarUpdate(this, 4, this.tileentity.storage >>> 16);
   }

   public void detectAndSendChanges() {
      super.detectAndSendChanges();

      for(int i = 0; i < super.crafters.size(); ++i) {
         ICrafting icrafting = (ICrafting)super.crafters.get(i);
         icrafting.sendProgressBarUpdate(this, 0, this.tileentity.sunIsUp?1:0);
         icrafting.sendProgressBarUpdate(this, 1, this.tileentity.skyIsVisible?1:0);
         icrafting.sendProgressBarUpdate(this, 2, this.tileentity.generating);
         icrafting.sendProgressBarUpdate(this, 3, this.tileentity.storage & '\uffff');
         icrafting.sendProgressBarUpdate(this, 4, this.tileentity.storage >>> 16);
      }

      this.sunIsUp = this.tileentity.sunIsUp;
      this.skyIsVisible = this.tileentity.skyIsVisible;
      this.generating = this.tileentity.generating;
      this.storage = this.tileentity.storage;
   }

   public void updateProgressBar(int key, int value) {
      if(key == 0) {
         this.tileentity.sunIsUp = value != 0;
      }

      if(key == 1) {
         this.tileentity.skyIsVisible = value != 0;
      }

      if(key == 2) {
         this.tileentity.generating = value;
      }

      if(key == 3) {
         this.tileentity.storage = this.tileentity.storage & -65536 | value;
      }

      if(key == 4) {
         this.tileentity.storage = this.tileentity.storage & '\uffff' | value << 16;
      }

   }

   public ItemStack transferStackInSlot(EntityPlayer player, int slot) {
      ItemStack stack = null;
      Slot slotObject = (Slot)super.inventorySlots.get(slot);
      if(slotObject != null && slotObject.getHasStack()) {
         ItemStack stackInSlot = slotObject.getStack();
         stack = stackInSlot.copy();
         if(slot >= 0 && slot <= 3) {
            if(!this.mergeItemStack(stackInSlot, 4, 40, true)) {
               return null;
            }
         } else if(slot >= 4 && slot < 31) {
            if(!this.mergeItemStack(stackInSlot, 0, 4, false) && !this.mergeItemStack(stackInSlot, 31, 40, false)) {
               return null;
            }
         } else if(slot >= 31 && slot < 39) {
            if(!this.mergeItemStack(stackInSlot, 0, 30, false)) {
               return null;
            }
         } else if(!this.mergeItemStack(stackInSlot, 0, 30, false)) {
            return null;
         }

         if(stackInSlot.stackSize == 0) {
            slotObject.putStack((ItemStack)null);
         } else {
            slotObject.onSlotChanged();
         }

         if(stack.stackSize == stackInSlot.stackSize) {
            return null;
         }

         slotObject.onPickupFromSlot(player, stackInSlot);
      }

      return stack;
   }

   public boolean canInteractWith(EntityPlayer player) {
      return this.tileentity.isUseableByPlayer(player);
   }
}
