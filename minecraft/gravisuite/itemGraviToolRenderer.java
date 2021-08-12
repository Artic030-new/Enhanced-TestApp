package gravisuite;

import gravisuite.ItemGraviTool;
import gravisuite.util.CustomRender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import net.minecraftforge.client.IItemRenderer.ItemRendererHelper;

public class itemGraviToolRenderer implements IItemRenderer {

   private static RenderItem renderItem = new RenderItem();

   public boolean handleRenderType(ItemStack itemStack, ItemRenderType type) {
      return type == ItemRenderType.INVENTORY?true:type == ItemRenderType.EQUIPPED;
   }

   public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
      return false;
   }

   public void renderItem(ItemRenderType type, ItemStack itemStack, Object ... data) {
      FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
      Integer iconIndex = ItemGraviTool.readTextureIndex(itemStack);
      if(type == ItemRenderType.INVENTORY) {
         renderItem.renderTexturedQuad(0, 0, iconIndex.intValue() % 16 * 16, iconIndex.intValue() / 16 * 16, 16, 16);
      }

      if(type == ItemRenderType.EQUIPPED) {
         CustomRender.renderItem("/gravisuite/gravi_items.png", iconIndex.intValue(), itemStack, 1);
      }

   }

}
