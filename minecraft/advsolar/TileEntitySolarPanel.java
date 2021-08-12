package advsolar;

import advsolar.ContainerAdvSolarPanel;
import ic2.api.Direction;
import ic2.api.ElectricItem;
import ic2.api.IElectricItem;
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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;

public class TileEntitySolarPanel extends TileEntity implements IEnergyTile, IWrenchable, IEnergySource, IInventory, INetworkClientTileEntityEventListener, INetworkDataProvider, INetworkUpdateListener {

   public static Random randomizer = new Random();
   public int ticker;
   public int generating;
   public int genDay;
   public int genNight;
   public boolean initialized;
   public boolean sunIsUp;
   public boolean skyIsVisible;
   private short facing = 0;
   private boolean noSunWorld;
   private boolean wetBiome;
   public boolean addedToEnergyNet;
   private boolean created = false;
   private ItemStack[] chargeSlots;
   public int fuel;
   private int lastX;
   private int lastY;
   private int lastZ;
   public int storage;
   private int solarType;
   public String panelName;
   public int production;
   public int maxStorage;
   public boolean loaded = false;
   private static List fields = Arrays.asList(new String[0]);

   public TileEntitySolarPanel(String gName, int typeSolar, int gDay, int gNight, int gOutput, int gmaxStorage) {
      this.solarType = typeSolar;
      this.genDay = gDay;
      this.genNight = gNight;
      this.storage = 0;
      this.panelName = gName;
      this.sunIsUp = false;
      this.skyIsVisible = false;
      this.maxStorage = gmaxStorage;
      this.chargeSlots = new ItemStack[4];
      this.initialized = false;
      this.production = gOutput;
      this.ticker = randomizer.nextInt(this.tickRate());
      this.lastX = super.xCoord;
      this.lastY = super.yCoord;
      this.lastZ = super.zCoord;
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

   public void intialize() {
      this.wetBiome = super.worldObj.getWorldChunkManager().getBiomeGenAt(super.xCoord, super.zCoord).getIntRainfall() > 0;
      this.noSunWorld = super.worldObj.provider.hasNoSky;
      this.updateVisibility();
      this.initialized = true;
      if(!this.addedToEnergyNet) {
         this.onLoaded();
      }

   }

   public boolean canUpdate() {
      return true;
   }

   public void updateEntity() {
      super.updateEntity();
      if(!this.initialized) {
         this.intialize();
      }

      if(this.lastX != super.xCoord || this.lastZ != super.zCoord || this.lastY != super.yCoord) {
         this.lastX = super.xCoord;
         this.lastY = super.yCoord;
         this.lastZ = super.zCoord;
         this.onUnloaded();
         this.intialize();
      }

      this.gainFuel();
      if(this.generating > 0) {
         if(this.storage + this.generating <= this.maxStorage) {
            this.storage += this.generating;
         } else {
            this.storage = this.maxStorage;
         }
      }

      boolean needInvUpdate = false;
      boolean sentPacket = false;

      for(int i = 0; i < this.chargeSlots.length; ++i) {
         if(this.chargeSlots[i] != null && Item.itemsList[this.chargeSlots[i].itemID] instanceof IElectricItem && this.storage > 0) {
            int var4 = ElectricItem.charge(this.chargeSlots[i], this.storage, this.solarType, false, false);
            if(var4 > 0) {
               needInvUpdate = true;
            }

            this.storage -= var4;
         }
      }

      if(needInvUpdate) {
         this.onInventoryChanged();
      }

      if(this.storage - this.production >= 0) {
         this.storage -= this.production - this.sendEnergy(this.production);
      }

   }

   public int gainFuel() {
      if(this.ticker++ % this.tickRate() == 0) {
         this.updateVisibility();
      }

      if(this.sunIsUp && this.skyIsVisible) {
         this.generating = 0 + this.genDay;
         return this.generating;
      } else if(this.skyIsVisible) {
         this.generating = 0 + this.genNight;
         return this.generating;
      } else {
         this.generating = 0;
         return this.generating;
      }
   }

   public void updateVisibility() {
      Boolean rainWeather = Boolean.valueOf(this.wetBiome && (super.worldObj.isRaining() || super.worldObj.isThundering()));
      if(super.worldObj.isDaytime() && !rainWeather.booleanValue()) {
         this.sunIsUp = true;
      } else {
         this.sunIsUp = false;
      }

      if(super.worldObj.canBlockSeeTheSky(super.xCoord, super.yCoord + 1, super.zCoord) && !this.noSunWorld) {
         this.skyIsVisible = true;
      } else {
         this.skyIsVisible = false;
      }

   }

   public void readFromNBT(NBTTagCompound tagCompound) {
      super.readFromNBT(tagCompound);
      this.storage = tagCompound.getInteger("storage");
      this.lastX = tagCompound.getInteger("lastX");
      this.lastY = tagCompound.getInteger("lastY");
      this.lastZ = tagCompound.getInteger("lastZ");
      NBTTagList nbttaglist = tagCompound.getTagList("Items");
      this.chargeSlots = new ItemStack[this.getSizeInventory()];

      for(int i = 0; i < nbttaglist.tagCount(); ++i) {
         NBTTagCompound nbttagcompound1 = (NBTTagCompound)nbttaglist.tagAt(i);
         int j = nbttagcompound1.getByte("Slot") & 255;
         if(j >= 0 && j < this.chargeSlots.length) {
            this.chargeSlots[j] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
         }
      }

   }

   public void writeToNBT(NBTTagCompound tagCompound) {
      super.writeToNBT(tagCompound);
      NBTTagList nbttaglist = new NBTTagList();
      tagCompound.setInteger("storage", this.storage);
      tagCompound.setInteger("lastX", this.lastX);
      tagCompound.setInteger("lastY", this.lastY);
      tagCompound.setInteger("lastZ", this.lastZ);

      for(int i = 0; i < this.chargeSlots.length; ++i) {
         if(this.chargeSlots[i] != null) {
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();
            nbttagcompound1.setByte("Slot", (byte)i);
            this.chargeSlots[i].writeToNBT(nbttagcompound1);
            nbttaglist.appendTag(nbttagcompound1);
         }
      }

      tagCompound.setTag("Items", nbttaglist);
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
      return this.production;
   }

   public int gaugeEnergyScaled(int i) {
      return this.storage * i / this.maxStorage;
   }

   public int gaugeFuelScaled(int i) {
      return i;
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

   public ItemStack[] getContents() {
      return this.chargeSlots;
   }

   public int getSizeInventory() {
      return 4;
   }

   public ItemStack getStackInSlot(int i) {
      return this.chargeSlots[i];
   }

   public ItemStack decrStackSize(int i, int j) {
      if(this.chargeSlots[i] != null) {
         ItemStack itemstack1;
         if(this.chargeSlots[i].stackSize <= j) {
            itemstack1 = this.chargeSlots[i];
            this.chargeSlots[i] = null;
            this.onInventoryChanged();
            return itemstack1;
         } else {
            itemstack1 = this.chargeSlots[i].splitStack(j);
            if(this.chargeSlots[i].stackSize == 0) {
               this.chargeSlots[i] = null;
            }

            this.onInventoryChanged();
            return itemstack1;
         }
      } else {
         return null;
      }
   }

   public void setInventorySlotContents(int index, ItemStack stack) {
      this.chargeSlots[index] = stack;
      if(stack != null && stack.stackSize > this.getInventoryStackLimit()) {
    	 stack.stackSize = this.getInventoryStackLimit();
      }

      this.onInventoryChanged();
   }

   public int getInventoryStackLimit() {
      return 64;
   }

   public void openChest() {}

   public void closeChest() {}

   public Container getGuiContainer(InventoryPlayer player) {
      return new ContainerAdvSolarPanel(player, this);
   }

   public String getInvName() {
      return null;
   }

   public ItemStack getStackInSlotOnClosing(int slot) {
      if(this.chargeSlots[slot] != null) {
         ItemStack var2 = this.chargeSlots[slot];
         this.chargeSlots[slot] = null;
         return var2;
      } else {
         return null;
      }
   }

   public void onNetworkUpdate(String field) {}

   public List getNetworkedFields() {
      return fields;
   }

   public void onNetworkEvent(EntityPlayer player, int event) {}

}
