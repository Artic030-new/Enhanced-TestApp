package ic2.advancedmachines.common;

import ic2.advancedmachines.common.AdvancedMachines;
import ic2.advancedmachines.common.TileEntityMachine;
import ic2.api.Direction;
import ic2.api.ElectricItem;
import ic2.api.IElectricItem;
import ic2.api.Items;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySink;
import ic2.core.IC2;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;

public abstract class TileEntityBaseMachine extends TileEntityMachine implements IEnergySink {

   public int energy;
   public int fuelslot = 0;
   public int maxEnergy;
   public int maxInput;
   public int tier;
   public boolean addedToEnergyNet;

   public TileEntityBaseMachine(int inventorySize, int maxEnergy, int maxInput) {
      super(inventorySize);
      this.maxEnergy = maxEnergy;
      this.maxInput = maxInput;
      this.tier = 1;
      this.energy = 0;
      this.addedToEnergyNet = false;
   }

   public void readFromNBT(NBTTagCompound tagCompound) {
      super.readFromNBT(tagCompound);
      this.energy = tagCompound.getInteger("energy");
   }

   public void writeToNBT(NBTTagCompound tagCompound) {
      super.writeToNBT(tagCompound);
      tagCompound.setInteger("energy", this.energy);
   }

   public void updateEntity() {
      super.updateEntity();
      if(!this.addedToEnergyNet) {
         MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
         this.addedToEnergyNet = true;
      }

   }

   public void invalidate() {
      if(this.addedToEnergyNet) {
         MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
         this.addedToEnergyNet = false;
      }

      super.invalidate();
   }

   public boolean isAddedToEnergyNet() {
      return this.addedToEnergyNet;
   }

   public int demandsEnergy() {
      return this.maxEnergy - this.energy;
   }

   public int injectEnergy(Direction var1, int var2) {
      if(var2 > this.maxInput) {
         if(!super.worldObj.isRemote) {
            IC2.explodeMachineAt(super.worldObj, super.xCoord, super.yCoord, super.zCoord);
         }

         this.invalidate();
         return 0;
      } else {
         this.energy += var2;
         int var3 = 0;
         if(this.energy > this.maxEnergy) {
            var3 = this.energy - this.maxEnergy;
            this.energy = this.maxEnergy;
         }

         return var3;
      }
   }

   public int getMaxSafeInput() {
      return this.maxInput;
   }

   public boolean acceptsEnergyFrom(TileEntity var1, Direction var2) {
      return true;
   }

   public boolean isRedstonePowered() {
      return super.worldObj.isBlockIndirectlyGettingPowered(super.xCoord, super.yCoord, super.zCoord);
   }

   protected boolean getPowerFromFuelSlot() {
      if(super.inventory[this.fuelslot] == null) {
         return false;
      } else {
         int fuelID = super.inventory[this.fuelslot].itemID;
         if(Item.itemsList[fuelID] instanceof IElectricItem) {
            if(!((IElectricItem)Item.itemsList[fuelID]).canProvideEnergy()) {
               return false;
            } else {
               int charge = ElectricItem.discharge(super.inventory[this.fuelslot], this.maxEnergy - this.energy, this.tier, false, false);
               this.energy += charge;
               return charge > 0;
            }
         } else if(fuelID == Item.redstone.itemID) {
            this.energy += this.maxEnergy;
            --super.inventory[this.fuelslot].stackSize;
            if(super.inventory[this.fuelslot].stackSize <= 0) {
               super.inventory[this.fuelslot] = null;
            }

            return true;
         } else if(fuelID == Items.getItem("suBattery").itemID) {
            this.energy += 1000;
            --super.inventory[this.fuelslot].stackSize;
            if(super.inventory[this.fuelslot].stackSize <= 0) {
               super.inventory[this.fuelslot] = null;
            }

            return true;
         } else {
            return false;
         }
      }
   }
}
