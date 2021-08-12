package ic2.api;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

public interface IReactor {

   ChunkCoordinates getPosition();

   World getWorld();

   int getHeat();

   void setHeat(int var1);

   int addHeat(int var1);

   int getMaxHeat();

   void setMaxHeat(int var1);

   float getHeatEffectModifier();

   void setHeatEffectModifier(float var1);

   int getOutput();

   int addOutput(int var1);

   @Deprecated
   int getPulsePower();

   ItemStack getItemAt(int var1, int var2);

   void setItemAt(int var1, int var2, ItemStack var3);

   void explode();

   int getTickRate();

   boolean produceEnergy();
}
