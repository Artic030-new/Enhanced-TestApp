package ic2.core.audio;

import ic2.core.audio.PositionSpec;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class AudioPosition {

   public World world;
   public float x;
   public float y;
   public float z;


   public static AudioPosition getFrom(Object obj, PositionSpec positionSpec) {
      if(obj instanceof AudioPosition) {
         return (AudioPosition)obj;
      } else if(obj instanceof Entity) {
         Entity te1 = (Entity)obj;
         return new AudioPosition(te1.worldObj, (float)te1.posX, (float)te1.posY, (float)te1.posZ);
      } else if(obj instanceof TileEntity) {
         TileEntity te = (TileEntity)obj;
         return new AudioPosition(te.worldObj, (float)te.xCoord + 0.5F, (float)te.yCoord + 0.5F, (float)te.zCoord + 0.5F);
      } else {
         return null;
      }
   }

   public AudioPosition(World world, float x, float y, float z) {
      this.world = world;
      this.x = x;
      this.y = y;
      this.z = z;
   }
}
