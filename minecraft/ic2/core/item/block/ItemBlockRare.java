package ic2.core.item.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.block.IRareBlock;
import net.minecraft.block.Block;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockRare extends ItemBlock {

   public ItemBlockRare(int id) {
      super(id);
   }

   @SideOnly(Side.CLIENT)
   public EnumRarity getRarity(ItemStack stack) {
      return Block.blocksList[stack.itemID] instanceof IRareBlock?((IRareBlock)Block.blocksList[stack.itemID]).getRarity(stack):super.getRarity(stack);
   }
}
