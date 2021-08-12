package gravisuite;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gravisuite.GraviSuite;
import ic2.api.ElectricItem;
import ic2.api.IElectricItem;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

public class ItemAdvDDrill extends ItemTool implements IElectricItem {

   private int maxCharge;
   private int tier;
   private float effPower;
   private int energyPerOperation;
   private int transferLimit;
   public Set mineableBlocks = new HashSet();
   public int soundTicker;
   public int co;

   protected ItemAdvDDrill(int par1, int par2, EnumToolMaterial par3EnumToolMaterial, Block[] par4ArrayOfBlock) {
      super(par1, par2, par3EnumToolMaterial, par4ArrayOfBlock);
      this.setIconIndex(8);
      this.setMaxDamage(27);
      this.maxCharge = 15000;
      this.transferLimit = 500;
      this.tier = 2;
      this.effPower = 35.0F;
      super.efficiencyOnProperMaterial = this.effPower;
      this.energyPerOperation = 160;
      this.co = 1;
      this.setCreativeTab(GraviSuite.ic2Tab);
      this.init();
   }

   public void init() {
      this.mineableBlocks.add(Block.cobblestone);
      this.mineableBlocks.add(Block.stoneSingleSlab);
      this.mineableBlocks.add(Block.stoneDoubleSlab);
      this.mineableBlocks.add(Block.stairCompactCobblestone);
      this.mineableBlocks.add(Block.stone);
      this.mineableBlocks.add(Block.sandStone);
      this.mineableBlocks.add(Block.stairsSandStone);
      this.mineableBlocks.add(Block.cobblestoneMossy);
      this.mineableBlocks.add(Block.oreIron);
      this.mineableBlocks.add(Block.blockSteel);
      this.mineableBlocks.add(Block.oreCoal);
      this.mineableBlocks.add(Block.blockGold);
      this.mineableBlocks.add(Block.oreGold);
      this.mineableBlocks.add(Block.oreDiamond);
      this.mineableBlocks.add(Block.blockDiamond);
      this.mineableBlocks.add(Block.ice);
      this.mineableBlocks.add(Block.netherrack);
      this.mineableBlocks.add(Block.oreLapis);
      this.mineableBlocks.add(Block.blockLapis);
      this.mineableBlocks.add(Block.oreRedstone);
      this.mineableBlocks.add(Block.oreRedstoneGlowing);
      this.mineableBlocks.add(Block.brick);
      this.mineableBlocks.add(Block.stairsBrick);
      this.mineableBlocks.add(Block.glowStone);
      this.mineableBlocks.add(Block.grass);
      this.mineableBlocks.add(Block.dirt);
      this.mineableBlocks.add(Block.mycelium);
      this.mineableBlocks.add(Block.sand);
      this.mineableBlocks.add(Block.gravel);
      this.mineableBlocks.add(Block.snow);
      this.mineableBlocks.add(Block.blockSnow);
      this.mineableBlocks.add(Block.blockClay);
      this.mineableBlocks.add(Block.tilledField);
      this.mineableBlocks.add(Block.stoneBrick);
      this.mineableBlocks.add(Block.stairsStoneBrickSmooth);
      this.mineableBlocks.add(Block.netherBrick);
      this.mineableBlocks.add(Block.stairsNetherBrick);
      this.mineableBlocks.add(Block.slowSand);
      this.mineableBlocks.add(Block.obsidian);
      this.mineableBlocks.add(Block.anvil);
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
      return block.blockMaterial != Material.rock && block.blockMaterial != Material.iron?this.mineableBlocks.contains(block):true;
   }

   public boolean hitEntity(ItemStack var1, EntityLiving var2, EntityLiving var3) {
      return true;
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
      if((double)Block.blocksList[par3].getBlockHardness(world, par4, par5, par6) != 0.0D) {
         if(entityliving instanceof EntityPlayer) {
            ElectricItem.use(stack, this.energyPerOperation, (EntityPlayer)entityliving);
         } else {
            ElectricItem.discharge(stack, this.energyPerOperation, this.tier, true, false);
         }
      }

      return true;
   }

   public String getRandomDrillSound() {
      switch(GraviSuite.random.nextInt(4)) {
      case 1:
         return "drillOne";
      case 2:
         return "drillTwo";
      case 3:
         return "drillThree";
      default:
         return "drill";
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
