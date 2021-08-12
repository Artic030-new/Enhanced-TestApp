package ic2.core.block.personal;

import ic2.core.ContainerIC2;
import ic2.core.block.personal.TileEntityEnergyOMat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;

public class ContainerEnergyOMatOpen extends ContainerIC2 {

   public TileEntityEnergyOMat tileEntity;
   public int paidFor = -1;
   public int euBuffer = -1;


   public ContainerEnergyOMatOpen(EntityPlayer entityPlayer, TileEntityEnergyOMat tileEntity) {
      this.tileEntity = tileEntity;
      this.addSlotToContainer(new Slot(tileEntity, 0, 24, 17));
      this.addSlotToContainer(new Slot(tileEntity, 2, 24, 53));
      this.addSlotToContainer(new Slot(tileEntity, 1, 80, 17));

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
         if(this.paidFor != this.tileEntity.paidFor) {
            icrafting.sendProgressBarUpdate(this, 0, this.tileEntity.paidFor & '\uffff');
            icrafting.sendProgressBarUpdate(this, 1, this.tileEntity.paidFor >>> 16);
         }

         if(this.euBuffer != this.tileEntity.euBuffer) {
            icrafting.sendProgressBarUpdate(this, 2, this.tileEntity.euBuffer & '\uffff');
            icrafting.sendProgressBarUpdate(this, 3, this.tileEntity.euBuffer >>> 16);
         }
      }

      this.paidFor = this.tileEntity.paidFor;
   }

   public void updateProgressBar(int i, int j) {
      switch(i) {
      case 0:
         this.tileEntity.paidFor = this.tileEntity.paidFor & -65536 | j;
         break;
      case 1:
         this.tileEntity.paidFor = this.tileEntity.paidFor & '\uffff' | j << 16;
         break;
      case 2:
         this.tileEntity.euBuffer = this.tileEntity.euBuffer & -65536 | j;
         break;
      case 3:
         this.tileEntity.euBuffer = this.tileEntity.euBuffer & '\uffff' | j << 16;
      }

   }

   public boolean canInteractWith(EntityPlayer entityplayer) {
      return this.tileEntity.isUseableByPlayer(entityplayer);
   }

   public int guiInventorySize() {
      return 3;
   }

   public int getInput() {
      return 1;
   }
}
