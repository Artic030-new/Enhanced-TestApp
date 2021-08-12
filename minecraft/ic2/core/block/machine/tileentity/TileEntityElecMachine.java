package ic2.core.block.machine.tileentity;

import ic2.api.Direction;
import ic2.api.IElectricItem;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySink;
import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.block.machine.tileentity.TileEntityMachine;
import ic2.core.item.ElectricItem;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;

public abstract class TileEntityElecMachine extends TileEntityMachine implements IEnergySink {

   public int energy = 0;
   public int fuelslot;
   public int maxEnergy;
   public int maxInput;
   public int tier = 0;
   public boolean addedToEnergyNet = false;


   public TileEntityElecMachine(int slots, int fuelslot, int maxenergy, int maxinput) {
      super(slots);
      this.fuelslot = fuelslot;
      this.maxEnergy = maxenergy;
      this.maxInput = maxinput;
      this.tier = 1;
   }

   public TileEntityElecMachine(int slots, int fuelslot, int maxenergy, int maxinput, int tier) {
      super(slots);
      this.fuelslot = fuelslot;
      this.maxEnergy = maxenergy;
      this.maxInput = maxinput;
      this.tier = tier;
   }

   public void readFromNBT(NBTTagCompound nbttagcompound) {
      super.readFromNBT(nbttagcompound);
      this.energy = nbttagcompound.getInteger("energy");
   }

   public void writeToNBT(NBTTagCompound nbttagcompound) {
      super.writeToNBT(nbttagcompound);
      nbttagcompound.setInteger("energy", this.energy);
   }

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

   public boolean provideEnergy() {
      boolean ret = false;
      if(super.inventory[this.fuelslot] == null) {
         return false;
      } else {
         int id = super.inventory[this.fuelslot].itemID;
         if(Item.itemsList[id] instanceof IElectricItem) {
            if(!((IElectricItem)Item.itemsList[id]).canProvideEnergy()) {
               return false;
            } else {
               int transfer = ElectricItem.discharge(super.inventory[this.fuelslot], this.maxEnergy - this.energy, this.tier, false, false);
               this.energy += transfer;
               return ret || transfer > 0;
            }
         } else if(id == Item.redstone.itemID) {
            this.energy += this.maxEnergy;
            --super.inventory[this.fuelslot].stackSize;
            if(super.inventory[this.fuelslot].stackSize <= 0) {
               super.inventory[this.fuelslot] = null;
            }

            return true;
         } else if(id == Ic2Items.suBattery.itemID) {
            this.energy += 1000;
            --super.inventory[this.fuelslot].stackSize;
            if(super.inventory[this.fuelslot].stackSize <= 0) {
               super.inventory[this.fuelslot] = null;
            }

            return true;
         } else {
            return ret;
         }
      }
   }

   public boolean isAddedToEnergyNet() {
      return this.addedToEnergyNet;
   }

   public int demandsEnergy() {
      return this.maxEnergy - this.energy;
   }

   public int injectEnergy(Direction directionFrom, int amount) {
      if(amount > this.maxInput) {
         IC2.explodeMachineAt(super.worldObj, super.xCoord, super.yCoord, super.zCoord);
         return 0;
      } else {
         this.energy += amount;
         int re = 0;
         if(this.energy > this.maxEnergy) {
            re = this.energy - this.maxEnergy;
            this.energy = this.maxEnergy;
         }

         return re;
      }
   }

   public int getMaxSafeInput() {
      return this.maxInput;
   }

   public boolean acceptsEnergyFrom(TileEntity emitter, Direction direction) {
      return true;
   }

   public boolean isRedstonePowered() {
      return super.worldObj.isBlockIndirectlyGettingPowered(super.xCoord, super.yCoord, super.zCoord);
   }
}
