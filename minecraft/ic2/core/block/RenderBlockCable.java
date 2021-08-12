package ic2.core.block;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.Direction;
import ic2.core.block.wiring.TileEntityCable;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;

@SideOnly(Side.CLIENT)
public class RenderBlockCable implements ISimpleBlockRenderingHandler {

   private static final Direction[] directions = Direction.values();
   public static int renderId;


   public RenderBlockCable() {
      renderId = RenderingRegistry.getNextAvailableRenderId();
   }

   public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {}

   public boolean renderWorldBlock(IBlockAccess iBlockAccess, int x, int y, int z, Block block, int modelId, RenderBlocks renderblocks) {
      TileEntity te = iBlockAccess.getBlockTileEntity(x, y, z);
      if(!(te instanceof TileEntityCable)) {
         return true;
      } else {
         TileEntityCable cable = (TileEntityCable)te;
         if(cable.foamed > 0) {
            renderblocks.renderStandardBlock(block, x, y, z);
         } else {
            float th = cable.getCableThickness();
            float sp = (1.0F - th) / 2.0F;
            int connectivity = 0;
            int renderSide = 0;
            int mask = 1;
            Direction[] tessellator = directions;
            int texture = tessellator.length;

            for(int xD = 0; xD < texture; ++xD) {
               Direction direction = tessellator[xD];
               TileEntity yD = direction.applyToTileEntity(cable);
               if(yD != null && cable.canInteractWith(yD)) {
                  connectivity |= mask;
                  if(yD instanceof TileEntityCable && ((TileEntityCable)yD).getCableThickness() < th) {
                     renderSide |= mask;
                  }
               }

               mask *= 2;
            }

            Tessellator var23 = Tessellator.instance;
            texture = block.getBlockTexture(iBlockAccess, x, y, z, 0);
            double var24 = (double)x;
            double var25 = (double)y;
            double zD = (double)z;
            var23.setBrightness(block.getMixedBrightnessForBlock(iBlockAccess, x, y, z));
            if(connectivity == 0) {
               block.setBlockBounds(sp, sp, sp, sp + th, sp + th, sp + th);
               renderblocks.setRenderBoundsFromBlock(block);
               var23.setColorOpaque_F(0.5F, 0.5F, 0.5F);
               renderblocks.renderBottomFace(block, var24, var25, zD, texture);
               var23.setColorOpaque_F(1.0F, 1.0F, 1.0F);
               renderblocks.renderTopFace(block, var24, var25, zD, texture);
               var23.setColorOpaque_F(0.8F, 0.8F, 0.8F);
               renderblocks.renderEastFace(block, var24, var25, zD, texture);
               renderblocks.renderWestFace(block, var24, (double)y, zD, texture);
               var23.setColorOpaque_F(0.6F, 0.6F, 0.6F);
               renderblocks.renderNorthFace(block, var24, var25, zD, texture);
               renderblocks.renderSouthFace(block, var24, var25, zD, texture);
            } else if(connectivity == 3) {
               block.setBlockBounds(0.0F, sp, sp, 1.0F, sp + th, sp + th);
               renderblocks.setRenderBoundsFromBlock(block);
               var23.setColorOpaque_F(0.5F, 0.5F, 0.5F);
               renderblocks.renderBottomFace(block, var24, var25, zD, texture);
               var23.setColorOpaque_F(1.0F, 1.0F, 1.0F);
               renderblocks.renderTopFace(block, var24, var25, zD, texture);
               var23.setColorOpaque_F(0.8F, 0.8F, 0.8F);
               renderblocks.renderEastFace(block, var24, var25, zD, texture);
               renderblocks.renderWestFace(block, var24, (double)y, zD, texture);
               if((renderSide & 1) != 0) {
                  var23.setColorOpaque_F(0.6F, 0.6F, 0.6F);
                  renderblocks.renderNorthFace(block, var24, var25, zD, texture);
               }

               if((renderSide & 2) != 0) {
                  var23.setColorOpaque_F(0.6F, 0.6F, 0.6F);
                  renderblocks.renderSouthFace(block, var24, var25, zD, texture);
               }
            } else if(connectivity == 12) {
               block.setBlockBounds(sp, 0.0F, sp, sp + th, 1.0F, sp + th);
               renderblocks.setRenderBoundsFromBlock(block);
               var23.setColorOpaque_F(0.8F, 0.8F, 0.8F);
               renderblocks.renderEastFace(block, var24, var25, zD, texture);
               renderblocks.renderWestFace(block, var24, (double)y, zD, texture);
               var23.setColorOpaque_F(0.6F, 0.6F, 0.6F);
               renderblocks.renderNorthFace(block, var24, var25, zD, texture);
               renderblocks.renderSouthFace(block, var24, var25, zD, texture);
               if((renderSide & 4) != 0) {
                  var23.setColorOpaque_F(0.5F, 0.5F, 0.5F);
                  renderblocks.renderBottomFace(block, var24, var25, zD, texture);
               }

               if((renderSide & 8) != 0) {
                  var23.setColorOpaque_F(1.0F, 1.0F, 1.0F);
                  renderblocks.renderTopFace(block, var24, var25, zD, texture);
               }
            } else if(connectivity == 48) {
               block.setBlockBounds(sp, sp, 0.0F, sp + th, sp + th, 1.0F);
               renderblocks.setRenderBoundsFromBlock(block);
               var23.setColorOpaque_F(0.5F, 0.5F, 0.5F);
               renderblocks.renderBottomFace(block, var24, var25, zD, texture);
               var23.setColorOpaque_F(1.0F, 1.0F, 1.0F);
               renderblocks.renderTopFace(block, var24, var25, zD, texture);
               var23.setColorOpaque_F(0.6F, 0.6F, 0.6F);
               renderblocks.renderNorthFace(block, var24, var25, zD, texture);
               renderblocks.renderSouthFace(block, var24, var25, zD, texture);
               if((renderSide & 16) != 0) {
                  var23.setColorOpaque_F(0.8F, 0.8F, 0.8F);
                  renderblocks.renderEastFace(block, var24, (double)y, zD, texture);
               }

               if((renderSide & 32) != 0) {
                  var23.setColorOpaque_F(0.8F, 0.8F, 0.8F);
                  renderblocks.renderWestFace(block, var24, var25, zD, texture);
               }
            } else {
               if((connectivity & 1) == 0) {
                  block.setBlockBounds(sp, sp, sp, sp + th, sp + th, sp + th);
                  renderblocks.setRenderBoundsFromBlock(block);
                  var23.setColorOpaque_F(0.6F, 0.6F, 0.6F);
                  renderblocks.renderNorthFace(block, var24, var25, zD, texture);
               } else {
                  block.setBlockBounds(0.0F, sp, sp, sp, sp + th, sp + th);
                  renderblocks.setRenderBoundsFromBlock(block);
                  var23.setColorOpaque_F(0.5F, 0.5F, 0.5F);
                  renderblocks.renderBottomFace(block, var24, var25, zD, texture);
                  var23.setColorOpaque_F(1.0F, 1.0F, 1.0F);
                  renderblocks.renderTopFace(block, var24, var25, zD, texture);
                  var23.setColorOpaque_F(0.8F, 0.8F, 0.8F);
                  renderblocks.renderEastFace(block, var24, var25, zD, texture);
                  renderblocks.renderWestFace(block, var24, (double)y, zD, texture);
                  if((renderSide & 1) != 0) {
                     var23.setColorOpaque_F(0.6F, 0.6F, 0.6F);
                     renderblocks.renderNorthFace(block, var24, var25, zD, texture);
                  }
               }

               if((connectivity & 2) == 0) {
                  block.setBlockBounds(sp, sp, sp, sp + th, sp + th, sp + th);
                  renderblocks.setRenderBoundsFromBlock(block);
                  var23.setColorOpaque_F(0.6F, 0.6F, 0.6F);
                  renderblocks.renderSouthFace(block, var24, var25, zD, texture);
               } else {
                  block.setBlockBounds(sp + th, sp, sp, 1.0F, sp + th, sp + th);
                  renderblocks.setRenderBoundsFromBlock(block);
                  var23.setColorOpaque_F(0.5F, 0.5F, 0.5F);
                  renderblocks.renderBottomFace(block, var24, var25, zD, texture);
                  var23.setColorOpaque_F(1.0F, 1.0F, 1.0F);
                  renderblocks.renderTopFace(block, var24, var25, zD, texture);
                  var23.setColorOpaque_F(0.8F, 0.8F, 0.8F);
                  renderblocks.renderEastFace(block, var24, var25, zD, texture);
                  renderblocks.renderWestFace(block, var24, (double)y, zD, texture);
                  if((renderSide & 2) != 0) {
                     var23.setColorOpaque_F(0.6F, 0.6F, 0.6F);
                     renderblocks.renderSouthFace(block, var24, var25, zD, texture);
                  }
               }

               if((connectivity & 4) == 0) {
                  block.setBlockBounds(sp, sp, sp, sp + th, sp + th, sp + th);
                  renderblocks.setRenderBoundsFromBlock(block);
                  var23.setColorOpaque_F(0.5F, 0.5F, 0.5F);
                  renderblocks.renderBottomFace(block, var24, var25, zD, texture);
               } else {
                  block.setBlockBounds(sp, 0.0F, sp, sp + th, sp, sp + th);
                  renderblocks.setRenderBoundsFromBlock(block);
                  var23.setColorOpaque_F(0.8F, 0.8F, 0.8F);
                  renderblocks.renderEastFace(block, var24, var25, zD, texture);
                  renderblocks.renderWestFace(block, var24, (double)y, zD, texture);
                  var23.setColorOpaque_F(0.6F, 0.6F, 0.6F);
                  renderblocks.renderNorthFace(block, var24, var25, zD, texture);
                  renderblocks.renderSouthFace(block, var24, var25, zD, texture);
                  if((renderSide & 4) != 0) {
                     var23.setColorOpaque_F(0.5F, 0.5F, 0.5F);
                     renderblocks.renderBottomFace(block, var24, var25, zD, texture);
                  }
               }

               if((connectivity & 8) == 0) {
                  block.setBlockBounds(sp, sp, sp, sp + th, sp + th, sp + th);
                  renderblocks.setRenderBoundsFromBlock(block);
                  var23.setColorOpaque_F(1.0F, 1.0F, 1.0F);
                  renderblocks.renderTopFace(block, var24, var25, zD, texture);
               } else {
                  block.setBlockBounds(sp, sp + th, sp, sp + th, 1.0F, sp + th);
                  renderblocks.setRenderBoundsFromBlock(block);
                  var23.setColorOpaque_F(0.8F, 0.8F, 0.8F);
                  renderblocks.renderEastFace(block, var24, var25, zD, texture);
                  renderblocks.renderWestFace(block, var24, (double)y, zD, texture);
                  var23.setColorOpaque_F(0.6F, 0.6F, 0.6F);
                  renderblocks.renderNorthFace(block, var24, var25, zD, texture);
                  renderblocks.renderSouthFace(block, var24, var25, zD, texture);
                  if((renderSide & 8) != 0) {
                     var23.setColorOpaque_F(1.0F, 1.0F, 1.0F);
                     renderblocks.renderTopFace(block, var24, var25, zD, texture);
                  }
               }

               if((connectivity & 16) == 0) {
                  block.setBlockBounds(sp, sp, sp, sp + th, sp + th, sp + th);
                  renderblocks.setRenderBoundsFromBlock(block);
                  var23.setColorOpaque_F(0.8F, 0.8F, 0.8F);
                  renderblocks.renderEastFace(block, var24, (double)y, zD, texture);
               } else {
                  block.setBlockBounds(sp, sp, 0.0F, sp + th, sp + th, sp);
                  renderblocks.setRenderBoundsFromBlock(block);
                  var23.setColorOpaque_F(0.5F, 0.5F, 0.5F);
                  renderblocks.renderBottomFace(block, var24, var25, zD, texture);
                  var23.setColorOpaque_F(1.0F, 1.0F, 1.0F);
                  renderblocks.renderTopFace(block, var24, var25, zD, texture);
                  var23.setColorOpaque_F(0.6F, 0.6F, 0.6F);
                  renderblocks.renderNorthFace(block, var24, var25, zD, texture);
                  renderblocks.renderSouthFace(block, var24, var25, zD, texture);
                  if((renderSide & 16) != 0) {
                     var23.setColorOpaque_F(0.8F, 0.8F, 0.8F);
                     renderblocks.renderEastFace(block, var24, (double)y, zD, texture);
                  }
               }

               if((connectivity & 32) == 0) {
                  block.setBlockBounds(sp, sp, sp, sp + th, sp + th, sp + th);
                  renderblocks.setRenderBoundsFromBlock(block);
                  var23.setColorOpaque_F(0.8F, 0.8F, 0.8F);
                  renderblocks.renderWestFace(block, var24, var25, zD, texture);
               } else {
                  block.setBlockBounds(sp, sp, sp + th, sp + th, sp + th, 1.0F);
                  renderblocks.setRenderBoundsFromBlock(block);
                  var23.setColorOpaque_F(0.5F, 0.5F, 0.5F);
                  renderblocks.renderBottomFace(block, var24, var25, zD, texture);
                  var23.setColorOpaque_F(1.0F, 1.0F, 1.0F);
                  renderblocks.renderTopFace(block, var24, var25, zD, texture);
                  var23.setColorOpaque_F(0.6F, 0.6F, 0.6F);
                  renderblocks.renderNorthFace(block, var24, var25, zD, texture);
                  renderblocks.renderSouthFace(block, var24, var25, zD, texture);
                  if((renderSide & 32) != 0) {
                     var23.setColorOpaque_F(0.8F, 0.8F, 0.8F);
                     renderblocks.renderWestFace(block, var24, var25, zD, texture);
                  }
               }
            }

            block.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
            renderblocks.setRenderBoundsFromBlock(block);
         }

         return true;
      }
   }

   public boolean shouldRender3DInInventory() {
      return false;
   }

   public int getRenderId() {
      return renderId;
   }

}
