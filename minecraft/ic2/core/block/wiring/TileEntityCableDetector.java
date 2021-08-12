package ic2.core.block.wiring;

import ic2.core.EnergyNet;
import ic2.core.IC2;
import ic2.core.block.wiring.TileEntityCable;
import net.minecraft.nbt.NBTTagCompound;

public class TileEntityCableDetector extends TileEntityCable {

   public long lastValue = -1L;
   public static int tickRate = 20;
   public int ticker = 0;


   public TileEntityCableDetector(short meta) {
      super(meta);
   }

   public TileEntityCableDetector() {}

   public void readFromNBT(NBTTagCompound nbttagcompound) {
      super.readFromNBT(nbttagcompound);
      this.setActiveWithoutNotify(nbttagcompound.getBoolean("active"));
   }

   public void writeToNBT(NBTTagCompound nbttagcompound) {
      super.writeToNBT(nbttagcompound);
      nbttagcompound.setBoolean("active", this.getActive());
   }

   public boolean canUpdate() {
      return IC2.platform.isSimulating();
   }

   public void updateEntity() {
      super.updateEntity();
      if(this.ticker++ % tickRate == 0) {
         long newValue = EnergyNet.getForWorld(super.worldObj).getTotalEnergyEmitted(this);
         if(this.lastValue != -1L) {
            if(newValue > this.lastValue) {
               if(!this.getActive()) {
                  this.setActive(true);
                  super.worldObj.notifyBlocksOfNeighborChange(super.xCoord, super.yCoord, super.zCoord, super.worldObj.getBlockId(super.xCoord, super.yCoord, super.zCoord));
               }
            } else if(this.getActive()) {
               this.setActive(false);
               super.worldObj.notifyBlocksOfNeighborChange(super.xCoord, super.yCoord, super.zCoord, super.worldObj.getBlockId(super.xCoord, super.yCoord, super.zCoord));
            }
         }

         this.lastValue = newValue;
      }

   }

}
