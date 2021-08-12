package ic2.core.item.tfbp;

import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.block.machine.tileentity.TileEntityTerra;
import ic2.core.item.tfbp.ItemTFBP;
import java.util.ArrayList;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemTFBPCultivation extends ItemTFBP {

   public static ArrayList plantIDs = new ArrayList();


   public ItemTFBPCultivation(int id, int index) {
      super(id, index);
   }

   public static void init() {
      plantIDs.add(Integer.valueOf(Block.tallGrass.blockID));
      plantIDs.add(Integer.valueOf(Block.plantRed.blockID));
      plantIDs.add(Integer.valueOf(Block.plantYellow.blockID));
      plantIDs.add(Integer.valueOf(Block.sapling.blockID));
      plantIDs.add(Integer.valueOf(Block.crops.blockID));
      plantIDs.add(Integer.valueOf(Block.mushroomRed.blockID));
      plantIDs.add(Integer.valueOf(Block.mushroomBrown.blockID));
      plantIDs.add(Integer.valueOf(Block.pumpkin.blockID));
      if(Ic2Items.rubberSapling != null) {
         plantIDs.add(Integer.valueOf(Ic2Items.rubberSapling.itemID));
      }

   }

   public boolean onItemUse(ItemStack itemstack, EntityPlayer entityplayer, World world, int i, int j, int k, int l, float a, float b, float c) {
      if(super.onItemUse(itemstack, entityplayer, world, i, j, k, l, a, b, c)) {
         if(entityplayer.dimension == 1) {
            IC2.achievements.issueAchievement(entityplayer, "terraformEndCultivation");
         }

         return true;
      } else {
         return false;
      }
   }

   public int getConsume() {
      return 4000;
   }

   public int getRange() {
      return 40;
   }

   public boolean terraform(World world, int x, int z, int yCoord) {
      int y = TileEntityTerra.getFirstSolidBlockFrom(world, x, z, yCoord + 10);
      if(y >= 10) {
         return false;
      } else if(TileEntityTerra.switchGround(world, Block.sand, Block.dirt, x, y, z, true)) {
         return true;
      } else {
         int id = world.getBlockId(x, y, z);
         if(id == Block.dirt.blockID) {
            world.setBlockWithNotify(x, y, z, Block.grass.blockID);
            return true;
         } else {
            return id == Block.grass.blockID?this.growPlantsOn(world, x, y + 1, z):false;
         }
      }
   }

   public boolean growPlantsOn(World world, int x, int y, int z) {
      int id = world.getBlockId(x, y, z);
      if(id != 0 && (id != Block.tallGrass.blockID || world.rand.nextInt(4) != 0)) {
         return false;
      } else {
         int plant = this.pickRandomPlantId(world.rand);
         if(plant == Block.crops.blockID) {
            world.setBlockWithNotify(x, y - 1, z, Block.tilledField.blockID);
         }

         if(plant == Block.tallGrass.blockID) {
            world.setBlockAndMetadataWithNotify(x, y, z, plant, 1);
            return true;
         } else {
            world.setBlockWithNotify(x, y, z, plant);
            return true;
         }
      }
   }

   public int pickRandomPlantId(Random random) {
      for(int i = 0; i < plantIDs.size(); ++i) {
         if(random.nextInt(5) <= 1) {
            return ((Integer)plantIDs.get(i)).intValue();
         }
      }

      return Block.tallGrass.blockID;
   }

}
