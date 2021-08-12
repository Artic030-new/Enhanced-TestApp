package advsolar;

import advsolar.ASPServerProxy;
import advsolar.GuiAdvSolarPanel;
import advsolar.GuiQGenerator;
import advsolar.TileEntityAdvancedSolarPanel;
import advsolar.TileEntityHybridSolarPanel;
import advsolar.TileEntityQGenerator;
import advsolar.TileEntityUltimateSolarPanel;
import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;

public class ASPClientProxy extends ASPServerProxy {

   public void registerRenderers() {
      MinecraftForgeClient.preloadTexture("/advsolar/texture/advancedSolarHelmet.png");
      MinecraftForgeClient.preloadTexture("/advsolar/texture/hybridSolarHelmet.png");
      MinecraftForgeClient.preloadTexture("/advsolar/texture/ultimateSolarHelmet.png");
      MinecraftForgeClient.preloadTexture("/advsolar/texture/adv_items.png");
      MinecraftForgeClient.preloadTexture("/advsolar/texture/advsolar_texture.png");
   }

   public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
      TileEntity te = world.getBlockTileEntity(x, y, z);
      if(te != null) {
    	 if(te instanceof TileEntitySolarPanel) {
    	    return new GuiAdvSolarPanel(player.inventory, (TileEntitySolarPanel)te);
    	 }
    	
    	 if(te instanceof TileEntityQGenerator) {
    	    return new GuiQGenerator(player.inventory, (TileEntityQGenerator)te);
    	 }
      }
      
      return null;
   }

   public int addArmor(String armorName) {
      return RenderingRegistry.addNewArmourRendererPrefix(armorName);
   }
}
