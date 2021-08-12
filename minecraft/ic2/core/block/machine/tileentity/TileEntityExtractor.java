package ic2.core.block.machine.tileentity;

import ic2.api.Ic2Recipes;
import ic2.core.Ic2Items;
import ic2.core.block.machine.tileentity.TileEntityElectricMachine;
import ic2.core.util.StackUtil;
import java.util.List;
import java.util.Vector;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class TileEntityExtractor extends TileEntityElectricMachine {

   public static List recipes = new Vector();


   public TileEntityExtractor() {
      super(3, 2, 400, 32);
   }

   public static void init() {
      if(Ic2Items.rubberSapling != null) {
         Ic2Recipes.addExtractorRecipe(Ic2Items.rubberSapling, Ic2Items.rubber);
      }

      Ic2Recipes.addExtractorRecipe(Ic2Items.resin, StackUtil.copyWithSize(Ic2Items.rubber, 3));
      Ic2Recipes.addExtractorRecipe(Ic2Items.bioCell, Ic2Items.biofuelCell);
      Ic2Recipes.addExtractorRecipe(Ic2Items.hydratedCoalCell, Ic2Items.coalfuelCell);
      Ic2Recipes.addExtractorRecipe(Ic2Items.waterCell, Ic2Items.hydratingCell);
   }

   public ItemStack getResultFor(ItemStack itemStack, boolean adjustInput) {
      return Ic2Recipes.getExtractorOutputFor(itemStack, adjustInput);
   }

   public String getInvName() {
      return "Extractor";
   }

   public String getGuiClassName(EntityPlayer entityPlayer) {
      return "block.machine.gui.GuiExtractor";
   }

   public String getStartSoundFile() {
      return "Machines/ExtractorOp.ogg";
   }

   public String getInterruptSoundFile() {
      return "Machines/InterruptOne.ogg";
   }

   public float getWrenchDropRate() {
      return 0.85F;
   }

}
