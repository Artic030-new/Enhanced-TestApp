package ic2.core.block.personal;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.block.personal.ContainerTradeOMatClosed;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GuiTradeOMatClosed extends GuiContainer {

   public ContainerTradeOMatClosed container;
   public String name;
   public String wantLabel;
   public String offerLabel;
   public String stockLabel;
   public String inv;


   public GuiTradeOMatClosed(ContainerTradeOMatClosed container) {
      super(container);
      this.container = container;
      this.name = StatCollector.translateToLocal("blockPersonalTrader.name");
      this.wantLabel = StatCollector.translateToLocal("container.personalTrader.want");
      this.offerLabel = StatCollector.translateToLocal("container.personalTrader.offer");
      this.stockLabel = StatCollector.translateToLocal("container.personalTrader.stock");
      this.inv = StatCollector.translateToLocal("container.inventory");
   }

   protected void drawGuiContainerForegroundLayer(int par1, int par2) {
      super.fontRenderer.drawString(this.name, (super.xSize - super.fontRenderer.getStringWidth(this.name)) / 2, 6, 4210752);
      super.fontRenderer.drawString(this.inv, 8, super.ySize - 96 + 2, 4210752);
      super.fontRenderer.drawString(this.wantLabel, 12, 23, 4210752);
      super.fontRenderer.drawString(this.offerLabel, 12, 42, 4210752);
      super.fontRenderer.drawString(this.stockLabel, 12, 60, 4210752);
      super.fontRenderer.drawString("" + this.container.tileEntity.stock, 50, 60, this.container.tileEntity.stock > 0?4210752:16733525);
   }

   protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
      int i = super.mc.renderEngine.getTexture("/ic2/sprites/GUITradeOMatClosed.png");
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      super.mc.renderEngine.bindTexture(i);
      int j = (super.width - super.xSize) / 2;
      int k = (super.height - super.ySize) / 2;
      this.drawTexturedModalRect(j, k, 0, 0, super.xSize, super.ySize);
   }
}
