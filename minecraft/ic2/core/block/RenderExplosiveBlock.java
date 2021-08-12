package ic2.core.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.block.EntityIC2Explosive;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderExplosiveBlock extends Render {

   public RenderBlocks blockRenderer = new RenderBlocks();


   public RenderExplosiveBlock() {
      super.shadowSize = 0.5F;
   }

   public void func_153_a(EntityIC2Explosive entitytntprimed, double d, double d1, double d2, float f, float f1) {
      GL11.glPushMatrix();
      GL11.glTranslatef((float)d, (float)d1, (float)d2);
      float f3;
      if((float)entitytntprimed.fuse - f1 + 1.0F < 10.0F) {
         f3 = 1.0F - ((float)entitytntprimed.fuse - f1 + 1.0F) / 10.0F;
         if(f3 < 0.0F) {
            f3 = 0.0F;
         }

         if(f3 > 1.0F) {
            f3 = 1.0F;
         }

         f3 *= f3;
         f3 *= f3;
         float f4 = 1.0F + f3 * 0.3F;
         GL11.glScalef(f4, f4, f4);
      }

      f3 = (1.0F - ((float)entitytntprimed.fuse - f1 + 1.0F) / 100.0F) * 0.8F;
      this.loadTexture(entitytntprimed.renderBlock.getTextureFile());
      this.blockRenderer.renderBlockAsItem(entitytntprimed.renderBlock, 0, entitytntprimed.getBrightness(f1));
      if(entitytntprimed.fuse / 5 % 2 == 0) {
         GL11.glDisable(3553);
         GL11.glDisable(2896);
         GL11.glEnable(3042);
         GL11.glBlendFunc(770, 772);
         GL11.glColor4f(1.0F, 1.0F, 1.0F, f3);
         this.blockRenderer.renderBlockAsItem(entitytntprimed.renderBlock, 0, 1.0F);
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         GL11.glDisable(3042);
         GL11.glEnable(2896);
         GL11.glEnable(3553);
      }

      GL11.glPopMatrix();
   }

   public void doRender(Entity entity, double d, double d1, double d2, float f, float f1) {
      this.func_153_a((EntityIC2Explosive)entity, d, d1, d2, f, f1);
   }
}
