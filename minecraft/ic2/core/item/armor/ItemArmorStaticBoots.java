package ic2.core.item.armor;

import ic2.api.IElectricItem;
import ic2.api.IMetalArmor;
import ic2.core.IItemTickListener;
import ic2.core.item.ElectricItem;
import ic2.core.item.armor.ItemArmorUtility;
import ic2.core.util.StackUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ItemArmorStaticBoots extends ItemArmorUtility implements IMetalArmor, IItemTickListener {

   public ItemArmorStaticBoots(int id, int index, int renderIndex) {
      super(id, index, renderIndex, 3);
   }

   public boolean isMetalArmor(ItemStack itemstack, EntityPlayer player) {
      return true;
   }

   public boolean onTick(EntityPlayer player, ItemStack itemStack) {
      if(player.inventory.armorInventory[2] != null && player.inventory.armorInventory[2].getItem() instanceof IElectricItem) {
         NBTTagCompound compound = StackUtil.getOrCreateNbtData(itemStack);
         boolean isNotWalking = player.ridingEntity != null || player.isInWater();
         if(!compound.hasKey("x") || isNotWalking) {
            compound.setInteger("x", (int)player.posX);
         }

         if(!compound.hasKey("z") || isNotWalking) {
            compound.setInteger("z", (int)player.posZ);
         }

         double distance = Math.sqrt((double)((compound.getInteger("x") - (int)player.posX) * (compound.getInteger("x") - (int)player.posX) + (compound.getInteger("z") - (int)player.posZ) * (compound.getInteger("z") - (int)player.posZ)));
         if(distance >= 5.0D) {
            compound.setInteger("x", (int)player.posX);
            compound.setInteger("z", (int)player.posZ);
            return ElectricItem.charge(player.inventory.armorInventory[2], Math.min(3, (int)distance / 5), Integer.MAX_VALUE, true, false) > 0;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }
}
