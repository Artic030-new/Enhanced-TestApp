package ic2.core.item;

import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.block.TileEntityBarrel;
import ic2.core.item.ItemIC2;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemMug extends ItemIC2 {

   public ItemMug(int id, int index) {
      super(id, index);
      this.setMaxStackSize(1);
   }

   public boolean onItemUse(ItemStack itemstack, EntityPlayer entityplayer, World world, int i, int j, int k, int side, float a, float b, float c) {
      if(world.getBlockId(i, j, k) == Ic2Items.blockBarrel.itemID) {
         TileEntityBarrel barrel = (TileEntityBarrel)world.getBlockTileEntity(i, j, k);
         if(barrel.treetapSide >= 2 && barrel.treetapSide == side) {
            int value = barrel.calculateMetaValue();
            if(barrel.drainLiquid(1) && IC2.platform.isSimulating()) {
               ItemStack is = new ItemStack(Ic2Items.mugBooze.itemID, 1, value);
               if(entityplayer.inventory.mainInventory[entityplayer.inventory.currentItem].stackSize > 1) {
                  --entityplayer.inventory.mainInventory[entityplayer.inventory.currentItem].stackSize;
                  if(!entityplayer.inventory.addItemStackToInventory(is)) {
                     entityplayer.dropPlayerItem(is);
                  }
               } else {
                  entityplayer.inventory.mainInventory[entityplayer.inventory.currentItem] = is;
               }

               return true;
            } else {
               return false;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }
}
