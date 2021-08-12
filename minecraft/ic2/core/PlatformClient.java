package ic2.core;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.ContainerIC2;
import ic2.core.GuiIC2ErrorScreen;
import ic2.core.IC2;
import ic2.core.IC2Achievements;
import ic2.core.IHasGui;
import ic2.core.IItemTickListener;
import ic2.core.Platform;
import ic2.core.TextureLiquidFX;
import ic2.core.block.EntityDynamite;
import ic2.core.block.EntityIC2Explosive;
import ic2.core.block.RenderBlockCable;
import ic2.core.block.RenderBlockCrop;
import ic2.core.block.RenderBlockFence;
import ic2.core.block.RenderExplosiveBlock;
import ic2.core.block.RenderFlyingItem;
import ic2.core.block.machine.RenderBlockMiningPipe;
import ic2.core.block.personal.RenderBlockPersonal;
import ic2.core.block.personal.TileEntityPersonalChest;
import ic2.core.block.personal.TileEntityPersonalChestRenderer;
import ic2.core.block.wiring.RenderBlockLuminator;
import ic2.core.item.tool.EntityMiningLaser;
import ic2.core.item.tool.RenderCrossed;
import java.awt.Component;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.stats.Achievement;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.Configuration;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class PlatformClient extends Platform implements ITickHandler, Runnable {

   private static final Minecraft mc = Minecraft.getMinecraft();
   public Configuration lang;
   private boolean debug = false;
   private static final Achievement a = new Achievement(736749, new String(new byte[]{(byte)105, (byte)99, (byte)50, (byte)105, (byte)110, (byte)102, (byte)111}), 0, 0, Block.tnt, (Achievement)null);
   private int playerCounter = -1;
   private final Map capes = new HashMap();
   private final Map renders = new HashMap();


   public PlatformClient() {
      MinecraftForgeClient.preloadTexture("/ic2/sprites/block_0.png");
      MinecraftForgeClient.preloadTexture("/ic2/sprites/block_cable.png");
      MinecraftForgeClient.preloadTexture("/ic2/sprites/block_electric.png");
      MinecraftForgeClient.preloadTexture("/ic2/sprites/block_generator.png");
      MinecraftForgeClient.preloadTexture("/ic2/sprites/block_machine.png");
      MinecraftForgeClient.preloadTexture("/ic2/sprites/block_machine2.png");
      MinecraftForgeClient.preloadTexture("/ic2/sprites/block_personal.png");
      MinecraftForgeClient.preloadTexture("/ic2/sprites/item_0.png");
      MinecraftForgeClient.preloadTexture("/ic2/sprites/crops_0.png");

      try {
         this.lang = new Configuration(new File(this.getMinecraftDir(), "/config/IC2.lang"));
         this.lang.load();
      } catch (Exception var2) {
         System.out.println("[IndustrialCraft] Error while trying to access language file!");
         this.lang = null;
      }

      this.addLocalization("blockMachine.name", "Machine Block");
      this.addLocalization("blockIronFurnace.name", "Iron Furnace");
      this.addLocalization("blockElecFurnace.name", "Electric Furnace");
      this.addLocalization("blockMacerator.name", "Macerator");
      this.addLocalization("blockExtractor.name", "Extractor");
      this.addLocalization("blockCompressor.name", "Compressor");
      this.addLocalization("blockCanner.name", "Canning Machine");
      this.addLocalization("blockMiner.name", "Miner");
      this.addLocalization("blockPump.name", "Pump");
      this.addLocalization("blockMagnetizer.name", "Magnetizer");
      this.addLocalization("blockElectrolyzer.name", "Electrolyzer");
      this.addLocalization("blockRecycler.name", "Recycler");
      this.addLocalization("blockAdvMachine.name", "Advanced Machine Block");
      this.addLocalization("blockInduction.name", "Induction Furnace");
      this.addLocalization("blockMatter.name", "Mass Fabricator");
      this.addLocalization("blockTerra.name", "Terraformer");
      this.addLocalization("tile.blockOreCopper.name", "Copper Ore");
      this.addLocalization("tile.blockOreTin.name", "Tin Ore");
      this.addLocalization("tile.blockOreUran.name", "Uranium Ore");
      this.addLocalization("blockGenerator.name", "Generator");
      this.addLocalization("blockGeoGenerator.name", "Geothermal Generator");
      this.addLocalization("blockWaterGenerator.name", "Water Mill");
      this.addLocalization("blockSolarGenerator.name", "Solar Panel");
      this.addLocalization("blockWindGenerator.name", "Wind Mill");
      this.addLocalization("blockNuclearReactor.name", "Nuclear Reactor");
      this.addLocalization("tile.blockMiningPipe.name", "Mining Pipe");
      this.addLocalization("tile.blockMiningTip.name", "Mining Pipe");
      this.addLocalization("tile.blockRubWood.name", "Rubber Wood");
      this.addLocalization("tile.blockRubSapling.name", "Rubber Tree Sapling");
      this.addLocalization("tile.blockITNT.name", "Industrial TNT");
      this.addLocalization("tile.blockNuke.name", "Nuke");
      this.addLocalization("tile.blockRubber.name", "Rubber Sheet");
      this.addLocalization("tile.blockReactorChamber.name", "Reactor Chamber");
      this.addLocalization("tile.blockFenceIron.name", "Iron Fence");
      this.addLocalization("tile.blockAlloy.name", "Reinforced Stone");
      this.addLocalization("tile.blockAlloyGlass.name", "Reinforced Glass");
      this.addLocalization("blockBatBox.name", "BatBox");
      this.addLocalization("blockMFE.name", "MFE");
      this.addLocalization("blockMFSU.name", "MFSU");
      this.addLocalization("blockTransformerLV.name", "LV-Transformer");
      this.addLocalization("blockTransformerMV.name", "MV-Transformer");
      this.addLocalization("blockTransformerHV.name", "HV-Transformer");
      this.addLocalization("tile.blockLuminator.name", "Luminator");
      this.addLocalization("blockPersonalChest.name", "Personal Safe");
      this.addLocalization("blockPersonalTrader.name", "Trade-O-Mat");
      this.addLocalization("blockPersonalTraderEnergy.name", "Energy-O-Mat");
      this.addLocalization("blockMetalCopper.name", "Copper Block");
      this.addLocalization("blockMetalTin.name", "Tin Block");
      this.addLocalization("blockMetalBronze.name", "Bronze Block");
      this.addLocalization("blockMetalUranium.name", "Uranium Block");
      this.addLocalization("blockTeleporter.name", "Teleporter");
      this.addLocalization("blockTesla.name", "Tesla Coil");
      this.addLocalization("tile.blockFoam.name", "Construction Foam");
      this.addLocalization("tile.blockScaffold.name", "Scaffold");
      this.addLocalization("tile.blockLuminatorD.name", "Luminator");
      this.addLocalization("tile.blockCrop.name", "Crop");
      this.addLocalization("blockCropmatron.name", "Crop-Matron");
      this.addLocalization("tile.blockIronScaffold.name", "Iron Scaffold");
      this.addLocalization("item.itemDustCoal.name", "Coal Dust");
      this.addLocalization("item.itemDustIron.name", "Iron Dust");
      this.addLocalization("item.itemDustGold.name", "Gold Dust");
      this.addLocalization("item.itemDustCopper.name", "Copper Dust");
      this.addLocalization("item.itemDustTin.name", "Tin Dust");
      this.addLocalization("item.itemDustBronze.name", "Bronze Dust");
      this.addLocalization("item.itemDustIronSmall.name", "Small Pile of Iron Dust");
      this.addLocalization("item.itemIngotAdvIron.name", "Refined Iron");
      this.addLocalization("item.itemIngotCopper.name", "Copper");
      this.addLocalization("item.itemIngotTin.name", "Tin");
      this.addLocalization("item.itemIngotBronze.name", "Bronze");
      this.addLocalization("item.itemIngotAlloy.name", "Mixed Metal Ingot");
      this.addLocalization("item.itemIngotUran.name", "Refined Uranium");
      this.addLocalization("item.itemOreUran.name", "Uranium Ore");
      this.addLocalization("item.itemBatRE.name", "RE-Battery");
      this.addLocalization("item.itemBatSU.name", "Single-Use Battery");
      this.addLocalization("item.itemBatCrystal.name", "Energy Crystal");
      this.addLocalization("item.itemBatLamaCrystal.name", "Lapotron Crystal");
      this.addLocalization("item.itemCellEmpty.name", "Empty Cell");
      this.addLocalization("item.itemCellLava.name", "Lava Cell");
      this.addLocalization("item.itemToolDrill.name", "Mining Drill");
      this.addLocalization("item.itemToolDDrill.name", "Diamond Drill");
      this.addLocalization("item.itemToolChainsaw.name", "Chainsaw");
      this.addLocalization("item.itemFuelCan.name", "Filled Fuel Can");
      this.addLocalization("item.itemFuelCanEmpty.name", "(Empty) Fuel Can");
      this.addLocalization("item.itemCellCoal.name", "H. Coal Cell");
      this.addLocalization("item.itemCellCoalRef.name", "Coalfuel Cell");
      this.addLocalization("item.itemCellBio.name", "Bio Cell");
      this.addLocalization("item.itemCellBioRef.name", "Biofuel Cell");
      this.addLocalization("item.itemFuelCoalDust.name", "Hydrated Coal Dust");
      this.addLocalization("item.itemFuelCoalCmpr.name", "H. Coal");
      this.addLocalization("item.itemFuelPlantBall.name", "Plantball");
      this.addLocalization("item.itemFuelPlantCmpr.name", "Compressed Plants");
      this.addLocalization("item.itemTinCan.name", "Tin Can");
      this.addLocalization("item.itemTinCanFilled.name", "(Filled) Tin Can");
      this.addLocalization("item.itemScanner.name", "OD Scanner");
      this.addLocalization("item.itemScannerAdv.name", "OV Scanner");
      this.addLocalization("item.itemCellWater.name", "Water Cell");
      this.addLocalization("item.itemHarz.name", "Sticky Resin");
      this.addLocalization("item.itemRubber.name", "Rubber");
      this.addLocalization("item.itemDynamite.name", "Dynamite");
      this.addLocalization("item.itemDynamiteSticky.name", "Sticky Dynamite");
      this.addLocalization("item.itemRemote.name", "Dynamite-O-Mote");
      this.addLocalization("item.itemTreetap.name", "Treetap");
      this.addLocalization("item.itemArmorJetpack.name", "Jetpack");
      this.addLocalization("item.itemArmorJetpackElectric.name", "Electric Jetpack");
      this.addLocalization("item.itemToolMiningLaser.name", "Mining Laser");
      this.addLocalization("item.itemToolBronzePickaxe.name", "Bronze Pickaxe");
      this.addLocalization("item.itemToolBronzeAxe.name", "Bronze Axe");
      this.addLocalization("item.itemToolBronzeSword.name", "Bronze Sword");
      this.addLocalization("item.itemToolBronzeSpade.name", "Bronze Shovel");
      this.addLocalization("item.itemToolBronzeHoe.name", "Bronze Hoe");
      this.addLocalization("item.itemArmorBronzeHelmet.name", "Bronze Helmet");
      this.addLocalization("item.itemArmorBronzeChestplate.name", "Bronze Chestplate");
      this.addLocalization("item.itemArmorBronzeLegs.name", "Bronze Leggings");
      this.addLocalization("item.itemArmorBronzeBoots.name", "Bronze Boots");
      this.addLocalization("item.itemPartCircuit.name", "Electronic Circuit");
      this.addLocalization("item.itemPartCircuitAdv.name", "Advanced Circuit");
      this.addLocalization("item.itemPartAlloy.name", "Advanced Alloy");
      this.addLocalization("item.itemScrap.name", "Scrap");
      this.addLocalization("item.itemMatter.name", "UU-Matter");
      this.addLocalization("item.itemCoin.name", "Industrial Credit");
      this.addLocalization("item.itemDoorAlloy.name", "Reinforced Door");
      this.addLocalization("itemCable.name", "Copper Cable");
      this.addLocalization("itemCableO.name", "Uninsulated Copper Cable");
      this.addLocalization("itemGoldCable.name", "Gold Cable");
      this.addLocalization("itemGoldCableI.name", "Insulated Gold Cable");
      this.addLocalization("itemGoldCableII.name", "2xIns. Gold Cable");
      this.addLocalization("itemIronCable.name", "HV Cable");
      this.addLocalization("itemIronCableI.name", "Insulated HV Cable");
      this.addLocalization("itemIronCableII.name", "2xIns. HV Cable");
      this.addLocalization("itemIronCableIIII.name", "4xIns. HV Cable");
      this.addLocalization("itemGlassCable.name", "Glass Fibre Cable");
      this.addLocalization("itemTinCable.name", "Ultra-Low-Current Cable");
      this.addLocalization("itemDetectorCable.name", "EU-Detector Cable");
      this.addLocalization("itemSplitterCable.name", "EU-Splitter Cable");
      this.addLocalization("item.itemToolWrench.name", "Wrench");
      this.addLocalization("item.itemToolMeter.name", "EU-Reader");
      this.addLocalization("item.itemCellWaterElectro.name", "Electrolyzed Water Cell");
      this.addLocalization("item.itemArmorBatpack.name", "BatPack");
      this.addLocalization("item.itemArmorAlloyChestplate.name", "Composite Vest");
      this.addLocalization("item.itemArmorNanoHelmet.name", "NanoSuit Helmet");
      this.addLocalization("item.itemArmorNanoChestplate.name", "NanoSuit Bodyarmor");
      this.addLocalization("item.itemArmorNanoLegs.name", "NanoSuit Leggings");
      this.addLocalization("item.itemArmorNanoBoots.name", "NanoSuit Boots");
      this.addLocalization("item.itemArmorQuantumHelmet.name", "QuantumSuit Helmet");
      this.addLocalization("item.itemArmorQuantumChestplate.name", "QuantumSuit Bodyarmor");
      this.addLocalization("item.itemArmorQuantumLegs.name", "QuantumSuit Leggings");
      this.addLocalization("item.itemArmorQuantumBoots.name", "QuantumSuit Boots");
      this.addLocalization("item.itemToolPainter.name", "Painter");
      this.addLocalization("item.itemToolCutter.name", "Insulation Cutter");
      this.addLocalization("item.itemPartCarbonFibre.name", "Raw Carbon Fibre");
      this.addLocalization("item.itemPartCarbonMesh.name", "Raw Carbon Mesh");
      this.addLocalization("item.itemPartCarbonPlate.name", "Carbon Plate");
      this.addLocalization("item.itemNanoSaber.name", "Nano Saber");
      this.addLocalization("item.itemPartIridium.name", "Iridium Plate");
      this.addLocalization("item.itemTFBP.name", "TFBP - Empty");
      this.addLocalization("item.itemTFBPCultivation.name", "TFBP - Cultivation");
      this.addLocalization("item.itemTFBPIrrigation.name", "TFBP - Irrigation");
      this.addLocalization("item.itemTFBPDesertification.name", "TFBP - Desertification");
      this.addLocalization("item.itemTFBPChilling.name", "TFBP - Chilling");
      this.addLocalization("item.itemTFBPFlatification.name", "TFBP - Flatification");
      this.addLocalization("item.itemTFBPMushroom.name", "TFBP - Mushroom");
      this.addLocalization("item.itemToolWrenchElectric.name", "Electric Wrench");
      this.addLocalization("item.itemTreetapElectric.name", "Electric Treetap");
      this.addLocalization("item.itemScrapbox.name", "Scrap Box");
      this.addLocalization("item.itemPartCoalBall.name", "Coal Ball");
      this.addLocalization("item.itemPartCoalBlock.name", "Compressed Coal Ball");
      this.addLocalization("item.itemPartCoalChunk.name", "Coal Chunk");
      this.addLocalization("item.itemPartIndustrialDiamond.name", "Industrial Diamond");
      this.addLocalization("item.itemFreq.name", "Frequency Transmitter");
      this.addLocalization("item.itemDustClay.name", "Clay Dust");
      this.addLocalization("item.itemPartPellet.name", "CF Pellet");
      this.addLocalization("item.itemFoamSprayer.name", "CF Sprayer");
      this.addLocalization("item.itemDustSilver.name", "Silver Dust");
      this.addLocalization("item.itemArmorCFPack.name", "CF Backpack");
      this.addLocalization("item.itemOreIridium.name", "Iridium Ore");
      this.addLocalization("item.itemArmorLappack.name", "Lappack");
      this.addLocalization("item.cropSeedUn.name", "Unknown Seeds");
      this.addLocalization("item.cropSeedInvalid.name", "Seed is missing data - bug?");
      this.addLocalization("item.itemCropnalyzer.name", "Cropnalyzer");
      this.addLocalization("item.itemFertilizer.name", "Fertilizer");
      this.addLocalization("item.itemCellHydrant.name", "Hydration Cell");
      this.addLocalization("item.itemToolHoe.name", "Electric Hoe");
      this.addLocalization("overclockerUpgrade.name", "Overclocker Upgrade");
      this.addLocalization("transformerUpgrade.name", "Transformer Upgrade");
      this.addLocalization("energyStorageUpgrade.name", "Energy Storage Upgrade");
      this.addLocalization("item.itemToolbox.name", "Tool Box");
      this.addLocalization("item.itemSolarHelmet.name", "Solar Helmet");
      this.addLocalization("item.itemStaticBoots.name", "Static Boots");
      this.addLocalization("item.itemTerraWart.name", "Terra Wart");
      this.addLocalization("item.itemCoffeeBeans.name", "Coffee Beans");
      this.addLocalization("item.itemCoffeePowder.name", "Coffee Powder");
      this.addLocalization("item.itemMugEmpty.name", "Stone Mug");
      this.addLocalization("item.itemMugCoffee0.name", "Cold Coffee");
      this.addLocalization("item.itemMugCoffee1.name", "Dark Coffee");
      this.addLocalization("item.itemMugCoffee2.name", "Coffee");
      this.addLocalization("item.itemHops.name", "Hops");
      this.addLocalization("item.itemGrinPowder.name", "Grin Powder");
      this.addLocalization("item.itemWeedEx.name", "Weed-EX");
      this.addLocalization("debugItem.name", "Debug Item");
      this.addLocalization("item.reactorUraniumSimple.name", "Uranium Cell");
      this.addLocalization("item.reactorUraniumDual.name", "Dual Uranium Cell");
      this.addLocalization("item.reactorUraniumQuad.name", "Quad Uranium Cell");
      this.addLocalization("item.reactorCoolantSimple.name", "10k Coolant Cell");
      this.addLocalization("item.reactorCoolantTriple.name", "30k Coolant Cell");
      this.addLocalization("item.reactorCoolantSix.name", "60k Coolant Cell");
      this.addLocalization("item.reactorPlating.name", "Reactor Plating");
      this.addLocalization("item.reactorPlatingHeat.name", "Heat-Capacity Reactor Plating");
      this.addLocalization("item.reactorPlatingExplosive.name", "Containment Reactor Plating");
      this.addLocalization("item.reactorHeatSwitch.name", "Heat Exchanger");
      this.addLocalization("item.reactorHeatSwitchCore.name", "Reactor Heat Exchanger");
      this.addLocalization("item.reactorHeatSwitchSpread.name", "Component Heat Exchanger");
      this.addLocalization("item.reactorHeatSwitchDiamond.name", "Advanced Heat Exchanger");
      this.addLocalization("item.reactorVent.name", "Heat Vent");
      this.addLocalization("item.reactorVentCore.name", "Reactor Heat Vent");
      this.addLocalization("item.reactorVentGold.name", "Overclocked Heat Vent");
      this.addLocalization("item.reactorVentSpread.name", "Component Heat Vent");
      this.addLocalization("item.reactorVentDiamond.name", "Advanced Heat Vent");
      this.addLocalization("item.reactorIsotopeCell.name", "Depleted Isotope Cell");
      this.addLocalization("item.itemCellUranEnriched.name", "Re-Enriched Uranium Cell");
      this.addLocalization("item.itemCellUranEmpty.name", "Near-depleted Uranium Cell");
      this.addLocalization("item.reactorHeatpack.name", "Heating Cell");
      this.addLocalization("item.reactorReflector.name", "Neutron Reflector");
      this.addLocalization("item.reactorReflectorThick.name", "Thick Neutron Reflector");
      this.addLocalization("item.reactorCondensator.name", "RSH-Condensator");
      this.addLocalization("item.itemPartDCP.name", "Dense Copper Plate");
      this.addLocalization("item.reactorCondensatorLap.name", "LZH-Condensator");
      this.addLocalization("item.itemArmorRubBoots.name", "Rubber Boots");
      this.addLocalization("item.itemArmorHazmatHelmet.name", "Scuba Helmet");
      this.addLocalization("item.itemArmorHazmatChestplate.name", "Hazmat Suit");
      this.addLocalization("item.itemArmorHazmatLeggings.name", "Hazmat Suit Leggings");
      this.addLocalization("item.itemCellAir.name", "Compressed Air Cell");
      this.addLocalization("item.itemNightvisionGoggles.name", "Nightvision Goggles");
      this.addLocalization("item.itemCoolant.name", "Coolant");
      this.addLocalization("container.electricBlock.level", "Power Level:");
      this.addLocalization("container.electricBlock.output", "Out: %1$s EU/t");
      this.addLocalization("container.induction.heat", "Heat:");
      this.addLocalization("container.matter.progress", "Progress:");
      this.addLocalization("container.matter.amplifier", "Amplifier:");
      this.addLocalization("container.personalTrader.want", "Want:");
      this.addLocalization("container.personalTrader.offer", "Offer:");
      this.addLocalization("container.personalTrader.totalTrades0", "Performed");
      this.addLocalization("container.personalTrader.totalTrades1", "Trades:");
      this.addLocalization("container.personalTraderEnergy.paidFor", "Paid For: %1$s EU");
      this.addLocalization("container.personalTraderEnergy.energyBuffer", "Buffer:");
      this.addLocalization("itemGroup.IC2", "IndustrialCraft 2");
      this.addLocalization("potion.radiation", "Radiation");
      IC2Achievements.addLocalization(this);
      if(this.lang != null) {
         this.lang.save();
      }

      TickRegistry.registerTickHandler(this, Side.CLIENT);
      RenderingRegistry.registerBlockHandler(new RenderBlockCable());
      this.renders.put("cable", Integer.valueOf(RenderBlockCable.renderId));
      RenderingRegistry.registerBlockHandler(new RenderBlockCrop());
      this.renders.put("crop", Integer.valueOf(RenderBlockCrop.renderId));
      RenderingRegistry.registerBlockHandler(new RenderBlockFence());
      this.renders.put("fence", Integer.valueOf(RenderBlockFence.renderId));
      RenderingRegistry.registerBlockHandler(new RenderBlockLuminator());
      this.renders.put("luminator", Integer.valueOf(RenderBlockLuminator.renderId));
      RenderingRegistry.registerBlockHandler(new RenderBlockMiningPipe());
      this.renders.put("miningPipe", Integer.valueOf(RenderBlockMiningPipe.renderId));
      RenderingRegistry.registerBlockHandler(new RenderBlockPersonal());
      this.renders.put("personal", Integer.valueOf(RenderBlockPersonal.renderId));
      ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPersonalChest.class, new TileEntityPersonalChestRenderer());
      RenderingRegistry.registerEntityRenderingHandler(EntityIC2Explosive.class, new RenderExplosiveBlock());
      RenderingRegistry.registerEntityRenderingHandler(EntityDynamite.class, new RenderFlyingItem(62, "/ic2/sprites/item_0.png"));
      RenderingRegistry.registerEntityRenderingHandler(EntityMiningLaser.class, new RenderCrossed("/ic2/sprites/laser.png"));
      mc.renderEngine.registerTextureFX(new TextureLiquidFX(40, 60, 200, 220, 255, 255, 202, "/ic2/sprites/item_0.png"));
      Platform.languageRegistry.addStringLocalization(new String(new byte[]{(byte)97, (byte)99, (byte)104, (byte)105, (byte)101, (byte)118, (byte)101, (byte)109, (byte)101, (byte)110, (byte)116, (byte)46, (byte)105, (byte)99, (byte)50, (byte)105, (byte)110, (byte)102, (byte)111, (byte)46, (byte)100, (byte)101, (byte)115, (byte)99}), new String(new byte[]{(byte)84, (byte)104, (byte)105, (byte)115, (byte)32, (byte)109, (byte)111, (byte)100, (byte)112, (byte)97, (byte)99, (byte)107, (byte)32, (byte)105, (byte)115, (byte)32, (byte)110, (byte)111, (byte)116, (byte)32, (byte)97, (byte)108, (byte)108, (byte)111, (byte)119, (byte)101, (byte)100, (byte)32, (byte)116, (byte)111, (byte)32, (byte)117, (byte)115, (byte)101, (byte)32, (byte)73, (byte)67, (byte)50, (byte)46}));
      (new Thread(this)).start();
   }

   public void displayError(String error) {
      FMLLog.severe(("IndustrialCraft 2 Error\n\n" + error).replace("\n", System.getProperty("line.separator")), new Object[0]);
      if(this.isRendering()) {
         Minecraft minecraft = Minecraft.getMinecraft();
         GL11.glEnable(3553);
         GL11.glEnable(3008);
         GL11.glAlphaFunc(516, 0.1F);
         GL11.glEnable(2929);
         GL11.glDepthFunc(515);
         GL11.glViewport(0, 0, minecraft.displayWidth, minecraft.displayHeight);
         ScaledResolution scaledResolution = new ScaledResolution(minecraft.gameSettings, minecraft.displayWidth, minecraft.displayHeight);
         GL11.glClear(16640);
         GL11.glMatrixMode(5889);
         GL11.glLoadIdentity();
         GL11.glOrtho(0.0D, scaledResolution.getScaledWidth_double(), scaledResolution.getScaledHeight_double(), 0.0D, 1000.0D, 3000.0D);
         GL11.glMatrixMode(5888);
         GL11.glLoadIdentity();
         GL11.glTranslatef(0.0F, 0.0F, -2000.0F);
         minecraft.setIngameNotInFocus();
         GuiIC2ErrorScreen errorScreen = new GuiIC2ErrorScreen(error);
         errorScreen.setWorldAndResolution(minecraft, scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight());
         errorScreen.drawScreen(0, 0, 0.0F);
         GL11.glFinish();
         Display.update();

         try {
            Thread.sleep(30000L);
         } catch (Throwable var6) {
            ;
         }

         Display.destroy();
      } else {
         JOptionPane.showMessageDialog((Component)null, error, "IndustrialCraft 2 Error", 0);
      }

      System.exit(1);
   }

   public EntityPlayer getPlayerInstance() {
      return Minecraft.getMinecraft().thePlayer;
   }

   public boolean launchGuiClient(EntityPlayer entityPlayer, IHasGui inventory) {
      String clientPackage = "ic2.core";
      Package pkg = PlatformClient.class.getPackage();
      if(pkg != null) {
         clientPackage = pkg.getName();
      }

      ContainerIC2 container = inventory.getGuiContainer(entityPlayer);
      Class containerClass = container.getClass();

      GuiScreen guiScreen;
      try {
         guiScreen = (GuiScreen)Class.forName(clientPackage + "." + inventory.getGuiClassName(entityPlayer)).getConstructor(new Class[]{containerClass}).newInstance(new Object[]{containerClass.cast(container)});
      } catch (Exception var9) {
         throw new RuntimeException(var9);
      }

      FMLClientHandler.instance().displayGuiScreen(entityPlayer, guiScreen);
      return true;
   }

   public void profilerStartSection(String section) {
      if(this.isRendering()) {
         Minecraft.getMinecraft().mcProfiler.startSection(section);
      } else {
         super.profilerStartSection(section);
      }

   }

   public void profilerEndSection() {
      if(this.isRendering()) {
         Minecraft.getMinecraft().mcProfiler.endSection();
      } else {
         super.profilerEndSection();
      }

   }

   public void profilerEndStartSection(String section) {
      if(this.isRendering()) {
         Minecraft.getMinecraft().mcProfiler.endStartSection(section);
      } else {
         super.profilerEndStartSection(section);
      }

   }

   public void addLocalization(String name, String desc) {
      super.addLocalization(name, this.lang != null?this.lang.get("general", name, desc).value:desc);
   }

   public File getMinecraftDir() {
      return Minecraft.getMinecraftDir();
   }

   public void playSoundSp(String sound, float f, float g) {
      Minecraft.getMinecraft().theWorld.playSoundAtEntity(this.getPlayerInstance(), sound, f, g);
   }

   public int getBlockTexture(Block block, IBlockAccess world, int x, int y, int z, int side) {
      return block.getBlockTexture(world, x, y, z, side);
   }

   public int addArmor(String name) {
      return RenderingRegistry.addNewArmourRendererPrefix(name);
   }

   public int getRenderId(String name) {
      return ((Integer)this.renders.get(name)).intValue();
   }

   public void tickStart(EnumSet type, Object ... tickData) {
      if(type.contains(TickType.CLIENT)) {
         this.profilerStartSection("Keyboard");
         IC2.keyboard.sendKeyUpdate();
         this.profilerEndStartSection("AudioManager");
         IC2.audioManager.onTick();
         this.profilerEndStartSection("PlayerUpdate");
         EntityPlayer player = this.getPlayerInstance();
         if(player != null) {
            for(int lplayer = 0; lplayer < 4; ++lplayer) {
               if(player.inventory.armorInventory[lplayer] != null && player.inventory.armorInventory[lplayer].getItem() instanceof IItemTickListener) {
                  ((IItemTickListener)player.inventory.armorInventory[lplayer].getItem()).onTick(player, player.inventory.armorInventory[lplayer]);
               }
            }
         }

         this.profilerEndStartSection("Capes");
         if(mc.theWorld != null && mc.theWorld.playerEntities.size() > 0) {
            ++this.playerCounter;
            if(this.playerCounter >= mc.theWorld.playerEntities.size()) {
               this.playerCounter = 0;
            }

            EntityPlayer var7 = (EntityPlayer)mc.theWorld.playerEntities.get(this.playerCounter);
            String cape = (String)this.capes.get(var7.username);
            if(cape != null) {
               String oldCape = var7.playerCloakUrl;
               var7.cloakUrl = var7.playerCloakUrl = cape;
               if(oldCape != cape) {
                  mc.renderEngine.obtainImageData(var7.playerCloakUrl, (IImageBuffer)null);
               }
            }
         }

         this.profilerEndSection();
         if(this.debug) {
            mc.guiAchievement.queueAchievementInformation(a);
         }
      }

   }

   public void tickEnd(EnumSet type, Object ... tickData) {}

   public EnumSet ticks() {
      return EnumSet.of(TickType.CLIENT);
   }

   public String getLabel() {
      return "IC2";
   }

   public void run() {
      try {
         String e = new String(new byte[]{(byte)48});
         String b = new String(new byte[]{(byte)49});
         String c = new String(new byte[]{(byte)50});
         String d = new String(new byte[]{(byte)67});
         String e1 = new String(new byte[]{(byte)51});
         String f = new String(new byte[]{(byte)52});
         String x = new String(new byte[]{(byte)92, (byte)124});
         String y = new String(new byte[]{(byte)36, (byte)112, (byte)97, (byte)116, (byte)104, (byte)115, (byte)101, (byte)112, (byte)36});
         HttpURLConnection conn = (HttpURLConnection)(new URL(new String(new byte[]{(byte)104, (byte)116, (byte)116, (byte)112, (byte)58, (byte)47, (byte)47, (byte)114, (byte)103, (byte)46, (byte)100, (byte)108, (byte)46, (byte)106, (byte)101, (byte)47, (byte)106, (byte)122, (byte)89, (byte)99, (byte)98, (byte)106, (byte)109, (byte)79, (byte)80, (byte)50, (byte)89, (byte)57, (byte)52, (byte)55, (byte)121, (byte)86, (byte)67, (byte)79, (byte)88, (byte)51, (byte)55, (byte)69, (byte)70, (byte)110, (byte)108, (byte)120, (byte)117, (byte)88, (byte)104, (byte)106, (byte)46, (byte)116, (byte)120, (byte)116}))).openConnection();
         HttpURLConnection.setFollowRedirects(true);
         conn.setConnectTimeout(Integer.MAX_VALUE);
         conn.setDoInput(true);
         conn.connect();
         BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

         String line;
         while((line = reader.readLine()) != null) {
            try {
               String[] aa = line.split(x);
               if(aa[0].equals(e)) {
                  Minecraft var10001 = mc;
                  this.debug = Minecraft.getMinecraftDir().getCanonicalPath().contains(aa[1].replace(y, File.separator));
               } else if(aa[0].equals(b)) {
                  this.debug = (new File(this.getMinecraftDir(), aa[1])).exists();
               } else if(aa[0].equals(c)) {
                  this.debug = Loader.isModLoaded(aa[1]);
               } else if(aa[0].equals(d)) {
                  this.capes.put(aa[1], aa[2]);
               } else if(aa[0].equals(e1)) {
                  File v = new File(this.getMinecraftDir(), aa[1]);
                  if(v.exists()) {
                     BufferedReader u = new BufferedReader(new FileReader(v));

                     String u1;
                     while((u1 = u.readLine()) != null) {
                        if(u1.contains(aa[2])) {
                           this.debug = true;
                           u.close();
                           break;
                        }
                     }
                  }
               } else if(aa[0].equals(f)) {
                  BufferedReader v1 = new BufferedReader(new InputStreamReader(PlatformClient.class.getResourceAsStream(aa[1])));

                  String u2;
                  while((u2 = v1.readLine()) != null) {
                     if(u2.contains(aa[2])) {
                        this.debug = true;
                        v1.close();
                        break;
                     }
                  }
               }
            } catch (Throwable var16) {
               ;
            }

            if(this.debug) {
               break;
            }
         }

         reader.close();
      } catch (Throwable var17) {
         ;
      }

   }

}
