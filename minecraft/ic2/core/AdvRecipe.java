package ic2.core;

import ic2.api.IElectricItem;
import ic2.core.AdvShapelessRecipe;
import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.item.ElectricItem;
import java.util.HashMap;
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
import net.minecraftforge.liquids.LiquidContainerData;
import net.minecraftforge.liquids.LiquidContainerRegistry;
import net.minecraftforge.oredict.OreDictionary;

public class AdvRecipe implements IRecipe {

   public ItemStack output;
   public Object[] input;
   public int inputWidth;
   public boolean hidden;


   public static void addAndRegister(ItemStack result, Object ... args) {
      CraftingManager.getInstance().getRecipeList().add(new AdvRecipe(result, args));
   }

   public AdvRecipe(ItemStack result, Object ... args) {
      if(result == null) {
         displayError("null result", (String)null, (ItemStack)null, false);
      }

      HashMap charMapping = new HashMap();
      Vector inputArrangement = new Vector();
      Character lastChar = null;
      Object[] inputIndex = args;
      int i$ = args.length;

      for(int str = 0; str < i$; ++str) {
         Object i = inputIndex[str];
         if(i instanceof String) {
            if(lastChar == null) {
               if(!charMapping.isEmpty()) {
                  displayError("oredict name without preceding char", "N: " + i, result, false);
               }

               inputArrangement.add((String)i);
            } else {
               charMapping.put(lastChar, i);
               lastChar = null;
            }
         } else if(i instanceof Character) {
            if(lastChar != null) {
               displayError("two consecutive char definitions", "O: " + i + "\nC: " + lastChar, result, false);
            }

            lastChar = (Character)i;
         } else if(!(i instanceof ItemStack) && !(i instanceof Block) && !(i instanceof Item)) {
            if(i instanceof Boolean) {
               this.hidden = ((Boolean)i).booleanValue();
            } else {
               displayError("unknown type", "O: " + i + "\nT: " + (i == null?"null":i.getClass().getName()), result, false);
            }
         } else {
            if(lastChar == null) {
               displayError("item without preceding char", "O: " + i + "\nT: " + (i == null?"null":i.getClass().getName()) + "\nC: " + lastChar, result, false);
            }

            if(i instanceof Block) {
               i = new ItemStack((Block)i, 1, -1);
            } else if(i instanceof Item) {
               i = new ItemStack((Item)i, 1, -1);
            }

            charMapping.put(lastChar, i);
            lastChar = null;
         }
      }

      if(lastChar != null) {
         displayError("one or more unused mapping chars", "L: " + lastChar, result, false);
      }

      if(inputArrangement.size() == 0 || inputArrangement.size() > 3) {
         displayError("none or too many crafting rows", "S: " + inputArrangement.size(), result, false);
      }

      if(charMapping.size() == 0) {
         displayError("no mapping chars", (String)null, result, false);
      }

      this.inputWidth = ((String)inputArrangement.get(0)).length();
      this.input = new Object[this.inputWidth * inputArrangement.size()];
      int var11 = 0;
      Iterator var12 = inputArrangement.iterator();

      while(var12.hasNext()) {
         String var13 = (String)var12.next();
         if(var13.length() != this.inputWidth) {
            displayError("no fixed width", "W: " + this.inputWidth + "\nL: " + var13.length(), result, false);
         }

         for(int var14 = 0; var14 < var13.length(); ++var14) {
            char c = var13.charAt(var14);
            if(c == 32) {
               this.input[var11++] = null;
            } else {
               if(!charMapping.containsKey(Character.valueOf(c))) {
                  displayError("missing char mapping", "C: " + c, result, false);
               }

               this.input[var11++] = charMapping.get(Character.valueOf(c));
            }
         }
      }

      this.output = result;
   }

   public boolean matches(InventoryCrafting inventorycrafting, World world) {
      return this.getCraftingResult(inventorycrafting) != null;
   }

   public ItemStack getCraftingResult(InventoryCrafting inventorycrafting) {
      int inputHeight = this.input.length / this.inputWidth;
      int offerSize = inventorycrafting.getSizeInventory() == 9?3:2;
      if(offerSize >= this.inputWidth && offerSize >= inputHeight) {
         for(int xOffset = 0; xOffset <= offerSize - this.inputWidth; ++xOffset) {
            int yOffset = 0;

            label162:
            while(yOffset <= offerSize - inputHeight) {
               int outputCharge = 0;

               int ret;
               int x;
               for(ret = 0; ret < this.inputWidth; ++ret) {
                  for(x = 0; x < inputHeight; ++x) {
                     ItemStack offer = inventorycrafting.getStackInRowAndColumn(ret + xOffset, x + yOffset);
                     Object request = this.input[ret + x * this.inputWidth];
                     if(offer == null && request != null) {
                        ++yOffset;
                        continue label162;
                     }

                     if(offer != null) {
                        if(request == null) {
                           return null;
                        }

                        List requestedItemStacks = resolveOreDict(request);
                        boolean found = false;
                        Iterator i$ = requestedItemStacks.iterator();

                        while(i$.hasNext()) {
                           ItemStack requestedItemStack = (ItemStack)i$.next();
                           if(offer.getItem() instanceof IElectricItem) {
                              if(offer.itemID == requestedItemStack.itemID) {
                                 outputCharge += ElectricItem.discharge(offer, Integer.MAX_VALUE, Integer.MAX_VALUE, true, true);
                                 found = true;
                                 break;
                              }
                           } else if(offer.isItemEqual(requestedItemStack) || requestedItemStack.getItemDamage() == -1 && offer.itemID == requestedItemStack.itemID) {
                              found = true;
                              break;
                           }
                        }

                        if(!found) {
                           return null;
                        }
                     }
                  }
               }

               for(ret = 0; ret < xOffset; ++ret) {
                  for(x = 0; x < offerSize; ++x) {
                     if(inventorycrafting.getStackInRowAndColumn(ret, x) != null) {
                        return null;
                     }
                  }
               }

               for(ret = 0; ret < yOffset; ++ret) {
                  for(x = 0; x < offerSize; ++x) {
                     if(inventorycrafting.getStackInRowAndColumn(x, ret) != null) {
                        return null;
                     }
                  }
               }

               for(ret = xOffset + this.inputWidth; ret < offerSize; ++ret) {
                  for(x = 0; x < offerSize; ++x) {
                     if(inventorycrafting.getStackInRowAndColumn(ret, x) != null) {
                        return null;
                     }
                  }
               }

               for(ret = yOffset + inputHeight; ret < offerSize; ++ret) {
                  for(x = 0; x < offerSize; ++x) {
                     if(inventorycrafting.getStackInRowAndColumn(x, ret) != null) {
                        return null;
                     }
                  }
               }

               ItemStack var15 = this.output.copy();
               if(var15.getItem() instanceof IElectricItem && outputCharge != 0) {
                  ElectricItem.charge(var15, outputCharge, Integer.MAX_VALUE, true, false);
               }

               return var15;
            }
         }

         return null;
      } else {
         return null;
      }
   }

   public int getRecipeSize() {
      return this.input.length;
   }

   public ItemStack getRecipeOutput() {
      return this.output;
   }

   public static boolean recipeContains(Object[] inputs, ItemStack item) {
      Object[] arr$ = inputs;
      int len$ = inputs.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         Object input = arr$[i$];
         if(input != null) {
            List realInputs = resolveOreDict(input);
            Iterator i$1 = realInputs.iterator();

            while(i$1.hasNext()) {
               ItemStack realInput = (ItemStack)i$1.next();
               if(item.isItemEqual(realInput)) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   public static boolean canShow(Object[] input, ItemStack output, boolean hidden) {
      return (!IC2.enableSecretRecipeHiding || !hidden) && !recipeContains(input, Ic2Items.reBattery) && (!recipeContains(input, Ic2Items.industrialDiamond) || output.itemID != Item.diamond.itemID);
   }

   public static boolean canShow(AdvRecipe recipe) {
      return canShow(recipe.input, recipe.output, recipe.hidden);
   }

   public static boolean canShow(AdvShapelessRecipe recipe) {
      return canShow(recipe.input, recipe.output, recipe.hidden);
   }

   public static List resolveOreDict(Object o) {
      Object ret;
      if(o instanceof String) {
         String s = (String)o;
         if(s.startsWith("liquid$")) {
            ret = new Vector();
            int colon = s.indexOf(58);
            int id;
            int meta;
            if(colon != -1) {
               id = Integer.valueOf(s.substring(7, colon - 1)).intValue();
               meta = Integer.valueOf(s.substring(colon + 1)).intValue();
            } else {
               id = Integer.valueOf(s.substring(7)).intValue();
               meta = -1;
            }

            LiquidContainerData[] arr$ = LiquidContainerRegistry.getRegisteredLiquidContainerData();
            int len$ = arr$.length;

            for(int i$ = 0; i$ < len$; ++i$) {
               LiquidContainerData data = arr$[i$];
               if(data.stillLiquid.itemID == id && (meta == -1 || data.stillLiquid.itemMeta == meta)) {
                  ((List)ret).add(data.filled);
               }
            }
         } else {
            ret = OreDictionary.getOres((String)o);
         }
      } else {
         if(!(o instanceof ItemStack)) {
            displayError("unknown type", "O: " + o + "\nT: " + (o == null?"null":o.getClass().getName()), (ItemStack)null, false);
            return null;
         }

         ret = new Vector(1);
         ((List)ret).add((ItemStack)o);
      }

      return (List)ret;
   }

   public static void displayError(String cause, String tech, ItemStack result, boolean shapeless) {
      IC2.platform.displayError("An invalid crafting recipe was attempted to be added. This could\nhappen due to a bug in IndustrialCraft 2 or an addon.\n\n(Technical information: Adv" + (shapeless?"Shapeless":"") + "Recipe, " + cause + ")\n" + (result != null?"R: " + result + "\n":"") + (tech != null?tech:""));
   }
}
