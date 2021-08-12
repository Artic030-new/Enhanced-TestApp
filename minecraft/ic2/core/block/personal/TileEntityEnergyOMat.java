package ic2.core.block.personal;

import ic2.api.Direction;
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
import ic2.core.block.personal.ContainerEnergyOMatClosed;
import ic2.core.block.personal.ContainerEnergyOMatOpen;
import ic2.core.block.personal.IPersonalBlock;
import ic2.core.block.personal.TileEntityPersonalChest;
import ic2.core.util.StackUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.ISidedInventory;
import net.minecraftforge.common.MinecraftForge;

public class TileEntityEnergyOMat extends TileEntityMachine implements IPersonalBlock, IHasGui, ISidedInventory, IEnergySink, IEnergySource, INetworkClientTileEntityEventListener {

   private static final Direction[] directions = Direction.values();
   public int euOffer = 1000;
   public String owner = "null";
   private boolean addedToEnergyNet = false;
   public int paidFor;
   public int euBuffer;
   private int euBufferMax = 10000;
   private int maxOutputRate = 32;


   public TileEntityEnergyOMat() {
      super(3);
   }

   public String getInvName() {
      return "Energy-O-Mat";
   }

   public void readFromNBT(NBTTagCompound nbttagcompound) {
      super.readFromNBT(nbttagcompound);
      this.owner = nbttagcompound.getString("owner");
      this.euOffer = nbttagcompound.getInteger("euOffer");
      this.paidFor = nbttagcompound.getInteger("paidFor");
      this.euBuffer = nbttagcompound.getInteger("euBuffer");
   }

   public void writeToNBT(NBTTagCompound nbttagcompound) {
      super.writeToNBT(nbttagcompound);
      nbttagcompound.setString("owner", this.owner);
      nbttagcompound.setInteger("euOffer", this.euOffer);
      nbttagcompound.setInteger("paidFor", this.paidFor);
      nbttagcompound.setInteger("euBuffer", this.euBuffer);
   }

   public boolean wrenchCanRemove(EntityPlayer entityPlayer) {
      return this.canAccess(entityPlayer);
   }

   public boolean canAccess(EntityPlayer player) {
      if(this.owner.equals("null")) {
         this.owner = player.username;
         return true;
      } else {
         return this.owner.equalsIgnoreCase(player.username);
      }
   }

   public int getStartInventorySide(ForgeDirection side) {
      return 1;
   }

   public int getSizeInventorySide(ForgeDirection side) {
      return 1;
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
      if(IC2.platform.isSimulating()) {
         this.euBufferMax = 10000;
         this.maxOutputRate = 32;
         if(super.inventory[2] != null) {
            if(super.inventory[2].isItemEqual(Ic2Items.energyStorageUpgrade)) {
               this.euBufferMax = 10000 * (super.inventory[2].stackSize + 1);
            } else if(super.inventory[2].isItemEqual(Ic2Items.transformerUpgrade)) {
               this.maxOutputRate = 32 * (int)Math.pow(4.0D, (double)Math.min(4, super.inventory[2].stackSize));
            }
         }

         if(super.inventory[1] != null && super.inventory[0] != null && super.inventory[1].isItemEqual(super.inventory[0])) {
            this.paidFor += this.euOffer / super.inventory[0].stackSize * super.inventory[1].stackSize;
            Direction[] min = directions;
            int event = min.length;

            for(int i$ = 0; i$ < event; ++i$) {
               Direction direction = min[i$];
               TileEntity target = direction.applyToTileEntity(this);
               if(target instanceof IInventory && (!(target instanceof TileEntityPersonalChest) || ((TileEntityPersonalChest)target).owner.equals(this.owner)) && StackUtil.putInInventory((IInventory)target, super.inventory[1])) {
                  break;
               }
            }

            super.inventory[1] = null;
            this.onInventoryChanged();
         }

         if(this.euBuffer > this.euBufferMax) {
            this.euBuffer = this.euBufferMax;
         }

         int var6 = Math.min(this.maxOutputRate, this.euBuffer);
         EnergyTileSourceEvent var7 = new EnergyTileSourceEvent(this, var6);
         MinecraftForge.EVENT_BUS.post(var7);
         this.euBuffer -= var6 - var7.amount;
      }

   }

   public boolean wrenchCanSetFacing(EntityPlayer entityPlayer, int side) {
      return this.getFacing() != side && this.canAccess(entityPlayer);
   }

   public boolean acceptsEnergyFrom(TileEntity emitter, Direction direction) {
      return this.facingMatchesDirection(direction);
   }

   public boolean facingMatchesDirection(Direction direction) {
      return direction.toSideValue() == this.getFacing();
   }

   public boolean isAddedToEnergyNet() {
      return this.addedToEnergyNet;
   }

   public boolean emitsEnergyTo(TileEntity receiver, Direction direction) {
      return !this.facingMatchesDirection(direction);
   }

   public int getMaxEnergyOutput() {
      return 32;
   }

   public int demandsEnergy() {
      return Math.min(this.paidFor, this.euBufferMax - this.euBuffer);
   }

   public int injectEnergy(Direction directionFrom, int amount) {
      int toAdd = Math.min(Math.min(amount, this.paidFor), this.euBufferMax - this.euBuffer);
      this.paidFor -= toAdd;
      this.euBuffer += toAdd;
      return amount - toAdd;
   }

   public int getMaxSafeInput() {
      return Integer.MAX_VALUE;
   }

   public ContainerIC2 getGuiContainer(EntityPlayer entityPlayer) {
      return (ContainerIC2)(this.canAccess(entityPlayer)?new ContainerEnergyOMatOpen(entityPlayer, this):new ContainerEnergyOMatClosed(entityPlayer, this));
   }

   public String getGuiClassName(EntityPlayer entityPlayer) {
      return this.canAccess(entityPlayer)?"block.personal.GuiEnergyOMatOpen":"block.personal.GuiEnergyOMatClosed";
   }

   public void onGuiClosed(EntityPlayer entityPlayer) {}

   public void onNetworkEvent(EntityPlayer player, int event) {
      if(this.canAccess(player)) {
         switch(event) {
         case 0:
            this.attemptSet(-1000);
            break;
         case 1:
            this.attemptSet(-100);
            break;
         case 2:
            this.attemptSet(1000);
            break;
         case 3:
            this.attemptSet(100);
         }

      }
   }

   private void attemptSet(int amount) {
      if(this.euOffer + amount <= 0) {
         amount = 0;
      }

      this.euOffer += amount;
   }

}
