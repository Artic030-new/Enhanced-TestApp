package ic2.core.block.wiring;

import ic2.api.Direction;
import ic2.api.IElectricItem;
import ic2.api.IEnergyStorage;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileSourceEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import ic2.api.network.INetworkClientTileEntityEventListener;
import ic2.core.ContainerIC2;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.Ic2Items;
import ic2.core.block.machine.tileentity.TileEntityMachine;
import ic2.core.block.wiring.ContainerElectricBlock;
import ic2.core.item.ElectricItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StringTranslate;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.ISidedInventory;
import net.minecraftforge.common.MinecraftForge;

public abstract class TileEntityElectricBlock extends TileEntityMachine implements IEnergySink, IEnergySource, IHasGui, ISidedInventory, INetworkClientTileEntityEventListener, IEnergyStorage {

   public StringTranslate translate;
   public int tier;
   public int output;
   public int maxStorage;
   public int energy = 0;
   public byte redstoneMode = 0;
   public static byte redstoneModes = 6;
   private boolean isEmittingRedstone = false;
   private int redstoneUpdateInhibit = 5;
   public boolean addedToEnergyNet = false;


   public TileEntityElectricBlock(int tierc, int outputc, int maxStoragec) {
      super(2);
      this.tier = tierc;
      this.output = outputc;
      this.maxStorage = maxStoragec;
      this.translate = StringTranslate.getInstance();
   }

   public String getNameByTier() {
      switch(this.tier) {
      case 1:
         return this.translate.translateKey("blockBatBox.name");
      case 2:
         return this.translate.translateKey("blockMFE.name");
      case 3:
         return this.translate.translateKey("blockMFSU.name");
      default:
         return null;
      }
   }

   public float getChargeLevel() {
      float ret = (float)this.energy / (float)this.maxStorage;
      if(ret > 1.0F) {
         ret = 1.0F;
      }

      return ret;
   }

   public void readFromNBT(NBTTagCompound nbttagcompound) {
      super.readFromNBT(nbttagcompound);
      this.setActiveWithoutNotify(nbttagcompound.getBoolean("active"));
      this.energy = nbttagcompound.getInteger("energy");
      if(this.maxStorage > Integer.MAX_VALUE) {
         this.energy *= 10;
      }

      this.redstoneMode = nbttagcompound.getByte("redstoneMode");
   }

   public void writeToNBT(NBTTagCompound nbttagcompound) {
      super.writeToNBT(nbttagcompound);
      int write = this.energy;
      if(this.maxStorage > Integer.MAX_VALUE) {
         write /= 10;
      }

      nbttagcompound.setInteger("energy", write);
      nbttagcompound.setBoolean("active", this.getActive());
      nbttagcompound.setByte("redstoneMode", this.redstoneMode);
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
      boolean needsInvUpdate = false;
      int shouldEmitRedstone;
      if(this.energy > 0 && super.inventory[0] != null && Item.itemsList[super.inventory[0].itemID] instanceof IElectricItem) {
         shouldEmitRedstone = ElectricItem.charge(super.inventory[0], this.energy, this.tier, false, false);
         this.energy -= shouldEmitRedstone;
         needsInvUpdate = shouldEmitRedstone > 0;
      }

      if(this.demandsEnergy() > 0 && super.inventory[1] != null) {
         if(Item.itemsList[super.inventory[1].itemID] instanceof IElectricItem) {
            IElectricItem var4 = (IElectricItem)Item.itemsList[super.inventory[1].itemID];
            if(var4.canProvideEnergy()) {
               int gain = ElectricItem.discharge(super.inventory[1], this.maxStorage - this.energy, this.tier, false, false);
               this.energy += gain;
               needsInvUpdate = gain > 0;
            }
         } else {
            shouldEmitRedstone = super.inventory[1].itemID;
            short var6 = 0;
            if(shouldEmitRedstone == Item.redstone.itemID) {
               var6 = 500;
            }

            if(shouldEmitRedstone == Ic2Items.suBattery.itemID) {
               var6 = 1000;
            }

            if(var6 > 0 && var6 <= this.maxStorage - this.energy) {
               --super.inventory[1].stackSize;
               if(super.inventory[1].stackSize <= 0) {
                  super.inventory[1] = null;
               }

               this.energy += var6;
            }
         }
      }

      if(this.energy >= this.output && (this.redstoneMode != 4 || !super.worldObj.isBlockGettingPowered(super.xCoord, super.yCoord, super.zCoord)) && (this.redstoneMode != 5 || !super.worldObj.isBlockGettingPowered(super.xCoord, super.yCoord, super.zCoord) || this.energy >= this.maxStorage)) {
         EnergyTileSourceEvent var5 = new EnergyTileSourceEvent(this, this.output);
         MinecraftForge.EVENT_BUS.post(var5);
         this.energy -= this.output - var5.amount;
      }

      boolean var7 = this.shouldEmitRedstone();
      if(var7 != this.isEmittingRedstone) {
         this.isEmittingRedstone = var7;
         this.setActive(this.isEmittingRedstone);
         super.worldObj.notifyBlocksOfNeighborChange(super.xCoord, super.yCoord, super.zCoord, super.worldObj.getBlockId(super.xCoord, super.yCoord, super.zCoord));
      }

      if(needsInvUpdate) {
         this.onInventoryChanged();
      }

   }

   public boolean isAddedToEnergyNet() {
      return this.addedToEnergyNet;
   }

   public boolean acceptsEnergyFrom(TileEntity emitter, Direction direction) {
      return !this.facingMatchesDirection(direction);
   }

   public boolean emitsEnergyTo(TileEntity receiver, Direction direction) {
      return this.facingMatchesDirection(direction);
   }

   public boolean facingMatchesDirection(Direction direction) {
      return direction.toSideValue() == this.getFacing();
   }

   public int getMaxEnergyOutput() {
      return this.output;
   }

   public int demandsEnergy() {
      return this.maxStorage - this.energy;
   }

   public int injectEnergy(Direction directionFrom, int amount) {
      if(amount > this.output) {
         IC2.explodeMachineAt(super.worldObj, super.xCoord, super.yCoord, super.zCoord);
         return 0;
      } else {
         int need = amount;
         if(this.energy + amount >= this.maxStorage + this.output) {
            need = this.maxStorage + this.output - this.energy - 1;
         }

         this.energy += need;
         return amount - need;
      }
   }

   public int getMaxSafeInput() {
      return this.output;
   }

   public ContainerIC2 getGuiContainer(EntityPlayer entityPlayer) {
      return new ContainerElectricBlock(entityPlayer, this);
   }

   public String getGuiClassName(EntityPlayer entityPlayer) {
      return "block.wiring.GuiElectricBlock";
   }

   public void onGuiClosed(EntityPlayer entityPlayer) {}

   public boolean wrenchCanSetFacing(EntityPlayer entityPlayer, int side) {
      return this.getFacing() != side;
   }

   public void setFacing(short facing) {
      if(this.addedToEnergyNet) {
         MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
      }

      this.addedToEnergyNet = false;
      super.setFacing(facing);
      MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
      this.addedToEnergyNet = true;
   }

   public boolean isEmittingRedstone() {
      return this.isEmittingRedstone;
   }

   public boolean shouldEmitRedstone() {
      boolean shouldEmitRedstone = false;
      switch(this.redstoneMode) {
      case 1:
         shouldEmitRedstone = this.energy >= this.maxStorage;
         break;
      case 2:
         shouldEmitRedstone = this.energy > this.output && this.energy < this.maxStorage;
         break;
      case 3:
         shouldEmitRedstone = this.energy < this.output;
      }

      if(this.isEmittingRedstone != shouldEmitRedstone && this.redstoneUpdateInhibit != 0) {
         --this.redstoneUpdateInhibit;
         return this.isEmittingRedstone;
      } else {
         this.redstoneUpdateInhibit = 5;
         return shouldEmitRedstone;
      }
   }

   public int getStartInventorySide(ForgeDirection side) {
      switch(TileEntityElectricBlock.NamelessClass1666279212.$SwitchMap$net$minecraftforge$common$ForgeDirection[side.ordinal()]) {
      case 1:
         return 1;
      default:
         return 0;
      }
   }

   public int getSizeInventorySide(ForgeDirection side) {
      return 1;
   }

   public void onNetworkEvent(EntityPlayer player, int event) {
      ++this.redstoneMode;
      if(this.redstoneMode >= redstoneModes) {
         this.redstoneMode = 0;
      }

      switch(this.redstoneMode) {
      case 0:
         IC2.platform.messagePlayer(player, "Redstone Behavior: Nothing");
         break;
      case 1:
         IC2.platform.messagePlayer(player, "Redstone Behavior: Emit if full");
         break;
      case 2:
         IC2.platform.messagePlayer(player, "Redstone Behavior: Emit if partially filled");
         break;
      case 3:
         IC2.platform.messagePlayer(player, "Redstone Behavior: Emit if empty");
         break;
      case 4:
         IC2.platform.messagePlayer(player, "Redstone Behavior: Do not output energy");
         break;
      case 5:
         IC2.platform.messagePlayer(player, "Redstone Behavior: Do not output energy unless full");
      }

   }

   public int getStored() {
      return this.energy;
   }

   public int getCapacity() {
      return this.maxStorage;
   }

   public int getOutput() {
      return this.output;
   }

   public void setStored(int energy) {
      this.energy = energy;
   }

   public int addEnergy(int amount) {
      this.energy += amount;
      return amount;
   }

   public boolean isTeleporterCompatible(Direction side) {
      return true;
   }


   // $FF: synthetic class
   static class NamelessClass1666279212 {

      // $FF: synthetic field
      static final int[] $SwitchMap$net$minecraftforge$common$ForgeDirection = new int[ForgeDirection.values().length];


      static {
         try {
            $SwitchMap$net$minecraftforge$common$ForgeDirection[ForgeDirection.DOWN.ordinal()] = 1;
         } catch (NoSuchFieldError var1) {
            ;
         }

      }
   }
}
