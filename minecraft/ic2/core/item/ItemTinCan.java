package ic2.core.item;

import ic2.core.Ic2Items;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class ItemTinCan extends ItemFood {

   public ItemTinCan(int id, int index) {
      super(id, 2, 0.95F, false);
      this.setHasSubtypes(true);
      super.iconIndex = index;
   }

   public ItemStack onFoodEaten(ItemStack itemstack, World world, EntityPlayer entityplayer) {
      super.onFoodEaten(itemstack, world, entityplayer);
      this.onEaten(entityplayer);
      return itemstack;
   }

   public void onEaten(EntityPlayer entityplayer) {
      entityplayer.heal(2);
      ItemStack is = Ic2Items.tinCan.copy();
      if(!entityplayer.inventory.addItemStackToInventory(is)) {
         entityplayer.dropPlayerItem(is);
      }

   }

   public void func_77849_c(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) {
      switch(par1ItemStack.getItemDamage()) {
      case 1:
         if(par3EntityPlayer.getRNG().nextFloat() < 0.8F) {
            par3EntityPlayer.addPotionEffect(new PotionEffect(Potion.hunger.id, 600 / (((ItemFood)Item.rottenFlesh).getHealAmount() / 2), 0));
         }
         break;
      case 2:
         par3EntityPlayer.addPotionEffect(new PotionEffect(Potion.poison.id, 100 / (((ItemFood)Item.spiderEye).getHealAmount() / 2), 0));
         break;
      case 3:
         if(par3EntityPlayer.getRNG().nextFloat() < 0.3F) {
            par3EntityPlayer.addPotionEffect(new PotionEffect(Potion.hunger.id, 600 / (((ItemFood)Item.chickenRaw).getHealAmount() / 2), 0));
         }
         break;
      case 4:
         par3EntityPlayer.addPotionEffect(new PotionEffect(Potion.regeneration.id, 100 / (((ItemFood)Item.appleGold).getHealAmount() / 2), 0));
         break;
      case 5:
         par3EntityPlayer.addPotionEffect(new PotionEffect(Potion.regeneration.id, 600 / (((ItemFood)Item.appleGold).getHealAmount() / 2), 3));
         par3EntityPlayer.addPotionEffect(new PotionEffect(Potion.resistance.id, 6000 / (((ItemFood)Item.appleGold).getHealAmount() / 2), 0));
         par3EntityPlayer.addPotionEffect(new PotionEffect(Potion.fireResistance.id, 6000 / (((ItemFood)Item.appleGold).getHealAmount() / 2), 0));
      }

   }

   public void addInformation(ItemStack stack, EntityPlayer player, List info, boolean debugTooltips) {
      int meta = stack.getItemDamage();
      if(meta == 1 || meta == 2 || meta == 3) {
         info.add("Выглядит плохо...");
      }

   }

   public int getMaxItemUseDuration(ItemStack itemstack) {
      return 20;
   }

   public String getTextureFile() {
      return "/ic2/sprites/item_0.png";
   }
}
