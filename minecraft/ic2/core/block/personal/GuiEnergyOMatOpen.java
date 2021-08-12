package ic2.core.block.personal;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.IC2;
import ic2.core.block.personal.ContainerEnergyOMatOpen;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiSmallButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GuiEnergyOMatOpen extends GuiContainer {

   public ContainerEnergyOMatOpen container;
   public String name;
   public String offerLabel;
   public String inv;


   public GuiEnergyOMatOpen(ContainerEnergyOMatOpen container) {
      super(container);
      this.container = container;
      this.name = StatCollector.translateToLocal("blockPersonalTraderEnergy.name");
      this.offerLabel = StatCollector.translateToLocal("container.personalTrader.offer");
      this.inv = StatCollector.translateToLocal("container.inventory");
   }

   protected void drawGuiContainerForegroundLayer(int par1, int par2) {
      super.fontRenderer.drawString(this.name, (super.xSize - super.fontRenderer.getStringWidth(this.name)) / 2, 6, 4210752);
      super.fontRenderer.drawString(this.inv, 8, super.ySize - 96 + 2, 4210752);
      super.fontRenderer.drawString(this.offerLabel, 112, 20, 4210752);
      super.fontRenderer.drawString(this.container.tileEntity.euOffer + " EU", 112, 28, 4210752);
   }

   protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
      int i = super.mc.renderEngine.getTexture("/ic2/sprites/GUIEnergyOMatOpen.png");
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      super.mc.renderEngine.bindTexture(i);
      int j = (super.width - super.xSize) / 2;
      int k = (super.height - super.ySize) / 2;
      this.drawTexturedModalRect(j, k, 0, 0, super.xSize, super.ySize);
   }

   public void initGui() {
      super.initGui();
      super.controlList.add(new GuiSmallButton(0, super.guiLeft + 112, super.guiTop + 43, 28, 12, "-1000"));
      super.controlList.add(new GuiSmallButton(1, super.guiLeft + 140, super.guiTop + 43, 28, 12, "-100"));
      super.controlList.add(new GuiSmallButton(2, super.guiLeft + 112, super.guiTop + 55, 28, 12, "+1000"));
      super.controlList.add(new GuiSmallButton(3, super.guiLeft + 140, super.guiTop + 55, 28, 12, "+100"));
   }

   protected void actionPerformed(GuiButton guibutton) {
      IC2.network.initiateClientTileEntityEvent(this.container.tileEntity, guibutton.id);
      super.actionPerformed(guibutton);
   }
}
