package ic2.core.item.armor;

import ic2.core.IC2;
import ic2.core.IC2DamageSource;
import ic2.core.IItemTickListener;
import ic2.core.Ic2Items;
import ic2.core.item.armor.ItemArmorUtility;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFluid;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ISpecialArmor.ArmorProperties;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingFallEvent;

public class ItemArmorHazmat extends ItemArmorUtility implements IItemTickListener {

   public ItemArmorHazmat(int id, int spriteIndex, int renderIndex, int type) {
      super(id, spriteIndex, renderIndex, type);
      this.setMaxDamage(64);
      this.setCreativeTab(IC2.tabIC2);
      if(super.armorType == 3) {
         MinecraftForge.EVENT_BUS.register(this);
      }

   }

   public ArmorProperties getProperties(EntityLiving player, ItemStack armor, DamageSource source, double damage, int slot) {
      if(super.armorType == 0 && this.hazmatAbsorbs(source) && hasCompleteHazmat(player)) {
         if(source == DamageSource.inFire || source == DamageSource.lava) {
            player.addPotionEffect(new PotionEffect(Potion.fireResistance.id, 60, 1));
         }

         return new ArmorProperties(10, 1.0D, Integer.MAX_VALUE);
      } else {
         return super.armorType == 3 && source == DamageSource.fall?new ArmorProperties(10, damage < 8.0D?1.0D:0.875D, (armor.getMaxDamage() - armor.getItemDamage() + 1) * 2 * 25):new ArmorProperties(0, 1.0D, (armor.getMaxDamage() - armor.getItemDamage() + 1) / 2 * 25);
      }
   }

   public void damageArmor(EntityLiving entity, ItemStack stack, DamageSource source, int damage, int slot) {
      if(!this.hazmatAbsorbs(source) || !hasCompleteHazmat(entity)) {
         int damageTotal = damage * 2;
         if(super.armorType == 3 && source == DamageSource.fall) {
            damageTotal = (damage + 1) / 2;
         }

         stack.damageItem(damageTotal, entity);
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

            int armorDamage = (fallDamage + 1) / 2;
            if(armorDamage <= armor.getMaxDamage() - armor.getItemDamage() && armorDamage >= 0) {
               armor.damageItem(armorDamage, player);
               event.setCanceled(true);
            }
         }
      }

   }

   public boolean isRepairable() {
      return true;
   }

   public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) {
      return 1;
   }

   public boolean onTick(EntityPlayer player, ItemStack itemStack) {
      if(super.armorType == 0) {
         if(player.isBurning() && hasCompleteHazmat(player)) {
            if(this.isInLava(player)) {
               player.addPotionEffect(new PotionEffect(Potion.fireResistance.id, 20, 0, true));
            }

            player.extinguish();
         }

         if(player.getAir() <= 100 && player.inventory.hasItem(Ic2Items.airCell.itemID)) {
            player.inventory.consumeInventoryItem(Ic2Items.airCell.itemID);
            player.inventory.addItemStackToInventory(new ItemStack(Ic2Items.cell.getItem()));
            player.setAir(player.getAir() + 150);
            return true;
         }
      }

      return false;
   }

   public boolean isInLava(EntityPlayer player) {
      double var2 = player.posY + 0.02D;
      int var4 = MathHelper.floor_double(player.posX);
      int var5 = MathHelper.floor_float((float)MathHelper.floor_double(var2));
      int var6 = MathHelper.floor_double(player.posZ);
      int var7 = player.worldObj.getBlockId(var4, var5, var6);
      if(var7 != 0 && (Block.blocksList[var7].blockMaterial == Material.lava || Block.blocksList[var7].blockMaterial == Material.fire)) {
         float var8 = BlockFluid.getFluidHeightPercent(player.worldObj.getBlockMetadata(var4, var5, var6)) - 0.11111111F;
         float var9 = (float)(var5 + 1) - var8;
         return var2 < (double)var9;
      } else {
         return false;
      }
   }

   public static boolean hasCompleteHazmat(EntityLiving living) {
      if(!(living instanceof EntityPlayer)) {
         return false;
      } else {
         EntityPlayer player = (EntityPlayer)living;
         ItemStack[] armor = player.inventory.armorInventory;
         return armor[0] != null && armor[0].getItem() instanceof ItemArmorHazmat && armor[1] != null && armor[1].getItem() instanceof ItemArmorHazmat && armor[2] != null && armor[2].getItem() instanceof ItemArmorHazmat && armor[3] != null && armor[3].getItem() instanceof ItemArmorHazmat;
      }
   }

   public boolean hazmatAbsorbs(DamageSource source) {
      return source == DamageSource.inFire || source == DamageSource.inWall || source == DamageSource.lava || source == DamageSource.onFire || source == IC2DamageSource.electricity || source == IC2DamageSource.radiation;
   }
}
