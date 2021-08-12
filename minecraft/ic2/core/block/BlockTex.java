package ic2.core.block;

import ic2.core.Ic2Items;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class BlockTex extends Block {

   public BlockTex(int id, int sprite, Material mat) {
      super(id, sprite, mat);
   }

   public String getTextureFile() {
      return "/ic2/sprites/block_0.png";
   }

   public int idDropped(int i, Random random, int j) {
      return Ic2Items.uraniumOre != null && super.blockID == Ic2Items.uraniumOre.itemID?Ic2Items.uraniumDrop.itemID:super.blockID;
   }

   public void dropBlockAsItemWithChance(World par1World, int par2, int par3, int par4, int par5, float par6, int par7) {
      super.dropBlockAsItemWithChance(par1World, par2, par3, par4, par5, par6, par7);
      if(Ic2Items.uraniumOre != null && super.blockID == Ic2Items.uraniumOre.itemID) {
         this.dropXpOnBlockBreak(par1World, par2, par3, par4, MathHelper.getRandomIntegerInRange(par1World.rand, 1, 3));
      }

   }
}
