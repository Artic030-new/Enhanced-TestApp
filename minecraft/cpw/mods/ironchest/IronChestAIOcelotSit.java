package cpw.mods.ironchest;

import cpw.mods.ironchest.IronChest;
import net.minecraft.entity.ai.EntityAIOcelotSit;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.world.World;

public class IronChestAIOcelotSit extends EntityAIOcelotSit {

   public IronChestAIOcelotSit(EntityOcelot par1EntityOcelot, float par2) {
      super(par1EntityOcelot, par2);
   }

   protected boolean isSittableBlock(World world, int x, int y, int z) {
      return world.getBlockId(x, y, z) == IronChest.ironChestBlock.blockID?true:super.isSittableBlock(world, x, y, z);
   }
}
