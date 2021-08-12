package ic2.core.item.reactor;

import ic2.api.IReactor;
import ic2.core.item.reactor.ItemReactorHeatStorage;
import net.minecraft.item.ItemStack;

public class ItemReactorVent extends ItemReactorHeatStorage {

   public int selfVent;
   public int reactorVent;


   public ItemReactorVent(int id, int index, int heatStorage, int selfvent, int reactorvent) {
      super(id, index, heatStorage);
      this.selfVent = selfvent;
      this.reactorVent = reactorvent;
   }

   public void processChamber(IReactor reactor, ItemStack yourStack, int x, int y) {
      if(this.reactorVent > 0) {
         int rheat = reactor.getHeat();
         int reactorDrain = rheat;
         if(rheat > this.reactorVent) {
            reactorDrain = this.reactorVent;
         }

         rheat -= reactorDrain;
         if(this.alterHeat(reactor, yourStack, x, y, reactorDrain) > 0) {
            return;
         }

         reactor.setHeat(rheat);
      }

      this.alterHeat(reactor, yourStack, x, y, -this.selfVent);
   }
}
