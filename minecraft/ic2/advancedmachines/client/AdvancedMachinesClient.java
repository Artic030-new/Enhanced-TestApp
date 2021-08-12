package ic2.advancedmachines.client;

import ic2.advancedmachines.client.GuiCentrifugeExtractor;
import ic2.advancedmachines.client.GuiRotaryMacerator;
import ic2.advancedmachines.client.GuiSingularityCompressor;
import ic2.advancedmachines.common.IProxy;
import ic2.advancedmachines.common.TileEntityCentrifugeExtractor;
import ic2.advancedmachines.common.TileEntityRotaryMacerator;
import ic2.advancedmachines.common.TileEntitySingularityCompressor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;

public class AdvancedMachinesClient implements IProxy {
   
   public void load() {
      MinecraftForgeClient.preloadTexture("/ic2/advancedmachines/client/sprites/block_advmachine.png");
	  MinecraftForgeClient.preloadTexture("/ic2/advancedmachines/client/sprites/GUICenterfuge.png");
	  MinecraftForgeClient.preloadTexture("/ic2/advancedmachines/client/sprites/GUIRotary.png");
	  MinecraftForgeClient.preloadTexture("/ic2/advancedmachines/client/sprites/GUISingularity.png");
   }

   public Object getGuiElementForClient(int ID, EntityPlayer player, World world, int x, int y, int z) {
      TileEntity te = world.getBlockTileEntity(x, y, z);
      if(te != null) {
         if(te instanceof TileEntityRotaryMacerator) {
            return new GuiRotaryMacerator(player.inventory, (TileEntityRotaryMacerator)te);
         }

         if(te instanceof TileEntityCentrifugeExtractor) {
            return new GuiCentrifugeExtractor(player.inventory, (TileEntityCentrifugeExtractor)te);
         }

         if(te instanceof TileEntitySingularityCompressor) {
            return new GuiSingularityCompressor(player.inventory, (TileEntitySingularityCompressor)te);
         }
      }

      return null;
   }
}