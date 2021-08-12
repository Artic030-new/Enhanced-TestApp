package ic2.core.item.reactor;

import ic2.api.IReactor;
import ic2.api.IReactorComponent;
import ic2.core.item.ItemIC2;
import net.minecraft.item.ItemStack;

public class ItemReactorHeatpack extends ItemIC2 implements IReactorComponent {

   public int maxPer;
   public int heatPer;


   public ItemReactorHeatpack(int id, int index, int maxper, int heatper) {
      super(id, index);
      this.maxPer = maxper;
      this.heatPer = heatper;
   }

   public void processChamber(IReactor reactor, ItemStack yourStack, int x, int y) {
      this.heat(reactor, yourStack.stackSize, x + 1, y);
      this.heat(reactor, yourStack.stackSize, x - 1, y);
      this.heat(reactor, yourStack.stackSize, x, y + 1);
      this.heat(reactor, yourStack.stackSize, x, y - 1);
   }

   private void heat(IReactor reactor, int stacksize, int x, int y) {
      int want = this.maxPer * stacksize;
      if(reactor.getHeat() < want) {
         ItemStack stack = reactor.getItemAt(x, y);
         if(stack != null && stack.getItem() instanceof IReactorComponent) {
            IReactorComponent comp = (IReactorComponent)stack.getItem();
            if(comp.canStoreHeat(reactor, stack, x, y)) {
               int add = this.heatPer * stacksize;
               int curr = comp.getCurrentHeat(reactor, stack, x, y);
               if(add > want - curr) {
                  add = want - curr;
               }

               if(add > 0) {
                  comp.alterHeat(reactor, stack, x, y, add);
               }
            }
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
      return 0;
   }

   public float influenceExplosion(IReactor reactor, ItemStack yourStack) {
      return (float)(yourStack.stackSize / 10);
   }
}
