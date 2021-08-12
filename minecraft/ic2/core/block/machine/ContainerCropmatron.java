package ic2.core.block.machine;

import ic2.core.ContainerIC2;
import ic2.core.Ic2Items;
import ic2.core.block.machine.tileentity.TileEntityCropmatron;
import ic2.core.slot.SlotCustom;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;

public class ContainerCropmatron extends ContainerIC2 {

   public TileEntityCropmatron tileEntity;
   public int energy = -1;


   public ContainerCropmatron(EntityPlayer entityPlayer, TileEntityCropmatron tileEntity) {
      this.tileEntity = tileEntity;
      this.addSlotToContainer(new SlotCustom(tileEntity, new Object[]{Ic2Items.fertilizer.getItem()}, 0, 62, 20));
      this.addSlotToContainer(new SlotCustom(tileEntity, new Object[]{Ic2Items.fertilizer.getItem()}, 1, 62, 38));
      this.addSlotToContainer(new SlotCustom(tileEntity, new Object[]{Ic2Items.fertilizer.getItem()}, 2, 62, 56));
      this.addSlotToContainer(new SlotCustom(tileEntity, new Object[]{Ic2Items.hydratingCell.getItem()}, 3, 98, 20));
      this.addSlotToContainer(new SlotCustom(tileEntity, new Object[]{Ic2Items.hydratingCell.getItem()}, 4, 98, 38));
      this.addSlotToContainer(new SlotCustom(tileEntity, new Object[]{Ic2Items.hydratingCell.getItem()}, 5, 98, 56));
      this.addSlotToContainer(new SlotCustom(tileEntity, new Object[]{Ic2Items.weedEx.getItem()}, 6, 134, 20));
      this.addSlotToContainer(new SlotCustom(tileEntity, new Object[]{Ic2Items.weedEx.getItem()}, 7, 134, 38));
      this.addSlotToContainer(new SlotCustom(tileEntity, new Object[]{Ic2Items.weedEx.getItem()}, 8, 134, 56));

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
         if(this.energy != this.tileEntity.energy) {
            icrafting.sendProgressBarUpdate(this, 1, this.tileEntity.energy & '\uffff');
            icrafting.sendProgressBarUpdate(this, 2, this.tileEntity.energy >>> 16);
         }
      }

      this.energy = this.tileEntity.energy;
   }

   public void updateProgressBar(int i, int j) {
      switch(i) {
      case 1:
         this.tileEntity.energy = this.tileEntity.energy & -65536 | j;
         break;
      case 2:
         this.tileEntity.energy = this.tileEntity.energy & '\uffff' | j << 16;
      }

   }

   public boolean canInteractWith(EntityPlayer entityplayer) {
      return this.tileEntity.isUseableByPlayer(entityplayer);
   }

   public int guiInventorySize() {
      return 9;
   }

   public int getInput() {
      return 0;
   }
}
