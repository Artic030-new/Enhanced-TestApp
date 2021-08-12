package ic2.core.block.machine;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderBlockMiningPipe implements ISimpleBlockRenderingHandler {

   public static int renderId;


   public RenderBlockMiningPipe() {
      renderId = RenderingRegistry.getNextAvailableRenderId();
   }

   public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderblocks) {
      Tessellator tessellator = Tessellator.instance;
      float f4 = 0.125F;
      block.setBlockBounds(0.5F - f4, 0.0F, 0.5F - f4, 0.5F + f4, 1.0F, 0.5F + f4);
      renderblocks.setRenderBoundsFromBlock(block);
      GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
      tessellator.startDrawingQuads();
      tessellator.setNormal(0.0F, -1.0F, 0.0F);
      renderblocks.renderBottomFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSide(0));
      tessellator.draw();
      tessellator.startDrawingQuads();
      tessellator.setNormal(0.0F, 1.0F, 0.0F);
      renderblocks.renderTopFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSide(1));
      tessellator.draw();
      tessellator.startDrawingQuads();
      tessellator.setNormal(0.0F, 0.0F, -1.0F);
      renderblocks.renderEastFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSide(2));
      tessellator.draw();
      tessellator.startDrawingQuads();
      tessellator.setNormal(0.0F, 0.0F, 1.0F);
      renderblocks.renderWestFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSide(3));
      tessellator.draw();
      tessellator.startDrawingQuads();
      tessellator.setNormal(-1.0F, 0.0F, 0.0F);
      renderblocks.renderNorthFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSide(4));
      tessellator.draw();
      tessellator.startDrawingQuads();
      tessellator.setNormal(1.0F, 0.0F, 0.0F);
      renderblocks.renderSouthFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSide(5));
      tessellator.draw();
      GL11.glTranslatef(0.5F, 0.5F, 0.5F);
      block.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
      renderblocks.setRenderBoundsFromBlock(block);
   }

   public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderblocks) {
      float f = 0.25F;
      float fi = (1.0F - f) / 2.0F;
      block.setBlockBounds(fi, 0.0F, fi, fi + f, 1.0F, fi + f);
      renderblocks.setRenderBoundsFromBlock(block);
      renderblocks.renderStandardBlock(block, x, y, z);
      block.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
      renderblocks.setRenderBoundsFromBlock(block);
      return true;
   }

   public boolean shouldRender3DInInventory() {
      return true;
   }

   public int getRenderId() {
      return renderId;
   }
}
