package ic2.core.util;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.entity.player.EntityPlayer;

public class Keyboard {

   private Map altKeyState = new HashMap();
   private Map boostKeyState = new HashMap();
   private Map forwardKeyState = new HashMap();
   private Map modeSwitchKeyState = new HashMap();
   private Map jumpKeyState = new HashMap();
   private Map sideinventoryKeyState = new HashMap();


   public boolean isAltKeyDown(EntityPlayer player) {
      return this.altKeyState.containsKey(player)?((Boolean)this.altKeyState.get(player)).booleanValue():false;
   }

   public boolean isBoostKeyDown(EntityPlayer player) {
      return this.boostKeyState.containsKey(player)?((Boolean)this.boostKeyState.get(player)).booleanValue():false;
   }

   public boolean isForwardKeyDown(EntityPlayer player) {
      return this.forwardKeyState.containsKey(player)?((Boolean)this.forwardKeyState.get(player)).booleanValue():false;
   }

   public boolean isJumpKeyDown(EntityPlayer player) {
      return this.jumpKeyState.containsKey(player)?((Boolean)this.jumpKeyState.get(player)).booleanValue():false;
   }

   public boolean isModeSwitchKeyDown(EntityPlayer player) {
      return this.modeSwitchKeyState.containsKey(player)?((Boolean)this.modeSwitchKeyState.get(player)).booleanValue():false;
   }

   public boolean isSideinventoryKeyDown(EntityPlayer player) {
      return this.sideinventoryKeyState.containsKey(player)?((Boolean)this.sideinventoryKeyState.get(player)).booleanValue():false;
   }

   public boolean isSneakKeyDown(EntityPlayer player) {
      return player.isSneaking();
   }

   public void sendKeyUpdate() {}

   public void processKeyUpdate(EntityPlayer player, int keyState) {
      this.altKeyState.put(player, Boolean.valueOf((keyState & 1) != 0));
      this.boostKeyState.put(player, Boolean.valueOf((keyState & 2) != 0));
      this.forwardKeyState.put(player, Boolean.valueOf((keyState & 4) != 0));
      this.modeSwitchKeyState.put(player, Boolean.valueOf((keyState & 8) != 0));
      this.jumpKeyState.put(player, Boolean.valueOf((keyState & 16) != 0));
      this.sideinventoryKeyState.put(player, Boolean.valueOf((keyState & 32) != 0));
   }

   public void removePlayerReferences(EntityPlayer player) {
      this.altKeyState.remove(player);
      this.boostKeyState.remove(player);
      this.forwardKeyState.remove(player);
      this.modeSwitchKeyState.remove(player);
      this.jumpKeyState.remove(player);
      this.sideinventoryKeyState.remove(player);
   }
}
