package ic2.core.block.generator.tileentity;

import ic2.api.Direction;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileSourceEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySource;
import ic2.core.IC2;
import ic2.core.block.generator.tileentity.TileEntityNuclearReactor;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;

public class TileEntityNuclearReactorElectric extends TileEntityNuclearReactor implements IEnergySource {

   public boolean addedToEnergyNet = false;


   public void onLoaded() {
      super.onLoaded();
      if(IC2.platform.isSimulating()) {
         MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
         this.addedToEnergyNet = true;
      }

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
      return 1512 * IC2.energyGeneratorNuclear;
   }

   public int sendEnergy(int send) {
      EnergyTileSourceEvent event = new EnergyTileSourceEvent(this, send * IC2.energyGeneratorNuclear);
      MinecraftForge.EVENT_BUS.post(event);
      return event.amount;
   }
}
