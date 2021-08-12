package ic2.core.block.generator.tileentity;

import ic2.core.ContainerIC2;
import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.block.generator.container.ContainerBaseGenerator;
import ic2.core.block.generator.tileentity.TileEntityBaseGenerator;
import ic2.core.block.machine.tileentity.TileEntityIronFurnace;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.ISidedInventory;

public class TileEntityGenerator extends TileEntityBaseGenerator implements ISidedInventory {

   public int itemFuelTime = 0;


   public TileEntityGenerator() {
      super(2, IC2.energyGeneratorBase, 4000);
   }

   public int gaugeFuelScaled(int i) {
      if(super.fuel <= 0) {
         return 0;
      } else {
         if(this.itemFuelTime <= 0) {
            this.itemFuelTime = super.fuel;
         }

         int r = super.fuel * i / this.itemFuelTime;
         if(r > i) {
            r = i;
         }

         return r;
      }
   }

   public boolean gainFuel() {
      if(super.inventory[1] == null) {
         return false;
      } else if(super.inventory[1].itemID == Item.bucketLava.itemID) {
         return false;
      } else {
         int value = TileEntityIronFurnace.getFuelValueFor(super.inventory[1]) / 4;
         if(super.inventory[1].isItemEqual(Ic2Items.scrap) && !IC2.enableBurningScrap) {
            value = 0;
         }

         if(value <= 0) {
            return false;
         } else {
            super.fuel += value;
            this.itemFuelTime = value;
            if(super.inventory[1].getItem().hasContainerItem()) {
               super.inventory[1] = super.inventory[1].getItem().getContainerItemStack(super.inventory[1]);
            } else {
               --super.inventory[1].stackSize;
            }

            if(super.inventory[1].stackSize == 0) {
               super.inventory[1] = null;
            }

            return true;
         }
      }
   }

   public String getInvName() {
      return "Generator";
   }

   public String getGuiClassName(EntityPlayer entityPlayer) {
      return "block.generator.gui.GuiGenerator";
   }

   public boolean isConverting() {
      return super.fuel > 0;
   }

   public String getOperationSoundFile() {
      return "Generators/GeneratorLoop.ogg";
   }

   public ContainerIC2 getGuiContainer(EntityPlayer entityPlayer) {
      return new ContainerBaseGenerator(entityPlayer, this);
   }

   public int getStartInventorySide(ForgeDirection side) {
      switch(TileEntityGenerator.NamelessClass2144473672.$SwitchMap$net$minecraftforge$common$ForgeDirection[side.ordinal()]) {
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
   static class NamelessClass2144473672 {

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
