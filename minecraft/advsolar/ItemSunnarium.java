package advsolar;

import net.minecraft.item.Item;

public class ItemSunnarium extends Item {

   public ItemSunnarium(int id) {
      super(id);
      this.maxStackSize = 64;
      this.setIconIndex(0);
   }

   public String getTextureFile() {
      return "/advsolar/adv_items.png";
   }
}
