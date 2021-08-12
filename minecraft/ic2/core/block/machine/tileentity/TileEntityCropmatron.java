package ic2.core.block.machine.tileentity;

import ic2.api.Direction;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySink;
import ic2.core.ContainerIC2;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.Ic2Items;
import ic2.core.block.TileEntityCrop;
import ic2.core.block.machine.ContainerCropmatron;
import ic2.core.block.machine.tileentity.TileEntityMachine;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.ISidedInventory;
import net.minecraftforge.common.MinecraftForge;

public class TileEntityCropmatron extends TileEntityMachine implements IEnergySink, IHasGui, ISidedInventory {

   public int energy = 0;
   public int ticker = 0;
   public int maxEnergy = 1000;
   public int scanX = -4;
   public int scanY = -1;
   public int scanZ = -4;
   public boolean addedToEnergyNet = false;
   public static int maxInput = 32;


   public TileEntityCropmatron() {
      super(9);
   }

   public void readFromNBT(NBTTagCompound nbttagcompound) {
      super.readFromNBT(nbttagcompound);
      this.energy = nbttagcompound.getShort("energy");
   }

   public void writeToNBT(NBTTagCompound nbttagcompound) {
      super.writeToNBT(nbttagcompound);
      nbttagcompound.setShort("energy", (short)this.energy);
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
      if(this.energy >= 31) {
         this.scan();
      }

   }

   public void scan() {
      ++this.scanX;
      if(this.scanX > 4) {
         this.scanX = -4;
         ++this.scanZ;
         if(this.scanZ > 4) {
            this.scanZ = -4;
            ++this.scanY;
            if(this.scanY > 1) {
               this.scanY = -1;
            }
         }
      }

      --this.energy;
      TileEntity te = super.worldObj.getBlockTileEntity(super.xCoord + this.scanX, super.yCoord + this.scanY, super.zCoord + this.scanZ);
      if(te instanceof TileEntityCrop) {
         TileEntityCrop crop = (TileEntityCrop)te;
         this.updateSlots();
         if(super.inventory[0] != null && super.inventory[0].itemID == Ic2Items.fertilizer.itemID && crop.applyFertilizer(false)) {
            this.energy -= 10;
            --super.inventory[0].stackSize;
            this.checkStackSizeZero(0);
         }

         if(super.inventory[3] != null && super.inventory[3].itemID == Ic2Items.hydratingCell.itemID && crop.applyHydration(false, super.inventory[3])) {
            this.energy -= 10;
            this.checkStackSizeZero(3);
         }

         if(super.inventory[6] != null && super.inventory[6].itemID == Ic2Items.weedEx.itemID && crop.applyWeedEx(false)) {
            this.energy -= 10;
            super.inventory[6].damageItem(1, (EntityLiving)null);
            if(super.inventory[6].getItemDamage() >= super.inventory[6].getMaxDamage()) {
               --super.inventory[6].stackSize;
               this.checkStackSizeZero(6);
            }
         }
      }

   }

   public void checkStackSizeZero(int x) {
      if(super.inventory[x] != null && super.inventory[x].stackSize <= 0) {
         super.inventory[x] = null;
      }

   }

   public void updateSlots() {
      this.moveFrom(1, 0);
      this.moveFrom(2, 1);
      this.moveFrom(4, 3);
      this.moveFrom(5, 4);
      this.moveFrom(7, 6);
      this.moveFrom(8, 7);
   }

   public void moveFrom(int from, int to) {
      if(super.inventory[from] != null && super.inventory[to] == null) {
         super.inventory[to] = super.inventory[from];
         super.inventory[from] = null;
      }

   }

   public boolean acceptsEnergyFrom(TileEntity emitter, Direction direction) {
      return true;
   }

   public boolean isAddedToEnergyNet() {
      return this.addedToEnergyNet;
   }

   public int demandsEnergy() {
      return this.maxEnergy - this.energy;
   }

   public int gaugeEnergyScaled(int i) {
      if(this.energy <= 0) {
         return 0;
      } else {
         int r = this.energy * i / this.maxEnergy;
         if(r > i) {
            r = i;
         }

         return r;
      }
   }

   public int injectEnergy(Direction directionFrom, int amount) {
      if(amount > maxInput) {
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
      return maxInput;
   }

   public String getInvName() {
      return "Crop-Matron";
   }

   public String getGuiClassName(EntityPlayer entityPlayer) {
      return "block.machine.gui.GuiCropmatron";
   }

   public ContainerIC2 getGuiContainer(EntityPlayer entityPlayer) {
      return new ContainerCropmatron(entityPlayer, this);
   }

   public void onGuiClosed(EntityPlayer entityPlayer) {}

   public int getStartInventorySide(ForgeDirection side) {
      switch(TileEntityCropmatron.NamelessClass1923162876.$SwitchMap$net$minecraftforge$common$ForgeDirection[side.ordinal()]) {
      case 1:
         return 6;
      case 2:
         return 3;
      default:
         return 0;
      }
   }

   public int getSizeInventorySide(ForgeDirection side) {
      return 3;
   }


   // $FF: synthetic class
   static class NamelessClass1923162876 {

      // $FF: synthetic field
      static final int[] $SwitchMap$net$minecraftforge$common$ForgeDirection = new int[ForgeDirection.values().length];


      static {
         try {
            $SwitchMap$net$minecraftforge$common$ForgeDirection[ForgeDirection.DOWN.ordinal()] = 1;
         } catch (NoSuchFieldError var2) {
            ;
         }

         try {
            $SwitchMap$net$minecraftforge$common$ForgeDirection[ForgeDirection.UP.ordinal()] = 2;
         } catch (NoSuchFieldError var1) {
            ;
         }

      }
   }
}
