package ic2.core.item;

import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.item.ItemIC2;
import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class ItemUpgradeModule extends ItemIC2 {

   public ItemUpgradeModule(int itemId) {
      super(itemId, 176);
      this.setCreativeTab(IC2.tabIC2);
      this.setHasSubtypes(true);
      Ic2Items.overclockerUpgrade = new ItemStack(this, 1, 0);
      Ic2Items.transformerUpgrade = new ItemStack(this, 1, 1);
      Ic2Items.energyStorageUpgrade = new ItemStack(this, 1, 2);
   }

   public int getIconFromDamage(int meta) {
      return super.iconIndex + meta;
   }

   public String getItemNameIS(ItemStack itemStack) {
      switch(itemStack.getItemDamage()) {
      case 0:
         return "overclockerUpgrade";
      case 1:
         return "transformerUpgrade";
      case 2:
         return "energyStorageUpgrade";
      default:
         return null;
      }
   }

   public void getSubItems(int i, CreativeTabs tabs, List itemList) {
      for(int meta = 0; meta <= 32767; ++meta) {
         ItemStack stack = new ItemStack(this, 1, meta);
         if(this.getItemNameIS(stack) == null) {
            break;
         }

         itemList.add(stack);
      }

   }
}
