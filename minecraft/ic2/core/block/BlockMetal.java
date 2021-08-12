package ic2.core.block;

import cpw.mods.fml.common.registry.GameRegistry;
import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.item.block.ItemBlockMetal;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class BlockMetal extends Block {

   public BlockMetal(int i) {
      super(i, Material.iron);
      this.setHardness(4.0F);
      this.setStepSound(Block.soundMetalFootstep);
      this.setCreativeTab(IC2.tabIC2);
      Ic2Items.bronzeBlock = new ItemStack(this, 1, 2);
      Ic2Items.copperBlock = new ItemStack(this, 1, 0);
      Ic2Items.tinBlock = new ItemStack(this, 1, 1);
      Ic2Items.uraniumBlock = new ItemStack(this, 1, 3);
      GameRegistry.registerBlock(this, ItemBlockMetal.class, "blockMetal");
   }

   public int damageDropped(int i) {
      return i;
   }

   public int getBlockTextureFromSideAndMetadata(int side, int meta) {
      switch(meta) {
      case 0:
         return 93;
      case 1:
         return 94;
      case 2:
         return 78;
      case 3:
         return side < 2?79:95;
      default:
         return 0;
      }
   }

   public String getTextureFile() {
      return "/ic2/sprites/block_0.png";
   }

   public void getSubBlocks(int j, CreativeTabs tabs, List itemList) {
      for(int i = 0; i < 16; ++i) {
         ItemStack is = new ItemStack(this, 1, i);
         if(Item.itemsList[super.blockID].getItemNameIS(is) != null) {
            itemList.add(is);
         }
      }

   }

   public boolean isBeaconBase(World worldObj, int x, int y, int z, int beaconX, int beaconY, int beaconZ) {
      int meta = worldObj.getBlockMetadata(x, y, z);
      return meta == 2 || meta == 3;
   }
}
