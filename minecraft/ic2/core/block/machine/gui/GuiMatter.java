package ic2.core.block.machine.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.block.machine.ContainerMatter;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GuiMatter extends GuiContainer {

   public ContainerMatter container;
   public String name;
   public String progressLabel;
   public String amplifierLabel;
   public String inv;


   public GuiMatter(ContainerMatter container) {
      super(container);
      this.container = container;
      this.name = StatCollector.translateToLocal("blockMatter.name");
      this.progressLabel = StatCollector.translateToLocal("container.matter.progress");
      this.amplifierLabel = StatCollector.translateToLocal("container.matter.amplifier");
      this.inv = StatCollector.translateToLocal("container.inventory");
   }

   protected void drawGuiContainerForegroundLayer(int par1, int par2) {
      super.fontRenderer.drawString(this.name, (super.xSize - super.fontRenderer.getStringWidth(this.name)) / 2, 6, 4210752);
      super.fontRenderer.drawString(this.inv, 8, super.ySize - 96 + 2, 4210752);
      super.fontRenderer.drawString(this.progressLabel, 16, 20, 4210752);
      super.fontRenderer.drawString(this.container.tileEntity.getProgressAsString(), 16, 28, 4210752);
      if(this.container.tileEntity.scrap > 0) {
         super.fontRenderer.drawString(this.amplifierLabel, 16, 44, 4210752);
         super.fontRenderer.drawString("" + this.container.tileEntity.scrap, 16, 56, 4210752);
      }

   }

   protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
      int i = super.mc.renderEngine.getTexture("/ic2/sprites/GUIMatter.png");
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      super.mc.renderEngine.bindTexture(i);
      int j = (super.width - super.xSize) / 2;
      int k = (super.height - super.ySize) / 2;
      this.drawTexturedModalRect(j, k, 0, 0, super.xSize, super.ySize);
   }
}
