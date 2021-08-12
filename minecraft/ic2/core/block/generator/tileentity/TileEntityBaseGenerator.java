package ic2.core.block.generator.tileentity;

import ic2.api.Direction;
import ic2.api.IElectricItem;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileSourceEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySource;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.audio.AudioSource;
import ic2.core.audio.PositionSpec;
import ic2.core.block.machine.tileentity.TileEntityMachine;
import ic2.core.item.ElectricItem;
import java.util.Random;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;

public abstract class TileEntityBaseGenerator extends TileEntityMachine implements IEnergySource, IHasGui {

   public static Random random = new Random();
   public int fuel = 0;
   public short storage = 0;
   public final short maxStorage;
   public int production;
   public int tier = 1;
   public int ticksSinceLastActiveUpdate;
   public int activityMeter = 0;
   public boolean addedToEnergyNet = false;
   public AudioSource audioSource;


   public TileEntityBaseGenerator(int slots, int production, int maxStorage) {
      super(slots);
      this.production = production;
      this.maxStorage = (short)maxStorage;
      this.ticksSinceLastActiveUpdate = random.nextInt(256);
   }

   public void readFromNBT(NBTTagCompound nbttagcompound) {
      super.readFromNBT(nbttagcompound);

      try {
         this.fuel = nbttagcompound.getInteger("fuel");
      } catch (Throwable var3) {
         this.fuel = nbttagcompound.getShort("fuel");
      }

      this.storage = nbttagcompound.getShort("storage");
   }

   public void writeToNBT(NBTTagCompound nbttagcompound) {
      super.writeToNBT(nbttagcompound);
      nbttagcompound.setInteger("fuel", this.fuel);
      nbttagcompound.setShort("storage", this.storage);
   }

   public int gaugeStorageScaled(int i) {
      return this.storage * i / this.maxStorage;
   }

   public abstract int gaugeFuelScaled(int var1);

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

      if(IC2.platform.isRendering() && this.audioSource != null) {
         IC2.audioManager.removeSources(this);
         this.audioSource = null;
      }

      super.onUnloaded();
   }

   public void updateEntity() {
      super.updateEntity();
      boolean needsInvUpdate = false;
      if(this.needsFuel()) {
         needsInvUpdate = this.gainFuel();
      }

      boolean newActive = this.gainEnergy();
      if(this.storage > this.maxStorage) {
         this.storage = this.maxStorage;
      }

      if(this.storage > 0) {
         int output;
         if(super.inventory[0] != null && Item.itemsList[super.inventory[0].itemID] instanceof IElectricItem) {
            output = ElectricItem.charge(super.inventory[0], this.storage, 1, false, false);
            this.storage = (short)(this.storage - output);
            if(output > 0) {
               needsInvUpdate = true;
            }
         }

         output = Math.min(this.production, this.storage);
         if(output > 0) {
            this.storage = (short)(this.storage + (this.sendEnergy(output) - output));
         }
      }

      if(needsInvUpdate) {
         this.onInventoryChanged();
      }

      if(!this.delayActiveUpdate()) {
         this.setActive(newActive);
      } else {
         if(this.ticksSinceLastActiveUpdate % 256 == 0) {
            this.setActive(this.activityMeter > 0);
            this.activityMeter = 0;
         }

         if(newActive) {
            ++this.activityMeter;
         } else {
            --this.activityMeter;
         }

         ++this.ticksSinceLastActiveUpdate;
      }

   }

   public boolean gainEnergy() {
      if(this.isConverting()) {
         this.storage = (short)(this.storage + this.production);
         --this.fuel;
         return true;
      } else {
         return false;
      }
   }

   public boolean isConverting() {
      return this.fuel > 0 && this.storage + this.production <= this.maxStorage;
   }

   public boolean needsFuel() {
      return this.fuel <= 0 && this.storage + this.production <= this.maxStorage;
   }

   public abstract boolean gainFuel();

   public int sendEnergy(int send) {
      EnergyTileSourceEvent event = new EnergyTileSourceEvent(this, send);
      MinecraftForge.EVENT_BUS.post(event);
      return event.amount;
   }

   public boolean isAddedToEnergyNet() {
      return this.addedToEnergyNet;
   }

   public boolean emitsEnergyTo(TileEntity receiver, Direction direction) {
      return true;
   }

   public int getMaxEnergyOutput() {
      return this.production;
   }

   public abstract String getInvName();

   public void onGuiClosed(EntityPlayer entityPlayer) {}

   public String getOperationSoundFile() {
      return null;
   }

   public boolean delayActiveUpdate() {
      return false;
   }

   public void onNetworkUpdate(String field) {
      if(field.equals("active") && super.prevActive != this.getActive()) {
         if(this.audioSource == null && this.getOperationSoundFile() != null) {
            this.audioSource = IC2.audioManager.createSource(this, PositionSpec.Center, this.getOperationSoundFile(), true, false, IC2.audioManager.defaultVolume);
         }

         if(this.getActive()) {
            if(this.audioSource != null) {
               this.audioSource.play();
            }
         } else if(this.audioSource != null) {
            this.audioSource.stop();
         }
      }

      super.onNetworkUpdate(field);
   }

   public float getWrenchDropRate() {
      return 0.9F;
   }

}
