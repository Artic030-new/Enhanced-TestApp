package ic2.core.item.block;

import ic2.core.item.block.ItemBlockRare;
import net.minecraft.item.ItemStack;

public class ItemPersonalBlock extends ItemBlockRare {

   public ItemPersonalBlock(int i) {
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
         return "blockPersonalChest";
      case 1:
         return "blockPersonalTrader";
      case 2:
         return "blockPersonalTraderEnergy";
      default:
         return null;
      }
   }
}
