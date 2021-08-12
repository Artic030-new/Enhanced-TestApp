package advsolar;

import advsolar.ContainerAdvSolarPanel;
import advsolar.TileEntitySolarPanel;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import org.lwjgl.opengl.GL11;

public class GuiAdvSolarPanel extends GuiContainer {

   public TileEntitySolarPanel tileentity;

   public GuiAdvSolarPanel(InventoryPlayer player, TileEntitySolarPanel tileentitysolarpanel) {
      super(new ContainerAdvSolarPanel(player, tileentitysolarpanel));
      this.tileentity = tileentitysolarpanel;
      super.allowUserInput = false;
      super.xSize = 194;
      super.ySize = 168;
   }

   protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
      int nmPos = (super.xSize - this.fontRenderer.getStringWidth(this.tileentity.panelName)) / 2;
      this.fontRenderer.drawString(this.tileentity.panelName, nmPos, 7, 7718655);
      this.fontRenderer.drawString("Энергия: " + this.tileentity.storage + "/" + this.tileentity.maxStorage, 50, 22, 13487565);
      this.fontRenderer.drawString("Выход: " + this.tileentity.production + " еЭ/т", 50, 32, 13487565);
      this.fontRenderer.drawString("Генерация: " + this.tileentity.generating + " еЭ/т", 50, 42, 13487565);
   }

   protected void drawGuiContainerBackgroundLayer(float partialTick, int mouseX, int mouseY) {
      int c = super.mc.renderEngine.getTexture("/advsolar/texture/GUIAdvancedSolarPanel.png");
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.renderEngine.bindTexture(c);
      int h = (super.width - super.xSize) / 2;
      int k = (super.height - super.ySize) / 2;
      this.drawTexturedModalRect(h, k, 0, 0, super.xSize, super.ySize);
      if(this.tileentity.storage > 0) {
         int l = this.tileentity.gaugeEnergyScaled(24);
         this.drawTexturedModalRect(h + 19, k + 24, 195, 0, l + 1, 14);
      }

      if(this.tileentity.skyIsVisible) {
         if(this.tileentity.sunIsUp) {
            this.drawTexturedModalRect(h + 24, k + 42, 195, 15, 14, 14);
         } else if(!this.tileentity.sunIsUp) {
            this.drawTexturedModalRect(h + 24, k + 42, 210, 15, 14, 14);
         }
      }

   }
}
