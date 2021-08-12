package ic2.core.util;


public final class Util {

   public static int roundToNegInf(float x) {
      int ret = (int)x;
      if((float)ret > x) {
         --ret;
      }

      return ret;
   }

   public static int roundToNegInf(double x) {
      int ret = (int)x;
      if((double)ret > x) {
         --ret;
      }

      return ret;
   }

   public static int countInArray(Object[] oa, Class cls) {
      int ret = 0;
      Object[] arr$ = oa;
      int len$ = oa.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         Object o = arr$[i$];
         if(cls.isAssignableFrom(o.getClass())) {
            ++ret;
         }
      }

      return ret;
   }
}
