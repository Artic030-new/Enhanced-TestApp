package ic2.core.item.block;

import ic2.core.item.block.ItemBlockRare;
import net.minecraft.item.ItemStack;

public class ItemBlockMetal extends ItemBlockRare {

   public ItemBlockMetal(int i) {
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
         return "blockMetalCopper";
      case 1:
         return "blockMetalTin";
      case 2:
         return "blockMetalBronze";
      case 3:
         return "blockMetalUranium";
      default:
         return null;
      }
   }
}
