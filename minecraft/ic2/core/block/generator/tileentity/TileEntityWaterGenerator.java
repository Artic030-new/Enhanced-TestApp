package ic2.core.block.generator.tileentity;

import ic2.core.ContainerIC2;
import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.block.generator.container.ContainerWaterGenerator;
import ic2.core.block.generator.tileentity.TileEntityBaseGenerator;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.ISidedInventory;
import net.minecraftforge.liquids.LiquidContainerRegistry;
import net.minecraftforge.liquids.LiquidStack;

public class TileEntityWaterGenerator extends TileEntityBaseGenerator implements ISidedInventory {

   public static Random randomizer = new Random();
   public int ticker;
   public int water = 0;
   public int microStorage = 0;
   public int maxWater = 2000;


   public TileEntityWaterGenerator() {
      super(2, 2, 2);
      super.production = 2;
      this.ticker = randomizer.nextInt(this.tickRate());
   }

   public void onLoaded() {
      super.onLoaded();
      this.updateWaterCount();
   }

   public int gaugeFuelScaled(int i) {
      return super.fuel <= 0?0:super.fuel * i / this.maxWater;
   }

   public boolean gainFuel() {
      if(super.inventory[1] != null && this.maxWater - super.fuel >= 500) {
         if(super.inventory[1].itemID == Item.bucketWater.itemID) {
            super.production = 1;
            super.fuel += 500;
            super.inventory[1].itemID = Item.bucketEmpty.itemID;
            return true;
         }

         if(super.inventory[1].itemID == Ic2Items.waterCell.itemID) {
            super.production = 2;
            super.fuel += 500;
            --super.inventory[1].stackSize;
            if(super.inventory[1].stackSize <= 0) {
               super.inventory[1] = null;
            }

            return true;
         }

         LiquidStack liquid = LiquidContainerRegistry.getLiquidForFilledItem(super.inventory[1]);
         if(liquid != null && liquid.itemID == Block.waterStill.blockID) {
            super.fuel += 500;
            if(super.inventory[1].getItem().hasContainerItem()) {
               super.production = 1;
               super.inventory[1] = super.inventory[1].getItem().getContainerItemStack(super.inventory[1]);
            } else {
               super.production = 2;
               --super.inventory[1].stackSize;
               if(super.inventory[1].stackSize <= 0) {
                  super.inventory[1] = null;
               }
            }

            return true;
         }
      } else if(super.fuel <= 0) {
         this.flowPower();
         super.production = this.microStorage / 100;
         this.microStorage -= super.production * 100;
         if(super.production > 0) {
            ++super.fuel;
            return true;
         }

         return false;
      }

      return false;
   }

   public boolean gainFuelSub(ItemStack stack) {
      return false;
   }

   public boolean needsFuel() {
      return super.fuel <= this.maxWater;
   }

   public void flowPower() {
      if(this.ticker++ % this.tickRate() == 0) {
         this.updateWaterCount();
      }

      this.water = this.water * IC2.energyGeneratorWater / 100;
      if(this.water > 0) {
         this.microStorage += this.water;
      }

   }

   public void updateWaterCount() {
      int count = 0;

      for(int x = super.xCoord - 1; x < super.xCoord + 2; ++x) {
         for(int y = super.yCoord - 1; y < super.yCoord + 2; ++y) {
            for(int z = super.zCoord - 1; z < super.zCoord + 2; ++z) {
               if(super.worldObj.getBlockId(x, y, z) == Block.waterMoving.blockID || super.worldObj.getBlockId(x, y, z) == Block.waterStill.blockID) {
                  ++count;
               }
            }
         }
      }

      this.water = count;
   }

   public String getInvName() {
      return "Water Mill";
   }

   public String getGuiClassName(EntityPlayer entityPlayer) {
      return "block.generator.gui.GuiWaterGenerator";
   }

   public int tickRate() {
      return 128;
   }

   public String getOperationSoundFile() {
      return "Generators/WatermillLoop.ogg";
   }

   public boolean delayActiveUpdate() {
      return true;
   }

   public ContainerIC2 getGuiContainer(EntityPlayer entityPlayer) {
      return new ContainerWaterGenerator(entityPlayer, this);
   }

   public int getStartInventorySide(ForgeDirection side) {
      switch(TileEntityWaterGenerator.NamelessClass1025329513.$SwitchMap$net$minecraftforge$common$ForgeDirection[side.ordinal()]) {
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
   static class NamelessClass1025329513 {

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
