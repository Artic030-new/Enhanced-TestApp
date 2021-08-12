package ic2.core.item.tfbp;

import ic2.core.Ic2Items;
import ic2.core.block.machine.tileentity.TileEntityTerra;
import ic2.core.item.tfbp.ItemTFBP;
import ic2.core.item.tfbp.ItemTFBPCultivation;
import net.minecraft.block.Block;
import net.minecraft.world.World;

public class ItemTFBPDesertification extends ItemTFBP {

   public ItemTFBPDesertification(int id, int index) {
      super(id, index);
   }

   public int getConsume() {
      return 2500;
   }

   public int getRange() {
      return 40;
   }

   public boolean terraform(World world, int x, int z, int yCoord) {
      int y = TileEntityTerra.getFirstBlockFrom(world, x, z, yCoord + 10);
      if(y >= 10) {
         return false;
      } else if(!TileEntityTerra.switchGround(world, Block.dirt, Block.sand, x, y, z, false) && !TileEntityTerra.switchGround(world, Block.grass, Block.sand, x, y, z, false) && !TileEntityTerra.switchGround(world, Block.tilledField, Block.sand, x, y, z, false)) {
         int id = world.getBlockId(x, y, z);
         if(id != Block.waterMoving.blockID && id != Block.waterStill.blockID && id != Block.snow.blockID && id != Block.leaves.blockID && id != Ic2Items.rubberLeaves.itemID && !this.isPlant(id)) {
            if(id != Block.ice.blockID && id != Block.blockSnow.blockID) {
               if((id == Block.planks.blockID || id == Block.wood.blockID || id == Ic2Items.rubberWood.itemID) && world.rand.nextInt(15) == 0) {
                  world.setBlockWithNotify(x, y, z, Block.fire.blockID);
                  return true;
               } else {
                  return false;
               }
            } else {
               world.setBlockWithNotify(x, y, z, Block.waterMoving.blockID);
               return true;
            }
         } else {
            world.setBlockWithNotify(x, y, z, 0);
            return true;
         }
      } else {
         TileEntityTerra.switchGround(world, Block.dirt, Block.sand, x, y, z, false);
         return true;
      }
   }

   public boolean isPlant(int id) {
      for(int i = 0; i < ItemTFBPCultivation.plantIDs.size(); ++i) {
         if(((Integer)ItemTFBPCultivation.plantIDs.get(i)).intValue() == id) {
            return true;
         }
      }

      return false;
   }
}
