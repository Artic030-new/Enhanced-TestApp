package ic2.advancedmachines.common;

import ic2.advancedmachines.common.AdvancedMachines;
import ic2.advancedmachines.common.TileEntityRotaryMacerator;
import ic2.api.IElectricItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotFurnace;
import net.minecraft.item.ItemStack;

public class ContainerRotaryMacerator extends Container {

   public TileEntityRotaryMacerator tileentity;
   public int progress = 0;
   public int energy = 0;
   public int speed = 0;

   public ContainerRotaryMacerator(InventoryPlayer inv, TileEntityRotaryMacerator tE) {
      this.tileentity = tE;
      this.addSlotToContainer(new Slot(tE, 0, 56, 53));
      this.addSlotToContainer(new Slot(tE, 1, 56, 17));
      this.addSlotToContainer(new SlotFurnace(inv.player, tE, 2, 115, 25));
      this.addSlotToContainer(new SlotFurnace(inv.player, tE, 3, 115, 46));
      this.addSlotToContainer(new Slot(tE, 4, 152, 6));
      this.addSlotToContainer(new Slot(tE, 5, 152, 24));
      this.addSlotToContainer(new Slot(tE, 6, 152, 42));
      this.addSlotToContainer(new Slot(tE, 7, 152, 60));
      this.addSlotToContainer(new Slot(tE, 8, 75, 17));

      int var3;
      for(var3 = 0; var3 < 3; ++var3) {
         for(int var4 = 0; var4 < 9; ++var4) {
            this.addSlotToContainer(new Slot(inv, var4 + var3 * 9 + 9, 8 + var4 * 18, 84 + var3 * 18));
         }
      }

      for(var3 = 0; var3 < 9; ++var3) {
         this.addSlotToContainer(new Slot(inv, var3, 8 + var3 * 18, 142));
      }

   }

   public void detectAndSendChanges() {
      super.detectAndSendChanges();

      for(int var1 = 0; var1 < super.crafters.size(); ++var1) {
         ICrafting var2 = (ICrafting)super.crafters.get(var1);
         if(this.progress != this.tileentity.progress) {
            var2.sendProgressBarUpdate(this, 0, this.tileentity.progress);
         }

         if(this.energy != this.tileentity.energy) {
            var2.sendProgressBarUpdate(this, 1, this.tileentity.energy & '\uffff');
            var2.sendProgressBarUpdate(this, 2, this.tileentity.energy >>> 16);
         }

         if(this.speed != this.tileentity.speed) {
            var2.sendProgressBarUpdate(this, 3, this.tileentity.speed);
         }
      }

      this.progress = this.tileentity.progress;
      this.energy = this.tileentity.energy;
      this.speed = this.tileentity.speed;
   }

   public void updateProgressBar(int key, int value) {
      switch(key) {
      case 0:
         this.tileentity.progress = (short)value;
         break;
      case 1:
         this.tileentity.energy = this.tileentity.energy & -65536 | value;
         break;
      case 2:
         this.tileentity.energy = this.tileentity.energy & '\uffff' | value << 16;
         break;
      case 3:
         this.tileentity.speed = (short)value;
      }

   }

   public ItemStack slotClick(int slot, int button, int modifier, EntityPlayer player) {
      if(slot == 8) {
         ItemStack slotItem = ((Slot)super.inventorySlots.get(8)).getStack();
         if(slotItem != null && this.tileentity.supplementedItemsLeft != 0) {
            --slotItem.stackSize;
            if(slotItem.stackSize < 1) {
               ((Slot)super.inventorySlots.get(8)).putStack((ItemStack)null);
            }

            this.tileentity.supplementedItemsLeft = 0;
         }
      }

      return super.slotClick(slot, button, modifier, player);
   }

   public ItemStack transferStackInSlot(EntityPlayer player, int slot) {
      ItemStack tempStack = null;
      Slot localslot = (Slot)super.inventorySlots.get(slot);
      if(localslot != null && localslot.getHasStack()) {
         ItemStack localstack = localslot.getStack();
         tempStack = localstack.copy();
         if(slot < 9) {
            this.mergeItemStack(localstack, 9, 38, false);
         } else if(localstack.itemID != AdvancedMachines.overClockerStack.itemID && localstack.itemID != AdvancedMachines.transformerStack.itemID && localstack.itemID != AdvancedMachines.energyStorageUpgradeStack.itemID) {
            if(localstack.getItem() instanceof IElectricItem) {
               if(((Slot)super.inventorySlots.get(0)).getStack() == null) {
                  ((Slot)super.inventorySlots.get(0)).putStack(localstack);
                  localslot.putStack((ItemStack)null);
               }
            } else {
               this.mergeItemStack(localstack, 1, 2, false);
            }
         } else {
            this.mergeItemStack(localstack, 4, 7, false);
         }

         if(localstack.stackSize == 0) {
            localslot.putStack((ItemStack)null);
         } else {
            localslot.onSlotChanged();
         }

         if(localstack.stackSize == tempStack.stackSize) {
            return null;
         }

         localslot.putStack(localstack);
      }

      return tempStack;
   }

   public boolean canInteractWith(EntityPlayer player) {
      return this.tileentity.isUseableByPlayer(player);
   }
}
