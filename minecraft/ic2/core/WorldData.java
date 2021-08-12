package ic2.core;

import ic2.core.EnergyNet;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.WeakHashMap;
import net.minecraft.world.World;

public class WorldData {

   private static Map mapping = new WeakHashMap();
   public Queue singleTickCallbacks = new ArrayDeque();
   public Set continuousTickCallbacks = new HashSet();
   public boolean continuousTickCallbacksInUse = false;
   public List continuousTickCallbacksToAdd = new ArrayList();
   public List continuousTickCallbacksToRemove = new ArrayList();
   public EnergyNet energyNet = new EnergyNet();
   public Set networkedFieldsToUpdate = new HashSet();
   public int ticksLeftToNetworkUpdate = 2;


   public static WorldData get(World world) {
      if(world == null) {
         throw new IllegalArgumentException("world is null");
      } else {
         WorldData ret = (WorldData)mapping.get(world);
         if(ret == null) {
            ret = new WorldData();
            mapping.put(world, ret);
         }

         return ret;
      }
   }

   public static void onWorldUnload(World world) {
      mapping.remove(world);
   }

}
