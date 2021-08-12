package ic2.core.item.tool;

import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;

public class ItemIC2Spade extends ItemSpade {

   private final ItemStack repairMaterial;
   public float a;


   public ItemIC2Spade(int id, int index, EnumToolMaterial enumtoolmaterial, float efficiency, ItemStack repairMaterial) {
      super(id, enumtoolmaterial);
      this.a = efficiency;
      this.setIconIndex(index);
      this.repairMaterial = repairMaterial;
   }

   public String getTextureFile() {
      return "/ic2/sprites/item_0.png";
   }

   public int getItemEnchantability() {
      return 13;
   }

   public boolean getIsRepairable(ItemStack stack1, ItemStack stack2) {
      return stack2 != null && stack2.itemID == this.repairMaterial.itemID;
   }
}
