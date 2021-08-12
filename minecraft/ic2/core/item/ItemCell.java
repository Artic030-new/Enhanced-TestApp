package ic2.core.item;

import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.item.ItemIC2;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class ItemCell extends ItemIC2 {

   public ItemCell(int id, int index) {
      super(id, index);
   }

   public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer) {
      float f = 1.0F;
      float f1 = entityplayer.prevRotationPitch + (entityplayer.rotationPitch - entityplayer.prevRotationPitch) * f;
      float f2 = entityplayer.prevRotationYaw + (entityplayer.rotationYaw - entityplayer.prevRotationYaw) * f;
      double d = entityplayer.prevPosX + (entityplayer.posX - entityplayer.prevPosX) * (double)f;
      double d1 = entityplayer.prevPosY + (entityplayer.posY - entityplayer.prevPosY) * (double)f + 1.62D - (double)entityplayer.yOffset;
      double d2 = entityplayer.prevPosZ + (entityplayer.posZ - entityplayer.prevPosZ) * (double)f;
      Vec3 vec3d = Vec3.createVectorHelper(d, d1, d2);
      float f3 = MathHelper.cos(-f2 * 0.01745329F - 3.141593F);
      float f4 = MathHelper.sin(-f2 * 0.01745329F - 3.141593F);
      float f5 = -MathHelper.cos(-f1 * 0.01745329F);
      float f6 = MathHelper.sin(-f1 * 0.01745329F);
      float f7 = f4 * f5;
      float f9 = f3 * f5;
      double d3 = 5.0D;
      Vec3 vec3d1 = vec3d.addVector((double)f7 * d3, (double)f6 * d3, (double)f9 * d3);
      MovingObjectPosition movingobjectposition = world.rayTraceBlocks_do(vec3d, vec3d1, true);
      if(movingobjectposition == null) {
         return itemstack;
      } else {
         if(movingobjectposition.typeOfHit == EnumMovingObjectType.TILE) {
            int i = movingobjectposition.blockX;
            int j = movingobjectposition.blockY;
            int k = movingobjectposition.blockZ;
            if(!world.canMineBlock(entityplayer, i, j, k)) {
               return itemstack;
            }

            if(world.getBlockId(i, j, k) == Block.waterStill.blockID && world.getBlockMetadata(i, j, k) == 0 && this.storeCell(Ic2Items.waterCell.copy(), entityplayer)) {
               world.setBlockWithNotify(i, j, k, 0);
               --itemstack.stackSize;
               return itemstack;
            }

            if(world.getBlockId(i, j, k) == Block.lavaStill.blockID && world.getBlockMetadata(i, j, k) == 0 && this.storeCell(Ic2Items.lavaCell.copy(), entityplayer)) {
               world.setBlockWithNotify(i, j, k, 0);
               --itemstack.stackSize;
               return itemstack;
            }
         }

         return itemstack;
      }
   }

   public boolean storeCell(ItemStack cell, EntityPlayer player) {
      if(player.inventory.addItemStackToInventory(cell)) {
         if(!IC2.platform.isRendering()) {
            player.openContainer.detectAndSendChanges();
         }

         return true;
      } else {
         return false;
      }
   }
}
