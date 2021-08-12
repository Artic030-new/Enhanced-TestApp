package cpw.mods.ironchest;

import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.ironchest.ContainerIronChestBase;
import cpw.mods.ironchest.IronChestType;
import cpw.mods.ironchest.TileEntityIronChest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class CommonProxy implements IGuiHandler {

   public void registerRenderInformation() {}

   public void registerTileEntitySpecialRenderer(IronChestType typ) {}

   public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
      return null;
   }

   public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
      TileEntity te = world.getBlockTileEntity(x, y, z);
      if(te != null && te instanceof TileEntityIronChest) {
         TileEntityIronChest icte = (TileEntityIronChest)te;
         return new ContainerIronChestBase(player.inventory, icte, icte.getType(), 0, 0);
      } else {
         return null;
      }
   }

   public World getClientWorld() {
      return null;
   }
}
