package ic2.core.block.machine.tileentity;

import ic2.core.IC2;
import ic2.core.block.TileEntityBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public abstract class TileEntityMachine extends TileEntityBlock implements IInventory {

   public ItemStack[] inventory;


   public TileEntityMachine(int slotcount) {
      this.inventory = new ItemStack[slotcount];
   }

   public int getSizeInventory() {
      return this.inventory.length;
   }

   public ItemStack getStackInSlot(int i) {
      return this.inventory[i];
   }

   public ItemStack decrStackSize(int i, int j) {
      if(this.inventory[i] != null) {
         ItemStack itemstack1;
         if(this.inventory[i].stackSize <= j) {
            itemstack1 = this.inventory[i];
            this.inventory[i] = null;
            return itemstack1;
         } else {
            itemstack1 = this.inventory[i].splitStack(j);
            if(this.inventory[i].stackSize == 0) {
               this.inventory[i] = null;
            }

            return itemstack1;
         }
      } else {
         return null;
      }
   }

   public void setInventorySlotContents(int i, ItemStack itemstack) {
      this.inventory[i] = itemstack;
      if(itemstack != null && itemstack.stackSize > this.getInventoryStackLimit()) {
         itemstack.stackSize = this.getInventoryStackLimit();
      }

   }

   public int getInventoryStackLimit() {
      return 64;
   }

   public boolean isUseableByPlayer(EntityPlayer entityplayer) {
      return super.worldObj.getBlockTileEntity(super.xCoord, super.yCoord, super.zCoord) != this?false:entityplayer.getDistance((double)super.xCoord + 0.5D, (double)super.yCoord + 0.5D, (double)super.zCoord + 0.5D) <= 64.0D;
   }

   public abstract String getInvName();

   public void readFromNBT(NBTTagCompound nbttagcompound) {
      super.readFromNBT(nbttagcompound);
      NBTTagList nbttaglist = nbttagcompound.getTagList("Items");
      this.inventory = new ItemStack[this.getSizeInventory()];

      for(int i = 0; i < nbttaglist.tagCount(); ++i) {
         NBTTagCompound nbttagcompound1 = (NBTTagCompound)nbttaglist.tagAt(i);
         byte byte0 = nbttagcompound1.getByte("Slot");
         if(byte0 >= 0 && byte0 < this.inventory.length) {
            this.inventory[byte0] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
         }
      }

   }

   public void writeToNBT(NBTTagCompound nbttagcompound) {
      super.writeToNBT(nbttagcompound);
      NBTTagList nbttaglist = new NBTTagList();

      for(int i = 0; i < this.inventory.length; ++i) {
         if(this.inventory[i] != null) {
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();
            nbttagcompound1.setByte("Slot", (byte)i);
            this.inventory[i].writeToNBT(nbttagcompound1);
            nbttaglist.appendTag(nbttagcompound1);
         }
      }

      nbttagcompound.setTag("Items", nbttaglist);
   }

   public boolean canUpdate() {
      return IC2.platform.isSimulating();
   }

   public void updateEntity() {
      super.updateEntity();
   }

   public void openChest() {}

   public void closeChest() {}

   public ItemStack getStackInSlotOnClosing(int var1) {
      return null;
   }
}
