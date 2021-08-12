package ic2.core.item;

import ic2.api.IElectricItem;
import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.item.ElectricItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemBattery extends ElectricItem {

   public ItemBattery(int id, int sprite, int maxCharge, int transferLimit, int tier) {
      super(id, sprite);
      this.setNoRepair();
      super.maxCharge = maxCharge;
      super.transferLimit = transferLimit;
      super.tier = tier;
   }

   public boolean canProvideEnergy() {
      return true;
   }

   public int getEmptyItemId() {
      return super.itemID == Ic2Items.chargedReBattery.itemID?Ic2Items.reBattery.itemID:super.getEmptyItemId();
   }

   public int getIconFromDamage(int i) {
      return i <= 1?super.iconIndex + 4:(i <= 8?super.iconIndex + 3:(i <= 14?super.iconIndex + 2:(i <= 20?super.iconIndex + 1:super.iconIndex)));
   }

   public int getIconFromChargeLevel(float chargeLevel) {
      return this.getIconFromDamage(1 + (int)Math.round((1.0D - (double)chargeLevel) * (double)(this.getMaxDamage() - 2)));
   }

   public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer entityplayer) {
      if(IC2.platform.isSimulating() && itemStack.itemID == Ic2Items.chargedReBattery.itemID) {
         boolean transferred = false;

         for(int i = 0; i < 9; ++i) {
            ItemStack stack = entityplayer.inventory.mainInventory[i];
            if(stack != null && Item.itemsList[stack.itemID] instanceof IElectricItem && !(Item.itemsList[stack.itemID] instanceof ItemBattery)) {
               IElectricItem item = (IElectricItem)stack.getItem();
               int transfer = discharge(itemStack, 2 * super.transferLimit, item.getTier(), true, true);
               transfer = charge(stack, transfer, super.tier, true, false);
               discharge(itemStack, transfer, item.getTier(), true, false);
               if(transfer > 0) {
                  transferred = true;
               }
            }
         }

         if(transferred && !IC2.platform.isRendering()) {
            entityplayer.openContainer.detectAndSendChanges();
         }
      }

      return itemStack;
   }
}
