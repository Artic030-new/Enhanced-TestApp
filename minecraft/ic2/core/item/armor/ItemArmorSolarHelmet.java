package ic2.core.item.armor;

import ic2.api.IElectricItem;
import ic2.core.IItemTickListener;
import ic2.core.block.generator.tileentity.TileEntitySolarGenerator;
import ic2.core.item.ElectricItem;
import ic2.core.item.armor.ItemArmorUtility;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ItemArmorSolarHelmet extends ItemArmorUtility implements IItemTickListener {

   public ItemArmorSolarHelmet(int id, int index, int renderIndex) {
      super(id, index, renderIndex, 0);
      this.setMaxDamage(0);
   }

   public boolean onTick(EntityPlayer player, ItemStack itemStack) {
      return player.inventory.armorInventory[2] != null && player.inventory.armorInventory[2].getItem() instanceof IElectricItem && TileEntitySolarGenerator.isSunVisible(player.worldObj, (int)player.posX, (int)player.posY + 1, (int)player.posZ)?ElectricItem.charge(player.inventory.armorInventory[2], 1, Integer.MAX_VALUE, true, false) > 0:false;
   }

   public int getItemEnchantability() {
      return 0;
   }
}
