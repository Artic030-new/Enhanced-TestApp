package ic2.advancedmachines.common;

import ic2.advancedmachines.common.AdvancedMachines;
import ic2.advancedmachines.common.IC2AudioSource;
import ic2.advancedmachines.common.TileEntityBaseMachine;
import ic2.api.Direction;
import ic2.api.network.NetworkHelper;
import java.util.List;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.ISidedInventory;

public abstract class TileEntityAdvancedMachine extends TileEntityBaseMachine implements ISidedInventory {

   private static final int MAX_PROGRESS = 4000;
   private static final int MAX_ENERGY = 5000;
   private static final int MAX_SPEED = 7500;
   private static final int MAX_INPUT = 32;
   private String inventoryName;
   private int[] inputs;
   private int[] outputs;
   short speed;
   short progress;
   private String dataFormat;
   private int dataScaling;
   private IC2AudioSource audioSource;
   private static final int EventStart = 0;
   private static final int EventInterrupt = 1;
   private static final int EventStop = 2;
   private int energyConsume = 2;
   private int acceleration = 1;
   private int maxSpeed;

   public TileEntityAdvancedMachine(String invName, String dataForm, int dataScale, int[] inputSlots, int[] outputSlots) {
      super(inputSlots.length + outputSlots.length + 6, 5000, 32);
      this.inventoryName = invName;
      this.dataFormat = dataForm;
      this.dataScaling = dataScale;
      this.inputs = inputSlots;
      this.outputs = outputSlots;
      this.speed = 0;
      this.progress = 0;
   }

   public void readFromNBT(NBTTagCompound tagCompound) {
      super.readFromNBT(tagCompound);
      this.speed = tagCompound.getShort("speed");
      this.progress = tagCompound.getShort("progress");
   }

   public void writeToNBT(NBTTagCompound tagCompound) {
      super.writeToNBT(tagCompound);
      tagCompound.setShort("speed", this.speed);
      tagCompound.setShort("progress", this.progress);
   }

   public String getInvName() {
      return this.inventoryName;
   }

   public int gaugeProgressScaled(int var1) {
      return var1 * this.progress / 4000;
   }

   public int gaugeFuelScaled(int var1) {
      return var1 * super.energy / super.maxEnergy;
   }

   public void updateEntity() {
      super.updateEntity();
      if(!super.worldObj.isRemote) {
         boolean newItemProcessing = false;
         if(super.energy <= super.maxEnergy) {
            this.getPowerFromFuelSlot();
         }

         boolean isActive = this.getActive();
         if(this.progress >= 4000) {
            this.operate();
            newItemProcessing = true;
            this.progress = 0;
            isActive = false;
            NetworkHelper.initiateTileEntityEvent(this, 2, true);
         }

         boolean bCanOperate = this.canOperate();
         if(super.energy > 0 && (bCanOperate || this.isRedstonePowered())) {
            this.setOverclockRates();
            if(this.speed < this.maxSpeed) {
               this.speed = (short)(this.speed + this.acceleration);
               super.energy -= this.energyConsume;
            } else {
               this.speed = (short)this.maxSpeed;
               super.energy -= AdvancedMachines.defaultEnergyConsume;
            }

            isActive = true;
            NetworkHelper.initiateTileEntityEvent(this, 0, true);
         } else {
            boolean wasWorking = this.speed != 0;
            this.speed = (short)(this.speed - Math.min(this.speed, 4));
            if(wasWorking && this.speed == 0) {
               NetworkHelper.initiateTileEntityEvent(this, 1, true);
            }
         }

         if(isActive && this.progress != 0) {
            if(!bCanOperate || this.speed == 0) {
               if(!bCanOperate) {
                  this.progress = 0;
               }

               isActive = false;
            }
         } else if(bCanOperate) {
            if(this.speed != 0) {
               isActive = true;
            }
         } else {
            this.progress = 0;
         }

         if(isActive && bCanOperate) {
            this.progress = (short)(this.progress + this.speed / 30);
         }

         if(newItemProcessing) {
            this.onInventoryChanged();
         }

         if(isActive != this.getActive()) {
            super.worldObj.markBlockForRenderUpdate(super.xCoord, super.yCoord, super.zCoord);
            this.setActive(isActive);
         }

      }
   }

   public int injectEnergy(Direction var1, int var2) {
      this.setOverclockRates();
      return super.injectEnergy(var1, var2);
   }

   private void operate() {
      if(this.canOperate()) {
         ItemStack resultStack = this.getResultFor(super.inventory[this.inputs[0]], true).copy();
         int[] stackSizeSpaceAvailableInOutput = new int[this.outputs.length];
         int resultMaxStackSize = resultStack.getMaxStackSize();

         int index;
         for(index = 0; index < this.outputs.length; ++index) {
            if(super.inventory[this.outputs[index]] == null) {
               stackSizeSpaceAvailableInOutput[index] = resultMaxStackSize;
            } else if(super.inventory[this.outputs[index]].isItemEqual(resultStack)) {
               stackSizeSpaceAvailableInOutput[index] = resultMaxStackSize - super.inventory[this.outputs[index]].stackSize;
            }
         }

         for(index = 0; index < stackSizeSpaceAvailableInOutput.length; ++index) {
            if(stackSizeSpaceAvailableInOutput[index] > 0) {
               int stackSizeToStash = Math.min(resultStack.stackSize, stackSizeSpaceAvailableInOutput[index]);
               if(super.inventory[this.outputs[index]] == null) {
                  super.inventory[this.outputs[index]] = resultStack;
                  break;
               }

               super.inventory[this.outputs[index]].stackSize += stackSizeToStash;
               resultStack.stackSize -= stackSizeToStash;
               if(resultStack.stackSize <= 0) {
                  break;
               }
            }
         }

         this.onFinishedProcessingItem();
         if(super.inventory[this.inputs[0]].stackSize <= 0) {
            super.inventory[this.inputs[0]] = null;
         }
      }

   }

   public void onFinishedProcessingItem() {}

   private boolean canOperate() {
      if(super.inventory[this.inputs[0]] == null) {
         return false;
      } else {
         ItemStack resultStack = this.getResultFor(super.inventory[this.inputs[0]], false);
         if(resultStack == null) {
            return false;
         } else {
            int resultMaxStackSize = resultStack.getMaxStackSize();
            int freeSpaceOutputSlots = 0;

            for(int index = 0; index < this.outputs.length; ++index) {
               int curOutputSlot = this.outputs[index];
               if(super.inventory[curOutputSlot] == null) {
                  freeSpaceOutputSlots += resultMaxStackSize;
               } else if(super.inventory[curOutputSlot].isItemEqual(resultStack)) {
                  freeSpaceOutputSlots += resultMaxStackSize - super.inventory[curOutputSlot].stackSize;
               }
            }

            return freeSpaceOutputSlots >= resultStack.stackSize;
         }
      }
   }

   public abstract ItemStack getResultFor(ItemStack input, boolean adjustOutput);

   public abstract Container getGuiContainer(InventoryPlayer var1);

   public int getStartInventorySide(ForgeDirection side) {
	   switch(side) {
      case DOWN:
         return 0;
      case UP:
         return this.inputs[0];
      default:
         return this.outputs[0];
      }
   }

   public int getSizeInventorySide(ForgeDirection side) {
	   switch(side) {
      case DOWN:
         return 1;
      case UP:
         return this.inputs.length;
      default:
         return this.outputs.length;
      }
   }

   public String printFormattedData() {
      return String.format(this.dataFormat, new Object[]{Integer.valueOf(this.speed * this.dataScaling)});
   }

   public void invalidate() {
      if(this.audioSource != null) {
         IC2AudioSource.removeSource(this.audioSource);
         this.audioSource = null;
      }

      super.invalidate();
   }

   protected String getStartSoundFile() {
      return null;
   }

   protected String getInterruptSoundFile() {
      return null;
   }

   public void onNetworkEvent(int event) {
      super.onNetworkEvent(event);
      if(super.worldObj.isRemote) {
         if(this.audioSource == null && this.getStartSoundFile() != null) {
            this.audioSource = new IC2AudioSource(this, this.getStartSoundFile());
         }

         switch(event) {
         case 0:
            this.setActiveWithoutNotify(true);
            if(this.audioSource != null) {
               this.audioSource.play();
            }
            break;
         case 1:
            this.setActiveWithoutNotify(false);
            if(this.audioSource != null) {
               this.audioSource.stop();
               if(this.getInterruptSoundFile() != null) {
                  IC2AudioSource.playOnce(this, this.getInterruptSoundFile());
               }
            }
            break;
         case 2:
            this.setActiveWithoutNotify(false);
            if(this.audioSource != null) {
               this.audioSource.stop();
            }
         }
      }

      NetworkHelper.announceBlockUpdate(super.worldObj, super.xCoord, super.yCoord, super.zCoord);
   }

   public abstract int getUpgradeSlotsStartSlot();

   public void setOverclockRates() {
      int overclockerUpgradeCount = 0;
      int transformerUpgradeCount = 0;
      int energyStorageUpgradeCount = 0;

      for(int i = 0; i < 4; ++i) {
         ItemStack itemStack = super.inventory[this.getUpgradeSlotsStartSlot() + i];
         if(itemStack != null) {
            if(itemStack.isItemEqual(AdvancedMachines.overClockerStack)) {
               overclockerUpgradeCount += itemStack.stackSize;
            } else if(itemStack.isItemEqual(AdvancedMachines.transformerStack)) {
               transformerUpgradeCount += itemStack.stackSize;
            } else if(itemStack.isItemEqual(AdvancedMachines.energyStorageUpgradeStack)) {
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

      this.energyConsume = (int)((double)AdvancedMachines.defaultEnergyConsume * Math.pow(AdvancedMachines.overClockEnergyRatio, (double)overclockerUpgradeCount));
      this.acceleration = (int)((double)AdvancedMachines.defaultAcceleration * Math.pow(AdvancedMachines.overClockAccelRatio, (double)overclockerUpgradeCount) / 2.0D);
      this.maxSpeed = 7500 + overclockerUpgradeCount * AdvancedMachines.overClockSpeedBonus;
      super.maxInput = 32 * (int)Math.pow(AdvancedMachines.overLoadInputRatio, (double)transformerUpgradeCount);
      super.maxEnergy = 5000 + energyStorageUpgradeCount * 5000 + super.maxInput - 1;
      super.tier = 1 + transformerUpgradeCount;
   }
}
