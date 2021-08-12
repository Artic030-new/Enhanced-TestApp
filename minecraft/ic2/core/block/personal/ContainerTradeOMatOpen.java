package ic2.core.block.personal;

import ic2.core.ContainerIC2;
import ic2.core.block.personal.TileEntityTradeOMat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;

public class ContainerTradeOMatOpen extends ContainerIC2 {

   public TileEntityTradeOMat tileEntity;
   public int totalTradeCount = -1;


   public ContainerTradeOMatOpen(EntityPlayer entityPlayer, TileEntityTradeOMat tileEntity) {
      this.tileEntity = tileEntity;
      this.addSlotToContainer(new Slot(tileEntity, 0, 24, 17));
      this.addSlotToContainer(new Slot(tileEntity, 1, 24, 53));
      this.addSlotToContainer(new Slot(tileEntity, 2, 80, 17));
      this.addSlotToContainer(new Slot(tileEntity, 3, 80, 53));

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
         if(this.totalTradeCount != this.tileEntity.totalTradeCount) {
            icrafting.sendProgressBarUpdate(this, 0, this.tileEntity.totalTradeCount & '\uffff');
            icrafting.sendProgressBarUpdate(this, 1, this.tileEntity.totalTradeCount >>> 16);
         }
      }

      this.totalTradeCount = this.tileEntity.totalTradeCount;
   }

   public void updateProgressBar(int i, int j) {
      switch(i) {
      case 0:
         this.tileEntity.totalTradeCount = this.tileEntity.totalTradeCount & -65536 | j;
         break;
      case 1:
         this.tileEntity.totalTradeCount = this.tileEntity.totalTradeCount & '\uffff' | j << 16;
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
