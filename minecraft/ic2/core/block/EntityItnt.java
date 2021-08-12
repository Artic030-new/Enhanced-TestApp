package ic2.core.block;

import ic2.core.Ic2Items;
import ic2.core.block.EntityIC2Explosive;
import net.minecraft.block.Block;
import net.minecraft.world.World;

public class EntityItnt extends EntityIC2Explosive {

   public EntityItnt(World world, double x, double y, double z) {
      super(world, x, y, z, 60, 5.5F, 0.9F, 0.3F, Block.blocksList[Ic2Items.industrialTnt.itemID]);
   }

   public EntityItnt(World world) {
      this(world, 0.0D, 0.0D, 0.0D);
   }
}
