package ic2.core.block.machine.tileentity;

import ic2.core.block.machine.tileentity.TileEntityElectricMachine;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;

public class TileEntityElecFurnace extends TileEntityElectricMachine {

   public TileEntityElecFurnace() {
      super(3, 3, 130, 32);
   }

   public ItemStack getResultFor(ItemStack input, boolean adjustInput) {
      ItemStack result = FurnaceRecipes.smelting().getSmeltingResult(input);
      if(adjustInput && result != null) {
         --input.stackSize;
      }

      return result;
   }

   public String getInvName() {
      return "Electric Furnace";
   }

   public String getGuiClassName(EntityPlayer entityPlayer) {
      return "block.machine.gui.GuiElecFurnace";
   }

   public String getStartSoundFile() {
      return "Machines/Electro Furnace/ElectroFurnaceLoop.ogg";
   }

   public String getInterruptSoundFile() {
      return null;
   }
}
