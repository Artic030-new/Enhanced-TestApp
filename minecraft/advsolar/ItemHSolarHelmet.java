package advsolar;

import advsolar.AdvancedSolarPanel;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.ElectricItem;
import ic2.api.IElectricItem;
import ic2.api.IMetalArmor;
import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.IArmorTextureProvider;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.common.ISpecialArmor.ArmorProperties;

public class ItemHSolarHelmet extends ItemArmor implements IArmorTextureProvider, IElectricItem, IMetalArmor, ISpecialArmor {

   public static int maxCharge;
   public static int transferLimit;
   public static int tier;
   public static int ticker;
   public static int generating;
   public int genDay;
   public int genNight;
   public int solarType;
   public static boolean initialized;
   public static boolean sunIsUp;
   public static boolean skyIsVisible;
   private static boolean noSunWorld;
   private static boolean wetBiome;

   public ItemHSolarHelmet(int par1, EnumArmorMaterial par2EnumArmorMaterial, int par3, int par4, int htype) {
      super(par1, par2EnumArmorMaterial, par3, par4);
      this.solarType = htype;
      maxCharge = 1000000;
      transferLimit = 5000;
      tier = 2;
      if(this.solarType == 1) {
         this.genDay = AdvancedSolarPanel.hGenDay;
         this.genNight = AdvancedSolarPanel.hGenNight;
         this.setIconIndex(12);
      }

      if(this.solarType == 2) {
         this.genDay = AdvancedSolarPanel.uhGenDay;
         this.genNight = AdvancedSolarPanel.uhGenNight;
         this.setIconIndex(13);
      }

      this.setMaxDamage(27);
      this.setCreativeTab(AdvancedSolarPanel.ic2Tab);
   }

   public ArmorProperties getProperties(EntityLiving entityliving, ItemStack itemstack, DamageSource damagesource, double damage, int slot) {
      double d1 = this.getBaseAbsorptionRatio() * this.getDamageAbsorptionRatio();
      int j = this.getEnergyPerDamage();
      int k = j <= 0?0:ElectricItem.discharge(itemstack, Integer.MAX_VALUE, Integer.MAX_VALUE, true, true) / j;
      return new ArmorProperties(0, d1, k);
   }

   public void onArmorTickUpdate(World worldObj, EntityPlayer player, ItemStack itemStack) {
      if(!worldObj.isRemote) {
         this.gainFuel(player);
         int airLevel = player.getAir();
         if(ElectricItem.canUse(itemStack, 1000) && airLevel < 100) {
            player.setAir(airLevel + 200);
            ElectricItem.use(itemStack, 1000, (EntityPlayer)null);
         }

         if(generating > 0) {
            int energyLeft = generating;

            int sentPacket;
            int j;
            for(j = 0; j < player.inventory.armorInventory.length; ++j) {
               if(energyLeft <= 0) {
                  return;
               }

               if(player.inventory.armorInventory[j] != null && Item.itemsList[player.inventory.armorInventory[j].itemID] instanceof IElectricItem) {
                  sentPacket = ElectricItem.charge(player.inventory.armorInventory[j], energyLeft, 3, false, false);
                  energyLeft -= sentPacket;
               }
            }

            for(j = 0; j < player.inventory.mainInventory.length; ++j) {
               if(energyLeft <= 0) {
                  return;
               }

               if(player.inventory.mainInventory[j] != null && Item.itemsList[player.inventory.mainInventory[j].itemID] instanceof IElectricItem) {
                  sentPacket = ElectricItem.charge(player.inventory.mainInventory[j], energyLeft, 3, false, false);
                  energyLeft -= sentPacket;
               }
            }
         }

      }
   }

   public int gainFuel(EntityPlayer player) {
      if(ticker++ % tickRate() == 0) {
         updateVisibility(player);
      }

      if(sunIsUp && skyIsVisible) {
         generating = 0 + this.genDay;
         return generating;
      } else if(skyIsVisible) {
         generating = 0 + this.genNight;
         return generating;
      } else {
         generating = 0;
         return generating;
      }
   }

   public static void updateVisibility(EntityPlayer player) {
      wetBiome = player.worldObj.getWorldChunkManager().getBiomeGenAt((int)player.posX, (int)player.posZ).getIntRainfall() > 0;
      noSunWorld = player.worldObj.provider.hasNoSky;
      Boolean rainWeather = Boolean.valueOf(wetBiome && (player.worldObj.isRaining() || player.worldObj.isThundering()));
      if(player.worldObj.isDaytime() && !rainWeather.booleanValue()) {
         sunIsUp = true;
      } else {
         sunIsUp = false;
      }

      if(player.worldObj.canBlockSeeTheSky((int)player.posX, (int)player.posY + 1, (int)player.posZ) && !noSunWorld) {
         skyIsVisible = true;
      } else {
         skyIsVisible = false;
      }

   }

   public int getEnergyPerDamage() {
      return 900;
   }

   public double getDamageAbsorptionRatio() {
      return 1.0D;
   }

   private double getBaseAbsorptionRatio() {
      return 0.15D;
   }

   public String getTextureFile() {
      return "/advsolar/texture/adv_items.png";
   }

   public String getArmorTextureFile(ItemStack itemstack) {
      return this.solarType == 1 ? "/advsolar/texture/hybridSolarHelmet.png":"/advsolar/texture/ultimateSolarHelmet.png";
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
      return maxCharge;
   }

   public int getTier() {
      return tier;
   }

   public int getTransferLimit() {
      return transferLimit;
   }

   public static int tickRate() {
      return 128;
   }

   public boolean isMetalArmor(ItemStack itemstack, EntityPlayer player) {
      return true;
   }

   public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) {
      return ElectricItem.discharge(armor, Integer.MAX_VALUE, Integer.MAX_VALUE, true, true) >= this.getEnergyPerDamage()?(int)Math.round(20.0D * this.getBaseAbsorptionRatio() * this.getDamageAbsorptionRatio()):0;
   }

   public void damageArmor(EntityLiving entity, ItemStack itemstack, DamageSource source, int damage, int slot) {
      ElectricItem.discharge(itemstack, damage * this.getEnergyPerDamage(), Integer.MAX_VALUE, true, false);
   }

   public void getSubItems(int id, CreativeTabs table, List list) {
      ItemStack var4 = new ItemStack(this, 1);
      ElectricItem.charge(var4, Integer.MAX_VALUE, Integer.MAX_VALUE, true, false);
      list.add(var4);
      list.add(new ItemStack(this, 1, this.getMaxDamage()));
   }

   @SideOnly(Side.CLIENT)
   public EnumRarity getRarity(ItemStack itemstack) {
      return this.solarType == 1 ? EnumRarity.rare : EnumRarity.epic;
   }
}
