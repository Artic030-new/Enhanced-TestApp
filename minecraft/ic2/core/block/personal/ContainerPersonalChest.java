package ic2.core.block.personal;

import ic2.core.ContainerIC2;
import ic2.core.block.personal.TileEntityPersonalChest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;

public class ContainerPersonalChest extends ContainerIC2 {

   public TileEntityPersonalChest tileEntity;


   public ContainerPersonalChest(EntityPlayer entityPlayer, TileEntityPersonalChest tileEntity) {
      this.tileEntity = tileEntity;
      tileEntity.openChest();

      int x;
      int x1;
      for(x = 0; x < 6; ++x) {
         for(x1 = 0; x1 < 9; ++x1) {
            this.addSlotToContainer(new Slot(tileEntity, x1 + x * 9, 8 + x1 * 18, 18 + x * 18));
         }
      }

      for(x = 0; x < 3; ++x) {
         for(x1 = 0; x1 < 9; ++x1) {
            this.addSlotToContainer(new Slot(entityPlayer.inventory, 9 + x1 + x * 9, 8 + x1 * 18, 140 + x * 18));
         }
      }

      for(x = 0; x < 9; ++x) {
         this.addSlotToContainer(new Slot(entityPlayer.inventory, x, 8 + x * 18, 198));
      }

   }

   public void updateProgressBar(int index, int value) {}

   public boolean canInteractWith(EntityPlayer entityplayer) {
      return this.tileEntity.isUseableByPlayer(entityplayer);
   }

   public int guiInventorySize() {
      return 54;
   }

   public int getInput() {
      return -1;
   }

   public void onCraftGuiClosed(EntityPlayer entityplayer) {
      this.tileEntity.closeChest();
   }
}
