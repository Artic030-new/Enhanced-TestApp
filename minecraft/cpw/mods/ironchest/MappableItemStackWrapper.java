package cpw.mods.ironchest;

import net.minecraft.item.ItemStack;

public class MappableItemStackWrapper {

   private ItemStack wrap;

   public MappableItemStackWrapper(ItemStack toWrap) {
      this.wrap = toWrap;
   }

   public boolean equals(Object obj) {
      if(!(obj instanceof MappableItemStackWrapper)) {
         return false;
      } else {
         MappableItemStackWrapper isw = (MappableItemStackWrapper)obj;
         return this.wrap.getHasSubtypes()?isw.wrap.isItemEqual(this.wrap):isw.wrap.itemID == this.wrap.itemID;
      }
   }

   public int hashCode() {
      return this.wrap.itemID;
   }
}
