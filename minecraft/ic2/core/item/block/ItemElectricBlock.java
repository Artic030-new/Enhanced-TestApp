package ic2.core.item.block;

import ic2.core.item.block.ItemBlockRare;
import net.minecraft.item.ItemStack;

public class ItemElectricBlock extends ItemBlockRare {

   public ItemElectricBlock(int i) {
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
         return "blockBatBox";
      case 1:
         return "blockMFE";
      case 2:
         return "blockMFSU";
      case 3:
         return "blockTransformerLV";
      case 4:
         return "blockTransformerMV";
      case 5:
         return "blockTransformerHV";
      default:
         return null;
      }
   }
}
