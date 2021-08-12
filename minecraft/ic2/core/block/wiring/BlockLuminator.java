package ic2.core.block.wiring;

import ic2.api.IElectricItem;
import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.block.BlockContainerCommon;
import ic2.core.block.BlockPoleFence;
import ic2.core.block.wiring.BlockCable;
import ic2.core.block.wiring.TileEntityLuminator;
import ic2.core.item.ElectricItem;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockLuminator extends BlockContainerCommon {

   boolean light;


   public BlockLuminator(int id, boolean l) {
      super(id, 31, Material.glass);
      this.setStepSound(Block.soundGlassFootstep);
      this.light = l;
      this.setHardness(0.3F);
      this.setResistance(0.5F);
      if(!this.light) {
         this.setCreativeTab(IC2.tabIC2);
      }

   }

   public int quantityDropped(Random random) {
      return 0;
   }

   public void onPostBlockPlaced(World world, int i, int j, int k, int direction) {
      world.setBlockMetadata(i, j, k, direction);
      super.onPostBlockPlaced(world, i, j, k, direction);
   }

   public boolean canPlaceBlockOnSide(World world, int i, int j, int k, int direction) {
      if(world.getBlockId(i, j, k) != 0) {
         return false;
      } else {
         switch(direction) {
         case 0:
            ++j;
            break;
         case 1:
            --j;
            break;
         case 2:
            ++k;
            break;
         case 3:
            --k;
            break;
         case 4:
            ++i;
            break;
         case 5:
            --i;
         }

         return isSupportingBlock(world, i, j, k);
      }
   }

   public static boolean isSupportingBlock(World world, int i, int j, int k) {
      return world.getBlockId(i, j, k) == 0?false:(world.isBlockOpaqueCube(i, j, k)?true:isSpecialSupporter(world, i, j, k));
   }

   public static boolean isSpecialSupporter(IBlockAccess world, int i, int j, int k) {
      Block block = Block.blocksList[world.getBlockId(i, j, k)];
      return block == null?false:(!(block instanceof BlockFence) && !(block instanceof BlockPoleFence) && !(block instanceof BlockCable)?block.blockID == Ic2Items.reinforcedGlass.itemID || block == Block.glass:true);
   }

   public boolean canBlockStay(World world, int i, int j, int k) {
      TileEntity te = world.getBlockTileEntity(i, j, k);
      if(te != null && ((TileEntityLuminator)te).ignoreBlockStay) {
         return true;
      } else {
         int facing = world.getBlockMetadata(i, j, k);
         switch(facing) {
         case 0:
            ++j;
            break;
         case 1:
            --j;
            break;
         case 2:
            ++k;
            break;
         case 3:
            --k;
            break;
         case 4:
            ++i;
            break;
         case 5:
            --i;
         }

         return isSupportingBlock(world, i, j, k);
      }
   }

   public void onNeighborBlockChange(World world, int i, int j, int k, int l) {
      if(!this.canBlockStay(world, i, j, k)) {
         world.setBlockWithNotify(i, j, k, 0);
      }

      super.onNeighborBlockChange(world, i, j, k, l);
   }

   public boolean renderAsNormalBlock() {
      return false;
   }

   public int getRenderType() {
      return IC2.platform.getRenderId("luminator");
   }

   public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int i, int j, int k) {
      float[] box = getBoxOfLuminator(world, i, j, k);
      return AxisAlignedBB.getBoundingBox((double)(box[0] + (float)i), (double)(box[1] + (float)j), (double)(box[2] + (float)k), (double)(box[3] + (float)i), (double)(box[4] + (float)j), (double)(box[5] + (float)k));
   }

   public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int i, int j, int k) {
      float[] box = getBoxOfLuminator(world, i, j, k);
      return AxisAlignedBB.getBoundingBox((double)(box[0] + (float)i), (double)(box[1] + (float)j), (double)(box[2] + (float)k), (double)(box[3] + (float)i), (double)(box[4] + (float)j), (double)(box[5] + (float)k));
   }

   public static float[] getBoxOfLuminator(IBlockAccess world, int i, int j, int k) {
      int facing = world.getBlockMetadata(i, j, k);
      float px = 0.0625F;
      switch(facing) {
      case 0:
         ++j;
         break;
      case 1:
         --j;
         break;
      case 2:
         ++k;
         break;
      case 3:
         --k;
         break;
      case 4:
         ++i;
         break;
      case 5:
         --i;
      }

      boolean fullCover = isSpecialSupporter(world, i, j, k);
      switch(facing) {
      case 1:
         return new float[]{0.0F, 0.0F, 0.0F, 1.0F, 1.0F * px, 1.0F};
      case 2:
         if(fullCover) {
            return new float[]{0.0F, 0.0F, 15.0F * px, 1.0F, 1.0F, 1.0F};
         }

         return new float[]{6.0F * px, 3.0F * px, 14.0F * px, 1.0F - 6.0F * px, 1.0F - 3.0F * px, 1.0F};
      case 3:
         if(fullCover) {
            return new float[]{0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F * px};
         }

         return new float[]{6.0F * px, 3.0F * px, 0.0F, 1.0F - 6.0F * px, 1.0F - 3.0F * px, 2.0F * px};
      case 4:
         if(fullCover) {
            return new float[]{15.0F * px, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F};
         }

         return new float[]{14.0F * px, 3.0F * px, 6.0F * px, 1.0F, 1.0F - 3.0F * px, 1.0F - 6.0F * px};
      case 5:
         if(fullCover) {
            return new float[]{0.0F, 0.0F, 0.0F, 1.0F * px, 1.0F, 1.0F};
         }

         return new float[]{0.0F, 3.0F * px, 6.0F * px, 2.0F * px, 1.0F - 3.0F * px, 1.0F - 6.0F * px};
      default:
         return fullCover?new float[]{0.0F, 15.0F * px, 0.0F, 1.0F, 1.0F, 1.0F}:new float[]{4.0F * px, 13.0F * px, 4.0F * px, 1.0F - 4.0F * px, 1.0F, 1.0F - 4.0F * px};
      }
   }

   public boolean isOpaqueCube() {
      return false;
   }

   public boolean isBlockNormalCube(World world, int i, int j, int k) {
      return false;
   }

   public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer entityplayer, int side, float a, float b, float c) {
      ItemStack itemStack = entityplayer.getCurrentEquippedItem();
      if(itemStack != null && itemStack.getItem() instanceof IElectricItem) {
         itemStack.getItem();
         TileEntityLuminator lumi = (TileEntityLuminator)world.getBlockTileEntity(i, j, k);
         int transfer = lumi.getMaxEnergy() - lumi.energy;
         if(transfer <= 0) {
            return false;
         } else {
            transfer = ElectricItem.discharge(itemStack, transfer, 2, true, false);
            if(!this.light) {
               world.setBlockAndMetadata(i, j, k, Ic2Items.activeLuminator.itemID, world.getBlockMetadata(i, j, k));
               lumi = (TileEntityLuminator)world.getBlockTileEntity(i, j, k);
            }

            lumi.energy += transfer;
            return true;
         }
      } else {
         return false;
      }
   }

   public void onEntityCollidedWithBlock(World world, int i, int j, int k, Entity entity) {
      if(this.light && entity instanceof EntityMob) {
         entity.setFire(entity instanceof EntityLiving && ((EntityLiving)entity).getCreatureAttribute() == EnumCreatureAttribute.UNDEAD?20:10);
      }

   }

   public TileEntity createNewTileEntity(World world) {
      return new TileEntityLuminator();
   }

   public String getTextureFile() {
      return "/ic2/sprites/block_0.png";
   }

   public void getSubBlocks(int i, CreativeTabs tabs, List itemList) {
      if(!this.light) {
         super.getSubBlocks(i, tabs, itemList);
      }

   }
}
