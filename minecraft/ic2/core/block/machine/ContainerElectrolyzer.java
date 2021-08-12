package ic2.core.block.machine;

import ic2.core.ContainerIC2;
import ic2.core.Ic2Items;
import ic2.core.block.machine.tileentity.TileEntityElectrolyzer;
import ic2.core.slot.SlotCustom;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;

public class ContainerElectrolyzer extends ContainerIC2 {

   public TileEntityElectrolyzer tileEntity;
   public short energy = -1;


   public ContainerElectrolyzer(EntityPlayer entityPlayer, TileEntityElectrolyzer tileEntity) {
      this.tileEntity = tileEntity;
      this.addSlotToContainer(new SlotCustom(tileEntity, new Object[]{Ic2Items.waterCell}, 0, 53, 35));
      this.addSlotToContainer(new SlotCustom(tileEntity, new Object[]{Ic2Items.electrolyzedWaterCell}, 1, 112, 35));

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
            icrafting.sendProgressBarUpdate(this, 0, this.tileEntity.energy);
         }
      }

      this.energy = this.tileEntity.energy;
   }

   public void updateProgressBar(int i, int j) {
      switch(i) {
      case 0:
         this.tileEntity.energy = (short)j;
      default:
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
