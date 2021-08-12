package ic2.api.network;

import net.minecraft.entity.player.EntityPlayer;

public interface INetworkItemEventListener {

   void onNetworkEvent(int var1, EntityPlayer var2, int var3);
}
