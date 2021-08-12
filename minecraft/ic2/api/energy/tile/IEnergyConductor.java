package ic2.api.energy.tile;

import ic2.api.energy.tile.IEnergyAcceptor;
import ic2.api.energy.tile.IEnergyEmitter;

public interface IEnergyConductor extends IEnergyAcceptor, IEnergyEmitter {

   double getConductionLoss();

   int getInsulationEnergyAbsorption();

   int getInsulationBreakdownEnergy();

   int getConductorBreakdownEnergy();

   void removeInsulation();

   void removeConductor();
}
