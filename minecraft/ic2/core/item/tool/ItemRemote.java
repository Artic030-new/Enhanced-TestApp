package ic2.core.item.tool;

import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.audio.PositionSpec;
import ic2.core.item.ItemIC2;
import ic2.core.util.StackUtil;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

public class ItemRemote extends ItemIC2 {

   public ItemRemote(int id, int index) {
      super(id, index);
      this.setMaxStackSize(1);
   }

   public boolean onItemUse(ItemStack itemstack, EntityPlayer entityplayer, World world, int i, int j, int k, int l, float a, float b, float c) {
      if(IC2.platform.isSimulating()) {
         if(world.getBlockId(i, j, k) == Ic2Items.dynamiteStick.itemID) {
            addRemote(i, j, k, itemstack);
            entityplayer.openContainer.detectAndSendChanges();
            world.setBlockWithNotify(i, j, k, Ic2Items.dynamiteStickWithRemote.itemID);
            return true;
         } else if(world.getBlockId(i, j, k) == Ic2Items.dynamiteStickWithRemote.itemID) {
            int index = hasRemote(i, j, k, itemstack);
            if(index > -1) {
               world.setBlockWithNotify(i, j, k, Ic2Items.dynamiteStick.itemID);
               removeRemote(index, itemstack);
               entityplayer.openContainer.detectAndSendChanges();
            } else {
               IC2.platform.messagePlayer(entityplayer, "This dynamite stick is not linked to this remote, cannot unlink.");
            }

            return true;
         } else {
            return true;
         }
      } else {
         return world.getBlockId(i, j, k) == Ic2Items.dynamiteStick.itemID || world.getBlockId(i, j, k) == Ic2Items.dynamiteStickWithRemote.itemID;
      }
   }

   public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer) {
      if(!IC2.platform.isSimulating()) {
         return itemstack;
      } else {
         IC2.audioManager.playOnce(entityplayer, PositionSpec.Hand, "Tools/dynamiteomote.ogg", true, IC2.audioManager.defaultVolume);
         launchRemotes(world, itemstack);
         entityplayer.openContainer.detectAndSendChanges();
         return itemstack;
      }
   }

   public static void addRemote(int x, int y, int z, ItemStack freq) {
      NBTTagCompound compound = StackUtil.getOrCreateNbtData(freq);
      if(!compound.hasKey("coords")) {
         compound.setTag("coords", new NBTTagList());
      }

      NBTTagList coords = compound.getTagList("coords");
      NBTTagCompound coord = new NBTTagCompound();
      coord.setInteger("x", x);
      coord.setInteger("y", y);
      coord.setInteger("z", z);
      coords.appendTag(coord);
      compound.setTag("coords", coords);
      freq.setItemDamage(coords.tagCount());
   }

   public void addInformation(ItemStack stack, EntityPlayer player, List info, boolean debugTooltips) {
      if(stack.getItemDamage() > 0) {
         info.add("Linked to " + stack.getItemDamage() + " dynamite");
      }

   }

   public static void launchRemotes(World world, ItemStack freq) {
      NBTTagCompound compound = StackUtil.getOrCreateNbtData(freq);
      if(compound.hasKey("coords")) {
         NBTTagList coords = compound.getTagList("coords");

         for(int i = 0; i < coords.tagCount(); ++i) {
            NBTTagCompound coord = (NBTTagCompound)coords.tagAt(i);
            int x = coord.getInteger("x");
            int y = coord.getInteger("y");
            int z = coord.getInteger("z");
            if(world.getBlockId(x, y, z) == Ic2Items.dynamiteStickWithRemote.itemID) {
               Block.blocksList[Ic2Items.dynamiteStickWithRemote.itemID].onBlockDestroyedByExplosion(world, x, y, z);
               world.setBlockAndMetadataWithNotify(x, y, z, 0, 0);
            }
         }

         compound.setTag("coords", new NBTTagList());
         freq.setItemDamage(0);
      }
   }

   public static int hasRemote(int x, int y, int z, ItemStack freq) {
      NBTTagCompound compound = StackUtil.getOrCreateNbtData(freq);
      if(!compound.hasKey("coords")) {
         return -1;
      } else {
         NBTTagList coords = compound.getTagList("coords");

         for(int i = 0; i < coords.tagCount(); ++i) {
            NBTTagCompound coord = (NBTTagCompound)coords.tagAt(i);
            if(coord.getInteger("x") == x && coord.getInteger("y") == y && coord.getInteger("z") == z) {
               return i;
            }
         }

         return -1;
      }
   }

   public static void removeRemote(int index, ItemStack freq) {
      NBTTagCompound compound = StackUtil.getOrCreateNbtData(freq);
      if(compound.hasKey("coords")) {
         NBTTagList coords = compound.getTagList("coords");
         NBTTagList newCoords = new NBTTagList();

         for(int i = 0; i < coords.tagCount(); ++i) {
            if(i != index) {
               newCoords.appendTag(coords.tagAt(i));
            }
         }

         compound.setTag("coords", newCoords);
         freq.setItemDamage(newCoords.tagCount());
      }
   }
}
