package ic2.core.block;

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
public class RenderBlockFence implements ISimpleBlockRenderingHandler {

   public static int renderId;


   public RenderBlockFence() {
      renderId = RenderingRegistry.getNextAvailableRenderId();
   }

   public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderblocks) {
      Tessellator tessellator = Tessellator.instance;

      for(int i1 = 0; i1 < 4; ++i1) {
         float f4 = 0.125F;
         if(i1 == 0) {
            block.setBlockBounds(0.5F - f4, 0.0F, 0.0F, 0.5F + f4, 1.0F, f4 * 2.0F);
         }

         if(i1 == 1) {
            block.setBlockBounds(0.5F - f4, 0.0F, 1.0F - f4 * 2.0F, 0.5F + f4, 1.0F, 1.0F);
         }

         f4 = 0.0625F;
         if(i1 == 2) {
            block.setBlockBounds(0.5F - f4, 1.0F - f4 * 3.0F, -f4 * 2.0F, 0.5F + f4, 1.0F - f4, 1.0F + f4 * 2.0F);
         }

         if(i1 == 3) {
            block.setBlockBounds(0.5F - f4, 0.5F - f4 * 3.0F, -f4 * 2.0F, 0.5F + f4, 0.5F - f4, 1.0F + f4 * 2.0F);
         }

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
      }

      block.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
      renderblocks.setRenderBoundsFromBlock(block);
   }

   public boolean renderWorldBlock(IBlockAccess iblockaccess, int x, int y, int z, Block block, int modelId, RenderBlocks renderblocks) {
      float w = 0.25F;
      float d = (1.0F - w) / 2.0F;
      float wi = 0.125F;
      float di = (1.0F - wi) / 2.0F;
      float ht1 = 0.75F;
      float ht2 = 0.9375F;
      float hb1 = 0.375F;
      float hb2 = 0.5625F;
      block.setBlockBounds(d, 0.0F, d, d + w, 1.0F, d + w);
      renderblocks.setRenderBoundsFromBlock(block);
      renderblocks.renderStandardBlock(block, x, y, z);
      int blockId = iblockaccess.getBlockId(x + 1, y, z);
      if(blockId == block.blockID || blockId == Block.fence.blockID) {
         block.setBlockBounds(d + w, ht1, di, 1.0F + d, ht2, di + wi);
         renderblocks.setRenderBoundsFromBlock(block);
         renderblocks.renderStandardBlock(block, x, y, z);
         block.setBlockBounds(d + w, hb1, di, 1.0F + d, hb2, di + wi);
         renderblocks.setRenderBoundsFromBlock(block);
         renderblocks.renderStandardBlock(block, x, y, z);
      }

      blockId = iblockaccess.getBlockId(x, y, z + 1);
      if(blockId == block.blockID || blockId == Block.fence.blockID) {
         block.setBlockBounds(di, ht1, d + w, di + wi, ht2, 1.0F + d);
         renderblocks.setRenderBoundsFromBlock(block);
         renderblocks.renderStandardBlock(block, x, y, z);
         block.setBlockBounds(di, hb1, d + w, di + wi, hb2, 1.0F + d);
         renderblocks.setRenderBoundsFromBlock(block);
         renderblocks.renderStandardBlock(block, x, y, z);
      }

      blockId = iblockaccess.getBlockId(x - 1, y, z);
      if(blockId == Block.fence.blockID) {
         block.setBlockBounds(-d, ht1, di, d, ht2, di + wi);
         renderblocks.renderStandardBlock(block, x, y, z);
         block.setBlockBounds(-d, hb1, di, d, hb2, di + wi);
         renderblocks.renderStandardBlock(block, x, y, z);
      }

      blockId = iblockaccess.getBlockId(x, y, z - 1);
      if(blockId == Block.fence.blockID) {
         block.setBlockBounds(di, ht1, -d, di + wi, ht2, d);
         renderblocks.setRenderBoundsFromBlock(block);
         renderblocks.renderStandardBlock(block, x, y, z);
         block.setBlockBounds(di, hb1, -d, di + wi, hb2, d);
         renderblocks.setRenderBoundsFromBlock(block);
         renderblocks.renderStandardBlock(block, x, y, z);
      }

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
