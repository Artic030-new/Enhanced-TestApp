package ic2.core.block;

import java.util.Random;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;

public class BlockIC2Door extends BlockDoor {

   public int spriteIndexTop;
   public int spriteIndexBottom;
   public int itemDropped;


   public BlockIC2Door(int id, int topsprite, int bottomsprite, Material mat) {
      super(id, mat);
      this.spriteIndexTop = topsprite;
      this.spriteIndexBottom = bottomsprite;
      super.blockIndexInTexture = 14;
      this.disableStats();
      this.setRequiresSelfNotify();
   }

   public BlockIC2Door setItemDropped(int itemid) {
      this.itemDropped = itemid;
      return this;
   }

   public int getBlockTextureFromSideAndMetadata(int side, int meta) {
      return (meta & 8) == 8?this.spriteIndexTop:this.spriteIndexBottom;
   }

   public int getBlockTexture(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
      return this.getBlockTextureFromSideAndMetadata(par5, par1IBlockAccess.getBlockMetadata(par2, par3, par4));
   }

   public int idDropped(int meta, Random random, int j) {
      return (meta & 8) == 8?0:this.itemDropped;
   }

   public String getTextureFile() {
      return "/ic2/sprites/block_0.png";
   }
}
