package ic2.advancedmachines.common;

import ic2.advancedmachines.common.AdvancedMachines;
import ic2.advancedmachines.common.ContainerCentrifugeExtractor;
import ic2.advancedmachines.common.TileEntityAdvancedMachine;
import ic2.api.Ic2Recipes;
import java.util.List;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;

public class TileEntityCentrifugeExtractor extends TileEntityAdvancedMachine {

   public TileEntityCentrifugeExtractor() {
      super("Centrifuge Extractor", "%5d M/S", 1, new int[]{1}, new int[]{2, 3, 4});
   }

   public Container getGuiContainer(InventoryPlayer player) {
      return new ContainerCentrifugeExtractor(player, this);
   }

   protected List getResultMap() {
      return Ic2Recipes.getExtractorRecipes();
   }

   public ItemStack getResultFor(ItemStack input, boolean adjustOutput) {
      return Ic2Recipes.getExtractorOutputFor(input, adjustOutput);
   }

   public int getUpgradeSlotsStartSlot() {
      return 5;
   }

   public String getStartSoundFile() {
      return AdvancedMachines.advExtcSound;
   }

   public String getInterruptSoundFile() {
      return AdvancedMachines.interruptSound;
   }
}
