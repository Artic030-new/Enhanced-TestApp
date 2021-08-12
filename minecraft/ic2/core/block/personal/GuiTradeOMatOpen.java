package ic2.core.block.personal;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.block.personal.ContainerTradeOMatOpen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GuiTradeOMatOpen extends GuiContainer {

   public ContainerTradeOMatOpen container;
   public String name;
   public String totalTradesLabel0;
   public String totalTradesLabel1;
   public String inv;


   public GuiTradeOMatOpen(ContainerTradeOMatOpen container) {
      super(container);
      this.container = container;
      this.name = StatCollector.translateToLocal("blockPersonalTrader.name");
      this.totalTradesLabel0 = StatCollector.translateToLocal("container.personalTrader.totalTrades0");
      this.totalTradesLabel1 = StatCollector.translateToLocal("container.personalTrader.totalTrades1");
      this.inv = StatCollector.translateToLocal("container.inventory");
   }

   protected void drawGuiContainerForegroundLayer(int par1, int par2) {
      super.fontRenderer.drawString(this.name, (super.xSize - super.fontRenderer.getStringWidth(this.name)) / 2, 6, 4210752);
      super.fontRenderer.drawString(this.inv, 8, super.ySize - 96 + 2, 4210752);
      super.fontRenderer.drawString(this.totalTradesLabel0, 112, 20, 4210752);
      super.fontRenderer.drawString(this.totalTradesLabel1, 112, 28, 4210752);
      super.fontRenderer.drawString("" + this.container.tileEntity.totalTradeCount, 112, 36, 4210752);
   }

   protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
      int i = super.mc.renderEngine.getTexture("/ic2/sprites/GUITradeOMatOpen.png");
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      super.mc.renderEngine.bindTexture(i);
      int j = (super.width - super.xSize) / 2;
      int k = (super.height - super.ySize) / 2;
      this.drawTexturedModalRect(j, k, 0, 0, super.xSize, super.ySize);
   }
}