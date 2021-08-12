package ic2.core.item.tool;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.item.tool.ContainerCropnalyzer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GuiCropnalyzer extends GuiContainer {

   public ContainerCropnalyzer container;
   public String name;


   public GuiCropnalyzer(ContainerCropnalyzer container) {
      super(container);
      this.container = container;
      this.name = StatCollector.translateToLocal("item.itemCropnalyzer.name");
   }

   protected void drawGuiContainerForegroundLayer(int par1, int par2) {
      super.fontRenderer.drawString(this.name, 74, 11, 0);
      int level = this.container.cropnalyzer.getScannedLevel();
      if(level > -1) {
         if(level == 0) {
            super.fontRenderer.drawString("UNKNOWN", 8, 37, 16777215);
         } else {
            super.fontRenderer.drawString(this.container.cropnalyzer.getSeedName(), 8, 37, 16777215);
            if(level >= 2) {
               super.fontRenderer.drawString("Tier: " + this.container.cropnalyzer.getSeedTier(), 8, 50, 16777215);
               super.fontRenderer.drawString("Discovered by:", 8, 73, 16777215);
               super.fontRenderer.drawString(this.container.cropnalyzer.getSeedDiscovered(), 8, 86, 16777215);
            }

            if(level >= 3) {
               super.fontRenderer.drawString(this.container.cropnalyzer.getSeedDesc(0), 8, 109, 16777215);
               super.fontRenderer.drawString(this.container.cropnalyzer.getSeedDesc(1), 8, 122, 16777215);
            }

            if(level >= 4) {
               super.fontRenderer.drawString("Growth:", 118, 37, 11403055);
               super.fontRenderer.drawString("" + this.container.cropnalyzer.getSeedGrowth(), 118, 50, 11403055);
               super.fontRenderer.drawString("Gain:", 118, 73, 15649024);
               super.fontRenderer.drawString("" + this.container.cropnalyzer.getSeedGain(), 118, 86, 15649024);
               super.fontRenderer.drawString("Resis.:", 118, 109, '\uced1');
               super.fontRenderer.drawString("" + this.container.cropnalyzer.getSeedResistence(), 118, 122, '\uced1');
            }

         }
      }
   }

   protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
      int i = super.mc.renderEngine.getTexture("/ic2/sprites/GUICropnalyzer.png");
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      super.mc.renderEngine.bindTexture(i);
      int j = (super.width - super.xSize) / 2;
      int k = (super.height - super.ySize) / 2;
      this.drawTexturedModalRect(j, k, 0, 0, super.xSize, super.ySize);
   }
}
