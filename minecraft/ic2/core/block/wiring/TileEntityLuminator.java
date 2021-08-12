package ic2.core.block.wiring;

import ic2.api.Direction;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySink;
import ic2.core.ExplosionIC2;
import ic2.core.IC2;
import ic2.core.ITickCallback;
import ic2.core.Ic2Items;
import ic2.core.block.wiring.TileEntityCable;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class TileEntityLuminator extends TileEntity implements IEnergySink {

   public int energy = 0;
   public int ticker = -1;
   public boolean ignoreBlockStay = false;
   public int maxInput = 32;
   public boolean addedToEnergyNet = false;
   private boolean loaded = false;


   public void readFromNBT(NBTTagCompound nbttagcompound) {
      super.readFromNBT(nbttagcompound);
      this.energy = nbttagcompound.getShort("energy");
   }

   public void writeToNBT(NBTTagCompound nbttagcompound) {
      super.writeToNBT(nbttagcompound);
      nbttagcompound.setShort("energy", (short)this.energy);
   }

   public void validate() {
      super.validate();
      if(!this.loaded) {
         if(!this.isInvalid() && super.worldObj != null) {
            if(IC2.platform.isSimulating()) {
               IC2.addSingleTickCallback(super.worldObj, new ITickCallback() {
                  public void tickCallback(World world) {
                     TileEntityLuminator.this.onLoaded();
                  }
               });
            } else {
               this.onLoaded();
            }
         } else {
            IC2.log.warning(this + " (" + super.xCoord + "," + super.yCoord + "," + super.zCoord + ") was not added, isInvalid=" + this.isInvalid() + ", worldObj=" + super.worldObj);
         }
      }

   }

   public void invalidate() {
      super.invalidate();
      if(this.loaded) {
         this.onUnloaded();
      }

   }

   public void onChunkUnload() {
      super.onChunkUnload();
      if(this.loaded) {
         this.onUnloaded();
      }

   }

   public void onLoaded() {
      if(IC2.platform.isSimulating() && !this.addedToEnergyNet) {
         MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
         this.addedToEnergyNet = true;
      }

      this.loaded = true;
   }

   public void onUnloaded() {
      if(IC2.platform.isSimulating() && this.addedToEnergyNet) {
         MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
         this.addedToEnergyNet = false;
      }

   }

   public void updateEntity() {
      if(IC2.platform.isSimulating()) {
         ++this.ticker;
         if(this.ticker % 4 == 0) {
            --this.energy;
            if(this.energy <= 0) {
               super.worldObj.setBlockAndMetadataWithNotify(super.xCoord, super.yCoord, super.zCoord, Ic2Items.luminator.itemID, super.worldObj.getBlockMetadata(super.xCoord, super.yCoord, super.zCoord));
            }
         }
      }

   }

   public boolean isAddedToEnergyNet() {
      return this.addedToEnergyNet;
   }

   public boolean acceptsEnergyFrom(TileEntity emitter, Direction direction) {
      return emitter instanceof TileEntityCable;
   }

   public int demandsEnergy() {
      return this.getMaxEnergy() - this.energy;
   }

   public int injectEnergy(Direction directionFrom, int amount) {
      if(amount > this.maxInput) {
         this.poof();
         return 0;
      } else if(amount <= 0) {
         return 0;
      } else if(super.worldObj.getBlockId(super.xCoord, super.yCoord, super.zCoord) == Ic2Items.luminator.itemID) {
         super.worldObj.setBlockAndMetadataWithNotify(super.xCoord, super.yCoord, super.zCoord, Ic2Items.activeLuminator.itemID, super.worldObj.getBlockMetadata(super.xCoord, super.yCoord, super.zCoord));
         TileEntityLuminator need1 = (TileEntityLuminator)super.worldObj.getBlockTileEntity(super.xCoord, super.yCoord, super.zCoord);
         return need1.injectEnergy(directionFrom, amount);
      } else {
         int need = amount;
         if(this.energy + amount >= this.getMaxEnergy() + this.maxInput) {
            need = this.getMaxEnergy() + this.maxInput - this.energy - 1;
         }

         this.energy += need;
         return amount - need;
      }
   }

   public int getMaxSafeInput() {
      return this.maxInput;
   }

   public int getMaxEnergy() {
      return 10000;
   }

   public void poof() {
      super.worldObj.setBlockWithNotify(super.xCoord, super.yCoord, super.zCoord, 0);
      ExplosionIC2 explosion = new ExplosionIC2(super.worldObj, (Entity)null, 0.5D + (double)super.xCoord, 0.5D + (double)super.yCoord, 0.5D + (double)super.zCoord, 0.5F, 0.85F, 2.0F);
      explosion.doExplosion();
   }

   public boolean canCableConnectFrom(int x, int y, int z) {
      int facing = super.worldObj.getBlockMetadata(super.xCoord, super.yCoord, super.zCoord);
      switch(facing) {
      case 0:
         return x == super.xCoord && y == super.yCoord + 1 && z == super.zCoord;
      case 1:
         return x == super.xCoord && y == super.yCoord - 1 && z == super.zCoord;
      case 2:
         return x == super.xCoord && y == super.yCoord && z == super.zCoord + 1;
      case 3:
         return x == super.xCoord && y == super.yCoord && z == super.zCoord - 1;
      case 4:
         return x == super.xCoord + 1 && y == super.yCoord && z == super.zCoord;
      case 5:
         return x == super.xCoord - 1 && y == super.yCoord && z == super.zCoord;
      default:
         return false;
      }
   }
}
