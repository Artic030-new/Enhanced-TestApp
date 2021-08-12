package ic2.core.block;

import ic2.core.Ic2Items;
import ic2.core.block.BlockTex;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class BlockResin extends BlockTex {

   public BlockResin(int id, int sprite) {
      super(id, sprite, Material.circuits);
      this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
      this.setHardness(1.6F);
      this.setResistance(0.5F);
      this.setStepSound(Block.soundSandFootstep);
      this.setBlockName("blockHarz");
      Ic2Items.resinSheet = new ItemStack(this);
   }

   public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int i, int j, int k) {
      return null;
   }

   public boolean isOpaqueCube() {
      return false;
   }

   public boolean renderAsNormalBlock() {
      return false;
   }

   public int idDropped(int i, Random random, int j) {
      return Ic2Items.resin.itemID;
   }

   public int quantityDropped(Random random) {
      return random.nextInt(5) == 0?0:1;
   }

   public boolean canPlaceBlockAt(World world, int i, int j, int k) {
      int l = world.getBlockId(i, j - 1, k);
      return l != 0 && Block.blocksList[l].isOpaqueCube()?world.getBlockMaterial(i, j - 1, k).isSolid():false;
   }

   public void onNeighborBlockChange(World world, int i, int j, int k, int l) {
      if(!this.canPlaceBlockAt(world, i, j, k)) {
         this.dropBlockAsItem(world, i, j, k, world.getBlockMetadata(i, j, k), 0);
         world.setBlockWithNotify(i, j, k, 0);
      }

   }

   public void onEntityCollidedWithBlock(World world, int i, int j, int k, Entity entity) {
      entity.fallDistance *= 0.75F;
      entity.motionX *= 0.6000000238418579D;
      entity.motionY *= 0.8500000238418579D;
      entity.motionZ *= 0.6000000238418579D;
   }
}
