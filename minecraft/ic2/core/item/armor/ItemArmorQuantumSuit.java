package ic2.core.item.armor;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.IMetalArmor;
import ic2.core.IC2;
import ic2.core.IC2Potion;
import ic2.core.IItemTickListener;
import ic2.core.Ic2Items;
import ic2.core.item.ElectricItem;
import ic2.core.item.ItemTinCan;
import ic2.core.item.armor.ItemArmorElectric;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ISpecialArmor.ArmorProperties;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingFallEvent;

public class ItemArmorQuantumSuit extends ItemArmorElectric implements IMetalArmor, IItemTickListener {

   public static Map speedTickerMap = new HashMap();
   public static Map jumpChargeMap = new HashMap();
   public static Map enableQuantumSpeedOnSprintMap = new HashMap();


   public ItemArmorQuantumSuit(int id, int index, int armorrendering, int armorType) {
      super(id, index, armorrendering, armorType, 1000000, 1000, 3);
      if(armorType == 3) {
         MinecraftForge.EVENT_BUS.register(this);
      }

   }

   public ArmorProperties getProperties(EntityLiving player, ItemStack armor, DamageSource source, double damage, int slot) {
      if(source == DamageSource.fall && super.armorType == 3) {
         int energyPerDamage = this.getEnergyPerDamage();
         int damageLimit = energyPerDamage > 0?25 * ElectricItem.discharge(armor, Integer.MAX_VALUE, Integer.MAX_VALUE, true, true) / energyPerDamage:0;
         return new ArmorProperties(10, 1.0D, damageLimit);
      } else {
         return super.getProperties(player, armor, source, damage, slot);
      }
   }

   @ForgeSubscribe
   public void onEntityLivingFallEvent(LivingFallEvent event) {
      if(IC2.platform.isSimulating() && event.entity instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)event.entity;
         ItemStack armor = player.inventory.armorInventory[0];
         if(armor != null && armor.itemID == super.itemID) {
            int fallDamage = (int)event.distance - 3;
            int energyCost = this.getEnergyPerDamage() * fallDamage;
            if(energyCost <= ElectricItem.discharge(armor, Integer.MAX_VALUE, Integer.MAX_VALUE, true, true)) {
               ElectricItem.discharge(armor, energyCost, Integer.MAX_VALUE, true, false);
               event.setCanceled(true);
            }
         }
      }

   }

   public double getDamageAbsorptionRatio() {
      return super.armorType == 1?1.1D:1.0D;
   }

   public int getEnergyPerDamage() {
      return 900;
   }

   public boolean isMetalArmor(ItemStack itemstack, EntityPlayer player) {
      return true;
   }

   @SideOnly(Side.CLIENT)
   public EnumRarity getRarity(ItemStack stack) {
      return EnumRarity.rare;
   }

   public boolean onTick(EntityPlayer player, ItemStack itemStack) {
      boolean ret = false;
      switch(super.armorType) {
      case 0:
         IC2.platform.profilerStartSection("QuantumHelmet");
         int air = player.getAir();
         if(ElectricItem.canUse(itemStack, 1000) && air < 100) {
            player.setAir(air + 200);
            ElectricItem.use(itemStack, 1000, (EntityPlayer)null);
            ret = true;
         } else if(air <= 0) {
            IC2.achievements.issueAchievement(player, "starveWithQHelmet");
         }

         if(ElectricItem.canUse(itemStack, 1000) && player.getFoodStats().needFood()) {
            int poison = -1;

            for(int radiation = 0; radiation < player.inventory.mainInventory.length; ++radiation) {
               if(player.inventory.mainInventory[radiation] != null && player.inventory.mainInventory[radiation].itemID == Ic2Items.filledTinCan.itemID) {
                  poison = radiation;
                  break;
               }
            }

            if(poison > -1) {
               ItemTinCan var12 = (ItemTinCan)player.inventory.mainInventory[poison].getItem();
               player.getFoodStats().addStats(var12.getHealAmount(), var12.getSaturationModifier());
               var12.func_77849_c(player.inventory.mainInventory[poison], player.worldObj, player);
               var12.onEaten(player);
               if(--player.inventory.mainInventory[poison].stackSize <= 0) {
                  player.inventory.mainInventory[poison] = null;
               }

               ElectricItem.use(itemStack, 1000, (EntityPlayer)null);
               ret = true;
            }
         } else if(player.getFoodStats().getFoodLevel() <= 0) {
            IC2.achievements.issueAchievement(player, "starveWithQHelmet");
         }

         PotionEffect var11 = player.getActivePotionEffect(Potion.poison);
         if(var11 != null && ElectricItem.canUse(itemStack, 10000 * var11.getAmplifier())) {
            ElectricItem.use(itemStack, 10000 * var11.getAmplifier(), (EntityPlayer)null);
            IC2.platform.removePotion(player, Potion.poison.id);
         }

         PotionEffect var13 = player.getActivePotionEffect(IC2Potion.radiation);
         if(var13 != null && ElectricItem.canUse(itemStack, 20000 * var13.getAmplifier())) {
            ElectricItem.use(itemStack, 20000 * var13.getAmplifier(), (EntityPlayer)null);
            IC2.platform.removePotion(player, IC2Potion.radiation.id);
         }

         PotionEffect wither = player.getActivePotionEffect(Potion.wither);
         if(wither != null && ElectricItem.canUse(itemStack, 25000 * wither.getAmplifier())) {
            ElectricItem.use(itemStack, 25000 * wither.getAmplifier(), (EntityPlayer)null);
            IC2.platform.removePotion(player, Potion.wither.id);
         }

         IC2.platform.profilerEndSection();
         break;
      case 1:
         IC2.platform.profilerStartSection("QuantumBodyarmor");
         player.extinguish();
         IC2.platform.profilerEndSection();
         break;
      case 2:
         IC2.platform.profilerStartSection("QuantumLeggings");
         boolean enableQuantumSpeedOnSprint = true;
         if(IC2.platform.isRendering()) {
            enableQuantumSpeedOnSprint = IC2.enableQuantumSpeedOnSprint;
         } else if(enableQuantumSpeedOnSprintMap.containsKey(player)) {
            enableQuantumSpeedOnSprint = ((Boolean)enableQuantumSpeedOnSprintMap.get(player)).booleanValue();
         }

         if(ElectricItem.canUse(itemStack, 1000) && (player.onGround && Math.abs(player.motionX) + Math.abs(player.motionZ) > 0.10000000149011612D || player.isInWater()) && (enableQuantumSpeedOnSprint && player.isSprinting() || !enableQuantumSpeedOnSprint && IC2.keyboard.isBoostKeyDown(player))) {
            int var14 = speedTickerMap.containsKey(player)?((Integer)speedTickerMap.get(player)).intValue():0;
            ++var14;
            if(var14 >= 10) {
               var14 = 0;
               ElectricItem.use(itemStack, 1000, (EntityPlayer)null);
               ret = true;
            }

            speedTickerMap.put(player, Integer.valueOf(var14));
            float speed = 0.22F;
            if(player.isInWater()) {
               speed = 0.1F;
               if(player.isJumping) {
                  player.motionY += 0.10000000149011612D;
               }
            }

            if(speed > 0.0F) {
               player.moveFlying(0.0F, 1.0F, speed);
            }
         }

         IC2.platform.profilerEndSection();
         break;
      case 3:
         IC2.platform.profilerStartSection("QuantumBoots");
         float jumpCharge = jumpChargeMap.containsKey(player)?((Float)jumpChargeMap.get(player)).floatValue():1.0F;
         if(ElectricItem.canUse(itemStack, 1000) && player.onGround && jumpCharge < 1.0F) {
            jumpCharge = 1.0F;
            ElectricItem.use(itemStack, 1000, (EntityPlayer)null);
            ret = true;
         }

         if(player.motionY >= 0.0D && jumpCharge > 0.0F && !player.isInWater()) {
            if(IC2.keyboard.isJumpKeyDown(player) && IC2.keyboard.isBoostKeyDown(player)) {
               if(jumpCharge == 1.0F) {
                  player.motionX *= 3.5D;
                  player.motionZ *= 3.5D;
               }

               player.motionY += (double)(jumpCharge * 0.3F);
               jumpCharge = (float)((double)jumpCharge * 0.75D);
            } else if(jumpCharge < 1.0F) {
               jumpCharge = 0.0F;
            }
         }

         jumpChargeMap.put(player, Float.valueOf(jumpCharge));
         IC2.platform.profilerEndSection();
      }

      return ret;
   }

   public static void removePlayerReferences(EntityPlayer player) {
      speedTickerMap.remove(player);
      jumpChargeMap.remove(player);
      enableQuantumSpeedOnSprintMap.remove(player);
   }

}
