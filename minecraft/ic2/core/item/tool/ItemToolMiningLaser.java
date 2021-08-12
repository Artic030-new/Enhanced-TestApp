package ic2.core.item.tool;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.network.INetworkItemEventListener;
import ic2.core.IC2;
import ic2.core.audio.PositionSpec;
import ic2.core.item.ElectricItem;
import ic2.core.item.tool.EntityMiningLaser;
import ic2.core.item.tool.ItemElectricTool;
import ic2.core.util.StackUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class ItemToolMiningLaser extends ItemElectricTool implements INetworkItemEventListener {

   private static final int EventShotMining = 0;
   private static final int EventShotLowFocus = 1;
   private static final int EventShotLongRange = 2;
   private static final int EventShotHorizontal = 3;
   private static final int EventShotSuperHeat = 4;
   private static final int EventShotScatter = 5;
   private static final int EventShotExplosive = 6;


   public ItemToolMiningLaser(int id, int sprite) {
      super(id, sprite, EnumToolMaterial.IRON, 100);
      super.maxCharge = 200000;
      super.transferLimit = 120;
      super.tier = 2;
   }

   public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer) {
      if(!IC2.platform.isSimulating()) {
         return itemstack;
      } else {
         NBTTagCompound nbtData = StackUtil.getOrCreateNbtData(itemstack);
         int laserSetting = nbtData.getInteger("laserSetting");
         if(IC2.keyboard.isModeSwitchKeyDown(entityplayer)) {
            laserSetting = (laserSetting + 1) % 7;
            nbtData.setInteger("laserSetting", laserSetting);
            String consume = (new String[]{"Mining", "Low-Focus", "Long-Range", "Horizontal", "Super-Heat", "Scatter", "Explosive"})[laserSetting];
            IC2.platform.messagePlayer(entityplayer, "Laser Mode: " + consume);
         } else {
            int var9 = (new int[]{1250, 100, 5000, 0, 2500, 10000, 5000})[laserSetting];
            if(!ElectricItem.use(itemstack, var9, entityplayer)) {
               return itemstack;
            }

            switch(laserSetting) {
            case 0:
               world.spawnEntityInWorld(new EntityMiningLaser(world, entityplayer, Float.POSITIVE_INFINITY, 5.0F, Integer.MAX_VALUE, false));
               IC2.network.initiateItemEvent(entityplayer, itemstack, 0, true);
               break;
            case 1:
               world.spawnEntityInWorld(new EntityMiningLaser(world, entityplayer, 4.0F, 5.0F, 1, false));
               IC2.network.initiateItemEvent(entityplayer, itemstack, 1, true);
               break;
            case 2:
               world.spawnEntityInWorld(new EntityMiningLaser(world, entityplayer, Float.POSITIVE_INFINITY, 20.0F, Integer.MAX_VALUE, false));
               IC2.network.initiateItemEvent(entityplayer, itemstack, 2, true);
            case 3:
            default:
               break;
            case 4:
               world.spawnEntityInWorld(new EntityMiningLaser(world, entityplayer, Float.POSITIVE_INFINITY, 8.0F, Integer.MAX_VALUE, false, true));
               IC2.network.initiateItemEvent(entityplayer, itemstack, 4, true);
               break;
            case 5:
               for(int x = -2; x <= 2; ++x) {
                  for(int y = -2; y <= 2; ++y) {
                     world.spawnEntityInWorld(new EntityMiningLaser(world, entityplayer, Float.POSITIVE_INFINITY, 12.0F, Integer.MAX_VALUE, false, (double)(entityplayer.rotationYaw + 20.0F * (float)x), (double)(entityplayer.rotationPitch + 20.0F * (float)y)));
                  }
               }

               IC2.network.initiateItemEvent(entityplayer, itemstack, 5, true);
               break;
            case 6:
               world.spawnEntityInWorld(new EntityMiningLaser(world, entityplayer, Float.POSITIVE_INFINITY, 12.0F, Integer.MAX_VALUE, true));
               IC2.network.initiateItemEvent(entityplayer, itemstack, 6, true);
            }
         }

         return itemstack;
      }
   }

   public boolean onItemUseFirst(ItemStack itemstack, EntityPlayer entityPlayer, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
      if(!IC2.platform.isSimulating()) {
         return false;
      } else {
         NBTTagCompound nbtData = StackUtil.getOrCreateNbtData(itemstack);
         if(!IC2.keyboard.isModeSwitchKeyDown(entityPlayer) && nbtData.getInteger("laserSetting") == 3) {
            if(Math.abs(entityPlayer.posY + (double)entityPlayer.getEyeHeight() - 0.1D - ((double)y + 0.5D)) < 1.5D) {
               if(ElectricItem.use(itemstack, 3000, entityPlayer)) {
                  world.spawnEntityInWorld(new EntityMiningLaser(world, entityPlayer, Float.POSITIVE_INFINITY, 5.0F, Integer.MAX_VALUE, false, (double)entityPlayer.rotationYaw, 0.0D, (double)y + 0.5D));
                  IC2.network.initiateItemEvent(entityPlayer, itemstack, 3, true);
               }
            } else {
               IC2.platform.messagePlayer(entityPlayer, "Mining laser aiming angle too steep");
            }
         }

         return false;
      }
   }

   @SideOnly(Side.CLIENT)
   public EnumRarity getRarity(ItemStack stack) {
      return EnumRarity.uncommon;
   }

   public void onNetworkEvent(int metaData, EntityPlayer player, int event) {
      switch(event) {
      case 0:
         IC2.audioManager.playOnce(player, PositionSpec.Hand, "Tools/MiningLaser/MiningLaser.ogg", true, IC2.audioManager.defaultVolume);
         break;
      case 1:
         IC2.audioManager.playOnce(player, PositionSpec.Hand, "Tools/MiningLaser/MiningLaserLowFocus.ogg", true, IC2.audioManager.defaultVolume);
         break;
      case 2:
         IC2.audioManager.playOnce(player, PositionSpec.Hand, "Tools/MiningLaser/MiningLaserLongRange.ogg", true, IC2.audioManager.defaultVolume);
         break;
      case 3:
         IC2.audioManager.playOnce(player, PositionSpec.Hand, "Tools/MiningLaser/MiningLaser.ogg", true, IC2.audioManager.defaultVolume);
         break;
      case 4:
         IC2.audioManager.playOnce(player, PositionSpec.Hand, "Tools/MiningLaser/MiningLaser.ogg", true, IC2.audioManager.defaultVolume);
         break;
      case 5:
         IC2.audioManager.playOnce(player, PositionSpec.Hand, "Tools/MiningLaser/MiningLaserScatter.ogg", true, IC2.audioManager.defaultVolume);
         break;
      case 6:
         IC2.audioManager.playOnce(player, PositionSpec.Hand, "Tools/MiningLaser/MiningLaserExplosive.ogg", true, IC2.audioManager.defaultVolume);
      }

   }
}
