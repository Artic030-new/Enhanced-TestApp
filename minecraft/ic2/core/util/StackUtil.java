package ic2.core.util;

import ic2.api.Direction;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.world.World;
import net.minecraftforge.common.ISidedInventory;

public final class StackUtil {

   private static final Direction[] directions = Direction.values();


   public static void distributeDrop(TileEntity source, List itemStacks) {
      Direction[] i$ = directions;
      int itemStack = i$.length;

      for(int i$1 = 0; i$1 < itemStack; ++i$1) {
         Direction direction = i$[i$1];
         if(itemStacks.isEmpty()) {
            break;
         }

         TileEntity target = direction.applyToTileEntity(source);
         if(target instanceof IInventory) {
            Object inventory = (IInventory)target;
            if(((IInventory)inventory).getSizeInventory() >= 6 && (!(inventory instanceof ISidedInventory) || ((ISidedInventory)inventory).getSizeInventorySide(direction.toForgeDirection()) >= 6)) {
               if(target instanceof TileEntityChest) {
                  Direction[] it = directions;
                  int itemStackSource = it.length;

                  for(int i$2 = 0; i$2 < itemStackSource; ++i$2) {
                     Direction direction2 = it[i$2];
                     if(direction2 != Direction.YN && direction2 != Direction.YP) {
                        TileEntity target2 = direction2.applyToTileEntity(target);
                        if(target2 instanceof TileEntityChest) {
                           inventory = new InventoryLargeChest("", (IInventory)inventory, (IInventory)target2);
                           break;
                        }
                     }
                  }
               }

               Iterator var15 = itemStacks.iterator();

               while(var15.hasNext()) {
                  ItemStack var16 = (ItemStack)var15.next();
                  if(var16 != null) {
                     putInInventory((IInventory)inventory, var16);
                     if(var16.stackSize == 0) {
                        var15.remove();
                     }
                  }
               }
            }
         }
      }

      Iterator var13 = itemStacks.iterator();

      while(var13.hasNext()) {
         ItemStack var14 = (ItemStack)var13.next();
         dropAsEntity(source.worldObj, source.xCoord, source.yCoord, source.zCoord, var14);
      }

      itemStacks.clear();
   }

   public static ItemStack getFromInventory(IInventory inventory, ItemStack itemStackDestination) {
      ItemStack ret = null;
      int toTransfer = itemStackDestination.stackSize;
      itemStackDestination.stackSize = 0;

      for(int i = 0; i < inventory.getSizeInventory(); ++i) {
         ItemStack itemStack = inventory.getStackInSlot(i);
         if(itemStack != null && isStackEqual(itemStack, itemStackDestination)) {
            if(ret == null) {
               ret = itemStack.copy();
               ret.stackSize = 0;
            }

            int transfer = Math.min(toTransfer, itemStack.stackSize);
            toTransfer -= transfer;
            itemStack.stackSize -= transfer;
            itemStackDestination.stackSize += transfer;
            ret.stackSize += transfer;
            if(itemStack.stackSize == 0) {
               inventory.setInventorySlotContents(i, (ItemStack)null);
            }

            if(toTransfer == 0) {
               return ret;
            }
         }
      }

      return null;
   }

   public static boolean putInInventory(IInventory inventory, ItemStack itemStackSource) {
      int i;
      ItemStack itemStack;
      int transfer;
      for(i = 0; i < inventory.getSizeInventory(); ++i) {
         itemStack = inventory.getStackInSlot(i);
         if(itemStack != null && itemStack.isItemEqual(itemStackSource)) {
            transfer = Math.min(itemStackSource.stackSize, itemStack.getMaxStackSize() - itemStack.stackSize);
            itemStack.stackSize += transfer;
            itemStackSource.stackSize -= transfer;
            if(itemStackSource.stackSize == 0) {
               return true;
            }
         }
      }

      for(i = 0; i < inventory.getSizeInventory(); ++i) {
         itemStack = inventory.getStackInSlot(i);
         if(itemStack == null) {
            transfer = Math.min(itemStackSource.stackSize, itemStackSource.getMaxStackSize());
            inventory.setInventorySlotContents(i, new ItemStack(itemStackSource.itemID, transfer, itemStackSource.getItemDamage()));
            itemStackSource.stackSize -= transfer;
            if(itemStackSource.stackSize == 0) {
               return true;
            }
         }
      }

      return false;
   }

   public static void dropAsEntity(World world, int x, int y, int z, ItemStack itemStack) {
      if(itemStack != null) {
         double f = 0.7D;
         double dx = (double)world.rand.nextFloat() * f + (1.0D - f) * 0.5D;
         double dy = (double)world.rand.nextFloat() * f + (1.0D - f) * 0.5D;
         double dz = (double)world.rand.nextFloat() * f + (1.0D - f) * 0.5D;
         EntityItem entityItem = new EntityItem(world, (double)x + dx, (double)y + dy, (double)z + dz, itemStack.copy());
         entityItem.delayBeforeCanPickup = 10;
         world.spawnEntityInWorld(entityItem);
      }
   }

   public static ItemStack copyWithSize(ItemStack itemStack, int newSize) {
      ItemStack ret = itemStack.copy();
      ret.stackSize = newSize;
      return ret;
   }

   public static NBTTagCompound getOrCreateNbtData(ItemStack itemStack) {
      NBTTagCompound ret = itemStack.getTagCompound();
      if(ret == null) {
         ret = new NBTTagCompound();
         itemStack.setTagCompound(ret);
      }

      return ret;
   }

   public static boolean isStackEqual(ItemStack stack1, ItemStack stack2) {
      return stack1 != null && stack2 != null && stack1.itemID == stack2.itemID && (stack1.getItem().isDamageable() || stack1.getItemDamage() == stack2.getItemDamage());
   }

}
