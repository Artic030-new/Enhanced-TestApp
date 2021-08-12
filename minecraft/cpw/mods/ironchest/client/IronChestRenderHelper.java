package cpw.mods.ironchest.client;

import com.google.common.collect.Maps;
import cpw.mods.ironchest.IronChest;
import cpw.mods.ironchest.IronChestType;
import cpw.mods.ironchest.TileEntityIronChest;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.ChestItemRenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class IronChestRenderHelper extends ChestItemRenderHelper {

   private Map itemRenders = Maps.newHashMap();

   public IronChestRenderHelper() {
      IronChestType[] arr$ = IronChestType.values();
      int len$ = arr$.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         IronChestType typ = arr$[i$];
         this.itemRenders.put(Integer.valueOf(typ.ordinal()), (TileEntityIronChest)IronChest.ironChestBlock.createTileEntity((World)null, typ.ordinal()));
      }

   }

   public void renderChest(Block block, int metadata, float blockName) {
      if(block == IronChest.ironChestBlock) {
         TileEntityRenderer.instance.renderTileEntityAt((TileEntity)this.itemRenders.get(Integer.valueOf(metadata)), 0.0D, 0.0D, 0.0D, 0.0F);
      } else {
         super.renderChest(block, metadata, blockName);
      }

   }
}
