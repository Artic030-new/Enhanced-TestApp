package ic2.core.block.machine.tileentity;

import ic2.api.Direction;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySink;
import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.block.TileEntityBlock;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;

public class TileEntityMagnetizer extends TileEntityBlock implements IEnergySink {

   public int energy = 0;
   public int ticker = 0;
   public int maxEnergy = 100;
   public int maxInput = 32;
   public boolean addedToEnergyNet = false;


   public void readFromNBT(NBTTagCompound nbttagcompound) {
      super.readFromNBT(nbttagcompound);
      this.energy = nbttagcompound.getShort("energy");
   }

   public void writeToNBT(NBTTagCompound nbttagcompound) {
      super.writeToNBT(nbttagcompound);
      nbttagcompound.setShort("energy", (short)this.energy);
   }

   public boolean canUpdate() {
      return IC2.platform.isSimulating();
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

   public void updateEntity() {
      super.updateEntity();
      if(this.ticker-- <= 0) {
         boolean change = false;

         int y;
         int need;
         for(y = super.yCoord - 1; y > 0 && y >= super.yCoord - 20 && this.energy > 0 && super.worldObj.getBlockId(super.xCoord, y, super.zCoord) == Ic2Items.ironFence.itemID; --y) {
            need = 15 - super.worldObj.getBlockMetadata(super.xCoord, y, super.zCoord);
            if(need > 0) {
               change = true;
               if(need > this.energy) {
                  this.energy = need;
               }

               super.worldObj.setBlockMetadataWithNotify(super.xCoord, y, super.zCoord, super.worldObj.getBlockMetadata(super.xCoord, y, super.zCoord) + need);
               IC2.network.announceBlockUpdate(super.worldObj, super.xCoord, y, super.zCoord);
               this.energy -= need;
            }
         }

         for(y = super.yCoord + 1; y < IC2.getWorldHeight(super.worldObj) && y <= super.yCoord + 20 && this.energy > 0 && super.worldObj.getBlockId(super.xCoord, y, super.zCoord) == Ic2Items.ironFence.itemID; ++y) {
            need = 15 - super.worldObj.getBlockMetadata(super.xCoord, y, super.zCoord);
            if(need > 0) {
               change = true;
               if(need > this.energy) {
                  this.energy = need;
               }

               super.worldObj.setBlockMetadataWithNotify(super.xCoord, y, super.zCoord, super.worldObj.getBlockMetadata(super.xCoord, y, super.zCoord) + need);
               IC2.network.announceBlockUpdate(super.worldObj, super.xCoord, y, super.zCoord);
               this.energy -= need;
            }
         }

         if(!change) {
            this.ticker = 10;
         }

      }
   }

   public boolean isAddedToEnergyNet() {
      return this.addedToEnergyNet;
   }

   public boolean acceptsEnergyFrom(TileEntity emitter, Direction direction) {
      return true;
   }

   public int demandsEnergy() {
      return this.maxEnergy - this.energy;
   }

   public int injectEnergy(Direction directionFrom, int amount) {
      if(amount > this.maxInput) {
         IC2.explodeMachineAt(super.worldObj, super.xCoord, super.yCoord, super.zCoord);
         return 0;
      } else {
         int need = amount;
         if(this.energy + amount >= this.maxEnergy + this.maxInput) {
            need = this.maxEnergy + this.maxInput - this.energy - 1;
         }

         this.energy += need;
         return amount - need;
      }
   }

   public int getMaxSafeInput() {
      return this.maxInput;
   }
}
