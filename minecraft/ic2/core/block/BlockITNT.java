package ic2.core.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.IC2;
import ic2.core.block.BlockIC2Explosive;
import ic2.core.block.EntityIC2Explosive;
import ic2.core.block.EntityItnt;
import ic2.core.block.EntityNuke;
import java.util.List;
import java.util.logging.Level;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class BlockITNT extends BlockIC2Explosive {

   public boolean isITNT;


   public BlockITNT(int id, int sprite, boolean is) {
      super(id, sprite, is);
      this.isITNT = is;
      this.setCreativeTab(IC2.tabIC2);
   }

   public EntityIC2Explosive getExplosionEntity(World world, float x, float y, float z, String username) {
      return (EntityIC2Explosive)(this.isITNT?new EntityItnt(world, (double)x, (double)y, (double)z):(new EntityNuke(world, (double)x, (double)y, (double)z)).setIgniter(username));
   }

   public void onBlockPlacedBy(World world, int x, int y, int z, EntityLiving entityliving) {
      if(!this.isITNT && entityliving instanceof EntityPlayer) {
         IC2.log.log(Level.INFO, "Player " + ((EntityPlayer)entityliving).username + " placed a nuke at " + world.provider.dimensionId + ":(" + x + "," + y + "," + z + ")");
      }

   }

   public void onIgnite(World world, EntityPlayer player, int x, int y, int z) {
      if(!this.isITNT) {
         IC2.log.log(Level.INFO, "Nuke at " + world.provider.dimensionId + ":(" + x + "," + y + "," + z + ") was ignited " + (player == null?"indirectly":"by " + player.username));
      }

   }

   @SideOnly(Side.CLIENT)
   public EnumRarity getRarity(ItemStack stack) {
      return this.isITNT?EnumRarity.common:EnumRarity.uncommon;
   }

   public void getSubBlocks(int i, CreativeTabs tabs, List itemList) {
      if(this.isITNT || IC2.enableCraftingNuke) {
         super.getSubBlocks(i, tabs, itemList);
      }
   }
}
