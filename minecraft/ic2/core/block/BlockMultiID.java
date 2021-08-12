package ic2.core.block;

import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.block.BlockContainerCommon;
import ic2.core.block.TileEntityBlock;
import ic2.core.util.StackUtil;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockMultiID extends BlockContainerCommon {

   public static final int[][] sideAndFacingToSpriteOffset = new int[][]{{3, 2, 0, 0, 0, 0}, {2, 3, 1, 1, 1, 1}, {1, 1, 3, 2, 5, 4}, {0, 0, 2, 3, 4, 5}, {4, 5, 4, 5, 3, 2}, {5, 4, 5, 4, 2, 3}};


   protected BlockMultiID(int i, Material mat) {
      super(i, mat);
      this.setCreativeTab(IC2.tabIC2);
   }

   public int getBlockTexture(IBlockAccess iblockaccess, int i, int j, int k, int side) {
      TileEntity te = iblockaccess.getBlockTileEntity(i, j, k);
      short facing = te instanceof TileEntityBlock?((TileEntityBlock)te).getFacing():0;
      int meta = iblockaccess.getBlockMetadata(i, j, k);
      return isActive(iblockaccess, i, j, k)?meta + (sideAndFacingToSpriteOffset[side][facing] + 6) * 16:meta + sideAndFacingToSpriteOffset[side][facing] * 16;
   }

   public int getBlockTextureFromSideAndMetadata(int side, int meta) {
      return meta + sideAndFacingToSpriteOffset[side][3] * 16;
   }

   public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float a, float b, float c) {
      if(entityPlayer.isSneaking()) {
         return false;
      } else {
         TileEntity te = world.getBlockTileEntity(x, y, z);
         return te instanceof IHasGui?(IC2.platform.isSimulating()?IC2.platform.launchGui(entityPlayer, (IHasGui)te):true):false;
      }
   }

   public ArrayList getBlockDropped(World world, int x, int y, int z, int metadata, int fortune) {
      ArrayList ret = super.getBlockDropped(world, x, y, z, metadata, fortune);
      TileEntity te = world.getBlockTileEntity(x, y, z);
      if(te instanceof IInventory) {
         IInventory inv = (IInventory)te;

         for(int i = 0; i < inv.getSizeInventory(); ++i) {
            ItemStack itemStack = inv.getStackInSlot(i);
            if(itemStack != null) {
               ret.add(itemStack);
               inv.setInventorySlotContents(i, (ItemStack)null);
            }
         }
      }

      return ret;
   }

   public TileEntityBlock createNewTileEntity(World world) {
      return null;
   }

   public abstract TileEntityBlock createNewTileEntity(World var1, int var2);

   public void breakBlock(World world, int x, int y, int z, int a, int b) {
      TileEntity te = world.getBlockTileEntity(x, y, z);
      if(te instanceof TileEntityBlock) {
         ((TileEntityBlock)te).onBlockBreak(a, b);
      }

      boolean firstItem = true;
      Iterator it = this.getBlockDropped(world, x, y, z, world.getBlockMetadata(x, y, z), 0).iterator();

      while(it.hasNext()) {
         ItemStack itemStack = (ItemStack)it.next();
         if(firstItem) {
            firstItem = false;
         } else {
            StackUtil.dropAsEntity(world, x, y, z, itemStack);
         }
      }

      super.breakBlock(world, x, y, z, a, b);
   }

   public void onBlockPlacedBy(World world, int i, int j, int k, EntityLiving entityliving) {
      if(IC2.platform.isSimulating()) {
         TileEntityBlock te = (TileEntityBlock)world.getBlockTileEntity(i, j, k);
         if(entityliving == null) {
            te.setFacing((short)2);
         } else {
            int l = MathHelper.floor_double((double)(entityliving.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
            switch(l) {
            case 0:
               te.setFacing((short)2);
               break;
            case 1:
               te.setFacing((short)5);
               break;
            case 2:
               te.setFacing((short)3);
               break;
            case 3:
               te.setFacing((short)4);
            }
         }

      }
   }

   public static boolean isActive(IBlockAccess iblockaccess, int i, int j, int k) {
      TileEntity te = iblockaccess.getBlockTileEntity(i, j, k);
      return te instanceof TileEntityBlock?((TileEntityBlock)te).getActive():false;
   }

   public void getSubBlocks(int j, CreativeTabs tabs, List itemList) {
      for(int i = 0; i < 16; ++i) {
         ItemStack is = new ItemStack(this, 1, i);
         if(Item.itemsList[super.blockID].getItemNameIS(is) != null) {
            itemList.add(is);
         }
      }

   }

   public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
      return new ItemStack(this, 1, world.getBlockMetadata(x, y, z));
   }

}
