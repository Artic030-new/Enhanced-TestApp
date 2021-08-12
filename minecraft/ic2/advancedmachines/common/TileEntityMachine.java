package ic2.advancedmachines.common;

import ic2.advancedmachines.common.TileEntityBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public abstract class TileEntityMachine extends TileEntityBlock implements IInventory {

   public ItemStack[] inventory;

   public TileEntityMachine(int inventorySize) {
      this.inventory = new ItemStack[inventorySize];
   }

   public int getSizeInventory() {
      return this.inventory.length;
   }

   public ItemStack getStackInSlot(int slot) {
      return this.inventory[slot];
   }

   public ItemStack decrStackSize(int index, int count) {
      if(this.inventory[index] != null) {
         ItemStack var3;
         if(this.inventory[index].stackSize <= count) {
            var3 = this.inventory[index];
            this.inventory[index] = null;
            return var3;
         } else {
            var3 = this.inventory[index].splitStack(count);
            if(this.inventory[index].stackSize == 0) {
               this.inventory[index] = null;
            }

            return var3;
         }
      } else {
         return null;
      }
   }

   public void setInventorySlotContents(int slot, ItemStack stack) {
      this.inventory[slot] = stack;
      if(stack != null && stack.stackSize > this.getInventoryStackLimit()) {
    	 stack.stackSize = this.getInventoryStackLimit();
      }

   }

   public int getInventoryStackLimit() {
      return 64;
   }

   public boolean isUseableByPlayer(EntityPlayer player) {
      return super.worldObj.getBlockTileEntity(super.xCoord, super.yCoord, super.zCoord) != this?false:player.getDistance((double)super.xCoord + 0.5D, (double)super.yCoord + 0.5D, (double)super.zCoord + 0.5D) <= 64.0D;
   }

   public abstract String getInvName();

   public void readFromNBT(NBTTagCompound tagCompound) {
      super.readFromNBT(tagCompound);
      NBTTagList var2 = tagCompound.getTagList("Items");
      this.inventory = new ItemStack[this.getSizeInventory()];

      for(int var3 = 0; var3 < var2.tagCount(); ++var3) {
         NBTTagCompound var4 = (NBTTagCompound)var2.tagAt(var3);
         byte var5 = var4.getByte("Slot");
         if(var5 >= 0 && var5 < this.inventory.length) {
            this.inventory[var5] = ItemStack.loadItemStackFromNBT(var4);
         }
      }

   }

   public void writeToNBT(NBTTagCompound tagCompound) {
      super.writeToNBT(tagCompound);
      NBTTagList var2 = new NBTTagList();

      for(int var3 = 0; var3 < this.inventory.length; ++var3) {
         if(this.inventory[var3] != null) {
            NBTTagCompound var4 = new NBTTagCompound();
            var4.setByte("Slot", (byte)var3);
            this.inventory[var3].writeToNBT(var4);
            var2.appendTag(var4);
         }
      }

      tagCompound.setTag("Items", var2);
   }

   public void updateEntity() {
      super.updateEntity();
   }

   public void openChest() {}

   public void closeChest() {}

   public ItemStack getStackInSlotOnClosing(int slot) {
      return null;
   }
}
