package ic2.core.block.machine.tileentity;

import ic2.core.ContainerIC2;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.Ic2Items;
import ic2.core.audio.AudioSource;
import ic2.core.block.machine.ContainerCanner;
import ic2.core.block.machine.tileentity.TileEntityElecMachine;
import ic2.core.item.ItemFuelCanEmpty;
import ic2.core.util.StackUtil;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.ISidedInventory;

public class TileEntityCanner extends TileEntityElecMachine implements IHasGui, ISidedInventory {

   public static final Map specialFood = new HashMap();
   public short progress = 0;
   public int energyconsume = 1;
   public int operationLength = 600;
   private int fuelQuality = 0;
   public AudioSource audioSource;


   public TileEntityCanner() {
      super(4, 1, 631, 32);
   }

   public void readFromNBT(NBTTagCompound nbttagcompound) {
      super.readFromNBT(nbttagcompound);

      try {
         this.fuelQuality = nbttagcompound.getInteger("fuelQuality");
      } catch (Throwable var3) {
         this.fuelQuality = nbttagcompound.getShort("fuelQuality");
      }

   }

   public void writeToNBT(NBTTagCompound nbttagcompound) {
      super.writeToNBT(nbttagcompound);
      nbttagcompound.setInteger("fuelQuality", this.fuelQuality);
   }

   public int gaugeProgressScaled(int i) {
      int l = this.operationLength;
      if(this.getMode() == 1 && super.inventory[0] != null) {
         int food = this.getFoodValue(super.inventory[0]);
         if(food > 0) {
            l = 50 * food;
         }
      }

      if(this.getMode() == 3) {
         l = 50;
      }

      return this.progress * i / l;
   }

   public int gaugeFuelScaled(int i) {
      if(super.energy <= 0) {
         return 0;
      } else {
         int r = super.energy * i / (this.operationLength * this.energyconsume);
         return r > i?i:r;
      }
   }

   public void updateEntity() {
      super.updateEntity();
      boolean needsInvUpdate = false;
      boolean canOperate = this.canOperate();
      if(super.energy <= this.energyconsume * this.operationLength && canOperate) {
         needsInvUpdate = this.provideEnergy();
      }

      boolean newActive = this.getActive();
      if(canOperate && (this.getMode() == 1 && this.progress >= this.getFoodValue(super.inventory[0]) * 50 || this.getMode() == 2 && this.progress > 0 && this.progress % 100 == 0 || this.getMode() == 3 && this.progress >= 50)) {
         if(this.getMode() != 1 && this.getMode() != 3 && this.progress < 600) {
            this.operate(true);
         } else {
            this.operate(false);
            this.fuelQuality = 0;
            this.progress = 0;
            newActive = false;
         }

         needsInvUpdate = true;
      }

      if(newActive && this.progress != 0) {
         if(!canOperate || super.energy < this.energyconsume) {
            if(!canOperate && this.getMode() != 2) {
               this.fuelQuality = 0;
               this.progress = 0;
            }

            newActive = false;
         }
      } else if(canOperate) {
         if(super.energy >= this.energyconsume) {
            newActive = true;
         }
      } else if(this.getMode() != 2) {
         this.fuelQuality = 0;
         this.progress = 0;
      }

      if(newActive) {
         ++this.progress;
         super.energy -= this.energyconsume;
      }

      if(needsInvUpdate) {
         this.onInventoryChanged();
      }

      if(newActive != this.getActive()) {
         this.setActive(newActive);
      }

   }

   public void operate(boolean incremental) {
      switch(this.getMode()) {
      case 1:
         int food = this.getFoodValue(super.inventory[0]);
         int meta = this.getFoodMeta(super.inventory[0]);
         --super.inventory[0].stackSize;
         if(super.inventory[0].getItem() == Item.bowlSoup && super.inventory[0].stackSize <= 0) {
            super.inventory[0] = new ItemStack(Item.bowlEmpty);
         }

         if(super.inventory[0].stackSize <= 0) {
            super.inventory[0] = null;
         }

         super.inventory[3].stackSize -= food;
         if(super.inventory[3].stackSize <= 0) {
            super.inventory[3] = null;
         }

         if(super.inventory[2] == null) {
            super.inventory[2] = new ItemStack(Ic2Items.filledTinCan.getItem(), food, meta);
         } else {
            super.inventory[2].stackSize += food;
         }
         break;
      case 2:
         int fuel = this.getFuelValue(super.inventory[0].itemID);
         --super.inventory[0].stackSize;
         if(super.inventory[0].stackSize <= 0) {
            super.inventory[0] = null;
         }

         this.fuelQuality += fuel;
         if(!incremental) {
            if(super.inventory[3].getItem() instanceof ItemFuelCanEmpty) {
               --super.inventory[3].stackSize;
               if(super.inventory[3].stackSize <= 0) {
                  super.inventory[3] = null;
               }

               super.inventory[2] = Ic2Items.filledFuelCan.copy();
               NBTTagCompound damage = StackUtil.getOrCreateNbtData(super.inventory[2]);
               damage.setInteger("value", this.fuelQuality);
            } else {
               int var6 = super.inventory[3].getItemDamage();
               var6 -= this.fuelQuality;
               if(var6 < 1) {
                  var6 = 1;
               }

               super.inventory[3] = null;
               super.inventory[2] = new ItemStack(Ic2Items.jetpack.itemID, 1, var6);
            }
         }
         break;
      case 3:
         --super.inventory[0].stackSize;
         super.inventory[3].setItemDamage(super.inventory[3].getItemDamage() - 2);
         if(super.inventory[0].stackSize <= 0) {
            super.inventory[0] = null;
         }

         if(super.inventory[0] == null || super.inventory[3].getItemDamage() <= 1) {
            super.inventory[2] = super.inventory[3];
            super.inventory[3] = null;
         }
      }

   }

   public void onUnloaded() {
      if(this.audioSource != null) {
         IC2.audioManager.removeSources(this);
         this.audioSource = null;
      }

      super.onUnloaded();
   }

   public boolean canOperate() {
      if(super.inventory[0] == null) {
         return false;
      } else {
         switch(this.getMode()) {
         case 1:
            int food = this.getFoodValue(super.inventory[0]);
            if(food > 0 && food <= super.inventory[3].stackSize && (super.inventory[2] == null || super.inventory[2].stackSize + food <= super.inventory[2].getMaxStackSize() && super.inventory[2].itemID == Ic2Items.filledTinCan.itemID && super.inventory[2].getItemDamage() == this.getFoodMeta(super.inventory[0]))) {
               return true;
            }
            break;
         case 2:
            int fuel = this.getFuelValue(super.inventory[0].itemID);
            if(fuel > 0 && super.inventory[2] == null) {
               return true;
            }
            break;
         case 3:
            if(super.inventory[3].getItemDamage() > 2 && this.getPelletValue(super.inventory[0]) > 0 && super.inventory[2] == null) {
               return true;
            }
         }

         return false;
      }
   }

   public int getMode() {
      return super.inventory[3] == null?0:(super.inventory[3].itemID == Ic2Items.tinCan.itemID?1:(!(super.inventory[3].getItem() instanceof ItemFuelCanEmpty) && super.inventory[3].itemID != Ic2Items.jetpack.itemID?(super.inventory[3].itemID == Ic2Items.cfPack.itemID?3:0):2));
   }

   public String getInvName() {
      return "Canning Machine";
   }

   private int getFoodValue(ItemStack item) {
      if(item.getItem() instanceof ItemFood) {
         ItemFood food = (ItemFood)item.getItem();
         return (int)Math.ceil((double)food.getHealAmount() / 2.0D);
      } else {
         return item.itemID != Item.cake.itemID && item.itemID != Block.cake.blockID?0:6;
      }
   }

   public int getFuelValue(int id) {
      return id == Ic2Items.coalfuelCell.itemID?2548:(id == Ic2Items.biofuelCell.itemID?868:(id == Item.redstone.itemID && this.fuelQuality > 0?(int)((double)this.fuelQuality * 0.2D):(id == Item.lightStoneDust.itemID && this.fuelQuality > 0?(int)((double)this.fuelQuality * 0.3D):(id == Item.gunpowder.itemID && this.fuelQuality > 0?(int)((double)this.fuelQuality * 0.4D):0))));
   }

   public int getPelletValue(ItemStack item) {
      return item == null?0:(item.itemID != Ic2Items.constructionFoamPellet.itemID?0:item.stackSize);
   }

   private int getFoodMeta(ItemStack item) {
      if(item == null) {
         return 0;
      } else {
         ChunkCoordIntPair ccip = new ChunkCoordIntPair(item.itemID, item.getItemDamage());
         return specialFood.containsKey(ccip)?((Integer)specialFood.get(ccip)).intValue():0;
      }
   }

   public String getStartSoundFile() {
      return null;
   }

   public String getInterruptSoundFile() {
      return null;
   }

   public ContainerIC2 getGuiContainer(EntityPlayer entityPlayer) {
      return new ContainerCanner(entityPlayer, this);
   }

   public String getGuiClassName(EntityPlayer entityPlayer) {
      return "block.machine.gui.GuiCanner";
   }

   public void onGuiClosed(EntityPlayer entityPlayer) {}

   public int getStartInventorySide(ForgeDirection side) {
      ForgeDirection leftSide;
      switch(this.getFacing()) {
      case 2:
         leftSide = ForgeDirection.WEST;
         break;
      case 3:
         leftSide = ForgeDirection.EAST;
         break;
      case 4:
         leftSide = ForgeDirection.SOUTH;
         break;
      default:
         leftSide = ForgeDirection.NORTH;
      }

      if(side == leftSide) {
         return 1;
      } else {
         switch(TileEntityCanner.NamelessClass1060076626.$SwitchMap$net$minecraftforge$common$ForgeDirection[side.ordinal()]) {
         case 1:
            return 3;
         case 2:
            return 0;
         default:
            return 2;
         }
      }
   }

   public int getSizeInventorySide(ForgeDirection side) {
      return 1;
   }

   public float getWrenchDropRate() {
      return 0.85F;
   }

   static {
      specialFood.put(new ChunkCoordIntPair(Item.rottenFlesh.itemID, 0), Integer.valueOf(1));
      specialFood.put(new ChunkCoordIntPair(Item.spiderEye.itemID, 0), Integer.valueOf(2));
      specialFood.put(new ChunkCoordIntPair(Item.poisonousPotato.itemID, 0), Integer.valueOf(2));
      specialFood.put(new ChunkCoordIntPair(Item.chickenRaw.itemID, 0), Integer.valueOf(3));
      specialFood.put(new ChunkCoordIntPair(Item.appleGold.itemID, 0), Integer.valueOf(4));
      specialFood.put(new ChunkCoordIntPair(Item.appleGold.itemID, 1), Integer.valueOf(5));
   }

   // $FF: synthetic class
   static class NamelessClass1060076626 {

      // $FF: synthetic field
      static final int[] $SwitchMap$net$minecraftforge$common$ForgeDirection = new int[ForgeDirection.values().length];


      static {
         try {
            $SwitchMap$net$minecraftforge$common$ForgeDirection[ForgeDirection.DOWN.ordinal()] = 1;
         } catch (NoSuchFieldError var2) {
            ;
         }

         try {
            $SwitchMap$net$minecraftforge$common$ForgeDirection[ForgeDirection.UP.ordinal()] = 2;
         } catch (NoSuchFieldError var1) {
            ;
         }

      }
   }
}
