package cpw.mods.ironchest.client;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.ironchest.CommonProxy;
import cpw.mods.ironchest.IronChestType;
import cpw.mods.ironchest.TileEntityIronChest;
import cpw.mods.ironchest.client.GUIChest;
import cpw.mods.ironchest.client.IronChestRenderHelper;
import cpw.mods.ironchest.client.TileEntityIronChestRenderer;
import net.minecraft.client.renderer.ChestItemRenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;

public class ClientProxy extends CommonProxy {

   public void registerRenderInformation() {
      ChestItemRenderHelper.instance = new IronChestRenderHelper();
      MinecraftForgeClient.preloadTexture("/cpw/mods/ironchest/sprites/block_textures.png");
      MinecraftForgeClient.preloadTexture("/cpw/mods/ironchest/sprites/item_textures.png");
   }

   public void registerTileEntitySpecialRenderer(IronChestType typ) {
      ClientRegistry.bindTileEntitySpecialRenderer(typ.clazz, new TileEntityIronChestRenderer());
   }

   public World getClientWorld() {
      return FMLClientHandler.instance().getClient().theWorld;
   }

   public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
      TileEntity te = world.getBlockTileEntity(x, y, z);
      return te != null && te instanceof TileEntityIronChest?GUIChest.GUI.buildGUI(IronChestType.values()[ID], player.inventory, (TileEntityIronChest)te):null;
   }
}
