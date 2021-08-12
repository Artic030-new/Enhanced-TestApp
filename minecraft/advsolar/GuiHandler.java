package advsolar;

import advsolar.GuiAdvSolarPanel;
import advsolar.GuiQGenerator;
import advsolar.TileEntityAdvancedSolarPanel;
import advsolar.TileEntityHybridSolarPanel;
import advsolar.TileEntityQGenerator;
import advsolar.TileEntityUltimateSolarPanel;
import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class GuiHandler implements IGuiHandler {

   public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
      TileEntity te = world.getBlockTileEntity(x, y, z);
      return te != null?(te instanceof TileEntityAdvancedSolarPanel?((TileEntityAdvancedSolarPanel)te).getGuiContainer(player.inventory):(te instanceof TileEntityHybridSolarPanel?((TileEntityHybridSolarPanel)te).getGuiContainer(player.inventory):(te instanceof TileEntityUltimateSolarPanel?((TileEntityUltimateSolarPanel)te).getGuiContainer(player.inventory):null))):null;
   }

   public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
      TileEntity te = world.getBlockTileEntity(x, y, z);
      if(te != null) {
    	 if (te instanceof TileEntitySolarPanel) {
    		 return new GuiAdvSolarPanel(player.inventory, (TileEntitySolarPanel)te);
    	  }
    	  
    	  if (te instanceof TileEntityQGenerator) {
    		 return new GuiQGenerator(player.inventory, (TileEntityQGenerator)te);
    	  }
       } 
    
      return null;
   }
}
