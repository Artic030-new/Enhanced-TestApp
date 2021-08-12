package ic2.core.item.tfbp;

import ic2.core.Ic2Items;
import ic2.core.block.machine.tileentity.TileEntityTerra;
import ic2.core.item.tfbp.ItemTFBP;
import java.util.ArrayList;
import net.minecraft.block.Block;
import net.minecraft.world.World;

public class ItemTFBPFlatification extends ItemTFBP {

   public static ArrayList removeIDs = new ArrayList();


   public ItemTFBPFlatification(int id, int index) {
      super(id, index);
   }

   public static void init() {
      removeIDs.add(Integer.valueOf(Block.snow.blockID));
      removeIDs.add(Integer.valueOf(Block.ice.blockID));
      removeIDs.add(Integer.valueOf(Block.grass.blockID));
      removeIDs.add(Integer.valueOf(Block.stone.blockID));
      removeIDs.add(Integer.valueOf(Block.gravel.blockID));
      removeIDs.add(Integer.valueOf(Block.sand.blockID));
      removeIDs.add(Integer.valueOf(Block.dirt.blockID));
      removeIDs.add(Integer.valueOf(Block.leaves.blockID));
      removeIDs.add(Integer.valueOf(Block.wood.blockID));
      removeIDs.add(Integer.valueOf(Block.tallGrass.blockID));
      removeIDs.add(Integer.valueOf(Block.plantRed.blockID));
      removeIDs.add(Integer.valueOf(Block.plantYellow.blockID));
      removeIDs.add(Integer.valueOf(Block.sapling.blockID));
      removeIDs.add(Integer.valueOf(Block.crops.blockID));
      removeIDs.add(Integer.valueOf(Block.mushroomRed.blockID));
      removeIDs.add(Integer.valueOf(Block.mushroomBrown.blockID));
      removeIDs.add(Integer.valueOf(Block.pumpkin.blockID));
      if(Ic2Items.rubberLeaves != null) {
         removeIDs.add(Integer.valueOf(Ic2Items.rubberLeaves.itemID));
      }

      if(Ic2Items.rubberSapling != null) {
         removeIDs.add(Integer.valueOf(Ic2Items.rubberSapling.itemID));
      }

      if(Ic2Items.rubberWood != null) {
         removeIDs.add(Integer.valueOf(Ic2Items.rubberWood.itemID));
      }

   }

   public int getConsume() {
      return 4000;
   }

   public int getRange() {
      return 40;
   }

   public boolean terraform(World world, int x, int z, int yCoord) {
      int y = TileEntityTerra.getFirstBlockFrom(world, x, z, yCoord + 20);
      if(y >= 10) {
         return false;
      } else {
         if(world.getBlockId(x, y, z) == Block.snow.blockID) {
            --y;
         }

         if(y == yCoord) {
            return false;
         } else if(y < yCoord) {
            world.setBlockWithNotify(x, y + 1, z, Block.dirt.blockID);
            return true;
         } else if(this.canRemove(world.getBlockId(x, y, z))) {
            world.setBlockWithNotify(x, y, z, 0);
            return true;
         } else {
            return false;
         }
      }
   }

   public boolean canRemove(int id) {
      for(int i = 0; i < removeIDs.size(); ++i) {
         if(((Integer)removeIDs.get(i)).intValue() == id) {
            return true;
         }
      }

      return false;
   }

}
