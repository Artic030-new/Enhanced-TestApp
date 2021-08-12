package ic2.core.block;

import cpw.mods.fml.common.registry.GameRegistry;
import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.block.TileEntityBarrel;
import ic2.core.item.block.ItemBlockRare;
import ic2.core.util.StackUtil;
import java.util.ArrayList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockBarrel extends BlockContainer {

   public BlockBarrel(int i) {
      super(i, 44, Material.wood);
      this.setHardness(1.0F);
      this.setStepSound(Block.soundWoodFootstep);
      Ic2Items.blockBarrel = new ItemStack(this);
      GameRegistry.registerBlock(this, ItemBlockRare.class, "blockBarrel");
   }

   public String getTextureFile() {
      return "/ic2/sprites/block_0.png";
   }

   public int getBlockTexture(IBlockAccess iblockaccess, int i, int j, int k, int side) {
      int treetap = ((TileEntityBarrel)iblockaccess.getBlockTileEntity(i, j, k)).treetapSide;
      return treetap > 1 && side == treetap?29:this.getBlockTextureFromSide(side);
   }

   public int getBlockTextureFromSide(int side) {
      return side < 2?28:44;
   }

   public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float a, float b, float c) {
      return ((TileEntityBarrel)world.getBlockTileEntity(x, y, z)).rightclick(entityPlayer);
   }

   public void onBlockClicked(World world, int x, int y, int z, EntityPlayer player) {
      TileEntityBarrel barrel = (TileEntityBarrel)world.getBlockTileEntity(x, y, z);
      if(barrel.treetapSide > 1) {
         if(IC2.platform.isSimulating()) {
            StackUtil.dropAsEntity(world, x, y, z, new ItemStack(Ic2Items.treetap.getItem()));
         }

         barrel.treetapSide = 0;
         barrel.update();
         barrel.drainLiquid(1);
      } else {
         if(IC2.platform.isSimulating()) {
            StackUtil.dropAsEntity(world, x, y, z, new ItemStack(Ic2Items.barrel.getItem(), 1, barrel.calculateMetaValue()));
         }

         world.setBlockWithNotify(x, y, z, Ic2Items.scaffold.itemID);
      }
   }

   public TileEntity createNewTileEntity(World world) {
      return new TileEntityBarrel();
   }

   public ArrayList getBlockDropped(World world, int x, int y, int z, int metadata, int fortune) {
      ArrayList re = new ArrayList();
      re.add(new ItemStack(Ic2Items.scaffold.getItem()));
      re.add(new ItemStack(Ic2Items.barrel.getItem(), 1, 0));
      return re;
   }
}
