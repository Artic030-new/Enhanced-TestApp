package ic2.core.item.tool;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.IElectricItem;
import ic2.core.IC2;
import ic2.core.item.ElectricItem;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

public abstract class ItemElectricTool extends ItemTool implements IElectricItem {

   public int co;
   public int operationEnergyCost;
   public int maxCharge;
   public int transferLimit;
   public int tier;
   public Set mineableBlocks = new HashSet();


   public ItemElectricTool(int id, int sprite, EnumToolMaterial toolmaterial, int operationEnergyCost) {
      super(id, 0, toolmaterial, new Block[0]);
      super.iconIndex = sprite;
      this.operationEnergyCost = operationEnergyCost;
      this.setMaxDamage(27);
      this.setMaxStackSize(1);
   }

   public float getStrVsBlock(ItemStack tool, Block block) {
      return !ElectricItem.canUse(tool, this.operationEnergyCost)?1.0F:(ForgeHooks.isToolEffective(tool, block, 0)?super.efficiencyOnProperMaterial:(this.canHarvestBlock(block)?super.efficiencyOnProperMaterial:1.0F));
   }

   public float getStrVsBlock(ItemStack tool, Block block, int md) {
      return !ElectricItem.canUse(tool, this.operationEnergyCost)?1.0F:(ForgeHooks.isToolEffective(tool, block, md)?super.efficiencyOnProperMaterial:(this.canHarvestBlock(block)?super.efficiencyOnProperMaterial:1.0F));
   }

   public boolean canHarvestBlock(Block block) {
      return this.mineableBlocks.contains(block);
   }

   public boolean hitEntity(ItemStack itemstack, EntityLiving entityliving, EntityLiving entityliving1) {
      return true;
   }

   public int getItemEnchantability() {
      return 0;
   }

   public boolean isRepairable() {
      return false;
   }

   public int getDamageVsEntity(Entity entity) {
      return this.co;
   }

   public String getTextureFile() {
      return "/ic2/sprites/item_0.png";
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

   public boolean onBlockDestroyed(ItemStack par1ItemStack, World par2World, int par3, int par4, int par5, int par6, EntityLiving par7EntityLiving) {
      if(Block.blocksList[par3] == null) {
         IC2.getInstance();
         IC2.log.severe("ItemElectricTool.onBlockDestroyed(): received invalid block id " + par3);
         return false;
      } else {
         if((double)Block.blocksList[par3].getBlockHardness(par2World, par4, par5, par6) != 0.0D) {
            if(par7EntityLiving instanceof EntityPlayer) {
               ElectricItem.use(par1ItemStack, this.operationEnergyCost, (EntityPlayer)par7EntityLiving);
            } else {
               ElectricItem.discharge(par1ItemStack, this.operationEnergyCost, this.tier, true, false);
            }
         }

         return true;
      }
   }

   public boolean getIsRepairable(ItemStack par1ItemStack, ItemStack par2ItemStack) {
      return false;
   }

   @SideOnly(Side.CLIENT)
   public void getSubItems(int i, CreativeTabs tabs, List itemList) {
      ItemStack charged = new ItemStack(this, 1);
      ElectricItem.charge(charged, Integer.MAX_VALUE, Integer.MAX_VALUE, true, false);
      itemList.add(charged);
      itemList.add(new ItemStack(this, 1, this.getMaxDamage()));
   }
}
