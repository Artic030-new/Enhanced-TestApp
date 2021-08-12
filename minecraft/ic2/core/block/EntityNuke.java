package ic2.core.block;

import ic2.core.IC2;
import ic2.core.IC2DamageSource;
import ic2.core.Ic2Items;
import ic2.core.block.EntityIC2Explosive;
import ic2.core.item.tool.ItemToolWrench;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class EntityNuke extends EntityIC2Explosive {

   public EntityNuke(World world, double x, double y, double z) {
      super(world, x, y, z, 300, IC2.explosionPowerNuke, 0.05F, 1.5F, Block.blocksList[Ic2Items.nuke.itemID], IC2DamageSource.nuke);
   }

   public EntityNuke(World world) {
      this(world, 0.0D, 0.0D, 0.0D);
   }

   public boolean interact(EntityPlayer player) {
      if(IC2.platform.isSimulating() && player.inventory.mainInventory[player.inventory.currentItem] != null && player.inventory.mainInventory[player.inventory.currentItem].getItem() instanceof ItemToolWrench) {
         ItemToolWrench wrench = (ItemToolWrench)player.inventory.mainInventory[player.inventory.currentItem].getItem();
         if(wrench.canTakeDamage(player.inventory.mainInventory[player.inventory.currentItem], 1)) {
            wrench.damage(player.inventory.mainInventory[player.inventory.currentItem], 1, player);
            this.setDead();
         }
      }

      return false;
   }
}
