package ic2.advancedmachines.common;

import ic2.advancedmachines.common.AdvancedMachines;
import ic2.advancedmachines.common.ContainerRotaryMacerator;
import ic2.advancedmachines.common.TileEntityAdvancedMachine;
import ic2.api.Ic2Recipes;
import ic2.api.Items;
import java.util.List;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class TileEntityRotaryMacerator extends TileEntityAdvancedMachine {

   public int supplementedItemsLeft = 0;
   private int currentResultCount;
   private int idIronDust;
   private int idCopperDust;
   private int idTinDust;
   private int idCoalDust;
   private int idWaterCell;
   private ItemStack bronzeDust;
   private ItemStack hydratedCoalDust;

   public TileEntityRotaryMacerator() {
      super("Rotary Macerator", "%5d RPM", 1, new int[]{1}, new int[]{2, 3});
      this.idIronDust = Items.getItem("ironDust").itemID;
      this.idCopperDust = Items.getItem("copperDust").itemID;
      this.idTinDust = Items.getItem("tinDust").itemID;
      this.idCoalDust = Items.getItem("coalDust").itemID;
      this.idWaterCell = Items.getItem("waterCell").itemID;
      this.bronzeDust = Items.getItem("bronzeDust");
      this.hydratedCoalDust = Items.getItem("hydratedCoalDust");
   }

   public Container getGuiContainer(InventoryPlayer player) {
      return new ContainerRotaryMacerator(player, this);
   }

   protected List getResultMap() {
      return Ic2Recipes.getMaceratorRecipes();
   }

   public ItemStack getResultFor(ItemStack macerated, boolean adjustOutput) {
      ItemStack result = Ic2Recipes.getMaceratorOutputFor(macerated, adjustOutput);
      ItemStack supplement = super.inventory[8] != null?super.inventory[8].copy():null;
      if(supplement != null) {
         if(this.supplementedItemsLeft > 0) {
            result = this.getSpecialResultFor(macerated, result, supplement, adjustOutput);
         } else if(this.getSpecialResultFor(macerated, result, supplement, adjustOutput) != null) {
            result = this.getSpecialResultFor(macerated, result, supplement, adjustOutput);
            this.supplementedItemsLeft = this.currentResultCount;
         }
      }

      return result;
   }

   public void onFinishedProcessingItem() {
      if(this.supplementedItemsLeft != 0) {
         if(this.supplementedItemsLeft == 1) {
            --super.inventory[8].stackSize;
            if(super.inventory[8].stackSize == 0) {
               super.inventory[8] = null;
            }
         }

         --this.supplementedItemsLeft;
      }

      super.onFinishedProcessingItem();
   }

   private ItemStack getSpecialResultFor(ItemStack original, ItemStack result, ItemStack supplement, boolean bool) {
      if(result != null && supplement != null) {
         ItemStack supplementOutput = Ic2Recipes.getMaceratorOutputFor(supplement, bool);
         if(result.itemID == this.idIronDust && supplement.itemID == Item.coal.itemID) {
            this.currentResultCount = 128;
            return new ItemStack(AdvancedMachines.refinedIronDust, result.stackSize);
         }

         if(result.itemID == this.idCopperDust && supplementOutput != null && supplementOutput.itemID == this.idTinDust) {
            this.currentResultCount = 4;
            return new ItemStack(this.bronzeDust.getItem(), result.stackSize);
         }

         if(result.itemID == this.idCoalDust && supplement.itemID == this.idWaterCell) {
            this.currentResultCount = 8;
            return this.hydratedCoalDust;
         }
      }

      return null;
   }

   public int getUpgradeSlotsStartSlot() {
      return 4;
   }

   public String getStartSoundFile() {
      return AdvancedMachines.advMaceSound;
   }

   public String getInterruptSoundFile() {
      return AdvancedMachines.interruptSound;
   }
}