package ic2.core.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.ICustomElectricItem;
import ic2.api.IElectricItem;
import ic2.core.IC2;
import ic2.core.item.ItemIC2;
import ic2.core.util.StackUtil;
import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public abstract class ElectricItem extends ItemIC2 implements IElectricItem {

   public int maxCharge;
   public int transferLimit = 100;
   public int tier = 1;


   public static int charge(ItemStack itemStack, int amount, int tier, boolean ignoreTransferLimit, boolean simulate) {
      if(!(itemStack.getItem() instanceof IElectricItem)) {
         return 0;
      } else {
         IElectricItem item = (IElectricItem)itemStack.getItem();
         if(item instanceof ICustomElectricItem) {
            return ((ICustomElectricItem)item).charge(itemStack, amount, tier, ignoreTransferLimit, simulate);
         } else if(amount >= 0 && itemStack.stackSize <= 1 && item.getTier() <= tier) {
            if(amount > item.getTransferLimit() && !ignoreTransferLimit) {
               amount = item.getTransferLimit();
            }

            NBTTagCompound nbtData = StackUtil.getOrCreateNbtData(itemStack);
            int charge = nbtData.getInteger("charge");
            if(amount > item.getMaxCharge() - charge) {
               amount = item.getMaxCharge() - charge;
            }

            charge += amount;
            if(!simulate) {
               nbtData.setInteger("charge", charge);
               itemStack.itemID = charge > 0?item.getChargedItemId():item.getEmptyItemId();
               if(itemStack.getItem() instanceof IElectricItem) {
                  item = (IElectricItem)itemStack.getItem();
                  if(itemStack.getMaxDamage() > 2) {
                     itemStack.setItemDamage(1 + (item.getMaxCharge() - charge) * (itemStack.getMaxDamage() - 2) / item.getMaxCharge());
                  } else {
                     itemStack.setItemDamage(0);
                  }
               } else {
                  itemStack.setItemDamage(0);
               }
            }

            return amount;
         } else {
            return 0;
         }
      }
   }

   public static int discharge(ItemStack itemStack, int amount, int tier, boolean ignoreTransferLimit, boolean simulate) {
      if(!(itemStack.getItem() instanceof IElectricItem)) {
         return 0;
      } else {
         IElectricItem item = (IElectricItem)itemStack.getItem();
         if(item instanceof ICustomElectricItem) {
            return ((ICustomElectricItem)item).discharge(itemStack, amount, tier, ignoreTransferLimit, simulate);
         } else if(amount >= 0 && itemStack.stackSize <= 1 && item.getTier() <= tier) {
            if(amount > item.getTransferLimit() && !ignoreTransferLimit) {
               amount = item.getTransferLimit();
            }

            NBTTagCompound nbtData = StackUtil.getOrCreateNbtData(itemStack);
            int charge = nbtData.getInteger("charge");
            if(amount > charge) {
               amount = charge;
            }

            if(!simulate) {
               charge -= amount;
               nbtData.setInteger("charge", charge);
               itemStack.itemID = charge > 0?item.getChargedItemId():item.getEmptyItemId();
               if(itemStack.getItem() instanceof IElectricItem) {
                  item = (IElectricItem)itemStack.getItem();
                  if(itemStack.getMaxDamage() > 2) {
                     itemStack.setItemDamage(1 + (item.getMaxCharge() - charge) * (itemStack.getMaxDamage() - 2) / item.getMaxCharge());
                  } else {
                     itemStack.setItemDamage(0);
                  }
               } else {
                  itemStack.setItemDamage(0);
               }
            }

            return amount;
         } else {
            return 0;
         }
      }
   }

   public static boolean canUse(ItemStack itemStack, int amount) {
      if(!(itemStack.getItem() instanceof IElectricItem)) {
         return false;
      } else {
         IElectricItem item = (IElectricItem)itemStack.getItem();
         if(item instanceof ICustomElectricItem) {
            return ((ICustomElectricItem)item).canUse(itemStack, amount);
         } else {
            NBTTagCompound nbtData = StackUtil.getOrCreateNbtData(itemStack);
            return nbtData.getInteger("charge") >= amount;
         }
      }
   }

   public static boolean use(ItemStack itemStack, int amount, EntityPlayer player) {
      if(IC2.platform.isSimulating() && itemStack.getItem() instanceof IElectricItem) {
         chargeFromArmor(itemStack, player);
         int transfer = discharge(itemStack, amount, Integer.MAX_VALUE, true, true);
         if(transfer == amount) {
            discharge(itemStack, amount, Integer.MAX_VALUE, true, false);
            chargeFromArmor(itemStack, player);
            return true;
         }
      }

      return false;
   }

   public static void chargeFromArmor(ItemStack itemStack, EntityPlayer player) {
      if(IC2.platform.isSimulating() && player != null && itemStack.getItem() instanceof IElectricItem) {
         boolean inventoryChanged = false;

         for(int i = 0; i < 4; ++i) {
            ItemStack armorItemStack = player.inventory.armorInventory[i];
            if(armorItemStack != null && armorItemStack.getItem() instanceof IElectricItem) {
               IElectricItem armorItem = (IElectricItem)armorItemStack.getItem();
               if(armorItem.canProvideEnergy() && armorItem.getTier() >= ((IElectricItem)itemStack.getItem()).getTier()) {
                  int transfer = charge(itemStack, Integer.MAX_VALUE, Integer.MAX_VALUE, true, true);
                  transfer = discharge(armorItemStack, transfer, Integer.MAX_VALUE, true, false);
                  if(transfer > 0) {
                     charge(itemStack, transfer, Integer.MAX_VALUE, true, false);
                     inventoryChanged = true;
                  }
               }
            }
         }

         if(inventoryChanged) {
            player.openContainer.detectAndSendChanges();
         }

      }
   }

   public ElectricItem(int id, int index) {
      super(id, index);
      this.setMaxDamage(27);
      this.setMaxStackSize(1);
   }

   public boolean canProvideEnergy() {
      return false;
   }

   public int getChargedItemId() {
      return super.itemID;
   }

   public int getEmptyItemId() {
      return super.itemID;
   }

   public int getMaxCharge() {
      return this.maxCharge;
   }

   public int getTier() {
      return this.tier;
   }

   public int getTransferLimit() {
      return this.transferLimit;
   }

   @SideOnly(Side.CLIENT)
   public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List itemList) {
      if(this.getChargedItemId() == super.itemID) {
         ItemStack charged = new ItemStack(this, 1);
         charge(charged, Integer.MAX_VALUE, Integer.MAX_VALUE, true, false);
         itemList.add(charged);
      }

      if(this.getEmptyItemId() == super.itemID) {
         itemList.add(new ItemStack(this, 1, this.getMaxDamage()));
      }

   }
}
