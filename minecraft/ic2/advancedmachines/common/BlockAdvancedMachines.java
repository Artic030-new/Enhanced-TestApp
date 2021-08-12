package ic2.advancedmachines.common;

import ic2.advancedmachines.client.AdvancedMachinesClient;
import ic2.advancedmachines.common.AdvancedMachines;
import ic2.advancedmachines.common.TileEntityAdvancedMachine;
import ic2.advancedmachines.common.TileEntityBlock;
import ic2.advancedmachines.common.TileEntityCentrifugeExtractor;
import ic2.advancedmachines.common.TileEntityRotaryMacerator;
import ic2.advancedmachines.common.TileEntitySingularityCompressor;
import ic2.api.Items;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.core.block.BlockMultiID;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class BlockAdvancedMachines extends BlockContainer {

   public int[][] sprites;
   private final int idWrench;
   private final int idEWrench;

   public BlockAdvancedMachines(int id) {
      super(id, Material.iron);
      this.setHardness(2.0F);
      this.setStepSound(Block.soundMetalFootstep);
      this.sprites = new int[][]{{86, 20, 86, 19, 86, 21, 86, 19}, {86, 26, 86, 27, 86, 26, 86, 28}, {86, 86, 24, 22, 86, 86, 25, 23}};
      super.blockIndexInTexture = this.sprites[0][0];
      this.idWrench = Items.getItem("wrench").itemID;
      this.idEWrench = Items.getItem("electricWrench").itemID;
      this.setCreativeTab(AdvancedMachines.ic2Tab);
   }

   public int getBlockTexture(IBlockAccess world, int x, int y, int z, int blockSide) {
      int blockMeta = world.getBlockMetadata(x, y, z);
      TileEntity te = world.getBlockTileEntity(x, y, z);
      short facing = te instanceof TileEntityBlock?((TileEntityBlock)te).getFacing():0;
      return isActive(world, x, y, z)?blockMeta + (BlockMultiID.sideAndFacingToSpriteOffset[blockSide][facing] + 6) * 16:blockMeta + BlockMultiID.sideAndFacingToSpriteOffset[blockSide][facing] * 16;
   }

   public int getBlockTextureFromSideAndMetadata(int blockSide, int metaData) {
      return metaData + BlockMultiID.sideAndFacingToSpriteOffset[blockSide][3] * 16;
   }

   public TileEntity createNewTileEntity(World world) {
      return null;
   }

   public TileEntity createNewTileEntity(World world, int meta) {
      return this.getBlockEntity(meta);
   }

   public void onBlockAdded(World world, int x, int y, int z) {
      super.onBlockAdded(world, x, y, z);
   }

   public ArrayList getBlockDropped(World world, int x, int y, int z, int metadata, int fortune) {
      ArrayList var7 = super.getBlockDropped(world, x, y, z, metadata, fortune);
      TileEntity var8 = world.getBlockTileEntity(x, y, z);
      if(var8 instanceof IInventory) {
         IInventory var9 = (IInventory)var8;

         for(int var10 = 0; var10 < var9.getSizeInventory(); ++var10) {
            ItemStack var11 = var9.getStackInSlot(var10);
            if(var11 != null) {
               var7.add(var11);
               var9.setInventorySlotContents(var10, (ItemStack)null);
            }
         }
      }

      return var7;
   }

   public void breakBlock(World world, int x, int y, int z, int blockID, int blockMeta) {
      boolean var5 = true;

      for(Iterator iter = this.getBlockDropped(world, x, y, z, world.getBlockMetadata(x, y, z), 0).iterator(); iter.hasNext(); var5 = false) {
         ItemStack var7 = (ItemStack)iter.next();
         if(!var5) {
            if(var7 == null) {
               return;
            }

            double var8 = 0.7D;
            double var10 = (double)world.rand.nextFloat() * var8 + (1.0D - var8) * 0.5D;
            double var12 = (double)world.rand.nextFloat() * var8 + (1.0D - var8) * 0.5D;
            double var14 = (double)world.rand.nextFloat() * var8 + (1.0D - var8) * 0.5D;
            EntityItem var16 = new EntityItem(world, (double)x + var10, (double)y + var12, (double)z + var14, var7);
            var16.delayBeforeCanPickup = 10;
            world.spawnEntityInWorld(var16);
            return;
         }
         
         world.removeBlockTileEntity(x, y, z);
         super.breakBlock(world, x, y, z, blockID, blockMeta);
      }
   }

   public int idDropped(int var1, Random var2, int var3) {
      return Items.getItem("advancedMachine").itemID;
   }

   public int getDamageValue(World world, int x, int y, int z) {
      return world.getBlockMetadata(x, y, z);
   }

   public String getTextureFile() {
      return "/ic2/advancedmachines/client/sprites/block_advmachine.png";
   }

   public int getGui(World var1, int var2, int var3, int var4, EntityPlayer var5) {
      switch(var1.getBlockMetadata(var2, var3, var4)) {
      case 0:
         return AdvancedMachines.guiIdRotary;
      case 1:
         return AdvancedMachines.guiIdSingularity;
      case 2:
         return AdvancedMachines.guiIdCentrifuge;
      default:
         return 0;
      }
   }

   public TileEntityAdvancedMachine getBlockEntity(int metadata) {
      switch(metadata) {
      case 0:
         return new TileEntityRotaryMacerator();
      case 1:
         return new TileEntitySingularityCompressor();
      case 2:
         return new TileEntityCentrifugeExtractor();
      default:
         return null;
      }
   }

   public void onBlockPlacedBy(World world, int x, int y, int z, EntityLiving player) {
      int heading = MathHelper.floor_double((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
      TileEntityAdvancedMachine te = (TileEntityAdvancedMachine)world.getBlockTileEntity(x, y, z);
      switch(heading) {
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

      super.onBlockPlacedBy(world, x, y, z, player);
   }

   public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int s, float f1, float f2, float f3) {
	  if(entityPlayer.isSneaking()) {
	     return false;
	  } else if(world.isRemote) {
	     return true;
	  } else if(entityPlayer.getCurrentEquippedItem() != null && (entityPlayer.getCurrentEquippedItem().itemID == this.idWrench || entityPlayer.getCurrentEquippedItem().itemID == this.idEWrench)) {
	     TileEntityAdvancedMachine team = (TileEntityAdvancedMachine)world.getBlockTileEntity(x, y, z);
	     if(team != null) {
	       return true;
	    } else {
	       return false;
	    }
	    } else {
	      entityPlayer.openGui(AdvancedMachines.instance, 0, world, x, y, z);
	      return true;
	    }
     }

   public static boolean isActive(IBlockAccess var0, int var1, int var2, int var3) {
      return ((TileEntityAdvancedMachine)var0.getBlockTileEntity(var1, var2, var3)).getActive();
   }

   public static int getFacing(IBlockAccess var0, int var1, int var2, int var3) {
      return ((TileEntityAdvancedMachine)var0.getBlockTileEntity(var1, var2, var3)).getFacing();
   }

   public static float getWrenchRate(IBlockAccess var0, int var1, int var2, int var3) {
      return ((TileEntityAdvancedMachine)var0.getBlockTileEntity(var1, var2, var3)).getWrenchDropRate();
   }

   public void randomDisplayTick(World world, int x, int y, int z, Random random) {
      int var6 = world.getBlockMetadata(x, y, z);
      if((var6 == 0 || var6 == 1) && isActive(world, x, y, z)) {
         float var7 = (float)x + 1.0F;
         float var8 = (float)y + 1.0F;
         float var9 = (float)z + 1.0F;

         for(int var10 = 0; var10 < 4; ++var10) {
            float var11 = -0.2F - random.nextFloat() * 0.6F;
            float var12 = -0.1F + random.nextFloat() * 0.2F;
            float var13 = -0.2F - random.nextFloat() * 0.6F;
            world.spawnParticle("smoke", (double)(var7 + var11), (double)(var8 + var12), (double)(var9 + var13), 0.0D, 0.0D, 0.0D);
         }
      }

   }
}
