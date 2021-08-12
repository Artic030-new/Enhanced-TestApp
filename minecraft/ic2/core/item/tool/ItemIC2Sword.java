package ic2.core.item.tool;

import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;

public class ItemIC2Sword extends ItemSword {

   private final ItemStack repairMaterial;
   public int weaponDamage;


   public ItemIC2Sword(int id, int index, EnumToolMaterial enumtoolmaterial, int damage, ItemStack repairMaterial) {
      super(id, enumtoolmaterial);
      this.setIconIndex(index);
      this.weaponDamage = damage;
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
