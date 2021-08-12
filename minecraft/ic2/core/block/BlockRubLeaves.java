package ic2.core.block;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.Ic2Items;
import ic2.core.item.block.ItemRubLeaves;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;

public class BlockRubLeaves extends BlockLeaves implements IShearable {

   int[] b;


   public BlockRubLeaves(int id) {
      super(id, 52);
      this.setTickRandomly(true);
      this.setHardness(0.2F);
      this.setLightOpacity(1);
      this.setStepSound(Block.soundGrassFootstep);
      this.setBlockName("leaves");
      this.disableStats();
      super.graphicsLevel = true;
      Ic2Items.rubberLeaves = new ItemStack(this);
      GameRegistry.registerBlock(this, ItemRubLeaves.class, "rubberLeaves");
   }

   public int getBlockTextureFromSideAndMetadata(int i, int j) {
      return super.blockIndexInTexture;
   }

   @SideOnly(Side.CLIENT)
   public int getRenderColor(int i) {
      return ColorizerFoliage.getFoliageColorBirch();
   }

   @SideOnly(Side.CLIENT)
   public int colorMultiplier(IBlockAccess iblockaccess, int i, int j, int k) {
      return ColorizerFoliage.getFoliageColorBirch();
   }

   public int quantityDropped(Random random) {
      return random.nextInt(35) != 0?0:1;
   }

   public int idDropped(int i, Random random, int j) {
      return Ic2Items.rubberSapling.itemID;
   }

   public int damageDropped(int i) {
      return 0;
   }

   @SideOnly(Side.CLIENT)
   public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
      return par1IBlockAccess.getBlockId(par2, par3, par4) == super.blockID?super.graphicsLevel:!par1IBlockAccess.isBlockOpaqueCube(par2, par3, par4);
   }

   public void dropBlockAsItemWithChance(World par1World, int par2, int par3, int par4, int par5, float par6, int par7) {
      if(!par1World.isRemote && par1World.rand.nextInt(35) == 0) {
         int var9 = this.idDropped(par5, par1World.rand, par7);
         this.dropBlockAsItem_do(par1World, par2, par3, par4, new ItemStack(var9, 1, this.damageDropped(par5)));
      }

   }

   public boolean isShearable(ItemStack item, World world, int x, int y, int z) {
      return true;
   }

   public ArrayList onSheared(ItemStack item, World world, int x, int y, int z, int fortune) {
      ArrayList ret = new ArrayList();
      ret.add(new ItemStack(this, 1, world.getBlockMetadata(x, y, z) & 3));
      return ret;
   }

   public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs, List par3List) {
      par3List.add(new ItemStack(par1, 1, 0));
   }
}
