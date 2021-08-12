package ic2.core.item.block;

import ic2.core.Ic2Items;
import ic2.core.block.BlockScaffold;
import ic2.core.block.TileEntityBarrel;
import ic2.core.item.ItemBooze;
import ic2.core.item.ItemIC2;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemBarrel extends ItemIC2 {

   public ItemBarrel(int id, int index) {
      super(id, index);
      this.setMaxStackSize(1);
   }

   public String getItemDisplayName(ItemStack itemstack) {
      int v = ItemBooze.getAmountOfValue(itemstack.getItemDamage());
      return v > 0?"" + v + "Литров Пивная Бочка":"Пустая Пивная Бочка";
   }

   public boolean onItemUse(ItemStack itemstack, EntityPlayer entityplayer, World world, int i, int j, int k, int side, float a, float b, float c) {
      if(world.getBlockId(i, j, k) == Ic2Items.scaffold.itemID && world.getBlockMetadata(i, j, k) < BlockScaffold.reinforcedStrength) {
         world.setBlockWithNotify(i, j, k, Ic2Items.blockBarrel.itemID);
         ((TileEntityBarrel)world.getBlockTileEntity(i, j, k)).set(itemstack.getItemDamage());
         if(!entityplayer.capabilities.isCreativeMode) {
            --entityplayer.inventory.mainInventory[entityplayer.inventory.currentItem].stackSize;
            if(entityplayer.inventory.mainInventory[entityplayer.inventory.currentItem].stackSize == 0) {
               entityplayer.inventory.mainInventory[entityplayer.inventory.currentItem] = null;
            }
         }

         return true;
      } else {
         return false;
      }
   }
}
