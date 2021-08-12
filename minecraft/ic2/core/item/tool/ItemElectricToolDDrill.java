package ic2.core.item.tool;

import ic2.core.item.tool.ItemElectricToolDrill;
import net.minecraft.block.Block;
import net.minecraft.item.EnumToolMaterial;

public class ItemElectricToolDDrill extends ItemElectricToolDrill {

   public ItemElectricToolDDrill(int id, int sprite) {
      super(id, sprite, EnumToolMaterial.EMERALD, 80);
      super.maxCharge = 10000;
      super.transferLimit = 100;
      super.tier = 1;
      super.efficiencyOnProperMaterial = 16.0F;
   }

   public void init() {
      super.init();
      super.mineableBlocks.add(Block.obsidian);
   }
}
