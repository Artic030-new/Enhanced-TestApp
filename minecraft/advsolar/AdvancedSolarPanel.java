package advsolar;

import advsolar.ASPServerProxy;
import advsolar.BlockAdvSolarPanel;
import advsolar.ItemAdvSolarHelmet;
import advsolar.ItemAdvSolarPanel;
import advsolar.ItemAdvanced;
import advsolar.ItemHSolarHelmet;
import advsolar.TileEntityAdvancedSolarPanel;
import advsolar.TileEntityHybridSolarPanel;
import advsolar.TileEntityQGenerator;
import advsolar.TileEntityUltimateSolarPanel;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import ic2.api.Ic2Recipes;
import ic2.api.Items;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.oredict.OreDictionary;

@Mod(
   modid = "AdvancedSolarPanel",
   name = "Advanced Solar Panels",
   dependencies = "required-after:IC2; after:GraviSuite",
   version = "3.3.4"
)
@NetworkMod(
   clientSideRequired = true,
   serverSideRequired = false
)
public class AdvancedSolarPanel {

   @SidedProxy(
      clientSide = "advsolar.ASPClientProxy",
      serverSide = "advsolar.ASPServerProxy"
   )
   public static ASPServerProxy proxy;
   public static Block blockAdvSolarPanel;
   public static Item itemAdvanced;
   public static Item advancedSolarHelmet;
   public static Item hybridSolarHelmet;
   public static Item ultimateSolarHelmet;
   public static ItemStack itemSunnarium;
   public static ItemStack itemSunnariumPart;
   public static ItemStack itemSunnariumAlloy;
   public static ItemStack ingotIridium;
   public static ItemStack itemIrradiantUranium;
   public static ItemStack itemEnrichedSunnarium;
   public static ItemStack itemEnrichedSunnariumAlloy;
   public static ItemStack itemIrradiantGlassPane;
   public static ItemStack itemIridiumIronPlate;
   public static ItemStack itemReinforcedIridiumIronPlate;
   public static ItemStack itemIrradiantReinforcedPlate;
   public static Configuration config;
   public static int advGenDay;
   public static int advGenNight;
   public static int advStorage;
   public static int advOutput;
   public static int hGenDay;
   public static int hGenNight;
   public static int hStorage;
   public static int hOutput;
   public static int uhGenDay;
   public static int uhGenNight;
   public static int uhStorage;
   public static int uhOutput;
   public static int idAdv;
   public static int itemsID;
   public static int advSolarHelmetID;
   public static int hybridSolarHelmetID;
   public static int ultimateSolarHelmetID;
   public static int qgbaseProduction;
   public static int qgbaseMaxPacketSize;
   public static CreativeTabs ic2Tab;
   private static boolean disableDoubleSlabRecipe;
   private static boolean enableSimpleAdvancedSolarPanelRecipes;
   private static boolean enableHardRecipes;
   public static final String CATEGORY_RECIPES = "recipes settings";
   public static final String CATEGORY_QGENERATOR = "quantum generator";
   public static final Side side = FMLCommonHandler.instance().getEffectiveSide();
   @Instance("AdvancedSolarPanel")
   public static AdvancedSolarPanel instance = new AdvancedSolarPanel();

   public static ItemStack setItemsSize(ItemStack itemStack, int newSize) {
      ItemStack newStack = itemStack.copy();
      newStack.stackSize = newSize;
      return newStack;
   }

   public static void getIC2Tab() {
      for(int i = 0; i < CreativeTabs.creativeTabArray.length; ++i) {
         if(CreativeTabs.creativeTabArray[i].getTabLabel() == "IC2") {
            ic2Tab = CreativeTabs.creativeTabArray[i];
         }
      }

   }

   public static boolean isSimulating() {
      return !FMLCommonHandler.instance().getEffectiveSide().isClient();
   }

   @PreInit
   public void preInit(FMLPreInitializationEvent event) {
      Configuration config = new Configuration(event.getSuggestedConfigurationFile());

      try {
         config.load();
         idAdv = Integer.parseInt(config.get("BLOCK", "blockID", 194).value);
         itemsID = Integer.parseInt(config.get("ITEMS", "itemsID", 30575).value);
         advSolarHelmetID = Integer.parseInt(config.get("ITEMS", "advSolarHelmetID", 30576).value);
         hybridSolarHelmetID = Integer.parseInt(config.get("ITEMS", "hybridSolarHelmetID", 30577).value);
         ultimateSolarHelmetID = Integer.parseInt(config.get("ITEMS", "ultimateSolarHelmetID", 30578).value);
         advGenDay = Integer.parseInt(config.get("general", "AdvGenDay", 8).value);
         advGenNight = Integer.parseInt(config.get("general", "AdvGenNight", 1).value);
         advStorage = Integer.parseInt(config.get("general", "AdvStorage", 32000).value);
         advOutput = Integer.parseInt(config.get("general", "AdvOutput", 32).value);
         hGenDay = Integer.parseInt(config.get("general", "HybGenDay", 64).value);
         hGenNight = Integer.parseInt(config.get("general", "HybGenNight", 8).value);
         hStorage = Integer.parseInt(config.get("general", "HybStorage", 100000).value);
         hOutput = Integer.parseInt(config.get("general", "HybOutput", 128).value);
         uhGenDay = Integer.parseInt(config.get("general", "UltHybGenDay", 512).value);
         uhGenNight = Integer.parseInt(config.get("general", "UltHybGenNight", 64).value);
         uhStorage = Integer.parseInt(config.get("general", "UltStorage", 1000000).value);
         uhOutput = Integer.parseInt(config.get("general", "UltOutput", 512).value);
         qgbaseProduction = Integer.parseInt(config.get("quantum generator", "Quantum generator default production", 512).value);
         qgbaseMaxPacketSize = Integer.parseInt(config.get("quantum generator", "Quantum generator default maximum packet size", 512).value);
         disableDoubleSlabRecipe = Boolean.parseBoolean(config.get("recipes settings", "Disable DoubleSlab recipe", false).value);
         enableSimpleAdvancedSolarPanelRecipes = Boolean.parseBoolean(config.get("recipes settings", "Enable simple Advanced Solar Panel recipe", false).value);
         enableHardRecipes = Boolean.parseBoolean(config.get("recipes settings", "Enable hard recipes", false).value);
         config.save();
      } catch (Exception var7) {
         FMLLog.log(Level.SEVERE, var7, "[AdvancedSolarPanels] error while loading config file", new Object[0]);
         throw new RuntimeException(var7);
      }
   }

   @Init
   public void load(FMLInitializationEvent event) {
      if(side == Side.CLIENT) {
         getIC2Tab();
      }

      blockAdvSolarPanel = new BlockAdvSolarPanel(idAdv);
      GameRegistry.registerBlock(blockAdvSolarPanel, ItemAdvSolarPanel.class);
      LanguageRegistry.instance().addStringLocalization("blockAdvancedSolarPanel.name", "Улучшенная солнечная панель");
      LanguageRegistry.instance().addStringLocalization("blockHybridSolarPanel.name", "Гибридная солнечная панель");
      LanguageRegistry.instance().addStringLocalization("blockUltimateSolarPanel.name", "Совершенная солнечная панель");
      LanguageRegistry.instance().addStringLocalization("blockQuantumGenerator.name", "Квантовый генератор");
      GameRegistry.registerTileEntity(TileEntityAdvancedSolarPanel.class, "Advanced Solar Panel");
      GameRegistry.registerTileEntity(TileEntityHybridSolarPanel.class, "Hybrid Solar Panel");
      GameRegistry.registerTileEntity(TileEntityUltimateSolarPanel.class, "Ultimate Hybrid Solar Panel");
      GameRegistry.registerTileEntity(TileEntityQGenerator.class, "Quantum Generator");
      advancedSolarHelmet = (new ItemAdvSolarHelmet(advSolarHelmetID, EnumArmorMaterial.DIAMOND, proxy.addArmor("AdvancedSolarPanel"), 0, 1)).setItemName("AdvancedSolarHelmet");
      hybridSolarHelmet = (new ItemHSolarHelmet(hybridSolarHelmetID, EnumArmorMaterial.DIAMOND, proxy.addArmor("AdvancedSolarPanel"), 0, 1)).setItemName("HybridSolarHelmet");
      ultimateSolarHelmet = (new ItemHSolarHelmet(ultimateSolarHelmetID, EnumArmorMaterial.DIAMOND, proxy.addArmor("AdvancedSolarPanel"), 0, 2)).setItemName("UltimateSolarHelmet");
      itemAdvanced = new ItemAdvanced(itemsID);
      itemSunnarium = new ItemStack(itemAdvanced.setItemName("itemSunnarium"), 1, 0);
      itemSunnariumAlloy = new ItemStack(itemAdvanced.setItemName("itemSunnariumAlloy"), 1, 1);
      itemIrradiantUranium = new ItemStack(itemAdvanced.setItemName("itemIrradiantUranium"), 1, 2);
      itemEnrichedSunnarium = new ItemStack(itemAdvanced.setItemName("itemEnrichedSunnarium"), 1, 3);
      itemEnrichedSunnariumAlloy = new ItemStack(itemAdvanced.setItemName("itemEnrichedSunnariumAlloy"), 1, 4);
      itemIrradiantGlassPane = new ItemStack(itemAdvanced.setItemName("itemIrradiantGlassPlane"), 1, 5);
      itemIridiumIronPlate = new ItemStack(itemAdvanced.setItemName("itemIridiumIronPlate"), 1, 6);
      itemReinforcedIridiumIronPlate = new ItemStack(itemAdvanced.setItemName("itemReinforcedIridiumIronPlate"), 1, 7);
      itemIrradiantReinforcedPlate = new ItemStack(itemAdvanced.setItemName(" itemIrradiantReinforcedPlate"), 1, 8);
      itemSunnariumPart = new ItemStack(itemAdvanced.setItemName("itemSunnariumPart"), 1, 9);
      ingotIridium = new ItemStack(itemAdvanced.setItemName("ingotIridium"), 1, 10);
      LanguageRegistry.addName(advancedSolarHelmet, "Улучшенный СП-шлем");
      LanguageRegistry.addName(hybridSolarHelmet, "Гибридный СП-шлем");
      LanguageRegistry.addName(ultimateSolarHelmet, "Совершенный СП-шлем");
      LanguageRegistry.instance().addStringLocalization("itemSunnarium.name", "Солнечная материя");
      LanguageRegistry.instance().addStringLocalization("itemSunnariumAlloy.name", "Сплав солнечной материи");
      LanguageRegistry.instance().addStringLocalization("itemIrradiantUranium.name", "Светящийся урановый слиток");
      LanguageRegistry.instance().addStringLocalization("itemEnrichedSunnarium.name", "Обогащённая солнечная материя");
      LanguageRegistry.instance().addStringLocalization("itemEnrichedSunnariumAlloy.name", "Обогащённый сплав солнечной материи");
      LanguageRegistry.instance().addStringLocalization("itemIrradiantGlassPane.name", "Светящаяся стекляная панель");
      LanguageRegistry.instance().addStringLocalization("itemIridiumIronPlate.name", "Иридиевый железный лист");
      LanguageRegistry.instance().addStringLocalization("itemReinforcedIridiumIronPlate.name", "Укреплённый иридиевый железный лист");
      LanguageRegistry.instance().addStringLocalization("itemIrradiantReinforcedPlate.name", "Светящийся укреплённый лист");
      LanguageRegistry.instance().addStringLocalization("itemSunnariumPart.name", "Часть солнечной материи");
      LanguageRegistry.instance().addStringLocalization("ingotIridium.name", "Слиток иридия");
      OreDictionary.registerOre("ingotIridium", ingotIridium);
      Ic2Recipes.addCompressorRecipe(Items.getItem("iridiumOre"), ingotIridium);
      proxy.registerRenderers();
      NetworkRegistry.instance().registerGuiHandler(this, proxy);
   }

   @PostInit
   public void postInit(FMLPostInitializationEvent event) {     
      Ic2Recipes.addCraftingRecipe(new ItemStack(advancedSolarHelmet, 1), new Object[]{" A ", "RBR", "FDF", Character.valueOf('A'), new ItemStack(blockAdvSolarPanel, 1, 0), Character.valueOf('B'), Items.getItem("nanoHelmet"), Character.valueOf('R'), Items.getItem("advancedCircuit"), Character.valueOf('D'), Items.getItem("lvTransformer"), Character.valueOf('F'), Items.getItem("doubleInsulatedGoldCableItem")});
      Ic2Recipes.addCraftingRecipe(new ItemStack(hybridSolarHelmet, 1), new Object[]{" A ", "RBR", "FDF", Character.valueOf('A'), new ItemStack(blockAdvSolarPanel, 1, 1), Character.valueOf('B'), Items.getItem("quantumHelmet"), Character.valueOf('R'), Items.getItem("advancedCircuit"), Character.valueOf('D'), Items.getItem("hvTransformer"), Character.valueOf('F'), Items.getItem("glassFiberCableItem")});
      Ic2Recipes.addCraftingRecipe(new ItemStack(ultimateSolarHelmet, 1), new Object[]{" A ", "RBR", "FDF", Character.valueOf('A'), new ItemStack(blockAdvSolarPanel, 1, 2), Character.valueOf('B'), Items.getItem("quantumHelmet"), Character.valueOf('R'), Items.getItem("advancedCircuit"), Character.valueOf('D'), Items.getItem("hvTransformer"), Character.valueOf('F'), Items.getItem("glassFiberCableItem")});
      Ic2Recipes.addCraftingRecipe(new ItemStack(ultimateSolarHelmet, 1), new Object[]{"A", "B", Character.valueOf('A'), new ItemStack(blockAdvSolarPanel, 1, 2), Character.valueOf('B'), new ItemStack(hybridSolarHelmet, 1)});
      ItemStack itemStack;
      Iterator iterator;
      if(!enableHardRecipes) {
    	  iterator = OreDictionary.getOres("ingotIridium").iterator();

         while(iterator.hasNext()) {
            itemStack = (ItemStack)iterator.next();
            GameRegistry.addRecipe(itemIridiumIronPlate, new Object[]{"AAA", "ABA", "AAA", Character.valueOf('A'), Items.getItem("refinedIronIngot"), Character.valueOf('B'), itemStack});
         }

         GameRegistry.addRecipe(itemIridiumIronPlate, new Object[]{"AAA", "ABA", "AAA", Character.valueOf('A'), Items.getItem("refinedIronIngot"), Character.valueOf('B'), ingotIridium});
         GameRegistry.addRecipe(itemReinforcedIridiumIronPlate, new Object[]{"ABA", "BCB", "ABA", Character.valueOf('A'), Items.getItem("advancedAlloy"), Character.valueOf('B'), Items.getItem("carbonPlate"), Character.valueOf('C'), itemIridiumIronPlate});
         GameRegistry.addRecipe(itemSunnariumPart, new Object[]{" A ", " B ", " A ", Character.valueOf('A'), Items.getItem("matter"), Character.valueOf('B'), Item.lightStoneDust});
         GameRegistry.addRecipe(itemIrradiantReinforcedPlate, new Object[]{"ABA", "DCD", "AFA", Character.valueOf('A'), Item.redstone, Character.valueOf('B'), itemSunnariumPart, Character.valueOf('D'), new ItemStack(Item.dyePowder, 1, 4), Character.valueOf('C'), itemReinforcedIridiumIronPlate, Character.valueOf('F'), Item.diamond});
         if(enableSimpleAdvancedSolarPanelRecipes) {
            GameRegistry.addRecipe(new ItemStack(blockAdvSolarPanel, 1, 0), new Object[]{"RRR", "DSD", "ABA", Character.valueOf('R'), Items.getItem("reinforcedGlass"), Character.valueOf('D'), Items.getItem("advancedAlloy"), Character.valueOf('S'), Items.getItem("solarPanel"), Character.valueOf('A'), Items.getItem("advancedCircuit"), Character.valueOf('B'), Items.getItem("advancedMachine")});
         } else {
            GameRegistry.addRecipe(new ItemStack(blockAdvSolarPanel, 1, 0), new Object[]{"RRR", "DSD", "ABA", Character.valueOf('R'), Items.getItem("reinforcedGlass"), Character.valueOf('D'), Items.getItem("advancedAlloy"), Character.valueOf('S'), Items.getItem("solarPanel"), Character.valueOf('A'), Items.getItem("advancedCircuit"), Character.valueOf('B'), itemIrradiantReinforcedPlate});
         }
         
         GameRegistry.addRecipe(new ItemStack(blockAdvSolarPanel, 1, 2), new Object[]{" A ", "XMX", "RXR", Character.valueOf('A'), Block.blockLapis, Character.valueOf('X'), Items.getItem("coalChunk"), Character.valueOf('M'), new ItemStack(blockAdvSolarPanel, 1, 0), Character.valueOf('R'), itemSunnariumAlloy});
         GameRegistry.addRecipe(new ItemStack(blockAdvSolarPanel, 1, 1), new Object[]{"XMX", "DSD", "ABA", Character.valueOf('X'), Items.getItem("carbonPlate"), Character.valueOf('M'), Block.blockLapis, Character.valueOf('D'), Items.getItem("iridiumPlate"), Character.valueOf('S'), new ItemStack(blockAdvSolarPanel, 1, 0), Character.valueOf('A'), Items.getItem("advancedCircuit"), Character.valueOf('B'), itemSunnarium});
         GameRegistry.addRecipe(new ItemStack(blockAdvSolarPanel, 1, 2), new Object[]{"MMM", "MXM", "MMM", Character.valueOf('M'), new ItemStack(blockAdvSolarPanel, 1, 1), Character.valueOf('X'), Items.getItem("advancedCircuit")});     
      } else {
    	 iterator = OreDictionary.getOres("ingotIridium").iterator();

         while(iterator.hasNext()) {
            itemStack = (ItemStack)iterator.next();
            GameRegistry.addRecipe(itemIridiumIronPlate, new Object[]{"AAA", "ABA", "AAA", Character.valueOf('A'), Items.getItem("refinedIronIngot"), Character.valueOf('B'), itemStack});
         }

         GameRegistry.addRecipe(itemReinforcedIridiumIronPlate, new Object[]{"ABA", "BCB", "ABA", Character.valueOf('A'), Items.getItem("advancedAlloy"), Character.valueOf('B'), Items.getItem("carbonPlate"), Character.valueOf('C'), itemIridiumIronPlate});
         GameRegistry.addRecipe(itemSunnariumPart, new Object[]{" A ", " B ", " A ", Character.valueOf('A'), Items.getItem("matter"), Character.valueOf('B'), Item.lightStoneDust});
         GameRegistry.addRecipe(itemIrradiantReinforcedPlate, new Object[]{"ABA", "DCD", "AFA", Character.valueOf('A'), Item.redstone, Character.valueOf('B'), itemSunnariumPart, Character.valueOf('D'), new ItemStack(Item.dyePowder, 1, 4), Character.valueOf('C'), itemReinforcedIridiumIronPlate, Character.valueOf('F'), Item.diamond});
         GameRegistry.addRecipe(setItemsSize(itemIrradiantGlassPane, 6), new Object[]{"RRR", "ASA", "RRR", Character.valueOf('R'), Items.getItem("reinforcedGlass"), Character.valueOf('A'), itemIrradiantUranium, Character.valueOf('S'), Item.lightStoneDust});
         if(enableSimpleAdvancedSolarPanelRecipes) {
            GameRegistry.addRecipe(new ItemStack(blockAdvSolarPanel, 1, 0), new Object[]{"RRR", "DSD", "ABA", Character.valueOf('R'), itemIrradiantGlassPane, Character.valueOf('D'), Items.getItem("advancedAlloy"), Character.valueOf('S'), Items.getItem("solarPanel"), Character.valueOf('A'), Items.getItem("advancedCircuit"), Character.valueOf('B'), Items.getItem("advancedMachine")});
         } else {
            GameRegistry.addRecipe(new ItemStack(blockAdvSolarPanel, 1, 0), new Object[]{"RRR", "DSD", "ABA", Character.valueOf('R'), itemIrradiantGlassPane, Character.valueOf('D'), Items.getItem("advancedAlloy"), Character.valueOf('S'), Items.getItem("solarPanel"), Character.valueOf('A'), Items.getItem("advancedCircuit"), Character.valueOf('B'), itemIrradiantReinforcedPlate});
         }
   
         GameRegistry.addRecipe(new ItemStack(blockAdvSolarPanel, 1, 2), new Object[]{" A ", "XMX", "RXR", Character.valueOf('A'), Block.blockLapis, Character.valueOf('X'), Items.getItem("coalChunk"), Character.valueOf('M'), new ItemStack(blockAdvSolarPanel, 1, 0), Character.valueOf('R'), itemEnrichedSunnariumAlloy});
         GameRegistry.addRecipe(new ItemStack(blockAdvSolarPanel, 1, 1), new Object[]{"XMX", "DSD", "ABA", Character.valueOf('X'), Items.getItem("carbonPlate"), Character.valueOf('M'), Block.blockLapis, Character.valueOf('D'), Items.getItem("iridiumPlate"), Character.valueOf('S'), new ItemStack(blockAdvSolarPanel, 1, 0), Character.valueOf('A'), Items.getItem("advancedCircuit"), Character.valueOf('B'), itemEnrichedSunnarium});            
         GameRegistry.addRecipe(itemIrradiantUranium, new Object[]{" R ", "RSR", " R ", Character.valueOf('R'), Item.lightStoneDust, Character.valueOf('S'), Items.getItem("uraniumIngot")});
         GameRegistry.addRecipe(itemEnrichedSunnarium, new Object[]{"RRR", "RSR", "RRR", Character.valueOf('R'), itemIrradiantUranium, Character.valueOf('S'), itemSunnarium});
         GameRegistry.addRecipe(itemEnrichedSunnariumAlloy, new Object[]{" R ", "RSR", " R ", Character.valueOf('R'), itemEnrichedSunnarium, Character.valueOf('S'), itemSunnariumAlloy});
      }
      
      GameRegistry.addRecipe(new ItemStack(blockAdvSolarPanel, 1, 2), new Object[]{"MMM", "MXM", "MMM", Character.valueOf('M'), new ItemStack(blockAdvSolarPanel, 1, 1), Character.valueOf('X'), Items.getItem("advancedCircuit")});
      GameRegistry.addRecipe(new ItemStack(blockAdvSolarPanel, 8, 1), new Object[]{"X", Character.valueOf('X'), new ItemStack(blockAdvSolarPanel, 1, 2)});   
      GameRegistry.addRecipe(itemSunnariumAlloy, new Object[]{"MMM", "MXM", "MMM", Character.valueOf('M'), Items.getItem("iridiumPlate"), Character.valueOf('X'), itemSunnarium});
      GameRegistry.addRecipe(itemSunnarium, new Object[]{"MMM", "XXX", "MMM", Character.valueOf('M'), Items.getItem("matter"), Character.valueOf('X'), Item.lightStoneDust});
      if(!disableDoubleSlabRecipe) {
         GameRegistry.addRecipe(new ItemStack(Block.stoneDoubleSlab, 1, 0), new Object[]{"A", "A", Character.valueOf('A'), new ItemStack(Block.stoneSingleSlab, 1, 0)});
         GameRegistry.addRecipe(new ItemStack(Block.stoneDoubleSlab, 1, 1), new Object[]{"A", "A", Character.valueOf('A'), new ItemStack(Block.stoneSingleSlab, 1, 1)});
      }

   }

}
