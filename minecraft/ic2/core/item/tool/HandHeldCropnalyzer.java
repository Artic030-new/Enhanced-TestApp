package ic2.core.item.tool;

import ic2.api.CropCard;
import ic2.api.IElectricItem;
import ic2.core.ContainerIC2;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.ITickCallback;
import ic2.core.Ic2Items;
import ic2.core.item.ElectricItem;
import ic2.core.item.ItemCropSeed;
import ic2.core.item.tool.ContainerCropnalyzer;
import ic2.core.util.StackUtil;
import java.util.Random;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

public class HandHeldCropnalyzer implements IHasGui, ITickCallback {

   private final ItemStack itemStack;
   private final ItemStack[] inventory = new ItemStack[3];


   public HandHeldCropnalyzer(EntityPlayer entityPlayer, ItemStack itemStack) {
      this.itemStack = itemStack;
      if(IC2.platform.isSimulating()) {
         NBTTagCompound nbtTagCompound = StackUtil.getOrCreateNbtData(itemStack);
         nbtTagCompound.setInteger("uid", (new Random()).nextInt());
         NBTTagList nbtTagList = nbtTagCompound.getTagList("Items");

         for(int i = 0; i < nbtTagList.tagCount(); ++i) {
            NBTTagCompound nbtTagCompoundSlot = (NBTTagCompound)nbtTagList.tagAt(i);
            byte slot = nbtTagCompoundSlot.getByte("Slot");
            if(slot >= 0 && slot < this.inventory.length) {
               this.inventory[slot] = ItemStack.loadItemStackFromNBT(nbtTagCompoundSlot);
            }
         }

         IC2.addContinuousTickCallback(entityPlayer.worldObj, this);
      }

   }

   public int getSizeInventory() {
      return this.inventory.length;
   }

   public ItemStack getStackInSlot(int i) {
      return this.inventory[i];
   }

   public ItemStack decrStackSize(int slot, int amount) {
      if(this.inventory[slot] != null) {
         ItemStack ret;
         if(this.inventory[slot].stackSize <= amount) {
            ret = this.inventory[slot];
            this.inventory[slot] = null;
            return ret;
         } else {
            ret = this.inventory[slot].splitStack(amount);
            if(this.inventory[slot].stackSize == 0) {
               this.inventory[slot] = null;
            }

            return ret;
         }
      } else {
         return null;
      }
   }

   public void setInventorySlotContents(int slot, ItemStack itemStack) {
      this.inventory[slot] = itemStack;
      if(itemStack != null && itemStack.stackSize > this.getInventoryStackLimit()) {
         itemStack.stackSize = this.getInventoryStackLimit();
      }

   }

   public String getInvName() {
      return "Cropnalyzer";
   }

   public int getInventoryStackLimit() {
      return 64;
   }

   public void onInventoryChanged() {}

   public boolean isUseableByPlayer(EntityPlayer entityPlayer) {
      return true;
   }

   public void openChest() {}

   public void closeChest() {}

   public ItemStack getStackInSlotOnClosing(int var1) {
      return null;
   }

   public ContainerIC2 getGuiContainer(EntityPlayer entityPlayer) {
      return new ContainerCropnalyzer(entityPlayer, this);
   }

   public String getGuiClassName(EntityPlayer entityPlayer) {
      return "item.tool.GuiCropnalyzer";
   }

   public void onGuiClosed(EntityPlayer entityPlayer) {
      if(IC2.platform.isSimulating()) {
         IC2.removeContinuousTickCallback(entityPlayer.worldObj, this);
         NBTTagCompound nbtTagCompound = StackUtil.getOrCreateNbtData(this.itemStack);
         boolean dropItself = false;

         for(int nbtTagList = 0; nbtTagList < this.getSizeInventory(); ++nbtTagList) {
            if(this.inventory[nbtTagList] != null) {
               NBTTagCompound i = StackUtil.getOrCreateNbtData(this.inventory[nbtTagList]);
               if(nbtTagCompound.getInteger("uid") == i.getInteger("uid")) {
                  this.itemStack.stackSize = 1;
                  this.inventory[nbtTagList] = null;
                  dropItself = true;
                  break;
               }
            }
         }

         NBTTagList var8 = new NBTTagList();

         int var9;
         for(var9 = 0; var9 < this.inventory.length; ++var9) {
            if(this.inventory[var9] != null) {
               NBTTagCompound itemStackSlot = new NBTTagCompound();
               itemStackSlot.setByte("Slot", (byte)var9);
               this.inventory[var9].writeToNBT(itemStackSlot);
               var8.appendTag(itemStackSlot);
            }
         }

         nbtTagCompound.setTag("Items", var8);
         if(dropItself) {
            StackUtil.dropAsEntity(entityPlayer.worldObj, (int)entityPlayer.posX, (int)entityPlayer.posY, (int)entityPlayer.posZ, this.itemStack);
         } else {
            for(var9 = -1; var9 < entityPlayer.inventory.getSizeInventory(); ++var9) {
               ItemStack var10;
               if(var9 == -1) {
                  var10 = entityPlayer.inventory.getItemStack();
               } else {
                  var10 = entityPlayer.inventory.getStackInSlot(var9);
               }

               if(var10 != null) {
                  NBTTagCompound nbtTagCompoundSlot = var10.getTagCompound();
                  if(nbtTagCompoundSlot != null && nbtTagCompound.getInteger("uid") == nbtTagCompoundSlot.getInteger("uid")) {
                     this.itemStack.stackSize = 1;
                     if(var9 == -1) {
                        entityPlayer.inventory.setItemStack(this.itemStack);
                     } else {
                        entityPlayer.inventory.setInventorySlotContents(var9, this.itemStack);
                     }
                     break;
                  }
               }
            }
         }
      }

   }

   public void tickCallback(World world) {
      if(this.inventory[1] == null && this.inventory[0] != null && this.inventory[0].itemID == Ic2Items.cropSeed.itemID) {
         byte level = ItemCropSeed.getScannedFromStack(this.inventory[0]);
         if(level == 4) {
            this.inventory[1] = this.inventory[0];
            this.inventory[0] = null;
            return;
         }

         if(this.inventory[2] == null || !(this.inventory[2].getItem() instanceof IElectricItem)) {
            return;
         }

         int ned = this.energyForLevel(level);
         int got = ElectricItem.discharge(this.inventory[2], ned, 2, true, false);
         if(got < ned) {
            return;
         }

         ItemCropSeed.incrementScannedOfStack(this.inventory[0]);
         this.inventory[1] = this.inventory[0];
         this.inventory[0] = null;
      }

   }

   public int energyForLevel(int i) {
      switch(i) {
      case 1:
         return 90;
      case 2:
         return 900;
      case 3:
         return 9000;
      default:
         return 10;
      }
   }

   public CropCard crop() {
      return CropCard.getCrop(ItemCropSeed.getIdFromStack(this.inventory[1]));
   }

   public int getScannedLevel() {
      return this.inventory[1] != null && this.inventory[1].getItem() == Ic2Items.cropSeed.getItem()?ItemCropSeed.getScannedFromStack(this.inventory[1]):-1;
   }

   public String getSeedName() {
      return this.crop().name();
   }

   public String getSeedTier() {
      switch(this.crop().tier()) {
      case 1:
         return "I";
      case 2:
         return "II";
      case 3:
         return "III";
      case 4:
         return "IV";
      case 5:
         return "V";
      case 6:
         return "VI";
      case 7:
         return "VII";
      case 8:
         return "VIII";
      case 9:
         return "IX";
      case 10:
         return "X";
      case 11:
         return "XI";
      case 12:
         return "XII";
      case 13:
         return "XIII";
      case 14:
         return "XIV";
      case 15:
         return "XV";
      case 16:
         return "XVI";
      default:
         return "0";
      }
   }

   public String getSeedDiscovered() {
      return this.crop().discoveredBy();
   }

   public String getSeedDesc(int i) {
      return this.crop().desc(i);
   }

   public int getSeedGrowth() {
      return ItemCropSeed.getGrowthFromStack(this.inventory[1]);
   }

   public int getSeedGain() {
      return ItemCropSeed.getGainFromStack(this.inventory[1]);
   }

   public int getSeedResistence() {
      return ItemCropSeed.getResistanceFromStack(this.inventory[1]);
   }

   public boolean matchesUid(int uid) {
      NBTTagCompound nbtTagCompound = StackUtil.getOrCreateNbtData(this.itemStack);
      return nbtTagCompound.getInteger("uid") == uid;
   }
}
