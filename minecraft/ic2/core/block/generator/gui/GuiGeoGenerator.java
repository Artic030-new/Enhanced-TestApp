package ic2.core.block.generator.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.block.generator.container.ContainerBaseGenerator;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GuiGeoGenerator extends GuiContainer {

   public ContainerBaseGenerator container;
   public String name;
   public String inv;


   public GuiGeoGenerator(ContainerBaseGenerator container) {
      super(container);
      this.container = container;
      this.name = StatCollector.translateToLocal("blockGeoGenerator.name");
      this.inv = StatCollector.translateToLocal("container.inventory");
   }

   protected void drawGuiContainerForegroundLayer(int par1, int par2) {
      super.fontRenderer.drawString(this.name, (super.xSize - super.fontRenderer.getStringWidth(this.name)) / 2, 6, 4210752);
      super.fontRenderer.drawString(this.inv, 8, super.ySize - 96 + 2, 4210752);
   }

   protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
      int i = super.mc.renderEngine.getTexture("/ic2/sprites/GUIGeoGenerator.png");
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      super.mc.renderEngine.bindTexture(i);
      int j = (super.width - super.xSize) / 2;
      int k = (super.height - super.ySize) / 2;
      this.drawTexturedModalRect(j, k, 0, 0, super.xSize, super.ySize);
      int i2;
      if(this.container.tileEntity.fuel > 0) {
         i2 = this.container.tileEntity.gaugeFuelScaled(14);
         this.drawTexturedModalRect(j + 66, k + 36 + 14 - i2, 176, 14 - i2, 14, i2);
      }

      if(this.container.tileEntity.storage > 0) {
         i2 = this.container.tileEntity.gaugeStorageScaled(22);
         this.drawTexturedModalRect(j + 89, k + 39, 177, 19, i2, 6);
      }

   }
}
