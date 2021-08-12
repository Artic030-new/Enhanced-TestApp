package ic2.core.item.armor;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.IElectricItem;
import ic2.core.item.ElectricItem;
import ic2.core.item.armor.ItemArmorJetpack;
import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class ItemArmorJetpackElectric extends ItemArmorJetpack implements IElectricItem {

   public ItemArmorJetpackElectric(int id, int index, int armorrendering) {
      super(id, index, armorrendering);
      this.setMaxDamage(27);
      this.setMaxStackSize(1);
   }

   public int getCharge(ItemStack itemStack) {
      return ElectricItem.discharge(itemStack, Integer.MAX_VALUE, Integer.MAX_VALUE, true, true);
   }

   public void use(ItemStack itemStack, int amount) {
      ElectricItem.discharge(itemStack, amount, Integer.MAX_VALUE, true, false);
   }

   public boolean canProvideEnergy() {
      return false;
   }

   public int getChargedItemId() {
      return super.itemID;
   }

   public int getEmptyItemId() {
      return super.itemID;
   }

   public int getMaxCharge() {
      return 30000;
   }

   public int getTier() {
      return 1;
   }

   public int getTransferLimit() {
      return 60;
   }

   @SideOnly(Side.CLIENT)
   public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List itemList) {
      if(this.getChargedItemId() == super.itemID) {
         ItemStack charged = new ItemStack(this, 1);
         ElectricItem.charge(charged, Integer.MAX_VALUE, Integer.MAX_VALUE, true, false);
         itemList.add(charged);
      }

      if(this.getEmptyItemId() == super.itemID) {
         itemList.add(new ItemStack(this, 1, this.getMaxDamage()));
      }

   }
}
