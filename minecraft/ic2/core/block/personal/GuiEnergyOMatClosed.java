package ic2.core.block.personal;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.block.personal.ContainerEnergyOMatClosed;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GuiEnergyOMatClosed extends GuiContainer {

   public ContainerEnergyOMatClosed container;
   public String name;
   public String wantLabel;
   public String offerLabel;
   public String paidForLabel;
   public String inv;


   public GuiEnergyOMatClosed(ContainerEnergyOMatClosed container) {
      super(container);
      this.container = container;
      this.name = StatCollector.translateToLocal("blockPersonalTraderEnergy.name");
      this.wantLabel = StatCollector.translateToLocal("container.personalTrader.want");
      this.offerLabel = StatCollector.translateToLocal("container.personalTrader.offer");
      this.paidForLabel = StatCollector.translateToLocal("container.personalTraderEnergy.paidFor");
      this.inv = StatCollector.translateToLocal("container.inventory");
   }

   protected void drawGuiContainerForegroundLayer(int par1, int par2) {
      super.fontRenderer.drawString(this.name, (super.xSize - super.fontRenderer.getStringWidth(this.name)) / 2, 6, 4210752);
      super.fontRenderer.drawString(this.inv, 8, super.ySize - 96 + 2, 4210752);
      super.fontRenderer.drawString(this.wantLabel, 12, 23, 4210752);
      super.fontRenderer.drawString(this.offerLabel, 12, 42, 4210752);
      super.fontRenderer.drawString(this.container.tileEntity.euOffer + " EU", 50, 42, 4210752);
      super.fontRenderer.drawString(StatCollector.translateToLocalFormatted("container.personalTraderEnergy.paidFor", new Object[]{Integer.valueOf(this.container.tileEntity.paidFor)}), 12, 60, 4210752);
   }

   protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
      int i = super.mc.renderEngine.getTexture("/ic2/sprites/GUIEnergyOMatClosed.png");
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      super.mc.renderEngine.bindTexture(i);
      int j = (super.width - super.xSize) / 2;
      int k = (super.height - super.ySize) / 2;
      this.drawTexturedModalRect(j, k, 0, 0, super.xSize, super.ySize);
   }
}
