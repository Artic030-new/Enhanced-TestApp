package ic2.core.item.tool;

import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.block.machine.tileentity.TileEntityTeleporter;
import ic2.core.item.ItemIC2;
import ic2.core.util.StackUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class ItemFrequencyTransmitter extends ItemIC2 {

   public ItemFrequencyTransmitter(int i, int index) {
      super(i, index);
      super.maxStackSize = 1;
      this.setMaxDamage(0);
   }

   public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer) {
      if(IC2.platform.isSimulating()) {
         if(itemstack.getItemDamage() == 0) {
            NBTTagCompound nbtData = StackUtil.getOrCreateNbtData(itemstack);
            if(nbtData.getBoolean("targetSet")) {
               nbtData.setBoolean("targetSet", false);
               IC2.platform.messagePlayer(entityplayer, "Frequency Transmitter unlinked");
            }
         } else {
            itemstack.setItemDamage(0);
         }
      }

      return itemstack;
   }

   public boolean onItemUseFirst(ItemStack itemstack, EntityPlayer entityplayer, World world, int i, int j, int k, int l, float hitX, float hitY, float hitZ) {
      TileEntity tileEntity = world.getBlockTileEntity(i, j, k);
      if(tileEntity instanceof TileEntityTeleporter && IC2.platform.isSimulating()) {
         NBTTagCompound nbtData = StackUtil.getOrCreateNbtData(itemstack);
         boolean targetSet = nbtData.getBoolean("targetSet");
         int targetX = nbtData.getInteger("targetX");
         int targetY = nbtData.getInteger("targetY");
         int targetZ = nbtData.getInteger("targetZ");
         TileEntityTeleporter tp = (TileEntityTeleporter)tileEntity;
         if(targetSet) {
            boolean te = world.findingSpawnPoint;
            world.findingSpawnPoint = true;
            Chunk tp2 = world.getChunkProvider().provideChunk(targetX >> 4, targetZ >> 4);
            world.findingSpawnPoint = te;
            if(tp2 == null || tp2.getBlockID(targetX & 15, targetY, targetZ & 15) != Ic2Items.teleporter.itemID || tp2.getBlockMetadata(targetX & 15, targetY, targetZ & 15) != Ic2Items.teleporter.getItemDamage()) {
               targetSet = false;
            }
         }

         if(!targetSet) {
            targetSet = true;
            targetX = tp.xCoord;
            targetY = tp.yCoord;
            targetZ = tp.zCoord;
            IC2.platform.messagePlayer(entityplayer, "Frequency Transmitter linked to Teleporter.");
         } else if(tp.xCoord == targetX && tp.yCoord == targetY && tp.zCoord == targetZ) {
            IC2.platform.messagePlayer(entityplayer, "Can\'t link Teleporter to itself.");
         } else if(tp.targetSet && tp.targetX == targetX && tp.targetY == targetY && tp.targetZ == targetZ) {
            IC2.platform.messagePlayer(entityplayer, "Teleportation link unchanged.");
         } else {
            tp.setTarget(targetX, targetY, targetZ);
            TileEntity te1 = world.getBlockTileEntity(targetX, targetY, targetZ);
            if(te1 instanceof TileEntityTeleporter) {
               TileEntityTeleporter tp21 = (TileEntityTeleporter)te1;
               if(!tp21.targetSet) {
                  tp21.setTarget(tp.xCoord, tp.yCoord, tp.zCoord);
               }
            }

            IC2.platform.messagePlayer(entityplayer, "Teleportation link established.");
         }

         nbtData.setBoolean("targetSet", targetSet);
         nbtData.setInteger("targetX", targetX);
         nbtData.setInteger("targetY", targetY);
         nbtData.setInteger("targetZ", targetZ);
         itemstack.setItemDamage(1);
         return false;
      } else {
         return false;
      }
   }
}
