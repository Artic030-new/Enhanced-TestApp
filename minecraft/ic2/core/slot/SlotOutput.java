package ic2.core.slot;

import ic2.core.IC2;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotOutput extends Slot {

   private EntityPlayer player;


   public SlotOutput(EntityPlayer player, IInventory par1iInventory, int par2, int par3, int par4) {
      super(par1iInventory, par2, par3, par4);
      this.player = player;
   }

   public boolean isItemValid(ItemStack stack) {
      return false;
   }

   public void onCrafting(ItemStack stack, int stackSize) {
      IC2.achievements.onMachineOp(this.player, stack, super.inventory);
   }
}
