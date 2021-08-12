package ic2.core.block.machine.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.block.machine.ContainerIronFurnace;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GuiIronFurnace extends GuiContainer {

   public ContainerIronFurnace container;
   public String name;
   public String inv;


   public GuiIronFurnace(ContainerIronFurnace container) {
      super(container);
      this.container = container;
      this.name = StatCollector.translateToLocal("blockIronFurnace.name");
      this.inv = StatCollector.translateToLocal("container.inventory");
   }

   protected void drawGuiContainerForegroundLayer(int par1, int par2) {
      super.fontRenderer.drawString(this.name, (super.xSize - super.fontRenderer.getStringWidth(this.name)) / 2, 6, 4210752);
      super.fontRenderer.drawString(this.inv, 8, super.ySize - 96 + 2, 4210752);
   }

   protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
      int i = super.mc.renderEngine.getTexture("/gui/furnace.png");
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      super.mc.renderEngine.bindTexture(i);
      int j = (super.width - super.xSize) / 2;
      int k = (super.height - super.ySize) / 2;
      this.drawTexturedModalRect(j, k, 0, 0, super.xSize, super.ySize);
      int i1;
      if(this.container.tileEntity.fuel > 0) {
         i1 = this.container.tileEntity.gaugeFuelScaled(12);
         this.drawTexturedModalRect(j + 56, k + 36 + 12 - i1, 176, 12 - i1, 14, i1 + 2);
      }

      i1 = this.container.tileEntity.gaugeProgressScaled(24);
      this.drawTexturedModalRect(j + 79, k + 34, 176, 14, i1 + 1, 16);
   }
}
