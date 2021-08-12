package cpw.mods.ironchest;

import cpw.mods.ironchest.IronChestType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerIronChestBase extends Container {

   private IronChestType type;
   private EntityPlayer player;
   private IInventory chest;

   public ContainerIronChestBase(IInventory playerInventory, IInventory chestInventory, IronChestType type, int xSize, int ySize) {
      this.chest = chestInventory;
      this.player = ((InventoryPlayer)playerInventory).player;
      this.type = type;
      chestInventory.openChest();
      this.layoutContainer(playerInventory, chestInventory, type, xSize, ySize);
   }

   public boolean canInteractWith(EntityPlayer player) {
      return this.chest.isUseableByPlayer(player);
   }

   public ItemStack transferStackInSlot(EntityPlayer p, int i) {
      ItemStack itemstack = null;
      Slot slot = (Slot)super.inventorySlots.get(i);
      if(slot != null && slot.getHasStack()) {
         ItemStack itemstack1 = slot.getStack();
         itemstack = itemstack1.copy();
         if(i < this.type.size) {
            if(!this.mergeItemStack(itemstack1, this.type.size, super.inventorySlots.size(), true)) {
               return null;
            }
         } else if(!this.mergeItemStack(itemstack1, 0, this.type.size, false)) {
            return null;
         }

         if(itemstack1.stackSize == 0) {
            slot.putStack((ItemStack)null);
         } else {
            slot.onSlotChanged();
         }
      }

      return itemstack;
   }

   public void onCraftGuiClosed(EntityPlayer entityplayer) {
      super.onCraftGuiClosed(entityplayer);
      this.chest.closeChest();
   }

   protected void layoutContainer(IInventory playerInventory, IInventory chestInventory, IronChestType type, int xSize, int ySize) {
      int leftCol;
      int hotbarSlot;
      for(leftCol = 0; leftCol < type.getRowCount(); ++leftCol) {
         for(hotbarSlot = 0; hotbarSlot < type.getRowLength(); ++hotbarSlot) {
            this.addSlotToContainer(new Slot(chestInventory, hotbarSlot + leftCol * type.getRowLength(), 12 + hotbarSlot * 18, 8 + leftCol * 18));
         }
      }

      leftCol = (xSize - 162) / 2 + 1;

      for(hotbarSlot = 0; hotbarSlot < 3; ++hotbarSlot) {
         for(int playerInvCol = 0; playerInvCol < 9; ++playerInvCol) {
            this.addSlotToContainer(new Slot(playerInventory, playerInvCol + hotbarSlot * 9 + 9, leftCol + playerInvCol * 18, ySize - (4 - hotbarSlot) * 18 - 10));
         }
      }

      for(hotbarSlot = 0; hotbarSlot < 9; ++hotbarSlot) {
         this.addSlotToContainer(new Slot(playerInventory, hotbarSlot, leftCol + hotbarSlot * 18, ySize - 24));
      }

   }

   public EntityPlayer getPlayer() {
      return this.player;
   }
}
