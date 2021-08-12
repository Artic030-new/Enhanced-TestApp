package ic2.core.block.machine;

import ic2.core.ContainerIC2;
import ic2.core.block.machine.tileentity.TileEntityMiner;
import ic2.core.item.tool.ItemElectricToolDDrill;
import ic2.core.item.tool.ItemElectricToolDrill;
import ic2.core.item.tool.ItemScanner;
import ic2.core.slot.SlotCustom;
import ic2.core.slot.SlotDischarge;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;

public class ContainerMiner extends ContainerIC2 {

   public TileEntityMiner tileEntity;
   public short miningTicker = -1;
   public int energy = -1;


   public ContainerMiner(EntityPlayer entityPlayer, TileEntityMiner tileEntity) {
      this.tileEntity = tileEntity;
      this.addSlotToContainer(new SlotDischarge(tileEntity, tileEntity.tier, 0, 81, 59));
      this.addSlotToContainer(new SlotCustom(tileEntity, new Object[]{ItemScanner.class}, 1, 117, 22));
      this.addSlotToContainer(new SlotCustom(tileEntity, new Object[]{Block.class}, 2, 81, 22));
      this.addSlotToContainer(new SlotCustom(tileEntity, new Object[]{ItemElectricToolDrill.class, ItemElectricToolDDrill.class}, 3, 45, 22));

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
         if(this.miningTicker != this.tileEntity.miningTicker) {
            icrafting.sendProgressBarUpdate(this, 0, this.tileEntity.miningTicker);
         }

         if(this.energy != this.tileEntity.energy) {
            icrafting.sendProgressBarUpdate(this, 1, this.tileEntity.energy & '\uffff');
            icrafting.sendProgressBarUpdate(this, 2, this.tileEntity.energy >>> 16);
         }
      }

      this.miningTicker = this.tileEntity.miningTicker;
      this.energy = this.tileEntity.energy;
   }

   public void updateProgressBar(int i, int j) {
      switch(i) {
      case 0:
         this.tileEntity.miningTicker = (short)j;
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
      return 4;
   }

   public int getInput() {
      return 0;
   }
}
