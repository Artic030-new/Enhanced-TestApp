package ic2.core.item.block;

import ic2.core.item.block.ItemBlockRare;
import net.minecraft.item.ItemStack;

public class ItemGenerator extends ItemBlockRare {

   public ItemGenerator(int i) {
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
         return "blockGenerator";
      case 1:
         return "blockGeoGenerator";
      case 2:
         return "blockWaterGenerator";
      case 3:
         return "blockSolarGenerator";
      case 4:
         return "blockWindGenerator";
      case 5:
         return "blockNuclearReactor";
      default:
         return null;
      }
   }
}
