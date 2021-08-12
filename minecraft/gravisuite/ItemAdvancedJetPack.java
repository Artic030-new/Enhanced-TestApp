package gravisuite;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gravisuite.GraviSuite;
import gravisuite.IItemTickListener;
import gravisuite.Keyboard;
import gravisuite.ServerProxy;
import gravisuite.audio.AudioManager;
import gravisuite.audio.AudioSource;
import ic2.api.ElectricItem;
import ic2.api.IElectricItem;
import ic2.api.IMetalArmor;
import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.IArmorTextureProvider;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.common.ISpecialArmor.ArmorProperties;

public class ItemAdvancedJetPack extends ItemArmor implements IArmorTextureProvider, IElectricItem, IItemTickListener, IMetalArmor, ISpecialArmor {

   public static int maxCharge;
   private int transferLimit;
   private int tier;
   public static int energyPerTick;
   public static int boostMultiplier;
   private static byte toggleTimer;
   private static boolean lastJetpackUsed = false;
   public AudioSource audioSource;

   public ItemAdvancedJetPack(int par1, EnumArmorMaterial par2EnumArmorMaterial, int par3, int par4) {
      super(par1, par2EnumArmorMaterial, par3, par4);
      this.setIconIndex(13);
      this.setMaxDamage(27);
      maxCharge = 1000000;
      energyPerTick = 12;
      boostMultiplier = 5;
      this.transferLimit = 1000;
      this.tier = 2;
      toggleTimer = 10;
      this.setCreativeTab(GraviSuite.ic2Tab);
   }

   public ArmorProperties getProperties(EntityLiving entityliving, ItemStack itemstack, DamageSource damagesource, double d, int i) {
      double d1 = this.getBaseAbsorptionRatio() * this.getDamageAbsorptionRatio();
      int j = this.getEnergyPerDamage();
      int k = j <= 0?0:ElectricItem.discharge(itemstack, Integer.MAX_VALUE, Integer.MAX_VALUE, true, true) / j;
      return new ArmorProperties(0, d1, k);
   }

   public static int getCharge(ItemStack itemstack) {
      NBTTagCompound nbttagcompound = GraviSuite.getOrCreateNbtData(itemstack);
      int k = nbttagcompound.getInteger("charge");
      return k;
   }

   public static int switchWorkMode(EntityPlayer player, ItemStack itemstack) {
      if(readWorkMode(itemstack)) {
         saveWorkMode(itemstack, false);
         ServerProxy.sendPlayerMessage(player, "§eРежим парения §cотключен.");
      } else {
         saveWorkMode(itemstack, true);
         ServerProxy.sendPlayerMessage(player, "§eРежим парения §aвключен.");
      }

      return 0;
   }

   public static int switchFlyState(EntityPlayer player, ItemStack itemstack) {
      if(readFlyStatus(itemstack)) {
         saveFlyStatus(itemstack, false);
         ServerProxy.sendPlayerMessage(player, "§eРеактивный ранец §cвыключен");
      } else {
         saveFlyStatus(itemstack, true);
         ServerProxy.sendPlayerMessage(player, "§eРеактивный ранец §aвключен");
      }

      return 0;
   }

   public static boolean useJetpack(EntityPlayer player, boolean hoverMode) {
      ItemStack itemstack = player.inventory.armorInventory[2];
      int currCharge = getCharge(itemstack);
      if(currCharge < energyPerTick && !player.capabilities.isCreativeMode) {
         return false;
      } else {
         float var5 = 1.0F;
         float var6 = 0.001F;
         float bcoff = (float)maxCharge / 20.0F;
         if((float)getCharge(itemstack) / (float)maxCharge <= var6) {
            var5 *= (float)getCharge(itemstack) / bcoff;
         }

         if(player.capabilities.isCreativeMode) {
            var5 = 1.0F;
         }

         Keyboard var10000 = GraviSuite.keyboard;
         if(Keyboard.isForwardKeyDown(player)) {
            float var141 = 0.3F;
            if(hoverMode) {
               var141 = 0.65F;
            }

            float var91 = var5 * var141 * 2.0F;
            float var9 = 0.0F;
            var10000 = GraviSuite.keyboard;
            if(Keyboard.isBoostKeyDown(player) && (currCharge > energyPerTick * boostMultiplier || player.capabilities.isCreativeMode)) {
               var9 = 0.09F;
               if(hoverMode) {
                  var9 = 0.07F;
               }
            }

            if(var91 > 0.0F) {
               player.moveFlying(0.0F, 0.4F * var91 + var9, 0.02F + var9);
               if(var9 > 0.0F && !player.capabilities.isCreativeMode) {
                  use(itemstack, energyPerTick * boostMultiplier);
               }
            }
         }

         int var1411 = player.worldObj.getHeight();
         double var911 = player.posY;
         if(var911 > (double)(var1411 - 25)) {
            if(var911 > (double)var1411) {
               var911 = (double)var1411;
            }

            var5 = (float)((double)var5 * (((double)var1411 - var911) / 25.0D));
         }

         double var11 = player.motionY;
         player.motionY = Math.min(player.motionY + (double)(var5 * 0.2F), 0.6000000238418579D);
         if(hoverMode) {
            float var13 = -0.1F;
            var10000 = GraviSuite.keyboard;
            if(Keyboard.isJumpKeyDown(player)) {
               var13 = 0.1F;
            }

            if(player.motionY > (double)var13) {
               player.motionY = (double)var13;
               if(var11 > player.motionY) {
                  player.motionY = var11;
               }
            }
         }

         if(!player.capabilities.isCreativeMode) {
            use(itemstack, energyPerTick);
         }

         player.fallDistance = 0.0F;
         player.distanceWalkedModified = 0.0F;
         if(player instanceof EntityPlayerMP) {
            ((EntityPlayerMP)player).playerNetServerHandler.ticksForFloatKick = 0;
         }

         return true;
      }
   }

   public boolean onTick(EntityPlayer var1, ItemStack var2) {
      NBTTagCompound var3 = GraviSuite.getOrCreateNbtData(var2);
      boolean var4 = readWorkMode(var2);
      byte var5 = var3.getByte("toggleTimer");
      boolean var6 = false;
      Keyboard var10000 = GraviSuite.keyboard;
      if((Keyboard.isJumpKeyDown(var1) || var4 && var1.motionY < -0.3499999940395355D) && readFlyStatus(var2)) {
         var6 = useJetpack(var1, var4);
      }

      if(ServerProxy.isSimulating() && var5 > 0) {
         --var5;
         var3.setByte("toggleTimer", var5);
      }

      if(!GraviSuite.isSimulating()) {
         if(lastJetpackUsed != var6) {
            if(var6) {
               if(this.audioSource == null) {
                  this.audioSource = AudioManager.createSource(var1, AudioManager.PositionSpec.Backpack, "JetpackLoop.ogg", true, false, AudioManager.defaultVolume);
               }

               if(this.audioSource != null) {
                  this.audioSource.play();
               }
            } else if(this.audioSource != null) {
               this.audioSource.remove();
               this.audioSource = null;
            }

            lastJetpackUsed = var6;
         }

         if(this.audioSource != null) {
            this.audioSource.updatePosition();
         }
      }

      return var6;
   }

   public static void use(ItemStack item, int value) {
      ElectricItem.discharge(item, value, Integer.MAX_VALUE, true, false);
   }

   public int getEnergyPerDamage() {
      return 0;
   }

   public double getDamageAbsorptionRatio() {
      return 0.0D;
   }

   private double getBaseAbsorptionRatio() {
      return 0.0D;
   }

   public String getTextureFile() {
      return "/gravisuite/gravi_items.png";
   }

   public String getArmorTextureFile(ItemStack itemstack) {
      return "/gravisuite/advanced_jetpack.png";
   }

   public boolean canProvideEnergy() {
      return true;
   }

   public int getChargedItemId() {
      return super.itemID;
   }

   public int getEmptyItemId() {
      return super.itemID;
   }

   public int getMaxCharge() {
      return maxCharge;
   }

   public int getTier() {
      return this.tier;
   }

   public int getTransferLimit() {
      return this.transferLimit;
   }

   public boolean isMetalArmor(ItemStack itemstack, EntityPlayer player) {
      return true;
   }

   public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) {
      return (int)Math.round(20.0D * this.getBaseAbsorptionRatio() * this.getDamageAbsorptionRatio());
   }

   public void damageArmor(EntityLiving entity, ItemStack itemstack, DamageSource source, int damage, int slot) {
      ElectricItem.discharge(itemstack, damage * this.getEnergyPerDamage(), Integer.MAX_VALUE, true, false);
   }

   public boolean isRepairable() {
      return false;
   }

   public int getItemEnchantability() {
      return 0;
   }

   public static boolean readWorkMode(ItemStack itemstack) {
      NBTTagCompound nbttagcompound = GraviSuite.getOrCreateNbtData(itemstack);
      return nbttagcompound.getBoolean("isHoverActive");
   }

   public static boolean saveWorkMode(ItemStack itemstack, boolean workMode) {
      NBTTagCompound nbttagcompound = GraviSuite.getOrCreateNbtData(itemstack);
      nbttagcompound.setBoolean("isHoverActive", workMode);
      nbttagcompound.setByte("toggleTimer", toggleTimer);
      return true;
   }

   public static boolean readFlyStatus(ItemStack itemstack) {
      NBTTagCompound nbttagcompound = GraviSuite.getOrCreateNbtData(itemstack);
      return nbttagcompound.getBoolean("isFlyActive");
   }

   public static boolean saveFlyStatus(ItemStack itemstack, boolean flyMode) {
      NBTTagCompound nbttagcompound = GraviSuite.getOrCreateNbtData(itemstack);
      nbttagcompound.setBoolean("isFlyActive", flyMode);
      return true;
   }

   public void getSubItems(int id, CreativeTabs table, List list) {
      ItemStack var4 = new ItemStack(this, 1);
      ElectricItem.charge(var4, Integer.MAX_VALUE, Integer.MAX_VALUE, true, false);
      list.add(var4);
      list.add(new ItemStack(this, 1, this.getMaxDamage()));
   }

   @SideOnly(Side.CLIENT)
   public EnumRarity getRarity(ItemStack itemstack) {
      return EnumRarity.uncommon;
   }

}
