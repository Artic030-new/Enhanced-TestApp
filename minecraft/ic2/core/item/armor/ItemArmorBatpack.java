package ic2.core.item.armor;

import ic2.core.item.armor.ItemArmorElectric;

public class ItemArmorBatpack extends ItemArmorElectric {

   public ItemArmorBatpack(int id, int index, int armorrendering) {
      super(id, index, armorrendering, 1, '\uea60', 100, 1);
   }

   public boolean canProvideEnergy() {
      return true;
   }

   public double getDamageAbsorptionRatio() {
      return 0.0D;
   }

   public int getEnergyPerDamage() {
      return 0;
   }
}
