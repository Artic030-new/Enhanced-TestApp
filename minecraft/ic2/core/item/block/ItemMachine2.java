package ic2.core.item.block;

import ic2.core.item.block.ItemBlockRare;
import net.minecraft.item.ItemStack;

public class ItemMachine2 extends ItemBlockRare {

   public ItemMachine2(int i) {
      super(i);
      this.setMaxDamage(0);
      this.setHasSubtypes(true);
   }

   public int getMetadata(int i) {
      return i;
   }

   public String getItemNameIS(ItemStack itemstack) {
      int meta = itemstack.getItemDamage();
      switch(meta) {
      case 0:
         return "blockTeleporter";
      case 1:
         return "blockTesla";
      case 2:
         return "blockCropmatron";
      default:
         return null;
      }
   }
}
