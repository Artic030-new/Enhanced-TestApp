package ic2.core;

import cpw.mods.fml.common.IFuelHandler;
import cpw.mods.fml.common.IPlayerTracker;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.IWorldGenerator;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.IConnectionHandler;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.common.network.NetworkMod.SidedPacketHandler;
import cpw.mods.fml.common.network.NetworkMod.VersionCheckHandler;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import ic2.api.Crops;
import ic2.api.ExplosionWhitelist;
import ic2.api.Ic2Recipes;
import ic2.core.CreativeTabIC2;
import ic2.core.EnergyNet;
import ic2.core.ExplosionIC2;
import ic2.core.IC2Achievements;
import ic2.core.IC2DamageSource;
import ic2.core.IC2Loot;
import ic2.core.IC2Potion;
import ic2.core.IItemTickListener;
import ic2.core.ITickCallback;
import ic2.core.Ic2Items;
import ic2.core.Platform;
import ic2.core.RecipeGradual;
import ic2.core.WorldData;
import ic2.core.audio.AudioManager;
import ic2.core.block.BlockBarrel;
import ic2.core.block.BlockCrop;
import ic2.core.block.BlockDynamite;
import ic2.core.block.BlockFoam;
import ic2.core.block.BlockIC2Door;
import ic2.core.block.BlockITNT;
import ic2.core.block.BlockMetal;
import ic2.core.block.BlockPoleFence;
import ic2.core.block.BlockResin;
import ic2.core.block.BlockRubLeaves;
import ic2.core.block.BlockRubSapling;
import ic2.core.block.BlockRubWood;
import ic2.core.block.BlockRubberSheet;
import ic2.core.block.BlockScaffold;
import ic2.core.block.BlockTex;
import ic2.core.block.BlockTexGlass;
import ic2.core.block.BlockWall;
import ic2.core.block.EntityDynamite;
import ic2.core.block.EntityItnt;
import ic2.core.block.EntityNuke;
import ic2.core.block.EntityStickyDynamite;
import ic2.core.block.TileEntityBarrel;
import ic2.core.block.TileEntityBlock;
import ic2.core.block.TileEntityCrop;
import ic2.core.block.WorldGenRubTree;
import ic2.core.block.crop.IC2Crops;
import ic2.core.block.generator.block.BlockGenerator;
import ic2.core.block.generator.block.BlockReactorChamber;
import ic2.core.block.generator.tileentity.TileEntityGenerator;
import ic2.core.block.generator.tileentity.TileEntityGeoGenerator;
import ic2.core.block.generator.tileentity.TileEntityNuclearReactorElectric;
import ic2.core.block.generator.tileentity.TileEntityReactorChamberElectric;
import ic2.core.block.generator.tileentity.TileEntitySolarGenerator;
import ic2.core.block.generator.tileentity.TileEntityWaterGenerator;
import ic2.core.block.generator.tileentity.TileEntityWindGenerator;
import ic2.core.block.machine.BlockMachine;
import ic2.core.block.machine.BlockMachine2;
import ic2.core.block.machine.BlockMiningPipe;
import ic2.core.block.machine.BlockMiningTip;
import ic2.core.block.machine.tileentity.TileEntityCanner;
import ic2.core.block.machine.tileentity.TileEntityCompressor;
import ic2.core.block.machine.tileentity.TileEntityCropmatron;
import ic2.core.block.machine.tileentity.TileEntityElecFurnace;
import ic2.core.block.machine.tileentity.TileEntityElectrolyzer;
import ic2.core.block.machine.tileentity.TileEntityExtractor;
import ic2.core.block.machine.tileentity.TileEntityInduction;
import ic2.core.block.machine.tileentity.TileEntityIronFurnace;
import ic2.core.block.machine.tileentity.TileEntityMacerator;
import ic2.core.block.machine.tileentity.TileEntityMagnetizer;
import ic2.core.block.machine.tileentity.TileEntityMatter;
import ic2.core.block.machine.tileentity.TileEntityMiner;
import ic2.core.block.machine.tileentity.TileEntityPump;
import ic2.core.block.machine.tileentity.TileEntityRecycler;
import ic2.core.block.machine.tileentity.TileEntityTeleporter;
import ic2.core.block.machine.tileentity.TileEntityTerra;
import ic2.core.block.machine.tileentity.TileEntityTesla;
import ic2.core.block.personal.BlockPersonal;
import ic2.core.block.personal.TileEntityEnergyOMat;
import ic2.core.block.personal.TileEntityPersonalChest;
import ic2.core.block.personal.TileEntityTradeOMat;
import ic2.core.block.wiring.BlockCable;
import ic2.core.block.wiring.BlockElectric;
import ic2.core.block.wiring.BlockLuminator;
import ic2.core.block.wiring.TileEntityCable;
import ic2.core.block.wiring.TileEntityCableDetector;
import ic2.core.block.wiring.TileEntityCableSplitter;
import ic2.core.block.wiring.TileEntityElectricBatBox;
import ic2.core.block.wiring.TileEntityElectricMFE;
import ic2.core.block.wiring.TileEntityElectricMFSU;
import ic2.core.block.wiring.TileEntityLuminator;
import ic2.core.block.wiring.TileEntityTransformerHV;
import ic2.core.block.wiring.TileEntityTransformerLV;
import ic2.core.block.wiring.TileEntityTransformerMV;
import ic2.core.item.ItemBattery;
import ic2.core.item.ItemBatteryDischarged;
import ic2.core.item.ItemBatterySU;
import ic2.core.item.ItemBooze;
import ic2.core.item.ItemCell;
import ic2.core.item.ItemCropSeed;
import ic2.core.item.ItemFertilizer;
import ic2.core.item.ItemFuelCanEmpty;
import ic2.core.item.ItemFuelCanFilled;
import ic2.core.item.ItemGradual;
import ic2.core.item.ItemIC2;
import ic2.core.item.ItemMug;
import ic2.core.item.ItemMugCoffee;
import ic2.core.item.ItemResin;
import ic2.core.item.ItemScrapbox;
import ic2.core.item.ItemTerraWart;
import ic2.core.item.ItemTinCan;
import ic2.core.item.ItemToolbox;
import ic2.core.item.ItemUpgradeModule;
import ic2.core.item.armor.ItemArmorBatpack;
import ic2.core.item.armor.ItemArmorCFPack;
import ic2.core.item.armor.ItemArmorHazmat;
import ic2.core.item.armor.ItemArmorIC2;
import ic2.core.item.armor.ItemArmorJetpack;
import ic2.core.item.armor.ItemArmorJetpackElectric;
import ic2.core.item.armor.ItemArmorLappack;
import ic2.core.item.armor.ItemArmorNanoSuit;
import ic2.core.item.armor.ItemArmorNightvisionGoggles;
import ic2.core.item.armor.ItemArmorQuantumSuit;
import ic2.core.item.armor.ItemArmorSolarHelmet;
import ic2.core.item.armor.ItemArmorStaticBoots;
import ic2.core.item.block.ItemBarrel;
import ic2.core.item.block.ItemBlockRare;
import ic2.core.item.block.ItemCable;
import ic2.core.item.block.ItemDynamite;
import ic2.core.item.block.ItemIC2Door;
import ic2.core.item.reactor.ItemReactorCondensator;
import ic2.core.item.reactor.ItemReactorDepletedUranium;
import ic2.core.item.reactor.ItemReactorHeatStorage;
import ic2.core.item.reactor.ItemReactorHeatSwitch;
import ic2.core.item.reactor.ItemReactorHeatpack;
import ic2.core.item.reactor.ItemReactorPlating;
import ic2.core.item.reactor.ItemReactorReflector;
import ic2.core.item.reactor.ItemReactorUranium;
import ic2.core.item.reactor.ItemReactorVent;
import ic2.core.item.reactor.ItemReactorVentSpread;
import ic2.core.item.tfbp.ItemTFBPChilling;
import ic2.core.item.tfbp.ItemTFBPCultivation;
import ic2.core.item.tfbp.ItemTFBPDesertification;
import ic2.core.item.tfbp.ItemTFBPFlatification;
import ic2.core.item.tfbp.ItemTFBPIrrigation;
import ic2.core.item.tfbp.ItemTFBPMushroom;
import ic2.core.item.tool.EntityMiningLaser;
import ic2.core.item.tool.ItemCropnalyzer;
import ic2.core.item.tool.ItemDebug;
import ic2.core.item.tool.ItemElectricToolChainsaw;
import ic2.core.item.tool.ItemElectricToolDDrill;
import ic2.core.item.tool.ItemElectricToolDrill;
import ic2.core.item.tool.ItemElectricToolHoe;
import ic2.core.item.tool.ItemFrequencyTransmitter;
import ic2.core.item.tool.ItemIC2Axe;
import ic2.core.item.tool.ItemIC2Hoe;
import ic2.core.item.tool.ItemIC2Pickaxe;
import ic2.core.item.tool.ItemIC2Spade;
import ic2.core.item.tool.ItemIC2Sword;
import ic2.core.item.tool.ItemNanoSaber;
import ic2.core.item.tool.ItemRemote;
import ic2.core.item.tool.ItemScanner;
import ic2.core.item.tool.ItemScannerAdv;
import ic2.core.item.tool.ItemSprayer;
import ic2.core.item.tool.ItemToolCutter;
import ic2.core.item.tool.ItemToolMeter;
import ic2.core.item.tool.ItemToolMiningLaser;
import ic2.core.item.tool.ItemToolPainter;
import ic2.core.item.tool.ItemToolWrench;
import ic2.core.item.tool.ItemToolWrenchElectric;
import ic2.core.item.tool.ItemTreetap;
import ic2.core.item.tool.ItemTreetapElectric;
import ic2.core.network.NetworkManager;
import ic2.core.network.NetworkManagerClient;
import ic2.core.util.Keyboard;
import ic2.core.util.StackUtil;
import ic2.core.util.TextureIndex;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import java.util.Map.Entry;
import java.util.logging.Logger;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.NetLoginHandler;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet1Login;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.storage.SaveHandler;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.EnumHelper;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Property;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingSpecialSpawnEvent;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.event.world.WorldEvent.Unload;
import net.minecraftforge.liquids.LiquidContainerData;
import net.minecraftforge.liquids.LiquidContainerRegistry;
import net.minecraftforge.liquids.LiquidDictionary;
import net.minecraftforge.liquids.LiquidStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.OreDictionary.OreRegisterEvent;

@Mod(
   modid = "IC2",
   name = "IndustrialCraft 2",
   version = "1.115.207-lf",
   useMetadata = true,
   certificateFingerprint = "de041f9f6187debbc77034a344134053277aa3b0"
)
@NetworkMod(
   clientSideRequired = true,
   clientPacketHandlerSpec =       @SidedPacketHandler(
         channels = {"ic2"},
         packetHandler = NetworkManagerClient.class
      ),
   serverPacketHandlerSpec =       @SidedPacketHandler(
         channels = {"ic2"},
         packetHandler = NetworkManager.class
      )
)
public class IC2 implements ITickHandler, IWorldGenerator, IFuelHandler, IConnectionHandler, IPlayerTracker {

   public static final String VERSION = "1.115.207-lf";
   public static final boolean BETA = true;
   private static IC2 instance = null;
   @SidedProxy(
      clientSide = "ic2.core.PlatformClient",
      serverSide = "ic2.core.Platform"
   )
   public static Platform platform;
   @SidedProxy(
      clientSide = "ic2.core.network.NetworkManagerClient",
      serverSide = "ic2.core.network.NetworkManager"
   )
   public static NetworkManager network;
   @SidedProxy(
      clientSide = "ic2.core.util.KeyboardClient",
      serverSide = "ic2.core.util.Keyboard"
   )
   public static Keyboard keyboard;
   @SidedProxy(
      clientSide = "ic2.core.audio.AudioManagerClient",
      serverSide = "ic2.core.audio.AudioManager"
   )
   public static AudioManager audioManager;
   @SidedProxy(
      clientSide = "ic2.core.util.TextureIndexClient",
      serverSide = "ic2.core.util.TextureIndex"
   )
   public static TextureIndex textureIndex;
   public static Logger log;
   public static IC2Achievements achievements;
   public static int cableRenderId;
   public static int fenceRenderId;
   public static int miningPipeRenderId;
   public static int luminatorRenderId;
   public static int cropRenderId;
   public static Random random = new Random();
   public static int windStrength;
   public static int windTicker;
   public static Map valuableOres = new TreeMap();
   public static boolean enableCraftingBucket;
   public static boolean enableCraftingCoin;
   public static boolean enableCraftingGlowstoneDust;
   public static boolean enableCraftingGunpowder;
   public static boolean enableCraftingITnt;
   public static boolean enableCraftingNuke;
   public static boolean enableCraftingRail;
   public static boolean enableDynamicIdAllocation;
   public static boolean enableLoggingWrench;
   public static boolean enableSecretRecipeHiding;
   public static boolean enableQuantumSpeedOnSprint;
   public static boolean enableMinerLapotron;
   public static boolean enableTeleporterInventory;
   public static boolean enableBurningScrap;
   public static boolean enableWorldGenTreeRubber;
   public static boolean enableWorldGenOreCopper;
   public static boolean enableWorldGenOreTin;
   public static boolean enableWorldGenOreUranium;
   public static float explosionPowerNuke;
   public static float explosionPowerReactorMax;
   public static int energyGeneratorBase;
   public static int energyGeneratorGeo;
   public static int energyGeneratorWater;
   public static int energyGeneratorSolar;
   public static int energyGeneratorWind;
   public static int energyGeneratorNuclear;
   public static boolean suddenlyHoes;
   public static boolean seasonal;
   private static boolean showDisclaimer;
   public static boolean enableSteamReactor;
   public static float oreDensityFactor;
   public static boolean initialized;
   public static Properties runtimeIdProperties;
   public static CreativeTabIC2 tabIC2;
   private static boolean silverDustSmeltingRegistered;
   private static Field dropChances;
   private static Property dynamicIdAllocationProp;
   public static final int networkProtocolVersion = 1;


   public IC2() {
      instance = this;
   }

   public static IC2 getInstance() {
      return instance;
   }

   @VersionCheckHandler
   public boolean checkVersion(String version) {
      String[] partsLocal = "1.115.207-lf".split("\\.");
      String[] partsRemote = version.split("\\.");
      return partsLocal.length >= 2 && partsRemote.length >= 2 && partsLocal[0].equals(partsRemote[0]) && partsLocal[1].equals(partsRemote[1]);
   }

   @PreInit
   public void load(FMLPreInitializationEvent event) {
      log = event.getModLog();
      short minForge = 369;
      int forge = ForgeVersion.getBuildVersion();
      if(forge > 0 && forge < minForge) {
         platform.displayError("The currently installed version of Minecraft Forge (" + ForgeVersion.getMajorVersion() + "." + ForgeVersion.getMinorVersion() + "." + ForgeVersion.getRevisionVersion() + "." + forge + ") is too old.\n" + "Please update the Minecraft Forge.\n" + "\n" + "(Technical information: " + forge + " < " + minForge + ")");
      }

      Configuration config;
      try {
         File prop = new File(new File(platform.getMinecraftDir(), "config"), "IC2.cfg");
         config = new Configuration(prop);
         config.load();
         log.info("Config loaded from " + prop.getAbsolutePath());
      } catch (Exception var15) {
         log.warning("Error while trying to access configuration! " + var15);
         config = null;
      }

      if(config != null) {
         dynamicIdAllocationProp = config.get("general", "enableDynamicIdAllocation", enableDynamicIdAllocation);
         dynamicIdAllocationProp.comment = "Enable searching for free block ids, will get disabled after the next successful load";
         enableDynamicIdAllocation = Boolean.parseBoolean(dynamicIdAllocationProp.value);
         Property var16 = config.get("general", "enableCraftingBucket", enableCraftingBucket);
         var16.comment = "Enable crafting of buckets out of tin";
         enableCraftingBucket = Boolean.parseBoolean(var16.value);
         var16 = config.get("general", "enableCraftingCoin", enableCraftingCoin);
         var16.comment = "Enable crafting of Industrial Credit coins";
         enableCraftingCoin = Boolean.parseBoolean(var16.value);
         var16 = config.get("general", "enableCraftingGlowstoneDust", enableCraftingGlowstoneDust);
         var16.comment = "Enable crafting of glowstone dust out of dusts";
         enableCraftingGlowstoneDust = Boolean.parseBoolean(var16.value);
         var16 = config.get("general", "enableCraftingGunpowder", enableCraftingGunpowder);
         var16.comment = "Enable crafting of gunpowder out of dusts";
         enableCraftingGunpowder = Boolean.parseBoolean(var16.value);
         var16 = config.get("general", "enableCraftingITnt", enableCraftingITnt);
         var16.comment = "Enable crafting of ITNT";
         enableCraftingITnt = Boolean.parseBoolean(var16.value);
         var16 = config.get("general", "enableCraftingNuke", enableCraftingNuke);
         var16.comment = "Enable crafting of nukes";
         enableCraftingNuke = Boolean.parseBoolean(var16.value);
         var16 = config.get("general", "enableCraftingRail", enableCraftingRail);
         var16.comment = "Enable crafting of rails out of bronze";
         enableCraftingRail = Boolean.parseBoolean(var16.value);
         var16 = config.get("general", "enableSecretRecipeHiding", enableSecretRecipeHiding);
         var16.comment = "Enable hiding of secret recipes in CraftGuide/NEI";
         enableSecretRecipeHiding = Boolean.parseBoolean(var16.value);
         var16 = config.get("general", "enableQuantumSpeedOnSprint", enableQuantumSpeedOnSprint);
         var16.comment = "Enable activation of the quantum leggings\' speed boost when sprinting instead of holding the boost key";
         enableQuantumSpeedOnSprint = Boolean.parseBoolean(var16.value);
         var16 = config.get("general", "enableMinerLapotron", enableMinerLapotron);
         var16.comment = "Enable usage of lapotron crystals on miners";
         enableMinerLapotron = Boolean.parseBoolean(var16.value);
         var16 = config.get("general", "enableTeleporterInventory", enableTeleporterInventory);
         var16.comment = "Enable calculation of inventory weight when going through a teleporter";
         enableTeleporterInventory = Boolean.parseBoolean(var16.value);
         var16 = config.get("general", "enableBurningScrap", enableBurningScrap);
         var16.comment = "Enable burning of scrap in a generator";
         enableBurningScrap = Boolean.parseBoolean(var16.value);
         var16 = config.get("general", "enableLoggingWrench", enableLoggingWrench);
         var16.comment = "Enable logging of players when they remove a machine using a wrench";
         enableLoggingWrench = Boolean.parseBoolean(var16.value);
         var16 = config.get("general", "enableWorldGenTreeRubber", enableWorldGenTreeRubber);
         var16.comment = "Enable generation of rubber trees in the world";
         enableWorldGenTreeRubber = Boolean.parseBoolean(var16.value);
         var16 = config.get("general", "enableWorldGenOreCopper", enableWorldGenOreCopper);
         var16.comment = "Enable generation of copper in the world";
         enableWorldGenOreCopper = Boolean.parseBoolean(var16.value);
         var16 = config.get("general", "enableWorldGenOreTin", enableWorldGenOreTin);
         var16.comment = "Enable generation of tin in the world";
         enableWorldGenOreTin = Boolean.parseBoolean(var16.value);
         var16 = config.get("general", "enableWorldGenOreUranium", enableWorldGenOreUranium);
         var16.comment = "Enable generation of uranium in the world";
         enableWorldGenOreUranium = Boolean.parseBoolean(var16.value);
         var16 = config.get("general", "enableSteamReactor", enableSteamReactor);
         var16.comment = "Enable steam-outputting reactors if Railcraft is installed";
         enableSteamReactor = Boolean.parseBoolean(var16.value);
         var16 = config.get("general", "explosionPowerNuke", Float.toString(explosionPowerNuke));
         var16.comment = "Explosion power of a nuke, where TNT is 4";
         explosionPowerNuke = Float.parseFloat(var16.value);
         var16 = config.get("general", "explosionPowerReactorMax", Float.toString(explosionPowerReactorMax));
         var16.comment = "Maximum explosion power of a nuclear reactor, where TNT is 4";
         explosionPowerReactorMax = Float.parseFloat(var16.value);
         var16 = config.get("general", "energyGeneratorBase", energyGeneratorBase);
         var16.comment = "Base energy generation values - increase those for higher energy yield";
         energyGeneratorBase = Integer.parseInt(var16.value);
         energyGeneratorGeo = Integer.parseInt(config.get("general", "energyGeneratorGeo", energyGeneratorGeo).value);
         energyGeneratorWater = Integer.parseInt(config.get("general", "energyGeneratorWater", energyGeneratorWater).value);
         energyGeneratorSolar = Integer.parseInt(config.get("general", "energyGeneratorSolar", energyGeneratorSolar).value);
         energyGeneratorWind = Integer.parseInt(config.get("general", "energyGeneratorWind", energyGeneratorWind).value);
         energyGeneratorNuclear = Integer.parseInt(config.get("general", "energyGeneratorNuclear", energyGeneratorNuclear).value);
         var16 = config.get("general", "valuableOres", getValuableOreString());
         var16.comment = "List of valuable ores the miner should look for. Comma separated, format is id-metadata:value where value should be at least 1 to be considered by the miner";
         setValuableOreFromString(var16.value);
         var16 = config.get("general", "valuableOres", getValuableOreString());
         var16.comment = "List of valuable ores the miner should look for. Comma separated, format is id-metadata:value where value should be at least 1 to be considered by the miner";
         setValuableOreFromString(var16.value);
         var16 = config.get("general", "oreDensityFactor", Float.toString(oreDensityFactor));
         var16.comment = "Factor to adjust the ore generation rate";
         oreDensityFactor = Float.parseFloat(var16.value);
         config.save();
      }

      audioManager.initialize(config);
      runtimeIdProperties.put("initialVersion", "1.115.207-lf");
      EnumHelper.addToolMaterial("IC2_BRONZE", 2, 350, 6.0F, 2, 13);
      EnumArmorMaterial bronzeArmorMaterial = EnumHelper.addArmorMaterial("IC2_BRONZE", 15, new int[]{3, 8, 6, 3}, 9);
      EnumArmorMaterial alloyArmorMaterial = EnumHelper.addArmorMaterial("IC2_ALLOY", 50, new int[]{4, 9, 7, 4}, 12);
      if(enableWorldGenOreCopper) {
         Ic2Items.copperOre = new ItemStack((new BlockTex(getBlockIdFor(config, "blockOreCopper", 249), 32, Material.rock)).setHardness(3.0F).setResistance(5.0F).setBlockName("blockOreCopper").setCreativeTab(tabIC2));
         GameRegistry.registerBlock(Block.blocksList[Ic2Items.copperOre.itemID], "copperOre");
      }

      if(enableWorldGenOreTin) {
         Ic2Items.tinOre = new ItemStack((new BlockTex(getBlockIdFor(config, "blockOreTin", 248), 33, Material.rock)).setHardness(3.0F).setResistance(5.0F).setBlockName("blockOreTin").setCreativeTab(tabIC2));
         GameRegistry.registerBlock(Block.blocksList[Ic2Items.tinOre.itemID], "tinOre");
      }

      if(enableWorldGenOreUranium) {
         Ic2Items.uraniumOre = new ItemStack((new BlockTex(getBlockIdFor(config, "blockOreUran", 247), 34, Material.rock)).setHardness(4.0F).setResistance(6.0F).setBlockName("blockOreUran").setCreativeTab(tabIC2));
         GameRegistry.registerBlock(Block.blocksList[Ic2Items.uraniumOre.itemID], "uraniumOre");
      }

      if(enableWorldGenTreeRubber) {
         (new BlockRubWood(getBlockIdFor(config, "blockRubWood", 243))).setCreativeTab(tabIC2);
         (new BlockRubLeaves(getBlockIdFor(config, "blockRubLeaves", 242))).setCreativeTab(tabIC2);
         new BlockRubSapling(getBlockIdFor(config, "blockRubSapling", 241), 38);
      }

      new BlockResin(getBlockIdFor(config, "blockHarz", 240), 43);
      (new BlockRubberSheet(getBlockIdFor(config, "blockRubber", 234), 40)).setCreativeTab(tabIC2);
      new BlockPoleFence(getBlockIdFor(config, "blockFenceIron", 232), 1);
      Ic2Items.reinforcedStone = new ItemStack((new BlockTex(getBlockIdFor(config, "blockAlloy", 231), 12, Material.iron)).setHardness(80.0F).setResistance(150.0F).setStepSound(Block.soundMetalFootstep).setBlockName("blockAlloy").setCreativeTab(tabIC2));
      GameRegistry.registerBlock(Block.blocksList[Ic2Items.reinforcedStone.itemID], "reinforcedStone");
      Ic2Items.reinforcedGlass = new ItemStack((new BlockTexGlass(getBlockIdFor(config, "blockAlloyGlass", 230), 13, Material.glass, false)).setHardness(5.0F).setResistance(150.0F).setStepSound(Block.soundGlassFootstep).setBlockName("blockAlloyGlass").setCreativeTab(tabIC2));
      GameRegistry.registerBlock(Block.blocksList[Ic2Items.reinforcedGlass.itemID], "reinforcedGlass");
      Ic2Items.reinforcedDoorBlock = new ItemStack((new BlockIC2Door(getBlockIdFor(config, "blockDoorAlloy", 229), 14, 15, Material.iron)).setHardness(50.0F).setResistance(150.0F).setStepSound(Block.soundMetalFootstep).setBlockName("blockDoorAlloy"));
      GameRegistry.registerBlock(Block.blocksList[Ic2Items.reinforcedDoorBlock.itemID], "reinforcedDoorBlock");
      new BlockFoam(getBlockIdFor(config, "blockFoam", 222), 37);
      new BlockWall(getBlockIdFor(config, "blockWall", 221), 96);
      new BlockScaffold(getBlockIdFor(config, "blockScaffold", 220), Material.wood);
      new BlockScaffold(getBlockIdFor(config, "blockIronScaffold", 216), Material.iron);
      new BlockMetal(getBlockIdFor(config, "blockMetal", 224));
      new BlockCable(getBlockIdFor(config, "blockCable", 228));
      new BlockGenerator(getBlockIdFor(config, "blockGenerator", 246));
      new BlockReactorChamber(getBlockIdFor(config, "blockReactorChamber", 233));
      new BlockElectric(getBlockIdFor(config, "blockElectric", 227));
      new BlockMachine(getBlockIdFor(config, "blockMachine", 250));
      new BlockMachine2(getBlockIdFor(config, "blockMachine2", 223));
      Ic2Items.luminator = new ItemStack((new BlockLuminator(getBlockIdFor(config, "blockLuminatorDark", 219), false)).setBlockName("blockLuminatorD"));
      GameRegistry.registerBlock(Block.blocksList[Ic2Items.luminator.itemID], ItemBlockRare.class, "luminator");
      Ic2Items.activeLuminator = new ItemStack((new BlockLuminator(getBlockIdFor(config, "blockLuminator", 226), true)).setBlockName("blockLuminator").setLightValue(1.0F));
      GameRegistry.registerBlock(Block.blocksList[Ic2Items.activeLuminator.itemID], ItemBlockRare.class, "activeLuminator");
      new BlockMiningPipe(getBlockIdFor(config, "blockMiningPipe", 245), 35);
      new BlockMiningTip(getBlockIdFor(config, "blockMiningTip", 244), 36);
      new BlockPersonal(getBlockIdFor(config, "blockPersonal", 225));
      Ic2Items.industrialTnt = new ItemStack((new BlockITNT(getBlockIdFor(config, "blockITNT", 239), 58, true)).setHardness(0.0F).setStepSound(Block.soundGrassFootstep).setBlockName("blockITNT"));
      GameRegistry.registerBlock(Block.blocksList[Ic2Items.industrialTnt.itemID], ItemBlockRare.class, "industrialTnt");
      Ic2Items.nuke = new ItemStack((new BlockITNT(getBlockIdFor(config, "blockNuke", 237), 61, false)).setHardness(0.0F).setStepSound(Block.soundGrassFootstep).setBlockName("blockNuke"));
      GameRegistry.registerBlock(Block.blocksList[Ic2Items.nuke.itemID], ItemBlockRare.class, "nuke");
      Ic2Items.dynamiteStick = new ItemStack((new BlockDynamite(getBlockIdFor(config, "blockDynamite", 236), 57)).setHardness(0.0F).setStepSound(Block.soundGrassFootstep).setBlockName("blockDynamite"));
      GameRegistry.registerBlock(Block.blocksList[Ic2Items.dynamiteStick.itemID], ItemBlockRare.class, "dynamiteStick");
      Ic2Items.dynamiteStickWithRemote = new ItemStack((new BlockDynamite(getBlockIdFor(config, "blockDynamiteRemote", 235), 56)).setHardness(0.0F).setStepSound(Block.soundGrassFootstep).setBlockName("blockDynamiteRemote"));
      GameRegistry.registerBlock(Block.blocksList[Ic2Items.dynamiteStickWithRemote.itemID], ItemBlockRare.class, "dynamiteStickWithRemote");
      new BlockCrop(getBlockIdFor(config, "blockCrop", 218));
      new BlockBarrel(getBlockIdFor(config, "blockBarrel", 217));
      Ic2Items.resin = new ItemStack((new ItemResin(getItemIdFor(config, "itemHarz", 29961), 64)).setItemName("itemHarz").setCreativeTab(tabIC2));
      Ic2Items.rubber = new ItemStack((new ItemIC2(getItemIdFor(config, "itemRubber", 29960), 65)).setItemName("itemRubber").setCreativeTab(tabIC2));
      Ic2Items.uraniumDrop = new ItemStack((new ItemIC2(getItemIdFor(config, "itemOreUran", 29987), 13)).setItemName("itemOreUran").setCreativeTab(tabIC2));
      Ic2Items.bronzeDust = new ItemStack((new ItemIC2(getItemIdFor(config, "itemDustBronze", 29995), 5)).setItemName("itemDustBronze").setCreativeTab(tabIC2));
      Ic2Items.clayDust = new ItemStack((new ItemIC2(getItemIdFor(config, "itemDustClay", 29877), 14)).setItemName("itemDustClay").setCreativeTab(tabIC2));
      Ic2Items.coalDust = new ItemStack((new ItemIC2(getItemIdFor(config, "itemDustCoal", 30000), 0)).setItemName("itemDustCoal").setCreativeTab(tabIC2));
      Ic2Items.copperDust = new ItemStack((new ItemIC2(getItemIdFor(config, "itemDustCopper", 29997), 3)).setItemName("itemDustCopper").setCreativeTab(tabIC2));
      Ic2Items.goldDust = new ItemStack((new ItemIC2(getItemIdFor(config, "itemDustGold", 29998), 2)).setItemName("itemDustGold").setCreativeTab(tabIC2));
      Ic2Items.ironDust = new ItemStack((new ItemIC2(getItemIdFor(config, "itemDustIron", 29999), 1)).setItemName("itemDustIron").setCreativeTab(tabIC2));
      Ic2Items.silverDust = new ItemStack((new ItemIC2(getItemIdFor(config, "itemDustSilver", 29874), 240)).setItemName("itemDustSilver").setCreativeTab(tabIC2));
      Ic2Items.smallIronDust = new ItemStack((new ItemIC2(getItemIdFor(config, "itemDustIronSmall", 29994), 6)).setItemName("itemDustIronSmall").setCreativeTab(tabIC2));
      Ic2Items.tinDust = new ItemStack((new ItemIC2(getItemIdFor(config, "itemDustTin", 29996), 4)).setItemName("itemDustTin").setCreativeTab(tabIC2));
      Ic2Items.hydratedCoalDust = new ItemStack((new ItemIC2(getItemIdFor(config, "itemFuelCoalDust", 29970), 53)).setItemName("itemFuelCoalDust").setCreativeTab(tabIC2));
      Ic2Items.refinedIronIngot = new ItemStack((new ItemIC2(getItemIdFor(config, "itemIngotAdvIron", 29993), 7)).setItemName("itemIngotAdvIron").setCreativeTab(tabIC2));
      Ic2Items.copperIngot = new ItemStack((new ItemIC2(getItemIdFor(config, "itemIngotCopper", 29992), 8)).setItemName("itemIngotCopper").setCreativeTab(tabIC2));
      Ic2Items.tinIngot = new ItemStack((new ItemIC2(getItemIdFor(config, "itemIngotTin", 29991), 9)).setItemName("itemIngotTin").setCreativeTab(tabIC2));
      Ic2Items.bronzeIngot = new ItemStack((new ItemIC2(getItemIdFor(config, "itemIngotBronze", 29990), 10)).setItemName("itemIngotBronze").setCreativeTab(tabIC2));
      Ic2Items.mixedMetalIngot = new ItemStack((new ItemIC2(getItemIdFor(config, "itemIngotAlloy", 29989), 11)).setItemName("itemIngotAlloy").setCreativeTab(tabIC2));
      Ic2Items.uraniumIngot = new ItemStack((new ItemIC2(getItemIdFor(config, "itemIngotUran", 29988), 12)).setItemName("itemIngotUran").setCreativeTab(tabIC2));
      Ic2Items.electronicCircuit = new ItemStack((new ItemIC2(getItemIdFor(config, "itemPartCircuit", 29935), 96)).setItemName("itemPartCircuit").setCreativeTab(tabIC2));
      Ic2Items.advancedCircuit = new ItemStack((new ItemIC2(getItemIdFor(config, "itemPartCircuitAdv", 29934), 97)).setRarity(1).setItemName("itemPartCircuitAdv").setCreativeTab(tabIC2));
      Ic2Items.advancedAlloy = new ItemStack((new ItemIC2(getItemIdFor(config, "itemPartAlloy", 29931), 100)).setItemName("itemPartAlloy").setCreativeTab(tabIC2));
      Ic2Items.carbonFiber = new ItemStack((new ItemIC2(getItemIdFor(config, "itemPartCarbonFibre", 29896), 74)).setItemName("itemPartCarbonFibre").setCreativeTab(tabIC2));
      Ic2Items.carbonMesh = new ItemStack((new ItemIC2(getItemIdFor(config, "itemPartCarbonMesh", 29895), 75)).setItemName("itemPartCarbonMesh").setCreativeTab(tabIC2));
      Ic2Items.carbonPlate = new ItemStack((new ItemIC2(getItemIdFor(config, "itemPartCarbonPlate", 29894), 76)).setItemName("itemPartCarbonPlate").setCreativeTab(tabIC2));
      Ic2Items.matter = new ItemStack((new ItemIC2(getItemIdFor(config, "itemMatter", 29932), 99)).setRarity(2).setItemName("itemMatter").setCreativeTab(tabIC2));
      Ic2Items.iridiumOre = new ItemStack((new ItemIC2(getItemIdFor(config, "itemOreIridium", 29872), 151)).setRarity(2).setItemName("itemOreIridium").setCreativeTab(tabIC2));
      Ic2Items.iridiumPlate = new ItemStack((new ItemIC2(getItemIdFor(config, "itemPartIridium", 29891), 93)).setRarity(2).setItemName("itemPartIridium").setCreativeTab(tabIC2));
      Ic2Items.denseCopperPlate = new ItemStack((new ItemIC2(getItemIdFor(config, "itemPartDCP", 29828), 226)).setItemName("itemPartDCP").setCreativeTab(tabIC2));
      Ic2Items.treetap = new ItemStack((new ItemTreetap(getItemIdFor(config, "itemTreetap", 29956), 66)).setItemName("itemTreetap").setCreativeTab(tabIC2));
      Ic2Items.bronzePickaxe = new ItemStack((new ItemIC2Pickaxe(getItemIdFor(config, "itemToolBronzePickaxe", 29944), 80, EnumToolMaterial.IRON, 5.0F, Ic2Items.bronzeIngot)).setItemName("itemToolBronzePickaxe").setCreativeTab(tabIC2));
      Ic2Items.bronzeAxe = new ItemStack((new ItemIC2Axe(getItemIdFor(config, "itemToolBronzeAxe", 29943), 81, EnumToolMaterial.IRON, 5.0F, Ic2Items.bronzeIngot)).setItemName("itemToolBronzeAxe").setCreativeTab(tabIC2));
      Ic2Items.bronzeSword = new ItemStack((new ItemIC2Sword(getItemIdFor(config, "itemToolBronzeSword", 29942), 82, EnumToolMaterial.IRON, 7, Ic2Items.bronzeIngot)).setItemName("itemToolBronzeSword").setCreativeTab(tabIC2));
      Ic2Items.bronzeShovel = new ItemStack((new ItemIC2Spade(getItemIdFor(config, "itemToolBronzeSpade", 29941), 83, EnumToolMaterial.IRON, 5.0F, Ic2Items.bronzeIngot)).setItemName("itemToolBronzeSpade").setCreativeTab(tabIC2));
      Ic2Items.bronzeHoe = new ItemStack((new ItemIC2Hoe(getItemIdFor(config, "itemToolBronzeHoe", 29940), 84, EnumToolMaterial.IRON, Ic2Items.bronzeIngot)).setItemName("itemToolBronzeHoe").setCreativeTab(tabIC2));
      Ic2Items.wrench = new ItemStack((new ItemToolWrench(getItemIdFor(config, "itemToolWrench", 29927), 89)).setItemName("itemToolWrench").setCreativeTab(tabIC2));
      Ic2Items.cutter = new ItemStack((new ItemToolCutter(getItemIdFor(config, "itemToolCutter", 29897), 92)).setItemName("itemToolCutter").setCreativeTab(tabIC2));
      Ic2Items.constructionFoamSprayer = new ItemStack((new ItemSprayer(getItemIdFor(config, "itemFoamSprayer", 29875), 45)).setItemName("itemFoamSprayer").setCreativeTab(tabIC2));
      Ic2Items.toolbox = new ItemStack((new ItemToolbox(getItemIdFor(config, "itemToolbox", 29861), 162)).setItemName("itemToolbox").setCreativeTab(tabIC2));
      Ic2Items.miningDrill = new ItemStack((new ItemElectricToolDrill(getItemIdFor(config, "itemToolDrill", 29979), 48)).setItemName("itemToolDrill").setCreativeTab(tabIC2));
      Ic2Items.diamondDrill = new ItemStack((new ItemElectricToolDDrill(getItemIdFor(config, "itemToolDDrill", 29978), 49)).setItemName("itemToolDDrill").setCreativeTab(tabIC2));
      Ic2Items.chainsaw = new ItemStack((new ItemElectricToolChainsaw(getItemIdFor(config, "itemToolChainsaw", 29977), 50)).setItemName("itemToolChainsaw").setCreativeTab(tabIC2));
      Ic2Items.electricWrench = new ItemStack((new ItemToolWrenchElectric(getItemIdFor(config, "itemToolWrenchElectric", 29884), 94)).setItemName("itemToolWrenchElectric").setCreativeTab(tabIC2));
      Ic2Items.electricTreetap = new ItemStack((new ItemTreetapElectric(getItemIdFor(config, "itemTreetapElectric", 29868), 165)).setItemName("itemTreetapElectric").setCreativeTab(tabIC2));
      Ic2Items.miningLaser = new ItemStack((new ItemToolMiningLaser(getItemIdFor(config, "itemToolMiningLaser", 29952), 70)).setItemName("itemToolMiningLaser").setCreativeTab(tabIC2));
      Ic2Items.ecMeter = new ItemStack((new ItemToolMeter(getItemIdFor(config, "itemToolMEter", 29926), 90)).setItemName("itemToolMeter").setCreativeTab(tabIC2));
      Ic2Items.odScanner = new ItemStack((new ItemScanner(getItemIdFor(config, "itemScanner", 29964), 59, 1)).setItemName("itemScanner").setCreativeTab(tabIC2));
      Ic2Items.ovScanner = new ItemStack((new ItemScannerAdv(getItemIdFor(config, "itemScannerAdv", 29963), 60, 2)).setItemName("itemScannerAdv").setCreativeTab(tabIC2));
      Ic2Items.frequencyTransmitter = new ItemStack((new ItemFrequencyTransmitter(getItemIdFor(config, "itemFreq", 29878), 95)).setItemName("itemFreq").setMaxStackSize(1).setCreativeTab(tabIC2));
      Ic2Items.nanoSaber = new ItemStack((new ItemNanoSaber(getItemIdFor(config, "itemNanoSaberOff", 29892), 77, false)).setItemName("itemNanoSaber").setCreativeTab(tabIC2).setCreativeTab(tabIC2));
      Ic2Items.enabledNanoSaber = new ItemStack((new ItemNanoSaber(getItemIdFor(config, "itemNanoSaber", 29893), 78, true)).setItemName("itemNanoSaber").setCreativeTab(tabIC2));
      Ic2Items.hazmatHelmet = new ItemStack((new ItemArmorHazmat(getItemIdFor(config, "itemArmorHazmatHelmet", 29826), 228, platform.addArmor("ic2/hazmat"), 0)).setItemName("itemArmorHazmatHelmet"));
      Ic2Items.hazmatChestplate = new ItemStack((new ItemArmorHazmat(getItemIdFor(config, "itemArmorHazmatChestplate", 29825), 229, platform.addArmor("ic2/hazmat"), 1)).setItemName("itemArmorHazmatChestplate"));
      Ic2Items.hazmatLeggings = new ItemStack((new ItemArmorHazmat(getItemIdFor(config, "itemArmorHazmatLeggings", 29824), 230, platform.addArmor("ic2/hazmat"), 2)).setItemName("itemArmorHazmatLeggings"));
      Ic2Items.hazmatBoots = new ItemStack((new ItemArmorHazmat(getItemIdFor(config, "itemArmorRubBoots", 29955), 67, platform.addArmor("ic2/hazmat"), 3)).setItemName("itemArmorRubBoots"));
      Ic2Items.bronzeHelmet = new ItemStack((new ItemArmorIC2(getItemIdFor(config, "itemArmorBronzeHelmet", 29939), 85, bronzeArmorMaterial, platform.addArmor("ic2/bronze"), 0, Ic2Items.bronzeIngot)).setItemName("itemArmorBronzeHelmet"));
      Ic2Items.bronzeChestplate = new ItemStack((new ItemArmorIC2(getItemIdFor(config, "itemArmorBronzeChestplate", 29938), 86, bronzeArmorMaterial, platform.addArmor("ic2/bronze"), 1, Ic2Items.bronzeIngot)).setItemName("itemArmorBronzeChestplate"));
      Ic2Items.bronzeLeggings = new ItemStack((new ItemArmorIC2(getItemIdFor(config, "itemArmorBronzeLegs", 29937), 87, bronzeArmorMaterial, platform.addArmor("ic2/bronze"), 2, Ic2Items.bronzeIngot)).setItemName("itemArmorBronzeLegs"));
      Ic2Items.bronzeBoots = new ItemStack((new ItemArmorIC2(getItemIdFor(config, "itemArmorBronzeBoots", 29936), 88, bronzeArmorMaterial, platform.addArmor("ic2/bronze"), 3, Ic2Items.bronzeIngot)).setItemName("itemArmorBronzeBoots"));
      Ic2Items.compositeArmor = new ItemStack((new ItemArmorIC2(getItemIdFor(config, "itemArmorAlloyChestplate", 29923), 103, alloyArmorMaterial, platform.addArmor("ic2/alloy"), 1, Ic2Items.advancedAlloy)).setItemName("itemArmorAlloyChestplate"));
      Ic2Items.nanoHelmet = new ItemStack((new ItemArmorNanoSuit(getItemIdFor(config, "itemArmorNanoHelmet", 29922), 104, platform.addArmor("ic2/nano"), 0)).setItemName("itemArmorNanoHelmet"));
      Ic2Items.nanoBodyarmor = new ItemStack((new ItemArmorNanoSuit(getItemIdFor(config, "itemArmorNanoChestplate", 29921), 105, platform.addArmor("ic2/nano"), 1)).setItemName("itemArmorNanoChestplate"));
      Ic2Items.nanoLeggings = new ItemStack((new ItemArmorNanoSuit(getItemIdFor(config, "itemArmorNanoLegs", 29920), 106, platform.addArmor("ic2/nano"), 2)).setItemName("itemArmorNanoLegs"));
      Ic2Items.nanoBoots = new ItemStack((new ItemArmorNanoSuit(getItemIdFor(config, "itemArmorNanoBoots", 29919), 107, platform.addArmor("ic2/nano"), 3)).setItemName("itemArmorNanoBoots"));
      Ic2Items.quantumHelmet = new ItemStack((new ItemArmorQuantumSuit(getItemIdFor(config, "itemArmorQuantumHelmet", 29918), 108, platform.addArmor("ic2/quantum"), 0)).setItemName("itemArmorQuantumHelmet"));
      Ic2Items.quantumBodyarmor = new ItemStack((new ItemArmorQuantumSuit(getItemIdFor(config, "itemArmorQuantumChestplate", 29917), 109, platform.addArmor("ic2/quantum"), 1)).setItemName("itemArmorQuantumChestplate"));
      Ic2Items.quantumLeggings = new ItemStack((new ItemArmorQuantumSuit(getItemIdFor(config, "itemArmorQuantumLegs", 29916), 110, platform.addArmor("ic2/quantum"), 2)).setItemName("itemArmorQuantumLegs"));
      Ic2Items.quantumBoots = new ItemStack((new ItemArmorQuantumSuit(getItemIdFor(config, "itemArmorQuantumBoots", 29915), 111, platform.addArmor("ic2/quantum"), 3)).setItemName("itemArmorQuantumBoots"));
      Ic2Items.jetpack = new ItemStack((new ItemArmorJetpack(getItemIdFor(config, "itemArmorJetpack", 29954), 68, platform.addArmor("ic2/jetpack"))).setItemName("itemArmorJetpack").setCreativeTab(tabIC2));
      Ic2Items.electricJetpack = new ItemStack((new ItemArmorJetpackElectric(getItemIdFor(config, "itemArmorJetpackElectric", 29953), 69, platform.addArmor("ic2/jetpack"))).setItemName("itemArmorJetpackElectric").setCreativeTab(tabIC2));
      Ic2Items.batPack = new ItemStack((new ItemArmorBatpack(getItemIdFor(config, "itemArmorBatpack", 29924), 73, platform.addArmor("ic2/batpack"))).setItemName("itemArmorBatpack").setCreativeTab(tabIC2));
      Ic2Items.lapPack = new ItemStack((new ItemArmorLappack(getItemIdFor(config, "itemArmorLappack", 29871), 150, platform.addArmor("ic2/lappack"))).setItemName("itemArmorLappack").setCreativeTab(tabIC2));
      Ic2Items.cfPack = new ItemStack((new ItemArmorCFPack(getItemIdFor(config, "itemArmorCFPack", 29873), 46, platform.addArmor("ic2/batpack"))).setItemName("itemArmorCFPack").setCreativeTab(tabIC2));
      Ic2Items.solarHelmet = new ItemStack((new ItemArmorSolarHelmet(getItemIdFor(config, "itemSolarHelmet", 29860), 164, platform.addArmor("ic2/solar"))).setItemName("itemSolarHelmet").setCreativeTab(tabIC2));
      Ic2Items.staticBoots = new ItemStack((new ItemArmorStaticBoots(getItemIdFor(config, "itemStaticBoots", 29859), 67, platform.addArmor("ic2/rubber"))).setItemName("itemStaticBoots").setCreativeTab(tabIC2));
      Ic2Items.nightvisionGoggles = new ItemStack((new ItemArmorNightvisionGoggles(getItemIdFor(config, "itemNightvisionGoggles", 29822), 232, platform.addArmor("ic2/nightvision"))).setItemName("itemNightvisionGoggles").setCreativeTab(tabIC2));
      Ic2Items.reBattery = new ItemStack((new ItemBatteryDischarged(getItemIdFor(config, "itemBatREDischarged", 29983), 16, 10000, 100, 1)).setItemName("itemBatRE").setCreativeTab(tabIC2));
      Ic2Items.chargedReBattery = new ItemStack((new ItemBattery(getItemIdFor(config, "itemBatRE", 29986), 16, 10000, 100, 1)).setItemName("itemBatRE").setCreativeTab(tabIC2));
      Ic2Items.energyCrystal = new ItemStack((new ItemBattery(getItemIdFor(config, "itemBatCrystal", 29985), 21, 100000, 250, 2)).setItemName("itemBatCrystal").setCreativeTab(tabIC2));
      Ic2Items.lapotronCrystal = new ItemStack((new ItemBattery(getItemIdFor(config, "itemBatLamaCrystal", 29984), 26, 1000000, 600, 3)).setRarity(1).setItemName("itemBatLamaCrystal").setCreativeTab(tabIC2));
      Ic2Items.suBattery = new ItemStack((new ItemBatterySU(getItemIdFor(config, "itemBatSU", 29982), 31, 1000, 1)).setItemName("itemBatSU").setCreativeTab(tabIC2));
      new ItemCable(getItemIdFor(config, "itemCable", 29928), 112);
      Ic2Items.cell = new ItemStack((new ItemCell(getItemIdFor(config, "itemCellEmpty", 29981), 32)).setItemName("itemCellEmpty").setCreativeTab(tabIC2));
      Ic2Items.lavaCell = new ItemStack((new ItemIC2(getItemIdFor(config, "itemCellLava", 29980), 33)).setItemName("itemCellLava").setCreativeTab(tabIC2));
      Ic2Items.hydratedCoalCell = new ItemStack((new ItemIC2(getItemIdFor(config, "itemCellCoal", 29974), 34)).setItemName("itemCellCoal").setCreativeTab(tabIC2));
      Ic2Items.bioCell = new ItemStack((new ItemIC2(getItemIdFor(config, "itemCellBio", 29973), 35)).setItemName("itemCellBio").setCreativeTab(tabIC2));
      Ic2Items.coalfuelCell = new ItemStack((new ItemIC2(getItemIdFor(config, "itemCellCoalRef", 29972), 34)).setItemName("itemCellCoalRef").setCreativeTab(tabIC2));
      Ic2Items.biofuelCell = new ItemStack((new ItemIC2(getItemIdFor(config, "itemCellBioRef", 29971), 35)).setItemName("itemCellBioRef").setCreativeTab(tabIC2));
      Ic2Items.waterCell = new ItemStack((new ItemIC2(getItemIdFor(config, "itemCellWater", 29962), 37)).setItemName("itemCellWater").setCreativeTab(tabIC2));
      Ic2Items.electrolyzedWaterCell = new ItemStack((new ItemIC2(getItemIdFor(config, "itemCellWaterElectro", 29925), 43)).setItemName("itemCellWaterElectro").setCreativeTab(tabIC2));
      Ic2Items.fuelCan = new ItemStack((new ItemFuelCanEmpty(getItemIdFor(config, "itemFuelCanEmpty", 29975), 51)).setItemName("itemFuelCanEmpty").setCreativeTab(tabIC2));
      Ic2Items.filledFuelCan = new ItemStack((new ItemFuelCanFilled(getItemIdFor(config, "itemFuelCan", 29976), 52)).setItemName("itemFuelCan").setMaxStackSize(1).setContainerItem(Ic2Items.fuelCan.getItem()));
      Ic2Items.tinCan = new ItemStack((new ItemIC2(getItemIdFor(config, "itemTinCan", 29966), 57)).setItemName("itemTinCan").setCreativeTab(tabIC2));
      Ic2Items.filledTinCan = new ItemStack((new ItemTinCan(getItemIdFor(config, "itemTinCanFilled", 29965), 58)).setItemName("itemTinCanFilled").setCreativeTab(tabIC2));
      Ic2Items.airCell = new ItemStack((new ItemIC2(getItemIdFor(config, "itemCellAir", 29823), 231)).setItemName("itemCellAir").setCreativeTab(tabIC2));
      Ic2Items.reactorUraniumSimple = new ItemStack((new ItemReactorUranium(getItemIdFor(config, "reactorUraniumSimple", 29951), 38, 1)).setItemName("reactorUraniumSimple").setCreativeTab(tabIC2));
      Ic2Items.reactorUraniumDual = new ItemStack((new ItemReactorUranium(getItemIdFor(config, "reactorUraniumDual", 29846), 208, 2)).setItemName("reactorUraniumDual").setCreativeTab(tabIC2));
      Ic2Items.reactorUraniumQuad = new ItemStack((new ItemReactorUranium(getItemIdFor(config, "reactorUraniumQuad", 29845), 209, 4)).setItemName("reactorUraniumQuad").setCreativeTab(tabIC2));
      Ic2Items.reactorCoolantSimple = new ItemStack((new ItemReactorHeatStorage(getItemIdFor(config, "reactorCoolantSimple", 29950), 39, 10000)).setItemName("reactorCoolantSimple").setCreativeTab(tabIC2));
      Ic2Items.reactorCoolantTriple = new ItemStack((new ItemReactorHeatStorage(getItemIdFor(config, "reactorCoolantTriple", 29844), 210, 30000)).setItemName("reactorCoolantTriple").setCreativeTab(tabIC2));
      Ic2Items.reactorCoolantSix = new ItemStack((new ItemReactorHeatStorage(getItemIdFor(config, "reactorCoolantSix", 29843), 211, '\uea60')).setItemName("reactorCoolantSix").setCreativeTab(tabIC2));
      Ic2Items.reactorPlating = new ItemStack((new ItemReactorPlating(getItemIdFor(config, "reactorPlating", 29949), 71, 1000, 0.95F)).setItemName("reactorPlating").setCreativeTab(tabIC2));
      Ic2Items.reactorPlatingHeat = new ItemStack((new ItemReactorPlating(getItemIdFor(config, "reactorPlatingHeat", 29842), 212, 2000, 0.99F)).setItemName("reactorPlatingHeat").setCreativeTab(tabIC2));
      Ic2Items.reactorPlatingExplosive = new ItemStack((new ItemReactorPlating(getItemIdFor(config, "reactorPlatingExplosive", 29841), 213, 500, 0.9F)).setItemName("reactorPlatingExplosive").setCreativeTab(tabIC2));
      Ic2Items.reactorHeatSwitch = new ItemStack((new ItemReactorHeatSwitch(getItemIdFor(config, "reactorHeatSwitch", 29948), 214, 2500, 12, 4)).setItemName("reactorHeatSwitch").setCreativeTab(tabIC2));
      Ic2Items.reactorHeatSwitchCore = new ItemStack((new ItemReactorHeatSwitch(getItemIdFor(config, "reactorHeatSwitchCore", 29840), 215, 5000, 0, 72)).setItemName("reactorHeatSwitchCore").setCreativeTab(tabIC2));
      Ic2Items.reactorHeatSwitchSpread = new ItemStack((new ItemReactorHeatSwitch(getItemIdFor(config, "reactorHeatSwitchSpread", 29839), 216, 5000, 36, 0)).setItemName("reactorHeatSwitchSpread").setCreativeTab(tabIC2));
      Ic2Items.reactorHeatSwitchDiamond = new ItemStack((new ItemReactorHeatSwitch(getItemIdFor(config, "reactorHeatSwitchDiamond", 29838), 217, 10000, 24, 8)).setItemName("reactorHeatSwitchDiamond").setCreativeTab(tabIC2));
      Ic2Items.reactorVent = new ItemStack((new ItemReactorVent(getItemIdFor(config, "reactorVent", 29837), 218, 1000, 6, 0)).setItemName("reactorVent").setCreativeTab(tabIC2));
      Ic2Items.reactorVentCore = new ItemStack((new ItemReactorVent(getItemIdFor(config, "reactorVentCore", 29836), 219, 1000, 5, 5)).setItemName("reactorVentCore").setCreativeTab(tabIC2));
      Ic2Items.reactorVentGold = new ItemStack((new ItemReactorVent(getItemIdFor(config, "reactorVentGold", 29835), 220, 1000, 20, 36)).setItemName("reactorVentGold").setCreativeTab(tabIC2));
      Ic2Items.reactorVentSpread = new ItemStack((new ItemReactorVentSpread(getItemIdFor(config, "reactorVentSpread", 29834), 221, 4)).setItemName("reactorVentSpread").setCreativeTab(tabIC2));
      Ic2Items.reactorVentDiamond = new ItemStack((new ItemReactorVent(getItemIdFor(config, "reactorVentDiamond", 29833), 72, 1000, 12, 0)).setItemName("reactorVentDiamond").setCreativeTab(tabIC2));
      Ic2Items.reactorIsotopeCell = new ItemStack((new ItemReactorDepletedUranium(getItemIdFor(config, "reactorIsotopeCell", 29947), 40)).setItemName("reactorIsotopeCell").setCreativeTab(tabIC2));
      Ic2Items.reEnrichedUraniumCell = new ItemStack((new ItemIC2(getItemIdFor(config, "itemCellUranEnriched", 29946), 41)).setItemName("itemCellUranEnriched").setCreativeTab(tabIC2));
      Ic2Items.nearDepletedUraniumCell = new ItemStack((new ItemIC2(getItemIdFor(config, "itemCellUranEmpty", 29945), 42)).setItemName("itemCellUranEmpty").setCreativeTab(tabIC2));
      Ic2Items.reactorHeatpack = new ItemStack((new ItemReactorHeatpack(getItemIdFor(config, "reactorHeatpack", 29832), 222, 1000, 1)).setItemName("reactorHeatpack").setCreativeTab(tabIC2));
      Ic2Items.reactorReflector = new ItemStack((new ItemReactorReflector(getItemIdFor(config, "reactorReflector", 29831), 223, 10000)).setItemName("reactorReflector").setCreativeTab(tabIC2));
      Ic2Items.reactorReflectorThick = new ItemStack((new ItemReactorReflector(getItemIdFor(config, "reactorReflectorThick", 29830), 224, '\u9c40')).setItemName("reactorReflectorThick").setCreativeTab(tabIC2));
      Ic2Items.reactorCondensator = new ItemStack((new ItemReactorCondensator(getItemIdFor(config, "reactorCondensator", 29829), 225, 20000)).setItemName("reactorCondensator").setCreativeTab(tabIC2));
      Ic2Items.reactorCondensatorLap = new ItemStack((new ItemReactorCondensator(getItemIdFor(config, "reactorCondensatorLap", 29827), 227, 100000)).setItemName("reactorCondensatorLap").setCreativeTab(tabIC2));
      Ic2Items.terraformerBlueprint = new ItemStack((new ItemIC2(getItemIdFor(config, "itemTFBP", 29890), 144)).setItemName("itemTFBP").setCreativeTab(tabIC2));
      Ic2Items.cultivationTerraformerBlueprint = new ItemStack((new ItemTFBPCultivation(getItemIdFor(config, "itemTFBPCultivation", 29889), 145)).setItemName("itemTFBPCultivation").setCreativeTab(tabIC2));
      Ic2Items.irrigationTerraformerBlueprint = new ItemStack((new ItemTFBPIrrigation(getItemIdFor(config, "itemTFBPIrrigation", 29888), 146)).setItemName("itemTFBPIrrigation").setCreativeTab(tabIC2));
      Ic2Items.chillingTerraformerBlueprint = new ItemStack((new ItemTFBPChilling(getItemIdFor(config, "itemTFBPChilling", 29887), 147)).setItemName("itemTFBPChilling").setCreativeTab(tabIC2));
      Ic2Items.desertificationTerraformerBlueprint = new ItemStack((new ItemTFBPDesertification(getItemIdFor(config, "itemTFBPDesertification", 29886), 148)).setItemName("itemTFBPDesertification").setCreativeTab(tabIC2));
      Ic2Items.flatificatorTerraformerBlueprint = new ItemStack((new ItemTFBPFlatification(getItemIdFor(config, "itemTFBPFlatification", 29885), 149)).setItemName("itemTFBPFlatification").setCreativeTab(tabIC2));
      Ic2Items.mushroomTerraformerBlueprint = new ItemStack((new ItemTFBPMushroom(getItemIdFor(config, "itemTFBPMushroom", 29862), 161)).setItemName("itemTFBPMushroom").setCreativeTab(tabIC2));
      Ic2Items.coalBall = new ItemStack((new ItemIC2(getItemIdFor(config, "itemPartCoalBall", 29882), 158)).setItemName("itemPartCoalBall").setCreativeTab(tabIC2));
      Ic2Items.compressedCoalBall = new ItemStack((new ItemIC2(getItemIdFor(config, "itemPartCoalBlock", 29881), 157)).setItemName("itemPartCoalBlock").setCreativeTab(tabIC2));
      Ic2Items.coalChunk = new ItemStack((new ItemIC2(getItemIdFor(config, "itemPartCoalChunk", 29880), 156)).setItemName("itemPartCoalChunk").setCreativeTab(tabIC2));
      Ic2Items.industrialDiamond = new ItemStack((new ItemIC2(getItemIdFor(config, "itemPartIndustrialDiamond", 29879), 155)).setItemName("itemPartIndustrialDiamond"));
      Ic2Items.scrap = new ItemStack((new ItemIC2(getItemIdFor(config, "itemScrap", 29933), 98)).setItemName("itemScrap").setCreativeTab(tabIC2));
      Ic2Items.scrapBox = new ItemStack((new ItemScrapbox(getItemIdFor(config, "itemScrapbox", 29883), 159)).setItemName("itemScrapbox").setCreativeTab(tabIC2));
      Ic2Items.hydratedCoalClump = new ItemStack((new ItemIC2(getItemIdFor(config, "itemFuelCoalCmpr", 29969), 54)).setItemName("itemFuelCoalCmpr").setCreativeTab(tabIC2));
      Ic2Items.plantBall = new ItemStack((new ItemIC2(getItemIdFor(config, "itemFuelPlantBall", 29968), 55)).setItemName("itemFuelPlantBall").setCreativeTab(tabIC2));
      Ic2Items.compressedPlantBall = new ItemStack((new ItemIC2(getItemIdFor(config, "itemFuelPlantCmpr", 29967), 56)).setItemName("itemFuelPlantCmpr").setCreativeTab(tabIC2));
      Ic2Items.painter = new ItemStack((new ItemIC2(getItemIdFor(config, "itemToolPainter", 29914), 91)).setItemName("itemToolPainter").setCreativeTab(tabIC2));
      Ic2Items.blackPainter = new ItemStack((new ItemToolPainter(getItemIdFor(config, "itemToolPainterBlack", 29913), 0)).setItemName("itemToolPainter").setCreativeTab(tabIC2));
      Ic2Items.redPainter = new ItemStack((new ItemToolPainter(getItemIdFor(config, "itemToolPainterRed", 29912), 1)).setItemName("itemToolPainter").setCreativeTab(tabIC2));
      Ic2Items.greenPainter = new ItemStack((new ItemToolPainter(getItemIdFor(config, "itemToolPainterGreen", 29911), 2)).setItemName("itemToolPainter").setCreativeTab(tabIC2));
      Ic2Items.brownPainter = new ItemStack((new ItemToolPainter(getItemIdFor(config, "itemToolPainterBrown", 29910), 3)).setItemName("itemToolPainter").setCreativeTab(tabIC2));
      Ic2Items.bluePainter = new ItemStack((new ItemToolPainter(getItemIdFor(config, "itemToolPainterBlue", 29909), 4)).setItemName("itemToolPainter").setCreativeTab(tabIC2));
      Ic2Items.purplePainter = new ItemStack((new ItemToolPainter(getItemIdFor(config, "itemToolPainterPurple", 29908), 5)).setItemName("itemToolPainter").setCreativeTab(tabIC2));
      Ic2Items.cyanPainter = new ItemStack((new ItemToolPainter(getItemIdFor(config, "itemToolPainterCyan", 29907), 6)).setItemName("itemToolPainter").setCreativeTab(tabIC2));
      Ic2Items.lightGreyPainter = new ItemStack((new ItemToolPainter(getItemIdFor(config, "itemToolPainterLightGrey", 29906), 7)).setItemName("itemToolPainter").setCreativeTab(tabIC2));
      Ic2Items.darkGreyPainter = new ItemStack((new ItemToolPainter(getItemIdFor(config, "itemToolPainterDarkGrey", 29905), 8)).setItemName("itemToolPainter").setCreativeTab(tabIC2));
      Ic2Items.pinkPainter = new ItemStack((new ItemToolPainter(getItemIdFor(config, "itemToolPainterPink", 29904), 9)).setItemName("itemToolPainter").setCreativeTab(tabIC2));
      Ic2Items.limePainter = new ItemStack((new ItemToolPainter(getItemIdFor(config, "itemToolPainterLime", 29903), 10)).setItemName("itemToolPainter").setCreativeTab(tabIC2));
      Ic2Items.yellowPainter = new ItemStack((new ItemToolPainter(getItemIdFor(config, "itemToolPainterYellow", 29902), 11)).setItemName("itemToolPainter").setCreativeTab(tabIC2));
      Ic2Items.cloudPainter = new ItemStack((new ItemToolPainter(getItemIdFor(config, "itemToolPainterCloud", 29901), 12)).setItemName("itemToolPainter").setCreativeTab(tabIC2));
      Ic2Items.magentaPainter = new ItemStack((new ItemToolPainter(getItemIdFor(config, "itemToolPainterMagenta", 29900), 13)).setItemName("itemToolPainter").setCreativeTab(tabIC2));
      Ic2Items.orangePainter = new ItemStack((new ItemToolPainter(getItemIdFor(config, "itemToolPainterOrange", 29899), 14)).setItemName("itemToolPainter").setCreativeTab(tabIC2));
      Ic2Items.whitePainter = new ItemStack((new ItemToolPainter(getItemIdFor(config, "itemToolPainterWhite", 29898), 15)).setItemName("itemToolPainter").setCreativeTab(tabIC2));
      Ic2Items.dynamite = new ItemStack((new ItemDynamite(getItemIdFor(config, "itemDynamite", 29959), 62, false)).setItemName("itemDynamite").setCreativeTab(tabIC2));
      Ic2Items.stickyDynamite = new ItemStack((new ItemDynamite(getItemIdFor(config, "itemDynamiteSticky", 29958), 63, true)).setItemName("itemDynamiteSticky").setCreativeTab(tabIC2));
      Ic2Items.remote = new ItemStack((new ItemRemote(getItemIdFor(config, "itemRemote", 29957), 61)).setItemName("itemRemote").setCreativeTab(tabIC2));
      new ItemUpgradeModule(getItemIdFor(config, "upgradeModule", 29869));
      Ic2Items.coin = new ItemStack((new ItemIC2(getItemIdFor(config, "itemCoin", 29930), 101)).setItemName("itemCoin"));
      Ic2Items.reinforcedDoor = new ItemStack((new ItemIC2Door(getItemIdFor(config, "itemDoorAlloy", 29929), 102, Block.blocksList[Ic2Items.reinforcedDoorBlock.itemID])).setItemName("itemDoorAlloy"));
      Ic2Items.constructionFoamPellet = new ItemStack((new ItemIC2(getItemIdFor(config, "itemPartPellet", 29876), 44)).setItemName("itemPartPellet").setCreativeTab(tabIC2));
      Ic2Items.grinPowder = new ItemStack((new ItemIC2(getItemIdFor(config, "itemGrinPowder", 29850), 198)).setItemName("itemGrinPowder").setCreativeTab(tabIC2));
      Ic2Items.debug = new ItemStack(new ItemDebug(getItemIdFor(config, "itemDebug", 29848)));
      Ic2Items.coolant = new ItemStack((new ItemIC2(getItemIdFor(config, "itemCoolant", 29847), 202)).setItemName("itemCoolant"));
      Ic2Items.cropSeed = new ItemStack((new ItemCropSeed(getItemIdFor(config, "itemCropSeed", 29870), 152)).setItemName("itemCropSeed"));
      Ic2Items.cropnalyzer = new ItemStack((new ItemCropnalyzer(getItemIdFor(config, "itemCropnalyzer", 29866), 153)).setItemName("itemCropnalyzer").setCreativeTab(tabIC2));
      Ic2Items.fertilizer = new ItemStack((new ItemFertilizer(getItemIdFor(config, "itemFertilizer", 29865), 160)).setItemName("itemFertilizer").setCreativeTab(tabIC2));
      Ic2Items.hydratingCell = new ItemStack((new ItemGradual(getItemIdFor(config, "itemCellHydrant", 29864), 39)).setItemName("itemCellHydrant").setCreativeTab(tabIC2));
      Ic2Items.electricHoe = new ItemStack((new ItemElectricToolHoe(getItemIdFor(config, "itemToolHoe", 29863), 154)).setItemName("itemToolHoe").setCreativeTab(tabIC2));
      Ic2Items.terraWart = new ItemStack((new ItemTerraWart(getItemIdFor(config, "itemTerraWart", 29858), 166)).setItemName("itemTerraWart"));
      Ic2Items.weedEx = new ItemStack((new ItemIC2(getItemIdFor(config, "itemWeedEx", 29849), 199)).setItemName("itemWeedEx").setMaxStackSize(1).setMaxDamage(64).setCreativeTab(tabIC2));
      Ic2Items.mugEmpty = new ItemStack((new ItemMug(getItemIdFor(config, "itemMugEmpty", 29855), 169)).setItemName("itemMugEmpty").setCreativeTab(tabIC2));
      Ic2Items.coffeeBeans = new ItemStack((new ItemIC2(getItemIdFor(config, "itemCofeeBeans", 29857), 167)).setItemName("itemCoffeeBeans").setCreativeTab(tabIC2));
      Ic2Items.coffeePowder = new ItemStack((new ItemIC2(getItemIdFor(config, "itemCofeePowder", 29856), 168)).setItemName("itemCoffeePowder").setCreativeTab(tabIC2));
      Ic2Items.mugCoffee = new ItemStack((new ItemMugCoffee(getItemIdFor(config, "itemMugCoffee", 29854), 170)).setCreativeTab(tabIC2));
      Ic2Items.hops = new ItemStack((new ItemIC2(getItemIdFor(config, "itemHops", 29853), 174)).setItemName("itemHops").setCreativeTab(tabIC2));
      Ic2Items.barrel = new ItemStack((new ItemBarrel(getItemIdFor(config, "itemBarrel", 29852), 173)).setItemName("itemBarrel").setCreativeTab(tabIC2));
      Ic2Items.mugBooze = new ItemStack(new ItemBooze(getItemIdFor(config, "itemMugBooze", 29851), 192));
      Block.obsidian.setResistance(60.0F);
      Block.enchantmentTable.setResistance(60.0F);
      Block.enderChest.setResistance(60.0F);
      Block.anvil.setResistance(60.0F);
      Block.waterMoving.setResistance(30.0F);
      Block.waterStill.setResistance(30.0F);
      Block.lavaStill.setResistance(30.0F);
      ((BlockIC2Door)Block.blocksList[Ic2Items.reinforcedDoorBlock.itemID]).setItemDropped(Ic2Items.reinforcedDoor.itemID);
      ExplosionWhitelist.addWhitelistedBlock(Block.bedrock);
      Ic2Recipes.addMatterAmplifier(Ic2Items.scrap, 5000);
      Ic2Recipes.addMatterAmplifier(Ic2Items.scrapBox, '\uafc8');
      Crops.addBiomeBonus(BiomeGenBase.river, 2, 0);
      Crops.addBiomeBonus(BiomeGenBase.swampland, 2, 2);
      Crops.addBiomeBonus(BiomeGenBase.forest, 1, 1);
      Crops.addBiomeBonus(BiomeGenBase.forestHills, 1, 1);
      Crops.addBiomeBonus(BiomeGenBase.jungle, 1, 2);
      Crops.addBiomeBonus(BiomeGenBase.jungleHills, 1, 2);
      Crops.addBiomeBonus(BiomeGenBase.desert, -1, 0);
      Crops.addBiomeBonus(BiomeGenBase.desertHills, -1, 0);
      Crops.addBiomeBonus(BiomeGenBase.mushroomIsland, 0, 2);
      Crops.addBiomeBonus(BiomeGenBase.mushroomIslandShore, 0, 2);
      FurnaceRecipes furnaceRecipes = FurnaceRecipes.smelting();
      if(Ic2Items.rubberWood != null) {
         furnaceRecipes.addSmelting(Ic2Items.rubberWood.itemID, Ic2Items.rubberWood.getItemDamage(), new ItemStack(Block.wood, 1, 3), 0.1F);
      }

      if(Ic2Items.tinOre != null) {
         furnaceRecipes.addSmelting(Ic2Items.tinOre.itemID, Ic2Items.tinOre.getItemDamage(), Ic2Items.tinIngot, 0.5F);
      }

      if(Ic2Items.copperOre != null) {
         furnaceRecipes.addSmelting(Ic2Items.copperOre.itemID, Ic2Items.copperOre.getItemDamage(), Ic2Items.copperIngot, 0.5F);
      }

      furnaceRecipes.addSmelting(Item.ingotIron.itemID, Ic2Items.refinedIronIngot, 0.2F);
      furnaceRecipes.addSmelting(Ic2Items.ironDust.itemID, Ic2Items.ironDust.getItemDamage(), new ItemStack(Item.ingotIron, 1), 0.0F);
      furnaceRecipes.addSmelting(Ic2Items.goldDust.itemID, Ic2Items.goldDust.getItemDamage(), new ItemStack(Item.ingotGold, 1), 0.0F);
      furnaceRecipes.addSmelting(Ic2Items.tinDust.itemID, Ic2Items.tinDust.getItemDamage(), Ic2Items.tinIngot.copy(), 0.0F);
      furnaceRecipes.addSmelting(Ic2Items.copperDust.itemID, Ic2Items.copperDust.getItemDamage(), Ic2Items.copperIngot.copy(), 0.0F);
      furnaceRecipes.addSmelting(Ic2Items.hydratedCoalDust.itemID, Ic2Items.hydratedCoalDust.getItemDamage(), Ic2Items.coalDust.copy(), 0.0F);
      furnaceRecipes.addSmelting(Ic2Items.bronzeDust.itemID, Ic2Items.bronzeDust.getItemDamage(), Ic2Items.bronzeIngot.copy(), 0.0F);
      furnaceRecipes.addSmelting(Ic2Items.resin.itemID, Ic2Items.resin.getItemDamage(), Ic2Items.rubber.copy(), 0.3F);
      furnaceRecipes.addSmelting(Ic2Items.mugCoffee.itemID, new ItemStack(Ic2Items.mugCoffee.getItem(), 1, 1), 0.1F);
      ((ItemElectricToolChainsaw)Ic2Items.chainsaw.getItem()).init();
      ((ItemElectricToolDrill)Ic2Items.miningDrill.getItem()).init();
      ((ItemElectricToolDDrill)Ic2Items.diamondDrill.getItem()).init();
      ((ItemNanoSaber)Ic2Items.nanoSaber.getItem()).init();
      ItemScrapbox.init();
      ItemTFBPCultivation.init();
      ItemTFBPFlatification.init();
      TileEntityCompressor.init();
      TileEntityExtractor.init();
      TileEntityMacerator.init();
      TileEntityRecycler.init(config);
      MinecraftForge.setToolClass(Ic2Items.bronzePickaxe.getItem(), "pickaxe", 2);
      MinecraftForge.setToolClass(Ic2Items.bronzeAxe.getItem(), "axe", 2);
      MinecraftForge.setToolClass(Ic2Items.bronzeShovel.getItem(), "shovel", 2);
      MinecraftForge.setToolClass(Ic2Items.chainsaw.getItem(), "axe", 2);
      MinecraftForge.setToolClass(Ic2Items.miningDrill.getItem(), "pickaxe", 2);
      MinecraftForge.setToolClass(Ic2Items.diamondDrill.getItem(), "pickaxe", 3);
      MinecraftForge.setBlockHarvestLevel(Block.blocksList[Ic2Items.reinforcedStone.itemID], "pickaxe", 2);
      MinecraftForge.setBlockHarvestLevel(Block.blocksList[Ic2Items.reinforcedDoorBlock.itemID], "pickaxe", 2);
      MinecraftForge.setBlockHarvestLevel(Block.blocksList[Ic2Items.insulatedCopperCableBlock.itemID], "axe", 0);
      MinecraftForge.setBlockHarvestLevel(Block.blocksList[Ic2Items.constructionFoamWall.itemID], "pickaxe", 1);
      if(Ic2Items.copperOre != null) {
         MinecraftForge.setBlockHarvestLevel(Block.blocksList[Ic2Items.copperOre.itemID], "pickaxe", 1);
      }

      if(Ic2Items.tinOre != null) {
         MinecraftForge.setBlockHarvestLevel(Block.blocksList[Ic2Items.tinOre.itemID], "pickaxe", 1);
      }

      if(Ic2Items.uraniumOre != null) {
         MinecraftForge.setBlockHarvestLevel(Block.blocksList[Ic2Items.uraniumOre.itemID], "pickaxe", 2);
      }

      if(Ic2Items.rubberWood != null) {
         MinecraftForge.setBlockHarvestLevel(Block.blocksList[Ic2Items.rubberWood.itemID], "axe", 0);
      }

      windStrength = 10 + random.nextInt(10);
      windTicker = 0;
      Block.setBurnProperties(Ic2Items.scaffold.itemID, 8, 20);
      if(Ic2Items.rubberLeaves != null) {
         Block.setBurnProperties(Ic2Items.rubberLeaves.itemID, 30, 20);
      }

      if(Ic2Items.rubberWood != null) {
         Block.setBurnProperties(Ic2Items.rubberWood.itemID, 4, 20);
      }

      MinecraftForge.EVENT_BUS.register(this);
      registerCraftingRecipes();
      String[] d = OreDictionary.getOreNames();
      int coolantLiquid = d.length;

      for(int i$ = 0; i$ < coolantLiquid; ++i$) {
         String oreName = d[i$];
         Iterator i$1 = OreDictionary.getOres(oreName).iterator();

         while(i$1.hasNext()) {
            ItemStack ore = (ItemStack)i$1.next();
            this.registerOre(new OreRegisterEvent(oreName, ore));
         }
      }

      assert Ic2Items.uraniumDrop != null;

      assert Ic2Items.bronzeIngot != null;

      assert Ic2Items.copperIngot != null;

      assert Ic2Items.refinedIronIngot != null;

      assert Ic2Items.tinIngot != null;

      assert Ic2Items.uraniumIngot != null;

      assert Ic2Items.rubber != null;

      if(Ic2Items.copperOre != null) {
         OreDictionary.registerOre("oreCopper", Ic2Items.copperOre);
      }

      if(Ic2Items.tinOre != null) {
         OreDictionary.registerOre("oreTin", Ic2Items.tinOre);
      }

      if(Ic2Items.uraniumOre != null) {
         OreDictionary.registerOre("oreUranium", Ic2Items.uraniumOre);
      }

      OreDictionary.registerOre("dropUranium", Ic2Items.uraniumDrop);
      OreDictionary.registerOre("dustBronze", Ic2Items.bronzeDust);
      OreDictionary.registerOre("dustClay", Ic2Items.clayDust);
      OreDictionary.registerOre("dustCoal", Ic2Items.coalDust);
      OreDictionary.registerOre("dustCopper", Ic2Items.copperDust);
      OreDictionary.registerOre("dustGold", Ic2Items.goldDust);
      OreDictionary.registerOre("dustIron", Ic2Items.ironDust);
      OreDictionary.registerOre("dustSilver", Ic2Items.silverDust);
      OreDictionary.registerOre("dustTin", Ic2Items.tinDust);
      OreDictionary.registerOre("ingotBronze", Ic2Items.bronzeIngot);
      OreDictionary.registerOre("ingotCopper", Ic2Items.copperIngot);
      OreDictionary.registerOre("ingotRefinedIron", Ic2Items.refinedIronIngot);
      OreDictionary.registerOre("ingotTin", Ic2Items.tinIngot);
      OreDictionary.registerOre("ingotUranium", Ic2Items.uraniumIngot);
      OreDictionary.registerOre("itemRubber", Ic2Items.rubber);
      if(Ic2Items.rubberWood != null) {
         OreDictionary.registerOre("woodRubber", Ic2Items.rubberWood);
      }

      EnergyNet.initialize();
      IC2Crops.init();
      IC2DamageSource.addLocalization();
      IC2Potion.init();
      new IC2Loot();
      achievements = new IC2Achievements();
      enableDynamicIdAllocation = false;
      dynamicIdAllocationProp.value = "false";
      if(config != null) {
         config.save();
      }

      EntityRegistry.registerModEntity(EntityMiningLaser.class, "MiningLaser", 0, this, 160, 5, true);
      EntityRegistry.registerModEntity(EntityDynamite.class, "Dynamite", 1, this, 160, 5, true);
      EntityRegistry.registerModEntity(EntityStickyDynamite.class, "StickyDynamite", 2, this, 160, 5, true);
      EntityRegistry.registerModEntity(EntityItnt.class, "Itnt", 3, this, 160, 5, true);
      EntityRegistry.registerModEntity(EntityNuke.class, "Nuke", 4, this, 160, 5, true);
      int var17 = Integer.parseInt((new SimpleDateFormat("Mdd")).format(new Date()));
      suddenlyHoes = (double)var17 > Math.cbrt(6.4E7D) && (double)var17 < Math.cbrt(6.5939264E7D);
      seasonal = (double)var17 > Math.cbrt(1.089547389E9D) && (double)var17 < Math.cbrt(1.338273208E9D);
      TickRegistry.registerTickHandler(this, Side.SERVER);
      GameRegistry.registerWorldGenerator(this);
      GameRegistry.registerFuelHandler(this);
      NetworkRegistry.instance().registerConnectionHandler(this);
      GameRegistry.registerPlayerTracker(this);
      LiquidContainerRegistry.registerLiquid(new LiquidContainerData(new LiquidStack(Block.waterStill.blockID, 1000), Ic2Items.waterCell.copy(), Ic2Items.cell.copy()));
      LiquidContainerRegistry.registerLiquid(new LiquidContainerData(new LiquidStack(Block.lavaStill.blockID, 1000), Ic2Items.lavaCell.copy(), Ic2Items.cell.copy()));
      LiquidContainerRegistry.registerLiquid(new LiquidContainerData(new LiquidStack(Ic2Items.coolant.itemID, 6000), Ic2Items.reactorCoolantSix.copy(), Ic2Items.cell.copy()));
      LiquidContainerRegistry.registerLiquid(new LiquidContainerData(new LiquidStack(Ic2Items.coolant.itemID, 3000), Ic2Items.reactorCoolantTriple.copy(), Ic2Items.cell.copy()));
      LiquidStack var18 = new LiquidStack(Ic2Items.coolant.itemID, 1000);
      LiquidContainerRegistry.registerLiquid(new LiquidContainerData(var18, Ic2Items.reactorCoolantSimple.copy(), Ic2Items.cell.copy()));
      LiquidDictionary.getOrCreateLiquid("coolant", var18);
      initialized = true;
   }

   @PostInit
   public void modsLoaded(FMLPostInitializationEvent event) {
      if(!initialized) {
         platform.displayError("IndustrialCraft 2 has failed to initialize properly.");
      }

      if(loadSubModule("bcIntegration32x")) {
         log.info("BuildCraft 3.2 integration module loaded");
      }

      if(platform.isRendering()) {
         ;
      }

      String minorMods = "";

      try {
         Class e = Class.forName("portalgun.common.PortalGun");
         Method addBlockIDToGrabListMeta = e.getMethod("addBlockIDToGrabList", new Class[]{Integer.TYPE, int[].class});
         Method addBlockIDToGrabList = e.getMethod("addBlockIDToGrabList", new Class[]{Integer.TYPE});
         if(Ic2Items.rubberWood != null) {
            addBlockIDToGrabListMeta.invoke((Object)null, new Object[]{Integer.valueOf(Ic2Items.rubberWood.itemID), new int[]{2, 3, 4, 5, 6, 7, 8, 9, 10, 11}});
         }

         addBlockIDToGrabList.invoke((Object)null, new Object[]{Integer.valueOf(Ic2Items.miningPipe.itemID)});
         addBlockIDToGrabList.invoke((Object)null, new Object[]{Integer.valueOf(Ic2Items.miningPipeTip.itemID)});
         addBlockIDToGrabList.invoke((Object)null, new Object[]{Integer.valueOf(Ic2Items.batBox.itemID)});
         addBlockIDToGrabList.invoke((Object)null, new Object[]{Integer.valueOf(Ic2Items.machine.itemID)});
         addBlockIDToGrabList.invoke((Object)null, new Object[]{Integer.valueOf(Ic2Items.teleporter.itemID)});
         addBlockIDToGrabList.invoke((Object)null, new Object[]{Integer.valueOf(Ic2Items.luminator.itemID)});
         addBlockIDToGrabList.invoke((Object)null, new Object[]{Integer.valueOf(Ic2Items.activeLuminator.itemID)});
         addBlockIDToGrabList.invoke((Object)null, new Object[]{Integer.valueOf(Ic2Items.scaffold.itemID)});
         addBlockIDToGrabList.invoke((Object)null, new Object[]{Integer.valueOf(Ic2Items.rubberTrampoline.itemID)});
         minorMods = minorMods + ", Portal Gun";
      } catch (Throwable var8) {
         ;
      }

      try {
         Method e1 = Class.forName("mod_Gibbing").getMethod("addCustomItem", new Class[]{Integer.TYPE, Double.TYPE});
         e1.invoke((Object)null, new Object[]{Integer.valueOf(Ic2Items.nanoSaber.itemID), Double.valueOf(0.5D)});
         e1.invoke((Object)null, new Object[]{Integer.valueOf(Ic2Items.chainsaw.itemID), Double.valueOf(0.5D)});
         e1.invoke((Object)null, new Object[]{Integer.valueOf(Ic2Items.miningDrill.itemID), Double.valueOf(0.333D)});
         e1.invoke((Object)null, new Object[]{Integer.valueOf(Ic2Items.diamondDrill.itemID), Double.valueOf(0.333D)});
         minorMods = minorMods + ", Mob Amputation";
      } catch (Throwable var7) {
         ;
      }

      try {
         Field e2 = Class.forName("mod_Timber").getDeclaredField("axes");
         e2.set((Object)null, e2.get((Object)null) + ", " + Ic2Items.bronzeAxe.itemID + ", " + Ic2Items.chainsaw.itemID);
         minorMods = minorMods + ", Timber";
      } catch (Throwable var6) {
         ;
      }

      log.info("Loaded minor compatibility modules: " + (minorMods.isEmpty()?"none":minorMods.substring(2)));
      GameRegistry.registerTileEntity(TileEntityBlock.class, "Empty Management TileEntity");
      GameRegistry.registerTileEntity(TileEntityIronFurnace.class, "Iron Furnace");
      GameRegistry.registerTileEntity(TileEntityElecFurnace.class, "Electric Furnace");
      GameRegistry.registerTileEntity(TileEntityMacerator.class, "Macerator");
      GameRegistry.registerTileEntity(TileEntityExtractor.class, "Extractor");
      GameRegistry.registerTileEntity(TileEntityCompressor.class, "Compressor");
      GameRegistry.registerTileEntity(TileEntityGenerator.class, "Generator");
      GameRegistry.registerTileEntity(TileEntityGeoGenerator.class, "Geothermal Generator");
      GameRegistry.registerTileEntity(TileEntityWaterGenerator.class, "Water Mill");
      GameRegistry.registerTileEntity(TileEntitySolarGenerator.class, "Solar Panel");
      GameRegistry.registerTileEntity(TileEntityWindGenerator.class, "Wind Mill");
      GameRegistry.registerTileEntity(TileEntityCanner.class, "Canning Machine");
      GameRegistry.registerTileEntity(TileEntityMiner.class, "Miner");
      GameRegistry.registerTileEntity(TileEntityPump.class, "Pump");
      if(BlockGenerator.tileEntityNuclearReactorClass == TileEntityNuclearReactorElectric.class) {
         GameRegistry.registerTileEntity(TileEntityNuclearReactorElectric.class, "Nuclear Reactor");
      }

      if(BlockReactorChamber.tileEntityReactorChamberClass == TileEntityReactorChamberElectric.class) {
         GameRegistry.registerTileEntity(TileEntityReactorChamberElectric.class, "Reactor Chamber");
      }

      GameRegistry.registerTileEntity(TileEntityMagnetizer.class, "Magnetizer");
      GameRegistry.registerTileEntity(TileEntityCable.class, "Cable");
      GameRegistry.registerTileEntity(TileEntityElectricBatBox.class, "BatBox");
      GameRegistry.registerTileEntity(TileEntityElectricMFE.class, "MFE");
      GameRegistry.registerTileEntity(TileEntityElectricMFSU.class, "MFSU");
      GameRegistry.registerTileEntity(TileEntityTransformerLV.class, "LV-Transformer");
      GameRegistry.registerTileEntity(TileEntityTransformerMV.class, "MV-Transformer");
      GameRegistry.registerTileEntity(TileEntityTransformerHV.class, "HV-Transformer");
      GameRegistry.registerTileEntity(TileEntityLuminator.class, "Luminator");
      GameRegistry.registerTileEntity(TileEntityElectrolyzer.class, "Electrolyzer");
      if(BlockPersonal.tileEntityPersonalChestClass == TileEntityPersonalChest.class) {
         GameRegistry.registerTileEntity(TileEntityPersonalChest.class, "Personal Safe");
      }

      GameRegistry.registerTileEntity(TileEntityTradeOMat.class, "Trade-O-Mat");
      GameRegistry.registerTileEntity(TileEntityEnergyOMat.class, "Energy-O-Mat");
      GameRegistry.registerTileEntity(TileEntityRecycler.class, "Recycler");
      GameRegistry.registerTileEntity(TileEntityInduction.class, "Induction Furnace");
      GameRegistry.registerTileEntity(TileEntityMatter.class, "Mass Fabricator");
      GameRegistry.registerTileEntity(TileEntityTerra.class, "Terraformer");
      GameRegistry.registerTileEntity(TileEntityTeleporter.class, "Teleporter");
      GameRegistry.registerTileEntity(TileEntityTesla.class, "Tesla Coil");
      GameRegistry.registerTileEntity(TileEntityCableDetector.class, "Detector Cable");
      GameRegistry.registerTileEntity(TileEntityCableSplitter.class, "SplitterCable");
      GameRegistry.registerTileEntity(TileEntityCrop.class, "TECrop");
      GameRegistry.registerTileEntity(TileEntityBarrel.class, "TEBarrel");
      GameRegistry.registerTileEntity(TileEntityCropmatron.class, "Crop-Matron");
   }

   private static boolean loadSubModule(String name) {
      log.info("Loading IC2 submodule: " + name);

      try {
         Class t = IC2.class.getClassLoader().loadClass("ic2." + name + ".SubModule");
         return ((Boolean)t.getMethod("init", new Class[0]).invoke((Object)null, new Object[0])).booleanValue();
      } catch (Throwable var2) {
         log.info("Submodule " + name + " not loaded");
         return false;
      }
   }

   public int getBurnTime(ItemStack stack) {
      return Ic2Items.rubberSapling != null && stack.equals(Ic2Items.rubberSapling)?80:(stack.itemID == Item.reed.itemID?50:(stack.itemID == Block.cactus.blockID?50:(stack.itemID == Ic2Items.scrap.itemID?350:(stack.itemID == Ic2Items.scrapBox.itemID?3150:(stack.itemID == Ic2Items.lavaCell.itemID?TileEntityFurnace.getItemBurnTime(new ItemStack(Item.bucketLava)):0)))));
   }

   private static void registerCraftingRecipes() {
      Ic2Recipes.addCraftingRecipe(Ic2Items.copperBlock, new Object[]{"MMM", "MMM", "MMM", Character.valueOf('M'), "ingotCopper"});
      Ic2Recipes.addCraftingRecipe(Ic2Items.bronzeBlock, new Object[]{"MMM", "MMM", "MMM", Character.valueOf('M'), "ingotBronze"});
      Ic2Recipes.addCraftingRecipe(Ic2Items.tinBlock, new Object[]{"MMM", "MMM", "MMM", Character.valueOf('M'), "ingotTin"});
      Ic2Recipes.addCraftingRecipe(Ic2Items.uraniumBlock, new Object[]{"MMM", "MMM", "MMM", Character.valueOf('M'), "ingotUranium"});
      Ic2Recipes.addCraftingRecipe(Ic2Items.ironFurnace, new Object[]{"III", "I I", "III", Character.valueOf('I'), Item.ingotIron});
      Ic2Recipes.addCraftingRecipe(Ic2Items.ironFurnace, new Object[]{" I ", "I I", "IFI", Character.valueOf('I'), Item.ingotIron, Character.valueOf('F'), Block.stoneOvenIdle});
      Ic2Recipes.addCraftingRecipe(Ic2Items.electroFurnace, new Object[]{" C ", "RFR", Character.valueOf('C'), Ic2Items.electronicCircuit, Character.valueOf('R'), Item.redstone, Character.valueOf('F'), Ic2Items.ironFurnace});
      Ic2Recipes.addCraftingRecipe(Ic2Items.macerator, new Object[]{"FFF", "SMS", " C ", Character.valueOf('F'), Item.flint, Character.valueOf('S'), Block.cobblestone, Character.valueOf('M'), Ic2Items.machine, Character.valueOf('C'), Ic2Items.electronicCircuit});
      Ic2Recipes.addCraftingRecipe(Ic2Items.extractor, new Object[]{"TMT", "TCT", Character.valueOf('T'), Ic2Items.treetap, Character.valueOf('M'), Ic2Items.machine, Character.valueOf('C'), Ic2Items.electronicCircuit});
      Ic2Recipes.addCraftingRecipe(Ic2Items.compressor, new Object[]{"S S", "SMS", "SCS", Character.valueOf('S'), Block.stone, Character.valueOf('M'), Ic2Items.machine, Character.valueOf('C'), Ic2Items.electronicCircuit});
      Ic2Recipes.addCraftingRecipe(Ic2Items.miner, new Object[]{"CMC", " P ", " P ", Character.valueOf('P'), Ic2Items.miningPipe, Character.valueOf('M'), Ic2Items.machine, Character.valueOf('C'), Ic2Items.electronicCircuit});
      Ic2Recipes.addCraftingRecipe(Ic2Items.pump, new Object[]{"cCc", "cMc", "PTP", Character.valueOf('c'), Ic2Items.cell, Character.valueOf('T'), Ic2Items.treetap, Character.valueOf('P'), Ic2Items.miningPipe, Character.valueOf('M'), Ic2Items.machine, Character.valueOf('C'), Ic2Items.electronicCircuit});
      Ic2Recipes.addCraftingRecipe(Ic2Items.magnetizer, new Object[]{"RFR", "RMR", "RFR", Character.valueOf('R'), Item.redstone, Character.valueOf('F'), Ic2Items.ironFence, Character.valueOf('M'), Ic2Items.machine});
      Ic2Recipes.addCraftingRecipe(Ic2Items.electrolyzer, new Object[]{"c c", "cCc", "EME", Character.valueOf('E'), Ic2Items.cell, Character.valueOf('c'), Ic2Items.insulatedCopperCableItem, Character.valueOf('M'), Ic2Items.machine, Character.valueOf('C'), Ic2Items.electronicCircuit});
      Ic2Recipes.addCraftingRecipe(Ic2Items.advancedMachine, new Object[]{" A ", "CMC", " A ", Character.valueOf('A'), Ic2Items.advancedAlloy, Character.valueOf('C'), Ic2Items.carbonPlate, Character.valueOf('M'), Ic2Items.machine});
      Ic2Recipes.addCraftingRecipe(Ic2Items.advancedMachine, new Object[]{" C ", "AMA", " C ", Character.valueOf('A'), Ic2Items.advancedAlloy, Character.valueOf('C'), Ic2Items.carbonPlate, Character.valueOf('M'), Ic2Items.machine});
      Ic2Recipes.addCraftingRecipe(Ic2Items.personalSafe, new Object[]{"c", "M", "C", Character.valueOf('c'), Ic2Items.electronicCircuit, Character.valueOf('C'), Block.chest, Character.valueOf('M'), Ic2Items.machine});
      Ic2Recipes.addCraftingRecipe(Ic2Items.tradeOMat, new Object[]{"RRR", "CMC", Character.valueOf('R'), Item.redstone, Character.valueOf('C'), Block.chest, Character.valueOf('M'), Ic2Items.machine});
      Ic2Recipes.addCraftingRecipe(Ic2Items.energyOMat, new Object[]{"RBR", "CMC", Character.valueOf('R'), Item.redstone, Character.valueOf('C'), Ic2Items.insulatedCopperCableItem, Character.valueOf('M'), Ic2Items.machine, Character.valueOf('B'), Ic2Items.reBattery});
      Ic2Recipes.addCraftingRecipe(Ic2Items.massFabricator, new Object[]{"GCG", "ALA", "GCG", Character.valueOf('A'), Ic2Items.advancedMachine, Character.valueOf('L'), Ic2Items.lapotronCrystal, Character.valueOf('G'), Item.lightStoneDust, Character.valueOf('C'), Ic2Items.advancedCircuit});
      Ic2Recipes.addCraftingRecipe(Ic2Items.terraformer, new Object[]{"GTG", "DMD", "GDG", Character.valueOf('T'), Ic2Items.terraformerBlueprint, Character.valueOf('G'), Item.lightStoneDust, Character.valueOf('D'), Block.dirt, Character.valueOf('M'), Ic2Items.advancedMachine});
      Ic2Recipes.addCraftingRecipe(Ic2Items.teleporter, new Object[]{"GFG", "CMC", "GDG", Character.valueOf('M'), Ic2Items.advancedMachine, Character.valueOf('C'), Ic2Items.glassFiberCableItem, Character.valueOf('F'), Ic2Items.frequencyTransmitter, Character.valueOf('G'), Ic2Items.advancedCircuit, Character.valueOf('D'), Ic2Items.industrialDiamond});
      Ic2Recipes.addCraftingRecipe(Ic2Items.teleporter, new Object[]{"GFG", "CMC", "GDG", Character.valueOf('M'), Ic2Items.advancedMachine, Character.valueOf('C'), Ic2Items.glassFiberCableItem, Character.valueOf('F'), Ic2Items.frequencyTransmitter, Character.valueOf('G'), Ic2Items.advancedCircuit, Character.valueOf('D'), Item.diamond});
      Ic2Recipes.addCraftingRecipe(Ic2Items.inductionFurnace, new Object[]{"CCC", "CFC", "CMC", Character.valueOf('C'), "ingotCopper", Character.valueOf('F'), Ic2Items.electroFurnace, Character.valueOf('M'), Ic2Items.advancedMachine});
      Ic2Recipes.addCraftingRecipe(Ic2Items.machine, new Object[]{"III", "I I", "III", Character.valueOf('I'), "ingotRefinedIron"});
      Ic2Recipes.addCraftingRecipe(Ic2Items.recycler, new Object[]{" G ", "DMD", "IDI", Character.valueOf('D'), Block.dirt, Character.valueOf('G'), Item.lightStoneDust, Character.valueOf('M'), Ic2Items.compressor, Character.valueOf('I'), "ingotRefinedIron"});
      Ic2Recipes.addCraftingRecipe(Ic2Items.canner, new Object[]{"TCT", "TMT", "TTT", Character.valueOf('T'), "ingotTin", Character.valueOf('M'), Ic2Items.machine, Character.valueOf('C'), Ic2Items.electronicCircuit});
      Ic2Recipes.addCraftingRecipe(Ic2Items.teslaCoil, new Object[]{"RRR", "RMR", "ICI", Character.valueOf('M'), Ic2Items.mvTransformer, Character.valueOf('R'), Item.redstone, Character.valueOf('C'), Ic2Items.electronicCircuit, Character.valueOf('I'), "ingotRefinedIron"});
      Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.luminator, 8), new Object[]{"ICI", "GTG", "GGG", Character.valueOf('G'), Block.glass, Character.valueOf('I'), "ingotRefinedIron", Character.valueOf('T'), Ic2Items.tinCableItem, Character.valueOf('C'), Ic2Items.insulatedCopperCableItem});
      Ic2Recipes.addCraftingRecipe(Ic2Items.generator, new Object[]{" B ", "III", " F ", Character.valueOf('B'), Ic2Items.reBattery, Character.valueOf('F'), Ic2Items.ironFurnace, Character.valueOf('I'), "ingotRefinedIron"});
      Ic2Recipes.addCraftingRecipe(Ic2Items.generator, new Object[]{" B ", "III", " F ", Character.valueOf('B'), Ic2Items.chargedReBattery, Character.valueOf('F'), Ic2Items.ironFurnace, Character.valueOf('I'), "ingotRefinedIron"});
      Ic2Recipes.addCraftingRecipe(Ic2Items.generator, new Object[]{"B", "M", "F", Character.valueOf('B'), Ic2Items.reBattery, Character.valueOf('F'), Block.stoneOvenIdle, Character.valueOf('M'), Ic2Items.machine});
      Ic2Recipes.addCraftingRecipe(Ic2Items.generator, new Object[]{"B", "M", "F", Character.valueOf('B'), Ic2Items.chargedReBattery, Character.valueOf('F'), Block.stoneOvenIdle, Character.valueOf('M'), Ic2Items.machine});
      Ic2Recipes.addCraftingRecipe(Ic2Items.reactorChamber, new Object[]{" C ", "CMC", " C ", Character.valueOf('C'), Ic2Items.denseCopperPlate, Character.valueOf('M'), Ic2Items.machine});
      if(energyGeneratorWater > 0) {
         Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.waterMill, 2), new Object[]{"SPS", "PGP", "SPS", Character.valueOf('S'), "stickWood", Character.valueOf('P'), "plankWood", Character.valueOf('G'), Ic2Items.generator});
      }

      if(energyGeneratorSolar > 0) {
         Ic2Recipes.addCraftingRecipe(Ic2Items.solarPanel, new Object[]{"CgC", "gCg", "cGc", Character.valueOf('G'), Ic2Items.generator, Character.valueOf('C'), "dustCoal", Character.valueOf('g'), Block.glass, Character.valueOf('c'), Ic2Items.electronicCircuit});
      }

      if(energyGeneratorWind > 0) {
         Ic2Recipes.addCraftingRecipe(Ic2Items.windMill, new Object[]{"I I", " G ", "I I", Character.valueOf('I'), Item.ingotIron, Character.valueOf('G'), Ic2Items.generator});
      }

      if(energyGeneratorNuclear > 0) {
         Ic2Recipes.addCraftingRecipe(Ic2Items.nuclearReactor, new Object[]{" c ", "CCC", " G ", Character.valueOf('C'), Ic2Items.reactorChamber, Character.valueOf('c'), Ic2Items.advancedCircuit, Character.valueOf('G'), Ic2Items.generator});
      }

      if(energyGeneratorGeo > 0) {
         Ic2Recipes.addCraftingRecipe(Ic2Items.geothermalGenerator, new Object[]{"gCg", "gCg", "IGI", Character.valueOf('G'), Ic2Items.generator, Character.valueOf('C'), Ic2Items.cell, Character.valueOf('g'), Block.glass, Character.valueOf('I'), "ingotRefinedIron"});
      }

      Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.reactorUraniumSimple, new Object[]{Ic2Items.reEnrichedUraniumCell, "dustCoal"});
      Ic2Recipes.addShapelessCraftingRecipe(new ItemStack(Ic2Items.reactorIsotopeCell.itemID, 1, 9999), new Object[]{Ic2Items.nearDepletedUraniumCell, "dustCoal"});
      Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.nearDepletedUraniumCell, 8), new Object[]{"CCC", "CUC", "CCC", Character.valueOf('C'), Ic2Items.cell, Character.valueOf('U'), "ingotUranium"});
      Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.reactorUraniumSimple, new Object[]{Ic2Items.cell, "ingotUranium"});
      Ic2Recipes.addCraftingRecipe(Ic2Items.reactorUraniumDual, new Object[]{"UCU", Character.valueOf('U'), Ic2Items.reactorUraniumSimple, Character.valueOf('C'), Ic2Items.denseCopperPlate});
      Ic2Recipes.addCraftingRecipe(Ic2Items.reactorUraniumQuad, new Object[]{" U ", "CCC", " U ", Character.valueOf('U'), Ic2Items.reactorUraniumDual, Character.valueOf('C'), Ic2Items.denseCopperPlate});
      Ic2Recipes.addCraftingRecipe(Ic2Items.reactorCoolantSimple, new Object[]{" T ", "TWT", " T ", Character.valueOf('W'), "liquid$" + Block.waterStill.blockID, Character.valueOf('T'), "ingotTin"});
      Ic2Recipes.addCraftingRecipe(Ic2Items.reactorCoolantTriple, new Object[]{"TTT", "CCC", "TTT", Character.valueOf('C'), Ic2Items.reactorCoolantSimple, Character.valueOf('T'), "ingotTin"});
      Ic2Recipes.addCraftingRecipe(Ic2Items.reactorCoolantSix, new Object[]{"TCT", "TcT", "TCT", Character.valueOf('C'), Ic2Items.reactorCoolantTriple, Character.valueOf('T'), "ingotTin", Character.valueOf('c'), Ic2Items.denseCopperPlate});
      Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.reactorPlating, new Object[]{Ic2Items.denseCopperPlate, Ic2Items.advancedAlloy});
      Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.reactorPlatingHeat, new Object[]{Ic2Items.reactorPlating, Ic2Items.denseCopperPlate, Ic2Items.denseCopperPlate});
      Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.reactorPlatingExplosive, new Object[]{Ic2Items.reactorPlating, Ic2Items.advancedAlloy, Ic2Items.advancedAlloy});
      Ic2Recipes.addCraftingRecipe(Ic2Items.reactorHeatSwitch, new Object[]{" c ", "TCT", " T ", Character.valueOf('c'), Ic2Items.electronicCircuit, Character.valueOf('T'), "ingotTin", Character.valueOf('C'), Ic2Items.denseCopperPlate});
      Ic2Recipes.addCraftingRecipe(Ic2Items.reactorHeatSwitchCore, new Object[]{"C", "S", "C", Character.valueOf('S'), Ic2Items.reactorHeatSwitch, Character.valueOf('C'), Ic2Items.denseCopperPlate});
      Ic2Recipes.addCraftingRecipe(Ic2Items.reactorHeatSwitchSpread, new Object[]{" G ", "GSG", " G ", Character.valueOf('S'), Ic2Items.reactorHeatSwitch, Character.valueOf('G'), Item.ingotGold});
      Ic2Recipes.addCraftingRecipe(Ic2Items.reactorHeatSwitchDiamond, new Object[]{"GcG", "SCS", "GcG", Character.valueOf('S'), Ic2Items.reactorHeatSwitch, Character.valueOf('C'), Ic2Items.denseCopperPlate, Character.valueOf('G'), Ic2Items.glassFiberCableItem, Character.valueOf('c'), Ic2Items.electronicCircuit});
      Ic2Recipes.addCraftingRecipe(Ic2Items.reactorVent, new Object[]{"I#I", "# #", "I#I", Character.valueOf('I'), Ic2Items.refinedIronIngot, Character.valueOf('#'), Block.fenceIron});
      Ic2Recipes.addCraftingRecipe(Ic2Items.reactorVentCore, new Object[]{"C", "V", "C", Character.valueOf('V'), Ic2Items.reactorVent, Character.valueOf('C'), Ic2Items.denseCopperPlate});
      Ic2Recipes.addCraftingRecipe(Ic2Items.reactorVentGold, new Object[]{"G", "V", "G", Character.valueOf('V'), Ic2Items.reactorVentCore, Character.valueOf('G'), Item.ingotGold});
      Ic2Recipes.addCraftingRecipe(Ic2Items.reactorVentSpread, new Object[]{"#T#", "TVT", "#T#", Character.valueOf('V'), Ic2Items.reactorVent, Character.valueOf('#'), Block.fenceIron, Character.valueOf('T'), "ingotTin"});
      Ic2Recipes.addCraftingRecipe(Ic2Items.reactorVentDiamond, new Object[]{"#V#", "#D#", "#V#", Character.valueOf('V'), Ic2Items.reactorVent, Character.valueOf('#'), Block.fenceIron, Character.valueOf('D'), Item.diamond});
      Ic2Recipes.addCraftingRecipe(Ic2Items.reactorHeatpack, new Object[]{"c", "L", "C", Character.valueOf('c'), Ic2Items.electronicCircuit, Character.valueOf('C'), Ic2Items.denseCopperPlate, Character.valueOf('L'), Ic2Items.lavaCell});
      Ic2Recipes.addCraftingRecipe(Ic2Items.reactorReflector, new Object[]{"TcT", "cCc", "TcT", Character.valueOf('c'), "dustCoal", Character.valueOf('C'), Ic2Items.denseCopperPlate, Character.valueOf('T'), "dustTin"});
      Ic2Recipes.addCraftingRecipe(Ic2Items.reactorReflectorThick, new Object[]{" R ", "RCR", " R ", Character.valueOf('C'), Ic2Items.denseCopperPlate, Character.valueOf('R'), Ic2Items.reactorReflector});
      Ic2Recipes.addCraftingRecipe(Ic2Items.reactorCondensator, new Object[]{"RRR", "RVR", "RSR", Character.valueOf('R'), Item.redstone, Character.valueOf('V'), Ic2Items.reactorVent, Character.valueOf('S'), Ic2Items.reactorHeatSwitch});
      new RecipeGradual((ItemGradual)Ic2Items.reactorCondensator.getItem(), new ItemStack(Item.redstone), 10000);
      Ic2Recipes.addCraftingRecipe(Ic2Items.reactorCondensatorLap, new Object[]{"RVR", "CLC", "RSR", Character.valueOf('R'), Item.redstone, Character.valueOf('V'), Ic2Items.reactorVentCore, Character.valueOf('S'), Ic2Items.reactorHeatSwitchCore, Character.valueOf('C'), Ic2Items.reactorCondensator, Character.valueOf('L'), Block.blockLapis});
      new RecipeGradual((ItemGradual)Ic2Items.reactorCondensatorLap.getItem(), new ItemStack(Item.redstone), 5000);
      new RecipeGradual((ItemGradual)Ic2Items.reactorCondensatorLap.getItem(), new ItemStack(Item.dyePowder, 1, 4), '\u9c40');
      Ic2Recipes.addCraftingRecipe(Ic2Items.batBox, new Object[]{"PCP", "BBB", "PPP", Character.valueOf('P'), "plankWood", Character.valueOf('C'), Ic2Items.insulatedCopperCableItem, Character.valueOf('B'), Ic2Items.reBattery});
      Ic2Recipes.addCraftingRecipe(Ic2Items.batBox, new Object[]{"PCP", "BBB", "PPP", Character.valueOf('P'), "plankWood", Character.valueOf('C'), Ic2Items.insulatedCopperCableItem, Character.valueOf('B'), Ic2Items.chargedReBattery});
      Ic2Recipes.addCraftingRecipe(Ic2Items.mfeUnit, new Object[]{"cCc", "CMC", "cCc", Character.valueOf('M'), Ic2Items.machine, Character.valueOf('c'), Ic2Items.doubleInsulatedGoldCableItem, Character.valueOf('C'), Ic2Items.energyCrystal});
      Ic2Recipes.addCraftingRecipe(Ic2Items.mfsUnit, new Object[]{"LCL", "LML", "LAL", Character.valueOf('M'), Ic2Items.mfeUnit, Character.valueOf('A'), Ic2Items.advancedMachine, Character.valueOf('C'), Ic2Items.advancedCircuit, Character.valueOf('L'), Ic2Items.lapotronCrystal});
      Ic2Recipes.addCraftingRecipe(Ic2Items.lvTransformer, new Object[]{"PCP", "ccc", "PCP", Character.valueOf('P'), "plankWood", Character.valueOf('C'), Ic2Items.insulatedCopperCableItem, Character.valueOf('c'), "ingotCopper"});
      Ic2Recipes.addCraftingRecipe(Ic2Items.mvTransformer, new Object[]{" C ", " M ", " C ", Character.valueOf('M'), Ic2Items.machine, Character.valueOf('C'), Ic2Items.doubleInsulatedGoldCableItem});
      Ic2Recipes.addCraftingRecipe(Ic2Items.hvTransformer, new Object[]{" c ", "CED", " c ", Character.valueOf('E'), Ic2Items.mvTransformer, Character.valueOf('c'), Ic2Items.trippleInsulatedIronCableItem, Character.valueOf('D'), Ic2Items.energyCrystal, Character.valueOf('C'), Ic2Items.electronicCircuit});
      Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.reinforcedStone, 8), new Object[]{"SSS", "SAS", "SSS", Character.valueOf('S'), Block.stone, Character.valueOf('A'), Ic2Items.advancedAlloy});
      Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.reinforcedGlass, 7), new Object[]{"GAG", "GGG", "GAG", Character.valueOf('G'), Block.glass, Character.valueOf('A'), Ic2Items.advancedAlloy});
      Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.reinforcedGlass, 7), new Object[]{"GGG", "AGA", "GGG", Character.valueOf('G'), Block.glass, Character.valueOf('A'), Ic2Items.advancedAlloy});
      Ic2Recipes.addCraftingRecipe(Ic2Items.remote, new Object[]{" c ", "GCG", "TTT", Character.valueOf('c'), Ic2Items.insulatedCopperCableItem, Character.valueOf('G'), Item.lightStoneDust, Character.valueOf('C'), Ic2Items.electronicCircuit, Character.valueOf('T'), Block.tnt});
      Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.rubberTrampoline, 3), new Object[]{"RRR", "RRR", Character.valueOf('R'), "itemRubber"});
      Ic2Recipes.addCraftingRecipe(new ItemStack(Block.torchWood, 4), new Object[]{"R", "I", Character.valueOf('I'), "stickWood", Character.valueOf('R'), Ic2Items.resin, Boolean.valueOf(true)});
      Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.scaffold, 16), new Object[]{"PPP", " s ", "s s", Character.valueOf('P'), "plankWood", Character.valueOf('s'), "stickWood"});
      Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.ironScaffold, 16), new Object[]{"PPP", " s ", "s s", Character.valueOf('P'), "ingotRefinedIron", Character.valueOf('s'), Ic2Items.ironFence.getItem()});
      Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.ironFence, 12), new Object[]{"III", "III", Character.valueOf('I'), "ingotRefinedIron"});
      if(enableCraftingITnt) {
         Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.industrialTnt, 4), new Object[]{"FFF", "TTT", "FFF", Character.valueOf('F'), Item.flint, Character.valueOf('T'), Block.tnt});
         Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.industrialTnt, 4), new Object[]{"FTF", "FTF", "FTF", Character.valueOf('F'), Item.flint, Character.valueOf('T'), Block.tnt});
      }

      if(enableCraftingNuke) {
         Ic2Recipes.addCraftingRecipe(Ic2Items.nuke, new Object[]{"GUG", "UGU", "GUG", Character.valueOf('G'), Item.gunpowder, Character.valueOf('U'), "ingotUranium", Boolean.valueOf(true)});
      }

      Ic2Recipes.addCraftingRecipe(new ItemStack(Block.stone, 16), new Object[]{"   ", " M ", "   ", Character.valueOf('M'), Ic2Items.matter, Boolean.valueOf(true)});
      Ic2Recipes.addCraftingRecipe(new ItemStack(Block.glass, 32), new Object[]{" M ", "M M", " M ", Character.valueOf('M'), Ic2Items.matter, Boolean.valueOf(true)});
      Ic2Recipes.addCraftingRecipe(new ItemStack(Block.grass, 16), new Object[]{"   ", "M  ", "M  ", Character.valueOf('M'), Ic2Items.matter, Boolean.valueOf(true)});
      Ic2Recipes.addCraftingRecipe(new ItemStack(Block.cobblestoneMossy, 16), new Object[]{"   ", " M ", "M M", Character.valueOf('M'), Ic2Items.matter, Boolean.valueOf(true)});
      Ic2Recipes.addCraftingRecipe(new ItemStack(Block.sandStone, 16), new Object[]{"   ", "  M", " M ", Character.valueOf('M'), Ic2Items.matter, Boolean.valueOf(true)});
      Ic2Recipes.addCraftingRecipe(new ItemStack(Block.blockSnow, 4), new Object[]{"M M", "   ", "   ", Character.valueOf('M'), Ic2Items.matter, Boolean.valueOf(true)});
      Ic2Recipes.addCraftingRecipe(new ItemStack(Block.waterStill, 1), new Object[]{"   ", " M ", " M ", Character.valueOf('M'), Ic2Items.matter, Boolean.valueOf(true)});
      Ic2Recipes.addCraftingRecipe(new ItemStack(Block.lavaStill, 1), new Object[]{" M ", " M ", " M ", Character.valueOf('M'), Ic2Items.matter, Boolean.valueOf(true)});
      Ic2Recipes.addCraftingRecipe(new ItemStack(Block.oreIron, 2), new Object[]{"M M", " M ", "M M", Character.valueOf('M'), Ic2Items.matter, Boolean.valueOf(true)});
      Ic2Recipes.addCraftingRecipe(new ItemStack(Block.oreGold, 2), new Object[]{" M ", "MMM", " M ", Character.valueOf('M'), Ic2Items.matter, Boolean.valueOf(true)});
      Ic2Recipes.addCraftingRecipe(new ItemStack(Block.obsidian, 12), new Object[]{"M M", "M M", "   ", Character.valueOf('M'), Ic2Items.matter, Boolean.valueOf(true)});
      Ic2Recipes.addCraftingRecipe(new ItemStack(Block.netherrack, 16), new Object[]{"  M", " M ", "M  ", Character.valueOf('M'), Ic2Items.matter, Boolean.valueOf(true)});
      Ic2Recipes.addCraftingRecipe(new ItemStack(Block.glowStone, 8), new Object[]{" M ", "M M", "MMM", Character.valueOf('M'), Ic2Items.matter, Boolean.valueOf(true)});
      Ic2Recipes.addCraftingRecipe(new ItemStack(Block.wood, 8), new Object[]{" M ", "   ", "   ", Character.valueOf('M'), Ic2Items.matter, Boolean.valueOf(true)});
      Ic2Recipes.addCraftingRecipe(new ItemStack(Block.cactus, 48), new Object[]{" M ", "MMM", "M M", Character.valueOf('M'), Ic2Items.matter, Boolean.valueOf(true)});
      Ic2Recipes.addCraftingRecipe(new ItemStack(Block.vine, 24), new Object[]{"M  ", "M  ", "M  ", Character.valueOf('M'), Ic2Items.matter, Boolean.valueOf(true)});
      Ic2Recipes.addCraftingRecipe(new ItemStack(Block.cloth, 12), new Object[]{"M M", "   ", " M ", Character.valueOf('M'), Ic2Items.matter, Boolean.valueOf(true)});
      Ic2Recipes.addCraftingRecipe(new ItemStack(Item.coal, 20), new Object[]{"  M", "M  ", "  M", Character.valueOf('M'), Ic2Items.matter, Boolean.valueOf(true)});
      Ic2Recipes.addCraftingRecipe(new ItemStack(Item.diamond, 1), new Object[]{"MMM", "MMM", "MMM", Character.valueOf('M'), Ic2Items.matter, Boolean.valueOf(true)});
      Ic2Recipes.addCraftingRecipe(new ItemStack(Item.redstone, 24), new Object[]{"   ", " M ", "MMM", Character.valueOf('M'), Ic2Items.matter, Boolean.valueOf(true)});
      Ic2Recipes.addCraftingRecipe(new ItemStack(Item.dyePowder, 9, 4), new Object[]{" M ", " M ", " MM", Character.valueOf('M'), Ic2Items.matter, Boolean.valueOf(true)});
      Ic2Recipes.addCraftingRecipe(new ItemStack(Item.feather, 32), new Object[]{" M ", " M ", "M M", Character.valueOf('M'), Ic2Items.matter, Boolean.valueOf(true)});
      Ic2Recipes.addCraftingRecipe(new ItemStack(Item.snowball, 16), new Object[]{"   ", "   ", "MMM", Character.valueOf('M'), Ic2Items.matter, Boolean.valueOf(true)});
      Ic2Recipes.addCraftingRecipe(new ItemStack(Item.gunpowder, 15), new Object[]{"MMM", "M  ", "MMM", Character.valueOf('M'), Ic2Items.matter, Boolean.valueOf(true)});
      Ic2Recipes.addCraftingRecipe(new ItemStack(Item.clay, 48), new Object[]{"MM ", "M  ", "MM ", Character.valueOf('M'), Ic2Items.matter, Boolean.valueOf(true)});
      Ic2Recipes.addCraftingRecipe(new ItemStack(Item.dyePowder, 32, 3), new Object[]{"MM ", "  M", "MM ", Character.valueOf('M'), Ic2Items.matter, Boolean.valueOf(true)});
      Ic2Recipes.addCraftingRecipe(new ItemStack(Item.dyePowder, 48, 0), new Object[]{" MM", " MM", " M ", Character.valueOf('M'), Ic2Items.matter, Boolean.valueOf(true)});
      Ic2Recipes.addCraftingRecipe(new ItemStack(Item.reed, 48), new Object[]{"M M", "M M", "M M", Character.valueOf('M'), Ic2Items.matter, Boolean.valueOf(true)});
      Ic2Recipes.addCraftingRecipe(new ItemStack(Item.flint, 32), new Object[]{" M ", "MM ", "MM ", Character.valueOf('M'), Ic2Items.matter, Boolean.valueOf(true)});
      Ic2Recipes.addCraftingRecipe(new ItemStack(Item.bone, 32), new Object[]{"M  ", "MM ", "M  ", Character.valueOf('M'), Ic2Items.matter, Boolean.valueOf(true)});
      Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.resin, 21), new Object[]{"M M", "   ", "M M", Character.valueOf('M'), Ic2Items.matter, Boolean.valueOf(true)});
      Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.iridiumOre, 1), new Object[]{"MMM", " M ", "MMM", Character.valueOf('M'), Ic2Items.matter, Boolean.valueOf(true)});
      Ic2Recipes.addCraftingRecipe(new ItemStack(Block.mycelium, 24), new Object[]{"   ", "M M", "MMM", Character.valueOf('M'), Ic2Items.matter, Boolean.valueOf(true)});
      Ic2Recipes.addCraftingRecipe(new ItemStack(Block.stoneBrick, 48, 3), new Object[]{"MM ", "MM ", "M  ", Character.valueOf('M'), Ic2Items.matter, Boolean.valueOf(true)});
      if(Ic2Items.copperOre != null) {
         Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.copperOre, 5), new Object[]{"  M", "M M", "   ", Character.valueOf('M'), Ic2Items.matter, Boolean.valueOf(true)});
      } else {
         Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.copperDust, 10), new Object[]{"  M", "M M", "   ", Character.valueOf('M'), Ic2Items.matter, Boolean.valueOf(true)});
      }

      if(Ic2Items.tinOre != null) {
         Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.tinOre, 5), new Object[]{"   ", "M M", "  M", Character.valueOf('M'), Ic2Items.matter, Boolean.valueOf(true)});
      } else {
         Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.tinDust, 10), new Object[]{"   ", "M M", "  M", Character.valueOf('M'), Ic2Items.matter, Boolean.valueOf(true)});
      }

      if(Ic2Items.rubberWood != null) {
         Ic2Recipes.addCraftingRecipe(new ItemStack(Block.planks, 3, 3), new Object[]{"W", Character.valueOf('W'), Ic2Items.rubberWood});
      }

      Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.insulatedCopperCableItem, 6), new Object[]{"RRR", "CCC", "RRR", Character.valueOf('C'), "ingotCopper", Character.valueOf('R'), "itemRubber"});
      Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.insulatedCopperCableItem, 6), new Object[]{"RCR", "RCR", "RCR", Character.valueOf('C'), "ingotCopper", Character.valueOf('R'), "itemRubber"});
      Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.copperCableItem, 6), new Object[]{"CCC", Character.valueOf('C'), "ingotCopper"});
      Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.goldCableItem, 12), new Object[]{"GGG", Character.valueOf('G'), Item.ingotGold});
      Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.insulatedGoldCableItem, 4), new Object[]{" R ", "RGR", " R ", Character.valueOf('G'), Item.ingotGold, Character.valueOf('R'), "itemRubber"});
      Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.glassFiberCableItem, 4), new Object[]{"GGG", "RDR", "GGG", Character.valueOf('G'), Block.glass, Character.valueOf('R'), Item.redstone, Character.valueOf('D'), Item.diamond});
      Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.glassFiberCableItem, 4), new Object[]{"GGG", "RDR", "GGG", Character.valueOf('G'), Block.glass, Character.valueOf('R'), Item.redstone, Character.valueOf('D'), Ic2Items.industrialDiamond});
      Ic2Recipes.addCraftingRecipe(Ic2Items.detectorCableItem, new Object[]{" C ", "RIR", " R ", Character.valueOf('R'), Item.redstone, Character.valueOf('I'), Ic2Items.trippleInsulatedIronCableItem, Character.valueOf('C'), Ic2Items.electronicCircuit});
      Ic2Recipes.addCraftingRecipe(Ic2Items.splitterCableItem, new Object[]{" R ", "ILI", " R ", Character.valueOf('R'), Item.redstone, Character.valueOf('I'), Ic2Items.trippleInsulatedIronCableItem, Character.valueOf('L'), Block.lever});
      Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.ironCableItem, 12), new Object[]{"III", Character.valueOf('I'), "ingotRefinedIron"});
      Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.insulatedIronCableItem, 4), new Object[]{" R ", "RIR", " R ", Character.valueOf('I'), "ingotRefinedIron", Character.valueOf('R'), "itemRubber"});
      Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.glassFiberCableItem, 6), new Object[]{"GGG", "SDS", "GGG", Character.valueOf('G'), Block.glass, Character.valueOf('S'), "ingotSilver", Character.valueOf('R'), Item.redstone, Character.valueOf('D'), Item.diamond});
      Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.glassFiberCableItem, 6), new Object[]{"GGG", "SDS", "GGG", Character.valueOf('G'), Block.glass, Character.valueOf('S'), "ingotSilver", Character.valueOf('R'), Item.redstone, Character.valueOf('D'), Ic2Items.industrialDiamond});
      Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.tinCableItem, 9), new Object[]{"TTT", Character.valueOf('T'), "ingotTin"});
      Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.insulatedCopperCableItem, new Object[]{"itemRubber", Ic2Items.copperCableItem});
      Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.insulatedGoldCableItem, new Object[]{"itemRubber", Ic2Items.goldCableItem});
      Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.doubleInsulatedGoldCableItem, new Object[]{"itemRubber", Ic2Items.insulatedGoldCableItem});
      Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.doubleInsulatedGoldCableItem, new Object[]{"itemRubber", "itemRubber", Ic2Items.goldCableItem});
      Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.insulatedIronCableItem, new Object[]{"itemRubber", Ic2Items.ironCableItem});
      Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.doubleInsulatedIronCableItem, new Object[]{"itemRubber", Ic2Items.insulatedIronCableItem});
      Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.trippleInsulatedIronCableItem, new Object[]{"itemRubber", Ic2Items.doubleInsulatedIronCableItem});
      Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.trippleInsulatedIronCableItem, new Object[]{"itemRubber", "itemRubber", Ic2Items.insulatedIronCableItem});
      Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.doubleInsulatedIronCableItem, new Object[]{"itemRubber", "itemRubber", Ic2Items.ironCableItem});
      Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.trippleInsulatedIronCableItem, new Object[]{"itemRubber", "itemRubber", "itemRubber", Ic2Items.ironCableItem});
      Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.suBattery, 5), new Object[]{"C", "R", "D", Character.valueOf('D'), "dustCoal", Character.valueOf('R'), Item.redstone, Character.valueOf('C'), Ic2Items.insulatedCopperCableItem});
      Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.suBattery, 5), new Object[]{"C", "D", "R", Character.valueOf('D'), "dustCoal", Character.valueOf('R'), Item.redstone, Character.valueOf('C'), Ic2Items.insulatedCopperCableItem});
      Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.suBattery, 8), new Object[]{"c", "C", "R", Character.valueOf('R'), Item.redstone, Character.valueOf('C'), Ic2Items.hydratedCoalDust, Character.valueOf('c'), Ic2Items.insulatedCopperCableItem});
      Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.suBattery, 8), new Object[]{"c", "R", "C", Character.valueOf('R'), Item.redstone, Character.valueOf('C'), Ic2Items.hydratedCoalDust, Character.valueOf('c'), Ic2Items.insulatedCopperCableItem});
      Ic2Recipes.addCraftingRecipe(Ic2Items.reBattery, new Object[]{" C ", "TRT", "TRT", Character.valueOf('T'), "ingotTin", Character.valueOf('R'), Item.redstone, Character.valueOf('C'), Ic2Items.insulatedCopperCableItem});
      Ic2Recipes.addCraftingRecipe(Ic2Items.energyCrystal, new Object[]{"RRR", "RDR", "RRR", Character.valueOf('D'), Item.diamond, Character.valueOf('R'), Item.redstone});
      Ic2Recipes.addCraftingRecipe(Ic2Items.energyCrystal, new Object[]{"RRR", "RDR", "RRR", Character.valueOf('D'), Ic2Items.industrialDiamond, Character.valueOf('R'), Item.redstone});
      Ic2Recipes.addCraftingRecipe(Ic2Items.lapotronCrystal, new Object[]{"LCL", "LDL", "LCL", Character.valueOf('D'), Ic2Items.energyCrystal, Character.valueOf('C'), Ic2Items.electronicCircuit, Character.valueOf('L'), new ItemStack(Item.dyePowder, 1, 4)});
      Ic2Recipes.addCraftingRecipe(Ic2Items.treetap, new Object[]{" P ", "PPP", "P  ", Character.valueOf('P'), "plankWood"});
      Ic2Recipes.addCraftingRecipe(Ic2Items.painter, new Object[]{" CC", " IC", "I  ", Character.valueOf('C'), Block.cloth, Character.valueOf('I'), Item.ingotIron});
      Ic2Recipes.addCraftingRecipe(new ItemStack(Item.pickaxeDiamond, 1), new Object[]{"DDD", " S ", " S ", Character.valueOf('S'), "stickWood", Character.valueOf('D'), Ic2Items.industrialDiamond});
      Ic2Recipes.addCraftingRecipe(new ItemStack(Item.hoeDiamond, 1), new Object[]{"DD ", " S ", " S ", Character.valueOf('S'), "stickWood", Character.valueOf('D'), Ic2Items.industrialDiamond});
      Ic2Recipes.addCraftingRecipe(new ItemStack(Item.shovelDiamond, 1), new Object[]{"D", "S", "S", Character.valueOf('S'), "stickWood", Character.valueOf('D'), Ic2Items.industrialDiamond});
      Ic2Recipes.addCraftingRecipe(new ItemStack(Item.axeDiamond, 1), new Object[]{"DD ", "DS ", " S ", Character.valueOf('S'), "stickWood", Character.valueOf('D'), Ic2Items.industrialDiamond});
      Ic2Recipes.addCraftingRecipe(new ItemStack(Item.swordDiamond, 1), new Object[]{"D", "D", "S", Character.valueOf('S'), "stickWood", Character.valueOf('D'), Ic2Items.industrialDiamond});
      Ic2Recipes.addCraftingRecipe(new ItemStack(Ic2Items.constructionFoamSprayer.itemID, 1, 1601), new Object[]{"SS ", "Ss ", "  S", Character.valueOf('S'), Block.cobblestone, Character.valueOf('s'), "stickWood"});
      new RecipeGradual((ItemGradual)Ic2Items.constructionFoamSprayer.getItem(), Ic2Items.constructionFoamPellet, 100);
      Ic2Recipes.addCraftingRecipe(Ic2Items.bronzePickaxe, new Object[]{"BBB", " S ", " S ", Character.valueOf('B'), "ingotBronze", Character.valueOf('S'), "stickWood"});
      Ic2Recipes.addCraftingRecipe(Ic2Items.bronzeAxe, new Object[]{"BB", "SB", "S ", Character.valueOf('B'), "ingotBronze", Character.valueOf('S'), "stickWood"});
      Ic2Recipes.addCraftingRecipe(Ic2Items.bronzeHoe, new Object[]{"BB", "S ", "S ", Character.valueOf('B'), "ingotBronze", Character.valueOf('S'), "stickWood"});
      Ic2Recipes.addCraftingRecipe(Ic2Items.bronzeSword, new Object[]{"B", "B", "S", Character.valueOf('B'), "ingotBronze", Character.valueOf('S'), "stickWood"});
      Ic2Recipes.addCraftingRecipe(Ic2Items.bronzeShovel, new Object[]{" B ", " S ", " S ", Character.valueOf('B'), "ingotBronze", Character.valueOf('S'), "stickWood"});
      Ic2Recipes.addCraftingRecipe(Ic2Items.wrench, new Object[]{"B B", "BBB", " B ", Character.valueOf('B'), "ingotBronze"});
      Ic2Recipes.addCraftingRecipe(Ic2Items.cutter, new Object[]{"A A", " A ", "I I", Character.valueOf('A'), "ingotRefinedIron", Character.valueOf('I'), Item.ingotIron});
      Ic2Recipes.addCraftingRecipe(Ic2Items.toolbox, new Object[]{"ICI", "III", Character.valueOf('C'), Block.chest, Character.valueOf('I'), "ingotRefinedIron"});
      Ic2Recipes.addCraftingRecipe(Ic2Items.miningDrill, new Object[]{" I ", "ICI", "IBI", Character.valueOf('I'), "ingotRefinedIron", Character.valueOf('B'), Ic2Items.reBattery, Character.valueOf('C'), Ic2Items.electronicCircuit});
      Ic2Recipes.addCraftingRecipe(Ic2Items.miningDrill, new Object[]{" I ", "ICI", "IBI", Character.valueOf('I'), "ingotRefinedIron", Character.valueOf('B'), Ic2Items.chargedReBattery, Character.valueOf('C'), Ic2Items.electronicCircuit});
      Ic2Recipes.addCraftingRecipe(Ic2Items.chainsaw, new Object[]{" II", "ICI", "BI ", Character.valueOf('I'), "ingotRefinedIron", Character.valueOf('B'), Ic2Items.reBattery, Character.valueOf('C'), Ic2Items.electronicCircuit});
      Ic2Recipes.addCraftingRecipe(Ic2Items.chainsaw, new Object[]{" II", "ICI", "BI ", Character.valueOf('I'), "ingotRefinedIron", Character.valueOf('B'), Ic2Items.chargedReBattery, Character.valueOf('C'), Ic2Items.electronicCircuit});
      Ic2Recipes.addCraftingRecipe(Ic2Items.diamondDrill, new Object[]{" D ", "DdD", Character.valueOf('D'), Item.diamond, Character.valueOf('d'), Ic2Items.miningDrill});
      Ic2Recipes.addCraftingRecipe(Ic2Items.diamondDrill, new Object[]{" D ", "DdD", Character.valueOf('D'), Item.diamond, Character.valueOf('d'), Ic2Items.miningDrill});
      Ic2Recipes.addCraftingRecipe(Ic2Items.odScanner, new Object[]{" G ", "CBC", "ccc", Character.valueOf('B'), Ic2Items.reBattery, Character.valueOf('c'), Ic2Items.insulatedCopperCableItem, Character.valueOf('G'), Item.lightStoneDust, Character.valueOf('C'), Ic2Items.electronicCircuit});
      Ic2Recipes.addCraftingRecipe(Ic2Items.odScanner, new Object[]{" G ", "CBC", "ccc", Character.valueOf('B'), Ic2Items.chargedReBattery, Character.valueOf('c'), Ic2Items.insulatedCopperCableItem, Character.valueOf('G'), Item.lightStoneDust, Character.valueOf('C'), Ic2Items.electronicCircuit});
      Ic2Recipes.addCraftingRecipe(Ic2Items.ovScanner, new Object[]{" G ", "GCG", "cSc", Character.valueOf('S'), Ic2Items.odScanner, Character.valueOf('c'), Ic2Items.doubleInsulatedGoldCableItem, Character.valueOf('G'), Item.lightStoneDust, Character.valueOf('C'), Ic2Items.advancedCircuit});
      Ic2Recipes.addCraftingRecipe(Ic2Items.ovScanner, new Object[]{" G ", "GCG", "cSc", Character.valueOf('S'), Ic2Items.chargedReBattery, Character.valueOf('c'), Ic2Items.doubleInsulatedGoldCableItem, Character.valueOf('G'), Item.lightStoneDust, Character.valueOf('C'), Ic2Items.advancedCircuit});
      Ic2Recipes.addCraftingRecipe(Ic2Items.electricWrench, new Object[]{"  W", " C ", "B  ", Character.valueOf('W'), Ic2Items.wrench, Character.valueOf('B'), Ic2Items.reBattery, Character.valueOf('C'), Ic2Items.electronicCircuit});
      Ic2Recipes.addCraftingRecipe(Ic2Items.electricWrench, new Object[]{"  W", " C ", "B  ", Character.valueOf('W'), Ic2Items.wrench, Character.valueOf('B'), Ic2Items.chargedReBattery, Character.valueOf('C'), Ic2Items.electronicCircuit});
      Ic2Recipes.addCraftingRecipe(Ic2Items.electricTreetap, new Object[]{"  W", " C ", "B  ", Character.valueOf('W'), Ic2Items.treetap, Character.valueOf('B'), Ic2Items.reBattery, Character.valueOf('C'), Ic2Items.electronicCircuit});
      Ic2Recipes.addCraftingRecipe(Ic2Items.electricTreetap, new Object[]{"  W", " C ", "B  ", Character.valueOf('W'), Ic2Items.treetap, Character.valueOf('B'), Ic2Items.chargedReBattery, Character.valueOf('C'), Ic2Items.electronicCircuit});
      Ic2Recipes.addCraftingRecipe(Ic2Items.ecMeter, new Object[]{" G ", "cCc", "c c", Character.valueOf('G'), Item.lightStoneDust, Character.valueOf('c'), Ic2Items.insulatedCopperCableItem, Character.valueOf('C'), Ic2Items.electronicCircuit});
      Ic2Recipes.addCraftingRecipe(Ic2Items.miningLaser, new Object[]{"Rcc", "AAC", " AA", Character.valueOf('A'), Ic2Items.advancedAlloy, Character.valueOf('C'), Ic2Items.advancedCircuit, Character.valueOf('c'), Ic2Items.energyCrystal, Character.valueOf('R'), Item.redstone});
      Ic2Recipes.addCraftingRecipe(Ic2Items.nanoSaber, new Object[]{"GA ", "GA ", "CcC", Character.valueOf('C'), Ic2Items.carbonPlate, Character.valueOf('c'), Ic2Items.energyCrystal, Character.valueOf('G'), Item.lightStoneDust, Character.valueOf('A'), Ic2Items.advancedAlloy});
      Ic2Recipes.addCraftingRecipe(Ic2Items.electricHoe, new Object[]{"II ", " C ", " B ", Character.valueOf('I'), "ingotRefinedIron", Character.valueOf('B'), Ic2Items.reBattery, Character.valueOf('C'), Ic2Items.electronicCircuit});
      Ic2Recipes.addCraftingRecipe(Ic2Items.electricHoe, new Object[]{"II ", " C ", " B ", Character.valueOf('I'), "ingotRefinedIron", Character.valueOf('B'), Ic2Items.chargedReBattery, Character.valueOf('C'), Ic2Items.electronicCircuit});
      Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.frequencyTransmitter, new Object[]{Ic2Items.electronicCircuit, Ic2Items.insulatedCopperCableItem});
      Ic2Recipes.addCraftingRecipe(Ic2Items.advancedCircuit, new Object[]{"RGR", "LCL", "RGR", Character.valueOf('L'), new ItemStack(Item.dyePowder, 1, 4), Character.valueOf('G'), Item.lightStoneDust, Character.valueOf('R'), Item.redstone, Character.valueOf('C'), Ic2Items.electronicCircuit});
      Ic2Recipes.addCraftingRecipe(Ic2Items.advancedCircuit, new Object[]{"RLR", "GCG", "RLR", Character.valueOf('L'), new ItemStack(Item.dyePowder, 1, 4), Character.valueOf('G'), Item.lightStoneDust, Character.valueOf('R'), Item.redstone, Character.valueOf('C'), Ic2Items.electronicCircuit});
      Ic2Recipes.addCraftingRecipe(Ic2Items.plantBall, new Object[]{"PPP", "P P", "PPP", Character.valueOf('P'), Item.wheat});
      Ic2Recipes.addCraftingRecipe(Ic2Items.plantBall, new Object[]{"PPP", "P P", "PPP", Character.valueOf('P'), Item.reed});
      Ic2Recipes.addCraftingRecipe(Ic2Items.plantBall, new Object[]{"PPP", "P P", "PPP", Character.valueOf('P'), Item.seeds});
      Ic2Recipes.addCraftingRecipe(Ic2Items.plantBall, new Object[]{"PPP", "P P", "PPP", Character.valueOf('P'), "treeLeaves"});
      if(Ic2Items.rubberLeaves != null) {
         Ic2Recipes.addCraftingRecipe(Ic2Items.plantBall, new Object[]{"PPP", "P P", "PPP", Character.valueOf('P'), Ic2Items.rubberLeaves});
      }

      Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.plantBall, 2), new Object[]{"PPP", "P P", "PPP", Character.valueOf('P'), "treeSapling"});
      if(Ic2Items.rubberSapling != null) {
         Ic2Recipes.addCraftingRecipe(Ic2Items.plantBall, new Object[]{"PPP", "P P", "PPP", Character.valueOf('P'), Ic2Items.rubberSapling});
      }

      Ic2Recipes.addCraftingRecipe(Ic2Items.plantBall, new Object[]{"PPP", "P P", "PPP", Character.valueOf('P'), Block.tallGrass});
      Ic2Recipes.addCraftingRecipe(Ic2Items.carbonFiber, new Object[]{"CC", "CC", Character.valueOf('C'), "dustCoal"});
      Ic2Recipes.addCraftingRecipe(Ic2Items.iridiumPlate, new Object[]{"IAI", "ADA", "IAI", Character.valueOf('I'), Ic2Items.iridiumOre, Character.valueOf('A'), Ic2Items.advancedAlloy, Character.valueOf('D'), Item.diamond});
      Ic2Recipes.addCraftingRecipe(Ic2Items.iridiumPlate, new Object[]{"IAI", "ADA", "IAI", Character.valueOf('I'), Ic2Items.iridiumOre, Character.valueOf('A'), Ic2Items.advancedAlloy, Character.valueOf('D'), Ic2Items.industrialDiamond});
      Ic2Recipes.addCraftingRecipe(Ic2Items.coalBall, new Object[]{"CCC", "CFC", "CCC", Character.valueOf('C'), "dustCoal", Character.valueOf('F'), Item.flint});
      Ic2Recipes.addCraftingRecipe(Ic2Items.coalChunk, new Object[]{"###", "#O#", "###", Character.valueOf('#'), Ic2Items.compressedCoalBall, Character.valueOf('O'), Block.obsidian});
      Ic2Recipes.addCraftingRecipe(Ic2Items.coalChunk, new Object[]{"###", "#O#", "###", Character.valueOf('#'), Ic2Items.compressedCoalBall, Character.valueOf('O'), Block.blockSteel, Boolean.valueOf(true)});
      Ic2Recipes.addCraftingRecipe(Ic2Items.coalChunk, new Object[]{"###", "#O#", "###", Character.valueOf('#'), Ic2Items.compressedCoalBall, Character.valueOf('O'), Block.brick, Boolean.valueOf(true)});
      Ic2Recipes.addCraftingRecipe(Ic2Items.smallIronDust, new Object[]{"CTC", "TCT", "CTC", Character.valueOf('C'), "dustCopper", Character.valueOf('T'), "dustTin", Boolean.valueOf(true)});
      Ic2Recipes.addCraftingRecipe(Ic2Items.smallIronDust, new Object[]{"TCT", "CTC", "TCT", Character.valueOf('C'), "dustCopper", Character.valueOf('T'), "dustTin", Boolean.valueOf(true)});
      Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.hydratedCoalDust, 8), new Object[]{"CCC", "CWC", "CCC", Character.valueOf('C'), "dustCoal", Character.valueOf('W'), "liquid$" + Block.waterStill.blockID});
      Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.refinedIronIngot, 8), new Object[]{"M", Character.valueOf('M'), Ic2Items.machine});
      Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.copperIngot, 9), new Object[]{"B", Character.valueOf('B'), Ic2Items.copperBlock});
      Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.tinIngot, 9), new Object[]{"B", Character.valueOf('B'), Ic2Items.tinBlock});
      Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.bronzeIngot, 9), new Object[]{"B", Character.valueOf('B'), Ic2Items.bronzeBlock});
      Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.uraniumIngot, 9), new Object[]{"B", Character.valueOf('B'), Ic2Items.uraniumBlock});
      Ic2Recipes.addCraftingRecipe(Ic2Items.electronicCircuit, new Object[]{"CCC", "RIR", "CCC", Character.valueOf('I'), "ingotRefinedIron", Character.valueOf('R'), Item.redstone, Character.valueOf('C'), Ic2Items.insulatedCopperCableItem});
      Ic2Recipes.addCraftingRecipe(Ic2Items.electronicCircuit, new Object[]{"CRC", "CIC", "CRC", Character.valueOf('I'), "ingotRefinedIron", Character.valueOf('R'), Item.redstone, Character.valueOf('C'), Ic2Items.insulatedCopperCableItem});
      Ic2Recipes.addCraftingRecipe(Ic2Items.compositeArmor, new Object[]{"A A", "ALA", "AIA", Character.valueOf('L'), Item.plateLeather, Character.valueOf('I'), Item.plateSteel, Character.valueOf('A'), Ic2Items.advancedAlloy});
      Ic2Recipes.addCraftingRecipe(Ic2Items.compositeArmor, new Object[]{"A A", "AIA", "ALA", Character.valueOf('L'), Item.plateLeather, Character.valueOf('I'), Item.plateSteel, Character.valueOf('A'), Ic2Items.advancedAlloy});
      Ic2Recipes.addCraftingRecipe(Ic2Items.nanoHelmet, new Object[]{"CcC", "CGC", Character.valueOf('C'), Ic2Items.carbonPlate, Character.valueOf('c'), Ic2Items.energyCrystal, Character.valueOf('G'), Block.glass});
      Ic2Recipes.addCraftingRecipe(Ic2Items.nanoBodyarmor, new Object[]{"C C", "CcC", "CCC", Character.valueOf('C'), Ic2Items.carbonPlate, Character.valueOf('c'), Ic2Items.energyCrystal});
      Ic2Recipes.addCraftingRecipe(Ic2Items.nanoLeggings, new Object[]{"CcC", "C C", "C C", Character.valueOf('C'), Ic2Items.carbonPlate, Character.valueOf('c'), Ic2Items.energyCrystal});
      Ic2Recipes.addCraftingRecipe(Ic2Items.nanoBoots, new Object[]{"C C", "CcC", Character.valueOf('C'), Ic2Items.carbonPlate, Character.valueOf('c'), Ic2Items.energyCrystal});
      Ic2Recipes.addCraftingRecipe(Ic2Items.quantumHelmet, new Object[]{" n ", "ILI", "CGC", Character.valueOf('n'), Ic2Items.nanoHelmet, Character.valueOf('I'), Ic2Items.iridiumPlate, Character.valueOf('L'), Ic2Items.lapotronCrystal, Character.valueOf('G'), Ic2Items.reinforcedGlass, Character.valueOf('C'), Ic2Items.advancedCircuit});
      Ic2Recipes.addCraftingRecipe(Ic2Items.quantumBodyarmor, new Object[]{"AnA", "ILI", "IAI", Character.valueOf('n'), Ic2Items.nanoBodyarmor, Character.valueOf('I'), Ic2Items.iridiumPlate, Character.valueOf('L'), Ic2Items.lapotronCrystal, Character.valueOf('A'), Ic2Items.advancedAlloy});
      Ic2Recipes.addCraftingRecipe(Ic2Items.quantumLeggings, new Object[]{"MLM", "InI", "G G", Character.valueOf('n'), Ic2Items.nanoLeggings, Character.valueOf('I'), Ic2Items.iridiumPlate, Character.valueOf('L'), Ic2Items.lapotronCrystal, Character.valueOf('G'), Item.lightStoneDust, Character.valueOf('M'), Ic2Items.machine});
      Ic2Recipes.addCraftingRecipe(Ic2Items.quantumBoots, new Object[]{"InI", "RLR", Character.valueOf('n'), Ic2Items.nanoBoots, Character.valueOf('I'), Ic2Items.iridiumPlate, Character.valueOf('L'), Ic2Items.lapotronCrystal, Character.valueOf('R'), Ic2Items.hazmatBoots});
      Ic2Recipes.addCraftingRecipe(Ic2Items.hazmatHelmet, new Object[]{" O ", "RGR", "R#R", Character.valueOf('R'), "itemRubber", Character.valueOf('G'), Block.glass, Character.valueOf('#'), Block.fenceIron, Character.valueOf('O'), new ItemStack(Item.dyePowder, 1, 14)});
      Ic2Recipes.addCraftingRecipe(Ic2Items.hazmatChestplate, new Object[]{"R R", "ROR", "ROR", Character.valueOf('R'), "itemRubber", Character.valueOf('O'), new ItemStack(Item.dyePowder, 1, 14)});
      Ic2Recipes.addCraftingRecipe(Ic2Items.hazmatLeggings, new Object[]{"ROR", "R R", "R R", Character.valueOf('R'), "itemRubber", Character.valueOf('O'), new ItemStack(Item.dyePowder, 1, 14)});
      Ic2Recipes.addCraftingRecipe(Ic2Items.hazmatBoots, new Object[]{"R R", "R R", "RWR", Character.valueOf('R'), "itemRubber", Character.valueOf('W'), Block.cloth});
      Ic2Recipes.addCraftingRecipe(Ic2Items.batPack, new Object[]{"BCB", "BTB", "B B", Character.valueOf('T'), "ingotTin", Character.valueOf('C'), Ic2Items.electronicCircuit, Character.valueOf('B'), Ic2Items.chargedReBattery});
      Ic2Recipes.addCraftingRecipe(Ic2Items.batPack, new Object[]{"BCB", "BTB", "B B", Character.valueOf('T'), "ingotTin", Character.valueOf('C'), Ic2Items.electronicCircuit, Character.valueOf('B'), Ic2Items.reBattery});
      Ic2Recipes.addCraftingRecipe(Ic2Items.lapPack, new Object[]{"LAL", "LBL", "L L", Character.valueOf('L'), Block.blockLapis, Character.valueOf('A'), Ic2Items.advancedCircuit, Character.valueOf('B'), Ic2Items.batPack});
      Ic2Recipes.addCraftingRecipe(Ic2Items.solarHelmet, new Object[]{"III", "ISI", "CCC", Character.valueOf('I'), Item.ingotIron, Character.valueOf('S'), Ic2Items.solarPanel, Character.valueOf('C'), Ic2Items.insulatedCopperCableItem});
      Ic2Recipes.addCraftingRecipe(Ic2Items.solarHelmet, new Object[]{" H ", " S ", "CCC", Character.valueOf('H'), Item.helmetSteel, Character.valueOf('S'), Ic2Items.solarPanel, Character.valueOf('C'), Ic2Items.insulatedCopperCableItem});
      Ic2Recipes.addCraftingRecipe(Ic2Items.staticBoots, new Object[]{"I I", "ISI", "CCC", Character.valueOf('I'), Item.ingotIron, Character.valueOf('S'), Block.cloth, Character.valueOf('C'), Ic2Items.insulatedCopperCableItem});
      Ic2Recipes.addCraftingRecipe(Ic2Items.staticBoots, new Object[]{" H ", " S ", "CCC", Character.valueOf('H'), Item.bootsSteel, Character.valueOf('S'), Block.cloth, Character.valueOf('C'), Ic2Items.insulatedCopperCableItem});
      Ic2Recipes.addCraftingRecipe(Ic2Items.bronzeHelmet, new Object[]{"BBB", "B B", Character.valueOf('B'), "ingotBronze"});
      Ic2Recipes.addCraftingRecipe(Ic2Items.bronzeChestplate, new Object[]{"B B", "BBB", "BBB", Character.valueOf('B'), "ingotBronze"});
      Ic2Recipes.addCraftingRecipe(Ic2Items.bronzeLeggings, new Object[]{"BBB", "B B", "B B", Character.valueOf('B'), "ingotBronze"});
      Ic2Recipes.addCraftingRecipe(Ic2Items.bronzeBoots, new Object[]{"B B", "B B", Character.valueOf('B'), "ingotBronze"});
      Ic2Recipes.addCraftingRecipe(new ItemStack(Ic2Items.jetpack.itemID, 1, 18001), new Object[]{"ICI", "IFI", "R R", Character.valueOf('I'), "ingotRefinedIron", Character.valueOf('C'), Ic2Items.electronicCircuit, Character.valueOf('F'), Ic2Items.fuelCan, Character.valueOf('R'), Item.redstone});
      Ic2Recipes.addCraftingRecipe(Ic2Items.electricJetpack, new Object[]{"ICI", "IBI", "G G", Character.valueOf('I'), "ingotRefinedIron", Character.valueOf('C'), Ic2Items.advancedCircuit, Character.valueOf('B'), Ic2Items.batBox, Character.valueOf('G'), Item.lightStoneDust});
      Ic2Recipes.addCraftingRecipe(Ic2Items.terraformerBlueprint, new Object[]{" C ", " A ", "R R", Character.valueOf('C'), Ic2Items.electronicCircuit, Character.valueOf('A'), Ic2Items.advancedCircuit, Character.valueOf('R'), Item.redstone});
      Ic2Recipes.addCraftingRecipe(Ic2Items.cultivationTerraformerBlueprint, new Object[]{" S ", "S#S", " S ", Character.valueOf('#'), Ic2Items.terraformerBlueprint, Character.valueOf('S'), Item.seeds});
      Ic2Recipes.addCraftingRecipe(Ic2Items.desertificationTerraformerBlueprint, new Object[]{" S ", "S#S", " S ", Character.valueOf('#'), Ic2Items.terraformerBlueprint, Character.valueOf('S'), Block.sand});
      Ic2Recipes.addCraftingRecipe(Ic2Items.irrigationTerraformerBlueprint, new Object[]{" W ", "W#W", " W ", Character.valueOf('#'), Ic2Items.terraformerBlueprint, Character.valueOf('W'), Item.bucketWater});
      Ic2Recipes.addCraftingRecipe(Ic2Items.chillingTerraformerBlueprint, new Object[]{" S ", "S#S", " S ", Character.valueOf('#'), Ic2Items.terraformerBlueprint, Character.valueOf('S'), Item.snowball});
      Ic2Recipes.addCraftingRecipe(Ic2Items.flatificatorTerraformerBlueprint, new Object[]{" D ", "D#D", " D ", Character.valueOf('#'), Ic2Items.terraformerBlueprint, Character.valueOf('D'), Block.dirt});
      Ic2Recipes.addCraftingRecipe(Ic2Items.mushroomTerraformerBlueprint, new Object[]{"mMm", "M#M", "mMm", Character.valueOf('#'), Ic2Items.terraformerBlueprint, Character.valueOf('M'), Block.mushroomBrown, Character.valueOf('m'), Block.mycelium});
      Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.terraformerBlueprint, new Object[]{Ic2Items.cultivationTerraformerBlueprint});
      Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.terraformerBlueprint, new Object[]{Ic2Items.irrigationTerraformerBlueprint});
      Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.terraformerBlueprint, new Object[]{Ic2Items.chillingTerraformerBlueprint});
      Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.terraformerBlueprint, new Object[]{Ic2Items.desertificationTerraformerBlueprint});
      Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.terraformerBlueprint, new Object[]{Ic2Items.flatificatorTerraformerBlueprint});
      Ic2Recipes.addCraftingRecipe(Ic2Items.overclockerUpgrade, new Object[]{"CCC", "WEW", Character.valueOf('C'), Ic2Items.reactorCoolantSimple, Character.valueOf('W'), Ic2Items.insulatedCopperCableItem, Character.valueOf('E'), Ic2Items.electronicCircuit});
      Ic2Recipes.addCraftingRecipe(Ic2Items.transformerUpgrade, new Object[]{"GGG", "WTW", "GEG", Character.valueOf('G'), Block.glass, Character.valueOf('W'), Ic2Items.doubleInsulatedGoldCableItem, Character.valueOf('T'), Ic2Items.mvTransformer, Character.valueOf('E'), Ic2Items.electronicCircuit});
      Ic2Recipes.addCraftingRecipe(Ic2Items.energyStorageUpgrade, new Object[]{"www", "WBW", "wEw", Character.valueOf('w'), Block.planks, Character.valueOf('W'), Ic2Items.insulatedCopperCableItem, Character.valueOf('B'), Ic2Items.reBattery, Character.valueOf('E'), Ic2Items.electronicCircuit});
      Ic2Recipes.addCraftingRecipe(Ic2Items.energyStorageUpgrade, new Object[]{"www", "WBW", "wEw", Character.valueOf('w'), Block.planks, Character.valueOf('W'), Ic2Items.insulatedCopperCableItem, Character.valueOf('B'), Ic2Items.chargedReBattery, Character.valueOf('E'), Ic2Items.electronicCircuit});
      Ic2Recipes.addCraftingRecipe(Ic2Items.reinforcedDoor, new Object[]{"SS", "SS", "SS", Character.valueOf('S'), Ic2Items.reinforcedStone});
      Ic2Recipes.addCraftingRecipe(Ic2Items.scrapBox, new Object[]{"SSS", "SSS", "SSS", Character.valueOf('S'), Ic2Items.scrap});
      Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.stickyDynamite, 8), new Object[]{"DDD", "DRD", "DDD", Character.valueOf('D'), Ic2Items.dynamite, Character.valueOf('R'), Ic2Items.resin});
      Ic2Recipes.addShapelessCraftingRecipe(StackUtil.copyWithSize(Ic2Items.dynamite, 8), new Object[]{Ic2Items.industrialTnt, Item.silk});
      Ic2Recipes.addShapelessCraftingRecipe(StackUtil.copyWithSize(Ic2Items.bronzeDust, 2), new Object[]{"dustTin", "dustCopper", "dustCopper", "dustCopper"});
      Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.ironDust, new Object[]{Ic2Items.smallIronDust, Ic2Items.smallIronDust});
      Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.carbonMesh, new Object[]{Ic2Items.carbonFiber, Ic2Items.carbonFiber});
      Ic2Recipes.addShapelessCraftingRecipe(new ItemStack(Block.pistonStickyBase, 1), new Object[]{Block.pistonBase, Ic2Items.resin, Boolean.valueOf(true)});
      Ic2Recipes.addCraftingRecipe(new ItemStack(Block.pistonBase, 1), new Object[]{"TTT", "#X#", "#R#", Character.valueOf('#'), Block.cobblestone, Character.valueOf('X'), "ingotBronze", Character.valueOf('R'), Item.redstone, Character.valueOf('T'), Block.planks, Boolean.valueOf(true)});
      Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.miningPipe, 8), new Object[]{"I I", "I I", "ITI", Character.valueOf('I'), "ingotRefinedIron", Character.valueOf('T'), Ic2Items.treetap});
      if(Ic2Items.rubberSapling != null) {
         Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.plantBall, 2), new Object[]{"PPP", "P P", "PPP", Character.valueOf('P'), Ic2Items.rubberSapling});
      }

      if(enableCraftingGlowstoneDust) {
         Ic2Recipes.addCraftingRecipe(new ItemStack(Item.lightStoneDust, 1), new Object[]{"RGR", "GRG", "RGR", Character.valueOf('R'), Item.redstone, Character.valueOf('G'), "dustGold", Boolean.valueOf(true)});
      }

      if(enableCraftingGunpowder) {
         Ic2Recipes.addCraftingRecipe(new ItemStack(Item.gunpowder, 3), new Object[]{"RCR", "CRC", "RCR", Character.valueOf('R'), Item.redstone, Character.valueOf('C'), "dustCoal", Boolean.valueOf(true)});
      }

      if(enableCraftingBucket) {
         Ic2Recipes.addCraftingRecipe(new ItemStack(Item.bucketEmpty, 1), new Object[]{"T T", " T ", Character.valueOf('T'), "ingotTin", Boolean.valueOf(true)});
      }

      if(enableCraftingCoin) {
         Ic2Recipes.addCraftingRecipe(Ic2Items.refinedIronIngot, new Object[]{"III", "III", "III", Character.valueOf('I'), Ic2Items.coin});
      }

      if(enableCraftingCoin) {
         Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.coin, 16), new Object[]{"II", "II", Character.valueOf('I'), "ingotRefinedIron"});
      }

      if(enableCraftingRail) {
         Ic2Recipes.addCraftingRecipe(new ItemStack(Block.rail, 8), new Object[]{"B B", "BsB", "B B", Character.valueOf('B'), "ingotBronze", Character.valueOf('s'), "stickWood", Boolean.valueOf(true)});
      }

      Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.crop, 2), new Object[]{"S S", "S S", Character.valueOf('S'), "stickWood"});
      Ic2Recipes.addCraftingRecipe(new ItemStack(Ic2Items.cropnalyzer.getItem()), new Object[]{"cc ", "RGR", "RCR", Character.valueOf('G'), Block.glass, Character.valueOf('c'), Ic2Items.insulatedCopperCableItem, Character.valueOf('R'), Item.redstone, Character.valueOf('C'), Ic2Items.electronicCircuit});
      Ic2Recipes.addShapelessCraftingRecipe(StackUtil.copyWithSize(Ic2Items.fertilizer, 2), new Object[]{Ic2Items.scrap, new ItemStack(Item.dyePowder, 1, 15)});
      Ic2Recipes.addShapelessCraftingRecipe(StackUtil.copyWithSize(Ic2Items.fertilizer, 2), new Object[]{Ic2Items.scrap, Ic2Items.scrap, Ic2Items.fertilizer});
      Ic2Recipes.addCraftingRecipe(Ic2Items.weedEx, new Object[]{"R", "G", "C", Character.valueOf('R'), Item.redstone, Character.valueOf('G'), Ic2Items.grinPowder, Character.valueOf('C'), Ic2Items.cell});
      Ic2Recipes.addCraftingRecipe(Ic2Items.cropmatron, new Object[]{"cBc", "CMC", "CCC", Character.valueOf('M'), Ic2Items.machine, Character.valueOf('C'), Ic2Items.crop, Character.valueOf('c'), Ic2Items.electronicCircuit, Character.valueOf('B'), Block.chest});
      Ic2Recipes.addCraftingRecipe(new ItemStack(Ic2Items.mugEmpty.getItem()), new Object[]{"SS ", "SSS", "SS ", Character.valueOf('S'), Block.stone});
      Ic2Recipes.addShapelessCraftingRecipe(new ItemStack(Ic2Items.coffeePowder.getItem()), new Object[]{Ic2Items.coffeeBeans});
      Ic2Recipes.addShapelessCraftingRecipe(new ItemStack(Ic2Items.mugCoffee.getItem()), new Object[]{Ic2Items.mugEmpty, Ic2Items.coffeePowder, "liquid$" + Block.waterStill.blockID});
      Ic2Recipes.addShapelessCraftingRecipe(new ItemStack(Ic2Items.mugCoffee.getItem(), 1, 2), new Object[]{new ItemStack(Ic2Items.mugCoffee.getItem(), 1, 1), Item.sugar, Item.bucketMilk});
      if(Ic2Items.rubberWood != null) {
         Ic2Recipes.addCraftingRecipe(new ItemStack(Ic2Items.barrel.getItem()), new Object[]{"P", "W", "P", Character.valueOf('P'), Block.planks, Character.valueOf('W'), Ic2Items.rubberWood});
      }

      Ic2Recipes.addCraftingRecipe(new ItemStack(Ic2Items.mugEmpty.getItem()), new Object[]{"#", Character.valueOf('#'), new ItemStack(Ic2Items.mugBooze.getItem(), 1, -1)});
      Ic2Recipes.addCraftingRecipe(new ItemStack(Ic2Items.barrel.getItem()), new Object[]{"#", Character.valueOf('#'), new ItemStack(Ic2Items.barrel.getItem(), 1, -1)});
      Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.blackPainter, new Object[]{Ic2Items.painter, "dyeBlack"});
      Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.redPainter, new Object[]{Ic2Items.painter, "dyeRed"});
      Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.greenPainter, new Object[]{Ic2Items.painter, "dyeGreen"});
      Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.brownPainter, new Object[]{Ic2Items.painter, "dyeBrown"});
      Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.bluePainter, new Object[]{Ic2Items.painter, "dyeBlue"});
      Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.purplePainter, new Object[]{Ic2Items.painter, "dyePurple"});
      Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.cyanPainter, new Object[]{Ic2Items.painter, "dyeCyan"});
      Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.lightGreyPainter, new Object[]{Ic2Items.painter, "dyeLightGray"});
      Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.darkGreyPainter, new Object[]{Ic2Items.painter, "dyeGray"});
      Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.pinkPainter, new Object[]{Ic2Items.painter, "dyePink"});
      Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.limePainter, new Object[]{Ic2Items.painter, "dyeLime"});
      Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.yellowPainter, new Object[]{Ic2Items.painter, "dyeYellow"});
      Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.cloudPainter, new Object[]{Ic2Items.painter, "dyeLightBlue"});
      Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.magentaPainter, new Object[]{Ic2Items.painter, "dyeMagenta"});
      Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.orangePainter, new Object[]{Ic2Items.painter, "dyeOrange"});
      Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.whitePainter, new Object[]{Ic2Items.painter, "dyeWhite"});
      Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.painter, new Object[]{new ItemStack(Ic2Items.blackPainter.itemID, 1, -1)});
      Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.painter, new Object[]{new ItemStack(Ic2Items.redPainter.itemID, 1, -1)});
      Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.painter, new Object[]{new ItemStack(Ic2Items.greenPainter.itemID, 1, -1)});
      Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.painter, new Object[]{new ItemStack(Ic2Items.brownPainter.itemID, 1, -1)});
      Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.painter, new Object[]{new ItemStack(Ic2Items.bluePainter.itemID, 1, -1)});
      Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.painter, new Object[]{new ItemStack(Ic2Items.purplePainter.itemID, 1, -1)});
      Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.painter, new Object[]{new ItemStack(Ic2Items.cyanPainter.itemID, 1, -1)});
      Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.painter, new Object[]{new ItemStack(Ic2Items.lightGreyPainter.itemID, 1, -1)});
      Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.painter, new Object[]{new ItemStack(Ic2Items.darkGreyPainter.itemID, 1, -1)});
      Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.painter, new Object[]{new ItemStack(Ic2Items.pinkPainter.itemID, 1, -1)});
      Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.painter, new Object[]{new ItemStack(Ic2Items.limePainter.itemID, 1, -1)});
      Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.painter, new Object[]{new ItemStack(Ic2Items.yellowPainter.itemID, 1, -1)});
      Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.painter, new Object[]{new ItemStack(Ic2Items.cloudPainter.itemID, 1, -1)});
      Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.painter, new Object[]{new ItemStack(Ic2Items.magentaPainter.itemID, 1, -1)});
      Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.painter, new Object[]{new ItemStack(Ic2Items.orangePainter.itemID, 1, -1)});
      Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.painter, new Object[]{new ItemStack(Ic2Items.whitePainter.itemID, 1, -1)});
      Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.cell, 16), new Object[]{" T ", "T T", " T ", Character.valueOf('T'), "ingotTin"});
      Ic2Recipes.addCraftingRecipe(Ic2Items.fuelCan, new Object[]{" TT", "T T", "TTT", Character.valueOf('T'), "ingotTin"});
      Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.tinCan, 4), new Object[]{"T T", "TTT", Character.valueOf('T'), "ingotTin"});
      Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.waterCell, new Object[]{Ic2Items.cell, Item.bucketWater});
      Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.lavaCell, new Object[]{Ic2Items.cell, Item.bucketLava});
      Ic2Recipes.addShapelessCraftingRecipe(new ItemStack(Block.obsidian, 1), new Object[]{Ic2Items.waterCell, Ic2Items.waterCell, Ic2Items.lavaCell, Ic2Items.lavaCell});
      Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.hydratedCoalDust, new Object[]{"dustCoal", "liquid$" + Block.waterStill.blockID});
      Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.hydratedCoalCell, new Object[]{Ic2Items.cell, Ic2Items.hydratedCoalClump});
      Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.bioCell, new Object[]{Ic2Items.cell, Ic2Items.compressedPlantBall});
      Ic2Recipes.addCraftingRecipe(new ItemStack(Ic2Items.cfPack.itemID, 1, 259), new Object[]{"SCS", "FTF", "F F", Character.valueOf('T'), "ingotTin", Character.valueOf('C'), Ic2Items.electronicCircuit, Character.valueOf('F'), Ic2Items.fuelCan, Character.valueOf('S'), new ItemStack(Ic2Items.constructionFoamSprayer.itemID, 1, 1601)});
      Ic2Recipes.addShapelessCraftingRecipe(StackUtil.copyWithSize(Ic2Items.constructionFoam, 3), new Object[]{"dustClay", "liquid$" + Block.waterStill.blockID, Item.redstone, "dustCoal"});
      Ic2Recipes.addShapelessCraftingRecipe(new ItemStack(Item.diamond), new Object[]{Ic2Items.industrialDiamond});
      Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.mixedMetalIngot, 2), new Object[]{"III", "BBB", "TTT", Character.valueOf('I'), "ingotRefinedIron", Character.valueOf('B'), "ingotBronze", Character.valueOf('T'), "ingotTin"});
      Ic2Recipes.addCraftingRecipe(Ic2Items.remote, new Object[]{" C ", "TLT", " F ", Character.valueOf('C'), Ic2Items.insulatedCopperCableItem, Character.valueOf('F'), Ic2Items.frequencyTransmitter, Character.valueOf('L'), new ItemStack(Item.dyePowder, 1, 4), Character.valueOf('T'), "ingotTin"});
   }

   public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
      int baseScale;
      if(enableWorldGenTreeRubber) {
         BiomeGenBase baseHeight = world.getWorldChunkManager().getBiomeGenAt(chunkX * 16 + 16, chunkZ * 16 + 16);
         if(baseHeight != null && baseHeight.biomeName != null) {
            baseScale = 0;
            if(baseHeight.biomeName.toLowerCase().contains("taiga")) {
               baseScale += random.nextInt(3);
            }

            if(baseHeight.biomeName.toLowerCase().contains("forest") || baseHeight.biomeName.toLowerCase().contains("jungle")) {
               baseScale += random.nextInt(5) + 1;
            }

            if(baseHeight.biomeName.toLowerCase().contains("swamp")) {
               baseScale += random.nextInt(10) + 5;
            }

            if(random.nextInt(100) + 1 <= baseScale * 2) {
               (new WorldGenRubTree()).generate(world, random, chunkX * 16 + random.nextInt(16), baseScale, chunkZ * 16 + random.nextInt(16));
            }
         }
      }

      int var15 = getSeaLevel(world) + 1;
      baseScale = Math.round((float)var15 * oreDensityFactor);
      int baseCount;
      int count;
      int n;
      int x;
      int y;
      int z;
      if(enableWorldGenOreCopper && Ic2Items.copperOre != null) {
         baseCount = 15 * baseScale / 64;
         count = (int)Math.round(random.nextGaussian() * Math.sqrt((double)baseCount) + (double)baseCount);

         for(n = 0; n < count; ++n) {
            x = chunkX * 16 + random.nextInt(16);
            y = random.nextInt(40 * var15 / 64) + random.nextInt(20 * var15 / 64) + 10 * var15 / 64;
            z = chunkZ * 16 + random.nextInt(16);
            (new WorldGenMinable(Ic2Items.copperOre.itemID, 10)).generate(world, random, x, y, z);
         }
      }

      if(enableWorldGenOreTin && Ic2Items.tinOre != null) {
         baseCount = 25 * baseScale / 64;
         count = (int)Math.round(random.nextGaussian() * Math.sqrt((double)baseCount) + (double)baseCount);

         for(n = 0; n < count; ++n) {
            x = chunkX * 16 + random.nextInt(16);
            y = random.nextInt(40 * var15 / 64);
            z = chunkZ * 16 + random.nextInt(16);
            (new WorldGenMinable(Ic2Items.tinOre.itemID, 6)).generate(world, random, x, y, z);
         }
      }

      if(enableWorldGenOreUranium && Ic2Items.uraniumOre != null) {
         baseCount = 20 * baseScale / 64;
         count = (int)Math.round(random.nextGaussian() * Math.sqrt((double)baseCount) + (double)baseCount);

         for(n = 0; n < count; ++n) {
            x = chunkX * 16 + random.nextInt(16);
            y = random.nextInt(64 * var15 / 64);
            z = chunkZ * 16 + random.nextInt(16);
            (new WorldGenMinable(Ic2Items.uraniumOre.itemID, 3)).generate(world, random, x, y, z);
         }
      }

   }

   public void tickStart(EnumSet type, Object ... tickData) {
      if(type.contains(TickType.WORLD)) {
         platform.profilerStartSection("Init");
         World player = (World)tickData[0];
         WorldData needsInventoryUpdate = WorldData.get(player);
         platform.profilerEndStartSection("Wind");
         if(windTicker % 128 == 0) {
            updateWind(player);
         }

         ++windTicker;
         ++textureIndex.t;
         platform.profilerEndStartSection("EnergyNet");
         EnergyNet.onTick(player);
         platform.profilerEndStartSection("Networking");
         network.onTick(player);
         platform.profilerEndStartSection("SingleTickCallback");

         for(ITickCallback i = (ITickCallback)needsInventoryUpdate.singleTickCallbacks.poll(); i != null; i = (ITickCallback)needsInventoryUpdate.singleTickCallbacks.poll()) {
            platform.profilerStartSection(i.getClass().getName());
            i.tickCallback(player);
            platform.profilerEndSection();
         }

         platform.profilerEndStartSection("ContTickCallback");
         needsInventoryUpdate.continuousTickCallbacksInUse = true;
         Iterator var30 = needsInventoryUpdate.continuousTickCallbacks.iterator();

         while(var30.hasNext()) {
            ITickCallback worldId = (ITickCallback)var30.next();
            platform.profilerStartSection(worldId.getClass().getName());
            worldId.tickCallback(player);
            platform.profilerEndSection();
         }

         needsInventoryUpdate.continuousTickCallbacksInUse = false;
         needsInventoryUpdate.continuousTickCallbacks.addAll(needsInventoryUpdate.continuousTickCallbacksToAdd);
         needsInventoryUpdate.continuousTickCallbacksToAdd.clear();
         needsInventoryUpdate.continuousTickCallbacks.removeAll(needsInventoryUpdate.continuousTickCallbacksToRemove);
         needsInventoryUpdate.continuousTickCallbacksToRemove.clear();
         platform.profilerEndSection();
      }

      int var29;
      if(type.contains(TickType.WORLDLOAD) && platform.isSimulating()) {
         Integer[] var25 = DimensionManager.getIDs();
         int var27 = var25.length;

         for(var29 = 0; var29 < var27; ++var29) {
            int var31 = var25[var29].intValue();
            World world = DimensionManager.getProvider(var31).worldObj;
            if(world != null && world.getSaveHandler() instanceof SaveHandler) {
               SaveHandler saveHandler = (SaveHandler)world.getSaveHandler();
               File saveFolder = null;
               Field[] e = SaveHandler.class.getDeclaredFields();
               int mapIdPropertiesFile = e.length;

               for(int fileOutputStream = 0; fileOutputStream < mapIdPropertiesFile; ++fileOutputStream) {
                  Field properties = e[fileOutputStream];
                  if(properties.getType() == File.class) {
                     properties.setAccessible(true);

                     try {
                        File outdatedProperties = (File)properties.get(saveHandler);
                        if(saveFolder == null || saveFolder.getParentFile() == outdatedProperties) {
                           saveFolder = outdatedProperties;
                        }
                     } catch (Exception var24) {
                        ;
                     }
                  }
               }

               if(saveFolder != null) {
                  if(!MinecraftServer.getServer().isDedicatedServer()) {
                     File var33 = new File(MinecraftServer.getServer().isDedicatedServer()?".":"saves", "ic2_disclaimer_shown");
                     if(!var33.exists()) {
                        try {
                           FileOutputStream var32 = new FileOutputStream(var33);
                           var32.write(0);
                           var32.flush();
                           var32.close();
                        } catch (Throwable var22) {
                           ;
                        }
                     }
                  }

                  try {
                     Properties var34 = new Properties() {
                        public Set keySet() {
                           return Collections.unmodifiableSet(new TreeSet(super.keySet()));
                        }
                        public synchronized Enumeration keys() {
                           return Collections.enumeration(new TreeSet(super.keySet()));
                        }
                     };
                     var34.putAll(runtimeIdProperties);
                     File var36 = new File(saveFolder, "ic2_map.cfg");
                     if(var36.exists()) {
                        FileInputStream var35 = new FileInputStream(var36);
                        Properties var38 = new Properties();
                        var38.load(var35);
                        var35.close();
                        Vector var39 = new Vector();
                        Iterator i$ = var38.entrySet().iterator();

                        while(i$.hasNext()) {
                           Entry key = (Entry)i$.next();
                           String key1 = (String)key.getKey();
                           String value = (String)key.getValue();
                           if(!runtimeIdProperties.containsKey(key1)) {
                              var39.add(key1);
                           } else {
                              int separatorPos = key1.indexOf(46);
                              if(separatorPos != -1) {
                                 String section = key1.substring(0, separatorPos);
                                 String entry = key1.substring(separatorPos + 1);
                                 if((section.equals("block") || section.equals("item")) && !value.equals(runtimeIdProperties.get(key1))) {
                                    platform.displayError("IC2 detected an ID conflict between your IC2.cfg and the map you are\ntrying to load.\n\nMap: " + saveFolder.getName() + "\n" + "\n" + "Config section: " + section + "\n" + "Config entry: " + entry + "\n" + "Config value: " + runtimeIdProperties.get(key1) + "\n" + "Map value: " + value + "\n" + "\n" + "Adjust your config to match the IDs used by the map or convert your\n" + "map to use the IDs specified in the config.\n" + "\n" + "See also: config/IC2.cfg " + (platform.isRendering()?"saves/":"") + saveFolder.getName() + "/ic2_map.cfg");
                                 }
                              }
                           }
                        }

                        i$ = var39.iterator();

                        while(i$.hasNext()) {
                           String var40 = (String)i$.next();
                           var38.remove(var40);
                        }

                        var34.putAll(var38);
                     }

                     FileOutputStream var37 = new FileOutputStream(var36);
                     var34.store(var37, "ic2 map related configuration data");
                     var37.close();
                  } catch (IOException var23) {
                     var23.printStackTrace();
                  }
                  break;
               }
            }
         }
      }

      if(type.contains(TickType.PLAYER)) {
         EntityPlayer var26 = (EntityPlayer)tickData[0];
         if(var26.isDead) {
            return;
         }

         platform.profilerStartSection("ArmorTick");
         boolean var28 = false;

         for(var29 = 0; var29 < 4; ++var29) {
            if(var26.inventory.armorInventory[var29] != null && var26.inventory.armorInventory[var29].getItem() instanceof IItemTickListener && ((IItemTickListener)var26.inventory.armorInventory[var29].getItem()).onTick(var26, var26.inventory.armorInventory[var29])) {
               var28 = true;
            }
         }

         if(var28) {
            var26.openContainer.detectAndSendChanges();
         }

         platform.profilerEndStartSection("NanoSaber");
         ItemNanoSaber.timedLoss(var26);
         platform.profilerEndSection();
      }

   }

   public void tickEnd(EnumSet type, Object ... tickData) {}

   public EnumSet ticks() {
      return EnumSet.of(TickType.WORLD, TickType.WORLDLOAD, TickType.PLAYER);
   }

   public String getLabel() {
      return "IC2";
   }

   public void playerLoggedIn(Player player, NetHandler netHandler, INetworkManager manager) {}

   public String connectionReceived(NetLoginHandler netHandler, INetworkManager manager) {
      return null;
   }

   public void connectionOpened(NetHandler netClientHandler, String server, int port, INetworkManager manager) {}

   public void connectionOpened(NetHandler netClientHandler, MinecraftServer server, INetworkManager manager) {}

   public void connectionClosed(INetworkManager manager) {}

   public void clientLoggedIn(NetHandler clientHandler, INetworkManager manager, Packet1Login login) {
      network.sendLoginData();
   }

   public void onPlayerLogin(EntityPlayer player) {
      if(showDisclaimer && platform.isSimulating()) {
         showDisclaimer = false;
         ItemStack book = new ItemStack(Item.writtenBook);
         book.stackTagCompound = new NBTTagCompound();
         NBTTagList pages = new NBTTagList();
         pages.appendTag(new NBTTagString("", "Welcome to the IC2 Open Beta version 1.115.207-lf.\n \nThe next page has important information on how to use this beta properly. This disclaimer will only be shown once.\n \nThank you for your continued support!"));
         pages.appendTag(new NBTTagString("", "- If you find a bug, please post it in the beta topic, NOT on the Bugs forum.\n- If you have a critical (server crashing or duplication) bug, notify a team member in the beta topic, which will allow you to PM the information.\n\n\t\t (continued...)"));
         pages.appendTag(new NBTTagString("", "- New content may not be final and undergo balancing or other changes before reaching a final release, or may not even make it to the final release."));
         book.stackTagCompound.setTag("pages", pages);
         book.stackTagCompound.setString("title", "IC2 Open Beta Information");
         book.stackTagCompound.setString("author", "IndustrialCraft 2 Team");
         player.inventory.addItemStackToInventory(book);
      }

   }

   public void onPlayerLogout(EntityPlayer player) {
      if(platform.isSimulating()) {
         ItemArmorQuantumSuit.removePlayerReferences(player);
         keyboard.removePlayerReferences(player);
      }

   }

   public void onPlayerChangedDimension(EntityPlayer player) {}

   public void onPlayerRespawn(EntityPlayer player) {}

   @ForgeSubscribe
   public void onWorldLoad(Load event) {
      textureIndex.reset();
   }

   @ForgeSubscribe
   public void onWorldUnload(Unload event) {
      WorldData.onWorldUnload(event.world);
   }

   public static void addSingleTickCallback(World world, ITickCallback tickCallback) {
      WorldData worldData = WorldData.get(world);
      worldData.singleTickCallbacks.add(tickCallback);
   }

   public static void addContinuousTickCallback(World world, ITickCallback tickCallback) {
      WorldData worldData = WorldData.get(world);
      if(!worldData.continuousTickCallbacksInUse) {
         worldData.continuousTickCallbacks.add(tickCallback);
      } else {
         worldData.continuousTickCallbacksToRemove.remove(tickCallback);
         worldData.continuousTickCallbacksToAdd.add(tickCallback);
      }

   }

   public static void removeContinuousTickCallback(World world, ITickCallback tickCallback) {
      WorldData worldData = WorldData.get(world);
      if(!worldData.continuousTickCallbacksInUse) {
         worldData.continuousTickCallbacks.remove(tickCallback);
      } else {
         worldData.continuousTickCallbacksToAdd.remove(tickCallback);
         worldData.continuousTickCallbacksToRemove.add(tickCallback);
      }

   }

   public static void updateWind(World world) {
      if(world.provider.dimensionId == 0) {
         int upChance = 10;
         int downChance = 10;
         if(windStrength > 20) {
            upChance -= windStrength - 20;
         }

         if(windStrength < 10) {
            downChance -= 10 - windStrength;
         }

         if(random.nextInt(100) <= upChance) {
            ++windStrength;
         } else if(random.nextInt(100) <= downChance) {
            --windStrength;
         }
      }
   }

   public static int getBlockIdFor(Configuration config, String item, int standardId) {
      Integer ret;
      if(config == null) {
         ret = Integer.valueOf(standardId);
      } else {
         Property prop = config.get("block", item, standardId);
         ret = new Integer(prop.value);
         if(enableDynamicIdAllocation && (Block.blocksList[ret.intValue()] != null || ret.intValue() >= 256 && Item.itemsList[ret.intValue() - 256] != null)) {
            for(int blockId = Block.blocksList.length - 1; blockId > 0; --blockId) {
               if(Block.blocksList[blockId] == null && (blockId < 256 || Item.itemsList[blockId - 256] == null)) {
                  prop.value = Integer.toString(blockId);
                  ret = Integer.valueOf(blockId);
                  break;
               }
            }
         }
      }

      if(ret.intValue() <= 0 || ret.intValue() > Block.blocksList.length) {
         platform.displayError("An invalid block ID has been detected on your IndustrialCraft 2\nconfiguration file. Block IDs cannot be higher than " + (Block.blocksList.length - 1) + ".\n" + "\n" + "Block with invalid ID: " + item + "\n" + "Invalid ID: " + ret);
      }

      runtimeIdProperties.setProperty("block." + item, ret.toString());
      return ret.intValue();
   }

   public static int getItemIdFor(Configuration config, String item, int standardId) {
      Integer ret;
      if(config == null) {
         ret = Integer.valueOf(standardId);
      } else {
         try {
            Property e = config.get("item", item, standardId);
            ret = new Integer(e.value);
            if(enableDynamicIdAllocation && (ret.intValue() + 256 < Block.blocksList.length && Block.blocksList[ret.intValue() + 256] != null || Item.itemsList[ret.intValue()] != null)) {
               for(int itemId = Item.itemsList.length - 1; itemId > 0; --itemId) {
                  if((itemId >= Block.blocksList.length - 256 || Block.blocksList[itemId + 256] == null) && Item.itemsList[itemId] == null) {
                     e.value = Integer.toString(itemId);
                     ret = Integer.valueOf(itemId);
                     break;
                  }
               }
            }
         } catch (Exception var6) {
            log.warning("Error while trying to access ID-List, config wasn\'t loaded properly!");
            ret = Integer.valueOf(standardId);
         }
      }

      if(ret.intValue() < 0 || ret.intValue() > Item.itemsList.length) {
         platform.displayError("An invalid item ID has been detected on your IndustrialCraft 2\nconfiguration file. Item IDs cannot be higher than " + (Item.itemsList.length - 1) + ".\n" + "\n" + "Item with invalid ID: " + item + "\n" + "Invalid ID: " + ret);
      }

      runtimeIdProperties.setProperty("item." + item, ret.toString());
      return ret.intValue();
   }

   public static void explodeMachineAt(World world, int x, int y, int z) {
      world.setBlockWithNotify(x, y, z, 0);
      ExplosionIC2 explosion = new ExplosionIC2(world, (Entity)null, 0.5D + (double)x, 0.5D + (double)y, 0.5D + (double)z, 2.5F, 0.75F, 0.75F);
      explosion.doExplosion();
   }

   public static int getSeaLevel(World world) {
      return world.provider.getAverageGroundLevel();
   }

   public static int getWorldHeight(World world) {
      return world.getHeight();
   }

   public static void addValuableOre(int blockId, int value) {
      addValuableOre(blockId, -1, value);
   }

   public static void addValuableOre(int blockId, int metaData, int value) {
      if(valuableOres.containsKey(Integer.valueOf(blockId))) {
         Map metaMap = (Map)valuableOres.get(Integer.valueOf(blockId));
         if(metaMap.containsKey(Integer.valueOf(-1))) {
            return;
         }

         if(metaData == -1) {
            metaMap.clear();
            metaMap.put(Integer.valueOf(-1), Integer.valueOf(value));
         } else if(!metaMap.containsKey(Integer.valueOf(metaData))) {
            metaMap.put(Integer.valueOf(metaData), Integer.valueOf(value));
         }
      } else {
         TreeMap metaMap1 = new TreeMap();
         metaMap1.put(Integer.valueOf(metaData), Integer.valueOf(value));
         valuableOres.put(Integer.valueOf(blockId), metaMap1);
      }

   }

   private static String getValuableOreString() {
      StringBuilder ret = new StringBuilder();
      boolean first = true;
      Iterator i$ = valuableOres.entrySet().iterator();

      while(i$.hasNext()) {
         Entry entry = (Entry)i$.next();
         Iterator i$1 = ((Map)entry.getValue()).entrySet().iterator();

         while(i$1.hasNext()) {
            Entry entry2 = (Entry)i$1.next();
            if(first) {
               first = false;
            } else {
               ret.append(", ");
            }

            ret.append(entry.getKey());
            if(((Integer)entry2.getKey()).intValue() != -1) {
               ret.append("-");
               ret.append(entry2.getKey());
            }

            ret.append(":");
            ret.append(entry2.getValue());
         }
      }

      return ret.toString();
   }

   private static void setValuableOreFromString(String str) {
      valuableOres.clear();
      String[] strParts = str.trim().split("\\s*,\\s*");
      String[] arr$ = strParts;
      int len$ = strParts.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         String strPart = arr$[i$];
         String[] idMetaValue = strPart.split("\\s*:\\s*");
         String[] idMeta = idMetaValue[0].split("\\s*-\\s*");
         if(idMeta[0].length() != 0) {
            int blockId = Integer.parseInt(idMeta[0]);
            int metaData = -1;
            int value = 1;
            if(idMeta.length == 2) {
               metaData = Integer.parseInt(idMeta[1]);
            }

            if(idMetaValue.length == 2) {
               value = Integer.parseInt(idMetaValue[1]);
            }

            addValuableOre(blockId, metaData, value);
         }
      }

   }

   @ForgeSubscribe
   public void registerOre(OreRegisterEvent event) {
      String oreClass = event.Name;
      ItemStack ore = event.Ore;
      if(oreClass.equals("ingotCopper")) {
         Ic2Recipes.addMaceratorRecipe(ore, Ic2Items.copperDust);
         Ic2Recipes.addCompressorRecipe(StackUtil.copyWithSize(ore, 8), Ic2Items.denseCopperPlate);
      } else if(oreClass.equals("ingotRefinedIron")) {
         Ic2Recipes.addMaceratorRecipe(ore, Ic2Items.ironDust);
      } else if(oreClass.equals("ingotSilver")) {
         Ic2Recipes.addMaceratorRecipe(ore, Ic2Items.silverDust);
         if(!silverDustSmeltingRegistered) {
            FurnaceRecipes.smelting().addSmelting(Ic2Items.silverDust.itemID, Ic2Items.silverDust.getItemDamage(), ore, 0.8F);
            silverDustSmeltingRegistered = true;
         }
      } else if(oreClass.equals("ingotTin")) {
         Ic2Recipes.addMaceratorRecipe(ore, Ic2Items.tinDust);
      } else if(oreClass.equals("dropUranium")) {
         Ic2Recipes.addCompressorRecipe(ore, Ic2Items.uraniumIngot);
      } else if(oreClass.equals("oreCopper")) {
         Ic2Recipes.addMaceratorRecipe(ore, StackUtil.copyWithSize(Ic2Items.copperDust, 2));
         addValuableOre(ore.itemID, ore.getItemDamage(), 2);
      } else if(!oreClass.equals("oreGemRuby") && !oreClass.equals("oreGemGreenSapphire") && !oreClass.equals("oreGemSapphire")) {
         if(oreClass.equals("oreSilver")) {
            Ic2Recipes.addMaceratorRecipe(ore, StackUtil.copyWithSize(Ic2Items.silverDust, 2));
            addValuableOre(ore.itemID, ore.getItemDamage(), 3);
         } else if(oreClass.equals("oreTin")) {
            Ic2Recipes.addMaceratorRecipe(ore, StackUtil.copyWithSize(Ic2Items.tinDust, 2));
            addValuableOre(ore.itemID, ore.getItemDamage(), 2);
         } else if(oreClass.equals("oreUranium")) {
            Ic2Recipes.addCompressorRecipe(ore, Ic2Items.uraniumIngot);
            addValuableOre(ore.itemID, ore.getItemDamage(), 4);
         } else if(oreClass.equals("oreTungsten")) {
            addValuableOre(ore.itemID, ore.getItemDamage(), 5);
         } else if(oreClass.equals("woodRubber")) {
            Ic2Recipes.addExtractorRecipe(ore, Ic2Items.rubber);
         } else if(oreClass.startsWith("ore")) {
            addValuableOre(ore.itemID, ore.getItemDamage(), 1);
         }
      } else {
         addValuableOre(ore.itemID, ore.getItemDamage(), 4);
      }

   }

   @ForgeSubscribe
   @SuppressWarnings("deprecation")
   public void onLivingSpecialSpawn(LivingSpecialSpawnEvent event) {
      if(seasonal && (event.entityLiving instanceof EntityZombie || event.entityLiving instanceof EntitySkeleton) && event.entityLiving.worldObj.rand.nextFloat() < 0.1F) {
         try {
            dropChances.set(event.entityLiving, new float[]{-1.0F, -1.0F, -1.0F, -1.0F, -1.0F});
         } catch (Throwable var3) {
            throw new RuntimeException(var3);
         }

         if(event.entityLiving.worldObj.rand.nextFloat() < 0.1F) {
            if(event.entityLiving instanceof EntityZombie) {
               event.entityLiving.setCurrentItemOrArmor(0, Ic2Items.enabledNanoSaber.copy());
            }

            event.entityLiving.setCurrentItemOrArmor(1, Ic2Items.quantumHelmet.copy());
            event.entityLiving.setCurrentItemOrArmor(2, Ic2Items.quantumBodyarmor.copy());
            event.entityLiving.setCurrentItemOrArmor(3, Ic2Items.quantumLeggings.copy());
            event.entityLiving.setCurrentItemOrArmor(4, Ic2Items.quantumBoots.copy());
         } else {
            if(event.entityLiving instanceof EntityZombie) {
               event.entityLiving.setCurrentItemOrArmor(0, Ic2Items.nanoSaber.copy());
            }

            event.entityLiving.setCurrentItemOrArmor(1, Ic2Items.nanoHelmet.copy());
            event.entityLiving.setCurrentItemOrArmor(2, Ic2Items.nanoBodyarmor.copy());
            event.entityLiving.setCurrentItemOrArmor(3, Ic2Items.nanoLeggings.copy());
            event.entityLiving.setCurrentItemOrArmor(4, Ic2Items.nanoBoots.copy());
         }
      }

   }

   static {
      addValuableOre(Block.oreCoal.blockID, 1);
      addValuableOre(Block.oreGold.blockID, 3);
      addValuableOre(Block.oreRedstone.blockID, 3);
      addValuableOre(Block.oreLapis.blockID, 3);
      addValuableOre(Block.oreIron.blockID, 4);
      addValuableOre(Block.oreDiamond.blockID, 5);
      addValuableOre(Block.oreEmerald.blockID, 5);
      enableCraftingBucket = true;
      enableCraftingCoin = true;
      enableCraftingGlowstoneDust = true;
      enableCraftingGunpowder = true;
      enableCraftingITnt = true;
      enableCraftingNuke = true;
      enableCraftingRail = true;
      enableDynamicIdAllocation = true;
      enableLoggingWrench = true;
      enableSecretRecipeHiding = true;
      enableQuantumSpeedOnSprint = true;
      enableMinerLapotron = false;
      enableTeleporterInventory = true;
      enableBurningScrap = true;
      enableWorldGenTreeRubber = true;
      enableWorldGenOreCopper = true;
      enableWorldGenOreTin = true;
      enableWorldGenOreUranium = true;
      explosionPowerNuke = 35.0F;
      explosionPowerReactorMax = 45.0F;
      energyGeneratorBase = 10;
      energyGeneratorGeo = 20;
      energyGeneratorWater = 100;
      energyGeneratorSolar = 100;
      energyGeneratorWind = 100;
      energyGeneratorNuclear = 5;
      suddenlyHoes = false;
      seasonal = false;
      showDisclaimer = false;
      enableSteamReactor = false;
      oreDensityFactor = 1.0F;
      initialized = false;
      runtimeIdProperties = new Properties();
      tabIC2 = new CreativeTabIC2();
      silverDustSmeltingRegistered = false;
   }
}
