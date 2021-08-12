package ic2.core.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderFlyingItem extends Render {

   public int itemIconIndex;
   public String texturePath;


   public RenderFlyingItem(int icon, String file) {
      this.itemIconIndex = icon;
      this.texturePath = file;
   }

   public void doRender(Entity entity, double d, double d1, double d2, float f, float f1) {
      GL11.glPushMatrix();
      GL11.glTranslatef((float)d, (float)d1, (float)d2);
      GL11.glEnable('\u803a');
      GL11.glScalef(0.5F, 0.5F, 0.5F);
      this.loadTexture(this.texturePath);
      Tessellator tessellator = Tessellator.instance;
      float f2 = (float)(this.itemIconIndex % 16 * 16 + 0) / 256.0F;
      float f3 = (float)(this.itemIconIndex % 16 * 16 + 16) / 256.0F;
      float f4 = (float)(this.itemIconIndex / 16 * 16 + 0) / 256.0F;
      float f5 = (float)(this.itemIconIndex / 16 * 16 + 16) / 256.0F;
      float f6 = 1.0F;
      float f7 = 0.5F;
      float f8 = 0.25F;
      GL11.glRotatef(180.0F - super.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
      GL11.glRotatef(-super.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
      tessellator.startDrawingQuads();
      tessellator.setNormal(0.0F, 1.0F, 0.0F);
      tessellator.addVertexWithUV((double)(0.0F - f7), (double)(0.0F - f8), 0.0D, (double)f2, (double)f5);
      tessellator.addVertexWithUV((double)(f6 - f7), (double)(0.0F - f8), 0.0D, (double)f3, (double)f5);
      tessellator.addVertexWithUV((double)(f6 - f7), (double)(1.0F - f8), 0.0D, (double)f3, (double)f4);
      tessellator.addVertexWithUV((double)(0.0F - f7), (double)(1.0F - f8), 0.0D, (double)f2, (double)f4);
      tessellator.draw();
      GL11.glDisable('\u803a');
      GL11.glPopMatrix();
   }
}
