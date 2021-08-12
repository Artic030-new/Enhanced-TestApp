package ic2.core.block.generator.tileentity;

import ic2.core.ContainerIC2;
import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.block.generator.container.ContainerBaseGenerator;
import ic2.core.block.generator.tileentity.TileEntityBaseGenerator;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.ISidedInventory;
import net.minecraftforge.liquids.ILiquidTank;
import net.minecraftforge.liquids.ITankContainer;
import net.minecraftforge.liquids.LiquidContainerRegistry;
import net.minecraftforge.liquids.LiquidEvent;
import net.minecraftforge.liquids.LiquidStack;
import net.minecraftforge.liquids.LiquidTank;
import net.minecraftforge.liquids.LiquidEvent.LiquidSpilledEvent;

public class TileEntityGeoGenerator extends TileEntityBaseGenerator implements ISidedInventory, ITankContainer {

   public int maxLava = 24000;


   public TileEntityGeoGenerator() {
      super(2, IC2.energyGeneratorGeo, Math.max(IC2.energyGeneratorGeo, 32));
   }

   public int gaugeFuelScaled(int i) {
      return super.fuel <= 0?0:super.fuel * i / this.maxLava;
   }

   public boolean gainFuel() {
      if(super.inventory[1] != null && this.maxLava - super.fuel >= 1000) {
         if(super.inventory[1].itemID == Item.bucketLava.itemID) {
            super.fuel += 1000;
            super.inventory[1].itemID = Item.bucketEmpty.itemID;
            return true;
         } else if(super.inventory[1].itemID == Ic2Items.lavaCell.itemID) {
            super.fuel += 1000;
            --super.inventory[1].stackSize;
            if(super.inventory[1].stackSize <= 0) {
               super.inventory[1] = null;
            }

            return true;
         } else {
            LiquidStack liquid = LiquidContainerRegistry.getLiquidForFilledItem(super.inventory[1]);
            if(liquid != null && liquid.itemID == Block.lavaStill.blockID) {
               super.fuel += 1000;
               if(super.inventory[1].getItem().hasContainerItem()) {
                  super.inventory[1] = super.inventory[1].getItem().getContainerItemStack(super.inventory[1]);
               } else {
                  --super.inventory[1].stackSize;
                  if(super.inventory[1].stackSize <= 0) {
                     super.inventory[1] = null;
                  }
               }

               return true;
            } else {
               return false;
            }
         }
      } else {
         return false;
      }
   }

   public boolean gainFuelSub(ItemStack stack) {
      return false;
   }

   public boolean needsFuel() {
      return super.fuel <= this.maxLava;
   }

   public int distributeLava(int amount) {
      int need = this.maxLava - super.fuel;
      if(need > amount) {
         need = amount;
      }

      amount -= need;
      super.fuel += need / 2;
      return amount;
   }

   public String getInvName() {
      return IC2.platform.isRendering()?"Geothermal Generator":"Geoth. Generator";
   }

   public String getOperationSoundFile() {
      return "Generators/GeothermalLoop.ogg";
   }

   public ContainerIC2 getGuiContainer(EntityPlayer entityPlayer) {
      return new ContainerBaseGenerator(entityPlayer, this);
   }

   public String getGuiClassName(EntityPlayer entityPlayer) {
      return "block.generator.gui.GuiGeoGenerator";
   }

   public void onBlockBreak(int a, int b) {
      LiquidEvent.fireEvent(new LiquidSpilledEvent(new LiquidStack(Block.lavaStill.blockID, super.fuel), super.worldObj, super.xCoord, super.yCoord, super.zCoord));
   }

   public int getStartInventorySide(ForgeDirection side) {
      switch(TileEntityGeoGenerator.NamelessClass2083201373.$SwitchMap$net$minecraftforge$common$ForgeDirection[side.ordinal()]) {
      case 1:
         return 1;
      default:
         return 0;
      }
   }

   public int getSizeInventorySide(ForgeDirection side) {
      return 1;
   }

   public int fill(ForgeDirection from, LiquidStack resource, boolean doFill) {
      return this.fill(0, resource, doFill);
   }

   public int fill(int tankIndex, LiquidStack resource, boolean doFill) {
      if(resource.itemID != Block.lavaStill.blockID) {
         return 0;
      } else {
         int toAdd = Math.min(resource.amount, (this.maxLava - super.fuel) / 1);
         if(doFill) {
            super.fuel += toAdd / 1;
         }

         return toAdd;
      }
   }

   public LiquidStack drain(ForgeDirection from, int maxEmpty, boolean doDrain) {
      return this.drain(0, maxEmpty, doDrain);
   }

   public LiquidStack drain(int tankIndex, int maxEmpty, boolean doDrain) {
      return null;
   }

   public LiquidTank[] getTanks(ForgeDirection side) {
      LiquidTank tank = new LiquidTank(new LiquidStack(Block.lavaStill.blockID, super.fuel * 1), this.maxLava * 1, this);
      tank.setTankPressure(0);
      return new LiquidTank[]{tank};
   }

   public ILiquidTank getTank(ForgeDirection direction, LiquidStack type) {
      return type != null && type.itemID == Block.lavaStill.blockID?this.getTanks(ForgeDirection.UNKNOWN)[0]:null;
   }

   // $FF: synthetic class
   static class NamelessClass2083201373 {

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
