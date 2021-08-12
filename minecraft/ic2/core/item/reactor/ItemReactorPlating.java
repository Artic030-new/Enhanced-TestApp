package ic2.core.item.reactor;

import ic2.api.IReactor;
import ic2.api.IReactorComponent;
import ic2.core.item.ItemIC2;
import net.minecraft.item.ItemStack;

public class ItemReactorPlating extends ItemIC2 implements IReactorComponent {

   public int maxHeatAdd;
   public float effectModifier;


   public ItemReactorPlating(int id, int index, int maxheatadd, float effectmodifier) {
      super(id, index);
      this.maxHeatAdd = maxheatadd;
      this.effectModifier = effectmodifier;
   }

   public void processChamber(IReactor reactor, ItemStack yourStack, int x, int y) {
      reactor.setMaxHeat(reactor.getMaxHeat() + this.maxHeatAdd);
      reactor.setHeatEffectModifier(reactor.getHeatEffectModifier() * this.effectModifier);
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
      return this.effectModifier >= 1.0F?0.0F:this.effectModifier;
   }
}
