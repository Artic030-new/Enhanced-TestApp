package ic2.core.item.armor;

import ic2.core.IC2;
import ic2.core.IItemTickListener;
import ic2.core.Ic2Items;
import ic2.core.audio.AudioSource;
import ic2.core.audio.PositionSpec;
import ic2.core.item.armor.ItemArmorUtility;
import ic2.core.util.StackUtil;
import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ItemArmorJetpack extends ItemArmorUtility implements IItemTickListener {

   public static AudioSource audioSource;
   private static boolean lastJetpackUsed = false;


   public ItemArmorJetpack(int id, int spriteIndex, int armorrendering) {
      super(id, spriteIndex, armorrendering, 1);
      this.setMaxDamage(18002);
   }

   public int getCharge(ItemStack itemStack) {
      int ret = this.getMaxCharge(itemStack) - itemStack.getItemDamage() - 1;
      return ret > 0?ret:0;
   }

   public int getMaxCharge(ItemStack itemStack) {
      return itemStack.getMaxDamage() - 2;
   }

   public void use(ItemStack itemStack, int amount) {
      int newCharge = this.getCharge(itemStack) - amount;
      if(newCharge < 0) {
         newCharge = 0;
      }

      itemStack.setItemDamage(1 + itemStack.getMaxDamage() - newCharge);
   }

   public boolean useJetpack(EntityPlayer player, boolean hoverMode) {
      ItemStack jetpack = player.inventory.armorInventory[2];
      if(this.getCharge(jetpack) == 0) {
         return false;
      } else {
         boolean electric = jetpack.itemID != Ic2Items.jetpack.itemID;
         float power = 1.0F;
         float dropPercentage = 0.2F;
         if(electric) {
            power = 0.7F;
            dropPercentage = 0.05F;
         }

         if((float)this.getCharge(jetpack) / (float)this.getMaxCharge(jetpack) <= dropPercentage) {
            power *= (float)this.getCharge(jetpack) / ((float)this.getMaxCharge(jetpack) * dropPercentage);
         }

         if(IC2.keyboard.isForwardKeyDown(player)) {
            float worldHeight = 0.15F;
            if(hoverMode) {
               worldHeight = 0.5F;
            }

            if(electric) {
               worldHeight += 0.15F;
            }

            float maxFlightHeight = power * worldHeight * 2.0F;
            if(maxFlightHeight > 0.0F) {
               player.moveFlying(0.0F, 0.4F * maxFlightHeight, 0.02F);
            }
         }

         int worldHeight1 = IC2.getWorldHeight(player.worldObj);
         int maxFlightHeight1 = electric?(int)((float)worldHeight1 / 1.28F):worldHeight1;
         double y = player.posY;
         if(y > (double)(maxFlightHeight1 - 25)) {
            if(y > (double)maxFlightHeight1) {
               y = (double)maxFlightHeight1;
            }

            power = (float)((double)power * (((double)maxFlightHeight1 - y) / 25.0D));
         }

         double prevmotion = player.motionY;
         player.motionY = Math.min(player.motionY + (double)(power * 0.2F), 0.6000000238418579D);
         if(hoverMode) {
            float consume = -0.1F;
            if(electric && IC2.keyboard.isJumpKeyDown(player)) {
               consume = 0.1F;
            }

            if(player.motionY > (double)consume) {
               player.motionY = (double)consume;
               if(prevmotion > player.motionY) {
                  player.motionY = prevmotion;
               }
            }
         }

         int consume1 = 9;
         if(hoverMode) {
            consume1 = 6;
         }

         if(electric) {
            consume1 -= 2;
         }

         this.use(jetpack, consume1);
         player.fallDistance = 0.0F;
         player.distanceWalkedModified = 0.0F;
         IC2.platform.resetPlayerInAirTime(player);
         return true;
      }
   }

   public boolean onTick(EntityPlayer player, ItemStack itemStack) {
      NBTTagCompound nbtData = StackUtil.getOrCreateNbtData(itemStack);
      boolean hoverMode = nbtData.getBoolean("hoverMode");
      byte toggleTimer = nbtData.getByte("toggleTimer");
      boolean jetpackUsed = false;
      if(IC2.keyboard.isJumpKeyDown(player) && IC2.keyboard.isModeSwitchKeyDown(player) && toggleTimer == 0) {
         toggleTimer = 10;
         hoverMode = !hoverMode;
         if(IC2.platform.isSimulating()) {
            nbtData.setBoolean("hoverMode", hoverMode);
            if(hoverMode) {
               IC2.platform.messagePlayer(player, "Hover Mode enabled.");
            } else {
               IC2.platform.messagePlayer(player, "Hover Mode disabled.");
            }
         }
      }

      if(IC2.keyboard.isJumpKeyDown(player) || hoverMode && player.motionY < -0.3499999940395355D) {
         jetpackUsed = this.useJetpack(player, hoverMode);
      }

      if(IC2.platform.isSimulating() && toggleTimer > 0) {
         --toggleTimer;
         nbtData.setByte("toggleTimer", toggleTimer);
      }

      if(IC2.platform.isRendering() && player == IC2.platform.getPlayerInstance()) {
         if(lastJetpackUsed != jetpackUsed) {
            if(jetpackUsed) {
               if(audioSource == null) {
                  audioSource = IC2.audioManager.createSource(player, PositionSpec.Backpack, "Tools/Jetpack/JetpackLoop.ogg", true, false, IC2.audioManager.defaultVolume);
               }

               if(audioSource != null) {
                  audioSource.play();
               }
            } else if(audioSource != null) {
               audioSource.remove();
               audioSource = null;
            }

            lastJetpackUsed = jetpackUsed;
         }

         if(audioSource != null) {
            audioSource.updatePosition();
         }
      }

      return jetpackUsed;
   }

   public void getSubItems(int i, CreativeTabs tabs, List itemList) {
      itemList.add(new ItemStack(this, 1, 1));
   }

}
