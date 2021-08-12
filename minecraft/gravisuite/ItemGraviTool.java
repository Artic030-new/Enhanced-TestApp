package gravisuite;

import buildcraft.api.tools.IToolWrench;
import com.eloraam.redpower.core.IRotatable;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gravisuite.GraviSuite;
import gravisuite.Keyboard;
import gravisuite.KeyboardClient;
import gravisuite.ServerProxy;
import gravisuite.audio.AudioManager;
import gravisuite.redpower.coreLib;
import ic2.api.ElectricItem;
import ic2.api.IElectricItem;
import ic2.api.IWrenchable;
import ic2.api.Items;
import ic2.api.network.NetworkHelper;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.Event.Result;
import net.minecraftforge.event.entity.player.UseHoeEvent;

public class ItemGraviTool extends ItemTool implements IElectricItem, IToolWrench {

   private int maxCharge;
   private int tier;
   private float effPower;
   private boolean firstuse;
   private boolean lastUse;
   public Set mineableBlocks = new HashSet();
   public static int hoeTextureIndex = 16;
   public static int treeTapTextureIndex = 17;
   public static int wrenchTextureIndex = 18;
   public static int screwDriverTextureIndex = 19;
   private int energyPerHoe;
   private int energyPerTreeTap;
   private int energyPerSwitchSide;
   private int energyPerWrenchStandartOperation;
   private int energyPerWrenchFineOperation;
   private int privateToolMode;

   protected ItemGraviTool(int par1, int par2, EnumToolMaterial par3EnumToolMaterial, Block[] par4ArrayOfBlock) {
      super(par1, par2, par3EnumToolMaterial, par4ArrayOfBlock);
      this.setIconIndex(16);
      this.setMaxDamage(27);
      this.maxCharge = 100000;
      this.tier = 2;
      super.efficiencyOnProperMaterial = 16.0F;
      this.energyPerHoe = 50;
      this.energyPerTreeTap = 50;
      this.energyPerSwitchSide = 50;
      this.energyPerWrenchStandartOperation = 500;
      this.energyPerWrenchFineOperation = 10000;
      this.firstuse = true;
      this.setCreativeTab(GraviSuite.ic2Tab);
   }

   public boolean canDischarge(ItemStack var1, int var2) {
      return ElectricItem.discharge(var1, var2, Integer.MAX_VALUE, true, true) == var2;
   }

   public void init() {
      this.mineableBlocks.add(Block.dirt);
      this.mineableBlocks.add(Block.grass);
      this.mineableBlocks.add(Block.mycelium);
   }

   public void dischargeItem(ItemStack itemstack, EntityPlayer player, int value) {
      ElectricItem.use(itemstack, value, player);
   }

   @SideOnly(value=Side.CLIENT)
   public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean par4) {
      Integer toolMode = readToolMode(itemStack);
	  switch(toolMode.intValue()) {
	  case 1:
		 list.add("§7Режим: §2Мотыга");
		 break;
	  case 2:
		 list.add("§7Режим: §6Краник");
		 break;
	  case 3:
		 list.add("§7Режим: §bКлюч");
		 break;
	  case 4:
		 list.add("§7Режим: §dОтвёртка");
		 break;
	  }
   }

   public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
      if(GraviSuite.isSimulating() && Keyboard.isModeKeyDown(player)) {
         Integer toolMode = readToolMode(itemStack);
         toolMode = Integer.valueOf(toolMode.intValue() + 1);
         if(toolMode.intValue() > 4) {
            toolMode = Integer.valueOf(1);
         }

         saveToolMode(itemStack, toolMode);
         setToolName(itemStack);
         if(toolMode.intValue() == 1) {
            ServerProxy.sendPlayerMessage(player, "§fАктивирован режим: §2Мотыга");
         } else if(toolMode.intValue() == 2) {
            ServerProxy.sendPlayerMessage(player, "§fАктивирован режим: §6Краник");
         } else if(toolMode.intValue() == 3) {
            ServerProxy.sendPlayerMessage(player, "§fАктивирован режим: §bКлюч");
         } else if(toolMode.intValue() == 4) {
            ServerProxy.sendPlayerMessage(player, "§fАктивирован режим: §dОтвёртка");
         }
      }

      if(!GraviSuite.isSimulating() && KeyboardClient.isModeKeyPress(player)) {
         AudioManager.playOnce(player, AudioManager.PositionSpec.Hand, "toolChange.ogg", true, AudioManager.defaultVolume);
      }

      return itemStack;
   }

public boolean onItemUseFirst(ItemStack itemstack, EntityPlayer entityplayer, World world, int i, int j, int k, int side, float a, float b, float c) {
      setToolName(itemstack);
      Integer toolMode = readToolMode(itemstack);
      if(toolMode.intValue() == 3) {
         this.lastUse = this.onWrenchUse(itemstack, entityplayer, world, i, j, k, side, a, b, c);
         return this.lastUse;
      } else if(toolMode.intValue() == 4) {
         this.lastUse = this.onScrewdriverUse(itemstack, entityplayer, world, i, j, k, side, a, b, c);
         return this.lastUse;
      } else {
         return false;
      }
   }

   public boolean onItemUse(ItemStack itemstack, EntityPlayer entityplayer, World world, int i, int j, int k, int side, float a, float b, float c) {
      Integer toolMode = readToolMode(itemstack);
      if(toolMode.intValue() == 1) {
         return this.onHoeUse(itemstack, entityplayer, world, i, j, k, side, a, b, c);
      } else if(toolMode.intValue() == 2) {
         return this.onTreeTapUse(itemstack, entityplayer, world, i, j, k, side, a, b, c);
      } else {
         if(toolMode.intValue() == 3 && toolMode.intValue() == 4) {
            ;
         }

         return false;
      }
   }

   public boolean onHoeUse(ItemStack itemstack, EntityPlayer entityplayer, World world, int i, int j, int k, int side, float a, float b, float c) {
      if(!entityplayer.canCurrentToolHarvestBlock(i, j, k)) {
         return false;
      } else if(!this.canDischarge(itemstack, this.energyPerHoe)) {
         ServerProxy.sendPlayerMessage(entityplayer, "§cНедостаточно энергии для выполнения операции!");
         return false;
      } else {
         UseHoeEvent event = new UseHoeEvent(entityplayer, itemstack, world, i, j, k);
         if(MinecraftForge.EVENT_BUS.post(event)) {
            return false;
         } else if(event.getResult() == Result.ALLOW) {
            this.dischargeItem(itemstack, entityplayer, this.energyPerHoe);
            return true;
         } else {
            int i1 = world.getBlockId(i, j, k);
            int j1 = world.getBlockId(i, j + 1, k);
            if((side == 0 || j1 != 0 || i1 != Block.grass.blockID) && i1 != Block.dirt.blockID) {
               return false;
            } else {
               Block block = Block.tilledField;
               world.playSoundEffect((double)((float)i + 0.5F), (double)((float)j + 0.5F), (double)((float)k + 0.5F), block.stepSound.getStepSound(), (block.stepSound.getVolume() + 1.0F) / 2.0F, block.stepSound.getPitch() * 0.8F);
               if(!GraviSuite.isSimulating()) {
                  return true;
               } else {
                  this.dischargeItem(itemstack, entityplayer, this.energyPerHoe);
                  world.setBlockWithNotify(i, j, k, block.blockID);
                  return true;
               }
            }
         }
      }
   }

   public boolean onTreeTapUse(ItemStack itemstack, EntityPlayer entityplayer, World world, int i, int j, int k, int side, float a, float b, float c) {
      if(world.getBlockId(i, j, k) == Items.getItem("blockBarrel").itemID) {
         try {
            Method error = world.getBlockTileEntity(i, j, k).getClass().getMethod("useTreetapOn", new Class[]{EntityPlayer.class, Integer.TYPE});
            return ((Boolean)error.invoke((Object)null, new Object[]{entityplayer, Integer.valueOf(side)})).booleanValue();
         } catch (Throwable var12) {
            ;
         }
      }

      if(world.getBlockId(i, j, k) == Items.getItem("rubberWood").itemID) {
         this.attemptExtract(itemstack, entityplayer, world, i, j, k, side, (List)null);
         return true;
      } else {
         return false;
      }
   }

   public boolean onWrenchUse(ItemStack itemstack, EntityPlayer entityPlayer, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
      if(!this.canDischarge(itemstack, this.energyPerSwitchSide)) {
         return false;
      } else {
         int blockId = world.getBlockId(x, y, z);
         int metaData = world.getBlockMetadata(x, y, z);
         TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

         try {
            if(tileEntity.getClass().getName() == "TileEntityTerra") {
               Method wrenchable1 = tileEntity.getClass().getMethod("ejectBlueprint", new Class[0]);
               if(((Boolean)wrenchable1.invoke((Object)null, new Object[0])).booleanValue()) {
                  if(GraviSuite.isSimulating()) {
                     this.dischargeItem(itemstack, entityPlayer, this.energyPerSwitchSide);
                  }

                  if(!GraviSuite.isSimulating()) {
                     AudioManager.playOnce(entityPlayer, AudioManager.PositionSpec.Hand, "wrench.ogg", true, AudioManager.defaultVolume);
                  }

                  return GraviSuite.isSimulating();
               }
            }
         } catch (Throwable var20) {
            ;
         }

         if(tileEntity instanceof IWrenchable) {
            IWrenchable wrenchable11 = (IWrenchable)tileEntity;
            if(Keyboard.isAltKeyDown(entityPlayer)) {
               if(entityPlayer.isSneaking()) {
                  side = (wrenchable11.getFacing() + 5) % 6;
               } else {
                  side = (wrenchable11.getFacing() + 1) % 6;
               }
            } else if(entityPlayer.isSneaking()) {
               side += side % 2 * -2 + 1;
            }

            if(wrenchable11.wrenchCanSetFacing(entityPlayer, side)) {
               if(GraviSuite.isSimulating()) {
                  wrenchable11.setFacing((short)side);
                  this.dischargeItem(itemstack, entityPlayer, this.energyPerSwitchSide);
               }

               if(!GraviSuite.isSimulating()) {
                  AudioManager.playOnce(entityPlayer, AudioManager.PositionSpec.Hand, "wrench.ogg", true, AudioManager.defaultVolume);
               }

               return GraviSuite.isSimulating();
            }

            if(this.canDischarge(itemstack, this.energyPerWrenchStandartOperation) && wrenchable11.wrenchCanRemove(entityPlayer)) {
               if(GraviSuite.isSimulating()) {
                  if(GraviSuite.logWrench) {
                     String block1 = tileEntity.getClass().getName().replace("TileEntity", "");
                     MinecraftServer.getServer();
                     MinecraftServer.logger.log(Level.INFO, "Player " + entityPlayer.username + " used the wrench to remove the " + block1 + " (" + blockId + "-" + metaData + ") at " + x + "/" + y + "/" + z);
                  }

                  Block block11 = Block.blocksList[blockId];
                  boolean dropOriginalBlock = false;
                  if(wrenchable11.getWrenchDropRate() < 1.0F && this.overrideWrenchSuccessRate(itemstack)) {
                     if(!this.canDischarge(itemstack, this.energyPerWrenchFineOperation)) {
                        ServerProxy.sendPlayerMessage(entityPlayer, "§cНедостаточно энергии для выполнения операции!");
                        return true;
                     }

                     dropOriginalBlock = true;
                     this.dischargeItem(itemstack, entityPlayer, this.energyPerWrenchFineOperation);
                  } else {
                     dropOriginalBlock = world.rand.nextFloat() <= wrenchable11.getWrenchDropRate();
                     this.dischargeItem(itemstack, entityPlayer, this.energyPerWrenchStandartOperation);
                  }

                  ArrayList drops = block11.getBlockDropped(world, x, y, z, metaData, 0);
                  if(dropOriginalBlock) {
                     if(drops.isEmpty()) {
                        drops.add(wrenchable11.getWrenchDrop(entityPlayer));
                     } else {
                        drops.set(0, wrenchable11.getWrenchDrop(entityPlayer));
                     }
                  }

                  Iterator iterator = drops.iterator();

                  while(iterator.hasNext()) {
                     ItemStack itemStack = (ItemStack)iterator.next();
                     dropAsEntity(world, x, y, z, itemStack);
                  }

                  world.setBlockWithNotify(x, y, z, 0);
               }

               if(!GraviSuite.isSimulating()) {
                  AudioManager.playOnce(entityPlayer, AudioManager.PositionSpec.Hand, "wrench.ogg", true, AudioManager.defaultVolume);
               }
            }
         }

         return false;
      }
   }

   public boolean overrideWrenchSuccessRate(ItemStack var1) {
      return true;
   }

   public static void dropAsEntity(World var0, int var1, int var2, int var3, ItemStack var4) {
      if(var4 != null) {
         double var5 = 0.7D;
         double var7 = (double)var0.rand.nextFloat() * var5 + (1.0D - var5) * 0.5D;
         double var9 = (double)var0.rand.nextFloat() * var5 + (1.0D - var5) * 0.5D;
         double var11 = (double)var0.rand.nextFloat() * var5 + (1.0D - var5) * 0.5D;
         EntityItem var13 = new EntityItem(var0, (double)var1 + var7, (double)var2 + var9, (double)var3 + var11, var4.copy());
         var13.delayBeforeCanPickup = 10;
         var0.spawnEntityInWorld(var13);
      }

   }

   public void ejectHarz(World world, int x, int y, int z, int side, int quantity) {
      double ejectX = (double)x + 0.5D;
      double ejectY = (double)y + 0.5D;
      double ejectZ = (double)z + 0.5D;
      if(side == 2) {
         ejectZ -= 0.3D;
      } else if(side == 5) {
         ejectX += 0.3D;
      } else if(side == 3) {
         ejectZ += 0.3D;
      } else if(side == 4) {
         ejectX -= 0.3D;
      }

      for(int i = 0; i < quantity; ++i) {
         EntityItem entityitem = new EntityItem(world, ejectX, ejectY, ejectZ, Items.getItem("resin").copy());
         entityitem.delayBeforeCanPickup = 10;
         world.spawnEntityInWorld(entityitem);
      }

   }

   public boolean attemptExtract(ItemStack treeTapItem, EntityPlayer entityplayer, World world, int i, int j, int k, int l, List stacks) {
      int meta = world.getBlockMetadata(i, j, k);
      if(meta >= 2 && meta % 6 == l) {
         if(meta < 6) {
            if(!this.canDischarge(treeTapItem, this.energyPerTreeTap)) {
               ServerProxy.sendPlayerMessage(entityplayer, "§cНедостаточно энергии для выполнения операции!");
               return false;
            }

            if(GraviSuite.isSimulating()) {
               world.setBlockMetadataWithNotify(i, j, k, meta + 6);
               if(stacks != null) {
                  stacks.add(copyWithSize(Items.getItem("resin"), world.rand.nextInt(3) + 1));
               } else {
                  this.ejectHarz(world, i, j, k, l, world.rand.nextInt(3) + 1);
               }

               world.scheduleBlockUpdate(i, j, k, Items.getItem("rubberWood").itemID, Block.blocksList[Items.getItem("rubberWood").itemID].tickRate());
               NetworkHelper.announceBlockUpdate(world, i, j, k);
               this.dischargeItem(treeTapItem, entityplayer, this.energyPerTreeTap);
               return true;
            }

            if(!GraviSuite.isSimulating()) {
               AudioManager.playOnce(entityplayer, AudioManager.PositionSpec.Hand, "Treetap.ogg", true, AudioManager.defaultVolume);
            }
         }

         if(world.rand.nextInt(5) == 0 && GraviSuite.isSimulating()) {
            world.setBlockMetadataWithNotify(i, j, k, 1);
            NetworkHelper.announceBlockUpdate(world, i, j, k);
         }

         if(world.rand.nextInt(5) == 0) {
            if(!this.canDischarge(treeTapItem, this.energyPerTreeTap)) {
               ServerProxy.sendPlayerMessage(entityplayer, "§cНедостаточно энергии для выполнения операции!");
               return false;
            } else {
               if(GraviSuite.isSimulating()) {
                  this.ejectHarz(world, i, j, k, l, 1);
                  if(stacks != null) {
                     stacks.add(copyWithSize(Items.getItem("resin"), 1));
                  } else {
                     this.ejectHarz(world, i, j, k, l, 1);
                  }

                  this.dischargeItem(treeTapItem, entityplayer, this.energyPerTreeTap);
               }

               if(!GraviSuite.isSimulating()) {
                  AudioManager.playOnce(entityplayer, AudioManager.PositionSpec.Hand, "Treetap.ogg", true, AudioManager.defaultVolume);
               }

               return true;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public boolean onScrewdriverUse(ItemStack var1, EntityPlayer var2, World var3, int var4, int var5, int var6, int var7, float var8, float var9, float var10) {
      boolean var11 = false;
      if(var2 != null && var2.isSneaking()) {
         var11 = true;
      }

      int var12 = var3.getBlockId(var4, var5, var6);
      int var13 = var3.getBlockMetadata(var4, var5, var6);
      if(var12 != Block.redstoneRepeaterIdle.blockID && var12 != Block.redstoneRepeaterActive.blockID) {
         if(var12 == Block.dispenser.blockID) {
            if(!this.canDischarge(var1, this.energyPerWrenchStandartOperation)) {
               if(GraviSuite.isSimulating()) {
                  ServerProxy.sendPlayerMessage(var2, "§cНедостаточно энергии для выполнения операции!");
               }

               return false;
            } else {
               var13 = var13 & 3 ^ var13 >> 2;
               var13 += 2;
               if(!GraviSuite.isSimulating()) {
                  AudioManager.playOnce(var2, AudioManager.PositionSpec.Hand, "wrench.ogg", true, AudioManager.defaultVolume);
               }

               if(GraviSuite.isSimulating()) {
                  this.dischargeItem(var1, var2, this.energyPerWrenchStandartOperation);
                  var3.setBlockMetadataWithNotify(var4, var5, var6, var13);
               }

               return GraviSuite.isSimulating();
            }
         } else if(var12 != Block.pistonBase.blockID && var12 != Block.pistonStickyBase.blockID) {
            TileEntity iRotatableTileEntity = var3.getBlockTileEntity(var4, var5, var6);
            if(iRotatableTileEntity instanceof IRotatable) {
               if(!this.canDischarge(var1, this.energyPerWrenchStandartOperation)) {
                  if(GraviSuite.isSimulating()) {
                     ServerProxy.sendPlayerMessage(var2, "§cНедостаточно энергии для выполнения операции!");
                  }

                  return false;
               } else {
                  MovingObjectPosition var15 = coreLib.retraceBlock(var3, var2, var4, var5, var6);
                  if(var15 == null) {
                     return false;
                  } else {
                     int var16 = ((IRotatable)iRotatableTileEntity).getPartMaxRotation(var15.subHit, var11);
                     if(var16 == 0) {
                        return false;
                     } else {
                        int var17 = ((IRotatable)iRotatableTileEntity).getPartRotation(var15.subHit, var11);
                        ++var17;
                        if(var17 > var16) {
                           var17 = 0;
                        }

                        if(!GraviSuite.isSimulating()) {
                           AudioManager.playOnce(var2, AudioManager.PositionSpec.Hand, "wrench.ogg", true, AudioManager.defaultVolume);
                        }

                        if(GraviSuite.isSimulating()) {
                           this.dischargeItem(var1, var2, this.energyPerWrenchStandartOperation);
                           ((IRotatable)iRotatableTileEntity).setPartRotation(var15.subHit, var11, var17);
                        }

                        return GraviSuite.isSimulating();
                     }
                  }
               }
            } else {
               return false;
            }
         } else {
            ++var13;
            if(!this.canDischarge(var1, this.energyPerWrenchStandartOperation)) {
               if(GraviSuite.isSimulating()) {
                  ServerProxy.sendPlayerMessage(var2, "§cНедостаточно энергии для выполнения операции!");
               }

               return false;
            } else {
               if(var13 > 5) {
                  var13 = 0;
               }

               if(!GraviSuite.isSimulating()) {
                  AudioManager.playOnce(var2, AudioManager.PositionSpec.Hand, "wrench.ogg", true, AudioManager.defaultVolume);
               }

               if(GraviSuite.isSimulating()) {
                  this.dischargeItem(var1, var2, this.energyPerWrenchStandartOperation);
                  var3.setBlockMetadataWithNotify(var4, var5, var6, var13);
               }

               return GraviSuite.isSimulating();
            }
         }
      } else if(!this.canDischarge(var1, this.energyPerWrenchStandartOperation)) {
         if(GraviSuite.isSimulating()) {
            ServerProxy.sendPlayerMessage(var2, "§cНедостаточно энергии для выполнения операции!");
         }

         return false;
      } else {
         if(!GraviSuite.isSimulating()) {
            AudioManager.playOnce(var2, AudioManager.PositionSpec.Hand, "wrench.ogg", true, AudioManager.defaultVolume);
         }

         if(GraviSuite.isSimulating()) {
            this.dischargeItem(var1, var2, this.energyPerWrenchStandartOperation);
            var3.setBlockMetadataWithNotify(var4, var5, var6, var13 & 12 | var13 + 1 & 3);
         }

         return GraviSuite.isSimulating();
      }
   }

   public static ItemStack copyWithSize(ItemStack itemStack, int var1) {
      ItemStack var2 = itemStack.copy();
      var2.stackSize = var1;
      return var2;
   }

   public boolean canProvideEnergy() {
      return false;
   }

   public int getChargedItemId() {
      return super.itemID;
   }

   public int getEmptyItemId() {
      return super.itemID;
   }

   public int getMaxCharge() {
      return this.maxCharge;
   }

   public int getTier() {
      return 2;
   }

   public int getTransferLimit() {
      return 10000;
   }

   public int getDamageVsEntity(Entity par1Entity) {
      return 1;
   }

   public boolean hitEntity(ItemStack var1, EntityLiving var2, EntityLiving var3) {
      return false;
   }

   public String getTextureFile() {
      return "/gravisuite/gravi_items.png";
   }

   public boolean onBlockDestroyed(ItemStack itemstack, World par2World, int par3, int par4, int par5, int par6, EntityLiving entityliving) {
      if(entityliving instanceof EntityPlayer) {
         ;
      }

      return true;
   }

   public void damage(ItemStack var1, int var2, EntityPlayer var3) {
      ElectricItem.use(var1, var2, var3);
   }

   public boolean isRepairable() {
      return false;
   }

   public int getItemEnchantability() {
      return 0;
   }

   @SideOnly(Side.CLIENT)
   public EnumRarity getRarity(ItemStack var1) {
      return EnumRarity.uncommon;
   }

   public static Integer readToolMode(ItemStack itemstack) {
      NBTTagCompound nbttagcompound = GraviSuite.getOrCreateNbtData(itemstack);
      Integer toolMode = Integer.valueOf(nbttagcompound.getInteger("toolMode"));
      if(toolMode.intValue() <= 0 || toolMode.intValue() > 4) {
         toolMode = Integer.valueOf(1);
      }

      return toolMode;
   }

   public static Integer readTextureIndex(ItemStack itemstack) {
      NBTTagCompound nbttagcompound = GraviSuite.getOrCreateNbtData(itemstack);
      Integer textureIndex = Integer.valueOf(nbttagcompound.getInteger("textureIndex"));
      if(textureIndex.intValue() <= 0) {
         textureIndex = Integer.valueOf(hoeTextureIndex);
      }

      return textureIndex;
   }

   public static boolean saveToolMode(ItemStack itemstack, Integer toolMode) {
      NBTTagCompound nbttagcompound = GraviSuite.getOrCreateNbtData(itemstack);
      nbttagcompound.setInteger("toolMode", toolMode.intValue());
      if(toolMode.intValue() == 1) {
         nbttagcompound.setInteger("textureIndex", hoeTextureIndex);
      }

      if(toolMode.intValue() == 2) {
         nbttagcompound.setInteger("textureIndex", treeTapTextureIndex);
      }

      if(toolMode.intValue() == 3) {
         nbttagcompound.setInteger("textureIndex", wrenchTextureIndex);
      }

      if(toolMode.intValue() == 4) {
         nbttagcompound.setInteger("textureIndex", screwDriverTextureIndex);
      }

      return true;
   }

   public void getSubItems(int var1, CreativeTabs var2, List var3) {
      ItemStack var4 = new ItemStack(this, 1);
      ElectricItem.charge(var4, Integer.MAX_VALUE, Integer.MAX_VALUE, true, false);
      var3.add(var4);
      var3.add(new ItemStack(this, 1, this.getMaxDamage()));
   }

   public boolean canWrench(EntityPlayer player, int x, int y, int z) {
      ItemStack itemstack = player.inventory.getCurrentItem();
      Integer toolMode = readToolMode(itemstack);
      if(toolMode.intValue() == 3) {
         if(this.canDischarge(itemstack, this.energyPerWrenchStandartOperation)) {
            return true;
         } else {
            if(GraviSuite.isSimulating()) {
               ServerProxy.sendPlayerMessage(player, "§cНедостаточно энергии для выполнения операции!");
            }

            return false;
         }
      } else {
         return false;
      }
   }

   public void wrenchUsed(EntityPlayer player, int x, int y, int z) {
      if(GraviSuite.isSimulating()) {
         ItemStack itemstack = player.inventory.getCurrentItem();
         Integer toolMode = readToolMode(itemstack);
         this.dischargeItem(itemstack, player, this.energyPerWrenchStandartOperation);
      } else {
         AudioManager.playOnce(player, AudioManager.PositionSpec.Hand, "wrench.ogg", true, AudioManager.defaultVolume);
      }

   }

   private void setToolName(ItemStack itemStack) {
	   
   }
   
   public boolean shouldPassSneakingClickToBlock(World par2World, int par4, int par5, int par6) {
      return true;
   }

}
