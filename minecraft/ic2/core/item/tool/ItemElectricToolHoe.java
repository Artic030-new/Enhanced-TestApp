package ic2.core.item.tool;

import ic2.core.IC2;
import ic2.core.item.ElectricItem;
import ic2.core.item.tool.ItemElectricTool;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.UseHoeEvent;

public class ItemElectricToolHoe extends ItemElectricTool {

   public ItemElectricToolHoe(int id, int sprite) {
      super(id, sprite, EnumToolMaterial.IRON, 50);
      super.maxCharge = 10000;
      super.transferLimit = 100;
      super.tier = 1;
      super.efficiencyOnProperMaterial = 16.0F;
   }

   public void init() {
      super.mineableBlocks.add(Block.dirt);
      super.mineableBlocks.add(Block.grass);
      super.mineableBlocks.add(Block.mycelium);
   }

   public boolean onBlockStartBreak(ItemStack itemstack, int i, int j, int k, EntityPlayer entityliving) {
      ElectricItem.use(itemstack, super.operationEnergyCost, entityliving);
      return false;
   }

   public boolean onItemUse(ItemStack itemstack, EntityPlayer entityplayer, World world, int i, int j, int k, int l, float a, float b, float c) {
      if(!entityplayer.canCurrentToolHarvestBlock(i, j, k)) {
         return false;
      } else if(!ElectricItem.use(itemstack, super.operationEnergyCost, entityplayer)) {
         return false;
      } else if(MinecraftForge.EVENT_BUS.post(new UseHoeEvent(entityplayer, itemstack, world, i, j, k))) {
         return true;
      } else {
         int i1 = world.getBlockId(i, j, k);
         int j1 = world.getBlockId(i, j + 1, k);
         if((l == 0 || j1 != 0 || i1 != Block.grass.blockID) && i1 != Block.dirt.blockID) {
            return false;
         } else {
            Block block = Block.tilledField;
            world.playSoundEffect((double)((float)i + 0.5F), (double)((float)j + 0.5F), (double)((float)k + 0.5F), block.stepSound.getStepSound(), (block.stepSound.getVolume() + 1.0F) / 2.0F, block.stepSound.getPitch() * 0.8F);
            if(!IC2.platform.isSimulating()) {
               return true;
            } else {
               world.setBlockWithNotify(i, j, k, block.blockID);
               return true;
            }
         }
      }
   }
}
