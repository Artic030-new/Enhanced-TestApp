package ic2.core.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemIC2 extends Item {

   public int rarity = 0;


   public ItemIC2(int id, int index) {
      super(id);
      this.setIconIndex(index);
   }

   public String getTextureFile() {
      return "/ic2/sprites/item_0.png";
   }

   public ItemIC2 setRarity(int rarity) {
      this.rarity = rarity;
      return this;
   }

   @SideOnly(Side.CLIENT)
   public EnumRarity getRarity(ItemStack stack) {
      return EnumRarity.values()[this.rarity];
   }
}
