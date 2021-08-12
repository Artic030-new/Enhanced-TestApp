package gravisuite.audio;

import gravisuite.audio.AudioManager;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class AudioPosition {

   public World world;
   public float x;
   public float y;
   public float z;

   public static AudioPosition getFrom(Object var0, AudioManager.PositionSpec var1) {
      if(var0 instanceof AudioPosition) {
         return (AudioPosition)var0;
      } else if(var0 instanceof Entity) {
         Entity var21 = (Entity)var0;
         return new AudioPosition(var21.worldObj, (float)var21.posX, (float)var21.posY, (float)var21.posZ);
      } else if(var0 instanceof TileEntity) {
         TileEntity var2 = (TileEntity)var0;
         return new AudioPosition(var2.worldObj, (float)var2.xCoord + 0.5F, (float)var2.yCoord + 0.5F, (float)var2.zCoord + 0.5F);
      } else {
         return null;
      }
   }

   public AudioPosition(World var1, float var2, float var3, float var4) {
      this.world = var1;
      this.x = var2;
      this.y = var3;
      this.z = var4;
   }
}
