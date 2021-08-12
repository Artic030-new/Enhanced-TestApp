package ic2.core.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.IC2;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.world.World;

public class ItemTerraWart extends ItemFood {

   public ItemTerraWart(int id, int index) {
      super(id, 0, 1.0F, false);
      super.iconIndex = index;
      this.setAlwaysEdible();
      this.setCreativeTab(IC2.tabIC2);
   }

   public ItemStack onFoodEaten(ItemStack itemstack, World world, EntityPlayer player) {
      --itemstack.stackSize;
      world.playSoundAtEntity(player, "random.burp", 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);
      IC2.platform.removePotion(player, Potion.confusion.id);
      IC2.platform.removePotion(player, Potion.digSlowdown.id);
      IC2.platform.removePotion(player, Potion.hunger.id);
      IC2.platform.removePotion(player, Potion.moveSlowdown.id);
      IC2.platform.removePotion(player, Potion.weakness.id);
      return itemstack;
   }

   @SideOnly(Side.CLIENT)
   public EnumRarity getRarity(ItemStack stack) {
      return EnumRarity.rare;
   }

   public String getTextureFile() {
      return "/ic2/sprites/item_0.png";
   }
}
