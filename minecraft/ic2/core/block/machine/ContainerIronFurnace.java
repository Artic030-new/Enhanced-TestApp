package ic2.core.block.machine;

import ic2.core.ContainerIC2;
import ic2.core.block.machine.tileentity.TileEntityIronFurnace;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotFurnace;

public class ContainerIronFurnace extends ContainerIC2 {

   public TileEntityIronFurnace tileEntity;
   public short progress = -1;
   public int fuel = -1;
   public int maxFuel = -1;


   public ContainerIronFurnace(EntityPlayer entityPlayer, TileEntityIronFurnace tileEntity) {
      this.tileEntity = tileEntity;
      this.addSlotToContainer(new Slot(tileEntity, 0, 56, 17));
      this.addSlotToContainer(new Slot(tileEntity, 1, 56, 53));
      this.addSlotToContainer(new SlotFurnace(entityPlayer, tileEntity, 2, 116, 35));

      int j;
      for(j = 0; j < 3; ++j) {
         for(int k = 0; k < 9; ++k) {
            this.addSlotToContainer(new Slot(entityPlayer.inventory, k + j * 9 + 9, 8 + k * 18, 84 + j * 18));
         }
      }

      for(j = 0; j < 9; ++j) {
         this.addSlotToContainer(new Slot(entityPlayer.inventory, j, 8 + j * 18, 142));
      }

   }

   public void detectAndSendChanges() {
      super.detectAndSendChanges();

      for(int i = 0; i < super.crafters.size(); ++i) {
         ICrafting icrafting = (ICrafting)super.crafters.get(i);
         if(this.progress != this.tileEntity.progress) {
            icrafting.sendProgressBarUpdate(this, 0, this.tileEntity.progress);
         }

         if(this.fuel != this.tileEntity.fuel) {
            icrafting.sendProgressBarUpdate(this, 1, this.tileEntity.fuel);
            icrafting.sendProgressBarUpdate(this, 2, this.tileEntity.fuel);
         }

         if(this.maxFuel != this.tileEntity.maxFuel) {
            icrafting.sendProgressBarUpdate(this, 3, this.tileEntity.maxFuel);
            icrafting.sendProgressBarUpdate(this, 4, this.tileEntity.maxFuel);
         }
      }

      this.progress = this.tileEntity.progress;
      this.fuel = this.tileEntity.fuel;
      this.maxFuel = this.tileEntity.maxFuel;
   }

   public void updateProgressBar(int i, int j) {
      switch(i) {
      case 0:
         this.tileEntity.progress = (short)j;
         break;
      case 1:
         this.tileEntity.fuel = this.tileEntity.fuel & -65536 | j;
         break;
      case 2:
         this.tileEntity.fuel = this.tileEntity.fuel & '\uffff' | j << 16;
         break;
      case 3:
         this.tileEntity.maxFuel = this.tileEntity.maxFuel & -65536 | j;
         break;
      case 4:
         this.tileEntity.maxFuel = this.tileEntity.maxFuel & '\uffff' | j << 16;
      }

   }

   public boolean canInteractWith(EntityPlayer entityplayer) {
      return this.tileEntity.isUseableByPlayer(entityplayer);
   }

   public int guiInventorySize() {
      return 3;
   }

   public int getInput() {
      return 0;
   }
}
