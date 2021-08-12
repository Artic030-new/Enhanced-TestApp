package advsolar;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemAdvSolarPanel extends ItemBlock {

   public ItemAdvSolarPanel(int id) {
      super(id);
      this.setMaxDamage(0);
      this.setHasSubtypes(true);
   }

   protected int damageDropped(int metadata) {
      return metadata;
   }

   public int getMetadata(int metadata) {
      return metadata;
   }

   public String getItemNameIS(ItemStack itemstack) {
	  switch(itemstack.getItemDamage()) {
      case 0:
         return "blockAdvancedSolarPanel";
      case 1:
         return "blockHybridSolarPanel";
      case 2:
         return "blockUltimateSolarPanel";
      case 3:
         return "blockQuantumGenerator";
      default:
         return "blockAdvancedSolarPanel";
      }
   }

   @SideOnly(Side.CLIENT)
   public EnumRarity getRarity(ItemStack itemstack) {
	  switch(itemstack.getItemDamage()) {
      case 0:
         return EnumRarity.uncommon;
      case 1:
         return EnumRarity.rare;
      case 2:
         return EnumRarity.epic;
      case 3:
         return EnumRarity.epic;
      default:
         return EnumRarity.uncommon;
      }
   }
}
