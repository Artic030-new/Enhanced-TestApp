package ic2.core;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiSmallButton;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GuiIconButton extends GuiSmallButton {

   private String texture;
   private int textureX;
   private int textureY;
   private ItemStack icon = null;
   private boolean drawQuantity;
   private RenderItem renderItem;


   public GuiIconButton(int id, int x, int y, int w, int h, String texture, int textureX, int textureY) {
      super(id, x, y, w, h, "");
      this.texture = texture;
      this.textureX = textureX;
      this.textureY = textureY;
   }

   public GuiIconButton(int id, int x, int y, int w, int h, ItemStack icon, boolean drawQuantity) {
      super(id, x, y, w, h, "");
      this.icon = icon;
      this.drawQuantity = drawQuantity;
   }

   public void drawButton(Minecraft minecraft, int i, int j) {
      super.drawButton(minecraft, i, j);
      if(this.icon == null) {
         int k = minecraft.renderEngine.getTexture(this.texture);
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         minecraft.renderEngine.bindTexture(k);
         this.drawTexturedModalRect(super.xPosition + 2, super.yPosition + 1, this.textureX, this.textureY, super.width - 4, super.height - 4);
      } else {
         if(this.renderItem == null) {
            this.renderItem = new RenderItem();
         }

         this.renderItem.renderItemIntoGUI(minecraft.fontRenderer, minecraft.renderEngine, this.icon, super.xPosition + 2, super.yPosition + 1);
         if(this.drawQuantity) {
            this.renderItem.renderItemOverlayIntoGUI(minecraft.fontRenderer, minecraft.renderEngine, this.icon, super.xPosition + 2, super.xPosition + 1);
         }
      }

   }
}
