package advsolar;

import advsolar.TileEntityAdvancedSolarPanel;
import advsolar.TileEntityHybridSolarPanel;
import advsolar.TileEntityQGenerator;
import advsolar.TileEntityUltimateSolarPanel;
import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class ASPServerProxy implements IGuiHandler {

   public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
      TileEntity te = world.getBlockTileEntity(x, y, z);
      if(te != null) {
         if(te instanceof TileEntityAdvancedSolarPanel) {
            return ((TileEntityAdvancedSolarPanel)te).getGuiContainer(player.inventory);
         }

         if(te instanceof TileEntityHybridSolarPanel) {
            return ((TileEntityHybridSolarPanel)te).getGuiContainer(player.inventory);
         }

         if(te instanceof TileEntityUltimateSolarPanel) {
            return ((TileEntityUltimateSolarPanel)te).getGuiContainer(player.inventory);
         }

         if(te instanceof TileEntityQGenerator) {
            return ((TileEntityQGenerator)te).getGuiContainer(player.inventory);
         }
      }

      return null;
   }

   public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
      return null;
   }

   public int addArmor(String armorName) {
      return 0;
   }

   public void registerRenderers() {}
}
