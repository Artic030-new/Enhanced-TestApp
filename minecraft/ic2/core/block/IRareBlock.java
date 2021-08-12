package ic2.core.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;

public interface IRareBlock {

   @SideOnly(Side.CLIENT)
   EnumRarity getRarity(ItemStack var1);
}
