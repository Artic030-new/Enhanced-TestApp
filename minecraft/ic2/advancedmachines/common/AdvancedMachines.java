package ic2.advancedmachines.common;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import ic2.advancedmachines.common.BlockAdvancedMachines;
import ic2.advancedmachines.common.IProxy;
import ic2.advancedmachines.common.ItemAdvancedMachine;
import ic2.advancedmachines.common.ItemDust;
import ic2.advancedmachines.common.TileEntityCentrifugeExtractor;
import ic2.advancedmachines.common.TileEntityRotaryMacerator;
import ic2.advancedmachines.common.TileEntitySingularityCompressor;
import ic2.api.Ic2Recipes;
import ic2.api.Items;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

@Mod(
   modid = "AdvancedMachines",
   name = "IC2 Advanced Machines Addon",
   version = "4.7b",
   dependencies = "required-after:IC2"
)
@NetworkMod(
   clientSideRequired = true,
   serverSideRequired = false
)
public class AdvancedMachines implements IGuiHandler, IProxy {

   public static AdvancedMachines instance;
   public static Configuration config;
   public static CreativeTabs ic2Tab;
   public static Block blockAdvancedMachine;
   public static ItemStack stackRotaryMacerator;
   public static ItemStack stackSingularityCompressor;
   public static ItemStack stackCentrifugeExtractor;
   private int refIronID;
   public static Item refinedIronDust;
   public static int guiIdRotary;
   public static int guiIdSingularity;
   public static int guiIdCentrifuge;
   public static String advMaceName = "Роторный дробитель";
   public static String advCompName = "Сингулярный компрессор";
   public static String advExtcName = "Центрифужный экстрактор";
   public static String refIronDustName = "Переработанная железная пыль";
   public static ItemStack overClockerStack;
   public static ItemStack transformerStack;
   public static ItemStack energyStorageUpgradeStack;
   public static String advMaceSound = "Machines/MaceratorOp.ogg";
   public static String advCompSound = "Machines/CompressorOp.ogg";
   public static String advExtcSound = "Machines/ExtractorOp.ogg";
   public static String interruptSound = "Machines/InterruptOne.ogg";
   public static int defaultEnergyConsume = 3;
   public static int defaultAcceleration = 2;
   public static int overClockSpeedBonus = 500;
   public static double overClockEnergyRatio = 2.0D;
   public static double overClockAccelRatio = 1.6D;
   public static double overLoadInputRatio = 4.0D;
   public static final Side side = FMLCommonHandler.instance().getEffectiveSide();
   @SidedProxy(
      clientSide = "ic2.advancedmachines.client.AdvancedMachinesClient",
      serverSide = "ic2.advancedmachines.common.AdvancedMachines"
   )
   public static IProxy proxy;
   
   public static void getIC2Tab() {
	  for(int i = 0; i < CreativeTabs.creativeTabArray.length; ++i) {
	     if(CreativeTabs.creativeTabArray[i].getTabLabel() == "IC2") {
	        ic2Tab = CreativeTabs.creativeTabArray[i];
	     }
	   }
     }

   @PreInit
   public void preInit(FMLPreInitializationEvent event) {
      instance = this;
      config = new Configuration(event.getSuggestedConfigurationFile());
      config.load();
   }

   @Init
   public void load(FMLInitializationEvent event) {
	  if(side == Side.CLIENT) {
	     getIC2Tab();
      }
	  
      blockAdvancedMachine = (new BlockAdvancedMachines(config.getBlock("IDs", "AdvancedMachineBlock", 188).getInt())).setBlockName("blockAdvMachine");
      this.refIronID = config.getItem("IDs", "refIronID", 29775).getInt(29775);
      guiIdRotary = config.get("IDs", "guiIdRotary", 40).getInt();
      guiIdSingularity = config.get("IDs", "guiIdSingularity", 41).getInt();
      guiIdCentrifuge = config.get("IDs", "guiIdCentrifuge", 42).getInt();
      Property prop = config.get("Sounds", "advCompressorSound", advCompSound);
      prop.comment = "Sound files to use on operation. Remember to use \'/\' instead of backslashes and the Sound directory starts on ic2/sounds. Set empty to disable.";
      advCompSound = prop.value;
      advExtcSound = config.get("Sounds", "advExtractorSound", advExtcSound).value;
      advMaceSound = config.get("Sounds", "advMaceratorSound", advMaceSound).value;
      prop = config.get("Sounds", "interuptSound", interruptSound);
      prop.comment = "Sound played when a machine process is interrupted";
      interruptSound = prop.value;
      prop = config.get("translation", "nameAdvCompressor", advCompName);
      prop.comment = "Item names. This will also affect their GUI";
      advCompName = prop.value;
      advExtcName = config.get("translation", "nameAdvExtractor", advExtcName).value;
      advMaceName = config.get("translation", "nameAdvMacerator", advMaceName).value;
      refIronDustName = config.get("translation", "nameAdvRefIronDust", refIronDustName).value;
      prop = config.get("OPness control", "baseEnergyConsumption", "3");
      prop.comment = "Base power draw per work tick.";
      defaultEnergyConsume = prop.getInt(3);
      prop = config.get("OPness control", "baseMachineAcceleration", "2");
      prop.comment = "Base machine speedup each work tick.";
      defaultAcceleration = prop.getInt(2);
      prop = config.get("OPness control", "overClockSpeedBonus", "500");
      prop.comment = "How much additional max speed does each Overclocker grant.";
      overClockSpeedBonus = prop.getInt(500);
      prop = config.get("OPness control", "overClockEnergyRatio", "2.0");
      prop.comment = "Exponent of Overclocker energy consumption function. 2.0 equals a squared rise of power draw as you add Overclockers.";
      overClockEnergyRatio = Double.valueOf(prop.value).doubleValue();
      prop = config.get("OPness control", "overClockAccelRatio", "1.6");
      prop.comment = "Exponent of Overclocker acceleration function. 1.6 equals a less-than-squared rise of acceleration speed as you add Overclockers.";
      overClockAccelRatio = Double.valueOf(prop.value).doubleValue();
      prop = config.get("OPness control", "overLoadInputRatio", "4.0");
      prop.comment = "Exponent of Transformer input function. Determines rise of maximum power intake as you add Transformers.";
      overLoadInputRatio = Double.valueOf(prop.value).doubleValue();
      if(config != null) {
         config.save();
      }

      proxy.load();
      GameRegistry.registerBlock(blockAdvancedMachine, ItemAdvancedMachine.class, "blockAdvMachine");
      GameRegistry.registerTileEntity(TileEntityRotaryMacerator.class, "Роторный дробитель");
      GameRegistry.registerTileEntity(TileEntitySingularityCompressor.class, "Сингулярный компрессор");
      GameRegistry.registerTileEntity(TileEntityCentrifugeExtractor.class, "Центрифужный экстрактор");
      NetworkRegistry.instance().registerGuiHandler(this, this);
   }

   @PostInit
   public void afterModsLoaded(FMLPostInitializationEvent event) {
      stackRotaryMacerator = new ItemStack(blockAdvancedMachine, 1, 0);
      Ic2Recipes.addCraftingRecipe(stackRotaryMacerator, new Object[]{"RRR", "RMR", "RAR", Character.valueOf('R'), Items.getItem("refinedIronIngot"), Character.valueOf('M'), Items.getItem("macerator"), Character.valueOf('A'), Items.getItem("advancedMachine")});
      LanguageRegistry.addName(stackRotaryMacerator, advMaceName);
      stackSingularityCompressor = new ItemStack(blockAdvancedMachine, 1, 1);
      Ic2Recipes.addCraftingRecipe(stackSingularityCompressor, new Object[]{"RRR", "RMR", "RAR", Character.valueOf('R'), Block.obsidian, Character.valueOf('M'), Items.getItem("compressor"), Character.valueOf('A'), Items.getItem("advancedMachine")});
      LanguageRegistry.addName(stackSingularityCompressor, advCompName);
      stackCentrifugeExtractor = new ItemStack(blockAdvancedMachine, 1, 2);
      Ic2Recipes.addCraftingRecipe(stackCentrifugeExtractor, new Object[]{"RRR", "RMR", "RAR", Character.valueOf('R'), Items.getItem("electrolyzedWaterCell"), Character.valueOf('M'), Items.getItem("extractor"), Character.valueOf('A'), Items.getItem("advancedMachine")});
      LanguageRegistry.addName(stackCentrifugeExtractor, advExtcName);
      overClockerStack = Items.getItem("overclockerUpgrade");
      transformerStack = Items.getItem("transformerUpgrade");
      energyStorageUpgradeStack = Items.getItem("energyStorageUpgrade");
      refinedIronDust = (new ItemDust(this.refIronID)).setItemName("refinedIronDust");
      LanguageRegistry.addName(refinedIronDust, refIronDustName);
      GameRegistry.addSmelting(refinedIronDust.itemID, Items.getItem("refinedIronIngot"), 1.0F);
   }

   public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
      TileEntity te = world.getBlockTileEntity(x, y, z);
      if(te != null) {
         if(te instanceof TileEntityRotaryMacerator) {
            return ((TileEntityRotaryMacerator)te).getGuiContainer(player.inventory);
         }

         if(te instanceof TileEntityCentrifugeExtractor) {
            return ((TileEntityCentrifugeExtractor)te).getGuiContainer(player.inventory);
         }

         if(te instanceof TileEntitySingularityCompressor) {
            return ((TileEntitySingularityCompressor)te).getGuiContainer(player.inventory);
         }
      }

      return null;
   }

   public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
      return proxy.getGuiElementForClient(ID, player, world, x, y, z);
   }

   public void load() {}

   public Object getGuiElementForClient(int ID, EntityPlayer player, World world, int x, int y, int z) {
      return null;
   }

}
