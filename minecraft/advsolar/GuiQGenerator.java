package advsolar;

import advsolar.ContainerQGenerator;
import advsolar.TileEntityQGenerator;
import ic2.api.network.NetworkHelper;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.src.ModLoader;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class GuiQGenerator extends GuiContainer implements KeyListener {

   public Minecraft f;
   public TileEntityQGenerator tileentity;
   private GuiTextField maxPacketSizeEdit;
   private GuiTextField productonEdit;

   public GuiQGenerator(InventoryPlayer player, TileEntityQGenerator tileentityqgenerator) {
      super(new ContainerQGenerator(player, tileentityqgenerator));
      this.tileentity = tileentityqgenerator;
      super.xSize = 176;
      super.ySize = 193;
      this.f = ModLoader.getMinecraftInstance();
   }

   protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
      int nmPos = (super.xSize - this.fontRenderer.getStringWidth("Квантовый генератор")) / 2;
      super.fontRenderer.drawString("Квантовый генератор", nmPos, 6, 16777215);
      String gen = Integer.toString(this.tileentity.production);
      String mPSize = Integer.toString(this.tileentity.maxPacketSize);
      this.fontRenderer.drawString("Выход:", 54, 24, 16777215);
      this.fontRenderer.drawString("Размер пакета:", 7, 68, 16777215);
      this.fontRenderer.drawString(gen, 140 - this.fontRenderer.getStringWidth(gen), 25, 16777215);
      this.fontRenderer.drawString(mPSize, 140 - this.fontRenderer.getStringWidth(mPSize), 69, 16777215);
   }

   protected void drawGuiContainerBackgroundLayer(float partialTick, int mouseX, int mouseY) {
      int c = this.f.renderEngine.getTexture("/advsolar/texture/GUIQuantumGenerator.png");
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.f.renderEngine.bindTexture(c);
      int h = (super.width - super.xSize) / 2;
      int k = (super.height - super.ySize) / 2;
      this.drawTexturedModalRect(h, k, 0, 0, super.xSize, super.ySize);
      if(!this.tileentity.active) {
         this.drawTexturedModalRect(h + 145, k + 21, 176, 3, 14, 14);
      }

   }

   protected void actionPerformed(GuiButton button) {
      try {
         if(Keyboard.getEventKey() == 42 && Keyboard.getEventKeyState()) {
            NetworkHelper.initiateClientTileEntityEvent(this.tileentity, button.id + 100);
         } else {
            NetworkHelper.initiateClientTileEntityEvent(this.tileentity, button.id);
         }
      } catch (Exception var3) {
         System.out.println(var3.toString());
      }

      super.actionPerformed(button);
   }

   public void initGui() {
      super.initGui();
      int xGuiPos = (super.width - super.xSize) / 2;
      int yGuiPos = (super.height - super.ySize) / 2;
      this.controlList.add(new GuiButton(1, xGuiPos + 6, yGuiPos + 40, 32, 20, "-100"));
      this.controlList.add(new GuiButton(2, xGuiPos + 39, yGuiPos + 40, 26, 20, "-10"));
      this.controlList.add(new GuiButton(3, xGuiPos + 66, yGuiPos + 40, 20, 20, "-1"));
      this.controlList.add(new GuiButton(4, xGuiPos + 89, yGuiPos + 40, 20, 20, "+1"));
      this.controlList.add(new GuiButton(5, xGuiPos + 110, yGuiPos + 40, 26, 20, "+10"));
      this.controlList.add(new GuiButton(6, xGuiPos + 137, yGuiPos + 40, 32, 20, "+100"));
      this.controlList.add(new GuiButton(7, xGuiPos + 6, yGuiPos + 84, 32, 20, "-100"));
      this.controlList.add(new GuiButton(8, xGuiPos + 39, yGuiPos + 84, 26, 20, "-10"));
      this.controlList.add(new GuiButton(9, xGuiPos + 66, yGuiPos + 84, 20, 20, "-1"));
      this.controlList.add(new GuiButton(10, xGuiPos + 89, yGuiPos + 84, 20, 20, "+1"));
      this.controlList.add(new GuiButton(11, xGuiPos + 110, yGuiPos + 84, 26, 20, "+10"));
      this.controlList.add(new GuiButton(12, xGuiPos + 137, yGuiPos + 84, 32, 20, "+100"));
   }

   public void keyPressed(KeyEvent e) {
      System.out.println(e.getKeyCode());
   }

   public void keyReleased(KeyEvent e) {}

   public void keyTyped(KeyEvent e) {}
}
