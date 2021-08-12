package ic2.core.item.armor;

import ic2.api.IElectricItem;
import ic2.core.IC2;
import ic2.core.item.ElectricItem;
import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.common.ISpecialArmor.ArmorProperties;

public abstract class ItemArmorElectric extends ItemArmor implements ISpecialArmor, IElectricItem {

   public int maxCharge;
   public int transferLimit;
   public int tier;


   public ItemArmorElectric(int itemId, int spriteIndex, int armorRendering, int armorType, int maxCharge, int transferLimit, int tier) {
      super(itemId, EnumArmorMaterial.DIAMOND, armorRendering, armorType);
      super.iconIndex = spriteIndex;
      this.maxCharge = maxCharge;
      this.tier = tier;
      this.transferLimit = transferLimit;
      this.setMaxDamage(27);
      this.setMaxStackSize(1);
      this.setCreativeTab(IC2.tabIC2);
   }

   public int getItemEnchantability() {
      return 0;
   }

   public boolean isRepairable() {
      return false;
   }

   public void getSubItems(int i, CreativeTabs tabs, List itemList) {
      ItemStack charged = new ItemStack(this, 1);
      ElectricItem.charge(charged, Integer.MAX_VALUE, Integer.MAX_VALUE, true, false);
      itemList.add(charged);
      itemList.add(new ItemStack(this, 1, this.getMaxDamage()));
   }

   public String getTextureFile() {
      return "/ic2/sprites/item_0.png";
   }

   public ArmorProperties getProperties(EntityLiving player, ItemStack armor, DamageSource source, double damage, int slot) {
      if(source.isUnblockable()) {
         return new ArmorProperties(0, 0.0D, 0);
      } else {
         double absorptionRatio = this.getBaseAbsorptionRatio() * this.getDamageAbsorptionRatio();
         int energyPerDamage = this.getEnergyPerDamage();
         int damageLimit = energyPerDamage > 0?25 * ElectricItem.discharge(armor, Integer.MAX_VALUE, Integer.MAX_VALUE, true, true) / energyPerDamage:0;
         return new ArmorProperties(0, absorptionRatio, damageLimit);
      }
   }

   public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) {
      return ElectricItem.discharge(armor, Integer.MAX_VALUE, Integer.MAX_VALUE, true, true) >= this.getEnergyPerDamage()?(int)Math.round(20.0D * this.getBaseAbsorptionRatio() * this.getDamageAbsorptionRatio()):0;
   }

   public void damageArmor(EntityLiving entity, ItemStack stack, DamageSource source, int damage, int slot) {
      ElectricItem.discharge(stack, damage * this.getEnergyPerDamage(), Integer.MAX_VALUE, true, false);
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
      return this.maxCharge;
   }

   public int getTier() {
      return this.tier;
   }

   public int getTransferLimit() {
      return this.transferLimit;
   }

   public boolean getIsRepairable(ItemStack par1ItemStack, ItemStack par2ItemStack) {
      return false;
   }

   public abstract double getDamageAbsorptionRatio();

   public abstract int getEnergyPerDamage();

   private double getBaseAbsorptionRatio() {
      switch(super.armorType) {
      case 0:
         return 0.15D;
      case 1:
         return 0.4D;
      case 2:
         return 0.3D;
      case 3:
         return 0.15D;
      default:
         return 0.0D;
      }
   }
}
