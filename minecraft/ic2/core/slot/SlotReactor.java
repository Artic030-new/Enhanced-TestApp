package ic2.core.slot;

import ic2.core.block.generator.tileentity.TileEntityNuclearReactor;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotReactor extends Slot {

   public SlotReactor(IInventory par1iInventory, int par2, int par3, int par4) {
      super(par1iInventory, par2, par3, par4);
   }

   public boolean isItemValid(ItemStack stack) {
      return stack == null?false:TileEntityNuclearReactor.isUsefulItem(stack);
   }
}
