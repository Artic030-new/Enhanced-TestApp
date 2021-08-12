package ic2.core.slot;

import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class SlotCustom extends Slot {

   private Object[] items;


   public SlotCustom(IInventory iinventory, Object[] items, int i, int j, int k) {
      super(iinventory, i, j, k);
      this.items = items;
   }

   public boolean isItemValid(ItemStack itemstack) {
      if(itemstack == null) {
         return false;
      } else {
         Object[] arr$ = this.items;
         int len$ = arr$.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            Object o = arr$[i$];
            if(o != null) {
               if(o instanceof Class) {
                  if(itemstack.itemID < Block.blocksList.length && Block.blocksList[itemstack.itemID] != null && ((Class)o).isAssignableFrom(Block.blocksList[itemstack.itemID].getClass())) {
                     return true;
                  }

                  if((itemstack.itemID >= Block.blocksList.length || Block.blocksList[itemstack.itemID] == null) && Item.itemsList[itemstack.itemID] != null && ((Class)o).isAssignableFrom(Item.itemsList[itemstack.itemID].getClass())) {
                     return true;
                  }
               } else if(o instanceof ItemStack) {
                  if(itemstack.getItemDamage() == -1 && itemstack.itemID == ((ItemStack)o).itemID) {
                     return true;
                  }

                  if(itemstack.isItemEqual((ItemStack)o)) {
                     return true;
                  }
               } else {
                  if(o instanceof Block && itemstack.itemID == ((Block)o).blockID) {
                     return true;
                  }

                  if(o instanceof Item && itemstack.itemID == ((Item)o).itemID) {
                     return true;
                  }

                  if(o instanceof Integer && itemstack.itemID == ((Integer)o).intValue()) {
                     return true;
                  }
               }
            }
         }

         return false;
      }
   }
}
