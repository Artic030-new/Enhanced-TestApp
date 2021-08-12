package cpw.mods.ironchest.client;

import cpw.mods.ironchest.ContainerIronChestBase;
import cpw.mods.ironchest.IronChestType;
import cpw.mods.ironchest.TileEntityIronChest;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import org.lwjgl.opengl.GL11;

public class GUIChest extends GuiContainer {

   private GUIChest.GUI type;

   public int getRowLength() {
      return this.type.mainType.getRowLength();
   }

   private GUIChest(GUIChest.GUI type, IInventory player, IInventory chest) {
      super(type.makeContainer(player, chest));
      this.type = type;
      super.xSize = type.xSize;
      super.ySize = type.ySize;
      super.allowUserInput = false;
   }

   protected void drawGuiContainerBackgroundLayer(float partialTick, int mouseX, int mouseY) {
      int tex = super.mc.renderEngine.getTexture(this.type.guiTexture);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      super.mc.renderEngine.bindTexture(tex);
      int x = (super.width - super.xSize) / 2;
      int y = (super.height - super.ySize) / 2;
      this.drawTexturedModalRect(x, y, 0, 0, super.xSize, super.ySize);
   }

   public static enum GUI {
	  IRON(184, 202, "/cpw/mods/ironchest/sprites/ironcontainer.png", IronChestType.IRON),
	  GOLD(184, 256, "/cpw/mods/ironchest/sprites/goldcontainer.png", IronChestType.GOLD),
	  DIAMOND(238, 256, "/cpw/mods/ironchest/sprites/diamondcontainer.png", IronChestType.DIAMOND),
	  COPPER(184, 184, "/cpw/mods/ironchest/sprites/coppercontainer.png", IronChestType.COPPER),
	  SILVER(184, 238, "/cpw/mods/ironchest/sprites/silvercontainer.png", IronChestType.SILVER),
	  CRYSTAL(238, 256, "/cpw/mods/ironchest/sprites/diamondcontainer.png", IronChestType.CRYSTAL),
	  OBSIDIAN(238, 256, "/cpw/mods/ironchest/sprites/diamondcontainer.png", IronChestType.OBSIDIAN);
	  
      private int xSize;
      private int ySize;
      private String guiTexture;
      private IronChestType mainType;
      
      private GUI(int xSize, int ySize, String guiTexture, IronChestType mainType) {
         this.xSize = xSize;
         this.ySize = ySize;
         this.guiTexture = guiTexture;
         this.mainType = mainType;
      }

      protected Container makeContainer(IInventory player, IInventory chest) {
         return new ContainerIronChestBase(player, chest, this.mainType, this.xSize, this.ySize);
      }

      public static GUIChest buildGUI(IronChestType type, IInventory playerInventory, TileEntityIronChest chestInventory) {
         return new GUIChest(values()[chestInventory.getType().ordinal()], playerInventory, chestInventory);
      }

   }
}
