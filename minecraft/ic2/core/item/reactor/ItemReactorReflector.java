package ic2.core.item.reactor;

import ic2.api.IReactor;
import ic2.api.IReactorComponent;
import ic2.core.item.ItemGradualInt;
import net.minecraft.item.ItemStack;

public class ItemReactorReflector extends ItemGradualInt implements IReactorComponent {

   public ItemReactorReflector(int id, int index, int maxDamage) {
      super(id, index, maxDamage);
   }

   public void processChamber(IReactor reactor, ItemStack yourStack, int x, int y) {}

   public boolean acceptUraniumPulse(IReactor reactor, ItemStack yourStack, ItemStack pulsingStack, int youX, int youY, int pulseX, int pulseY) {
      reactor.addOutput(1);
      if(this.getDamageOfStack(yourStack) + 1 >= super.maxDmg) {
         reactor.setItemAt(youX, youY, (ItemStack)null);
      } else {
         this.setDamageForStack(yourStack, this.getDamageOfStack(yourStack) + 1);
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
      return -1.0F;
   }
}
