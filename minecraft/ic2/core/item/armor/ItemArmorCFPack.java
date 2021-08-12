package ic2.core.item.armor;

import ic2.core.item.armor.ItemArmorUtility;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ItemArmorCFPack extends ItemArmorUtility {

   public ItemArmorCFPack(int id, int index, int armorrendering) {
      super(id, index, armorrendering, 1);
      this.setMaxDamage(260);
   }

   public boolean getCFPellet(EntityPlayer player, ItemStack pack) {
      if(pack.getItemDamage() < pack.getMaxDamage() - 1) {
         pack.setItemDamage(pack.getItemDamage() + 1);
         return true;
      } else {
         return false;
      }
   }

   public boolean isRepairable() {
      return true;
   }
}
