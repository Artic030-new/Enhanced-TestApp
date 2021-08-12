package ic2.core.item.reactor;

import ic2.api.IReactor;
import ic2.api.IReactorComponent;
import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.item.ItemGradual;
import java.util.ArrayList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;

public class ItemReactorUranium extends ItemGradual implements IReactorComponent {

   public int numberOfCells;


   public ItemReactorUranium(int id, int index, int cells) {
      super(id, index);
      this.numberOfCells = cells;
   }

   public void processChamber(IReactor reactor, ItemStack yourStack, int x, int y) {
      if(reactor.produceEnergy()) {
         for(int iteration = 0; iteration < this.numberOfCells; ++iteration) {
            int pulses = 1 + this.numberOfCells / 2;

            int heat;
            for(heat = 0; heat < pulses; ++heat) {
               this.acceptUraniumPulse(reactor, yourStack, yourStack, x, y, x, y);
            }

            pulses += this.checkPulseable(reactor, x - 1, y, yourStack, x, y) + this.checkPulseable(reactor, x + 1, y, yourStack, x, y) + this.checkPulseable(reactor, x, y - 1, yourStack, x, y) + this.checkPulseable(reactor, x, y + 1, yourStack, x, y);
            heat = this.sumUp(pulses) * 4;
            ArrayList heatAcceptors = new ArrayList();
            this.checkHeatAcceptor(reactor, x - 1, y, heatAcceptors);
            this.checkHeatAcceptor(reactor, x + 1, y, heatAcceptors);
            this.checkHeatAcceptor(reactor, x, y - 1, heatAcceptors);
            this.checkHeatAcceptor(reactor, x, y + 1, heatAcceptors);

            while(heatAcceptors.size() > 0 && heat > 0) {
               int dheat = heat / heatAcceptors.size();
               heat -= dheat;
               dheat = ((IReactorComponent)((ItemReactorUranium.ItemStackCoord)heatAcceptors.get(0)).stack.getItem()).alterHeat(reactor, ((ItemReactorUranium.ItemStackCoord)heatAcceptors.get(0)).stack, ((ItemReactorUranium.ItemStackCoord)heatAcceptors.get(0)).x, ((ItemReactorUranium.ItemStackCoord)heatAcceptors.get(0)).y, dheat);
               heat += dheat;
               heatAcceptors.remove(0);
            }

            if(heat > 0) {
               reactor.addHeat(heat);
            }
         }

         if(yourStack.getItemDamage() >= this.getMaxDamage() - 1) {
            if(IC2.random.nextInt(3) == 0) {
               reactor.setItemAt(x, y, new ItemStack(Ic2Items.nearDepletedUraniumCell.getItem(), this.numberOfCells));
            } else {
               reactor.setItemAt(x, y, (ItemStack)null);
            }
         } else {
            yourStack.damageItem(1, (EntityLiving)null);
         }

      }
   }

   private int checkPulseable(IReactor reactor, int x, int y, ItemStack me, int mex, int mey) {
      ItemStack other = reactor.getItemAt(x, y);
      return other != null && other.getItem() instanceof IReactorComponent && ((IReactorComponent)other.getItem()).acceptUraniumPulse(reactor, other, me, x, y, mex, mey)?1:0;
   }

   private int sumUp(int x) {
      int sum = 0;

      for(int i = 1; i <= x; ++i) {
         sum += i;
      }

      return sum;
   }

   private void checkHeatAcceptor(IReactor reactor, int x, int y, ArrayList heatAcceptors) {
      ItemStack thing = reactor.getItemAt(x, y);
      if(thing != null && thing.getItem() instanceof IReactorComponent && ((IReactorComponent)thing.getItem()).canStoreHeat(reactor, thing, x, y)) {
         heatAcceptors.add(new ItemReactorUranium.ItemStackCoord(thing, x, y));
      }

   }

   public boolean acceptUraniumPulse(IReactor reactor, ItemStack yourStack, ItemStack pulsingStack, int youX, int youY, int pulseX, int pulseY) {
      reactor.addOutput(1);
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
      return (float)(2 * this.numberOfCells);
   }

   private class ItemStackCoord {

      public ItemStack stack;
      public int x;
      public int y;


      public ItemStackCoord(ItemStack stack, int x, int y) {
         this.stack = stack;
         this.x = x;
         this.y = y;
      }
   }
}
