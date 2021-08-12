package ic2.api;

import java.lang.reflect.Method;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public final class ElectricItem {

   private static Method ElectricItem_charge;
   private static Method ElectricItem_discharge;
   private static Method ElectricItem_canUse;
   private static Method ElectricItem_use;
   private static Method ElectricItem_chargeFromArmor;


   public static int charge(ItemStack itemStack, int amount, int tier, boolean ignoreTransferLimit, boolean simulate) {
      try {
         if(ElectricItem_charge == null) {
            ElectricItem_charge = Class.forName(getPackage() + ".core.item.ElectricItem").getMethod("charge", new Class[]{ItemStack.class, Integer.TYPE, Integer.TYPE, Boolean.TYPE, Boolean.TYPE});
         }

         return ((Integer)ElectricItem_charge.invoke((Object)null, new Object[]{itemStack, Integer.valueOf(amount), Integer.valueOf(tier), Boolean.valueOf(ignoreTransferLimit), Boolean.valueOf(simulate)})).intValue();
      } catch (Exception var6) {
         throw new RuntimeException(var6);
      }
   }

   public static int discharge(ItemStack itemStack, int amount, int tier, boolean ignoreTransferLimit, boolean simulate) {
      try {
         if(ElectricItem_discharge == null) {
            ElectricItem_discharge = Class.forName(getPackage() + ".core.item.ElectricItem").getMethod("discharge", new Class[]{ItemStack.class, Integer.TYPE, Integer.TYPE, Boolean.TYPE, Boolean.TYPE});
         }

         return ((Integer)ElectricItem_discharge.invoke((Object)null, new Object[]{itemStack, Integer.valueOf(amount), Integer.valueOf(tier), Boolean.valueOf(ignoreTransferLimit), Boolean.valueOf(simulate)})).intValue();
      } catch (Exception var6) {
         throw new RuntimeException(var6);
      }
   }

   public static boolean canUse(ItemStack itemStack, int amount) {
      try {
         if(ElectricItem_canUse == null) {
            ElectricItem_canUse = Class.forName(getPackage() + ".core.item.ElectricItem").getMethod("canUse", new Class[]{ItemStack.class, Integer.TYPE});
         }

         return ((Boolean)ElectricItem_canUse.invoke((Object)null, new Object[]{itemStack, Integer.valueOf(amount)})).booleanValue();
      } catch (Exception var3) {
         throw new RuntimeException(var3);
      }
   }

   public static boolean use(ItemStack itemStack, int amount, EntityPlayer player) {
      try {
         if(ElectricItem_use == null) {
            ElectricItem_use = Class.forName(getPackage() + ".core.item.ElectricItem").getMethod("use", new Class[]{ItemStack.class, Integer.TYPE, EntityPlayer.class});
         }

         return ((Boolean)ElectricItem_use.invoke((Object)null, new Object[]{itemStack, Integer.valueOf(amount), player})).booleanValue();
      } catch (Exception var4) {
         throw new RuntimeException(var4);
      }
   }

   public static void chargeFromArmor(ItemStack itemStack, EntityPlayer player) {
      try {
         if(ElectricItem_chargeFromArmor == null) {
            ElectricItem_chargeFromArmor = Class.forName(getPackage() + ".core.item.ElectricItem").getMethod("chargeFromArmor", new Class[]{ItemStack.class, EntityPlayer.class});
         }

         ElectricItem_chargeFromArmor.invoke((Object)null, new Object[]{itemStack, player});
      } catch (Exception var3) {
         throw new RuntimeException(var3);
      }
   }

   private static String getPackage() {
      Package pkg = ElectricItem.class.getPackage();
      return pkg != null?pkg.getName().substring(0, pkg.getName().lastIndexOf(46)):"ic2";
   }
}
