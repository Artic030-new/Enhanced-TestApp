package ic2.core.item.armor;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.item.armor.ItemArmorElectric;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;

public class ItemArmorLappack extends ItemArmorElectric {

   public ItemArmorLappack(int id, int index, int armorrendering) {
      super(id, index, armorrendering, 1, 300000, 250, 2);
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

   @SideOnly(Side.CLIENT)
   public EnumRarity getRarity(ItemStack stack) {
      return EnumRarity.uncommon;
   }
}
