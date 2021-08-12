package ic2.core;

import cpw.mods.fml.common.ICraftingHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import ic2.core.Ic2Items;
import ic2.core.Platform;
import ic2.core.block.machine.tileentity.TileEntityCompressor;
import ic2.core.block.machine.tileentity.TileEntityMatter;
import java.util.HashMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatBase;
import net.minecraftforge.common.AchievementPage;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;

public class IC2Achievements implements ICraftingHandler {

   public HashMap achievementList = new HashMap();
   private int achievementBaseX = -4;
   private int achievementBaseY = -5;


   public IC2Achievements() {
      this.registerAchievement(736750, "acquireResin", 2, 0, Ic2Items.resin, AchievementList.mineWood, false);
      if(Ic2Items.copperOre != null || Ic2Items.tinOre != null || Ic2Items.uraniumOre != null) {
         this.registerAchievement(736751, "mineOre", 4, 0, Ic2Items.copperOre == null?(Ic2Items.tinOre == null?Ic2Items.uraniumOre:Ic2Items.tinOre):Ic2Items.copperOre, AchievementList.buildBetterPickaxe, false);
      }

      this.registerAchievement(736752, "acquireRefinedIron", 0, 0, Ic2Items.refinedIronIngot, AchievementList.acquireIron, false);
      this.registerAchievement(736753, "buildCable", 0, 2, Ic2Items.insulatedCopperCableItem, "acquireRefinedIron", false);
      this.registerAchievement(736754, "buildGenerator", 6, 2, Ic2Items.generator, "buildCable", false);
      this.registerAchievement(736755, "buildMacerator", 6, 0, Ic2Items.macerator, "buildGenerator", false);
      this.registerAchievement(736757, "buildCoalDiamond", 8, 0, Ic2Items.industrialDiamond, "buildMacerator", false);
      this.registerAchievement(736758, "buildElecFurnace", 8, 2, Ic2Items.electroFurnace, "buildGenerator", false);
      this.registerAchievement(736759, "buildIndFurnace", 10, 2, Ic2Items.inductionFurnace, "buildElecFurnace", false);
      this.registerAchievement(736761, "buildCompressor", 4, 4, Ic2Items.compressor, "buildGenerator", false);
      this.registerAchievement(736762, "compressUranium", 2, 4, Ic2Items.uraniumIngot, "buildCompressor", false);
      this.registerAchievement(736763, "dieFromOwnNuke", 0, 4, Ic2Items.nuke, "compressUranium", true);
      this.registerAchievement(736764, "buildExtractor", 8, 4, Ic2Items.extractor, "buildGenerator", false);
      this.registerAchievement(736760, "buildBatBox", 6, 6, Ic2Items.batBox, "buildGenerator", false);
      this.registerAchievement(736765, "buildDrill", 8, 6, Ic2Items.miningDrill, "buildBatBox", false);
      this.registerAchievement(736766, "buildDDrill", 10, 6, Ic2Items.diamondDrill, "buildDrill", false);
      this.registerAchievement(736767, "buildChainsaw", 4, 6, Ic2Items.chainsaw, "buildBatBox", false);
      this.registerAchievement(736768, "killCreeperChainsaw", 2, 6, Ic2Items.chainsaw, "buildChainsaw", true);
      this.registerAchievement(736769, "buildMFE", 6, 8, Ic2Items.mfeUnit, "buildBatBox", false);
      this.registerAchievement(736770, "buildMassFab", 8, 8, Ic2Items.massFabricator, "buildBatBox", false);
      this.registerAchievement(736771, "acquireMatter", 10, 8, Ic2Items.matter, "buildMassFab", false);
      this.registerAchievement(736772, "buildQArmor", 12, 8, Ic2Items.quantumBodyarmor, "acquireMatter", false);
      this.registerAchievement(736773, "starveWithQHelmet", 14, 8, Ic2Items.quantumHelmet, "buildQArmor", true);
      this.registerAchievement(736774, "buildMiningLaser", 4, 8, Ic2Items.miningLaser, "buildMFE", false);
      this.registerAchievement(736775, "killDragonMiningLaser", 2, 8, Ic2Items.miningLaser, "buildMiningLaser", true);
      this.registerAchievement(736776, "buildMFS", 6, 10, Ic2Items.mfsUnit, "buildMFE", false);
      this.registerAchievement(736777, "buildTeleporter", 4, 10, Ic2Items.teleporter, "buildMFS", false);
      this.registerAchievement(736778, "teleportFarAway", 2, 10, Ic2Items.teleporter, "buildTeleporter", true);
      this.registerAchievement(736779, "buildTerraformer", 8, 10, Ic2Items.terraformer, "buildMFS", false);
      this.registerAchievement(736780, "terraformEndCultivation", 10, 10, Ic2Items.cultivationTerraformerBlueprint, "buildTerraformer", true);
      AchievementPage.registerAchievementPage(new AchievementPage("IndustrialCraft 2", (Achievement[])this.achievementList.values().toArray(new Achievement[this.achievementList.size()])));
      MinecraftForge.EVENT_BUS.register(this);
      GameRegistry.registerCraftingHandler(this);
   }

   public Achievement registerAchievement(int id, String textId, int x, int y, ItemStack icon, Achievement requirement, boolean special) {
      Achievement achievement = new Achievement(id, textId, this.achievementBaseX + x, this.achievementBaseY + y, icon, requirement);
      if(special) {
         achievement.setSpecial();
      }

      achievement.registerAchievement();
      this.achievementList.put(textId, achievement);
      return achievement;
   }

   public Achievement registerAchievement(int id, String textId, int x, int y, ItemStack icon, String requirement, boolean special) {
      Achievement achievement = new Achievement(id, textId, this.achievementBaseX + x, this.achievementBaseY + y, icon, this.getAchievement(requirement));
      if(special) {
         achievement.setSpecial();
      }

      achievement.registerAchievement();
      this.achievementList.put(textId, achievement);
      return achievement;
   }

   public void issueAchievement(EntityPlayer entityplayer, String textId) {
      if(this.achievementList.containsKey(textId)) {
         entityplayer.triggerAchievement((StatBase)this.achievementList.get(textId));
      }

   }

   public Achievement getAchievement(String textId) {
      return this.achievementList.containsKey(textId)?(Achievement)this.achievementList.get(textId):null;
   }

   public void onCrafting(EntityPlayer entityplayer, ItemStack itemstack, IInventory iinventory) {
      if(itemstack.isItemEqual(Ic2Items.generator)) {
         this.issueAchievement(entityplayer, "buildGenerator");
      } else if(itemstack.itemID == Ic2Items.insulatedCopperCableItem.itemID) {
         this.issueAchievement(entityplayer, "buildCable");
      } else if(itemstack.isItemEqual(Ic2Items.macerator)) {
         this.issueAchievement(entityplayer, "buildMacerator");
      } else if(itemstack.isItemEqual(Ic2Items.electroFurnace)) {
         this.issueAchievement(entityplayer, "buildElecFurnace");
      } else if(itemstack.isItemEqual(Ic2Items.compressor)) {
         this.issueAchievement(entityplayer, "buildCompressor");
      } else if(itemstack.isItemEqual(Ic2Items.batBox)) {
         this.issueAchievement(entityplayer, "buildBatBox");
      } else if(itemstack.isItemEqual(Ic2Items.mfeUnit)) {
         this.issueAchievement(entityplayer, "buildMFE");
      } else if(itemstack.isItemEqual(Ic2Items.teleporter)) {
         this.issueAchievement(entityplayer, "buildTeleporter");
      } else if(itemstack.isItemEqual(Ic2Items.massFabricator)) {
         this.issueAchievement(entityplayer, "buildMassFab");
      } else if(itemstack.itemID != Ic2Items.quantumBodyarmor.itemID && itemstack.itemID != Ic2Items.quantumBoots.itemID && itemstack.itemID != Ic2Items.quantumHelmet.itemID && itemstack.itemID != Ic2Items.quantumLeggings.itemID) {
         if(itemstack.isItemEqual(Ic2Items.extractor)) {
            this.issueAchievement(entityplayer, "buildExtractor");
         } else if(itemstack.itemID == Ic2Items.miningDrill.itemID) {
            this.issueAchievement(entityplayer, "buildDrill");
         } else if(itemstack.itemID == Ic2Items.diamondDrill.itemID) {
            this.issueAchievement(entityplayer, "buildDDrill");
         } else if(itemstack.itemID == Ic2Items.chainsaw.itemID) {
            this.issueAchievement(entityplayer, "buildChainsaw");
         } else if(itemstack.itemID == Ic2Items.miningLaser.itemID) {
            this.issueAchievement(entityplayer, "buildMiningLaser");
         } else if(itemstack.isItemEqual(Ic2Items.mfsUnit)) {
            this.issueAchievement(entityplayer, "buildMFS");
         } else if(itemstack.isItemEqual(Ic2Items.terraformer)) {
            this.issueAchievement(entityplayer, "buildTerraformer");
         } else if(itemstack.isItemEqual(Ic2Items.coalChunk)) {
            this.issueAchievement(entityplayer, "buildCoalDiamond");
         } else if(itemstack.isItemEqual(Ic2Items.inductionFurnace)) {
            this.issueAchievement(entityplayer, "buildIndFurnace");
         }
      } else {
         this.issueAchievement(entityplayer, "buildQArmor");
      }

   }

   public void onSmelting(EntityPlayer entityplayer, ItemStack itemstack) {
      if(itemstack.isItemEqual(Ic2Items.refinedIronIngot)) {
         this.issueAchievement(entityplayer, "acquireRefinedIron");
      }

   }

   public void onMachineOp(EntityPlayer entityplayer, ItemStack itemstack, IInventory inventory) {
      if(inventory instanceof TileEntityCompressor && itemstack.itemID == Ic2Items.uraniumIngot.itemID) {
         this.issueAchievement(entityplayer, "compressUranium");
      } else if(inventory instanceof TileEntityMatter && itemstack.itemID == Ic2Items.matter.itemID) {
         this.issueAchievement(entityplayer, "acquireMatter");
      }

   }

   @ForgeSubscribe
   public void onItemPickup(EntityItemPickupEvent event) {
      if(Ic2Items.copperOre != null && event.item.getEntityItem().isItemEqual(Ic2Items.copperOre) || Ic2Items.tinOre != null && event.item.getEntityItem().isItemEqual(Ic2Items.tinOre) || Ic2Items.uraniumDrop != null && event.item.getEntityItem().isItemEqual(Ic2Items.uraniumDrop)) {
         this.issueAchievement(event.entityPlayer, "mineOre");
      }

   }

   public static void addLocalization(Platform platform) {
      platform.addLocalization("achievement.acquireRefinedIron", "Better than Iron");
      platform.addLocalization("achievement.acquireRefinedIron.desc", "Smelt a refined iron ingot");
      platform.addLocalization("achievement.buildGenerator", "Suitable Power");
      platform.addLocalization("achievement.buildGenerator.desc", "Build a generator");
      platform.addLocalization("achievement.acquireResin", "Sticky Situation");
      platform.addLocalization("achievement.acquireResin.desc", "Get resin from a rubber tree with a treetap");
      platform.addLocalization("achievement.buildCable", "Energy Flow");
      platform.addLocalization("achievement.buildCable.desc", "Make some cables");
      platform.addLocalization("achievement.mineOre", "New Ore Miner");
      platform.addLocalization("achievement.mineOre.desc", "Mine copper, tin or uranium");
      platform.addLocalization("achievement.buildMacerator", "Double Trouble");
      platform.addLocalization("achievement.buildMacerator.desc", "Build a macerator");
      platform.addLocalization("achievement.buildCoalDiamond", "Shiny");
      platform.addLocalization("achievement.buildCoalDiamond.desc", "Make a coal chunk to compress into a diamond");
      platform.addLocalization("achievement.buildElecFurnace", "Even Hotter Topic");
      platform.addLocalization("achievement.buildElecFurnace.desc", "Build an electric furnace");
      platform.addLocalization("achievement.buildIndFurnace", "Hyperspeed");
      platform.addLocalization("achievement.buildIndFurnace.desc", "Build an induction furnace");
      platform.addLocalization("achievement.buildBatBox", "Stash It");
      platform.addLocalization("achievement.buildBatBox.desc", "Build a BatBox");
      platform.addLocalization("achievement.buildCompressor", "Epic Squeeze");
      platform.addLocalization("achievement.buildCompressor.desc", "Build a compressor");
      platform.addLocalization("achievement.compressUranium", "Radioactivity");
      platform.addLocalization("achievement.compressUranium.desc", "Compress uranium into an uranium fuel ingot");
      platform.addLocalization("achievement.dieFromOwnNuke", "Crazy Ivan");
      platform.addLocalization("achievement.dieFromOwnNuke.desc", "Die from a nuke you have placed");
      platform.addLocalization("achievement.buildExtractor", "Super Treetap");
      platform.addLocalization("achievement.buildExtractor.desc", "Build an extractor");
      platform.addLocalization("achievement.buildChainsaw", "Lumberjack Assassin");
      platform.addLocalization("achievement.buildChainsaw.desc", "Build a chainsaw");
      platform.addLocalization("achievement.killCreeperChainsaw", "Creeper Chainsaw Massacre");
      platform.addLocalization("achievement.killCreeperChainsaw.desc", "Kill a creeper with a chainsaw");
      platform.addLocalization("achievement.buildDrill", "Meet the Dentist");
      platform.addLocalization("achievement.buildDrill.desc", "Build a mining drill");
      platform.addLocalization("achievement.buildDDrill", "Valuable Upgrade");
      platform.addLocalization("achievement.buildDDrill.desc", "Upgrade your mining drill to a diamond-tipped mining drill");
      platform.addLocalization("achievement.buildMFE", "Storage Upgrade");
      platform.addLocalization("achievement.buildMFE.desc", "Build a MFE Unit");
      platform.addLocalization("achievement.buildMiningLaser", "Laser Time");
      platform.addLocalization("achievement.buildMiningLaser.desc", "Build a mining laser");
      platform.addLocalization("achievement.killDragonMiningLaser", "Like A Boss");
      platform.addLocalization("achievement.killDragonMiningLaser.desc", "Kill the enderdragon with a mining laser");
      platform.addLocalization("achievement.buildMassFab", "Energy To Matter");
      platform.addLocalization("achievement.buildMassFab.desc", "Build a Mass Fabricator");
      platform.addLocalization("achievement.acquireMatter", "Pink Blob");
      platform.addLocalization("achievement.acquireMatter.desc", "Produce some UU-Matter");
      platform.addLocalization("achievement.buildQArmor", "Hi-Tech Wonder");
      platform.addLocalization("achievement.buildQArmor.desc", "Build a piece of the quantum suit");
      platform.addLocalization("achievement.starveWithQHelmet", "Forgot to Recharge");
      platform.addLocalization("achievement.starveWithQHelmet.desc", "Starve to death or drown with a quantum helmet");
      platform.addLocalization("achievement.buildMFS", "Just Too Much");
      platform.addLocalization("achievement.buildMFS.desc", "Build a MFS Unit");
      platform.addLocalization("achievement.buildTeleporter", "Intradimensional Warp");
      platform.addLocalization("achievement.buildTeleporter.desc", "Build a Teleporter");
      platform.addLocalization("achievement.teleportFarAway", "Far Far Away");
      platform.addLocalization("achievement.teleportFarAway.desc", "Teleport for at least 1 km");
      platform.addLocalization("achievement.buildTerraformer", "Change The World");
      platform.addLocalization("achievement.buildTerraformer.desc", "Build a terraformer");
      platform.addLocalization("achievement.terraformEndCultivation", "Endgame Paradise");
      platform.addLocalization("achievement.terraformEndCultivation.desc", "Terraform the End to look like the overworld");
   }
}
