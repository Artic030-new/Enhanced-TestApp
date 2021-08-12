package ic2.api;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;

public enum Direction {

   XN("XN", 0, 0),
   XP("XP", 1, 1),
   YN("YN", 2, 2),
   YP("YP", 3, 3),
   ZN("ZN", 4, 4),
   ZP("ZP", 5, 5);
   private int dir;
   private static final Direction[] directions = values();
   // $FF: synthetic field
   private static final Direction[] $VALUES = new Direction[]{XN, XP, YN, YP, ZN, ZP};


   private Direction(String var1, int var2, int dir) {
      this.dir = dir;
   }

   public TileEntity applyToTileEntity(TileEntity tileEntity) {
      int[] coords = new int[]{tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord};
      coords[this.dir / 2] += this.getSign();
      return tileEntity.worldObj != null && tileEntity.worldObj.blockExists(coords[0], coords[1], coords[2])?tileEntity.worldObj.getBlockTileEntity(coords[0], coords[1], coords[2]):null;
   }

   public Direction getInverse() {
      int inverseDir = this.dir - this.getSign();
      Direction[] arr$ = directions;
      int len$ = arr$.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         Direction direction = arr$[i$];
         if(direction.dir == inverseDir) {
            return direction;
         }
      }

      return this;
   }

   public int toSideValue() {
      return (this.dir + 4) % 6;
   }

   private int getSign() {
      return this.dir % 2 * 2 - 1;
   }

   public ForgeDirection toForgeDirection() {
      return ForgeDirection.getOrientation(this.toSideValue());
   }

}
