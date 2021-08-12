package advsolar;

import advsolar.AdvancedSolarPanel;
import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemAdvanced extends Item {

   public ItemAdvanced(int id) {
      super(id);
      this.setHasSubtypes(true);
      this.setMaxDamage(0);
      this.setCreativeTab(AdvancedSolarPanel.ic2Tab);
   }

   public String getItemNameIS(ItemStack stack) {
	  switch(stack.getItemDamage()) {
	  case 0:
         return "itemSunnarium";
	  case 1:
	     return "itemSunnariumAlloy";
	  case 2:
	     return "itemIrradiantUranium";
	  case 3:
	     return "itemEnrichedSunnarium";
	  case 4:
	     return "itemEnrichedSunnariumAlloy";
	  case 5:
	     return "itemIrradiantGlassPane";
	  case 6:
	     return "itemIridiumIronPlate";
	  case 7:
	     return "itemReinforcedIridiumIronPlate";
	  case 8:
	     return "itemIrradiantReinforcedPlate";
	  case 9:
	     return "itemSunnariumPart";
	  case 10:
         return "ingotIridium";
	  default:
	     return "Ooooppsss";
	  }
   }

   public int getIconFromDamage(int meta) {
	  return meta >= 0 && meta <= 10 ? meta : 0;
   }

   public void getSubItems(int id, CreativeTabs table, List list) {
      for(int i = 0; i <= 10; ++i) {
    	 list.add(new ItemStack(id, 1, i));
      }

   }

   public String getTextureFile() {
      return "/advsolar/texture/adv_items.png";
   }
}
