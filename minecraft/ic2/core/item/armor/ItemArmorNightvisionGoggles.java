package ic2.core.item.armor;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.IElectricItem;
import ic2.api.Ic2Recipes;
import ic2.core.IC2;
import ic2.core.IItemTickListener;
import ic2.core.Ic2Items;
import ic2.core.item.ElectricItem;
import ic2.core.item.armor.ItemArmorUtility;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumSkyBlock;

public class ItemArmorNightvisionGoggles extends ItemArmorUtility implements IElectricItem, IItemTickListener {

   public ItemArmorNightvisionGoggles(int id, int spriteIndex, int renderIndex) {
      super(id, 127, renderIndex, 0);

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
      return 20000;
   }

   public int getTier() {
      return 1;
   }

   public int getTransferLimit() {
      return 200;
   }

   public boolean onTick(EntityPlayer player, ItemStack itemStack) {
      if(player.worldObj.isRemote) {
         return false;
      } else {
         ElectricItem.chargeFromArmor(itemStack, player);
         if(ElectricItem.discharge(itemStack, 1, 1, true, false) >= 1) {
            if(player.worldObj.isDaytime()) {
               int x = MathHelper.floor_double(player.posX);
               int z = MathHelper.floor_double(player.posZ);
               int skylight = player.worldObj.getChunkFromBlockCoords(x, z).getSavedLightValue(EnumSkyBlock.Sky, x & 15, MathHelper.floor_double(player.posY), z & 15);
               if(skylight > 12) {
                  IC2.platform.removePotion(player, Potion.nightVision.id);
                  player.addPotionEffect(new PotionEffect(Potion.blindness.id, 100 + (skylight - 13) * 50, 0, true));
               }
            } else {
               IC2.platform.removePotion(player, Potion.blindness.id);
               player.addPotionEffect(new PotionEffect(Potion.nightVision.id, 250, 0, true));
            }

            return true;
         } else {
            return false;
         }
      }
   }

   @SideOnly(Side.CLIENT)
   public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List itemList) {
      if(this.getChargedItemId() == super.itemID) {
         ItemStack charged = new ItemStack(this, 1);
         ElectricItem.charge(charged, Integer.MAX_VALUE, Integer.MAX_VALUE, true, false);
         itemList.add(charged);
      }

      if(this.getEmptyItemId() == super.itemID) {
         itemList.add(new ItemStack(this, 1, this.getMaxDamage()));
      }

   }
}
