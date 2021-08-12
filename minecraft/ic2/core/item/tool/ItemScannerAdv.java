package ic2.core.item.tool;

import ic2.core.item.ElectricItem;
import ic2.core.item.tool.ItemScanner;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ItemScannerAdv extends ItemScanner {

   public ItemScannerAdv(int id, int index, int t) {
      super(id, index, t);
   }

   public int startLayerScan(ItemStack itemStack) {
      return ElectricItem.use(itemStack, 250, (EntityPlayer)null)?4:0;
   }
}
