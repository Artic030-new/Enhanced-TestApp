package gravisuite;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gravisuite.GraviSuite;
import gravisuite.ItemGraviTool;
import ic2.api.ElectricItem;
import ic2.api.IElectricItem;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class ItemVajra extends ItemTool implements IElectricItem {

   private int maxCharge;
   private int tier;
   private float effPower;
   private int energyPerOperation;

   protected ItemVajra(int par1, int par2, EnumToolMaterial par3EnumToolMaterial, Block[] par4ArrayOfBlock) {
      super(par1, par2, par3EnumToolMaterial, par4ArrayOfBlock);
      this.setIconIndex(7);
      this.setMaxDamage(27);
      this.maxCharge = 1000000;
      this.tier = 2;
      this.effPower = 20000.0F;
      super.efficiencyOnProperMaterial = this.effPower;
      this.energyPerOperation = 3333;
      this.setCreativeTab(GraviSuite.ic2Tab);
   }

   public boolean onItemUse(ItemStack itemstack, EntityPlayer entityplayer, World world, int i, int j, int k, int side, float a, float b, float c) {
      if(!GraviSuite.disableVajraAccurate) {
         int blockId = world.getBlockId(i, j, k);
         int metaData = world.getBlockMetadata(i, j, k);
         Block block = Block.blocksList[blockId];
         if(block != Block.bedrock) {
            if(!ElectricItem.canUse(itemstack, this.energyPerOperation)) {
               return false;
            }

            if(GraviSuite.isSimulating()) {
               ArrayList drops = block.getBlockDropped(world, i, j, k, metaData, 0);
               Iterator iterator = drops.iterator();

               while(iterator.hasNext()) {
                  ItemStack itemStack = (ItemStack)iterator.next();
                  ItemGraviTool.dropAsEntity(world, i, j, k, itemStack);
               }

               world.setBlockWithNotify(i, j, k, 0);
               world.playSoundEffect((double)((float)i + 0.5F), (double)((float)j + 0.5F), (double)((float)k + 0.5F), block.stepSound.getBreakSound(), (block.stepSound.getVolume() + 1.0F) / 2.0F, block.stepSound.getPitch() * 0.8F);
               ElectricItem.use(itemstack, this.energyPerOperation, entityplayer);
            }

            return true;
         }
      }

      return false;
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
      return 10000;
   }

   public boolean canHarvestBlock(Block block) {
      return true;
   }

   public int getDamageVsEntity(Entity entity) {
      return 1;
   }

   public boolean hitEntity(ItemStack stack, EntityLiving entity, EntityLiving player) {
      if(ElectricItem.use(stack, this.energyPerOperation * 2, (EntityPlayer)player)) {
    	 entity.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer)player), 25);
      } else {
    	 entity.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer)player), 1);
      }

      return false;
   }

   public String getTextureFile() {
      return "/gravisuite/gravi_items.png";
   }

   public float getStrVsBlock(ItemStack stack, Block block) {
      return ElectricItem.canUse(stack, this.energyPerOperation)?this.effPower:0.5F;
   }

   public boolean onBlockDestroyed(ItemStack itemstack, World par2World, int par3, int par4, int par5, int par6, EntityLiving entityliving) {
      if(entityliving instanceof EntityPlayer) {
         ElectricItem.use(itemstack, this.energyPerOperation, (EntityPlayer)entityliving);
      } else {
         ElectricItem.discharge(itemstack, this.energyPerOperation, this.tier, true, false);
      }

      return true;
   }

   public boolean isRepairable() {
      return false;
   }

   public int getItemEnchantability() {
      return 0;
   }

   public void getSubItems(int id, CreativeTabs table, List list) {
      ItemStack var4 = new ItemStack(this, 1);
      ElectricItem.charge(var4, Integer.MAX_VALUE, Integer.MAX_VALUE, true, false);
      list.add(var4);
      list.add(new ItemStack(this, 1, this.getMaxDamage()));
   }

   @SideOnly(Side.CLIENT)
   public EnumRarity getRarity(ItemStack itemstack) {
      return EnumRarity.epic;
   }
}
