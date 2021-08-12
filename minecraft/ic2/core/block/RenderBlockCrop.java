package ic2.core.block;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.block.TileEntityCrop;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.ForgeHooksClient;

@SideOnly(Side.CLIENT)
public class RenderBlockCrop implements ISimpleBlockRenderingHandler {

   public static int renderId;


   public RenderBlockCrop() {
      renderId = RenderingRegistry.getNextAvailableRenderId();
   }

   public static void renderBlockCropsImpl(Block block, IBlockAccess blockAccess, int x, int y, int z) {
      Tessellator tessellator = Tessellator.instance;
      int j = block.getBlockTexture(blockAccess, x, y, z, 0);
      int k = (j & 15) << 4;
      int l = j & 240;
      double d = (double)x;
      double d1 = (double)y - 0.0625D;
      double d2 = (double)z;
      double d3 = (double)((float)k / 256.0F);
      double d4 = (double)(((float)k + 15.99F) / 256.0F);
      double d5 = (double)((float)l / 256.0F);
      double d6 = (double)(((float)l + 15.99F) / 256.0F);
      double d7 = d + 0.5D - 0.25D;
      double d8 = d + 0.5D + 0.25D;
      double d9 = d2 + 0.5D - 0.5D;
      double d10 = d2 + 0.5D + 0.5D;
      tessellator.addVertexWithUV(d7, d1 + 1.0D, d9, d3, d5);
      tessellator.addVertexWithUV(d7, d1 + 0.0D, d9, d3, d6);
      tessellator.addVertexWithUV(d7, d1 + 0.0D, d10, d4, d6);
      tessellator.addVertexWithUV(d7, d1 + 1.0D, d10, d4, d5);
      tessellator.addVertexWithUV(d7, d1 + 1.0D, d10, d3, d5);
      tessellator.addVertexWithUV(d7, d1 + 0.0D, d10, d3, d6);
      tessellator.addVertexWithUV(d7, d1 + 0.0D, d9, d4, d6);
      tessellator.addVertexWithUV(d7, d1 + 1.0D, d9, d4, d5);
      tessellator.addVertexWithUV(d8, d1 + 1.0D, d10, d3, d5);
      tessellator.addVertexWithUV(d8, d1 + 0.0D, d10, d3, d6);
      tessellator.addVertexWithUV(d8, d1 + 0.0D, d9, d4, d6);
      tessellator.addVertexWithUV(d8, d1 + 1.0D, d9, d4, d5);
      tessellator.addVertexWithUV(d8, d1 + 1.0D, d9, d3, d5);
      tessellator.addVertexWithUV(d8, d1 + 0.0D, d9, d3, d6);
      tessellator.addVertexWithUV(d8, d1 + 0.0D, d10, d4, d6);
      tessellator.addVertexWithUV(d8, d1 + 1.0D, d10, d4, d5);
      d7 = d + 0.5D - 0.5D;
      d8 = d + 0.5D + 0.5D;
      d9 = d2 + 0.5D - 0.25D;
      d10 = d2 + 0.5D + 0.25D;
      tessellator.addVertexWithUV(d7, d1 + 1.0D, d9, d3, d5);
      tessellator.addVertexWithUV(d7, d1 + 0.0D, d9, d3, d6);
      tessellator.addVertexWithUV(d8, d1 + 0.0D, d9, d4, d6);
      tessellator.addVertexWithUV(d8, d1 + 1.0D, d9, d4, d5);
      tessellator.addVertexWithUV(d8, d1 + 1.0D, d9, d3, d5);
      tessellator.addVertexWithUV(d8, d1 + 0.0D, d9, d3, d6);
      tessellator.addVertexWithUV(d7, d1 + 0.0D, d9, d4, d6);
      tessellator.addVertexWithUV(d7, d1 + 1.0D, d9, d4, d5);
      tessellator.addVertexWithUV(d8, d1 + 1.0D, d10, d3, d5);
      tessellator.addVertexWithUV(d8, d1 + 0.0D, d10, d3, d6);
      tessellator.addVertexWithUV(d7, d1 + 0.0D, d10, d4, d6);
      tessellator.addVertexWithUV(d7, d1 + 1.0D, d10, d4, d5);
      tessellator.addVertexWithUV(d7, d1 + 1.0D, d10, d3, d5);
      tessellator.addVertexWithUV(d7, d1 + 0.0D, d10, d3, d6);
      tessellator.addVertexWithUV(d8, d1 + 0.0D, d10, d4, d6);
      tessellator.addVertexWithUV(d8, d1 + 1.0D, d10, d4, d5);
   }

   public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {}

   public boolean renderWorldBlock(IBlockAccess blockAccess, int i, int j, int k, Block block, int modelId, RenderBlocks renderer) {
      Tessellator tessellator = Tessellator.instance;
      TileEntity te = blockAccess.getBlockTileEntity(i, j, k);
      if(te != null && te instanceof TileEntityCrop) {
         TileEntityCrop tecrop = (TileEntityCrop)te;
         ForgeHooksClient.bindTexture(tecrop.crop().getTextureFile(), 0);
      } else {
         ForgeHooksClient.bindTexture("/terrain.png", 0);
      }

      tessellator.setBrightness(block.getMixedBrightnessForBlock(blockAccess, i, j, k));
      tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
      renderBlockCropsImpl(block, blockAccess, i, j, k);
      return true;
   }

   public boolean shouldRender3DInInventory() {
      return false;
   }

   public int getRenderId() {
      return renderId;
   }
}
