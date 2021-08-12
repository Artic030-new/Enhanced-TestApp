package ic2.core;

import ic2.core.IC2DamageSource;
import net.minecraft.entity.EntityLiving;
import net.minecraft.potion.Potion;

public class IC2Potion extends Potion {

   public static final IC2Potion radiation = new IC2Potion(24, true, 5149489);


   public IC2Potion(int id, boolean badEffect, int liquidColor) {
      super(id, badEffect, liquidColor);
   }

   public void performEffect(EntityLiving entity, int amplifier) {
      if(super.id == radiation.id) {
         entity.attackEntityFrom(IC2DamageSource.radiation, amplifier + 1);
      }

   }

   public boolean isReady(int duration, int amplifier) {
      if(super.id == radiation.id) {
         int rate = 25 >> amplifier;
         return rate > 0?duration % rate == 0:true;
      } else {
         return false;
      }
   }

   public static void init() {
      radiation.setPotionName("potion.radiation");
      radiation.setIconIndex(6, 0);
      radiation.setEffectiveness(0.25D);
   }

}
