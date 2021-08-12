package ic2.core.block.machine;

import ic2.core.ContainerIC2;
import ic2.core.block.machine.tileentity.TileEntityElecFurnace;
import ic2.core.block.machine.tileentity.TileEntityElectricMachine;
import ic2.core.item.ItemUpgradeModule;
import ic2.core.slot.SlotCustom;
import ic2.core.slot.SlotDischarge;
import ic2.core.slot.SlotOutput;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotFurnace;

public class ContainerElectricMachine extends ContainerIC2 {

   public TileEntityElectricMachine tileEntity;
   public float lastChargeLevel = -1.0F;
   public float lastProgress = -1.0F;


   public ContainerElectricMachine(EntityPlayer entityPlayer, TileEntityElectricMachine tileEntity) {
      this.tileEntity = tileEntity;
      this.addSlotToContainer(new Slot(tileEntity, 0, 56, 17));
      this.addSlotToContainer(new SlotDischarge(tileEntity, Integer.MAX_VALUE, 1, 56, 53));
      if(tileEntity instanceof TileEntityElecFurnace) {
         this.addSlotToContainer(new SlotFurnace(entityPlayer, tileEntity, 2, 116, 35));
      } else {
         this.addSlotToContainer(new SlotOutput(entityPlayer, tileEntity, 2, 116, 35));
      }

      int j;
      for(j = 0; j < 4; ++j) {
         this.addSlotToContainer(new SlotCustom(tileEntity, new Object[]{ItemUpgradeModule.class}, 3 + j, 152, 8 + j * 18));
      }

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
      float chargeLevel = this.tileEntity.getChargeLevel();
      float progress = this.tileEntity.getProgress();

      for(int i = 0; i < super.crafters.size(); ++i) {
         ICrafting icrafting = (ICrafting)super.crafters.get(i);
         if(this.lastChargeLevel != chargeLevel) {
            icrafting.sendProgressBarUpdate(this, 0, (short)((int)(chargeLevel * 32767.0F)));
         }

         if(this.lastProgress != progress) {
            icrafting.sendProgressBarUpdate(this, 1, (short)((int)(progress * 32767.0F)));
         }
      }

      this.lastChargeLevel = chargeLevel;
      this.lastProgress = progress;
   }

   public void updateProgressBar(int i, int j) {
      switch(i) {
      case 0:
         this.tileEntity.setChargeLevel((float)j / 32767.0F);
         break;
      case 1:
         this.tileEntity.setProgress((float)j / 32767.0F);
      }

   }

   public boolean canInteractWith(EntityPlayer entityplayer) {
      return this.tileEntity.isUseableByPlayer(entityplayer);
   }

   public int guiInventorySize() {
      return 7;
   }

   public int getInput() {
      return 0;
   }
}
