package ic2.core.slot;

import ic2.core.block.machine.tileentity.TileEntityMatter;
import java.util.Iterator;
import java.util.Map.Entry;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotMatterScrap extends Slot {

   public SlotMatterScrap(IInventory par1iInventory, int par2, int par3, int par4) {
      super(par1iInventory, par2, par3, par4);
   }

   public boolean isItemValid(ItemStack stack) {
      if(stack == null) {
         return false;
      } else {
         Iterator i$ = TileEntityMatter.amplifiers.iterator();

         Entry amplifier;
         do {
            if(!i$.hasNext()) {
               return false;
            }

            amplifier = (Entry)i$.next();
         } while(!stack.isItemEqual((ItemStack)amplifier.getKey()));

         return true;
      }
   }
}
