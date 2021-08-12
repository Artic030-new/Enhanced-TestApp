package ic2.core.item.tool;

import ic2.core.Ic2Items;
import ic2.core.item.ElectricItem;
import ic2.core.item.tool.ItemElectricTool;
import ic2.core.item.tool.ItemTreetap;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemTreetapElectric extends ItemElectricTool {

   public ItemTreetapElectric(int id, int sprite) {
      super(id, sprite, EnumToolMaterial.IRON, 50);
      super.maxCharge = 10000;
      super.transferLimit = 100;
      super.tier = 1;
   }

   public boolean onItemUse(ItemStack itemstack, EntityPlayer entityplayer, World world, int i, int j, int k, int l, float a, float b, float c) {
      if(world.getBlockId(i, j, k) != Ic2Items.rubberWood.itemID) {
         return false;
      } else if(!ElectricItem.use(itemstack, super.operationEnergyCost, entityplayer)) {
         return false;
      } else {
         ItemTreetap.attemptExtract(entityplayer, world, i, j, k, l, (List)null);
         return true;
      }
   }
}
