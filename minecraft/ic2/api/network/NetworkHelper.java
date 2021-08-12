package ic2.api.network;

import ic2.api.network.INetworkDataProvider;
import java.lang.reflect.Method;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public final class NetworkHelper {

   private static Object instance;
   private static Method NetworkManager_updateTileEntityField;
   private static Method NetworkManager_initiateTileEntityEvent;
   private static Method NetworkManager_initiateItemEvent;
   private static Method NetworkManager_announceBlockUpdate;
   private static Method NetworkManager_requestInitialData;
   private static Method NetworkManager_initiateClientTileEntityEvent;
   private static Method NetworkManager_initiateClientItemEvent;


   public static void updateTileEntityField(TileEntity te, String field) {
      try {
         if(NetworkManager_updateTileEntityField == null) {
            NetworkManager_updateTileEntityField = Class.forName(getPackage() + ".core.network.NetworkManager").getMethod("updateTileEntityField", new Class[]{TileEntity.class, String.class});
         }

         if(instance == null) {
            instance = getInstance();
         }

         NetworkManager_updateTileEntityField.invoke(instance, new Object[]{te, field});
      } catch (Exception var3) {
         throw new RuntimeException(var3);
      }
   }

   public static void initiateTileEntityEvent(TileEntity te, int event, boolean limitRange) {
      try {
         if(NetworkManager_initiateTileEntityEvent == null) {
            NetworkManager_initiateTileEntityEvent = Class.forName(getPackage() + ".core.network.NetworkManager").getMethod("initiateTileEntityEvent", new Class[]{TileEntity.class, Integer.TYPE, Boolean.TYPE});
         }

         if(instance == null) {
            instance = getInstance();
         }

         NetworkManager_initiateTileEntityEvent.invoke(instance, new Object[]{te, Integer.valueOf(event), Boolean.valueOf(limitRange)});
      } catch (Exception var4) {
         throw new RuntimeException(var4);
      }
   }

   public static void initiateItemEvent(EntityPlayer player, ItemStack itemStack, int event, boolean limitRange) {
      try {
         if(NetworkManager_initiateItemEvent == null) {
            NetworkManager_initiateItemEvent = Class.forName(getPackage() + ".core.network.NetworkManager").getMethod("initiateItemEvent", new Class[]{EntityPlayer.class, ItemStack.class, Integer.TYPE, Boolean.TYPE});
         }

         if(instance == null) {
            instance = getInstance();
         }

         NetworkManager_initiateItemEvent.invoke(instance, new Object[]{player, itemStack, Integer.valueOf(event), Boolean.valueOf(limitRange)});
      } catch (Exception var5) {
         throw new RuntimeException(var5);
      }
   }

   public static void announceBlockUpdate(World world, int x, int y, int z) {
      try {
         if(NetworkManager_announceBlockUpdate == null) {
            NetworkManager_announceBlockUpdate = Class.forName(getPackage() + ".core.network.NetworkManager").getMethod("announceBlockUpdate", new Class[]{World.class, Integer.TYPE, Integer.TYPE, Integer.TYPE});
         }

         if(instance == null) {
            instance = getInstance();
         }

         NetworkManager_announceBlockUpdate.invoke(instance, new Object[]{world, Integer.valueOf(x), Integer.valueOf(y), Integer.valueOf(z)});
      } catch (Exception var5) {
         throw new RuntimeException(var5);
      }
   }

   public static void requestInitialData(INetworkDataProvider dataProvider) {
      try {
         if(NetworkManager_requestInitialData == null) {
            NetworkManager_requestInitialData = Class.forName(getPackage() + ".core.network.NetworkManager").getMethod("requestInitialData", new Class[]{INetworkDataProvider.class});
         }

         if(instance == null) {
            instance = getInstance();
         }

         NetworkManager_requestInitialData.invoke(instance, new Object[]{dataProvider});
      } catch (Exception var2) {
         throw new RuntimeException(var2);
      }
   }

   public static void initiateClientTileEntityEvent(TileEntity te, int event) {
      try {
         if(NetworkManager_initiateClientTileEntityEvent == null) {
            NetworkManager_initiateClientTileEntityEvent = Class.forName(getPackage() + ".core.network.NetworkManager").getMethod("initiateClientTileEntityEvent", new Class[]{TileEntity.class, Integer.TYPE});
         }

         if(instance == null) {
            instance = getInstance();
         }

         NetworkManager_initiateClientTileEntityEvent.invoke(instance, new Object[]{te, Integer.valueOf(event)});
      } catch (Exception var3) {
         throw new RuntimeException(var3);
      }
   }

   public static void initiateClientItemEvent(ItemStack itemStack, int event) {
      try {
         if(NetworkManager_initiateClientItemEvent == null) {
            NetworkManager_initiateClientItemEvent = Class.forName(getPackage() + ".core.network.NetworkManager").getMethod("initiateClientItemEvent", new Class[]{ItemStack.class, Integer.TYPE});
         }

         if(instance == null) {
            instance = getInstance();
         }

         NetworkManager_initiateClientItemEvent.invoke(instance, new Object[]{itemStack, Integer.valueOf(event)});
      } catch (Exception var3) {
         throw new RuntimeException(var3);
      }
   }

   private static String getPackage() {
      Package pkg = NetworkHelper.class.getPackage();
      if(pkg != null) {
         String packageName = pkg.getName();
         return packageName.substring(0, packageName.length() - ".api.network".length());
      } else {
         return "ic2";
      }
   }

   private static Object getInstance() {
      try {
         return Class.forName(getPackage() + ".core.IC2").getDeclaredField("network").get((Object)null);
      } catch (Throwable var1) {
         throw new RuntimeException(var1);
      }
   }
}
