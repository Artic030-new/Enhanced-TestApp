package ic2.api.energy.tile;

import ic2.api.energy.tile.IEnergyEmitter;

public interface IEnergySource extends IEnergyEmitter {

   int getMaxEnergyOutput();
}
