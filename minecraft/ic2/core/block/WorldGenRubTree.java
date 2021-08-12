package ic2.core.block;

import ic2.core.IC2;
import ic2.core.Ic2Items;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenRubTree extends WorldGenerator {

   public static final int maxHeight = 8;


   public boolean generate(World world, Random random, int x, int count, int z) {
      while(count > 0) {
         int y;
         for(y = IC2.getWorldHeight(world) - 1; world.getBlockId(x, y - 1, z) == 0 && y > 0; --y) {
            ;
         }

         if(!this.grow(world, x, y, z, random)) {
            count -= 3;
         }

         x += random.nextInt(15) - 7;
         z += random.nextInt(15) - 7;
         --count;
      }

      return true;
   }

   public boolean grow(World world, int x, int y, int z, Random random) {
      if(world != null && Ic2Items.rubberWood != null) {
         int treeholechance = 25;
         int h = this.getGrowHeight(world, x, y, z);
         if(h < 2) {
            return false;
         } else {
            int height = h / 2;
            h -= h / 2;
            height += random.nextInt(h + 1);

            int i;
            for(i = 0; i < height; ++i) {
               world.setBlockWithNotify(x, y + i, z, Ic2Items.rubberWood.itemID);
               if(random.nextInt(100) <= treeholechance) {
                  treeholechance -= 10;
                  world.setBlockMetadata(x, y + i, z, random.nextInt(4) + 2);
               } else {
                  world.setBlockMetadata(x, y + i, z, 1);
               }

               IC2.network.announceBlockUpdate(world, x, y + i, z);
               if(height < 4 || height < 7 && i > 1 || i > 2) {
                  for(int a = x - 2; a <= x + 2; ++a) {
                     for(int b = z - 2; b <= z + 2; ++b) {
                        int c = i + 4 - height;
                        if(c < 1) {
                           c = 1;
                        }

                        boolean gen = a > x - 2 && a < x + 2 && b > z - 2 && b < z + 2 || a > x - 2 && a < x + 2 && random.nextInt(c) == 0 || b > z - 2 && b < z + 2 && random.nextInt(c) == 0;
                        if(gen && world.getBlockId(a, y + i, b) == 0) {
                           world.setBlockWithNotify(a, y + i, b, Ic2Items.rubberLeaves.itemID);
                        }
                     }
                  }
               }
            }

            for(i = 0; i <= height / 4 + random.nextInt(2); ++i) {
               if(world.getBlockId(x, y + height + i, z) == 0) {
                  world.setBlockWithNotify(x, y + height + i, z, Ic2Items.rubberLeaves.itemID);
               }
            }

            return true;
         }
      } else {
         System.out.println("[ERROR] Had a null that shouldn\'t have been. RubberTree did not spawn! w=" + world + " r=" + Ic2Items.rubberWood);
         return false;
      }
   }

   public int getGrowHeight(World world, int x, int y, int z) {
      if((world.getBlockId(x, y - 1, z) == Block.grass.blockID || world.getBlockId(x, y - 1, z) == Block.dirt.blockID) && (world.getBlockId(x, y, z) == 0 || world.getBlockId(x, y, z) == Ic2Items.rubberSapling.itemID)) {
         int height;
         for(height = 1; world.getBlockId(x, y + 1, z) == 0 && height < 8; ++y) {
            ++height;
         }

         return height;
      } else {
         return 0;
      }
   }
}
