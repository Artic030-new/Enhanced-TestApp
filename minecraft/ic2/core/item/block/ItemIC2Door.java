package ic2.core.item.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ItemIC2Door extends ItemDoor {

   public Block block;


   public ItemIC2Door(int id, int index, Block doorblock) {
      super(id, Material.iron);
      this.setIconIndex(index);
      this.block = doorblock;
   }

   public String getTextureFile() {
      return "/ic2/sprites/item_0.png";
   }

   public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float a, float b, float c) {
      if(par7 != 1) {
         return false;
      } else {
         ++par5;
         if(par2EntityPlayer.canCurrentToolHarvestBlock(par4, par5, par6) && par2EntityPlayer.canCurrentToolHarvestBlock(par4, par5 + 1, par6)) {
            if(!this.block.canPlaceBlockAt(par3World, par4, par5, par6)) {
               return false;
            } else {
               int var9 = MathHelper.floor_double((double)((par2EntityPlayer.rotationYaw + 180.0F) * 4.0F / 360.0F) - 0.5D) & 3;
               placeDoorBlock(par3World, par4, par5, par6, var9, this.block);
               --par1ItemStack.stackSize;
               return true;
            }
         } else {
            return false;
         }
      }
   }
}
