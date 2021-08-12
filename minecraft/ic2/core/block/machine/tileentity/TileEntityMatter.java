package ic2.core.block.machine.tileentity;

import ic2.api.Direction;
import ic2.core.ContainerIC2;
import ic2.core.ExplosionIC2;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.Ic2Items;
import ic2.core.audio.AudioSource;
import ic2.core.audio.PositionSpec;
import ic2.core.block.machine.ContainerMatter;
import ic2.core.block.machine.tileentity.TileEntityElecMachine;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.Map.Entry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.ISidedInventory;

public class TileEntityMatter extends TileEntityElecMachine implements IHasGui, ISidedInventory {

   public int soundTicker;
   public int scrap = 0;
   public static Vector amplifiers = new Vector();
   private final int StateIdle = 0;
   private final int StateRunning = 1;
   private final int StateRunningScrap = 2;
   private int state = 0;
   private int prevState = 0;
   private AudioSource audioSource;
   private AudioSource audioSourceScrap;


   public TileEntityMatter() {
      super(2, 0, 1100000, 512);
      this.soundTicker = IC2.random.nextInt(32);
   }

   public void readFromNBT(NBTTagCompound nbttagcompound) {
      super.readFromNBT(nbttagcompound);

      try {
         this.scrap = nbttagcompound.getInteger("scrap");
      } catch (Throwable var3) {
         this.scrap = nbttagcompound.getShort("scrap");
      }

   }

   public void writeToNBT(NBTTagCompound nbttagcompound) {
      super.writeToNBT(nbttagcompound);
      nbttagcompound.setInteger("scrap", this.scrap);
   }

   public String getInvName() {
      return "Mass Fabricator";
   }

   public void updateEntity() {
      super.updateEntity();
      if(!this.isRedstonePowered() && super.energy > 0) {
         this.setState(this.scrap > 0?2:1);
         this.setActive(true);
         boolean needsInvUpdate = false;
         if(this.scrap < 1000 && super.inventory[0] != null) {
            Iterator i$ = amplifiers.iterator();

            while(i$.hasNext()) {
               Entry amplifier = (Entry)i$.next();
               if(super.inventory[0].isItemEqual((ItemStack)amplifier.getKey())) {
                  if(((ItemStack)amplifier.getKey()).getItem().hasContainerItem()) {
                     super.inventory[0] = ((ItemStack)amplifier.getKey()).getItem().getContainerItemStack(super.inventory[0]);
                  } else {
                     --super.inventory[0].stackSize;
                     if(super.inventory[0].stackSize <= 0) {
                        super.inventory[0] = null;
                     }
                  }

                  this.scrap += ((Integer)amplifier.getValue()).intValue();
                  break;
               }
            }
         }

         if(super.energy >= 1000000) {
            needsInvUpdate = this.attemptGeneration();
         }

         if(needsInvUpdate) {
            this.onInventoryChanged();
         }
      } else {
         this.setState(0);
         this.setActive(false);
      }

   }

   public void onUnloaded() {
      if(IC2.platform.isRendering() && this.audioSource != null) {
         IC2.audioManager.removeSources(this);
         this.audioSource = null;
         this.audioSourceScrap = null;
      }

      super.onUnloaded();
   }

   public boolean attemptGeneration() {
      if(super.inventory[1] == null) {
         super.inventory[1] = Ic2Items.matter.copy();
         super.energy -= 1000000;
         return true;
      } else if(super.inventory[1].isItemEqual(Ic2Items.matter) && super.inventory[1].stackSize < super.inventory[1].getMaxStackSize()) {
         super.energy -= 1000000;
         ++super.inventory[1].stackSize;
         return true;
      } else {
         return false;
      }
   }

   public int demandsEnergy() {
      return this.isRedstonePowered()?0:super.maxEnergy - super.energy;
   }

   public int injectEnergy(Direction directionFrom, int amount) {
      if(amount > this.getMaxSafeInput()) {
         super.worldObj.setBlockWithNotify(super.xCoord, super.yCoord, super.zCoord, 0);
         ExplosionIC2 bonus1 = new ExplosionIC2(super.worldObj, (Entity)null, (double)((float)super.xCoord + 0.5F), (double)((float)super.yCoord + 0.5F), (double)((float)super.zCoord + 0.5F), 15.0F, 0.01F, 1.5F);
         bonus1.doExplosion();
         return 0;
      } else {
         int bonus = amount;
         if(amount > this.scrap) {
            bonus = this.scrap;
         }

         this.scrap -= bonus;
         super.energy += amount + 5 * bonus;
         int re = 0;
         if(super.energy > super.maxEnergy) {
            re = super.energy - super.maxEnergy;
            super.energy = super.maxEnergy;
         }

         return re;
      }
   }

   public int getMaxSafeInput() {
      return 512;
   }

   public String getProgressAsString() {
      int p = super.energy / 10000;
      if(p > 100) {
         p = 100;
      }

      return "" + p + "%";
   }

   public ContainerIC2 getGuiContainer(EntityPlayer entityPlayer) {
      return new ContainerMatter(entityPlayer, this);
   }

   public String getGuiClassName(EntityPlayer entityPlayer) {
      return "block.machine.gui.GuiMatter";
   }

   public void onGuiClosed(EntityPlayer entityPlayer) {}

   private void setState(int state) {
      this.state = state;
      if(this.prevState != state) {
         IC2.network.updateTileEntityField(this, "state");
      }

      this.prevState = state;
   }

   public List getNetworkedFields() {
      Vector ret = new Vector(1);
      ret.add("state");
      return ret;
   }

   public void onNetworkUpdate(String field) {
      if(field.equals("state") && this.prevState != this.state) {
         switch(this.state) {
         case 0:
            if(this.audioSource != null) {
               this.audioSource.stop();
            }

            if(this.audioSourceScrap != null) {
               this.audioSourceScrap.stop();
            }
            break;
         case 1:
            if(this.audioSource == null) {
               this.audioSource = IC2.audioManager.createSource(this, PositionSpec.Center, "Generators/MassFabricator/MassFabLoop.ogg", true, false, IC2.audioManager.defaultVolume);
            }

            if(this.audioSource != null) {
               this.audioSource.play();
            }

            if(this.audioSourceScrap != null) {
               this.audioSourceScrap.stop();
            }
            break;
         case 2:
            if(this.audioSource == null) {
               this.audioSource = IC2.audioManager.createSource(this, PositionSpec.Center, "Generators/MassFabricator/MassFabLoop.ogg", true, false, IC2.audioManager.defaultVolume);
            }

            if(this.audioSourceScrap == null) {
               this.audioSourceScrap = IC2.audioManager.createSource(this, PositionSpec.Center, "Generators/MassFabricator/MassFabScrapSolo.ogg", true, false, IC2.audioManager.defaultVolume);
            }

            if(this.audioSource != null) {
               this.audioSource.play();
            }

            if(this.audioSourceScrap != null) {
               this.audioSourceScrap.play();
            }
         }

         this.prevState = this.state;
      }

      super.onNetworkUpdate(field);
   }

   public int getStartInventorySide(ForgeDirection side) {
      switch(TileEntityMatter.NamelessClass889214618.$SwitchMap$net$minecraftforge$common$ForgeDirection[side.ordinal()]) {
      case 1:
         return 0;
      default:
         return 1;
      }
   }

   public int getSizeInventorySide(ForgeDirection side) {
      return 1;
   }

   public float getWrenchDropRate() {
      return 0.7F;
   }

   public boolean amplificationIsAvailable() {
      if(this.scrap > 0) {
         return true;
      } else if(super.inventory[0] == null) {
         return false;
      } else {
         Iterator i$ = amplifiers.iterator();

         Entry amplifier;
         do {
            if(!i$.hasNext()) {
               return false;
            }

            amplifier = (Entry)i$.next();
         } while(!super.inventory[0].isItemEqual((ItemStack)amplifier.getKey()));

         return true;
      }
   }


   // $FF: synthetic class
   static class NamelessClass889214618 {

      // $FF: synthetic field
      static final int[] $SwitchMap$net$minecraftforge$common$ForgeDirection = new int[ForgeDirection.values().length];


      static {
         try {
            $SwitchMap$net$minecraftforge$common$ForgeDirection[ForgeDirection.DOWN.ordinal()] = 1;
         } catch (NoSuchFieldError var1) {
            ;
         }

      }
   }
}
