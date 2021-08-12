package ic2.core.block.generator.container;

import ic2.core.ContainerIC2;
import ic2.core.block.generator.tileentity.TileEntitySolarGenerator;
import ic2.core.slot.SlotCharge;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;

public class ContainerSolarGenerator extends ContainerIC2 {

   public TileEntitySolarGenerator tileEntity;
   public boolean sunIsVisible = false;
   public boolean initialized = false;


   public ContainerSolarGenerator(EntityPlayer entityPlayer, TileEntitySolarGenerator tileEntity) {
      this.tileEntity = tileEntity;
      this.addSlotToContainer(new SlotCharge(tileEntity, tileEntity.tier, 0, 80, 26));

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
         if(this.sunIsVisible != this.tileEntity.sunIsVisible || !this.initialized) {
            icrafting.sendProgressBarUpdate(this, 0, this.tileEntity.sunIsVisible?1:0);
            this.initialized = true;
         }
      }

      this.sunIsVisible = this.tileEntity.sunIsVisible;
   }

   public void updateProgressBar(int i, int j) {
      switch(i) {
      case 0:
         this.tileEntity.sunIsVisible = j != 0;
      default:
      }
   }

   public boolean canInteractWith(EntityPlayer entityplayer) {
      return this.tileEntity.isUseableByPlayer(entityplayer);
   }

   public int guiInventorySize() {
      return 1;
   }

   public int getInput() {
      return 0;
   }
}
