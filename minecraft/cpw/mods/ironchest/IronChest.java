package cpw.mods.ironchest;

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
import cpw.mods.ironchest.BlockIronChest;
import cpw.mods.ironchest.ChestChangerType;
import cpw.mods.ironchest.CommonProxy;
import cpw.mods.ironchest.IronChestType;
import cpw.mods.ironchest.ItemIronChest;
import cpw.mods.ironchest.OcelotsSitOnChestsHandler;
import cpw.mods.ironchest.PacketHandler;
import cpw.mods.ironchest.Version;
import java.util.logging.Level;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;

@Mod(
   modid = "IronChest",
   name = "Iron Chests",
   dependencies = "required-after:Forge@[6.5,);required-after:FML@[4.7.22,)"
)
@NetworkMod(
   channels = {"IronChest"},
   versionBounds = "[5.1,)",
   clientSideRequired = true,
   serverSideRequired = false,
   packetHandler = PacketHandler.class
)
public class IronChest {

   public static BlockIronChest ironChestBlock;
   @SidedProxy(
      clientSide = "cpw.mods.ironchest.client.ClientProxy",
      serverSide = "cpw.mods.ironchest.CommonProxy"
   )
   public static CommonProxy proxy;
   @Instance("IronChest")
   public static IronChest instance;
   public static boolean CACHE_RENDER = true;
   public static boolean OCELOTS_SITONCHESTS = true;
   private int blockId;

   @PreInit
   public void preInit(FMLPreInitializationEvent event) {
      Version.init(event.getVersionProperties());
      event.getModMetadata().version = Version.fullVersionString();
      Configuration cfg = new Configuration(event.getSuggestedConfigurationFile());

      try {
         cfg.load();
         this.blockId = cfg.getBlock("ironChests", 975).getInt(975);
         ChestChangerType.buildItems(cfg, 19501);
         CACHE_RENDER = cfg.get("general", "cacheRenderingInformation", true).getBoolean(true);
         OCELOTS_SITONCHESTS = cfg.get("general", "ocelotsSitOnChests", true).getBoolean(true);
      } catch (Exception var7) {
         FMLLog.log(Level.SEVERE, var7, "IronChest has a problem loading it\'s configuration", new Object[0]);
      } finally {
         cfg.save();
      }

   }

   @Init
   public void load(FMLInitializationEvent evt) {
      ironChestBlock = new BlockIronChest(this.blockId);
      GameRegistry.registerBlock(ironChestBlock, ItemIronChest.class, "BlockIronChest");
      IronChestType[] arr$ = IronChestType.values();
      int len$ = arr$.length;

      int i$;
      for(i$ = 0; i$ < len$; ++i$) {
         IronChestType typ = arr$[i$];
         GameRegistry.registerTileEntityWithAlternatives(typ.clazz, "IronChest." + typ.name(), new String[]{typ.name()});
         LanguageRegistry.instance().addStringLocalization(typ.name() + ".name", "en_US", typ.friendlyName);
         proxy.registerTileEntitySpecialRenderer(typ);
      }

      ChestChangerType[] var6 = ChestChangerType.values();
      len$ = var6.length;

      for(i$ = 0; i$ < len$; ++i$) {
         ChestChangerType var7 = var6[i$];
         LanguageRegistry.instance().addStringLocalization("item." + var7.itemName + ".name", "en_US", var7.descriptiveName);
      }

      IronChestType.generateTieredRecipes(ironChestBlock);
      ChestChangerType.generateRecipes();
      NetworkRegistry.instance().registerGuiHandler(instance, proxy);
      proxy.registerRenderInformation();
      if(OCELOTS_SITONCHESTS) {
         MinecraftForge.EVENT_BUS.register(new OcelotsSitOnChestsHandler());
      }

      MinecraftForge.EVENT_BUS.register(this);
   }

   @PostInit
   public void modsLoaded(FMLPostInitializationEvent evt) {}

}
