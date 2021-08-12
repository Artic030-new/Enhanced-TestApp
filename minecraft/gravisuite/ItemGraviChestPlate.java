package gravisuite;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gravisuite.GraviSuite;
import ic2.api.ElectricItem;
import ic2.api.IElectricItem;
import ic2.api.IMetalArmor;
import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.IArmorTextureProvider;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.common.ISpecialArmor.ArmorProperties;

public class ItemGraviChestPlate extends ItemArmor implements IArmorTextureProvider, IElectricItem, IMetalArmor, ISpecialArmor {

   public static int maxCharge;
   public static int minCharge;
   public static int transferLimit;
   public static int tier;
   public static int dischargeOnTick;
   public static float boostSpeed;
   public static int boostMultiplier;

   public ItemGraviChestPlate(int par1, EnumArmorMaterial par2EnumArmorMaterial, int par3, int par4) {
      super(par1, par2EnumArmorMaterial, par3, par4);
      this.setIconIndex(0);
      this.setMaxDamage(27);
      maxCharge = 10000000;
      transferLimit = 20000;
      tier = 2;
      minCharge = 80000;
      dischargeOnTick = 416;
      boostSpeed = 0.11F;
      boostMultiplier = 3;
      this.setCreativeTab(GraviSuite.ic2Tab);
   }

   public ArmorProperties getProperties(EntityLiving entityliving, ItemStack itemstack, DamageSource damagesource, double d, int i) {
      double d1 = this.getBaseAbsorptionRatio() * this.getDamageAbsorptionRatio();
      int j = this.getEnergyPerDamage();
      int k = j > 0?25 * ElectricItem.discharge(itemstack, Integer.MAX_VALUE, Integer.MAX_VALUE, true, true) / j:0;
      return new ArmorProperties(0, d1, k);
   }

   public static int getCharge(ItemStack itemstack) {
      NBTTagCompound nbttagcompound = GraviSuite.getOrCreateNbtData(itemstack);
      int k = nbttagcompound.getInteger("charge");
      return k;
   }

   public static void setCharge(ItemStack itemstack, int newCharge) {
      NBTTagCompound nbttagcompound = GraviSuite.getOrCreateNbtData(itemstack);
      nbttagcompound.setInteger("charge", newCharge);
      System.out.println(newCharge);
   }

   public int getEnergyPerDamage() {
      return 900;
   }

   public double getDamageAbsorptionRatio() {
      return 1.1D;
   }

   private double getBaseAbsorptionRatio() {
      return 0.4D;
   }

   public String getTextureFile() {
      return "/gravisuite/gravi_items.png";
   }

   public String getArmorTextureFile(ItemStack itemstack) {
      return "/gravisuite/graviChestPlate.png";
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
      return tier;
   }

   public int getTransferLimit() {
      return transferLimit;
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

   public boolean isRepairable() {
      return false;
   }

   public int getItemEnchantability() {
      return 0;
   }

   @SideOnly(Side.CLIENT)
   public EnumRarity getRarity(ItemStack itemstack) {
      return EnumRarity.epic;
   }
}
