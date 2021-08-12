package cpw.mods.ironchest;

import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.ironchest.BlockIronChest;
import cpw.mods.ironchest.ChestChangerType;
import cpw.mods.ironchest.IronChest;
import cpw.mods.ironchest.IronChestType;
import cpw.mods.ironchest.TileEntityIronChest;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.world.World;

public class ItemChestChanger extends Item {

   private ChestChangerType type;

   public ItemChestChanger(int id, ChestChangerType type) {
      super(id);
      this.setMaxStackSize(1);
      this.type = type;
      this.setIconIndex(type.ordinal());
      this.setItemName(type.itemName);
      this.setCreativeTab(CreativeTabs.tabMisc);
   }

   public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
      if(world.isRemote) {
         return false;
      } else {
         TileEntity te = world.getBlockTileEntity(x, y, z);
         TileEntityIronChest newchest;
         if(te != null && te instanceof TileEntityIronChest) {
            TileEntityIronChest var18 = (TileEntityIronChest)te;
            newchest = var18.applyUpgradeItem(this);
            if(newchest == null) {
               return false;
            }
         } else {
            if(te == null || !(te instanceof TileEntityChest)) {
               return false;
            }

            TileEntityChest tec = (TileEntityChest)te;
            if(tec.numUsingPlayers > 0) {
               return false;
            }

            if(!this.getType().canUpgrade(IronChestType.WOOD)) {
               return false;
            }

            newchest = IronChestType.makeEntity(this.getTargetChestOrdinal(IronChestType.WOOD.ordinal()));
            int newSize = newchest.chestContents.length;
            ItemStack[] chestContents = (ItemStack[])ObfuscationReflectionHelper.getPrivateValue(TileEntityChest.class, tec, 0);
            System.arraycopy(chestContents, 0, newchest.chestContents, 0, Math.min(newSize, chestContents.length));
            BlockIronChest block = IronChest.ironChestBlock;
            block.dropContent(newSize, tec, world, tec.xCoord, tec.yCoord, tec.zCoord);
            newchest.setFacing((byte)tec.getBlockMetadata());
            newchest.sortTopStacks();

            for(int i = 0; i < Math.min(newSize, chestContents.length); ++i) {
               chestContents[i] = null;
            }

            world.setBlockWithNotify(x, y, z, 0);
            tec.updateContainingBlockInfo();
            tec.checkForAdjacentChests();
            world.setBlockWithNotify(x, y, z, block.blockID);
         }

         world.setBlockTileEntity(x, y, z, newchest);
         world.setBlockMetadataWithNotify(x, y, z, newchest.getType().ordinal());
         world.notifyBlocksOfNeighborChange(x, y, z, world.getBlockId(x, y, z));
         world.markBlockForUpdate(x, y, z);
         stack.stackSize = 0;
         return true;
      }
   }

   public String getTextureFile() {
      return "/cpw/mods/ironchest/sprites/item_textures.png";
   }

   public int getTargetChestOrdinal(int sourceOrdinal) {
      return this.type.getTarget();
   }

   public ChestChangerType getType() {
      return this.type;
   }
}
