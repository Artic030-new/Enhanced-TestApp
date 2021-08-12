package ic2.core;

import ic2.core.Ic2Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.DungeonHooks;

public class IC2Loot {

   private static final WeightedRandomChestContent[] MINESHAFT_CORRIDOR = new WeightedRandomChestContent[]{new WeightedRandomChestContent(Ic2Items.uraniumDrop.copy(), 1, 2, 1), new WeightedRandomChestContent(Ic2Items.bronzePickaxe.copy(), 1, 1, 1), new WeightedRandomChestContent(Ic2Items.filledTinCan.copy(), 4, 16, 8)};
   private static final WeightedRandomChestContent[] STRONGHOLD_CORRIDOR = new WeightedRandomChestContent[]{new WeightedRandomChestContent(Ic2Items.matter.copy(), 1, 4, 1)};
   private static final WeightedRandomChestContent[] STRONGHOLD_CROSSING = new WeightedRandomChestContent[]{new WeightedRandomChestContent(Ic2Items.bronzePickaxe.copy(), 1, 1, 1)};
   private static final WeightedRandomChestContent[] VILLAGE_BLACKSMITH = new WeightedRandomChestContent[]{new WeightedRandomChestContent(Ic2Items.bronzeIngot.copy(), 2, 4, 5)};
   private static final WeightedRandomChestContent[] BONUS_CHEST = new WeightedRandomChestContent[]{new WeightedRandomChestContent(Ic2Items.treetap.copy(), 1, 1, 2)};
   private static final WeightedRandomChestContent[] bronzeToolsArmor = new WeightedRandomChestContent[]{new WeightedRandomChestContent(Ic2Items.bronzePickaxe.copy(), 1, 1, 3), new WeightedRandomChestContent(Ic2Items.bronzeSword.copy(), 1, 1, 3), new WeightedRandomChestContent(Ic2Items.bronzeHelmet.copy(), 1, 1, 3), new WeightedRandomChestContent(Ic2Items.bronzeChestplate.copy(), 1, 1, 3), new WeightedRandomChestContent(Ic2Items.bronzeLeggings.copy(), 1, 1, 3), new WeightedRandomChestContent(Ic2Items.bronzeBoots.copy(), 1, 1, 3)};
   private static final WeightedRandomChestContent[] ingots = new WeightedRandomChestContent[]{new WeightedRandomChestContent(Ic2Items.copperIngot.copy(), 2, 6, 9), new WeightedRandomChestContent(Ic2Items.tinIngot.copy(), 1, 5, 8)};
   private static WeightedRandomChestContent[] rubberSapling = new WeightedRandomChestContent[0];


   @SuppressWarnings("deprecation")
   public IC2Loot() {
      if(Ic2Items.rubberSapling != null) {
         rubberSapling = new WeightedRandomChestContent[]{new WeightedRandomChestContent(Ic2Items.rubberSapling.copy(), 1, 4, 4)};
      }

      this.addLoot("mineshaftCorridor", new WeightedRandomChestContent[][]{MINESHAFT_CORRIDOR, ingots});
      this.addLoot("pyramidDesertyChest", new WeightedRandomChestContent[][]{bronzeToolsArmor, ingots});
      this.addLoot("pyramidJungleChest", new WeightedRandomChestContent[][]{bronzeToolsArmor, ingots});
      this.addLoot("strongholdCorridor", new WeightedRandomChestContent[][]{STRONGHOLD_CORRIDOR, bronzeToolsArmor, ingots});
      this.addLoot("strongholdCrossing", new WeightedRandomChestContent[][]{STRONGHOLD_CROSSING, bronzeToolsArmor, ingots});
      this.addLoot("villageBlacksmith", new WeightedRandomChestContent[][]{VILLAGE_BLACKSMITH, bronzeToolsArmor, ingots, rubberSapling});
      this.addLoot("bonusChest", new WeightedRandomChestContent[][]{BONUS_CHEST});
      DungeonHooks.addDungeonLoot(Ic2Items.copperIngot.copy(), 100, 2, 5);
      DungeonHooks.addDungeonLoot(Ic2Items.tinIngot.copy(), 100, 2, 5);
      DungeonHooks.addDungeonLoot(Ic2Items.bronzeHoe.copy(), 1);
      DungeonHooks.addDungeonLoot(new ItemStack(Item.recordBlocks), 5);
      DungeonHooks.addDungeonLoot(new ItemStack(Item.recordChirp), 5);
      DungeonHooks.addDungeonLoot(new ItemStack(Item.recordFar), 5);
      DungeonHooks.addDungeonLoot(new ItemStack(Item.recordMall), 5);
      DungeonHooks.addDungeonLoot(new ItemStack(Item.recordMellohi), 5);
      DungeonHooks.addDungeonLoot(new ItemStack(Item.recordStal), 5);
      DungeonHooks.addDungeonLoot(new ItemStack(Item.recordStrad), 5);
      DungeonHooks.addDungeonLoot(new ItemStack(Item.recordWard), 5);
      DungeonHooks.addDungeonLoot(new ItemStack(Item.record11), 5);
      DungeonHooks.addDungeonLoot(new ItemStack(Item.recordWait), 5);
   }

   private void addLoot(String category, WeightedRandomChestContent[] ... loot) {
      ChestGenHooks cgh = ChestGenHooks.getInfo(category);
      WeightedRandomChestContent[][] arr$ = loot;
      int len$ = loot.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         WeightedRandomChestContent[] lootList = arr$[i$];
         WeightedRandomChestContent[] arr$1 = lootList;
         int len$1 = lootList.length;

         for(int i$1 = 0; i$1 < len$1; ++i$1) {
            WeightedRandomChestContent lootEntry = arr$1[i$1];
            cgh.addItem(lootEntry);
         }
      }

   }

}
