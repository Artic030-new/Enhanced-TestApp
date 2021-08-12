package ic2.core.block.generator.container;

import ic2.core.ContainerIC2;
import ic2.core.block.generator.tileentity.TileEntityBaseGenerator;
import ic2.core.slot.SlotCharge;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;

public class ContainerBaseGenerator extends ContainerIC2 {

   public TileEntityBaseGenerator tileEntity;
   public short storage = -1;
   public int fuel = -1;


   public ContainerBaseGenerator(EntityPlayer entityPlayer, TileEntityBaseGenerator tileEntity) {
      this.tileEntity = tileEntity;
      this.addSlotToContainer(new SlotCharge(tileEntity, tileEntity.tier, 0, 65, 17));
      this.addSlotToContainer(new Slot(tileEntity, 1, 65, 53));

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
         if(this.storage != this.tileEntity.storage) {
            icrafting.sendProgressBarUpdate(this, 0, this.tileEntity.storage);
         }

         if(this.fuel != this.tileEntity.fuel) {
            icrafting.sendProgressBarUpdate(this, 1, this.tileEntity.fuel & '\uffff');
            icrafting.sendProgressBarUpdate(this, 2, this.tileEntity.fuel >>> 16);
         }
      }

      this.storage = this.tileEntity.storage;
      this.fuel = this.tileEntity.fuel;
   }

   public void updateProgressBar(int i, int j) {
      switch(i) {
      case 0:
         this.tileEntity.storage = (short)j;
         break;
      case 1:
         this.tileEntity.fuel = this.tileEntity.fuel & -65536 | j;
         break;
      case 2:
         this.tileEntity.fuel = this.tileEntity.fuel & '\uffff' | j << 16;
      }

   }

   public boolean canInteractWith(EntityPlayer entityplayer) {
      return this.tileEntity.isUseableByPlayer(entityplayer);
   }

   public int guiInventorySize() {
      return 2;
   }

   public int getInput() {
      return 1;
   }
}
