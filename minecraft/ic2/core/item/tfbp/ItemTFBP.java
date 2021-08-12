package ic2.core.item.tfbp;

import ic2.api.ITerraformingBP;
import ic2.core.block.machine.tileentity.TileEntityTerra;
import ic2.core.item.ItemIC2;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public abstract class ItemTFBP extends ItemIC2 implements ITerraformingBP {

   public ItemTFBP(int id, int index) {
      super(id, index);
      this.setMaxStackSize(1);
   }

   public boolean onItemUse(ItemStack itemstack, EntityPlayer entityplayer, World world, int i, int j, int k, int l, float a, float b, float c) {
      if(world.getBlockTileEntity(i, j, k) instanceof TileEntityTerra) {
         ((TileEntityTerra)world.getBlockTileEntity(i, j, k)).insertBlueprint(itemstack);
         entityplayer.inventory.mainInventory[entityplayer.inventory.currentItem] = null;
         return true;
      } else {
         return false;
      }
   }
}
