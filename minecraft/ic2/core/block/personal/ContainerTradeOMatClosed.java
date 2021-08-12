package ic2.core.block.personal;

import ic2.core.ContainerIC2;
import ic2.core.block.personal.TileEntityTradeOMat;
import ic2.core.slot.SlotDisplay;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;

public class ContainerTradeOMatClosed extends ContainerIC2 {

   public TileEntityTradeOMat tileEntity;
   public int stock = -1;


   public ContainerTradeOMatClosed(EntityPlayer entityPlayer, TileEntityTradeOMat tileEntity) {
      this.tileEntity = tileEntity;
      tileEntity.updateStock();
      this.addSlotToContainer(new SlotDisplay(tileEntity, 0, 50, 19));
      this.addSlotToContainer(new SlotDisplay(tileEntity, 1, 50, 38));
      this.addSlotToContainer(new Slot(tileEntity, 2, 143, 17));
      this.addSlotToContainer(new Slot(tileEntity, 3, 143, 53));

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
         if(this.stock != this.tileEntity.stock) {
            icrafting.sendProgressBarUpdate(this, 0, this.tileEntity.stock & '\uffff');
            icrafting.sendProgressBarUpdate(this, 1, this.tileEntity.stock >>> 16);
         }
      }

   }

   public void updateProgressBar(int i, int j) {
      switch(i) {
      case 0:
         this.tileEntity.stock = this.tileEntity.stock & -65536 | j;
         break;
      case 1:
         this.tileEntity.stock = this.tileEntity.stock & '\uffff' | j << 16;
      }

   }

   public boolean canInteractWith(EntityPlayer entityplayer) {
      return this.tileEntity.isUseableByPlayer(entityplayer);
   }

   public int guiInventorySize() {
      return 4;
   }

   public int getInput() {
      return 2;
   }
}
