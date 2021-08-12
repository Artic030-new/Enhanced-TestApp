package ic2.core.item.block;

import ic2.api.IBoxable;
import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.block.wiring.BlockCable;
import ic2.core.item.ItemIC2;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemCable extends ItemIC2 implements IBoxable {

   public ItemCable(int i, int j) {
      super(i, j);
      this.setHasSubtypes(true);
      this.setCreativeTab(IC2.tabIC2);
      Ic2Items.copperCableItem = new ItemStack(this, 1, 1);
      Ic2Items.insulatedCopperCableItem = new ItemStack(this, 1, 0);
      Ic2Items.goldCableItem = new ItemStack(this, 1, 2);
      Ic2Items.insulatedGoldCableItem = new ItemStack(this, 1, 3);
      Ic2Items.doubleInsulatedGoldCableItem = new ItemStack(this, 1, 4);
      Ic2Items.ironCableItem = new ItemStack(this, 1, 5);
      Ic2Items.insulatedIronCableItem = new ItemStack(this, 1, 6);
      Ic2Items.doubleInsulatedIronCableItem = new ItemStack(this, 1, 7);
      Ic2Items.trippleInsulatedIronCableItem = new ItemStack(this, 1, 8);
      Ic2Items.glassFiberCableItem = new ItemStack(this, 1, 9);
      Ic2Items.tinCableItem = new ItemStack(this, 1, 10);
      Ic2Items.detectorCableItem = new ItemStack(this, 1, 11);
      Ic2Items.splitterCableItem = new ItemStack(this, 1, 12);
   }

   public int getIconFromDamage(int i) {
      return super.iconIndex + i;
   }

   public String getItemNameIS(ItemStack itemstack) {
      int meta = itemstack.getItemDamage();
      switch(meta) {
      case 0:
         return "itemCable";
      case 1:
         return "itemCableO";
      case 2:
         return "itemGoldCable";
      case 3:
         return "itemGoldCableI";
      case 4:
         return "itemGoldCableII";
      case 5:
         return "itemIronCable";
      case 6:
         return "itemIronCableI";
      case 7:
         return "itemIronCableII";
      case 8:
         return "itemIronCableIIII";
      case 9:
         return "itemGlassCable";
      case 10:
         return "itemTinCable";
      case 11:
         return "itemDetectorCable";
      case 12:
         return "itemSplitterCable";
      default:
         return null;
      }
   }

   public boolean onItemUse(ItemStack itemstack, EntityPlayer entityplayer, World world, int x, int y, int z, int side, float a, float b, float c) {
      int blockId = world.getBlockId(x, y, z);
      if(blockId > 0) {
         if(blockId == Block.snow.blockID) {
            side = 1;
         } else if(blockId != Block.vine.blockID && blockId != Block.tallGrass.blockID && blockId != Block.deadBush.blockID && (Block.blocksList[blockId] == null || !Block.blocksList[blockId].isBlockReplaceable(world, x, y, z))) {
            switch(side) {
            case 0:
               --y;
               break;
            case 1:
               ++y;
               break;
            case 2:
               --z;
               break;
            case 3:
               ++z;
               break;
            case 4:
               --x;
               break;
            case 5:
               ++x;
            }
         }
      }

      BlockCable block = (BlockCable)Block.blocksList[Ic2Items.insulatedCopperCableBlock.itemID];
      if((blockId == 0 || world.canPlaceEntityOnSide(Ic2Items.insulatedCopperCableBlock.itemID, x, y, z, false, side, entityplayer)) && world.checkIfAABBIsClear(block.getCollisionBoundingBoxFromPool(world, x, y, z, itemstack.getItemDamage())) && world.setBlockAndMetadataWithNotify(x, y, z, block.blockID, itemstack.getItemDamage())) {
         block.onPostBlockPlaced(world, x, y, z, side);
         block.onBlockPlacedBy(world, x, y, z, entityplayer);
         if(!entityplayer.capabilities.isCreativeMode) {
            --itemstack.stackSize;
         }

         return true;
      } else {
         return false;
      }
   }

   public void getSubItems(int i, CreativeTabs tabs, List itemList) {
      for(int meta = 0; meta < 32767; ++meta) {
         ItemStack stack = new ItemStack(this, 1, meta);
         if(this.getItemNameIS(stack) == null) {
            break;
         }

         itemList.add(stack);
      }

   }

   public boolean canBeStoredInToolbox(ItemStack itemstack) {
      return true;
   }
}
