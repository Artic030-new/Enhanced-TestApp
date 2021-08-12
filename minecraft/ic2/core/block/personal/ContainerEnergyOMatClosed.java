package ic2.core.block.personal;

import ic2.core.ContainerIC2;
import ic2.core.block.personal.TileEntityEnergyOMat;
import ic2.core.slot.SlotDisplay;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;

public class ContainerEnergyOMatClosed extends ContainerIC2 {

   public TileEntityEnergyOMat tileEntity;
   public int paidFor = -1;
   public int euOffer = -1;


   public ContainerEnergyOMatClosed(EntityPlayer entityPlayer, TileEntityEnergyOMat tileEntity) {
      this.tileEntity = tileEntity;
      this.addSlotToContainer(new SlotDisplay(tileEntity, 0, 50, 19));
      this.addSlotToContainer(new Slot(tileEntity, 1, 143, 17));

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

         if(this.euOffer != this.tileEntity.euOffer) {
            icrafting.sendProgressBarUpdate(this, 2, this.tileEntity.euOffer & '\uffff');
            icrafting.sendProgressBarUpdate(this, 3, this.tileEntity.euOffer >>> 16);
         }
      }

      this.paidFor = this.tileEntity.paidFor;
      this.euOffer = this.tileEntity.euOffer;
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
         this.tileEntity.euOffer = this.tileEntity.euOffer & -65536 | j;
         break;
      case 3:
         this.tileEntity.euOffer = this.tileEntity.euOffer & '\uffff' | j << 16;
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
