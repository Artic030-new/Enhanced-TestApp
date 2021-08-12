package gravisuite;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import gravisuite.GraviChestPlateServerProxy;
import gravisuite.GraviSuite;
import gravisuite.IItemTickListener;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ServerTickHandler implements ITickHandler {

   public static Map isFlyActiveByMod = new HashMap();
   public static Map lastUndressed = new HashMap();
   public static Map isLastCreativeState = new HashMap();

   public void tickStart(EnumSet type, Object ... tickData) {
      if(type.contains(TickType.PLAYER)) {
         EntityPlayer player = (EntityPlayer)tickData[0];
         ItemStack itemstack = player.inventory.armorInventory[2];
         if(itemstack != null && (itemstack.getItem() == GraviSuite.advJetpack || itemstack.getItem() == GraviSuite.advNanoChestPlate)) {
            ((IItemTickListener)itemstack.getItem()).onTick(player, itemstack);
         }

         if(itemstack != null && itemstack.getItem() == GraviSuite.graviChestPlate) {
            if(player.capabilities.isCreativeMode && !checkLastCreativeState(player)) {
               isLastCreativeState.put(player, Boolean.valueOf(true));
            } else if(!player.capabilities.isCreativeMode && checkLastCreativeState(player) && checkFlyActiveByMode(player)) {
               player.capabilities.allowFlying = true;
               player.capabilities.isFlying = true;
               isLastCreativeState.put(player, Boolean.valueOf(false));
            }

            GraviChestPlateServerProxy.onTickServer(player, itemstack, 0.0F, 0.0F);
            if(player.posY > 262.0D && !player.capabilities.isCreativeMode) {
               player.setPosition(player.posX, 262.0D, player.posZ);
            }
         } else if(checkFlyActiveByMode(player)) {
            player.capabilities.allowFlying = false;
            player.capabilities.isFlying = false;
            isFlyActiveByMod.put(player, Boolean.valueOf(false));
            lastUndressed.put(player, Boolean.valueOf(true));
         }
      }

      if(type.contains(TickType.WORLDLOAD)) {
         ;
      }

   }

   public void tickEnd(EnumSet type, Object ... tickData) {}

   public EnumSet ticks() {
      return EnumSet.of(TickType.WORLDLOAD, TickType.PLAYER);
   }

   public String getLabel() {
      return "GraviSuite";
   }

   public static boolean checkLastUndressed(EntityPlayer player) {
      return lastUndressed.containsKey(player)?((Boolean)lastUndressed.get(player)).booleanValue():false;
   }

   public static boolean checkFlyActiveByMode(EntityPlayer player) {
      return isFlyActiveByMod.containsKey(player)?((Boolean)isFlyActiveByMod.get(player)).booleanValue():false;
   }

   public static boolean checkLastCreativeState(EntityPlayer player) {
      return isLastCreativeState.containsKey(player)?((Boolean)isLastCreativeState.get(player)).booleanValue():false;
   }

}
