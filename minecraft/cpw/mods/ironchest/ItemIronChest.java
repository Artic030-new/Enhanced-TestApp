package cpw.mods.ironchest;

import cpw.mods.ironchest.IronChestType;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemIronChest extends ItemBlock {

   public ItemIronChest(int id) {
      super(id);
      this.setMaxDamage(0);
      this.setHasSubtypes(true);
   }

   public int getMetadata(int meta) {
      return IronChestType.validateMeta(meta);
   }

   public String getItemNameIS(ItemStack itemstack) {
      return IronChestType.values()[itemstack.getItemDamage()].name();
   }
}
