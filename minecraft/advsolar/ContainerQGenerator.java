package advsolar;

import advsolar.TileEntityQGenerator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;

public class ContainerQGenerator extends Container {

   private TileEntityQGenerator tileentity;

   public ContainerQGenerator(InventoryPlayer inventoryplayer, TileEntityQGenerator tileentityqgenerator) {
      this.tileentity = tileentityqgenerator;

      int j;
      for(j = 0; j < 3; ++j) {
         for(int k = 0; k < 9; ++k) {
            this.addSlotToContainer(new Slot(inventoryplayer, k + j * 9 + 9, 8 + k * 18, 110 + j * 18));
         }
      }

      for(j = 0; j < 9; ++j) {
         this.addSlotToContainer(new Slot(inventoryplayer, j, 8 + j * 18, 168));
      }

   }

   public void detectAndSendChanges() {
      super.detectAndSendChanges();

      for(int i = 0; i < super.crafters.size(); ++i) {
         ICrafting icrafting = (ICrafting)super.crafters.get(i);
         icrafting.sendProgressBarUpdate(this, 0, this.tileentity.production);
         icrafting.sendProgressBarUpdate(this, 1, this.tileentity.maxPacketSize);
         icrafting.sendProgressBarUpdate(this, 2, this.tileentity.active?1:0);
      }

   }

   public void updateProgressBar(int key, int value) {
      if(key == 0) {
         this.tileentity.production = value;
      }

      if(key == 1) {
         this.tileentity.maxPacketSize = value;
      }

      if(key == 2) {
         this.tileentity.active = value != 0;
      }

   }

   public boolean canInteractWith(EntityPlayer player) {
      return this.tileentity.isUseableByPlayer(player);
   }
}
