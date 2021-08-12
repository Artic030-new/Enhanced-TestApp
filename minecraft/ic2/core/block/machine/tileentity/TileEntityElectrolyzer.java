package ic2.core.block.machine.tileentity;

import ic2.core.ContainerIC2;
import ic2.core.IHasGui;
import ic2.core.Ic2Items;
import ic2.core.block.machine.ContainerElectrolyzer;
import ic2.core.block.machine.tileentity.TileEntityMachine;
import ic2.core.block.wiring.TileEntityElectricBlock;
import java.util.Random;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.ISidedInventory;

public class TileEntityElectrolyzer extends TileEntityMachine implements IHasGui, ISidedInventory {

   public static Random randomizer = new Random();
   public short energy = 0;
   public TileEntityElectricBlock mfe = null;
   public int ticker;


   public TileEntityElectrolyzer() {
      super(2);
      this.ticker = randomizer.nextInt(16);
   }

   public void readFromNBT(NBTTagCompound nbttagcompound) {
      super.readFromNBT(nbttagcompound);
      this.energy = nbttagcompound.getShort("energy");
   }

   public void writeToNBT(NBTTagCompound nbttagcompound) {
      super.writeToNBT(nbttagcompound);
      nbttagcompound.setShort("energy", this.energy);
   }

   public String getInvName() {
      return "Electrolyzer";
   }

   public void updateEntity() {
      super.updateEntity();
      boolean needsInvUpdate = false;
      boolean turnActive = false;
      if(this.ticker++ % 16 == 0) {
         this.mfe = this.lookForMFE();
      }

      if(this.mfe != null) {
         if(this.shouldDrain() && this.canDrain()) {
            needsInvUpdate = this.drain();
            turnActive = true;
         }

         if(this.shouldPower() && (this.canPower() || this.energy > 0)) {
            needsInvUpdate = this.power();
            turnActive = true;
         }

         if(this.getActive() != turnActive) {
            this.setActive(turnActive);
            needsInvUpdate = true;
         }

         if(needsInvUpdate) {
            this.onInventoryChanged();
         }

      }
   }

   public boolean shouldDrain() {
      return this.mfe != null && (double)this.mfe.energy / (double)this.mfe.maxStorage >= 0.7D;
   }

   public boolean shouldPower() {
      return this.mfe != null && (double)this.mfe.energy / (double)this.mfe.maxStorage <= 0.3D;
   }

   public boolean canDrain() {
      return super.inventory[0] != null && super.inventory[0].isItemEqual(Ic2Items.waterCell) && (super.inventory[1] == null || super.inventory[1].isItemEqual(Ic2Items.electrolyzedWaterCell) && super.inventory[1].stackSize < super.inventory[1].getMaxStackSize());
   }

   public boolean canPower() {
      return (super.inventory[0] == null || super.inventory[0].isItemEqual(Ic2Items.waterCell) && super.inventory[0].stackSize < super.inventory[0].getMaxStackSize()) && super.inventory[1] != null && super.inventory[1].isItemEqual(Ic2Items.electrolyzedWaterCell);
   }

   public boolean drain() {
      this.mfe.energy -= this.processRate();
      this.energy = (short)(this.energy + this.processRate());
      if(this.energy >= 20000) {
         this.energy = (short)(this.energy - 20000);
         --super.inventory[0].stackSize;
         if(super.inventory[0].stackSize <= 0) {
            super.inventory[0] = null;
         }

         if(super.inventory[1] == null) {
            super.inventory[1] = Ic2Items.electrolyzedWaterCell.copy();
         } else {
            ++super.inventory[1].stackSize;
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean power() {
      if(this.energy > 0) {
         int out = this.processRate();
         if(out > this.energy) {
            out = this.energy;
         }

         this.energy = (short)(this.energy - out);
         this.mfe.energy += out;
         return false;
      } else {
         this.energy = (short)(this.energy + 12000 + 2000 * this.mfe.tier);
         --super.inventory[1].stackSize;
         if(super.inventory[1].stackSize <= 0) {
            super.inventory[1] = null;
         }

         if(super.inventory[0] == null) {
            super.inventory[0] = Ic2Items.waterCell.copy();
         } else {
            ++super.inventory[0].stackSize;
         }

         return true;
      }
   }

   public int processRate() {
      switch(this.mfe.tier) {
      case 2:
         return 8;
      case 3:
         return 32;
      default:
         return 2;
      }
   }

   public TileEntityElectricBlock lookForMFE() {
      return super.worldObj.getBlockTileEntity(super.xCoord, super.yCoord - 1, super.zCoord) instanceof TileEntityElectricBlock?(TileEntityElectricBlock)super.worldObj.getBlockTileEntity(super.xCoord, super.yCoord - 1, super.zCoord):(super.worldObj.getBlockTileEntity(super.xCoord, super.yCoord + 1, super.zCoord) instanceof TileEntityElectricBlock?(TileEntityElectricBlock)super.worldObj.getBlockTileEntity(super.xCoord, super.yCoord + 1, super.zCoord):(super.worldObj.getBlockTileEntity(super.xCoord - 1, super.yCoord, super.zCoord) instanceof TileEntityElectricBlock?(TileEntityElectricBlock)super.worldObj.getBlockTileEntity(super.xCoord - 1, super.yCoord, super.zCoord):(super.worldObj.getBlockTileEntity(super.xCoord + 1, super.yCoord, super.zCoord) instanceof TileEntityElectricBlock?(TileEntityElectricBlock)super.worldObj.getBlockTileEntity(super.xCoord + 1, super.yCoord, super.zCoord):(super.worldObj.getBlockTileEntity(super.xCoord, super.yCoord, super.zCoord - 1) instanceof TileEntityElectricBlock?(TileEntityElectricBlock)super.worldObj.getBlockTileEntity(super.xCoord, super.yCoord, super.zCoord - 1):(super.worldObj.getBlockTileEntity(super.xCoord, super.yCoord, super.zCoord + 1) instanceof TileEntityElectricBlock?(TileEntityElectricBlock)super.worldObj.getBlockTileEntity(super.xCoord, super.yCoord, super.zCoord + 1):null)))));
   }

   public int gaugeEnergyScaled(int i) {
      if(this.energy <= 0) {
         return 0;
      } else {
         int r = this.energy * i / 20000;
         if(r > i) {
            r = i;
         }

         return r;
      }
   }

   public ContainerIC2 getGuiContainer(EntityPlayer entityPlayer) {
      return new ContainerElectrolyzer(entityPlayer, this);
   }

   public String getGuiClassName(EntityPlayer entityPlayer) {
      return "block.machine.gui.GuiElectrolyzer";
   }

   public void onGuiClosed(EntityPlayer entityPlayer) {}

   public int getStartInventorySide(ForgeDirection side) {
      switch(TileEntityElectrolyzer.NamelessClass1750109756.$SwitchMap$net$minecraftforge$common$ForgeDirection[side.ordinal()]) {
      case 1:
         return 1;
      default:
         return 0;
      }
   }

   public int getSizeInventorySide(ForgeDirection side) {
      return 1;
   }


   // $FF: synthetic class
   static class NamelessClass1750109756 {

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
