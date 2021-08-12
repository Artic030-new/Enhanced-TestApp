package ic2.core.item.tool;

import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.audio.PositionSpec;
import ic2.core.block.TileEntityBarrel;
import ic2.core.item.ItemIC2;
import ic2.core.util.StackUtil;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemTreetap extends ItemIC2 {

   public ItemTreetap(int id, int index) {
      super(id, index);
      this.setMaxStackSize(1);
      this.setMaxDamage(16);
   }

   public boolean onItemUse(ItemStack itemstack, EntityPlayer entityplayer, World world, int i, int j, int k, int side, float a, float b, float c) {
      if(world.getBlockId(i, j, k) == Ic2Items.blockBarrel.itemID) {
         return ((TileEntityBarrel)world.getBlockTileEntity(i, j, k)).useTreetapOn(entityplayer, side);
      } else if(world.getBlockId(i, j, k) == Ic2Items.rubberWood.itemID) {
         attemptExtract(entityplayer, world, i, j, k, side, (List)null);
         if(IC2.platform.isSimulating()) {
            itemstack.damageItem(1, entityplayer);
         }

         return true;
      } else {
         return false;
      }
   }

   public static void ejectHarz(World world, int x, int y, int z, int side, int quantity) {
      double ejectX = (double)x + 0.5D;
      double ejectY = (double)y + 0.5D;
      double ejectZ = (double)z + 0.5D;
      if(side == 2) {
         ejectZ -= 0.3D;
      } else if(side == 5) {
         ejectX += 0.3D;
      } else if(side == 3) {
         ejectZ += 0.3D;
      } else if(side == 4) {
         ejectX -= 0.3D;
      }

      for(int i = 0; i < quantity; ++i) {
         EntityItem entityitem = new EntityItem(world, ejectX, ejectY, ejectZ, Ic2Items.resin.copy());
         entityitem.delayBeforeCanPickup = 10;
         world.spawnEntityInWorld(entityitem);
      }

   }

   public static boolean attemptExtract(EntityPlayer entityplayer, World world, int i, int j, int k, int l, List stacks) {
      int meta = world.getBlockMetadata(i, j, k);
      if(meta >= 2 && meta % 6 == l) {
         if(meta < 6) {
            if(IC2.platform.isSimulating()) {
               world.setBlockMetadataWithNotify(i, j, k, meta + 6);
               if(stacks != null) {
                  stacks.add(StackUtil.copyWithSize(Ic2Items.resin, world.rand.nextInt(3) + 1));
               } else {
                  ejectHarz(world, i, j, k, l, world.rand.nextInt(3) + 1);
               }

               if(entityplayer != null) {
                  IC2.achievements.issueAchievement(entityplayer, "acquireResin");
               }

               world.scheduleBlockUpdate(i, j, k, Ic2Items.rubberWood.itemID, Block.blocksList[Ic2Items.rubberWood.itemID].tickRate());
               IC2.network.announceBlockUpdate(world, i, j, k);
            }

            if(IC2.platform.isRendering() && entityplayer != null) {
               IC2.audioManager.playOnce(entityplayer, PositionSpec.Hand, "Tools/Treetap.ogg", true, IC2.audioManager.defaultVolume);
            }

            return true;
         } else {
            if(world.rand.nextInt(5) == 0 && IC2.platform.isSimulating()) {
               world.setBlockMetadataWithNotify(i, j, k, 1);
               IC2.network.announceBlockUpdate(world, i, j, k);
            }

            if(world.rand.nextInt(5) == 0) {
               if(IC2.platform.isSimulating()) {
                  ejectHarz(world, i, j, k, l, 1);
                  if(stacks != null) {
                     stacks.add(StackUtil.copyWithSize(Ic2Items.resin, 1));
                  } else {
                     ejectHarz(world, i, j, k, l, 1);
                  }
               }

               if(IC2.platform.isRendering() && entityplayer != null) {
                  IC2.audioManager.playOnce(entityplayer, PositionSpec.Hand, "Tools/Treetap.ogg", true, IC2.audioManager.defaultVolume);
               }

               return true;
            } else {
               return false;
            }
         }
      } else {
         return false;
      }
   }
}
