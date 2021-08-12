package ic2.core.item.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.Ic2Items;
import net.minecraft.block.Block;
import net.minecraft.item.ItemLeaves;
import net.minecraft.item.ItemStack;
import net.minecraft.world.ColorizerFoliage;

public class ItemRubLeaves extends ItemLeaves {

   public ItemRubLeaves(int par1) {
      super(par1);
      this.setHasSubtypes(false);
   }

   @SideOnly(Side.CLIENT)
   public int getColorFromItemStack(ItemStack par1ItemStack, int par2) {
      return ColorizerFoliage.getFoliageColorBirch();
   }

   public String getItemNameIS(ItemStack par1ItemStack) {
      return super.getItemName();
   }

   public int getIconFromDamage(int par1) {
      return Block.blocksList[Ic2Items.rubberLeaves.itemID].getBlockTextureFromSideAndMetadata(0, par1);
   }
}
