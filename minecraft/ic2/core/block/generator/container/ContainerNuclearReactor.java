package ic2.core.block.generator.container;

import ic2.core.ContainerIC2;
import ic2.core.block.generator.tileentity.TileEntityNuclearReactor;
import ic2.core.slot.SlotReactor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;

public class ContainerNuclearReactor extends ContainerIC2 {

   public TileEntityNuclearReactor tileEntity;
   public short output = -1;
   public int heat = -1;
   public short size;


   public ContainerNuclearReactor(EntityPlayer entityPlayer, TileEntityNuclearReactor tileEntity) {
      this.tileEntity = tileEntity;
      this.size = tileEntity.getReactorSize();
      int startX = 89 - 9 * this.size;
      byte startY = 18;
      int x = 0;
      int y = 0;

      int j;
      for(j = 0; j < 54; ++j) {
         if(x < this.size) {
            this.addSlotToContainer(new SlotReactor(tileEntity, j, startX + 18 * x, startY + 18 * y));
         }

         ++x;
         if(x >= 9) {
            ++y;
            x = 0;
         }
      }

      for(j = 0; j < 3; ++j) {
         for(int k = 0; k < 9; ++k) {
            this.addSlotToContainer(new Slot(entityPlayer.inventory, k + j * 9 + 9, 8 + k * 18, 140 + j * 18));
         }
      }

      for(j = 0; j < 9; ++j) {
         this.addSlotToContainer(new Slot(entityPlayer.inventory, j, 8 + j * 18, 198));
      }

   }

   public void detectAndSendChanges() {
      super.detectAndSendChanges();

      for(int i = 0; i < super.crafters.size(); ++i) {
         ICrafting icrafting = (ICrafting)super.crafters.get(i);
         if(this.output != this.tileEntity.output) {
            icrafting.sendProgressBarUpdate(this, 0, this.tileEntity.output);
         }

         if(this.heat != this.tileEntity.heat) {
            icrafting.sendProgressBarUpdate(this, 1, this.tileEntity.heat & '\uffff');
            icrafting.sendProgressBarUpdate(this, 2, this.tileEntity.heat >>> 16);
         }
      }

      this.output = this.tileEntity.output;
      this.heat = this.tileEntity.heat;
   }

   public void updateProgressBar(int i, int j) {
      switch(i) {
      case 0:
         this.tileEntity.output = (short)j;
         break;
      case 1:
         this.tileEntity.heat = this.tileEntity.heat & -65536 | j;
         break;
      case 2:
         this.tileEntity.heat = this.tileEntity.heat & '\uffff' | j << 16;
      }

   }

   public boolean canInteractWith(EntityPlayer entityplayer) {
      return this.tileEntity.isUseableByPlayer(entityplayer);
   }

   public int guiInventorySize() {
      return 6 * this.size;
   }

   public int getInput() {
      return -1;
   }
}
