package ic2.core.item.tfbp;

import ic2.core.block.machine.tileentity.TileEntityTerra;
import ic2.core.item.tfbp.ItemTFBP;
import net.minecraft.block.Block;
import net.minecraft.block.BlockMushroom;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class ItemTFBPMushroom extends ItemTFBP {

   public ItemTFBPMushroom(int id, int index) {
      super(id, index);
   }

   public int getConsume() {
      return 8000;
   }

   public int getRange() {
      return 25;
   }

   public boolean terraform(World world, int x, int z, int yCoord) {
      int y = TileEntityTerra.getFirstSolidBlockFrom(world, x, z, yCoord + 20);
      return y >= 10?false:this.growBlockWithDependancy(world, x, y, z, Block.mushroomCapBrown.blockID, Block.mushroomBrown.blockID);
   }

   public boolean growBlockWithDependancy(World world, int x, int y, int z, int id, int dependancy) {
      int base;
      int xm;
      int zm;
      int block;
      for(base = x - 1; dependancy != -1 && base < x + 1; ++base) {
         for(xm = z - 1; xm < z + 1; ++xm) {
            for(zm = y + 5; zm > y - 2; --zm) {
               block = world.getBlockId(base, zm, xm);
               if(dependancy == Block.mycelium.blockID) {
                  if(block == dependancy || block == Block.mushroomCapBrown.blockID || block == Block.mushroomCapRed.blockID) {
                     break;
                  }

                  if(block == 0) {
                     continue;
                  }

                  if(block == Block.dirt.blockID || block == Block.grass.blockID) {
                     world.setBlockWithNotify(base, zm, xm, dependancy);
                     TileEntityTerra.setBiomeAt(world, x, z, BiomeGenBase.mushroomIsland);
                     return true;
                  }
               }

               if(dependancy == Block.mushroomBrown.blockID) {
                  if(block == Block.mushroomBrown.blockID || block == Block.mushroomRed.blockID) {
                     break;
                  }

                  if(block != 0 && this.growBlockWithDependancy(world, base, zm, xm, Block.mushroomBrown.blockID, Block.mycelium.blockID)) {
                     return true;
                  }
               }
            }
         }
      }

      if(id == Block.mushroomBrown.blockID) {
         base = world.getBlockId(x, y, z);
         if(base != Block.mycelium.blockID) {
            if(base != Block.mushroomCapBrown.blockID && base != Block.mushroomCapRed.blockID) {
               return false;
            }

            world.setBlockWithNotify(x, y, z, Block.mycelium.blockID);
         }

         xm = world.getBlockId(x, y + 1, z);
         if(xm != 0 && xm != Block.tallGrass.blockID) {
            return false;
         } else {
            zm = Block.mushroomBrown.blockID;
            if(world.rand.nextBoolean()) {
               zm = Block.mushroomRed.blockID;
            }

            world.setBlockWithNotify(x, y + 1, z, zm);
            return true;
         }
      } else {
         if(id == Block.mushroomCapBrown.blockID) {
            base = world.getBlockId(x, y + 1, z);
            if(base != Block.mushroomBrown.blockID && base != Block.mushroomRed.blockID) {
               return false;
            }

            if(((BlockMushroom)Block.blocksList[base]).fertilizeMushroom(world, x, y + 1, z, world.rand)) {
               for(xm = x - 1; xm < x + 1; ++xm) {
                  for(zm = z - 1; zm < z + 1; ++zm) {
                     block = world.getBlockId(xm, y + 1, zm);
                     if(block == Block.mushroomBrown.blockID || block == Block.mushroomRed.blockID) {
                        world.setBlockWithNotify(xm, y + 1, zm, 0);
                     }
                  }
               }

               return true;
            }
         }

         return false;
      }
   }
}
