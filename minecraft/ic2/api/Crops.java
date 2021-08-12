package ic2.api;

import java.util.HashMap;
import net.minecraft.world.biome.BiomeGenBase;

public class Crops {

   private static final HashMap humidityBiomeBonus = new HashMap();
   private static final HashMap nutrientBiomeBonus = new HashMap();


   public static void addBiomeBonus(BiomeGenBase biome, int humidityBonus, int nutrientsBonus) {
      humidityBiomeBonus.put(biome, Integer.valueOf(humidityBonus));
      nutrientBiomeBonus.put(biome, Integer.valueOf(nutrientsBonus));
   }

   public static int getHumidityBiomeBonus(BiomeGenBase biome) {
      return humidityBiomeBonus.containsKey(biome)?((Integer)humidityBiomeBonus.get(biome)).intValue():0;
   }

   public static int getNutrientBiomeBonus(BiomeGenBase biome) {
      return nutrientBiomeBonus.containsKey(biome)?((Integer)nutrientBiomeBonus.get(biome)).intValue():0;
   }

}
