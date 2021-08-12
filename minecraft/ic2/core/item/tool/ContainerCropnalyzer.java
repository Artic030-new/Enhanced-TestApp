package ic2.core.item.tool;

import ic2.core.ContainerIC2;
import ic2.core.IC2;
import ic2.core.item.ItemCropSeed;
import ic2.core.item.tool.HandHeldCropnalyzer;
import ic2.core.slot.SlotCustom;
import ic2.core.slot.SlotDischarge;
import ic2.core.util.StackUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ContainerCropnalyzer extends ContainerIC2 {

   public HandHeldCropnalyzer cropnalyzer;


   public ContainerCropnalyzer(EntityPlayer entityPlayer, HandHeldCropnalyzer cropnalyzer) {
      this.cropnalyzer = cropnalyzer;
      this.addSlotToContainer(new SlotCustom(cropnalyzer, new Object[]{ItemCropSeed.class}, 0, 8, 7));
      this.addSlotToContainer(new SlotCustom(cropnalyzer, new Object[0], 1, 41, 7));
      this.addSlotToContainer(new SlotDischarge(cropnalyzer, 2, 152, 7));

      for(int j = 0; j < 9; ++j) {
         this.addSlotToContainer(new Slot(entityPlayer.inventory, j, 8 + j * 18, 142));
      }

   }

   public int guiInventorySize() {
      return 3;
   }

   public int getInput() {
      return 0;
   }

   public void updateProgressBar(int index, int value) {}

   public boolean canInteractWith(EntityPlayer entityplayer) {
      return this.cropnalyzer.isUseableByPlayer(entityplayer);
   }

   public ItemStack slotClick(int slot, int button, int par3, EntityPlayer entityPlayer) {
      if(IC2.platform.isSimulating() && slot == -999 && (button == 0 || button == 1)) {
         ItemStack itemStackSlot = entityPlayer.inventory.getItemStack();
         if(itemStackSlot != null) {
            NBTTagCompound nbtTagCompoundSlot = StackUtil.getOrCreateNbtData(itemStackSlot);
            if(this.cropnalyzer.matchesUid(nbtTagCompoundSlot.getInteger("uid"))) {
               entityPlayer.closeScreen();
            }
         }
      }

      return super.slotClick(slot, button, par3, entityPlayer);
   }

   public void onCraftGuiClosed(EntityPlayer entityPlayer) {
      this.cropnalyzer.onGuiClosed(entityPlayer);
      super.onCraftGuiClosed(entityPlayer);
   }
}
