package ic2.api;

import ic2.api.Direction;

public interface IEnergyStorage {

   int getStored();

   void setStored(int var1);

   int addEnergy(int var1);

   int getCapacity();

   int getOutput();

   boolean isTeleporterCompatible(Direction var1);
}
