package ic2.core.block;

import ic2.core.block.EntityDynamite;
import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;

public class EntityStickyDynamite extends EntityDynamite {

   public EntityStickyDynamite(World world) {
      super(world, 0.0D, 0.0D, 0.0D);
      super.sticky = true;
   }

   public EntityStickyDynamite(World world, EntityLiving entityliving) {
      super(world, entityliving);
      super.sticky = true;
   }
}
