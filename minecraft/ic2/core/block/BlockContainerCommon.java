package ic2.core.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.block.IRareBlock;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public abstract class BlockContainerCommon extends BlockContainer implements IRareBlock {

   public BlockContainerCommon(int par1, Material par3Material) {
      super(par1, par3Material);
   }

   public BlockContainerCommon(int par1, int par2, Material par3Material) {
      super(par1, par2, par3Material);
   }

   public void onBlockDestroyedByExplosion(World world, int x, int y, int z) {
      world.removeBlockTileEntity(x, y, z);
   }

   @SideOnly(Side.CLIENT)
   public EnumRarity getRarity(ItemStack stack) {
      return EnumRarity.common;
   }
}
