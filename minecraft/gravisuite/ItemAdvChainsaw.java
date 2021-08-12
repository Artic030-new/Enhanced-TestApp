package gravisuite;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gravisuite.GraviSuite;
import ic2.api.ElectricItem;
import ic2.api.IElectricItem;
import ic2.api.Items;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.stats.StatList;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.EntityInteractEvent;

public class ItemAdvChainsaw extends ItemTool implements IElectricItem {

   private int maxCharge;
   private int tier;
   private float effPower;
   private int energyPerOperation;
   private int transferLimit;
   public Set mineableBlocks = new HashSet();
   public int soundTicker;
   public int co;

   protected ItemAdvChainsaw(int par1, int par2, EnumToolMaterial par3EnumToolMaterial, Block[] par4ArrayOfBlock) {
      super(par1, par2, par3EnumToolMaterial, par4ArrayOfBlock);
      this.setIconIndex(9);
      this.setMaxDamage(27);
      this.maxCharge = 15000;
      this.transferLimit = 500;
      this.tier = 2;
      this.effPower = 35.0F;
      super.efficiencyOnProperMaterial = this.effPower;
      this.energyPerOperation = 100;
      this.co = 1;
      this.setCreativeTab(GraviSuite.ic2Tab);
      MinecraftForge.EVENT_BUS.register(this);
   }

   public void init() {
      this.mineableBlocks.add(Block.planks);
      this.mineableBlocks.add(Block.bookShelf);
      this.mineableBlocks.add(Block.wood);
      this.mineableBlocks.add(Block.chest);
      this.mineableBlocks.add(Block.leaves);
      this.mineableBlocks.add(Block.web);
      this.mineableBlocks.add(Block.cloth);
      this.mineableBlocks.add(Block.pumpkin);
      this.mineableBlocks.add(Block.melon);
      this.mineableBlocks.add(Block.cactus);
      this.mineableBlocks.add(Block.snow);
      ItemStack tmpItem = Items.getItem("rubberLeaves");
      if(tmpItem != null) {
         this.mineableBlocks.add(Block.blocksList[tmpItem.itemID]);
      }

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
      return 2;
   }

   public int getTransferLimit() {
      return this.transferLimit;
   }

   public boolean canHarvestBlock(Block block) {
      return block.blockMaterial == Material.wood?true:this.mineableBlocks.contains(block);
   }

   public boolean hitEntity(ItemStack stack, EntityLiving entity, EntityLiving player) {
      if(ElectricItem.use(stack, this.energyPerOperation, (EntityPlayer)player) && ElectricItem.use(stack, this.energyPerOperation, (EntityPlayer)player)) {
    	 entity.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer)player), 13);
      } else {
    	 entity.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer)player), 1);
      }

      return false;
   }

   @ForgeSubscribe
   public void onEntityInteract(EntityInteractEvent var1) {
      Entity var2 = var1.target;
      if(!var2.worldObj.isRemote) {
         EntityPlayer var3 = var1.entityPlayer;
         ItemStack var4 = var3.inventory.mainInventory[var3.inventory.currentItem];
         if(var4 != null && var4.itemID == super.itemID && var2 instanceof IShearable && ElectricItem.use(var4, this.energyPerOperation * 2, var3)) {
            IShearable var5 = (IShearable)var2;
            if(var5.isShearable(var4, var2.worldObj, (int)var2.posX, (int)var2.posY, (int)var2.posZ)) {
               ArrayList var6 = var5.onSheared(var4, var2.worldObj, (int)var2.posX, (int)var2.posY, (int)var2.posZ, EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, var4));

               EntityItem var9;
               for(Iterator var7 = var6.iterator(); var7.hasNext(); var9.motionZ += (double)((Item.itemRand.nextFloat() - Item.itemRand.nextFloat()) * 0.1F)) {
                  ItemStack var8 = (ItemStack)var7.next();
                  var9 = var2.entityDropItem(var8, 1.0F);
                  var9.motionY += (double)(Item.itemRand.nextFloat() * 0.05F);
                  var9.motionX += (double)((Item.itemRand.nextFloat() - Item.itemRand.nextFloat()) * 0.1F);
               }
            }
         }
      }

   }

   public boolean onBlockStartBreak(ItemStack stack, int x, int y, int z, EntityPlayer player) {
      if(player.worldObj.isRemote) {
         return false;
      } else {
         int var6 = player.worldObj.getBlockId(x, y, z);
         if(Block.blocksList[var6] != null && Block.blocksList[var6] instanceof IShearable) {
            IShearable var7 = (IShearable)Block.blocksList[var6];
            if(var7.isShearable(stack, player.worldObj, x, y, z) && ElectricItem.use(stack, this.energyPerOperation, (EntityPlayer)null) && ElectricItem.use(stack, this.energyPerOperation, (EntityPlayer)null)) {
               ArrayList var8 = var7.onSheared(stack, player.worldObj, x, y, z, EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, stack));
               Iterator var9 = var8.iterator();

               while(var9.hasNext()) {
                  ItemStack var10 = (ItemStack)var9.next();
                  float var11 = 0.7F;
                  double var12 = (double)(Item.itemRand.nextFloat() * var11) + (double)(1.0F - var11) * 0.5D;
                  double var14 = (double)(Item.itemRand.nextFloat() * var11) + (double)(1.0F - var11) * 0.5D;
                  double var16 = (double)(Item.itemRand.nextFloat() * var11) + (double)(1.0F - var11) * 0.5D;
                  EntityItem var18 = new EntityItem(player.worldObj, (double)x + var12, (double)y + var14, (double)z + var16, var10);
                  var18.delayBeforeCanPickup = 10;
                  player.worldObj.spawnEntityInWorld(var18);
               }

               player.addStat(StatList.mineBlockStatArray[var6], 1);
            }
         }

         return false;
      }
   }

   public int getDamageVsEntity(Entity entity) {
      return this.co;
   }

   public int getItemEnchantability() {
      return 0;
   }

   public boolean isRepairable() {
      return false;
   }

   public String getTextureFile() {
      return "/gravisuite/gravi_items.png";
   }

   public float getStrVsBlock(ItemStack stack, Block block) {
      return !ElectricItem.canUse(stack, this.energyPerOperation)?1.0F:(ForgeHooks.isToolEffective(stack, block, 0)?super.efficiencyOnProperMaterial:(this.canHarvestBlock(block)?super.efficiencyOnProperMaterial:1.0F));
   }

   public float getStrVsBlock(ItemStack stack, Block block, int meta) {
      return !ElectricItem.canUse(stack, this.energyPerOperation)?1.0F:(ForgeHooks.isToolEffective(stack, block, meta)?super.efficiencyOnProperMaterial:(this.canHarvestBlock(block)?super.efficiencyOnProperMaterial:1.0F));
   }

   public boolean onBlockDestroyed(ItemStack stack, World world, int par3, int par4, int par5, int par6, EntityLiving entityliving) {
      if(Block.blocksList[par3] == null) {
         return false;
      } else {
         if((double)Block.blocksList[par3].getBlockHardness(world, par4, par5, par6) != 0.0D) {
            if(entityliving instanceof EntityPlayer) {
               ElectricItem.use(stack, this.energyPerOperation, (EntityPlayer)entityliving);
            } else {
               ElectricItem.discharge(stack, this.energyPerOperation, this.tier, true, false);
            }
         }

         return true;
      }
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
