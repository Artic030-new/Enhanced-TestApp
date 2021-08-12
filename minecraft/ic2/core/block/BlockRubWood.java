package ic2.core.block;

import cpw.mods.fml.common.registry.GameRegistry;
import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.block.BlockTex;
import ic2.core.item.block.ItemBlockRare;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockRubWood extends BlockTex {

   public BlockRubWood(int id) {
      super(id, 44, Material.wood);
      this.setTickRandomly(true);
      this.setHardness(1.0F);
      this.setStepSound(Block.soundWoodFootstep);
      this.setBlockName("blockRubWood");
      Ic2Items.rubberWood = new ItemStack(this);
      GameRegistry.registerBlock(this, ItemBlockRare.class, "rubberWood");
   }

   public int getBlockTexture(IBlockAccess iblockaccess, int i, int j, int k, int side) {
      int meta = iblockaccess.getBlockMetadata(i, j, k);
      return side < 2?47:(side == meta % 6?(meta > 5?46:45):44);
   }

   public int getBlockTextureFromSideAndMetadata(int side, int meta) {
      return side < 2?47:44;
   }

   public void dropBlockAsItemWithChance(World world, int i, int j, int k, int l, float f, int dropBuff) {
      if(IC2.platform.isSimulating()) {
         int i1 = this.quantityDropped(world.rand);

         for(int j1 = 0; j1 < i1; ++j1) {
            if(world.rand.nextFloat() <= f) {
               int k1 = this.idDropped(l, world.rand, 0);
               if(k1 > 0) {
                  this.dropBlockAsItem_do(world, i, j, k, new ItemStack(k1, 1, 0));
               }

               if(l != 0 && world.rand.nextInt(6) == 0) {
                  this.dropBlockAsItem_do(world, i, j, k, new ItemStack(Ic2Items.resin.getItem()));
               }
            }
         }

      }
   }

   public void breakBlock(World world, int i, int j, int k, int a, int b) {
      byte byte0 = 4;
      int l = byte0 + 1;
      if(world.checkChunksExist(i - l, j - l, k - l, i + l, j + l, k + l)) {
         for(int i1 = -byte0; i1 <= byte0; ++i1) {
            for(int j1 = -byte0; j1 <= byte0; ++j1) {
               for(int k1 = -byte0; k1 <= byte0; ++k1) {
                  int l1 = world.getBlockId(i + i1, j + j1, k + k1);
                  if(l1 == Ic2Items.rubberLeaves.itemID) {
                     int i2 = world.getBlockMetadata(i + i1, j + j1, k + k1);
                     if((i2 & 8) == 0) {
                        world.setBlockMetadata(i + i1, j + j1, k + k1, i2 | 8);
                     }
                  }
               }
            }
         }
      }

   }

   public void updateTick(World world, int x, int y, int z, Random random) {
      int meta = world.getBlockMetadata(x, y, z);
      if(meta >= 6) {
         if(random.nextInt(200) == 0) {
            world.setBlockMetadata(x, y, z, meta % 6);
            IC2.network.announceBlockUpdate(world, x, y, z);
         } else {
            world.scheduleBlockUpdate(x, y, z, super.blockID, this.tickRate());
         }

      }
   }

   public int tickRate() {
      return 100;
   }

   public int getMobilityFlag() {
      return 2;
   }

   public boolean canSustainLeaves(World world, int x, int y, int z) {
      return true;
   }
}
