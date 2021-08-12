package ic2.core.block;

import cpw.mods.fml.common.registry.GameRegistry;
import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.block.TileEntityCrop;
import ic2.core.item.block.ItemBlockRare;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockCrop extends BlockContainer {

   public static TileEntityCrop tempStore;


   public BlockCrop(int id) {
      super(id, Material.plants);
      this.setHardness(0.8F);
      this.setResistance(0.2F);
      this.setBlockName("blockCrop");
      this.setStepSound(Block.soundGrassFootstep);
      this.setCreativeTab(IC2.tabIC2);
      Ic2Items.crop = new ItemStack(this, 1, 0);
      GameRegistry.registerBlock(this, ItemBlockRare.class, "crop");
   }

   public TileEntity createNewTileEntity(World world) {
      return new TileEntityCrop();
   }

   public String getTextureFile() {
      return "/ic2/sprites/crops_0.png";
   }

   public int getBlockTexture(IBlockAccess iblockaccess, int i, int j, int k, int side) {
      return ((TileEntityCrop)iblockaccess.getBlockTileEntity(i, j, k)).getSprite();
   }

   public int getBlockTextureFromSideAndMetadata(int side, int meta) {
      return 0;
   }

   public boolean canPlaceBlockAt(World world, int i, int j, int k) {
      return world.getBlockId(i, j - 1, k) == Block.tilledField.blockID && super.canPlaceBlockAt(world, i, j, k);
   }

   public void onNeighborBlockChange(World world, int i, int j, int k, int l) {
      super.onNeighborBlockChange(world, i, j, k, l);
      if(world.getBlockId(i, j - 1, k) != Block.tilledField.blockID) {
         world.setBlockWithNotify(i, j, k, 0);
         this.dropBlockAsItem(world, i, j, k, 0, 0);
      } else {
         ((TileEntityCrop)world.getBlockTileEntity(i, j, k)).onNeighbourChange();
      }

   }

   public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int i, int j, int k) {
      double d = 0.2D;
      return AxisAlignedBB.getBoundingBox(d, 0.0D, d, 1.0D - d, 0.7D, 1.0D - d);
   }

   public void onEntityCollidedWithBlock(World world, int i, int j, int k, Entity entity) {
      ((TileEntityCrop)world.getBlockTileEntity(i, j, k)).onEntityCollision(entity);
   }

   public boolean isOpaqueCube() {
      return false;
   }

   public boolean renderAsNormalBlock() {
      return false;
   }

   public int getRenderType() {
      return IC2.platform.getRenderId("crop");
   }

   public boolean isProvidingWeakPower(IBlockAccess iblockaccess, int i, int j, int k, int l) {
      return ((TileEntityCrop)iblockaccess.getBlockTileEntity(i, j, k)).emitRedstone();
   }

   public void breakBlock(World world, int i, int j, int k, int a, int b) {
      if(world != null) {
         tempStore = (TileEntityCrop)world.getBlockTileEntity(i, j, k);
      }

      super.breakBlock(world, i, j, k, a, b);
   }

   public void onBlockDestroyedByExplosion(World world, int i, int j, int k) {
      if(tempStore != null) {
         tempStore.onBlockDestroyed();
      }

   }

   public int getLightValue(IBlockAccess iblockaccess, int i, int j, int k) {
      return ((TileEntityCrop)iblockaccess.getBlockTileEntity(i, j, k)).getEmittedLight();
   }

   public void onBlockClicked(World world, int i, int j, int k, EntityPlayer entityplayer) {
      ((TileEntityCrop)world.getBlockTileEntity(i, j, k)).leftclick(entityplayer);
   }

   public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer entityplayer, int side, float a, float b, float c) {
      return ((TileEntityCrop)world.getBlockTileEntity(i, j, k)).rightclick(entityplayer);
   }
}
