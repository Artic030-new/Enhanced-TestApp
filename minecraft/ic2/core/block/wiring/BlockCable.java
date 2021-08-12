package ic2.core.block.wiring;

import cpw.mods.fml.common.registry.GameRegistry;
import ic2.api.Direction;
import ic2.api.IPaintableBlock;
import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.block.BlockMultiID;
import ic2.core.block.TileEntityBlock;
import ic2.core.block.wiring.TileEntityCable;
import ic2.core.block.wiring.TileEntityCableDetector;
import ic2.core.block.wiring.TileEntityCableSplitter;
import ic2.core.item.block.ItemBlockRare;
import ic2.core.item.tool.ItemToolCutter;
import ic2.core.util.AabbUtil;
import ic2.core.util.StackUtil;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockCable extends BlockMultiID implements IPaintableBlock {

   private static final Direction[] directions = Direction.values();


   public BlockCable(int i) {
      super(i, Material.iron);
      this.setHardness(0.2F);
      this.setStepSound(Block.soundClothFootstep);
      Ic2Items.copperCableBlock = new ItemStack(this, 1, 1);
      Ic2Items.insulatedCopperCableBlock = new ItemStack(this, 1, 0);
      Ic2Items.goldCableBlock = new ItemStack(this, 1, 2);
      Ic2Items.insulatedGoldCableBlock = new ItemStack(this, 1, 3);
      Ic2Items.doubleInsulatedGoldCableBlock = new ItemStack(this, 1, 4);
      Ic2Items.ironCableBlock = new ItemStack(this, 1, 5);
      Ic2Items.insulatedIronCableBlock = new ItemStack(this, 1, 6);
      Ic2Items.doubleInsulatedIronCableBlock = new ItemStack(this, 1, 7);
      Ic2Items.trippleInsulatedIronCableBlock = new ItemStack(this, 1, 8);
      Ic2Items.glassFiberCableBlock = new ItemStack(this, 1, 9);
      Ic2Items.tinCableBlock = new ItemStack(this, 1, 10);
      Ic2Items.detectorCableBlock = new ItemStack(this, 1, 11);
      Ic2Items.splitterCableBlock = new ItemStack(this, 1, 12);
      GameRegistry.registerBlock(this, ItemBlockRare.class, "cableBlock");
   }

   public String getTextureFile() {
      return "/ic2/sprites/block_cable.png";
   }

   public int getBlockTexture(IBlockAccess iblockaccess, int x, int y, int z, int side) {
      int color = 0;
      TileEntity te = iblockaccess.getBlockTileEntity(x, y, z);
      short cableType;
      if(te instanceof TileEntityCable) {
         TileEntityCable cable = (TileEntityCable)te;
         if(cable.foamed != 0) {
            if(cable.foamed == 1) {
               return 178;
            }

            return 208 + cable.foamColor;
         }

         cableType = cable.cableType;
         if(!(te instanceof TileEntityCableDetector) && !(te instanceof TileEntityCableSplitter)) {
            color = cable.color;
         } else {
            color = cable.getActive()?1:0;
         }
      } else {
         cableType = (short)iblockaccess.getBlockMetadata(x, y, z);
      }

      return cableType * 16 + color;
   }

   public int getBlockTextureFromSideAndMetadata(int side, int meta) {
      return meta * 16;
   }

   public MovingObjectPosition collisionRayTrace(World world, int x, int y, int z, Vec3 origin, Vec3 absDirection) {
      TileEntity te = world.getBlockTileEntity(x, y, z);
      if(!(te instanceof TileEntityCable)) {
         return null;
      } else {
         TileEntityCable tileEntityCable = (TileEntityCable)te;
         Vec3 direction = Vec3.createVectorHelper(absDirection.xCoord - origin.xCoord, absDirection.yCoord - origin.yCoord, absDirection.zCoord - origin.zCoord);
         double maxLength = direction.lengthVector();
         double halfThickness = (double)tileEntityCable.getCableThickness() / 2.0D;
         boolean hit = false;
         Vec3 intersection = Vec3.createVectorHelper(0.0D, 0.0D, 0.0D);
         Direction intersectingDirection = AabbUtil.getIntersection(origin, direction, AxisAlignedBB.getBoundingBox((double)x + 0.5D - halfThickness, (double)y + 0.5D - halfThickness, (double)z + 0.5D - halfThickness, (double)x + 0.5D + halfThickness, (double)y + 0.5D + halfThickness, (double)z + 0.5D + halfThickness), intersection);
         if(intersectingDirection != null && intersection.distanceTo(origin) <= maxLength) {
            hit = true;
         }

         Direction[] arr$ = directions;
         int len$ = arr$.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            Direction dir = arr$[i$];
            if(hit) {
               break;
            }

            TileEntity target = dir.applyToTileEntity(tileEntityCable);
            if(tileEntityCable.canInteractWith(target)) {
               AxisAlignedBB bbox = null;
               switch(BlockCable.NamelessClass103308949.$SwitchMap$ic2$api$Direction[dir.ordinal()]) {
               case 1:
                  bbox = AxisAlignedBB.getBoundingBox((double)x, (double)y + 0.5D - halfThickness, (double)z + 0.5D - halfThickness, (double)x + 0.5D, (double)y + 0.5D + halfThickness, (double)z + 0.5D + halfThickness);
                  break;
               case 2:
                  bbox = AxisAlignedBB.getBoundingBox((double)x + 0.5D, (double)y + 0.5D - halfThickness, (double)z + 0.5D - halfThickness, (double)x + 1.0D, (double)y + 0.5D + halfThickness, (double)z + 0.5D + halfThickness);
                  break;
               case 3:
                  bbox = AxisAlignedBB.getBoundingBox((double)x + 0.5D - halfThickness, (double)y, (double)z + 0.5D - halfThickness, (double)x + 0.5D + halfThickness, (double)y + 0.5D, (double)z + 0.5D + halfThickness);
                  break;
               case 4:
                  bbox = AxisAlignedBB.getBoundingBox((double)x + 0.5D - halfThickness, (double)y + 0.5D, (double)z + 0.5D - halfThickness, (double)x + 0.5D + halfThickness, (double)y + 1.0D, (double)z + 0.5D + halfThickness);
                  break;
               case 5:
                  bbox = AxisAlignedBB.getBoundingBox((double)x + 0.5D - halfThickness, (double)y + 0.5D - halfThickness, (double)z, (double)x + 0.5D + halfThickness, (double)y + 0.5D, (double)z + 0.5D);
                  break;
               case 6:
                  bbox = AxisAlignedBB.getBoundingBox((double)x + 0.5D - halfThickness, (double)y + 0.5D - halfThickness, (double)z + 0.5D, (double)x + 0.5D + halfThickness, (double)y + 0.5D + halfThickness, (double)z + 1.0D);
               }

               intersectingDirection = AabbUtil.getIntersection(origin, direction, bbox, intersection);
               if(intersectingDirection != null && intersection.distanceTo(origin) <= maxLength) {
                  hit = true;
               }
            }
         }

         return hit?new MovingObjectPosition(x, y, z, intersectingDirection.toSideValue(), intersection):null;
      }
   }

   public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z, int meta) {
      double halfThickness = (double)TileEntityCable.getCableThickness(meta);
      return AxisAlignedBB.getBoundingBox((double)x + 0.5D - halfThickness, (double)y + 0.5D - halfThickness, (double)z + 0.5D - halfThickness, (double)x + 0.5D + halfThickness, (double)y + 0.5D + halfThickness, (double)z + 0.5D + halfThickness);
   }

   public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
      return this.getCommonBoundingBoxFromPool(world, x, y, z, false);
   }

   public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
      return this.getCommonBoundingBoxFromPool(world, x, y, z, true);
   }

   public AxisAlignedBB getCommonBoundingBoxFromPool(World world, int x, int y, int z, boolean selectionBoundingBox) {
      TileEntity te = world.getBlockTileEntity(x, y, z);
      if(!(te instanceof TileEntityCable)) {
         return this.getCollisionBoundingBoxFromPool(world, x, y, z, 3);
      } else {
         TileEntityCable cable = (TileEntityCable)te;
         double halfThickness = cable.foamed == 1 && selectionBoundingBox?0.5D:(double)cable.getCableThickness() / 2.0D;
         double minX = (double)x + 0.5D - halfThickness;
         double minY = (double)y + 0.5D - halfThickness;
         double minZ = (double)z + 0.5D - halfThickness;
         double maxX = (double)x + 0.5D + halfThickness;
         double maxY = (double)y + 0.5D + halfThickness;
         double maxZ = (double)z + 0.5D + halfThickness;
         if(cable.canInteractWith(world.getBlockTileEntity(x - 1, y, z))) {
            minX = (double)x;
         }

         if(cable.canInteractWith(world.getBlockTileEntity(x, y - 1, z))) {
            minY = (double)y;
         }

         if(cable.canInteractWith(world.getBlockTileEntity(x, y, z - 1))) {
            minZ = (double)z;
         }

         if(cable.canInteractWith(world.getBlockTileEntity(x + 1, y, z))) {
            maxX = (double)(x + 1);
         }

         if(cable.canInteractWith(world.getBlockTileEntity(x, y + 1, z))) {
            maxY = (double)(y + 1);
         }

         if(cable.canInteractWith(world.getBlockTileEntity(x, y, z + 1))) {
            maxZ = (double)(z + 1);
         }

         return AxisAlignedBB.getBoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
      }
   }

   public boolean isBlockNormalCube(World world, int x, int y, int z) {
      TileEntity te = world.getBlockTileEntity(x, y, z);
      if(te instanceof TileEntityCable) {
         TileEntityCable cable = (TileEntityCable)te;
         if(cable.foamed > 0) {
            return true;
         }
      }

      return false;
   }

   public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float a, float b, float c) {
      ItemStack cur = entityPlayer.getCurrentEquippedItem();
      if(cur != null && cur.itemID == Block.sand.blockID) {
         if(!IC2.platform.isSimulating()) {
            return true;
         }

         TileEntity te = world.getBlockTileEntity(x, y, z);
         if(te instanceof TileEntityCable) {
            TileEntityCable cable = (TileEntityCable)te;
            if(cable.foamed == 1 && cable.changeFoam((byte)2)) {
               --cur.stackSize;
               if(cur.stackSize <= 0) {
                  entityPlayer.inventory.mainInventory[entityPlayer.inventory.currentItem] = null;
               }

               return true;
            }
         }
      }

      return false;
   }

   public static int getCableColor(IBlockAccess iblockaccess, int i, int j, int k) {
      TileEntity te = iblockaccess.getBlockTileEntity(i, j, k);
      return te instanceof TileEntityCable?((TileEntityCable)te).color:0;
   }

   public boolean colorBlock(World world, int i, int j, int k, int color) {
      return ((TileEntityCable)world.getBlockTileEntity(i, j, k)).changeColor(color);
   }

   public boolean canHarvestBlock(EntityPlayer player, int md) {
      return true;
   }

   public ArrayList getBlockDropped(World world, int x, int y, int z, int metadata, int fortune) {
      ArrayList ret = new ArrayList();
      TileEntity te = world.getBlockTileEntity(x, y, z);
      if(te instanceof TileEntityCable) {
         TileEntityCable cable = (TileEntityCable)te;
         ret.add(new ItemStack(Ic2Items.insulatedCopperCableItem.itemID, 1, cable.cableType));
      } else if(metadata != 13) {
         ret.add(new ItemStack(Ic2Items.insulatedCopperCableItem.itemID, 1, metadata));
      }

      return ret;
   }

   public void breakBlock(World world, int x, int y, int z, int a, int b) {
      if(world.getBlockMetadata(x, y, z) == 13) {
         TileEntity te = world.getBlockTileEntity(x, y, z);
         if(te instanceof TileEntityCable) {
            TileEntityCable cable = (TileEntityCable)te;
            StackUtil.dropAsEntity(world, x, y, z, new ItemStack(Ic2Items.insulatedCopperCableItem.itemID, 1, cable.cableType));
         }
      }

      super.breakBlock(world, x, y, z, a, b);
   }

   public TileEntityBlock createNewTileEntity(World world, int meta) {
      return (TileEntityBlock)(meta == 11?new TileEntityCableDetector((short)meta):(meta == 12?new TileEntityCableSplitter((short)meta):new TileEntityCable((short)meta)));
   }

   public boolean renderAsNormalBlock() {
      return false;
   }

   public int getRenderType() {
      return IC2.platform.getRenderId("cable");
   }

   public boolean isOpaqueCube() {
      return false;
   }

   public void onBlockClicked(World world, int i, int j, int k, EntityPlayer entityplayer) {
      if(entityplayer.getCurrentEquippedItem() != null && entityplayer.getCurrentEquippedItem().getItem() instanceof ItemToolCutter) {
         ItemToolCutter.cutInsulationFrom(entityplayer.getCurrentEquippedItem(), world, i, j, k);
      }

   }

   public boolean isProvidingWeakPower(IBlockAccess iblockaccess, int x, int y, int z, int side) {
      TileEntity te = iblockaccess.getBlockTileEntity(x, y, z);
      return te instanceof TileEntityCableDetector?((TileEntityCableDetector)te).getActive():false;
   }

   public void getSubBlocks(int i, CreativeTabs tabs, List itemList) {}

   public float getBlockHardness(World world, int x, int y, int z) {
      return world != null && world.getBlockMetadata(x, y, z) == 13?3.0F:0.2F;
   }

   public float getExplosionResistance(Entity exploder, World world, int x, int y, int z, double src_x, double src_y, double src_z) {
      TileEntity te = world.getBlockTileEntity(x, y, z);
      if(te instanceof TileEntityCable) {
         TileEntityCable cable = (TileEntityCable)te;
         if(cable.foamed == 2) {
            return 90.0F;
         }
      }

      return 6.0F;
   }

   public boolean canConnectRedstone(IBlockAccess world, int X, int Y, int Z, int direction) {
      int meta = world.getBlockMetadata(X, Y, Z);
      return meta == 11 || meta == 12;
   }

   public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
      return new ItemStack(Ic2Items.insulatedCopperCableItem.itemID, 1, world.getBlockMetadata(x, y, z));
   }


   // $FF: synthetic class
   static class NamelessClass103308949 {

      // $FF: synthetic field
      static final int[] $SwitchMap$ic2$api$Direction = new int[Direction.values().length];


      static {
         try {
            $SwitchMap$ic2$api$Direction[Direction.XN.ordinal()] = 1;
         } catch (NoSuchFieldError var6) {
            ;
         }

         try {
            $SwitchMap$ic2$api$Direction[Direction.XP.ordinal()] = 2;
         } catch (NoSuchFieldError var5) {
            ;
         }

         try {
            $SwitchMap$ic2$api$Direction[Direction.YN.ordinal()] = 3;
         } catch (NoSuchFieldError var4) {
            ;
         }

         try {
            $SwitchMap$ic2$api$Direction[Direction.YP.ordinal()] = 4;
         } catch (NoSuchFieldError var3) {
            ;
         }

         try {
            $SwitchMap$ic2$api$Direction[Direction.ZN.ordinal()] = 5;
         } catch (NoSuchFieldError var2) {
            ;
         }

         try {
            $SwitchMap$ic2$api$Direction[Direction.ZP.ordinal()] = 6;
         } catch (NoSuchFieldError var1) {
            ;
         }

      }
   }
}
