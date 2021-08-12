package ic2.core.item.armor;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.IMetalArmor;
import ic2.core.IC2;
import ic2.core.item.ElectricItem;
import ic2.core.item.armor.ItemArmorElectric;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ISpecialArmor.ArmorProperties;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingFallEvent;

public class ItemArmorNanoSuit extends ItemArmorElectric implements IMetalArmor {

   public ItemArmorNanoSuit(int id, int index, int armorrendering, int armorType) {
      super(id, index, armorrendering, armorType, 100000, 160, 2);
      if(armorType == 3) {
         MinecraftForge.EVENT_BUS.register(this);
      }

   }

   public ArmorProperties getProperties(EntityLiving player, ItemStack armor, DamageSource source, double damage, int slot) {
      if(source == DamageSource.fall && super.armorType == 3) {
         int energyPerDamage = this.getEnergyPerDamage();
         int damageLimit = energyPerDamage > 0?25 * ElectricItem.discharge(armor, Integer.MAX_VALUE, Integer.MAX_VALUE, true, true) / energyPerDamage:0;
         return new ArmorProperties(10, damage < 8.0D?1.0D:0.875D, damageLimit);
      } else {
         return super.getProperties(player, armor, source, damage, slot);
      }
   }

   @ForgeSubscribe
   public void onEntityLivingFallEvent(LivingFallEvent event) {
      if(IC2.platform.isSimulating() && event.entity instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)event.entity;
         ItemStack armor = player.inventory.armorInventory[0];
         if(armor != null && armor.getItem() == this) {
            int fallDamage = (int)event.distance - 3;
            if(fallDamage >= 8) {
               return;
            }

            int energyCost = this.getEnergyPerDamage() * fallDamage;
            if(energyCost <= ElectricItem.discharge(armor, Integer.MAX_VALUE, Integer.MAX_VALUE, true, true)) {
               ElectricItem.discharge(armor, energyCost, Integer.MAX_VALUE, true, false);
               event.setCanceled(true);
            }
         }
      }

   }

   public double getDamageAbsorptionRatio() {
      return 0.9D;
   }

   public int getEnergyPerDamage() {
      return 800;
   }

   public boolean isMetalArmor(ItemStack itemstack, EntityPlayer player) {
      return true;
   }

   @SideOnly(Side.CLIENT)
   public EnumRarity getRarity(ItemStack stack) {
      return EnumRarity.uncommon;
   }
}
