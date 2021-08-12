package gravisuite;

import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gravisuite.ClientProxy;
import gravisuite.GraviChestPlateClientProxy;
import gravisuite.GraviSuite;
import gravisuite.Keyboard;
import java.util.EnumSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.src.ModLoader;

@SideOnly(Side.CLIENT)
public class KeyboardClient extends Keyboard {

   public static Minecraft mc = ModLoader.getMinecraftInstance();
   public static KeyBinding flyKey = new KeyBinding("Gravi Fly Key", 33);
   private static int lastKeyState = 0;
   private static boolean lastKeyModeState = false;
   public static int icBoostKeyID;
   public static int icAltKeyID;
   public static int icModeKeyID;
   public static float moveStrafe;
   public static float moveForward;

   public KeyboardClient() {
      KeyBindingRegistry.registerKeyBinding(new KeyHandler(new KeyBinding[]{flyKey}, new boolean[]{false}) {
         public String getLabel() {
            return "GraviSuiteKeyboard";
         }
         public void keyDown(EnumSet types, KeyBinding kb, boolean tickEnd, boolean isRepeat) {
            if(tickEnd && kb == KeyboardClient.flyKey && KeyboardClient.mc.inGameHasFocus) {
               ItemStack itemstack = KeyboardClient.mc.thePlayer.inventory.armorItemInSlot(2);
               if(itemstack != null && itemstack.getItem() == GraviSuite.graviChestPlate) {
                  ClientProxy.sendMyPacket("keyFLY", 1);
                  GraviChestPlateClientProxy.switchFlyModeClient(KeyboardClient.mc.thePlayer, itemstack);
               }

               if(itemstack != null && (itemstack.getItem() == GraviSuite.advJetpack || itemstack.getItem() == GraviSuite.advNanoChestPlate)) {
                  ClientProxy.sendMyPacket("keyFLY", 1);
               }
            }

         }
         public EnumSet ticks() {
            return EnumSet.of(TickType.CLIENT);
         }
         public void keyUp(EnumSet types, KeyBinding kb, boolean tickEnd) {}
      });
   }

   public static boolean isBoostKeyDown(EntityPlayer player) {
      return mc.gameSettings.keyBindings[icBoostKeyID].pressed;
   }

   public static boolean isAltKeyDown(EntityPlayer player) {
      return mc.gameSettings.keyBindings[icAltKeyID].pressed;
   }

   public static boolean isModeKeyPress(EntityPlayer player) {
      if(mc.gameSettings.keyBindings[icModeKeyID].pressed) {
         if(!lastKeyModeState) {
            lastKeyModeState = true;
            sendModeKey(player);
         }

         return true;
      } else {
         lastKeyModeState = false;
         return false;
      }
   }

   public static boolean isJumpKeyDown(EntityPlayer player) {
      return mc.gameSettings.keyBindJump.pressed;
   }

   public static boolean isForwardKeyDown(EntityPlayer player) {
      return mc.gameSettings.keyBindForward.pressed;
   }

   public static boolean isSneakKeyDown(EntityPlayer player) {
      return mc.gameSettings.keyBindSneak.pressed;
   }

   public static void sendModeKey(EntityPlayer player) {
      ClientProxy.sendMyPacket("keyModePressed", 1);
   }

   public void sendKeyUpdate(EntityPlayer player) {
      int currentKeyState = (isBoostKeyDown(player)?1:0) << 0 | (isAltKeyDown(player)?1:0) << 1 | (isModeKeyPress(player)?1:0) << 2 | (isForwardKeyDown(player)?1:0) << 3 | (isJumpKeyDown(player)?1:0) << 4 | (isSneakKeyDown(player)?1:0) << 5;
      if(currentKeyState != lastKeyState) {
         ClientProxy.sendMyPacket("keyState", currentKeyState);
         lastKeyState = currentKeyState;
         super.processKeyUpdate(player, currentKeyState);
      }

   }

   public static void updatePlayerMove() {
      moveStrafe = 0.0F;
      moveForward = 0.0F;
      if(mc.gameSettings.keyBindForward.pressed) {
         ++moveForward;
      }

      if(mc.gameSettings.keyBindBack.pressed) {
         --moveForward;
      }

      if(mc.gameSettings.keyBindLeft.pressed) {
         ++moveStrafe;
      }

      if(mc.gameSettings.keyBindRight.pressed) {
         --moveStrafe;
      }

      if(mc.gameSettings.keyBindSneak.pressed) {
         moveStrafe = (float)((double)moveStrafe * 0.3D);
         moveForward = (float)((double)moveForward * 0.3D);
      }

   }

}
