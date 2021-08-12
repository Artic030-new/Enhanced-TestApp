package ic2.core.block.machine.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.block.machine.ContainerElectricMachine;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GuiExtractor extends GuiContainer {

   public ContainerElectricMachine container;
   public String name;
   public String inv;


   public GuiExtractor(ContainerElectricMachine container) {
      super(container);
      this.container = container;
      this.name = StatCollector.translateToLocal("blockExtractor.name");
      this.inv = StatCollector.translateToLocal("container.inventory");
   }

   protected void drawGuiContainerForegroundLayer(int par1, int par2) {
      super.fontRenderer.drawString(this.name, (super.xSize - super.fontRenderer.getStringWidth(this.name)) / 2, 6, 4210752);
      super.fontRenderer.drawString(this.inv, 8, super.ySize - 96 + 2, 4210752);
   }

   protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
      int i = super.mc.renderEngine.getTexture("/ic2/sprites/GUIExtractor.png");
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      super.mc.renderEngine.bindTexture(i);
      int j = (super.width - super.xSize) / 2;
      int k = (super.height - super.ySize) / 2;
      this.drawTexturedModalRect(j, k, 0, 0, super.xSize, super.ySize);
      int chargeLevel = (int)(14.0F * this.container.tileEntity.getChargeLevel());
      int progress = (int)(24.0F * this.container.tileEntity.getProgress());
      if(chargeLevel > 0) {
         this.drawTexturedModalRect(j + 56, k + 36 + 14 - chargeLevel, 176, 14 - chargeLevel, 14, chargeLevel);
      }

      if(progress > 0) {
         this.drawTexturedModalRect(j + 79, k + 34, 176, 14, progress + 1, 16);
      }

   }
}
