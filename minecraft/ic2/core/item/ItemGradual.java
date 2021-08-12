package ic2.core.item;

import ic2.api.IBoxable;
import ic2.core.Ic2Items;
import ic2.core.item.ItemIC2;
import net.minecraft.item.ItemStack;

public class ItemGradual extends ItemIC2 implements IBoxable {

   public ItemGradual(int id, int index) {
      super(id, index);
      this.setMaxStackSize(1);
      this.setMaxDamage(10000);
      this.setNoRepair();
   }

   public boolean canBeStoredInToolbox(ItemStack itemstack) {
      return itemstack.itemID == Ic2Items.hydratingCell.itemID;
   }

   public void setDamageForStack(ItemStack stack, int dmg) {
      stack.setItemDamage(dmg);
   }

   public int getDamageOfStack(ItemStack stack) {
      return stack.getItemDamage();
   }

   public int getMaxDamageEx() {
      return this.getMaxDamage();
   }
}
