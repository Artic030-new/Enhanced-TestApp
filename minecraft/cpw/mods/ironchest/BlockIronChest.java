package cpw.mods.ironchest;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import cpw.mods.ironchest.IronChest;
import cpw.mods.ironchest.IronChestType;
import cpw.mods.ironchest.TileEntityIronChest;
import java.util.List;
import java.util.Random;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class BlockIronChest extends BlockContainer {

   private final Random random = new Random();

   public BlockIronChest(int id) {
      super(id, Material.iron);
      this.setBlockName("IronChest");
      this.setHardness(3.0F);
      this.setRequiresSelfNotify();
      this.setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
      this.setCreativeTab(CreativeTabs.tabDecorations);
   }

   public TileEntity createNewTileEntity(World world) {
      return null;
   }

   public String getTextureFile() {
      return "/cpw/mods/ironchest/sprites/block_textures.png";
   }

   public boolean isOpaqueCube() {
      return false;
   }

   public boolean renderAsNormalBlock() {
      return false;
   }

   public int getRenderType() {
      return 22;
   }

   public TileEntity createNewTileEntity(World world, int metadata) {
      return IronChestType.makeEntity(metadata);
   }

   public int getBlockTexture(IBlockAccess worldAccess, int x, int y, int z, int metadata) {
      int meta = worldAccess.getBlockMetadata(x, y, z);
      IronChestType type = IronChestType.values()[meta];
      TileEntity te = worldAccess.getBlockTileEntity(x, y, z);
      TileEntityIronChest icte = null;
      if(te != null && te instanceof TileEntityIronChest) {
         icte = (TileEntityIronChest)te;
      }

      return metadata != 0 && metadata != 1?(icte != null && metadata == icte.getFacing()?type.getTextureRow() * 16 + 2:type.getTextureRow() * 16):type.getTextureRow() * 16 + 1;
   }

   public int getBlockTextureFromSideAndMetadata(int blockSide, int metaData) {
      IronChestType typ = IronChestType.values()[metaData];
      switch(blockSide) {
      case 0:
      case 1:
         return typ.getTextureRow() * 16 + 1;
      case 2:
      default:
         return typ.getTextureRow() * 16;
      case 3:
         return typ.getTextureRow() * 16 + 2;
      }
   }

   public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int i1, float f1, float f2, float f3) {
      TileEntity te = world.getBlockTileEntity(x, y, z);
      if(te != null && te instanceof TileEntityIronChest) {
         if(world.isBlockSolidOnSide(x, y + 1, z, ForgeDirection.DOWN)) {
            return true;
         } else if(world.isRemote) {
            return true;
         } else {
            player.openGui(IronChest.instance, ((TileEntityIronChest)te).getType().ordinal(), world, x, y, z);
            return true;
         }
      } else {
         return true;
      }
   }

   public void onBlockAdded(World world, int x, int y, int z) {
      super.onBlockAdded(world, x, y, z);
      world.markBlockForUpdate(x, y, z);
   }

   public void onBlockPlacedBy(World world, int x, int y, int z, EntityLiving player) {
      byte chestFacing = 0;
      int facing = MathHelper.floor_double((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
      if(facing == 0) {
         chestFacing = 2;
      }

      if(facing == 1) {
         chestFacing = 5;
      }

      if(facing == 2) {
         chestFacing = 3;
      }

      if(facing == 3) {
         chestFacing = 4;
      }

      TileEntity te = world.getBlockTileEntity(x, y, z);
      if(te != null && te instanceof TileEntityIronChest) {
         ((TileEntityIronChest)te).setFacing(chestFacing);
         world.markBlockForUpdate(x, y, z);
      }

   }

   public int damageDropped(int metadata) {
      return metadata;
   }

   public void breakBlock(World world, int x, int y, int z, int blockID, int blockMeta) {
      TileEntityIronChest tileentitychest = (TileEntityIronChest)world.getBlockTileEntity(x, y, z);
      if(tileentitychest != null) {
         this.dropContent(0, tileentitychest, world, tileentitychest.xCoord, tileentitychest.yCoord, tileentitychest.zCoord);
      }

      super.breakBlock(world, x, y, z, blockID, blockMeta);
   }

   public void dropContent(int newSize, IInventory chest, World world, int xCoord, int yCoord, int zCoord) {
	  for(int l = newSize; l < chest.getSizeInventory(); ++l) {
	     ItemStack itemstack = chest.getStackInSlot(l);
	     if (itemstack != null) {
	        chest.setInventorySlotContents(l, (ItemStack)null);
	        float f = this.random.nextFloat() * 0.8F + 0.1F;
	        float f1 = this.random.nextFloat() * 0.8F + 0.1F;
            float f2 = this.random.nextFloat() * 0.8F + 0.1F;
	        EntityItem entityitem = new EntityItem(world, (double)((float)xCoord + f), (double)((float)yCoord + (float)(newSize > 0 ? 1 : 0) + f1), (double)((float)zCoord + f2), itemstack);
	        float f3 = 0.05F;
	        entityitem.motionX = (double)((float)this.random.nextGaussian() * f3);
            entityitem.motionY = (double)((float)this.random.nextGaussian() * f3 + 0.2F);
	        entityitem.motionZ = (double)((float)this.random.nextGaussian() * f3);
	        world.spawnEntityInWorld(entityitem);
         }
      }

   }

   @SideOnly(Side.CLIENT)
   public void getSubBlocks(int id, CreativeTabs table, List list) {
      IronChestType[] arr$ = IronChestType.values();
      int len$ = arr$.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         IronChestType type = arr$[i$];
         if(type.isValidForCreativeMode()) {
           list.add(new ItemStack(this, 1, type.ordinal()));
         }
      }

   }

   public float getExplosionResistance(Entity par1Entity, World world, int x, int y, int z, double explosionX, double explosionY, double explosionZ) {
      TileEntity te = world.getBlockTileEntity(x, y, z);
      if(te instanceof TileEntityIronChest) {
         TileEntityIronChest teic = (TileEntityIronChest)te;
         if(teic.getType().isExplosionResistant()) {
            return 10000.0F;
         }
      }

      return super.getExplosionResistance(par1Entity, world, x, y, z, explosionX, explosionY, explosionZ);
   }
}
