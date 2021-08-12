package ic2.core.item.reactor;

import ic2.api.IReactor;
import ic2.api.IReactorComponent;
import ic2.core.Ic2Items;
import ic2.core.item.ItemGradual;
import net.minecraft.item.ItemStack;

public class ItemReactorDepletedUranium extends ItemGradual implements IReactorComponent {

   public ItemReactorDepletedUranium(int id, int index) {
      super(id, index);
   }

   public void processChamber(IReactor reactor, ItemStack yourStack, int x, int y) {}

   public boolean acceptUraniumPulse(IReactor reactor, ItemStack yourStack, ItemStack pulsingStack, int youX, int youY, int pulseX, int pulseY) {
      int myLevel = yourStack.getItemDamage() - 1 - reactor.getHeat() / 3000;
      if(myLevel <= 0) {
         reactor.setItemAt(youX, youY, new ItemStack(Ic2Items.reEnrichedUraniumCell.getItem()));
      } else {
         yourStack.setItemDamage(myLevel);
      }

      return true;
   }

   public boolean canStoreHeat(IReactor reactor, ItemStack yourStack, int x, int y) {
      return false;
   }

   public int getMaxHeat(IReactor reactor, ItemStack yourStack, int x, int y) {
      return 0;
   }

   public int getCurrentHeat(IReactor reactor, ItemStack yourStack, int x, int y) {
      return 0;
   }

   public int alterHeat(IReactor reactor, ItemStack yourStack, int x, int y, int heat) {
      return heat;
   }

   public float influenceExplosion(IReactor reactor, ItemStack yourStack) {
      return 0.0F;
   }
}
