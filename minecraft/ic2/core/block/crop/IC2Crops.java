package ic2.core.block.crop;

import ic2.api.CropCard;
import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.block.TileEntityCrop;
import ic2.core.block.crop.CropAurelia;
import ic2.core.block.crop.CropCocoa;
import ic2.core.block.crop.CropCoffee;
import ic2.core.block.crop.CropColorFlower;
import ic2.core.block.crop.CropFerru;
import ic2.core.block.crop.CropHops;
import ic2.core.block.crop.CropMelon;
import ic2.core.block.crop.CropNetherWart;
import ic2.core.block.crop.CropPotato;
import ic2.core.block.crop.CropPumpkin;
import ic2.core.block.crop.CropRedWheat;
import ic2.core.block.crop.CropReed;
import ic2.core.block.crop.CropSeedFood;
import ic2.core.block.crop.CropStickReed;
import ic2.core.block.crop.CropTerraWart;
import ic2.core.block.crop.CropVenomilia;
import ic2.core.block.crop.CropWeed;
import ic2.core.block.crop.CropWheat;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class IC2Crops {

   public static CropCard weed = new CropWeed();
   public static CropCard cropWheat = new CropWheat();
   public static CropCard cropPumpkin = new CropPumpkin();
   public static CropCard cropMelon = new CropMelon();
   public static CropCard cropYellowFlower = new CropColorFlower("Dandelion", new String[]{"Yellow", "Flower"}, 15, 11);
   public static CropCard cropRedFlower = new CropColorFlower("Rose", new String[]{"Red", "Flower", "Rose"}, 21, 1);
   public static CropCard cropBlackFlower = new CropColorFlower("Blackthorn", new String[]{"Black", "Flower", "Rose"}, 22, 0);
   public static CropCard cropPurpleFlower = new CropColorFlower("Tulip", new String[]{"Purple", "Flower", "Tulip"}, 23, 5);
   public static CropCard cropBlueFlower = new CropColorFlower("Cyazint", new String[]{"Blue", "Flower"}, 24, 6);
   public static CropCard cropVenomilia = new CropVenomilia();
   public static CropCard cropReed = new CropReed();
   public static CropCard cropStickReed = new CropStickReed();
   public static CropCard cropCocoa = new CropCocoa();
   public static CropCard cropFerru = new CropFerru();
   public static CropCard cropAurelia = new CropAurelia();
   public static CropCard cropRedwheat = new CropRedWheat();
   public static CropCard cropNetherWart = new CropNetherWart();
   public static CropCard cropTerraWart = new CropTerraWart();
   public static CropCard cropCoffee = new CropCoffee();
   public static CropCard cropHops = new CropHops();
   public static CropCard cropCarrots = new CropSeedFood("Carrots", 50, "Orange", new ItemStack(Item.carrot));
   public static CropCard cropPotato = new CropPotato();


   public static void init() {
      registerCrops();
      registerBaseSeeds();
   }

   public static void registerCrops() {
      CropCard.nameReference = new TileEntityCrop();
      if(!CropCard.registerCrop(weed, 0) || !CropCard.registerCrop(cropWheat, 1) || !CropCard.registerCrop(cropPumpkin, 2) || !CropCard.registerCrop(cropMelon, 3) || !CropCard.registerCrop(cropYellowFlower, 4) || !CropCard.registerCrop(cropRedFlower, 5) || !CropCard.registerCrop(cropBlackFlower, 6) || !CropCard.registerCrop(cropPurpleFlower, 7) || !CropCard.registerCrop(cropBlueFlower, 8) || !CropCard.registerCrop(cropVenomilia, 9) || !CropCard.registerCrop(cropReed, 10) || !CropCard.registerCrop(cropStickReed, 11) || !CropCard.registerCrop(cropCocoa, 12) || !CropCard.registerCrop(cropFerru, 13) || !CropCard.registerCrop(cropAurelia, 14) || !CropCard.registerCrop(cropRedwheat, 15) || !CropCard.registerCrop(cropNetherWart, 16) || !CropCard.registerCrop(cropTerraWart, 17) || !CropCard.registerCrop(cropCoffee, 18) || !CropCard.registerCrop(cropHops, 19) || !CropCard.registerCrop(cropCarrots, 20) || !CropCard.registerCrop(cropPotato, 21)) {
         IC2.platform.displayError("One or more crops have failed to initialize.\nThis could happen due to a crop addon using a crop ID already taken\nby a crop from IndustrialCraft 2.");
      }

   }

   public static void registerBaseSeeds() {
      CropCard.registerBaseSeed(new ItemStack(Item.seeds.itemID, 1, -1), cropWheat.getId(), 1, 1, 1, 1);
      CropCard.registerBaseSeed(new ItemStack(Item.pumpkinSeeds.itemID, 1, -1), cropPumpkin.getId(), 1, 1, 1, 1);
      CropCard.registerBaseSeed(new ItemStack(Item.melonSeeds.itemID, 1, -1), cropMelon.getId(), 1, 1, 1, 1);
      CropCard.registerBaseSeed(new ItemStack(Item.netherStalkSeeds.itemID, 1, -1), cropNetherWart.getId(), 1, 1, 1, 1);
      CropCard.registerBaseSeed(new ItemStack(Ic2Items.terraWart.itemID, 1, -1), cropTerraWart.getId(), 1, 1, 1, 1);
      CropCard.registerBaseSeed(new ItemStack(Ic2Items.coffeeBeans.itemID, 1, -1), cropCoffee.getId(), 1, 1, 1, 1);
      CropCard.registerBaseSeed(new ItemStack(Item.reed.itemID, 1, -1), cropReed.getId(), 1, 3, 0, 2);
      CropCard.registerBaseSeed(new ItemStack(Item.dyePowder.itemID, 1, 3), cropCocoa.getId(), 1, 0, 0, 0);
      CropCard.registerBaseSeed(new ItemStack(Block.plantRed.blockID, 4, -1), cropRedFlower.getId(), 4, 1, 1, 1);
      CropCard.registerBaseSeed(new ItemStack(Block.plantYellow.blockID, 4, -1), cropYellowFlower.getId(), 4, 1, 1, 1);
      CropCard.registerBaseSeed(new ItemStack(Item.carrot, 1, -1), cropCarrots.getId(), 1, 1, 1, 1);
      CropCard.registerBaseSeed(new ItemStack(Item.potato, 1, -1), cropPotato.getId(), 1, 1, 1, 1);
   }

}
