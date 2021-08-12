package gravisuite;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import gravisuite.GraviChestPlateClientProxy;
import gravisuite.GraviSuite;
import gravisuite.IItemTickListener;
import gravisuite.ItemAdvancedJetPack;
import gravisuite.ItemAdvancedLappack;
import gravisuite.ItemGraviChestPlate;
import gravisuite.ItemUltimateLappack;
import gravisuite.KeyboardClient;
import gravisuite.audio.AudioManager;
import java.util.EnumSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.item.ItemStack;
import net.minecraft.src.ModLoader;

public class ClientTickHandler implements ITickHandler {

   private boolean keyDown;
   public static Minecraft mc = ModLoader.getMinecraftInstance();
   public static boolean isFlyActiveByMod = false;
   public static boolean isFirstLoad = false;
   public static boolean isLastUndressed = false;
   public static boolean isLastCreativeState = false;

   public void tickStart(EnumSet type, Object ... tickData) {
      if(type.contains(TickType.CLIENT) && mc.theWorld != null) {
         if(!isFirstLoad) {
            isFirstLoad = true;

            for(int var41 = 0; var41 < mc.gameSettings.keyBindings.length; ++var41) {
               if(mc.gameSettings.keyBindings[var41].keyDescription == "Boost Key") {
                  KeyboardClient.icBoostKeyID = var41;
               }

               if(mc.gameSettings.keyBindings[var41].keyDescription == "ALT Key") {
                  KeyboardClient.icAltKeyID = var41;
               }

               if(mc.gameSettings.keyBindings[var41].keyDescription == "Mode Switch Key") {
                  KeyboardClient.icModeKeyID = var41;
               }
            }
         }

         AudioManager.onTick();
         GraviSuite.keyboard.sendKeyUpdate(mc.thePlayer);
         ItemStack var4 = mc.thePlayer.inventory.armorInventory[2];
         if(var4 != null && var4.getItem() == GraviSuite.graviChestPlate) {
            if(mc.thePlayer.capabilities.isCreativeMode && !isLastCreativeState) {
               isLastCreativeState = true;
            } else if(!mc.thePlayer.capabilities.isCreativeMode && isLastCreativeState && isFlyActiveByMod) {
               mc.thePlayer.capabilities.allowFlying = true;
               mc.thePlayer.capabilities.isFlying = true;
               isLastCreativeState = false;
            }

            GraviChestPlateClientProxy.onTickClient(mc.thePlayer, var4, KeyboardClient.moveStrafe, KeyboardClient.moveForward);
            if(mc.thePlayer.posY > 262.0D && !mc.thePlayer.capabilities.isCreativeMode) {
               mc.thePlayer.setPosition(mc.thePlayer.posX, 262.0D, mc.thePlayer.posZ);
            }
         } else if(isFlyActiveByMod) {
            mc.thePlayer.capabilities.allowFlying = false;
            mc.thePlayer.capabilities.isFlying = false;
            isFlyActiveByMod = false;
            isLastUndressed = true;
         }

         if(var4 != null && (var4.getItem() == GraviSuite.advJetpack || var4.getItem() == GraviSuite.advNanoChestPlate)) {
            KeyboardClient.isModeKeyPress(mc.thePlayer);
            ((IItemTickListener)var4.getItem()).onTick(mc.thePlayer, var4);
         }
      }

   }

   public void tickEnd(EnumSet type, Object ... tickData) {
      if(type.contains(TickType.RENDER) && GraviSuite.displayHud && mc.theWorld != null && mc.inGameHasFocus) {
         Minecraft var10000 = mc;
         if(!Minecraft.isDebugInfoEnabled()) {
            ItemStack itemstack = mc.thePlayer.inventory.armorItemInSlot(2);
            int xPos = 0;
            int yPos = 0;
            int xPos2 = 0;
            int yPos2 = 0;
            int statusFontWidth = 0;
            int elevelFontWidth = 0;
            String elevelString = "";
            String statusString = "";
            byte yOffset = 3;
            int currCharge;
            int energyStatus;
            if(itemstack != null && itemstack.getItem() == GraviSuite.graviChestPlate) {
               currCharge = ItemGraviChestPlate.getCharge(itemstack);
               energyStatus = currCharge * 100 / ItemGraviChestPlate.maxCharge;
               elevelString = "Уровень энергии: " + this.GetTextEnergyStatus(energyStatus);
               elevelFontWidth = mc.fontRenderer.getStringWidth(elevelString);
               if(isFlyActiveByMod && ItemGraviChestPlate.readFlyStatus(itemstack)) {
                  statusString = "§aГравитационный двигатель включен";
                  statusFontWidth = mc.fontRenderer.getStringWidth(statusString);
               } else {
                  statusString = "";
               }
            } else if(itemstack != null && (itemstack.getItem() == GraviSuite.ultimateLappack || itemstack.getItem() == GraviSuite.advLappack)) {
               int scaledresolution121;
               if(itemstack.getItem() == GraviSuite.ultimateLappack) {
                  ItemUltimateLappack disp_width121 = (ItemUltimateLappack)itemstack.getItem();
                  currCharge = ItemUltimateLappack.getCharge(itemstack);
                  scaledresolution121 = currCharge * 100;
                  ItemUltimateLappack disp_height132 = (ItemUltimateLappack)itemstack.getItem();
                  energyStatus = scaledresolution121 / ItemUltimateLappack.maxCharge;
               } else if(itemstack.getItem() == GraviSuite.advLappack) {
                  ItemAdvancedLappack disp_width124 = (ItemAdvancedLappack)itemstack.getItem();
                  currCharge = ItemAdvancedLappack.getCharge(itemstack);
                  scaledresolution121 = currCharge * 100;
                  ItemAdvancedLappack disp_height131 = (ItemAdvancedLappack)itemstack.getItem();
                  energyStatus = scaledresolution121 / ItemAdvancedLappack.maxCharge;
               } else {
                  boolean disp_width123 = false;
                  energyStatus = 0;
               }

               elevelString = "Уровень энергии: " + this.GetTextEnergyStatus(energyStatus);
               elevelFontWidth = mc.fontRenderer.getStringWidth("Уровень энергии: " + Integer.toString(energyStatus) + "%");
            } else if(itemstack != null && (itemstack.getItem() == GraviSuite.advJetpack || itemstack.getItem() == GraviSuite.advNanoChestPlate)) {
               currCharge = ItemAdvancedJetPack.getCharge(itemstack);
               energyStatus = currCharge * 100 / ItemAdvancedJetPack.maxCharge;
               boolean scaledresolution12 = ItemAdvancedJetPack.readWorkMode(itemstack);
               String disp_width12 = "";
               String disp_height13 = "";
               if(scaledresolution12) {
                  disp_width12 = " (режим парения)";
                  disp_height13 = " §e(режим парения)";
               }

               elevelString = "Уровень Энергии: " + this.GetTextEnergyStatus(energyStatus);
               elevelFontWidth = mc.fontRenderer.getStringWidth("Уровень энергии: " + Integer.toString(energyStatus) + "%");
               if(ItemAdvancedJetPack.readFlyStatus(itemstack)) {
                  statusString = "§eРеактивный ранец §aвключен" + disp_height13;
                  statusFontWidth = mc.fontRenderer.getStringWidth("§eРеактивный ранец §aвключен" + disp_width12);
               } else {
                  statusString = "";
               }
            }

            if(elevelString.isEmpty()) {
               ScaledResolution scaledresolution122 = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
               int disp_width122 = scaledresolution122.getScaledWidth();
               int disp_height133 = scaledresolution122.getScaledHeight();
               if(GraviSuite.hudPos == 1) {
                  xPos = 2;
                  xPos2 = 2;
                  yPos = 24;
                  yPos2 = 24 + yOffset + mc.fontRenderer.FONT_HEIGHT;
               }

               if(GraviSuite.hudPos == 2) {
            	  if(statusString.isEmpty()) {
                     xPos = disp_width122 - statusFontWidth - 2;
                  }

                  xPos2 = disp_width122 - elevelFontWidth - 2;
                  yPos = 24;
                  yPos2 = 24 + yOffset + mc.fontRenderer.FONT_HEIGHT;
               }

               if(GraviSuite.hudPos == 3) {
                  xPos = 2;
                  xPos2 = 2;
                  yPos = disp_height133 - 2 - mc.fontRenderer.FONT_HEIGHT;
                  yPos2 = yPos - yOffset - mc.fontRenderer.FONT_HEIGHT;
               }

               if(GraviSuite.hudPos == 4) {
            	  if(statusString.isEmpty()) {
                     xPos = disp_width122 - statusFontWidth - 2;
                  }

                  xPos2 = disp_width122 - elevelFontWidth - 2;
                  yPos = disp_height133 - 2 - mc.fontRenderer.FONT_HEIGHT;
                  yPos2 = yPos - yOffset - mc.fontRenderer.FONT_HEIGHT;
               }

               if(statusString.isEmpty()) {
                  mc.ingameGUI.drawString(mc.fontRenderer, statusString, xPos, yPos, 16777215);
                  mc.ingameGUI.drawString(mc.fontRenderer, elevelString, xPos2, yPos2, 16777215);
               } else {
                  mc.ingameGUI.drawString(mc.fontRenderer, elevelString, xPos2, yPos, 16777215);
               }
            }
         }
      }

   }

   public EnumSet ticks() {
      return EnumSet.of(TickType.WORLD, TickType.WORLDLOAD, TickType.CLIENT, TickType.RENDER);
   }

   public String GetTextEnergyStatus(int energyStatus) {
      return energyStatus <= 10 && energyStatus > 5?"§6" + Integer.toString(energyStatus) + "%":(energyStatus <= 5?"§c" + Integer.toString(energyStatus) + "%":Integer.toString(energyStatus) + "%");
   }

   public String getLabel() {
      return "GraviSuite";
   }

}
