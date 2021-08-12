package ic2.core.block.wiring;

import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.core.IC2;
import ic2.core.block.wiring.TileEntityCable;
import net.minecraftforge.common.MinecraftForge;

public class TileEntityCableSplitter extends TileEntityCable {

   public static final int tickRate = 20;
   public int ticksUntilNextUpdate = 0;


   public TileEntityCableSplitter(short type) {
      super(type);
   }

   public TileEntityCableSplitter() {}

   public boolean canUpdate() {
      return IC2.platform.isSimulating();
   }

   public void updateEntity() {
      if(this.ticksUntilNextUpdate == 0) {
         this.ticksUntilNextUpdate = 20;
         if(super.worldObj.isBlockGettingPowered(super.xCoord, super.yCoord, super.zCoord) == super.addedToEnergyNet) {
            if(super.addedToEnergyNet) {
               MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
               super.addedToEnergyNet = false;
            } else {
               MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
               super.addedToEnergyNet = true;
            }
         }

         this.setActive(super.addedToEnergyNet);
      }

      --this.ticksUntilNextUpdate;
   }
}
