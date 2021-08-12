package gravisuite;

import gravisuite.ClientProxy;
import gravisuite.ClientTickHandler;
import gravisuite.ItemGraviChestPlate;
import gravisuite.KeyboardClient;
import ic2.api.ElectricItem;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.src.ModLoader;

public class GraviChestPlateClientProxy {

   public static boolean firstLoad = false;
   private Minecraft mc = ModLoader.getMinecraftInstance();
   private static int ticker = 0;

   public static boolean switchFlyModeClient(EntityPlayer player, ItemStack itemstack) {
      if(ItemGraviChestPlate.readFlyStatus(itemstack)) {
         if(!player.capabilities.isCreativeMode) {
            player.capabilities.allowFlying = false;
            player.capabilities.isFlying = false;
         }

         ClientProxy.sendPlayerMessage(player, "§cГравитационный двигатель отключен");
         ClientTickHandler.isFlyActiveByMod = false;
         ItemGraviChestPlate.saveFlyStatus(itemstack, false);
      } else {
         int currCharge = ItemGraviChestPlate.getCharge(itemstack);
         if(currCharge < ItemGraviChestPlate.minCharge && !player.capabilities.isCreativeMode) {
            ClientProxy.sendPlayerMessage(player, "Недостаточно энергии для включения гравитационного двигателя!");
         } else {
            ClientProxy.sendPlayerMessage(player, "§aГравитационный двигатель включен");
            player.capabilities.allowFlying = true;
            player.capabilities.isFlying = true;
            ClientTickHandler.isFlyActiveByMod = true;
            ItemGraviChestPlate.saveFlyStatus(itemstack, true);
         }
      }

      return true;
   }

   public static boolean boostMode(EntityPlayer player, ItemStack itemstack, float moveStrafing, float moveForward) {
      if(ItemGraviChestPlate.readFlyStatus(itemstack) && !player.onGround && player.capabilities.isFlying && !player.isInWater()) {
         int currCharge = ItemGraviChestPlate.getCharge(itemstack);
         if(currCharge > ItemGraviChestPlate.dischargeOnTick * ItemGraviChestPlate.boostMultiplier || player.capabilities.isCreativeMode) {
            player.moveFlying(moveStrafing, moveForward, ItemGraviChestPlate.boostSpeed);
            if(!player.capabilities.isCreativeMode) {
               ElectricItem.discharge(itemstack, ItemGraviChestPlate.dischargeOnTick * ItemGraviChestPlate.boostMultiplier, 3, true, false);
            }
         }
      }

      return true;
   }

   public static boolean firstLoadClient(EntityPlayer player, ItemStack itemstack) {
      if(ItemGraviChestPlate.readFlyStatus(itemstack)) {
         ItemGraviChestPlate.saveFlyStatus(itemstack, false);
         switchFlyModeClient(player, itemstack);
      }

      return true;
   }

   public static boolean onTickClient(EntityPlayer player, ItemStack itemstack, float moveStrafing, float moveForward) {
      if(firstLoad) {
         ClientProxy.sendMyPacket("worldLoad", 1);
         firstLoad = false;
         return true;
      } else {
         if(ClientTickHandler.isLastUndressed) {
            ItemGraviChestPlate.saveFlyStatus(itemstack, false);
            ClientTickHandler.isLastUndressed = false;
         }

         if(ItemGraviChestPlate.readFlyStatus(itemstack)) {
            int currCharge = ItemGraviChestPlate.getCharge(itemstack);
            if(!player.capabilities.isCreativeMode) {
               if(currCharge < ItemGraviChestPlate.dischargeOnTick) {
                  ClientProxy.sendPlayerMessage(player, "§cВнимание! Закончилась энергия. Гравитационный двигатель будет отключен.");
                  switchFlyModeClient(player, itemstack);
               } else {
                  ElectricItem.discharge(itemstack, ItemGraviChestPlate.dischargeOnTick, 3, true, false);
               }
            }

            player.fallDistance = 0.0F;
            if(!player.onGround && player.capabilities.isFlying && KeyboardClient.isBoostKeyDown(player)) {
               KeyboardClient.updatePlayerMove();
               if(currCharge <= ItemGraviChestPlate.dischargeOnTick * ItemGraviChestPlate.boostMultiplier && !player.capabilities.isCreativeMode) {
                  ClientProxy.sendPlayerMessage(player, "Недостаточно энергии для ускорения!");
               } else {
                  boostMode(player, itemstack, moveStrafing, moveForward);
                  if(KeyboardClient.isJumpKeyDown(player)) {
                     player.motionY += (double)(ItemGraviChestPlate.boostSpeed + 0.03F);
                  }

                  if(KeyboardClient.isSneakKeyDown(player)) {
                     player.motionY -= (double)(ItemGraviChestPlate.boostSpeed + 0.03F);
                  }

                  if(!player.capabilities.isCreativeMode) {
                     ElectricItem.discharge(itemstack, ItemGraviChestPlate.dischargeOnTick * ItemGraviChestPlate.boostMultiplier, 3, true, false);
                  }
               }
            }
         }

         player.extinguish();
         return true;
      }
   }

}
