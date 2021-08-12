package ic2.api;

import ic2.api.IReactor;
import net.minecraft.item.ItemStack;

public interface IReactorComponent {

   void processChamber(IReactor var1, ItemStack var2, int var3, int var4);

   boolean acceptUraniumPulse(IReactor var1, ItemStack var2, ItemStack var3, int var4, int var5, int var6, int var7);

   boolean canStoreHeat(IReactor var1, ItemStack var2, int var3, int var4);

   int getMaxHeat(IReactor var1, ItemStack var2, int var3, int var4);

   int getCurrentHeat(IReactor var1, ItemStack var2, int var3, int var4);

   int alterHeat(IReactor var1, ItemStack var2, int var3, int var4, int var5);

   float influenceExplosion(IReactor var1, ItemStack var2);
}
