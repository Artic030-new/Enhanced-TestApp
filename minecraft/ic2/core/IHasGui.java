package ic2.core;

import ic2.core.ContainerIC2;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;

public interface IHasGui extends IInventory {

   ContainerIC2 getGuiContainer(EntityPlayer var1);

   String getGuiClassName(EntityPlayer var1);

   void onGuiClosed(EntityPlayer var1);
}
