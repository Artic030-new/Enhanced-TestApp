package ic2.core.block.wiring;

import ic2.core.ContainerIC2;
import ic2.core.block.wiring.TileEntityElectricBlock;
import ic2.core.slot.SlotCharge;
import ic2.core.slot.SlotDischarge;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;

public class ContainerElectricBlock extends ContainerIC2 {

   public TileEntityElectricBlock tileEntity;
   public int energy = -1;


   public ContainerElectricBlock(EntityPlayer entityPlayer, TileEntityElectricBlock tileEntity) {
      this.tileEntity = tileEntity;
      this.addSlotToContainer(new SlotCharge(tileEntity, tileEntity.tier, 0, 56, 17));
      this.addSlotToContainer(new SlotDischarge(tileEntity, tileEntity.tier, 1, 56, 53));

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
            icrafting.sendProgressBarUpdate(this, 0, this.tileEntity.energy & '\uffff');
            icrafting.sendProgressBarUpdate(this, 1, this.tileEntity.energy >>> 16);
         }
      }

      this.energy = this.tileEntity.energy;
   }

   public void updateProgressBar(int i, int j) {
      switch(i) {
      case 0:
         this.tileEntity.energy = this.tileEntity.energy & -65536 | j;
         break;
      case 1:
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
      return 0;
   }
}
