package gravisuite;

import gravisuite.GraviSuite;
import gravisuite.ItemGraviChestPlate;
import gravisuite.Keyboard;
import gravisuite.ServerProxy;
import gravisuite.ServerTickHandler;
import ic2.api.ElectricItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.ModLoader;

public class GraviChestPlateServerProxy {

   private MinecraftServer mc = ModLoader.getMinecraftServerInstance();

   public static boolean switchFlyModeServer(EntityPlayer player, ItemStack itemstack) {
      if(ItemGraviChestPlate.readFlyStatus(itemstack)) {
         if(!player.capabilities.isCreativeMode) {
            player.capabilities.allowFlying = false;
            player.capabilities.isFlying = false;
         }

         ServerProxy.sendPlayerMessage(player, "§cГравитационный двигатель отключен");
         ServerTickHandler.isFlyActiveByMod.put(player, Boolean.valueOf(false));
         ItemGraviChestPlate.saveFlyStatus(itemstack, false);
      } else {
         int currCharge = ItemGraviChestPlate.getCharge(itemstack);
         if(currCharge < ItemGraviChestPlate.minCharge && !player.capabilities.isCreativeMode) {
            ServerProxy.sendPlayerMessage(player, "Недостаточно энергии для включения гравитационного двигателя!");
         } else {
            ServerProxy.sendPlayerMessage(player, "§aГравитационный двигатель включен");
            player.capabilities.allowFlying = true;
            player.capabilities.isFlying = true;
            ServerTickHandler.isFlyActiveByMod.put(player, Boolean.valueOf(true));
            ItemGraviChestPlate.saveFlyStatus(itemstack, true);
         }
      }

      return true;
   }

   public static boolean boostModeServer(EntityPlayer player, ItemStack itemstack, float moveStrafing, float moveForward) {
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

   public static boolean firstLoadServer(EntityPlayer player, ItemStack itemstack) {
      if(ItemGraviChestPlate.readFlyStatus(itemstack)) {
         ItemGraviChestPlate.saveFlyStatus(itemstack, false);
         switchFlyModeServer(player, itemstack);
         ServerProxy.sendPacket(player, "setFlyStatus", 0);
      }

      return true;
   }

   public static boolean onTickServer(EntityPlayer player, ItemStack itemstack, float moveStrafing, float moveForward) {
      if(ServerTickHandler.checkLastUndressed(player)) {
         ItemGraviChestPlate.saveFlyStatus(itemstack, false);
         ServerTickHandler.lastUndressed.put(player, Boolean.valueOf(false));
      }

      if(ItemGraviChestPlate.readFlyStatus(itemstack)) {
         NBTTagCompound nbttagcompound = GraviSuite.getOrCreateNbtData(itemstack);
         int currCharge = ItemGraviChestPlate.getCharge(itemstack);
         if(!player.capabilities.isCreativeMode) {
            if(currCharge < ItemGraviChestPlate.dischargeOnTick) {
               ServerProxy.sendPlayerMessage(player, "§cВнимание! Закончилась энергия. Гравитационный двигатель будет отключен.");
               switchFlyModeServer(player, itemstack);
            } else {
               ElectricItem.discharge(itemstack, ItemGraviChestPlate.dischargeOnTick, 3, true, false);
            }
         }

         player.fallDistance = 0.0F;
         if(!player.onGround && player.capabilities.isFlying && Keyboard.isBoostKeyDown(player)) {
            boostModeServer(player, itemstack, moveStrafing, moveForward);
            if(currCharge <= ItemGraviChestPlate.dischargeOnTick * ItemGraviChestPlate.boostMultiplier && !player.capabilities.isCreativeMode) {
               ServerProxy.sendPlayerMessage(player, "Недостаточно энергии для ускорения!");
            } else {
               if(Keyboard.isJumpKeyDown(player)) {
                  player.motionY += (double)(ItemGraviChestPlate.boostSpeed + 0.03F);
               }

               if(Keyboard.isSneakKeyDown(player)) {
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
