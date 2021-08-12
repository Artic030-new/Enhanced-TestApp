package ic2.core.block.crop;

import ic2.api.TECrop;
import ic2.core.block.crop.CropSeedFood;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class CropPotato extends CropSeedFood {

   public CropPotato() {
      super("Potato", 51, "Yellow", new ItemStack(Item.potato));
   }

   public ItemStack getGain(TECrop crop) {
      return crop.worldObj.rand.nextInt(50) == 0?new ItemStack(Item.poisonousPotato):super.getGain(crop);
   }
}
