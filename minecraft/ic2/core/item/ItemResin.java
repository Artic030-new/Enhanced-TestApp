package ic2.core.item;

import ic2.core.Ic2Items;
import ic2.core.item.ItemIC2;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemResin extends ItemIC2 {

   public ItemResin(int id, int index) {
      super(id, index);
   }

   public boolean onItemUse(ItemStack itemstack, EntityPlayer entityplayer, World world, int i, int j, int k, int side, float a, float b, float c) {
      if(world.getBlockId(i, j, k) == Block.pistonBase.blockID && world.getBlockMetadata(i, j, k) == side) {
         world.setBlockAndMetadataWithNotify(i, j, k, Block.pistonStickyBase.blockID, side);
         if(!entityplayer.capabilities.isCreativeMode) {
            --itemstack.stackSize;
         }

         return true;
      } else if(side != 1) {
         return false;
      } else {
         ++j;
         if(world.getBlockId(i, j, k) == 0 && Block.blocksList[Ic2Items.resinSheet.itemID].canPlaceBlockAt(world, i, j, k)) {
            world.setBlockWithNotify(i, j, k, Ic2Items.resinSheet.itemID);
            if(!entityplayer.capabilities.isCreativeMode) {
               --itemstack.stackSize;
            }

            return true;
         } else {
            return false;
         }
      }
   }
}
