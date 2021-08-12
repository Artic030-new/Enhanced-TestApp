package ic2.core.block;

import cpw.mods.fml.common.registry.GameRegistry;
import ic2.api.IPaintableBlock;
import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.block.BlockTex;
import ic2.core.item.block.ItemBlockRare;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockWall extends BlockTex implements IPaintableBlock {

   public BlockWall(int id, int sprite) {
      super(id, sprite, Material.rock);
      this.setHardness(3.0F);
      this.setResistance(30.0F);
      this.setBlockName("blockWall");
      this.setStepSound(Block.soundStoneFootstep);
      Ic2Items.constructionFoamWall = new ItemStack(this);
      GameRegistry.registerBlock(this, ItemBlockRare.class, "constructionFoamWall");
   }

   public int getBlockTexture(IBlockAccess iblockaccess, int i, int j, int k, int side) {
      int meta = iblockaccess.getBlockMetadata(i, j, k);
      return super.blockIndexInTexture + meta;
   }

   public int getBlockTextureFromSideAndMetadata(int side, int meta) {
      return super.blockIndexInTexture + meta;
   }

   public int quantityDropped(Random r) {
      return 0;
   }

   public boolean colorBlock(World world, int x, int y, int z, int color) {
      if(color != world.getBlockMetadata(x, y, z)) {
         world.setBlockMetadata(x, y, z, color);
         IC2.network.announceBlockUpdate(world, x, y, z);
         return true;
      } else {
         return false;
      }
   }

   public ItemStack createStackedBlock(int i) {
      return null;
   }

   public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
      return Ic2Items.constructionFoam.copy();
   }
}
