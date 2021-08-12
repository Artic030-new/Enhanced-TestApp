package ic2.core.block.machine;

import cpw.mods.fml.common.registry.GameRegistry;
import ic2.core.Ic2Items;
import ic2.core.block.BlockTex;
import ic2.core.item.block.ItemBlockRare;
import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class BlockMiningTip extends BlockTex {

   public BlockMiningTip(int id, int sprite) {
      super(id, sprite, Material.iron);
      this.setHardness(6.0F);
      this.setResistance(10.0F);
      this.setBlockName("blockMiningTip");
      Ic2Items.miningPipeTip = new ItemStack(this);
      GameRegistry.registerBlock(this, ItemBlockRare.class, "miningPipeTip");
   }

   public boolean canPlaceBlockAt(World world, int i, int j, int k) {
      return false;
   }

   public int idDropped(int i, Random random, int j) {
      return Ic2Items.miningPipe.itemID;
   }

   public void getSubBlocks(int i, CreativeTabs tabs, List itemList) {}
}
