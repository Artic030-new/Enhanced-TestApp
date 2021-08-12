package ic2.advancedmachines.common;

import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemAdvancedMachine extends ItemBlock {

   public ItemAdvancedMachine(int id) {
      super(id);
      this.setMaxDamage(0);
      this.setHasSubtypes(true);
   }

   public int getMetadata(int metadata) {
      return metadata;
   }

   public String getItemDisplayName(ItemStack itemStack) {
	  return "§e" + super.getItemDisplayName(itemStack);
   }

   public String getItemNameIS(ItemStack stack) {
	  switch(stack.getItemDamage()) {
      case 0:
         return "blockRotaryMacerator";
      case 1:
         return "blockSingularityCompressor";
      case 2:
         return "blockCentrifugeExtractor";
      default:
         return null;
      }
   }

   public void getSubItems(int id, CreativeTabs table, List list) {
	  list.add(new ItemStack(id, 1, 0));
      list.add(new ItemStack(id, 1, 1));
      list.add(new ItemStack(id, 1, 2));
   }
}
