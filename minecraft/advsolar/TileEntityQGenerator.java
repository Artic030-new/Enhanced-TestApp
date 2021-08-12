package advsolar;

import advsolar.AdvancedSolarPanel;
import advsolar.ContainerQGenerator;
import ic2.api.Direction;
import ic2.api.IWrenchable;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileSourceEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySource;
import ic2.api.energy.tile.IEnergyTile;
import ic2.api.network.INetworkClientTileEntityEventListener;
import ic2.api.network.INetworkDataProvider;
import ic2.api.network.INetworkUpdateListener;
import ic2.api.network.NetworkHelper;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;

public class TileEntityQGenerator extends TileEntity implements IEnergyTile, IWrenchable, IEnergySource, INetworkClientTileEntityEventListener, INetworkDataProvider, INetworkUpdateListener, IInventory {

   public static Random randomizer = new Random();
   public int ticker;
   public int production;
   public boolean initialized = false;
   private short facing = 0;
   public boolean addedToEnergyNet;
   private boolean created = false;
   public boolean active;
   public boolean lastState;
   public int maxPacketSize;
   private int lastX;
   private int lastY;
   private int lastZ;
   public boolean loaded = false;
   private static List fields = Arrays.asList(new String[0]);

   public TileEntityQGenerator() {
      this.production = AdvancedSolarPanel.qgbaseProduction;
      this.maxPacketSize = AdvancedSolarPanel.qgbaseMaxPacketSize;
      this.ticker = randomizer.nextInt(this.tickRate());
      this.lastX = super.xCoord;
      this.lastY = super.yCoord;
      this.lastZ = super.zCoord;
      this.lastState = false;
   }

   public void validate() {
      super.validate();
      this.onLoaded();
   }

   public void invalidate() {
      if(this.loaded) {
         this.onUnloaded();
      }

      super.invalidate();
   }

   public void onLoaded() {
      if(super.worldObj.isRemote) {
         NetworkHelper.requestInitialData(this);
      } else {
         MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
      }

      this.addedToEnergyNet = true;
      this.loaded = true;
   }

   public void onChunkUnload() {
      if(this.loaded) {
         this.onUnloaded();
      }

      super.onChunkUnload();
   }

   public void onUnloaded() {
      if(this.addedToEnergyNet) {
         MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
         this.addedToEnergyNet = false;
      }

      this.loaded = false;
   }

   public boolean canUpdate() {
      return true;
   }

   public void updateEntity() {
      super.updateEntity();
      if(!this.addedToEnergyNet) {
         this.onLoaded();
      }

      if(this.lastX != super.xCoord || this.lastZ != super.zCoord || this.lastY != super.yCoord) {
         this.lastX = super.xCoord;
         this.lastY = super.yCoord;
         this.lastZ = super.zCoord;
         this.onUnloaded();
         this.onLoaded();
      }

      this.getActive();
      if(!this.active) {
         if(this.production > this.maxPacketSize) {
            int leftOverPacket = this.production;

            while(leftOverPacket > 0) {
               if(leftOverPacket > this.maxPacketSize) {
                  this.sendEnergy(this.maxPacketSize);
                  leftOverPacket -= this.maxPacketSize;
               } else {
                  this.sendEnergy(leftOverPacket);
                  leftOverPacket = 0;
               }
            }
         } else {
            this.sendEnergy(this.production);
         }
      }

   }

   public void getActive() {
      this.active = super.worldObj.isBlockIndirectlyGettingPowered(super.xCoord, super.yCoord, super.zCoord);
      if(this.active != this.lastState) {
         this.lastState = this.active;
         super.worldObj.markBlockForUpdate(super.xCoord, super.yCoord, super.zCoord);
      }

   }

   public void readFromNBT(NBTTagCompound tagCompound) {
      super.readFromNBT(tagCompound);
      this.production = tagCompound.getInteger("production");
      this.maxPacketSize = tagCompound.getInteger("maxPacketSize");
      this.lastX = tagCompound.getInteger("lastX");
      this.lastY = tagCompound.getInteger("lastY");
      this.lastZ = tagCompound.getInteger("lastZ");
   }

   public void writeToNBT(NBTTagCompound tagCompound) {
      super.writeToNBT(tagCompound);
      new NBTTagList();
      tagCompound.setInteger("production", this.production);
      tagCompound.setInteger("maxPacketSize", this.maxPacketSize);
      tagCompound.setInteger("lastX", this.lastX);
      tagCompound.setInteger("lastY", this.lastY);
      tagCompound.setInteger("lastZ", this.lastZ);
   }

   public boolean isAddedToEnergyNet() {
      return this.addedToEnergyNet;
   }

   public boolean emitsEnergyTo(TileEntity receiver, Direction direction) {
      return true;
   }

   public int sendEnergy(int send) {
      EnergyTileSourceEvent sendEnergy = new EnergyTileSourceEvent(this, send);
      MinecraftForge.EVENT_BUS.post(sendEnergy);
      return sendEnergy.amount;
   }

   public int getMaxEnergyOutput() {
      return 2048;
   }

   public boolean isUseableByPlayer(EntityPlayer player) {
      return super.worldObj.getBlockTileEntity(super.xCoord, super.yCoord, super.zCoord) != this?false:player.getDistanceSq((double)super.xCoord + 0.5D, (double)super.yCoord + 0.5D, (double)super.zCoord + 0.5D) <= 64.0D;
   }

   public int tickRate() {
      return 128;
   }

   public short getFacing() {
      return this.facing;
   }

   public void setFacing(short facing) {
      this.facing = facing;
   }

   public boolean wrenchCanSetFacing(EntityPlayer player, int i) {
      return false;
   }

   public boolean wrenchCanRemove(EntityPlayer player) {
      return true;
   }

   public float getWrenchDropRate() {
      return 1.0F;
   }

   public ItemStack getWrenchDrop(EntityPlayer player) {
      return new ItemStack(super.worldObj.getBlockId(super.xCoord, super.yCoord, super.zCoord), 1, super.worldObj.getBlockMetadata(super.xCoord, super.yCoord, super.zCoord));
   }

   public void onNetworkUpdate(String field) {}

   public List getNetworkedFields() {
      return fields;
   }

   public int getSizeInventory() {
      return 4;
   }

   public void openChest() {}

   public void closeChest() {}

   public Container getGuiContainer(InventoryPlayer player) {
      return new ContainerQGenerator(player, this);
   }

   public String getInvName() {
      return "QuantumGenerator";
   }

   public ItemStack getStackInSlot(int slot) {
      return null;
   }

   public ItemStack decrStackSize(int index, int count) {
      return null;
   }

   public ItemStack getStackInSlotOnClosing(int slot) {
      return null;
   }

   public void setInventorySlotContents(int index, ItemStack stack) {}

   public int getInventoryStackLimit() {
      return 0;
   }

   public void changeProductionOutput(int value) {
      this.production += value;
      if(this.production < 1) {
         this.production = 1;
      }

   }

   public void changeMaxPacketSize(int value) {
      this.maxPacketSize += value;
      if(this.maxPacketSize < 1) {
         this.maxPacketSize = 1;
      }

   }

   public void onNetworkEvent(EntityPlayer player, int event) {
      switch(event) {
      case 1:
         this.changeProductionOutput(-100);
         break;
      case 2:
         this.changeProductionOutput(-10);
         break;
      case 3:
         this.changeProductionOutput(-1);
         break;
      case 4:
         this.changeProductionOutput(1);
         break;
      case 5:
         this.changeProductionOutput(10);
         break;
      case 6:
         this.changeProductionOutput(100);
         break;
      case 7:
         this.changeMaxPacketSize(-100);
         break;
      case 8:
         this.changeMaxPacketSize(-10);
         break;
      case 9:
         this.changeMaxPacketSize(-1);
         break;
      case 10:
         this.changeMaxPacketSize(1);
         break;
      case 11:
         this.changeMaxPacketSize(10);
         break;
      case 12:
         this.changeMaxPacketSize(100);
         break;
      case 101:
         this.changeProductionOutput(-1000);
         break;
      case 102:
         this.changeProductionOutput(-100);
         break;
      case 103:
         this.changeProductionOutput(-10);
         break;
      case 104:
         this.changeProductionOutput(10);
         break;
      case 105:
         this.changeProductionOutput(100);
         break;
      case 106:
         this.changeProductionOutput(1000);
         break;
      case 107:
         this.changeMaxPacketSize(-1000);
         break;
      case 108:
         this.changeMaxPacketSize(-100);
         break;
      case 109:
         this.changeMaxPacketSize(-10);
         break;
      case 110:
         this.changeMaxPacketSize(10);
         break;
      case 111:
         this.changeMaxPacketSize(100);
         break;
      case 112:
         this.changeMaxPacketSize(1000);
      }

   }

}
