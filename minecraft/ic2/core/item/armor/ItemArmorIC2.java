package ic2.core.item.armor;

import ic2.api.IMetalArmor;
import ic2.core.IC2;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

public class ItemArmorIC2 extends ItemArmor implements IMetalArmor {

   private final ItemStack repairMaterial;


   public ItemArmorIC2(int i, int index, EnumArmorMaterial enumArmorMaterial, int k, int l, ItemStack repairMaterial) {
      super(i, enumArmorMaterial, k, l);
      this.setIconIndex(index);
      this.setMaxDamage(enumArmorMaterial.getDurability(l));
      this.setCreativeTab(IC2.tabIC2);
      this.repairMaterial = repairMaterial;
   }

   public boolean isMetalArmor(ItemStack itemstack, EntityPlayer player) {
      return true;
   }

   public boolean getIsRepairable(ItemStack stack1, ItemStack stack2) {
      return stack2 != null && stack2.itemID == this.repairMaterial.itemID;
   }

   public String getTextureFile() {
      return "/ic2/sprites/item_0.png";
   }
}
