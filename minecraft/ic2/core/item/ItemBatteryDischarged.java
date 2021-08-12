package ic2.core.item;

import ic2.api.IBoxable;
import ic2.core.Ic2Items;
import ic2.core.item.ItemBattery;
import net.minecraft.item.ItemStack;

public class ItemBatteryDischarged extends ItemBattery implements IBoxable {

   public ItemBatteryDischarged(int id, int sprite, int maxCharge, int transferLimit, int tier) {
      super(id, sprite, maxCharge, transferLimit, tier);
      this.setMaxDamage(0);
      this.setMaxStackSize(16);
   }

   public int getChargedItemId() {
      return Ic2Items.chargedReBattery.itemID;
   }

   public int getIconFromDamage(int i) {
      return super.iconIndex;
   }

   public boolean canBeStoredInToolbox(ItemStack itemstack) {
      return true;
   }
}
