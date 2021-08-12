package ic2.core.block.generator.tileentity;

import ic2.api.Direction;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileSourceEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySource;
import ic2.core.IC2;
import ic2.core.block.generator.tileentity.TileEntityReactorChamber;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;

public class TileEntityReactorChamberElectric extends TileEntityReactorChamber implements IEnergySource {

   public boolean addedToEnergyNet = false;


   public void onLoaded() {
      if(IC2.platform.isSimulating() && !this.addedToEnergyNet) {
         MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
         this.addedToEnergyNet = true;
      }

      super.onLoaded();
   }

   public void onUnloaded() {
      if(IC2.platform.isSimulating() && this.addedToEnergyNet) {
         MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
         this.addedToEnergyNet = false;
      }

      super.onUnloaded();
   }

   public boolean isAddedToEnergyNet() {
      return this.addedToEnergyNet;
   }

   public boolean emitsEnergyTo(TileEntity receiver, Direction direction) {
      return true;
   }

   public int getMaxEnergyOutput() {
      return 240 * IC2.energyGeneratorNuclear;
   }

   public int sendEnergy(int send) {
      EnergyTileSourceEvent event = new EnergyTileSourceEvent(this, send);
      MinecraftForge.EVENT_BUS.post(event);
      return event.amount;
   }
}
