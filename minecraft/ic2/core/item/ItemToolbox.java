package ic2.core.item;

import ic2.api.IBoxable;
import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.item.ItemIC2;
import ic2.core.util.StackUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class ItemToolbox extends ItemIC2 implements IBoxable {

   public ItemToolbox(int id, int index) {
      super(id, index);
      this.setMaxStackSize(1);
   }

   public int getIconFromDamage(int i) {
      return i == 0?super.iconIndex + 1:super.iconIndex;
   }

   public String getItemNameIS(ItemStack itemstack) {
      if(itemstack == null) {
         return "DAMN TMI CAUSING NPE\'s!";
      } else if(itemstack.getItemDamage() == 0) {
         return "item.itemToolbox";
      } else {
         ItemStack[] inventory = getInventoryFromNBT(itemstack);
         return inventory[0] == null?"item.itemToolbox":inventory[0].getItem().getItemNameIS(inventory[0]);
      }
   }

   public static ItemStack[] getInventoryFromNBT(ItemStack is) {
      ItemStack[] re = new ItemStack[8];
      if(is.getTagCompound() == null) {
         return re;
      } else {
         NBTTagCompound tag = is.getTagCompound();

         for(int i = 0; i < 8; ++i) {
            NBTTagCompound item = tag.getCompoundTag("box" + i);
            if(item != null) {
               re[i] = ItemStack.loadItemStackFromNBT(item);
            }
         }

         return re;
      }
   }

   public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer) {
      if(!IC2.platform.isSimulating()) {
         return itemstack;
      } else {
         if(itemstack.getItemDamage() == 0) {
            this.pack(itemstack, entityplayer);
         } else {
            this.unpack(itemstack, entityplayer);
         }

         if(!IC2.platform.isRendering()) {
            entityplayer.openContainer.detectAndSendChanges();
         }

         return itemstack;
      }
   }

   public boolean canBeStoredInToolbox(ItemStack wayne) {
      return false;
   }

   public void pack(ItemStack toolbox, EntityPlayer player) {
      ItemStack[] hotbar = player.inventory.mainInventory;
      NBTTagCompound mainbox = new NBTTagCompound();
      int boxcount = 0;

      for(int i = 0; i < 9; ++i) {
         if(hotbar[i] != null && hotbar[i] != toolbox) {
            if(hotbar[i].getItem() instanceof IBoxable) {
               if(!((IBoxable)hotbar[i].getItem()).canBeStoredInToolbox(hotbar[i])) {
                  continue;
               }
            } else if(hotbar[i].getMaxStackSize() > 1 && hotbar[i].itemID != Ic2Items.scaffold.itemID && hotbar[i].itemID != Ic2Items.miningPipe.itemID) {
               continue;
            }

            NBTTagCompound myBox = new NBTTagCompound();
            hotbar[i].writeToNBT(myBox);
            hotbar[i] = null;
            mainbox.setCompoundTag("box" + boxcount, myBox);
            ++boxcount;
         }
      }

      if(boxcount != 0) {
         toolbox.setTagCompound(mainbox);
         toolbox.setItemDamage(1);
      }
   }

   public void unpack(ItemStack toolbox, EntityPlayer player) {
      NBTTagCompound box = toolbox.getTagCompound();
      if(box != null) {
         ItemStack[] inventory = getInventoryFromNBT(toolbox);
         ItemStack[] hotbar = player.inventory.mainInventory;
         int inv = 0;

         for(int i = 0; i < inventory.length && inventory[inv] != null; ++i) {
            if(hotbar[i] == null) {
               hotbar[i] = inventory[inv];
               ++inv;
            }
         }

         while(inv < 8 && inventory[inv] != null) {
            StackUtil.dropAsEntity(player.worldObj, (int)player.posX, (int)player.posY, (int)player.posZ, inventory[inv]);
            ++inv;
         }

         toolbox.setTagCompound((NBTTagCompound)null);
         toolbox.setItemDamage(0);
      }
   }
}
