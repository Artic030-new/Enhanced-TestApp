package ic2.core.block.personal;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.block.personal.TileEntityPersonalChest;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderBlockPersonal implements ISimpleBlockRenderingHandler {

   public static int renderId;
   private TileEntityPersonalChest invte = new TileEntityPersonalChest();


   public RenderBlockPersonal() {
      renderId = RenderingRegistry.getNextAvailableRenderId();
   }

   public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
      if(metadata != 0) {
         Tessellator var4 = Tessellator.instance;
         GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
         GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
         var4.startDrawingQuads();
         var4.setNormal(0.0F, -1.0F, 0.0F);
         renderer.renderBottomFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(0, metadata));
         var4.draw();
         var4.startDrawingQuads();
         var4.setNormal(0.0F, 1.0F, 0.0F);
         renderer.renderTopFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(1, metadata));
         var4.draw();
         var4.startDrawingQuads();
         var4.setNormal(0.0F, 0.0F, -1.0F);
         renderer.renderEastFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(2, metadata));
         var4.draw();
         var4.startDrawingQuads();
         var4.setNormal(0.0F, 0.0F, 1.0F);
         renderer.renderWestFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(3, metadata));
         var4.draw();
         var4.startDrawingQuads();
         var4.setNormal(-1.0F, 0.0F, 0.0F);
         renderer.renderNorthFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(4, metadata));
         var4.draw();
         var4.startDrawingQuads();
         var4.setNormal(1.0F, 0.0F, 0.0F);
         renderer.renderSouthFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(5, metadata));
         var4.draw();
         GL11.glTranslatef(0.5F, 0.5F, 0.5F);
      } else {
         GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
         GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
         TileEntityRenderer.instance.renderTileEntityAt(this.invte, 0.0D, 0.0D, 0.0D, 0.0F);
         GL11.glEnable('\u803a');
      }

   }

   public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
      if(world.getBlockMetadata(x, y, z) != 0) {
         renderer.renderStandardBlock(block, x, y, z);
      }

      return false;
   }

   public boolean shouldRender3DInInventory() {
      return true;
   }

   public int getRenderId() {
      return renderId;
   }
}
