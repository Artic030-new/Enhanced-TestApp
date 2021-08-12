package ic2.core.item.tool;

import ic2.api.IWrenchable;
import ic2.core.IC2;
import ic2.core.audio.PositionSpec;
import ic2.core.block.machine.tileentity.TileEntityTerra;
import ic2.core.item.ItemIC2;
import ic2.core.util.StackUtil;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class ItemToolWrench extends ItemIC2 {

   public ItemToolWrench(int id, int index) {
      super(id, index);
      this.setMaxDamage(160);
      this.setMaxStackSize(1);
   }

   public boolean canTakeDamage(ItemStack stack, int amount) {
      return true;
   }

   public boolean onItemUseFirst(ItemStack itemstack, EntityPlayer entityPlayer, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
      if(!this.canTakeDamage(itemstack, 1)) {
         return false;
      } else {
         int blockId = world.getBlockId(x, y, z);
         int metaData = world.getBlockMetadata(x, y, z);
         TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
         if(tileEntity instanceof TileEntityTerra) {
            TileEntityTerra wrenchable = (TileEntityTerra)tileEntity;
            if(wrenchable.ejectBlueprint()) {
               if(IC2.platform.isSimulating()) {
                  this.damage(itemstack, 1, entityPlayer);
               }

               if(IC2.platform.isRendering()) {
                  IC2.audioManager.playOnce(entityPlayer, PositionSpec.Hand, "Tools/wrench.ogg", true, IC2.audioManager.defaultVolume);
               }

               return IC2.platform.isSimulating();
            }
         }

         if(tileEntity instanceof IWrenchable) {
            IWrenchable wrenchable1 = (IWrenchable)tileEntity;
            if(IC2.keyboard.isAltKeyDown(entityPlayer)) {
               if(entityPlayer.isSneaking()) {
                  side = (wrenchable1.getFacing() + 5) % 6;
               } else {
                  side = (wrenchable1.getFacing() + 1) % 6;
               }
            } else if(entityPlayer.isSneaking()) {
               side += side % 2 * -2 + 1;
            }

            if(wrenchable1.wrenchCanSetFacing(entityPlayer, side)) {
               if(IC2.platform.isSimulating()) {
                  wrenchable1.setFacing((short)side);
                  this.damage(itemstack, 1, entityPlayer);
               }

               if(IC2.platform.isRendering()) {
                  IC2.audioManager.playOnce(entityPlayer, PositionSpec.Hand, "Tools/wrench.ogg", true, IC2.audioManager.defaultVolume);
               }

               return IC2.platform.isSimulating();
            }

            if(this.canTakeDamage(itemstack, 10) && wrenchable1.wrenchCanRemove(entityPlayer)) {
               if(IC2.platform.isSimulating()) {
                  if(IC2.enableLoggingWrench) {
                     String block = tileEntity.getClass().getName().replace("TileEntity", "");
                     MinecraftServer.getServer();
                     MinecraftServer.logger.log(Level.INFO, "Player " + entityPlayer.username + " used the wrench to remove the " + block + " (" + blockId + "-" + metaData + ") at " + x + "/" + y + "/" + z);
                  }

                  Block block1 = Block.blocksList[blockId];
                  boolean dropOriginalBlock = false;
                  if(wrenchable1.getWrenchDropRate() < 1.0F && this.overrideWrenchSuccessRate(itemstack)) {
                     if(!this.canTakeDamage(itemstack, 200)) {
                        IC2.platform.messagePlayer(entityPlayer, "Not enough energy for lossless wrench operation");
                        return true;
                     }

                     dropOriginalBlock = true;
                     this.damage(itemstack, 200, entityPlayer);
                  } else {
                     dropOriginalBlock = world.rand.nextFloat() <= wrenchable1.getWrenchDropRate();
                     this.damage(itemstack, 10, entityPlayer);
                  }

                  ArrayList drops = block1.getBlockDropped(world, x, y, z, metaData, 0);
                  if(dropOriginalBlock) {
                     if(drops.isEmpty()) {
                        drops.add(wrenchable1.getWrenchDrop(entityPlayer));
                     } else {
                        drops.set(0, wrenchable1.getWrenchDrop(entityPlayer));
                     }
                  }

                  Iterator i$ = drops.iterator();

                  while(i$.hasNext()) {
                     ItemStack itemStack = (ItemStack)i$.next();
                     StackUtil.dropAsEntity(world, x, y, z, itemStack);
                  }

                  world.setBlockWithNotify(x, y, z, 0);
               }

               if(IC2.platform.isRendering()) {
                  IC2.audioManager.playOnce(entityPlayer, PositionSpec.Hand, "Tools/wrench.ogg", true, IC2.audioManager.defaultVolume);
               }

               return IC2.platform.isSimulating();
            }
         }

         return false;
      }
   }

   public void damage(ItemStack is, int damage, EntityPlayer player) {
      is.damageItem(damage, player);
   }

   public boolean overrideWrenchSuccessRate(ItemStack itemStack) {
      return false;
   }
}
