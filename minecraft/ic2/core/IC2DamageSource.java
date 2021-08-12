package ic2.core;

import ic2.core.IC2;
import net.minecraft.util.DamageSource;

public class IC2DamageSource extends DamageSource {

   public static IC2DamageSource electricity = new IC2DamageSource("electricity");
   public static IC2DamageSource nuke = new IC2DamageSource("nuke");
   public static IC2DamageSource radiation = (IC2DamageSource)(new IC2DamageSource("radiation")).setDamageBypassesArmor();


   public IC2DamageSource(String s) {
      super(s);
   }

   public static void addLocalization() {
      IC2.platform.addLocalization("death.electricity", "%1$s was electrocuted");
      IC2.platform.addLocalization("death.nuke", "%1$s was nuked");
      IC2.platform.addLocalization("death.radiation", "%1$s died from radiation");
   }

}
