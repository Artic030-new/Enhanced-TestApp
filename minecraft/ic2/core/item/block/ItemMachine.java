package ic2.core.item.block;

import ic2.core.item.block.ItemBlockRare;
import net.minecraft.item.ItemStack;

public class ItemMachine extends ItemBlockRare {

   public ItemMachine(int i) {
      super(i);
      this.setMaxDamage(0);
      this.setHasSubtypes(true);
   }

   public int getMetadata(int i) {
      return i;
   }

   public String getItemNameIS(ItemStack itemstack) {
      int meta = itemstack.getItemDamage();
      switch(meta) {
      case 0:
         return "blockMachine";
      case 1:
         return "blockIronFurnace";
      case 2:
         return "blockElecFurnace";
      case 3:
         return "blockMacerator";
      case 4:
         return "blockExtractor";
      case 5:
         return "blockCompressor";
      case 6:
         return "blockCanner";
      case 7:
         return "blockMiner";
      case 8:
         return "blockPump";
      case 9:
         return "blockMagnetizer";
      case 10:
         return "blockElectrolyzer";
      case 11:
         return "blockRecycler";
      case 12:
         return "blockAdvMachine";
      case 13:
         return "blockInduction";
      case 14:
         return "blockMatter";
      case 15:
         return "blockTerra";
      default:
         return null;
      }
   }
}
