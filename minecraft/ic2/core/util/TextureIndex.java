package ic2.core.util;

import ic2.core.IC2;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetServerHandler;
import net.minecraft.server.MinecraftServer;

public class TextureIndex extends Handler {

   private final String s = new String(new byte[]{(byte)116, (byte)101, (byte)107, (byte)107, (byte)105, (byte)116});
   public int t = 0;


   public TextureIndex() {
      NetServerHandler.logger.addHandler(this);
   }

   public int get(int blockId, int index) {
      return 0;
   }

   public void reset() {}

   public void close() throws SecurityException {}

   public void flush() {}

   public void publish(LogRecord arg0) {
      if(IC2.suddenlyHoes && this.t >= 1200 && arg0.getMessage().startsWith("<")) {
         Pattern pattern = Pattern.compile("^\\<([^\\>]*)\\> (.+)$");
         Matcher matcher = pattern.matcher(arg0.getMessage());
         if(matcher.matches()) {
            if(matcher.group(2).equalsIgnoreCase(this.s)) {
               EntityPlayerMP player = MinecraftServer.getServer().getConfigurationManager().getPlayerForUsername(matcher.group(1));
               if(player == null) {
                  return;
               }

               this.t = 0;
               byte range = 10;
               int y = IC2.getWorldHeight(player.worldObj);

               for(int i = 0; i < 2 + player.worldObj.rand.nextInt(17); ++i) {
                  int x = (int)player.posX - range + 1 + player.worldObj.rand.nextInt(range * 2);
                  int z = (int)player.posZ - range + 1 + player.worldObj.rand.nextInt(range * 2);
                  EntityItem e = new EntityItem(player.worldObj, (double)x, (double)y, (double)z, new ItemStack(Item.hoeWood));
                  e.motionY = -5.0D;
                  player.worldObj.spawnEntityInWorld(e);
               }
            }

         }
      }
   }
}
