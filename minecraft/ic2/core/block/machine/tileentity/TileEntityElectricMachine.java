package ic2.core.block.machine.tileentity;

import ic2.api.network.INetworkTileEntityEventListener;
import ic2.core.ContainerIC2;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.Ic2Items;
import ic2.core.audio.AudioSource;
import ic2.core.block.machine.ContainerElectricMachine;
import ic2.core.block.machine.tileentity.TileEntityElecMachine;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.ISidedInventory;

public abstract class TileEntityElectricMachine extends TileEntityElecMachine implements IHasGui, INetworkTileEntityEventListener, ISidedInventory {

   public short progress = 0;
   public int defaultEnergyConsume;
   public int defaultOperationLength;
   public int defaultMaxInput;
   public int defaultEnergyStorage;
   public int energyConsume;
   public int operationLength;
   public float serverChargeLevel;
   public float serverProgress;
   public AudioSource audioSource;
   private static final int EventStart = 0;
   private static final int EventInterrupt = 1;
   private static final int EventStop = 2;


   public TileEntityElectricMachine(int slots, int e, int length, int maxinput) {
      super(slots + 4, 1, e * length + maxinput - 1, maxinput);
      this.defaultEnergyConsume = this.energyConsume = e;
      this.defaultOperationLength = this.operationLength = length;
      this.defaultMaxInput = super.maxInput;
      this.defaultEnergyStorage = e * length;
   }

   public void readFromNBT(NBTTagCompound nbttagcompound) {
      super.readFromNBT(nbttagcompound);
      this.progress = nbttagcompound.getShort("progress");
   }

   public void writeToNBT(NBTTagCompound nbttagcompound) {
      super.writeToNBT(nbttagcompound);
      nbttagcompound.setShort("progress", this.progress);
   }

   public float getChargeLevel() {
      float ret;
      if(IC2.platform.isSimulating()) {
         ret = (float)super.energy / (float)(super.maxEnergy - super.maxInput + 1);
         if(ret > 1.0F) {
            ret = 1.0F;
         }
      } else {
         ret = this.serverChargeLevel;
      }

      return ret;
   }

   public float getProgress() {
      float ret;
      if(IC2.platform.isSimulating()) {
         ret = (float)this.progress / (float)this.operationLength;
         if(ret > 1.0F) {
            ret = 1.0F;
         }
      } else {
         ret = this.serverProgress;
      }

      return ret;
   }

   public void setChargeLevel(float chargeLevel) {
      assert !IC2.platform.isSimulating();

      this.serverChargeLevel = chargeLevel;
   }

   public void setProgress(float progress) {
      assert !IC2.platform.isSimulating();

      this.serverProgress = progress;
   }

   public void onLoaded() {
      super.onLoaded();
      if(IC2.platform.isSimulating()) {
         this.setOverclockRates();
      }

   }

   public void onUnloaded() {
      super.onUnloaded();
      if(IC2.platform.isRendering() && this.audioSource != null) {
         IC2.audioManager.removeSources(this);
         this.audioSource = null;
      }

   }

   public void onInventoryChanged() {
      super.onInventoryChanged();
      if(IC2.platform.isSimulating()) {
         this.setOverclockRates();
      }

   }

   public void updateEntity() {
      super.updateEntity();
      boolean canOperate = this.canOperate();
      boolean needsInvUpdate = false;
      if(super.energy <= this.energyConsume * this.operationLength && canOperate) {
         needsInvUpdate = this.provideEnergy();
      }

      boolean newActive = this.getActive();
      if(this.progress >= this.operationLength) {
         this.operate();
         needsInvUpdate = true;
         this.progress = 0;
         newActive = false;
         IC2.network.initiateTileEntityEvent(this, 2, true);
      }

      canOperate = this.canOperate();
      if(newActive && this.progress != 0) {
         if(!canOperate || super.energy < this.energyConsume) {
            if(!canOperate) {
               this.progress = 0;
            }

            newActive = false;
            IC2.network.initiateTileEntityEvent(this, 1, true);
         }
      } else if(canOperate) {
         if(super.energy >= this.energyConsume) {
            newActive = true;
            IC2.network.initiateTileEntityEvent(this, 0, true);
         }
      } else {
         this.progress = 0;
      }

      if(newActive) {
         ++this.progress;
         super.energy -= this.energyConsume;
      }

      if(needsInvUpdate) {
         this.onInventoryChanged();
      }

      if(newActive != this.getActive()) {
         this.setActive(newActive);
      }

   }

   public void setOverclockRates() {
      int overclockerUpgradeCount = 0;
      int transformerUpgradeCount = 0;
      int energyStorageUpgradeCount = 0;

      for(int previousProgress = 0; previousProgress < 4; ++previousProgress) {
         ItemStack itemStack = super.inventory[previousProgress + super.inventory.length - 4];
         if(itemStack != null) {
            if(itemStack.isItemEqual(Ic2Items.overclockerUpgrade)) {
               overclockerUpgradeCount += itemStack.stackSize;
            } else if(itemStack.isItemEqual(Ic2Items.transformerUpgrade)) {
               transformerUpgradeCount += itemStack.stackSize;
            } else if(itemStack.isItemEqual(Ic2Items.energyStorageUpgrade)) {
               energyStorageUpgradeCount += itemStack.stackSize;
            }
         }
      }

      if(overclockerUpgradeCount > 32) {
         overclockerUpgradeCount = 32;
      }

      if(transformerUpgradeCount > 10) {
         transformerUpgradeCount = 10;
      }

      double var6 = (double)this.progress / (double)this.operationLength;
      this.energyConsume = (int)((float)this.defaultEnergyConsume * Math.pow(1.6F, (float)overclockerUpgradeCount));
      this.operationLength = (int)Math.round((double)this.defaultOperationLength * Math.pow(0.7D, (double)overclockerUpgradeCount));
      super.maxInput = this.defaultMaxInput * (int)Math.pow(4.0D, (double)transformerUpgradeCount);
      super.maxEnergy = this.defaultEnergyStorage + energyStorageUpgradeCount * 10000 + super.maxInput - 1;
      super.tier = transformerUpgradeCount + 1;
      if(this.operationLength < 1) {
         this.operationLength = 1;
      }

      this.progress = (short)((int)Math.round(var6 * (double)this.operationLength));
   }

   public boolean provideEnergy() {
      if(super.inventory[super.fuelslot] != null && super.inventory[super.fuelslot].getItem() == Item.redstone) {
         super.energy += this.defaultEnergyConsume * this.defaultOperationLength;
         --super.inventory[super.fuelslot].stackSize;
         if(super.inventory[super.fuelslot].stackSize <= 0) {
            super.inventory[super.fuelslot] = null;
         }

         return true;
      } else {
         return super.provideEnergy();
      }
   }

   public void operate() {
      if(this.canOperate()) {
         ItemStack processResult;
         if(super.inventory[0].getItem().hasContainerItem()) {
            processResult = this.getResultFor(super.inventory[0], false).copy();
            super.inventory[0] = super.inventory[0].getItem().getContainerItemStack(super.inventory[0]);
         } else {
            processResult = this.getResultFor(super.inventory[0], true).copy();
         }

         if(super.inventory[0].stackSize <= 0) {
            super.inventory[0] = null;
         }

         if(super.inventory[2] == null) {
            super.inventory[2] = processResult;
         } else {
            super.inventory[2].stackSize += processResult.stackSize;
         }

      }
   }

   public boolean canOperate() {
      if(super.inventory[0] == null) {
         return false;
      } else {
         ItemStack processResult = this.getResultFor(super.inventory[0], false);
         return processResult == null?false:(super.inventory[2] == null?true:(!super.inventory[2].isItemEqual(processResult)?false:super.inventory[2].stackSize + processResult.stackSize <= super.inventory[2].getMaxStackSize()));
      }
   }

   public abstract ItemStack getResultFor(ItemStack var1, boolean var2);

   public abstract String getInvName();

   public ContainerIC2 getGuiContainer(EntityPlayer entityPlayer) {
      return new ContainerElectricMachine(entityPlayer, this);
   }

   public void onGuiClosed(EntityPlayer entityPlayer) {}

   public String getStartSoundFile() {
      return null;
   }

   public String getInterruptSoundFile() {
      return null;
   }

   public void onNetworkEvent(int event) {
      if(this.audioSource == null && this.getStartSoundFile() != null) {
         this.audioSource = IC2.audioManager.createSource(this, this.getStartSoundFile());
      }

      switch(event) {
      case 0:
         if(this.audioSource != null) {
            this.audioSource.play();
         }
         break;
      case 1:
         if(this.audioSource != null) {
            this.audioSource.stop();
            if(this.getInterruptSoundFile() != null) {
               IC2.audioManager.playOnce(this, this.getInterruptSoundFile());
            }
         }
         break;
      case 2:
         if(this.audioSource != null) {
            this.audioSource.stop();
         }
      }

   }

   public int getStartInventorySide(ForgeDirection side) {
      switch(TileEntityElectricMachine.NamelessClass1811794228.$SwitchMap$net$minecraftforge$common$ForgeDirection[side.ordinal()]) {
      case 1:
         return 1;
      case 2:
         return 0;
      default:
         return 2;
      }
   }

   public int getSizeInventorySide(ForgeDirection side) {
      return 1;
   }


   // $FF: synthetic class
   static class NamelessClass1811794228 {

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
