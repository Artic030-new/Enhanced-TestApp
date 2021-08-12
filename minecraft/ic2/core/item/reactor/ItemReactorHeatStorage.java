package ic2.core.item.reactor;

import ic2.api.IReactor;
import ic2.api.IReactorComponent;
import ic2.core.item.ItemGradual;
import ic2.core.util.StackUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ItemReactorHeatStorage extends ItemGradual implements IReactorComponent {

   public int heatStorage;


   public ItemReactorHeatStorage(int id, int index, int heatStorage) {
      super(id, index);
      this.heatStorage = heatStorage;
   }

   public void processChamber(IReactor reactor, ItemStack yourStack, int x, int y) {}

   public boolean acceptUraniumPulse(IReactor reactor, ItemStack yourStack, ItemStack pulsingStack, int youX, int youY, int pulseX, int pulseY) {
      return false;
   }

   public boolean canStoreHeat(IReactor reactor, ItemStack yourStack, int x, int y) {
      return true;
   }

   public int getMaxHeat(IReactor reactor, ItemStack yourStack, int x, int y) {
      return this.heatStorage;
   }

   public int getCurrentHeat(IReactor reactor, ItemStack yourStack, int x, int y) {
      return this.getHeatOfStack(yourStack);
   }

   public int alterHeat(IReactor reactor, ItemStack yourStack, int x, int y, int heat) {
      int myHeat = this.getHeatOfStack(yourStack);
      myHeat += heat;
      if(myHeat > this.heatStorage) {
         reactor.setItemAt(x, y, (ItemStack)null);
         heat = this.heatStorage - myHeat + 1;
      } else {
         if(myHeat < 0) {
            heat = myHeat;
            myHeat = 0;
         } else {
            heat = 0;
         }

         this.setHeatForStack(yourStack, myHeat);
      }

      return heat;
   }

   public float influenceExplosion(IReactor reactor, ItemStack yourStack) {
      return 0.0F;
   }

   private void setHeatForStack(ItemStack stack, int heat) {
      NBTTagCompound nbtData = StackUtil.getOrCreateNbtData(stack);
      nbtData.setInteger("heat", heat);
      if(this.heatStorage > 0) {
         double p = (double)heat / (double)this.heatStorage;
         int newDmg = (int)((double)stack.getMaxDamage() * p);
         if(newDmg >= stack.getMaxDamage()) {
            newDmg = stack.getMaxDamage() - 1;
         }

         stack.setItemDamage(newDmg);
      }

   }

   private int getHeatOfStack(ItemStack stack) {
      NBTTagCompound nbtData = StackUtil.getOrCreateNbtData(stack);
      return nbtData.getInteger("heat");
   }
}
