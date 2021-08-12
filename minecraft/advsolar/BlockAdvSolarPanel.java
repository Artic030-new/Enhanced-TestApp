package advsolar;

import advsolar.AdvancedSolarPanel;
import advsolar.TileEntityAdvancedSolarPanel;
import advsolar.TileEntityHybridSolarPanel;
import advsolar.TileEntityQGenerator;
import advsolar.TileEntitySolarPanel;
import advsolar.TileEntityUltimateSolarPanel;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import java.util.Random;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockAdvSolarPanel extends BlockContainer {

   public boolean qgActive;
   public static final int[][] sprites = new int[][]{{0, 1, 2}, {3, 4, 5}, {6, 7, 8}, {9, 9, 9}, {10, 10, 10}};

   public BlockAdvSolarPanel(int id) {
      super(id, Material.iron);
      this.setHardness(3.0F);
      this.setCreativeTab(AdvancedSolarPanel.ic2Tab);
      this.qgActive = false;
   }

   public int getBlockTexture(IBlockAccess iblockaccess, int x, int y, int z, int blockSide) {
      int i1 = iblockaccess.getBlockMetadata(x, y, z);
      if(i1 >= 0 && i1 < sprites.length) {
         if(i1 == 3) {
            TileEntityQGenerator blokTE = (TileEntityQGenerator)iblockaccess.getBlockTileEntity(x, y, z);
            blokTE.getActive();
            if(!blokTE.active) {
               ++i1;
            }
         }

         return blockSide == 1?sprites[i1][1]:(blockSide == 0?sprites[i1][0]:sprites[i1][2]);
      } else {
         return 0;
      }
   }

   public int getBlockTextureFromSideAndMetadata(int blockSide, int metaData) {
      return metaData >= 0 && metaData < sprites.length?(blockSide == 1?sprites[metaData][1]:(blockSide == 0?sprites[metaData][0]:sprites[metaData][2])):0;
   }

   public String getTextureFile() {
      return "/advsolar/texture/advsolar_texture.png";
   }

   public void breakBlock(World world, int x, int y, int z, int blockID, int blockMeta) {
      TileEntity tileentity = world.getBlockTileEntity(x, y, z);
      if(tileentity != null && !(tileentity instanceof TileEntityQGenerator)) {
         this.dropItems((TileEntitySolarPanel)tileentity, world);
      }

      world.removeBlockTileEntity(x, y, z);
      super.breakBlock(world, x, y, z, blockID, blockMeta);
   }

   public int quantityDropped(Random random) {
      return 1;
   }

   public int idDropped(int i, Random var2, int j) {
      return super.blockID;
   }

   public int damageDropped(int metadata) {
      return metadata;
   }

   public TileEntity getBlockEntity(int metadata) {
      switch(metadata) {
      case 0:
         return new TileEntityAdvancedSolarPanel();
      case 1:
         return new TileEntityHybridSolarPanel();
      case 2:
         return new TileEntityUltimateSolarPanel();
      case 3:
         return new TileEntityQGenerator();
      default:
         return new TileEntityAdvancedSolarPanel();
      }
   }

   public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int s, float f1, float f2, float f3) {
      if(entityPlayer.isSneaking()) {
         return false;
      } else if(world.isRemote) {
         return true;
      } else {
         TileEntity tileentity = world.getBlockTileEntity(x, y, z);
         if(tileentity != null) {
            if(tileentity instanceof TileEntityQGenerator) {
               entityPlayer.openGui(AdvancedSolarPanel.instance, 1, world, x, y, z);
            } else {
               entityPlayer.openGui(AdvancedSolarPanel.instance, 1, world, x, y, z);
            }
         }

         return true;
      }
   }

   private void dropItems(TileEntitySolarPanel tileentity, World world) {
      Random rand = new Random();
      if(tileentity instanceof IInventory) {
         TileEntitySolarPanel inventory = tileentity;

         for(int i = 0; i < inventory.getSizeInventory(); ++i) {
            ItemStack item = inventory.getStackInSlot(i);
            if(item != null && item.stackSize > 0) {
               float rx = rand.nextFloat() * 0.8F + 0.1F;
               float ry = rand.nextFloat() * 0.8F + 0.1F;
               float rz = rand.nextFloat() * 0.8F + 0.1F;
               EntityItem entityItem = new EntityItem(world, (double)((float)tileentity.xCoord + rx), (double)((float)tileentity.yCoord + ry), (double)((float)tileentity.zCoord + rz), new ItemStack(item.itemID, item.stackSize, item.getItemDamage()));
               if(item.hasTagCompound()) {
                  entityItem.getEntityItem().setTagCompound((NBTTagCompound)item.getTagCompound().copy());
               }
               float factor = 0.05F;
               entityItem.motionX = rand.nextGaussian() * (double)factor;
               entityItem.motionY = rand.nextGaussian() * (double)factor + 0.20000000298023224D;
               entityItem.motionZ = rand.nextGaussian() * (double)factor;
               world.spawnEntityInWorld(entityItem);
               item.stackSize = 0;
            }
         }

      }
   }

   public TileEntity getBlockEntity() {
      return null;
   }

   public TileEntity createNewTileEntity(World world, int metadata) {
      switch(metadata) {
      case 0:
         return new TileEntityAdvancedSolarPanel();
      case 1:
         return new TileEntityHybridSolarPanel();
      case 2:
         return new TileEntityUltimateSolarPanel();
      case 3:
         return new TileEntityQGenerator();
      default:
         return new TileEntityAdvancedSolarPanel();
      }
   }

   public TileEntity createNewTileEntity(World world) {
      return null;
   }

   @SideOnly(Side.CLIENT)
   public void getSubBlocks(int id, CreativeTabs table, List list) {
      for(int ix = 0; ix < 4; ++ix) {
         list.add(new ItemStack(this, 1, ix));
      }

   }

}
