package ic2.core;

import ic2.core.item.ItemGradual;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class RecipeGradual implements IRecipe {

   public ItemGradual item;
   public ItemStack chargeMaterial;
   public int amount;


   public RecipeGradual(ItemGradual item, ItemStack chargeMaterial, int amount) {
      this.item = item;
      this.chargeMaterial = chargeMaterial;
      this.amount = amount;
      CraftingManager.getInstance().getRecipeList().add(this);
   }

   public boolean matches(InventoryCrafting var1, World world) {
      return this.getCraftingResult(var1) != null;
   }

   public ItemStack getCraftingResult(InventoryCrafting var1) {
      ItemStack gridItem = null;
      int chargeMats = 0;

      for(int stack = 0; stack < var1.getSizeInventory(); ++stack) {
         ItemStack damage = var1.getStackInSlot(stack);
         if(damage != null) {
            if(gridItem == null && damage.getItem() == this.item) {
               gridItem = damage;
            } else {
               if(damage.itemID != this.chargeMaterial.itemID || damage.getItemDamage() != this.chargeMaterial.getItemDamage() && this.chargeMaterial.getItemDamage() != -1) {
                  return null;
               }

               ++chargeMats;
            }
         }
      }

      if(gridItem != null && chargeMats > 0) {
         ItemStack var6 = gridItem.copy();
         int var7 = this.item.getDamageOfStack(var6) - this.amount * chargeMats;
         if(var7 > this.item.getMaxDamageEx()) {
            var7 = this.item.getMaxDamageEx();
         } else if(var7 < 0) {
            var7 = 0;
         }

         this.item.setDamageForStack(var6, var7);
         return var6;
      } else {
         return null;
      }
   }

   public int getRecipeSize() {
      return 2;
   }

   public ItemStack getRecipeOutput() {
      return new ItemStack(this.item);
   }
}
