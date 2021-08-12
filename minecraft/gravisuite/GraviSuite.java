package gravisuite;

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
import cpw.mods.fml.common.network.NetworkMod.SidedPacketHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import gravisuite.ClientPacketHandler;
import gravisuite.ClientTickHandler;
import gravisuite.ItemAdvChainsaw;
import gravisuite.ItemAdvDDrill;
import gravisuite.ItemAdvancedJetPack;
import gravisuite.ItemAdvancedLappack;
import gravisuite.ItemAdvancedNanoChestPlate;
import gravisuite.ItemGraviChestPlate;
import gravisuite.ItemGraviTool;
import gravisuite.ItemSimpleItems;
import gravisuite.ItemUltimateLappack;
import gravisuite.ItemVajra;
import gravisuite.Keyboard;
import gravisuite.ServerPacketHandler;
import gravisuite.ServerProxy;
import gravisuite.ServerTickHandler;
import ic2.api.Ic2Recipes;
import ic2.api.Items;
import java.util.Random;
import java.util.logging.Level;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

@Mod(
   modid = "GraviSuite",
   name = "Gravitation Suite",
   dependencies = "required-after:IC2; after:RedPowerCore",
   version = "1.8"
)
@NetworkMod(
   clientSideRequired = true,
   serverSideRequired = false,
   clientPacketHandlerSpec = @SidedPacketHandler(
         channels = {"gravisuite"},
         packetHandler = ClientPacketHandler.class
      ),
   serverPacketHandlerSpec = @SidedPacketHandler(
         channels = {"gravisuite"},
         packetHandler = ServerPacketHandler.class
      )
)
public class GraviSuite {

   @SidedProxy(
      clientSide = "gravisuite.ClientProxy",
      serverSide = "gravisuite.ServerProxy"
   )
   public static ServerProxy proxy;
   @SidedProxy(
      clientSide = "gravisuite.KeyboardClient",
      serverSide = "gravisuite.Keyboard"
   )
   public static Keyboard keyboard;
   public static int ultimateLappackID;
   public static int graviChestPlateID;
   public static int simpleItemsID;
   public static int vajraID;
   public static int graviToolID;
   public static int advDDrillID;
   public static int advChainsawID;
   public static int advLappackID;
   public static int advJetpackID;
   public static int advNanoChestPlateID;
   public static Configuration config;
   private boolean keyDown;
   public static int hudPos;
   public static boolean displayHud;
   public static Item graviChestPlate;
   public static Item ultimateLappack;
   public static Item vajra;
   public static Item graviTool;
   public static Item advDDrill;
   public static Item advChainsaw;
   public static Item advLappack;
   public static Item advJetpack;
   public static Item advNanoChestPlate;
   public Item itemSimpleItem;
   public static ItemStack superConductorCover;
   public static ItemStack superConductor;
   public static ItemStack coolingCore;
   public static ItemStack gravitationEngine;
   public static ItemStack magnetron;
   public static ItemStack vajraCore;
   public static ItemStack itemEngineBoost;
   public static ClientTickHandler clientTickHandler;
   public static ServerTickHandler serverTickHandler;
   public static boolean disableVajraAccurate;
   public static boolean disableSounds;
   public static boolean logWrench;
   public static CreativeTabs ic2Tab;
   public static final Side side = FMLCommonHandler.instance().getEffectiveSide();
   public static Random random = new Random();
   @Instance("GraviSuite")
   public static GraviSuite instance;

   public static void getIC2Tab() {
      for(int i = 0; i < CreativeTabs.creativeTabArray.length; ++i) {
         if(CreativeTabs.creativeTabArray[i].getTabLabel() == "IC2") {
            ic2Tab = CreativeTabs.creativeTabArray[i];
         }
      }

   }

   @PreInit
   public void preInit(FMLPreInitializationEvent event) {
      Configuration config = new Configuration(event.getSuggestedConfigurationFile());

      try {
         config.load();
         graviChestPlateID = Integer.parseInt(config.get("Items", "graviChestPlateID", 30217).value);
         ultimateLappackID = Integer.parseInt(config.get("Items", "ultimateLappackID", 30218).value);
         simpleItemsID = Integer.parseInt(config.get("Items", "otherItemsID", 30219).value);
         vajraID = Integer.parseInt(config.get("Items", "vajraID", 30221).value);
         advDDrillID = Integer.parseInt(config.get("Items", "advDDrillID", 30222).value);
         advChainsawID = Integer.parseInt(config.get("Items", "advChainsawID", 30223).value);
         advLappackID = Integer.parseInt(config.get("Items", "advLappackID", 30224).value);
         advJetpackID = Integer.parseInt(config.get("Items", "advJetpackID", 30225).value);
         graviToolID = Integer.parseInt(config.get("Items", "graviToolID", 30226).value);
         advNanoChestPlateID = Integer.parseInt(config.get("items", "advNanoChestPlateID", 30227).value);
         hudPos = Integer.parseInt(config.get("Hud settings", "hudPosition", 1).value);
         displayHud = Boolean.parseBoolean(config.get("Hud settings", "Display hud", true).value);
         disableVajraAccurate = Boolean.parseBoolean(config.get("Vajra settings", "Disable Vajra accurate mode", false).value);
         disableSounds = Boolean.parseBoolean(config.get("Sounds settings", "Disable all sounds", false).value);
         logWrench = true;
         config.save();
      } catch (Exception var7) {
         FMLLog.log(Level.SEVERE, var7, "[GraviSuite] error while loading config file", new Object[0]);
         throw new RuntimeException(var7);
      }
   }

   @Init
   public void load(FMLInitializationEvent event) {
      if(side == Side.CLIENT) {
         getIC2Tab();
      }

      graviChestPlate = (new ItemGraviChestPlate(graviChestPlateID, EnumArmorMaterial.DIAMOND, proxy.addArmor("GraviSuite"), 1)).setItemName("GraviChestPlate");
      advNanoChestPlate = (new ItemAdvancedNanoChestPlate(advNanoChestPlateID, EnumArmorMaterial.DIAMOND, proxy.addArmor("GraviSuite"), 1)).setItemName("Advanced NanoChestPlate");
      ultimateLappack = (new ItemUltimateLappack(ultimateLappackID, EnumArmorMaterial.DIAMOND, proxy.addArmor("GraviSuite"), 1)).setItemName("UltimateLappack");
      advLappack = (new ItemAdvancedLappack(advLappackID, EnumArmorMaterial.DIAMOND, proxy.addArmor("GraviSuite"), 1)).setItemName("Advanced Lappack");
      advJetpack = (new ItemAdvancedJetPack(advJetpackID, EnumArmorMaterial.DIAMOND, proxy.addArmor("GraviSuite"), 1)).setItemName("Advanced Electric Jetpack");
      vajra = (new ItemVajra(vajraID, 1, EnumToolMaterial.EMERALD, new Block[0])).setItemName("Vajra");
      graviTool = (new ItemGraviTool(graviToolID, 1, EnumToolMaterial.EMERALD, new Block[0])).setItemName("GraviTool");
      advDDrill = (new ItemAdvDDrill(advDDrillID, 1, EnumToolMaterial.EMERALD, new Block[0])).setItemName("Advanced Diamond Drill");
      advChainsaw = (new ItemAdvChainsaw(advChainsawID, 1, EnumToolMaterial.IRON, new Block[0])).setItemName("Advanced Chainsaw");
      this.itemSimpleItem = new ItemSimpleItems(simpleItemsID);
      superConductorCover = new ItemStack(this.itemSimpleItem, 1, 0);
      superConductor = new ItemStack(this.itemSimpleItem, 1, 1);
      coolingCore = new ItemStack(this.itemSimpleItem, 1, 2);
      gravitationEngine = new ItemStack(this.itemSimpleItem, 1, 3);
      magnetron = new ItemStack(this.itemSimpleItem, 1, 4);
      vajraCore = new ItemStack(this.itemSimpleItem, 1, 5);
      itemEngineBoost = new ItemStack(this.itemSimpleItem, 1, 6);
      LanguageRegistry.instance().addStringLocalization("itemSuperConductorCover.name", "Изоляция сверхпроводника");
      LanguageRegistry.instance().addStringLocalization("itemSuperConductor.name", "Сверхпроводник");
      LanguageRegistry.instance().addStringLocalization("itemCoolingCore.name", "Охлаждающий элемент");
      LanguageRegistry.instance().addStringLocalization("itemGravitationEngine.name", "Гравитационный двигатель");
      LanguageRegistry.instance().addStringLocalization("itemMagnetron.name", "Магнетрон");
      LanguageRegistry.instance().addStringLocalization("itemVajraCore.name", "Ядро Ваджры");
      LanguageRegistry.instance().addStringLocalization("itemEngineBoost.name", "Ускоритель двигателя");
      LanguageRegistry.addName(graviChestPlate, "Гравитационный нагрудник");
      LanguageRegistry.addName(advNanoChestPlate, "Улучшенный нано-нагрудник");
      LanguageRegistry.addName(ultimateLappack, "Совершенный мультиранец");
      LanguageRegistry.addName(vajra, "Ваджра");
      LanguageRegistry.addName(graviTool, "Грави-инструмент");
      LanguageRegistry.addName(advLappack, "Улучшенный мультиранец");
      LanguageRegistry.addName(advJetpack, "Улучшенный реактивный ранец");
      LanguageRegistry.addName(advDDrill, "Улучшенный алмазный бур");
      LanguageRegistry.addName(advChainsaw, "Улучшенная электропила");
      ((ItemAdvDDrill)advDDrill).init();
      ((ItemAdvChainsaw)advChainsaw).init();
      MinecraftForge.setToolClass(advDDrill, "pickaxe", 3);
      MinecraftForge.setToolClass(advChainsaw, "axe", 2);
      OreDictionary.registerOre("itemSuperconductor", superConductor);
      TickRegistry.registerTickHandler(new ServerTickHandler(), Side.SERVER);
      proxy.initCore();
      proxy.registerRenderers();
   }

   @PostInit
   public void postInit(FMLPostInitializationEvent event) {
	  if (hudPos < 1 || hudPos > 4) {
         hudPos = 1;
      }

      GameRegistry.addRecipe(new ItemStack(this.itemSimpleItem, 3, 0), new Object[]{"RBR", "CCC", "RBR", Character.valueOf('R'), Items.getItem("advancedAlloy"), Character.valueOf('B'), Items.getItem("iridiumPlate"), Character.valueOf('C'), Items.getItem("carbonPlate")});
      GameRegistry.addRecipe(new ItemStack(this.itemSimpleItem, 3, 1), new Object[]{"RRR", "CBC", "RRR", Character.valueOf('R'), new ItemStack(this.itemSimpleItem, 1, 0), Character.valueOf('B'), Item.ingotGold, Character.valueOf('C'), Items.getItem("glassFiberCableItem")});
      GameRegistry.addRecipe(new ItemStack(this.itemSimpleItem, 1, 2), new Object[]{"RBR", "CDC", "RBR", Character.valueOf('R'), Items.getItem("reactorCoolantSix"), Character.valueOf('B'), Items.getItem("reactorHeatSwitchDiamond"), Character.valueOf('C'), Items.getItem("reactorPlatingHeat"), Character.valueOf('D'), Items.getItem("iridiumPlate")});
      Ic2Recipes.addCraftingRecipe(new ItemStack(this.itemSimpleItem, 1, 3), new Object[]{"RBR", "CDC", "RBR", Character.valueOf('R'), Items.getItem("teslaCoil"), Character.valueOf('B'), "itemSuperconductor", Character.valueOf('C'), new ItemStack(this.itemSimpleItem, 1, 2), Character.valueOf('D'), Items.getItem("hvTransformer")});  
      Ic2Recipes.addCraftingRecipe(new ItemStack(ultimateLappack, 1), new Object[]{"RBR", "RDR", "RAR", Character.valueOf('R'), Items.getItem("lapotronCrystal"), Character.valueOf('B'), Items.getItem("iridiumPlate"), Character.valueOf('D'), Items.getItem("lapPack"), Character.valueOf('A'), "itemSuperconductor"});
      Ic2Recipes.addCraftingRecipe(new ItemStack(ultimateLappack, 1), new Object[]{"RBR", "RDR", "RAR", Character.valueOf('R'), Items.getItem("lapotronCrystal"), Character.valueOf('B'), Items.getItem("iridiumPlate"), Character.valueOf('D'), new ItemStack(advLappack, 1), Character.valueOf('A'), "itemSuperconductor"});
      Ic2Recipes.addCraftingRecipe(new ItemStack(graviChestPlate, 1), new Object[]{"RAR", "DBD", "RCR", Character.valueOf('R'), "itemSuperconductor", Character.valueOf('A'), Items.getItem("quantumBodyarmor"), Character.valueOf('D'), new ItemStack(this.itemSimpleItem, 1, 3), Character.valueOf('B'), Items.getItem("hvTransformer"), Character.valueOf('C'), ultimateLappack});
      Ic2Recipes.addCraftingRecipe(new ItemStack(advLappack, 1), new Object[]{" A ", " B ", " C ", Character.valueOf('A'), Items.getItem("lapPack"), Character.valueOf('B'), Items.getItem("advancedCircuit"), Character.valueOf('C'), Items.getItem("lapotronCrystal")});      
      Ic2Recipes.addCraftingRecipe(new ItemStack(advDDrill, 1), new Object[]{"   ", "ABA", "CAC", Character.valueOf('A'), Items.getItem("overclockerUpgrade"), Character.valueOf('B'), Items.getItem("diamondDrill"), Character.valueOf('C'), Items.getItem("advancedCircuit")});
      Ic2Recipes.addCraftingRecipe(new ItemStack(advChainsaw, 1), new Object[]{" F ", "ABA", "CAC", Character.valueOf('F'), Item.diamond, Character.valueOf('A'), Items.getItem("overclockerUpgrade"), Character.valueOf('B'), Items.getItem("chainsaw"), Character.valueOf('C'), Items.getItem("advancedCircuit")});
      GameRegistry.addRecipe(new ShapedOreRecipe(magnetron, new Object[]{"ABA", "BCB", "ABA", Character.valueOf('A'), "ingotRefinedIron", Character.valueOf('B'), "ingotCopper", Character.valueOf('C'), "itemSuperconductor"}));
      Ic2Recipes.addCraftingRecipe(vajraCore, new Object[]{" A ", "BCB", "FDF", Character.valueOf('A'), magnetron, Character.valueOf('B'), Items.getItem("iridiumPlate"), Character.valueOf('C'), Items.getItem("teslaCoil"), Character.valueOf('F'), "itemSuperconductor", Character.valueOf('D'), Items.getItem("hvTransformer")});
      Ic2Recipes.addCraftingRecipe(new ItemStack(vajra, 1), new Object[]{"ABA", "CDC", "FGF", Character.valueOf('A'), "ingotRefinedIron", Character.valueOf('B'), Items.getItem("energyCrystal"), Character.valueOf('C'), Items.getItem("carbonPlate"), Character.valueOf('D'), vajraCore, Character.valueOf('F'), Items.getItem("advancedAlloy"), Character.valueOf('G'), Items.getItem("lapotronCrystal")});    
      Ic2Recipes.addCraftingRecipe(new ItemStack(graviTool, 1), new Object[]{"ABA", "CDC", "EFG", Character.valueOf('A'), Items.getItem("carbonPlate"), Character.valueOf('B'), Items.getItem("electricHoe"), Character.valueOf('C'), Items.getItem("advancedAlloy"), Character.valueOf('D'), Items.getItem("energyCrystal"), Character.valueOf('E'), Items.getItem("electricWrench"), Character.valueOf('F'), Items.getItem("advancedCircuit"), Character.valueOf('G'), Items.getItem("electricTreetap")});
      Ic2Recipes.addCraftingRecipe(itemEngineBoost, new Object[]{"ABA", "CDC", "BFB", Character.valueOf('A'), Item.lightStoneDust, Character.valueOf('B'), Items.getItem("advancedAlloy"), Character.valueOf('C'), Items.getItem("advancedCircuit"), Character.valueOf('D'), Items.getItem("overclockerUpgrade"), Character.valueOf('F'), Items.getItem("reactorVentDiamond")});
      Ic2Recipes.addCraftingRecipe(new ItemStack(advJetpack, 1), new Object[]{"ABA", "CDC", "EFE", Character.valueOf('A'), Items.getItem("carbonPlate"), Character.valueOf('B'), Items.getItem("electricJetpack"), Character.valueOf('C'), itemEngineBoost, Character.valueOf('D'), advLappack, Character.valueOf('E'), Items.getItem("glassFiberCableItem"), Character.valueOf('F'), Items.getItem("advancedCircuit")});    
      Ic2Recipes.addCraftingRecipe(new ItemStack(advNanoChestPlate, 1), new Object[]{"ABA", "ACA", "DFD", Character.valueOf('A'), Items.getItem("carbonPlate"), Character.valueOf('B'), new ItemStack(advJetpack, 1), Character.valueOf('C'), Items.getItem("nanoBodyarmor"), Character.valueOf('D'), Items.getItem("glassFiberCableItem"), Character.valueOf('F'), Items.getItem("advancedCircuit")});     

   }

   public static boolean isSimulating() {
      return !FMLCommonHandler.instance().getEffectiveSide().isClient();
   }

   public static NBTTagCompound getOrCreateNbtData(ItemStack itemstack) {
      NBTTagCompound nbttagcompound = itemstack.getTagCompound();
      if(nbttagcompound == null) {
         nbttagcompound = new NBTTagCompound();
         itemstack.setTagCompound(nbttagcompound);
         nbttagcompound.setInteger("charge", 0);
      }

      return nbttagcompound;
   }

}
