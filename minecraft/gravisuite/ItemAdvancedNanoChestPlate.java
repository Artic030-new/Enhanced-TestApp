package gravisuite;

import gravisuite.GraviSuite;
import gravisuite.ItemAdvancedJetPack;
import gravisuite.audio.AudioSource;
import ic2.api.ElectricItem;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.ISpecialArmor.ArmorProperties;

public class ItemAdvancedNanoChestPlate extends ItemAdvancedJetPack {

   public ItemAdvancedNanoChestPlate(int par1, EnumArmorMaterial par2EnumArmorMaterial, int par3, int par4) {
      super(par1, par2EnumArmorMaterial, par3, par4);
      this.setIconIndex(15);
      this.setMaxDamage(27);
      this.setCreativeTab(GraviSuite.ic2Tab);
   }

   public ArmorProperties getProperties(EntityLiving entityliving, ItemStack itemstack, DamageSource damagesource, double d, int i) {
      double d1 = this.getBaseAbsorptionRatio() * this.getDamageAbsorptionRatio();
      int j = this.getEnergyPerDamage();
      int k = j <= 0?0:ElectricItem.discharge(itemstack, Integer.MAX_VALUE, Integer.MAX_VALUE, true, true) / j;
      return new ArmorProperties(0, d1, k);
   }

   public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) {
      return ElectricItem.discharge(armor, Integer.MAX_VALUE, Integer.MAX_VALUE, true, true) >= this.getEnergyPerDamage()?(int)Math.round(20.0D * this.getBaseAbsorptionRatio() * this.getDamageAbsorptionRatio()):0;
   }

   public int getEnergyPerDamage() {
      return 800;
   }

   public double getDamageAbsorptionRatio() {
      return 0.9D;
   }

   private double getBaseAbsorptionRatio() {
      return 0.4D;
   }

   public String getTextureFile() {
      return "/gravisuite/gravi_items.png";
   }

   public String getArmorTextureFile(ItemStack itemstack) {
      return "/gravisuite/advNanoChestPlate.png";
   }

}
