package gravisuite;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IItemTickListener {

   boolean onTick(EntityPlayer player, ItemStack stack);
}
