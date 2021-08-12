package gravisuite;

import gravisuite.GraviSuite;
import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemSimpleItems extends Item {

   public ItemSimpleItems(int id) {
      super(id);
      this.setMaxStackSize(64);
      this.setHasSubtypes(true);
      this.setCreativeTab(GraviSuite.ic2Tab);
   }

   public String getItemNameIS(ItemStack stack) {
	  switch(stack.getItemDamage()) {
	  case 0:
         return "itemSuperConductorCover";
	  case 1:
	     return "itemSuperConductor";
	  case 2:
	     return "itemCoolingCore";
	  case 3:
	     return "itemGravitationEngine";
	  case 4:
	     return "itemMagnetron";
	  case 5:
	     return "itemVajraCore";
	  case 6:
	     return "itemEngineBoost";
	  default:
	     return "Ooooppsss";
	  }
   }

   public int getIconFromDamage(int meta) {
      return meta == 0?2:(meta == 1?3:(meta == 2?4:(meta == 3?5:(meta == 4?11:(meta == 5?12:(meta == 6?14:2))))));
   }

   public void getSubItems(int id, CreativeTabs table, List list) {
      for(int i = 0; i <= 6; ++i) {
    	 list.add(new ItemStack(id, 1, i));
      }

   }

   public String getTextureFile() {
      return "/gravisuite/gravi_items.png";
   }
}
