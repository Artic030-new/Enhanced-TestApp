package ic2.core.block.wiring;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.GuiIconButton;
import ic2.core.IC2;
import ic2.core.block.wiring.ContainerElectricBlock;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraft.util.StringTranslate;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GuiElectricBlock extends GuiContainer {

   public ContainerElectricBlock container;
   public StringTranslate translate;
   public String inv;


   public GuiElectricBlock(ContainerElectricBlock container) {
      super(container);
      this.container = container;
      this.inv = StatCollector.translateToLocal("container.inventory");
   }

   public void initGui() {
      super.initGui();
      super.controlList.add(new GuiIconButton(0, (super.width - super.xSize) / 2 + 152, (super.height - super.ySize) / 2 + 4, 20, 20, new ItemStack(Item.redstone), true));
   }

   protected void drawGuiContainerForegroundLayer(int par1, int par2) {
      String name = this.container.tileEntity.getNameByTier();
      super.fontRenderer.drawString(name, (super.xSize - super.fontRenderer.getStringWidth(name)) / 2, 6, 4210752);
      super.fontRenderer.drawString(this.inv, 8, super.ySize - 96 + 2, 4210752);
      super.fontRenderer.drawString(this.container.tileEntity.translate.translateKey("container.electricBlock.level"), 79, 25, 4210752);
      int e = this.container.tileEntity.energy;
      if(e > this.container.tileEntity.maxStorage) {
         e = this.container.tileEntity.maxStorage;
      }

      super.fontRenderer.drawString(" " + e, 110, 35, 4210752);
      super.fontRenderer.drawString("/" + this.container.tileEntity.maxStorage, 110, 45, 4210752);
      super.fontRenderer.drawString(this.container.tileEntity.translate.translateKeyFormat("container.electricBlock.output", new Object[]{Integer.valueOf(this.container.tileEntity.output)}), 85, 60, 4210752);
   }

   protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
      int i = super.mc.renderEngine.getTexture("/ic2/sprites/GUIElectricBlock.png");
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      super.mc.renderEngine.bindTexture(i);
      int j = (super.width - super.xSize) / 2;
      int k = (super.height - super.ySize) / 2;
      this.drawTexturedModalRect(j, k, 0, 0, super.xSize, super.ySize);
      if(this.container.tileEntity.energy > 0) {
         int i1 = (int)(24.0F * this.container.tileEntity.getChargeLevel());
         this.drawTexturedModalRect(j + 79, k + 34, 176, 14, i1 + 1, 16);
      }

   }

   protected void actionPerformed(GuiButton guibutton) {
      if(guibutton.id == 0) {
         IC2.network.initiateClientTileEntityEvent(this.container.tileEntity, 0);
      }

      super.actionPerformed(guibutton);
   }
}
