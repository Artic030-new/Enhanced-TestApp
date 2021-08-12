package ic2.core.block;

import net.minecraft.block.BlockGlass;
import net.minecraft.block.material.Material;

public class BlockTexGlass extends BlockGlass {

   public BlockTexGlass(int id, int sprite, Material mat, boolean renderAdjacent) {
      super(id, sprite, mat, renderAdjacent);
   }

   public String getTextureFile() {
      return "/ic2/sprites/block_0.png";
   }
}
