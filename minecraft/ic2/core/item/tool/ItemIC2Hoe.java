package ic2.core.item.tool;

import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;

public class ItemIC2Hoe extends ItemHoe {

   private final ItemStack repairMaterial;


   public ItemIC2Hoe(int id, int index, EnumToolMaterial enumtoolmaterial, ItemStack repairMaterial) {
      super(id, enumtoolmaterial);
      this.setIconIndex(index);
      this.repairMaterial = repairMaterial;
   }

   public String getTextureFile() {
      return "/ic2/sprites/item_0.png";
   }

   public boolean getIsRepairable(ItemStack stack1, ItemStack stack2) {
      return stack2 != null && stack2.itemID == this.repairMaterial.itemID;
   }
}
