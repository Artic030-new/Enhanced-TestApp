package advsolar;

import net.minecraft.item.Item;

public class ItemSunnariumAlloy extends Item {

   public ItemSunnariumAlloy(int id) {
      super(id);
      this.maxStackSize = 64;
      this.setIconIndex(1);
   }

   public String getTextureFile() {
      return "/advsolar/adv_items.png";
   }
}
