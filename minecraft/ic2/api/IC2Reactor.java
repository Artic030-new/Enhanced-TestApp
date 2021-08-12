package ic2.api;

import java.lang.reflect.Field;

public class IC2Reactor {

   private static Field energyGeneratorNuclear;


   public static int getEUOutput() {
      try {
         if(energyGeneratorNuclear == null) {
            energyGeneratorNuclear = Class.forName(getPackage() + ".core.IC2").getDeclaredField("energyGeneratorNuclear");
         }

         return energyGeneratorNuclear.getInt((Object)null);
      } catch (Throwable var1) {
         throw new RuntimeException(var1);
      }
   }

   private static String getPackage() {
      Package pkg = IC2Reactor.class.getPackage();
      return pkg != null?pkg.getName().substring(0, pkg.getName().lastIndexOf(46)):"ic2";
   }
}
