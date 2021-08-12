package ic2.core.block.machine;

import cpw.mods.fml.common.registry.GameRegistry;
import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.block.BlockTex;
import ic2.core.item.block.ItemBlockRare;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class BlockMiningPipe extends BlockTex {

   public BlockMiningPipe(int id, int sprite) {
      super(id, sprite, Material.iron);
      this.setHardness(6.0F);
      this.setResistance(10.0F);
      this.setBlockName("blockMiningPipe");
      this.setCreativeTab(IC2.tabIC2);
      Ic2Items.miningPipe = new ItemStack(this);
      GameRegistry.registerBlock(this, ItemBlockRare.class, "miningPipe");
   }

   public boolean canPlaceBlockAt(World world, int i, int j, int k) {
      return false;
   }

   public boolean isOpaqueCube() {
      return false;
   }

   public boolean isBlockNormalCube(World world, int i, int j, int k) {
      return false;
   }

   public boolean renderAsNormalBlock() {
      return false;
   }

   public int getRenderType() {
      return IC2.platform.getRenderId("miningPipe");
   }

   public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int i, int j, int k) {
      return AxisAlignedBB.getBoundingBox((double)((float)i + 0.375F), (double)j, (double)((float)k + 0.375F), (double)((float)i + 0.625F), (double)((float)j + 1.0F), (double)((float)k + 0.625F));
   }

   public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int i, int j, int k) {
      return AxisAlignedBB.getBoundingBox((double)((float)i + 0.375F), (double)j, (double)((float)k + 0.375F), (double)((float)i + 0.625F), (double)((float)j + 1.0F), (double)((float)k + 0.625F));
   }
}
