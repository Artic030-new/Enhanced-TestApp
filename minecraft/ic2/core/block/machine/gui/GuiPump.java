package ic2.core.block.machine.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.block.machine.ContainerPump;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GuiPump extends GuiContainer {

   public ContainerPump container;
   public String name;
   public String inv;


   public GuiPump(ContainerPump container) {
      super(container);
      this.container = container;
      this.name = StatCollector.translateToLocal("blockPump.name");
      this.inv = StatCollector.translateToLocal("container.inventory");
   }

   protected void drawGuiContainerForegroundLayer(int par1, int par2) {
      super.fontRenderer.drawString(this.name, (super.xSize - super.fontRenderer.getStringWidth(this.name)) / 2, 6, 4210752);
      super.fontRenderer.drawString(this.inv, 8, super.ySize - 96 + 2, 4210752);
   }

   protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
      int i = super.mc.renderEngine.getTexture("/ic2/sprites/GUIPump.png");
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      super.mc.renderEngine.bindTexture(i);
      int j = (super.width - super.xSize) / 2;
      int k = (super.height - super.ySize) / 2;
      this.drawTexturedModalRect(j, k, 0, 0, super.xSize, super.ySize);
      int i1;
      if(this.container.tileEntity.energy > 0) {
         i1 = this.container.tileEntity.energy * 14 / 200;
         if(i1 > 14) {
            i1 = 14;
         }

         this.drawTexturedModalRect(j + 62, k + 36 + 14 - i1, 176, 14 - i1, 14, i1);
      }

      i1 = this.container.tileEntity.pumpCharge * 41 / 200;
      if(i1 > 41) {
         i1 = 41;
      }

      this.drawTexturedModalRect(j + 99, k + 61 - i1, 176, 55, 12, 5);
      if(i1 > 0) {
         this.drawTexturedModalRect(j + 99, k + 25 + 41 - i1, 176, 14, 12, i1);
      }

      this.drawTexturedModalRect(j + 98, k + 19, 188, 14, 13, 47);
   }
}
