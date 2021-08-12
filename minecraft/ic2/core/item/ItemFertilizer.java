package ic2.core.item;

import ic2.api.IBoxable;
import ic2.core.item.ItemIC2;
import net.minecraft.item.ItemStack;

public class ItemFertilizer extends ItemIC2 implements IBoxable {

   public ItemFertilizer(int id, int index) {
      super(id, index);
   }

   public boolean canBeStoredInToolbox(ItemStack itemstack) {
      return true;
   }
}
