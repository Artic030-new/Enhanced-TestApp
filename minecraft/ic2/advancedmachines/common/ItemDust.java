package ic2.advancedmachines.common;

import net.minecraft.item.Item;

public class ItemDust extends Item {

   public ItemDust(int id) {
      super(id);
      this.setTextureFile("ic2/advancedmachines/client/sprites/block_advmachine.png");
      this.setIconIndex(3);
   }
}