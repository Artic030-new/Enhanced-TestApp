package ic2.core.item;

import cpw.mods.fml.common.IFuelHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import ic2.core.item.ItemFuelCan;
import ic2.core.util.StackUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ItemFuelCanFilled extends ItemFuelCan implements IFuelHandler {

   public ItemFuelCanFilled(int id, int index) {
      super(id, index);
      GameRegistry.registerFuelHandler(this);
   }

   public int getBurnTime(ItemStack stack) {
      if(stack.itemID != super.itemID) {
         return 0;
      } else {
         NBTTagCompound data = StackUtil.getOrCreateNbtData(stack);
         if(stack.getItemDamage() > 0) {
            data.setInteger("value", stack.getItemDamage());
         }

         int fv = data.getInteger("value") * 2;
         return fv > 32767?32767:fv;
      }
   }
}
