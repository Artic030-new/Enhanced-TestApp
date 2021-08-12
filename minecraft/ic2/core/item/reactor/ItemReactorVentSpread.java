package ic2.core.item.reactor;

import ic2.api.IReactor;
import ic2.api.IReactorComponent;
import ic2.core.item.ItemIC2;
import net.minecraft.item.ItemStack;

public class ItemReactorVentSpread extends ItemIC2 implements IReactorComponent {

   public int sideVent;


   public ItemReactorVentSpread(int id, int index, int sidevent) {
      super(id, index);
      this.setMaxStackSize(1);
      this.sideVent = sidevent;
   }

   public void processChamber(IReactor reactor, ItemStack yourStack, int x, int y) {
      this.cool(reactor, x - 1, y);
      this.cool(reactor, x + 1, y);
      this.cool(reactor, x, y - 1);
      this.cool(reactor, x, y + 1);
   }

   private void cool(IReactor reactor, int x, int y) {
      ItemStack stack = reactor.getItemAt(x, y);
      if(stack != null && stack.getItem() instanceof IReactorComponent) {
         IReactorComponent comp = (IReactorComponent)stack.getItem();
         if(comp.canStoreHeat(reactor, stack, x, y)) {
            comp.alterHeat(reactor, stack, x, y, -this.sideVent);
         }
      }

   }

   public boolean acceptUraniumPulse(IReactor reactor, ItemStack yourStack, ItemStack pulsingStack, int youX, int youY, int pulseX, int pulseY) {
      return false;
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
