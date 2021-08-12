package ic2.core.item;

import ic2.core.item.ItemScrapbox;
import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BehaviorScrapboxDispense extends BehaviorDefaultDispenseItem {

   protected ItemStack dispenseStack(IBlockSource par1IBlockSource, ItemStack par2ItemStack) {
      EnumFacing var3 = EnumFacing.getFront(par1IBlockSource.func_82620_h());
      IPosition var4 = BlockDispenser.func_82525_a(par1IBlockSource);
      ItemStack var5 = par2ItemStack.splitStack(1);
      World var10000 = par1IBlockSource.getWorld();
      ItemScrapbox var10001 = (ItemScrapbox)par2ItemStack.getItem();
      func_82486_a(var10000, ItemScrapbox.getDrop(par1IBlockSource.getWorld()), 6, var3, var4);
      return par2ItemStack;
   }
}
