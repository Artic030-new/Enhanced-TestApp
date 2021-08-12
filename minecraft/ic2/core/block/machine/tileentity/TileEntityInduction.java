package ic2.core.block.machine.tileentity;

import ic2.api.Direction;
import ic2.core.ContainerIC2;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.block.machine.ContainerInduction;
import ic2.core.block.machine.tileentity.TileEntityElecMachine;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.ISidedInventory;

public class TileEntityInduction extends TileEntityElecMachine implements IHasGui, ISidedInventory {

   public int soundTicker;
   public static short maxHeat = 10000;
   public short heat = 0;
   public short progress = 0;
   private static final int inputSlot = 0;
   private static final int fuelSlot = 2;
   private static final int outputSlot = 3;


   public TileEntityInduction() {
      super(5, 2, maxHeat, 128, 2);
      this.soundTicker = IC2.random.nextInt(64);
   }

   public String getInvName() {
      return IC2.platform.isRendering()?"Induction Furnace":"InductionFurnace";
   }

   public void readFromNBT(NBTTagCompound nbttagcompound) {
      super.readFromNBT(nbttagcompound);
      this.heat = nbttagcompound.getShort("heat");
      this.progress = nbttagcompound.getShort("progress");
   }

   public void writeToNBT(NBTTagCompound nbttagcompound) {
      super.writeToNBT(nbttagcompound);
      nbttagcompound.setShort("heat", this.heat);
      nbttagcompound.setShort("progress", this.progress);
   }

   public String getHeat() {
      return "" + this.heat * 100 / maxHeat + "%";
   }

   public int gaugeProgressScaled(int i) {
      return i * this.progress / 4000;
   }

   public int gaugeFuelScaled(int i) {
      return i * super.energy / super.maxEnergy;
   }

   public void updateEntity() {
      super.updateEntity();
      boolean needsInvUpdate = false;
      if(super.energy <= super.maxEnergy) {
         needsInvUpdate = this.provideEnergy();
      }

      boolean newActive = this.getActive();
      if(this.heat == 0) {
         newActive = false;
      }

      if(this.progress >= 4000) {
         this.operate();
         needsInvUpdate = true;
         this.progress = 0;
         newActive = false;
      }

      boolean canOperate = this.canOperate();
      if(super.energy > 0 && (canOperate || this.isRedstonePowered())) {
         --super.energy;
         if(this.heat < maxHeat) {
            ++this.heat;
         }

         newActive = true;
      } else {
         this.heat = (short)(this.heat - Math.min(this.heat, 4));
      }

      if(newActive && this.progress != 0) {
         if(!canOperate || super.energy < 15) {
            if(!canOperate) {
               this.progress = 0;
            }

            newActive = false;
         }
      } else if(canOperate) {
         if(super.energy >= 15) {
            newActive = true;
         }
      } else {
         this.progress = 0;
      }

      if(newActive && canOperate) {
         this.progress = (short)(this.progress + this.heat / 30);
         super.energy -= 15;
      }

      if(needsInvUpdate) {
         this.onInventoryChanged();
      }

      if(newActive != this.getActive()) {
         this.setActive(newActive);
      }

   }

   public void operate() {
      this.operate(0, 3);
      this.operate(1, 4);
   }

   public void operate(int input, int output) {
      if(this.canOperate(input, output)) {
         ItemStack itemstack = this.getResultFor(super.inventory[input]);
         if(super.inventory[output] == null) {
            super.inventory[output] = itemstack.copy();
         } else {
            super.inventory[output].stackSize += itemstack.stackSize;
         }

         if(super.inventory[input].getItem().hasContainerItem()) {
            super.inventory[input] = super.inventory[input].getItem().getContainerItemStack(super.inventory[input]);
         } else {
            --super.inventory[input].stackSize;
         }

         if(super.inventory[input].stackSize <= 0) {
            super.inventory[input] = null;
         }

      }
   }

   public boolean canOperate() {
      return this.canOperate(0, 3) || this.canOperate(1, 4);
   }

   public boolean canOperate(int input, int output) {
      if(super.inventory[input] == null) {
         return false;
      } else {
         ItemStack itemstack = this.getResultFor(super.inventory[input]);
         return itemstack == null?false:super.inventory[output] == null || super.inventory[output].isItemEqual(itemstack) && super.inventory[output].stackSize + itemstack.stackSize <= itemstack.getMaxStackSize();
      }
   }

   public ItemStack getResultFor(ItemStack itemstack) {
      return FurnaceRecipes.smelting().getSmeltingResult(itemstack);
   }

   public int injectEnergy(Direction directionFrom, int amount) {
      if(amount > 128) {
         IC2.explodeMachineAt(super.worldObj, super.xCoord, super.yCoord, super.zCoord);
         return 0;
      } else {
         super.energy += amount;
         int re = 0;
         if(super.energy > super.maxEnergy) {
            re = super.energy - super.maxEnergy;
            super.energy = super.maxEnergy;
         }

         return re;
      }
   }

   public ContainerIC2 getGuiContainer(EntityPlayer entityPlayer) {
      return new ContainerInduction(entityPlayer, this);
   }

   public String getGuiClassName(EntityPlayer entityPlayer) {
      return "block.machine.gui.GuiInduction";
   }

   public void onGuiClosed(EntityPlayer entityPlayer) {}

   public int getStartInventorySide(ForgeDirection side) {
      switch(TileEntityInduction.NamelessClass1921076.$SwitchMap$net$minecraftforge$common$ForgeDirection[side.ordinal()]) {
      case 1:
         return 2;
      case 2:
         return 0;
      default:
         return 3;
      }
   }

   public int getSizeInventorySide(ForgeDirection side) {
      switch(TileEntityInduction.NamelessClass1921076.$SwitchMap$net$minecraftforge$common$ForgeDirection[side.ordinal()]) {
      case 1:
         return 1;
      default:
         return 2;
      }
   }

   public float getWrenchDropRate() {
      return 0.8F;
   }


   // $FF: synthetic class
   static class NamelessClass1921076 {

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
