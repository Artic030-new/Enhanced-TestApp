package ic2.core.item.tfbp;

import ic2.core.Ic2Items;
import ic2.core.block.BlockRubSapling;
import ic2.core.block.machine.tileentity.TileEntityTerra;
import ic2.core.item.tfbp.ItemTFBP;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSapling;
import net.minecraft.world.World;

public class ItemTFBPIrrigation extends ItemTFBP {

   public ItemTFBPIrrigation(int id, int index) {
      super(id, index);
   }

   public int getConsume() {
      return 3000;
   }

   public int getRange() {
      return 60;
   }

   public boolean terraform(World world, int x, int z, int yCoord) {
      if(world.rand.nextInt('\ubb80') == 0) {
         world.getWorldInfo().setRaining(true);
         return true;
      } else {
         int y = TileEntityTerra.getFirstBlockFrom(world, x, z, yCoord + 10);
         if(y >= 10) {
            return false;
         } else if(TileEntityTerra.switchGround(world, Block.sand, Block.dirt, x, y, z, true)) {
            TileEntityTerra.switchGround(world, Block.sand, Block.dirt, x, y, z, true);
            return true;
         } else {
            int id = world.getBlockId(x, y, z);
            if(id != Block.tallGrass.blockID) {
               if(id == Block.sapling.blockID) {
                  ((BlockSapling)Block.sapling).growTree(world, x, y, z, world.rand);
                  return true;
               } else if(id == Ic2Items.rubberSapling.itemID) {
                  ((BlockRubSapling)Block.blocksList[Ic2Items.rubberSapling.itemID]).growTree(world, x, y, z, world.rand);
                  return true;
               } else if(id == Block.wood.blockID) {
                  int meta = world.getBlockMetadata(x, y, z);
                  world.setBlockAndMetadataWithNotify(x, y + 1, z, Block.wood.blockID, meta);
                  this.createLeaves(world, x, y + 2, z, meta);
                  this.createLeaves(world, x + 1, y + 1, z, meta);
                  this.createLeaves(world, x - 1, y + 1, z, meta);
                  this.createLeaves(world, x, y + 1, z + 1, meta);
                  this.createLeaves(world, x, y + 1, z - 1, meta);
                  return true;
               } else if(id == Block.crops.blockID) {
                  world.setBlockMetadataWithNotify(x, y, z, 7);
                  return true;
               } else if(id == Block.fire.blockID) {
                  world.setBlockWithNotify(x, y, z, 0);
                  return true;
               } else {
                  return false;
               }
            } else {
               return this.spreadGrass(world, x + 1, y, z) || this.spreadGrass(world, x - 1, y, z) || this.spreadGrass(world, x, y, z + 1) || this.spreadGrass(world, x, y, z - 1);
            }
         }
      }
   }

   public void createLeaves(World world, int x, int y, int z, int meta) {
      if(world.getBlockId(x, y, z) == 0) {
         world.setBlockAndMetadataWithNotify(x, y, z, Block.leaves.blockID, meta);
      }

   }

   public boolean spreadGrass(World world, int x, int y, int z) {
      if(world.rand.nextBoolean()) {
         return false;
      } else {
         y = TileEntityTerra.getFirstBlockFrom(world, x, z, y);
         int id = world.getBlockId(x, y, z);
         if(id == Block.dirt.blockID) {
            world.setBlockWithNotify(x, y, z, Block.grass.blockID);
            return true;
         } else if(id == Block.grass.blockID) {
            world.setBlockAndMetadataWithNotify(x, y + 1, z, Block.tallGrass.blockID, 1);
            return true;
         } else {
            return false;
         }
      }
   }
}
