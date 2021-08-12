package ic2.core.block.machine;

import ic2.core.ContainerIC2;
import ic2.core.block.machine.tileentity.TileEntityInduction;
import ic2.core.slot.SlotDischarge;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotFurnace;

public class ContainerInduction extends ContainerIC2 {

   public TileEntityInduction tileEntity;
   public short progress = -1;
   public int energy = -1;
   public short heat = -1;


   public ContainerInduction(EntityPlayer entityPlayer, TileEntityInduction tileEntity) {
      this.tileEntity = tileEntity;
      this.addSlotToContainer(new Slot(tileEntity, 0, 47, 17));
      this.addSlotToContainer(new Slot(tileEntity, 1, 63, 17));
      this.addSlotToContainer(new SlotDischarge(tileEntity, tileEntity.tier, 2, 56, 53));
      this.addSlotToContainer(new SlotFurnace(entityPlayer, tileEntity, 3, 113, 35));
      this.addSlotToContainer(new SlotFurnace(entityPlayer, tileEntity, 4, 131, 35));

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

         if(this.energy != this.tileEntity.energy) {
            icrafting.sendProgressBarUpdate(this, 1, this.tileEntity.energy & '\uffff');
            icrafting.sendProgressBarUpdate(this, 2, this.tileEntity.energy >>> 16);
         }

         if(this.heat != this.tileEntity.heat) {
            icrafting.sendProgressBarUpdate(this, 3, this.tileEntity.heat);
         }
      }

      this.progress = this.tileEntity.progress;
      this.energy = this.tileEntity.energy;
      this.heat = this.tileEntity.heat;
   }

   public void updateProgressBar(int i, int j) {
      switch(i) {
      case 0:
         this.tileEntity.progress = (short)j;
         break;
      case 1:
         this.tileEntity.energy = this.tileEntity.energy & -65536 | j;
         break;
      case 2:
         this.tileEntity.energy = this.tileEntity.energy & '\uffff' | j << 16;
         break;
      case 3:
         this.tileEntity.heat = (short)j;
      }

   }

   public boolean canInteractWith(EntityPlayer entityplayer) {
      return this.tileEntity.isUseableByPlayer(entityplayer);
   }

   public int guiInventorySize() {
      return 5;
   }

   public int getInput() {
      return this.tileEntity.getStackInSlot(0) != null?1:0;
   }
}
