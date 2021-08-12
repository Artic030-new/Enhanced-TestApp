package ic2.advancedmachines.common;

import ic2.advancedmachines.common.AdvancedMachines;
import ic2.advancedmachines.common.ContainerSingularityCompressor;
import ic2.advancedmachines.common.TileEntityAdvancedMachine;
import ic2.api.Ic2Recipes;
import java.util.List;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;

public class TileEntitySingularityCompressor extends TileEntityAdvancedMachine {

   public TileEntitySingularityCompressor() {
      super("Singularity Compressor", "%6d PSI", 10, new int[]{1}, new int[]{2});
   }

   public Container getGuiContainer(InventoryPlayer player) {
      return new ContainerSingularityCompressor(player, this);
   }

   protected List getResultMap() {
      return Ic2Recipes.getCompressorRecipes();
   }

   public ItemStack getResultFor(ItemStack input, boolean adjustOutput) {
      return Ic2Recipes.getCompressorOutputFor(input, adjustOutput);
   }

   public int getUpgradeSlotsStartSlot() {
      return 3;
   }

   public String getStartSoundFile() {
      return AdvancedMachines.advCompSound;
   }

   public String getInterruptSoundFile() {
      return AdvancedMachines.interruptSound;
   }
}
