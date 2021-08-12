package ic2.core.block.machine;

import ic2.core.ContainerIC2;
import ic2.core.Ic2Items;
import ic2.core.block.machine.tileentity.TileEntityPump;
import ic2.core.slot.SlotCustom;
import ic2.core.slot.SlotDischarge;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;

public class ContainerPump extends ContainerIC2 {

   public TileEntityPump tileEntity;
   public short pumpCharge = -1;
   public int energy = -1;


   public ContainerPump(EntityPlayer entityPlayer, TileEntityPump tileEntity) {
      this.tileEntity = tileEntity;
      this.addSlotToContainer(new SlotCustom(tileEntity, new Object[]{Ic2Items.cell}, 0, 62, 17));
      this.addSlotToContainer(new SlotDischarge(tileEntity, tileEntity.tier, 1, 62, 53));

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
         if(this.pumpCharge != this.tileEntity.pumpCharge) {
            icrafting.sendProgressBarUpdate(this, 0, this.tileEntity.pumpCharge);
         }

         if(this.energy != this.tileEntity.energy) {
            icrafting.sendProgressBarUpdate(this, 1, this.tileEntity.energy & '\uffff');
            icrafting.sendProgressBarUpdate(this, 2, this.tileEntity.energy >>> 16);
         }
      }

      this.pumpCharge = this.tileEntity.pumpCharge;
      this.energy = this.tileEntity.energy;
   }

   public void updateProgressBar(int i, int j) {
      switch(i) {
      case 0:
         this.tileEntity.pumpCharge = (short)j;
         break;
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
      return 2;
   }

   public int getInput() {
      return 1;
   }
}
