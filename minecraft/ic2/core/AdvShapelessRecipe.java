package ic2.core;

import ic2.api.IElectricItem;
import ic2.core.AdvRecipe;
import ic2.core.item.ElectricItem;
import ic2.core.util.Util;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class AdvShapelessRecipe implements IRecipe {

   public ItemStack output;
   public Object[] input;
   public boolean hidden;


   public static void addAndRegister(ItemStack result, Object ... args) {
      CraftingManager.getInstance().getRecipeList().add(new AdvShapelessRecipe(result, args));
   }

   public AdvShapelessRecipe(ItemStack result, Object ... args) {
      if(result == null) {
         AdvRecipe.displayError("null result", (String)null, (ItemStack)null, true);
      }

      this.input = new Object[args.length - Util.countInArray(args, Boolean.class)];
      int inputIndex = 0;
      Object[] arr$ = args;
      int len$ = args.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         Object o = arr$[i$];
         if(o instanceof String) {
            this.input[inputIndex++] = o;
         } else if(!(o instanceof ItemStack) && !(o instanceof Block) && !(o instanceof Item)) {
            if(o instanceof Boolean) {
               this.hidden = ((Boolean)o).booleanValue();
            } else {
               AdvRecipe.displayError("unknown type", "O: " + o + "\nT: " + (o == null?"null":o.getClass().getName()), result, true);
            }
         } else {
            if(o instanceof Block) {
               o = new ItemStack((Block)o, 1, -1);
            } else if(o instanceof Item) {
               o = new ItemStack((Item)o, 1, -1);
            }

            this.input[inputIndex++] = o;
         }
      }

      if(inputIndex != this.input.length) {
         AdvRecipe.displayError("length calculation error", "I: " + inputIndex + "\nL: " + this.input.length, result, true);
      }

      this.output = result;
   }

   public boolean matches(InventoryCrafting inventorycrafting, World world) {
      return this.getCraftingResult(inventorycrafting) != null;
   }

   public ItemStack getCraftingResult(InventoryCrafting inventorycrafting) {
      int offerSize = inventorycrafting.getSizeInventory();
      if(offerSize < this.input.length) {
         return null;
      } else {
         Vector unmatched = new Vector();
         Object[] outputCharge = this.input;
         int ret = outputCharge.length;

         for(int offer = 0; offer < ret; ++offer) {
            Object j = outputCharge[offer];
            unmatched.add(j);
         }

         int var11 = 0;

         label67:
         for(ret = 0; ret < offerSize; ++ret) {
            ItemStack var13 = inventorycrafting.getStackInSlot(ret);
            if(var13 != null) {
               int var14 = 0;

               label62:
               while(var14 < unmatched.size()) {
                  List requestedItemStacks = AdvRecipe.resolveOreDict(unmatched.get(var14));
                  Iterator i$ = requestedItemStacks.iterator();

                  ItemStack requestedItemStack;
                  label59:
                  do {
                     do {
                        if(!i$.hasNext()) {
                           ++var14;
                           continue label62;
                        }

                        requestedItemStack = (ItemStack)i$.next();
                        if(!(var13.getItem() instanceof IElectricItem)) {
                           continue label59;
                        }
                     } while(var13.itemID != requestedItemStack.itemID);

                     var11 += ElectricItem.discharge(var13, Integer.MAX_VALUE, Integer.MAX_VALUE, true, true);
                     unmatched.remove(var14);
                     continue label67;
                  } while(!var13.isItemEqual(requestedItemStack) && (requestedItemStack.getItemDamage() != -1 || var13.itemID != requestedItemStack.itemID));

                  unmatched.remove(var14);
                  continue label67;
               }

               return null;
            }
         }

         if(!unmatched.isEmpty()) {
            return null;
         } else {
            ItemStack var12 = this.output.copy();
            if(var12.getItem() instanceof IElectricItem && var11 != 0) {
               ElectricItem.charge(var12, var11, Integer.MAX_VALUE, true, false);
            }

            return var12;
         }
      }
   }

   public int getRecipeSize() {
      return this.input.length;
   }

   public ItemStack getRecipeOutput() {
      return this.output;
   }
}
