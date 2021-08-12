package ic2.core.block.machine.tileentity;

import ic2.core.ContainerIC2;
import ic2.core.IHasGui;
import ic2.core.block.machine.ContainerIronFurnace;
import ic2.core.block.machine.tileentity.TileEntityMachine;
import ic2.core.item.ItemFuelCanFilled;
import ic2.core.util.StackUtil;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.ISidedInventory;
import net.minecraftforge.liquids.LiquidContainerRegistry;
import net.minecraftforge.liquids.LiquidStack;

public class TileEntityIronFurnace extends TileEntityMachine implements IHasGui, ISidedInventory {

   public int fuel = 0;
   public int maxFuel = 0;
   public short progress = 0;
   public final short operationLength = 160;


   public TileEntityIronFurnace() {
      super(3);
   }

   public void readFromNBT(NBTTagCompound nbttagcompound) {
      super.readFromNBT(nbttagcompound);

      try {
         this.fuel = nbttagcompound.getInteger("fuel");
      } catch (Throwable var4) {
         this.fuel = nbttagcompound.getShort("fuel");
      }

      try {
         this.maxFuel = nbttagcompound.getInteger("maxFuel");
      } catch (Throwable var3) {
         this.maxFuel = nbttagcompound.getShort("maxFuel");
      }

      this.progress = nbttagcompound.getShort("progress");
   }

   public void writeToNBT(NBTTagCompound nbttagcompound) {
      super.writeToNBT(nbttagcompound);
      nbttagcompound.setInteger("fuel", this.fuel);
      nbttagcompound.setInteger("maxFuel", this.maxFuel);
      nbttagcompound.setShort("progress", this.progress);
   }

   public int gaugeProgressScaled(int i) {
      return this.progress * i / 160;
   }

   public int gaugeFuelScaled(int i) {
      if(this.maxFuel == 0) {
         this.maxFuel = this.fuel;
         if(this.maxFuel == 0) {
            this.maxFuel = 160;
         }
      }

      return this.fuel * i / this.maxFuel;
   }

   public void updateEntity() {
      super.updateEntity();
      boolean wasOperating = this.isBurning();
      boolean needsInvUpdate = false;
      if(this.fuel <= 0 && this.canOperate()) {
         this.fuel = this.maxFuel = getFuelValueFor(super.inventory[1]);
         if(this.fuel > 0) {
            if(super.inventory[1].getItem().hasContainerItem()) {
               super.inventory[1] = super.inventory[1].getItem().getContainerItemStack(super.inventory[1]);
            } else {
               --super.inventory[1].stackSize;
            }

            if(super.inventory[1].stackSize <= 0) {
               super.inventory[1] = null;
            }

            needsInvUpdate = true;
         }
      }

      if(this.isBurning() && this.canOperate()) {
         ++this.progress;
         if(this.progress >= 160) {
            this.progress = 0;
            this.operate();
            needsInvUpdate = true;
         }
      } else {
         this.progress = 0;
      }

      if(this.fuel > 0) {
         --this.fuel;
      }

      if(wasOperating != this.isBurning()) {
         this.setActive(this.isBurning());
         needsInvUpdate = true;
      }

      if(needsInvUpdate) {
         this.onInventoryChanged();
      }

   }

   public void operate() {
      if(this.canOperate()) {
         ItemStack itemstack = this.getResultFor(super.inventory[0]);
         if(super.inventory[2] == null) {
            super.inventory[2] = itemstack.copy();
         } else {
            super.inventory[2].stackSize += itemstack.stackSize;
         }

         if(super.inventory[0].getItem().hasContainerItem()) {
            super.inventory[0] = super.inventory[0].getItem().getContainerItemStack(super.inventory[0]);
         } else {
            --super.inventory[0].stackSize;
         }

         if(super.inventory[0].stackSize <= 0) {
            super.inventory[0] = null;
         }

      }
   }

   public boolean isBurning() {
      return this.fuel > 0;
   }

   public boolean canOperate() {
      if(super.inventory[0] == null) {
         return false;
      } else {
         ItemStack itemstack = this.getResultFor(super.inventory[0]);
         return itemstack == null?false:(super.inventory[2] == null?true:(!super.inventory[2].isItemEqual(itemstack)?false:super.inventory[2].stackSize + itemstack.stackSize <= super.inventory[2].getMaxStackSize()));
      }
   }

   public static int getFuelValueFor(ItemStack itemstack) {
      if(itemstack == null) {
         return 0;
      } else {
         int itemIndex = itemstack.getItem().itemID;
         LiquidStack liquid = LiquidContainerRegistry.getLiquidForFilledItem(itemstack);
         if(liquid != null && liquid.itemID == Block.lavaStill.blockID) {
            return 2000;
         } else if(itemstack.getItem() instanceof ItemFuelCanFilled) {
            NBTTagCompound data = StackUtil.getOrCreateNbtData(itemstack);
            if(itemstack.getItemDamage() > 0) {
               data.setInteger("value", itemstack.getItemDamage());
            }

            return data.getInteger("value") * 2;
         } else {
            return TileEntityFurnace.getItemBurnTime(itemstack);
         }
      }
   }

   public ItemStack getResultFor(ItemStack itemstack) {
      return FurnaceRecipes.smelting().getSmeltingResult(itemstack);
   }

   public String getInvName() {
      return "Iron Furnace";
   }

   public ContainerIC2 getGuiContainer(EntityPlayer entityPlayer) {
      return new ContainerIronFurnace(entityPlayer, this);
   }

   public String getGuiClassName(EntityPlayer entityPlayer) {
      return "block.machine.gui.GuiIronFurnace";
   }

   public void onGuiClosed(EntityPlayer entityPlayer) {}

   public int getStartInventorySide(ForgeDirection side) {
      switch(TileEntityIronFurnace.NamelessClass132119430.$SwitchMap$net$minecraftforge$common$ForgeDirection[side.ordinal()]) {
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
   static class NamelessClass132119430 {

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
