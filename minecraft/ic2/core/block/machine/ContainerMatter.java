package ic2.core.block.machine;

import ic2.core.ContainerIC2;
import ic2.core.block.machine.tileentity.TileEntityMatter;
import ic2.core.slot.SlotMatterScrap;
import ic2.core.slot.SlotOutput;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;

public class ContainerMatter extends ContainerIC2 {

   public TileEntityMatter tileEntity;
   public int energy = -1;
   public int scrap = -1;


   public ContainerMatter(EntityPlayer entityPlayer, TileEntityMatter tileEntity) {
      this.tileEntity = tileEntity;
      this.addSlotToContainer(new SlotMatterScrap(tileEntity, 0, 114, 54));
      this.addSlotToContainer(new SlotOutput(entityPlayer, tileEntity, 1, 114, 18));

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

         if(this.scrap != this.tileEntity.scrap) {
            icrafting.sendProgressBarUpdate(this, 2, this.tileEntity.scrap & '\uffff');
            icrafting.sendProgressBarUpdate(this, 3, this.tileEntity.scrap >>> 16);
         }
      }

      this.energy = this.tileEntity.energy;
      this.scrap = this.tileEntity.scrap;
   }

   public void updateProgressBar(int i, int j) {
      switch(i) {
      case 0:
         this.tileEntity.energy = this.tileEntity.energy & -65536 | j;
         break;
      case 1:
         this.tileEntity.energy = this.tileEntity.energy & '\uffff' | j << 16;
         break;
      case 2:
         this.tileEntity.scrap = this.tileEntity.scrap & -65536 | j;
         break;
      case 3:
         this.tileEntity.scrap = this.tileEntity.scrap & '\uffff' | j << 16;
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
