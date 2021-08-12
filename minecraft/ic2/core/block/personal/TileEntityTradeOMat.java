package ic2.core.block.personal;

import ic2.api.Direction;
import ic2.api.network.INetworkTileEntityEventListener;
import ic2.core.ContainerIC2;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.audio.PositionSpec;
import ic2.core.block.machine.tileentity.TileEntityMachine;
import ic2.core.block.personal.ContainerTradeOMatClosed;
import ic2.core.block.personal.ContainerTradeOMatOpen;
import ic2.core.block.personal.IPersonalBlock;
import ic2.core.block.personal.TileEntityPersonalChest;
import ic2.core.util.StackUtil;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.ISidedInventory;

public class TileEntityTradeOMat extends TileEntityMachine implements IPersonalBlock, IHasGui, ISidedInventory, INetworkTileEntityEventListener {

   private static final Direction[] directions = Direction.values();
   public static Random randomizer = new Random();
   public String owner = "null";
   public int totalTradeCount = 0;
   public int stock = 0;
   private static final int EventTrade = 0;


   public TileEntityTradeOMat() {
      super(4);
   }

   public void readFromNBT(NBTTagCompound nbttagcompound) {
      super.readFromNBT(nbttagcompound);
      this.owner = nbttagcompound.getString("owner");
      this.totalTradeCount = nbttagcompound.getInteger("totalTradeCount");
   }

   public void writeToNBT(NBTTagCompound nbttagcompound) {
      super.writeToNBT(nbttagcompound);
      nbttagcompound.setString("owner", this.owner);
      nbttagcompound.setInteger("totalTradeCount", this.totalTradeCount);
   }

   public List getNetworkedFields() {
      Vector ret = new Vector(1);
      ret.add("owner");
      ret.addAll(super.getNetworkedFields());
      return ret;
   }

   public void updateEntity() {
      super.updateEntity();
      if(super.inventory[0] != null && super.inventory[1] != null && super.inventory[2] != null && StackUtil.isStackEqual(super.inventory[0], super.inventory[2]) && super.inventory[2].stackSize >= super.inventory[0].stackSize && (super.inventory[3] == null || StackUtil.isStackEqual(super.inventory[1], super.inventory[3]) && super.inventory[3].stackSize + super.inventory[1].stackSize <= super.inventory[3].getMaxStackSize())) {
         boolean tradePerformed = false;
         Direction[] arr$ = directions;
         int len$ = arr$.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            Direction direction = arr$[i$];
            TileEntity target = direction.applyToTileEntity(this);
            if(target instanceof IInventory && (!(target instanceof TileEntityPersonalChest) || ((TileEntityPersonalChest)target).owner.equals(this.owner))) {
               IInventory targetInventory = (IInventory)target;
               if(targetInventory.getSizeInventory() >= 18) {
                  int inputSpace = 0;
                  int outputAvailable = 0;

                  int outputSpace;
                  for(outputSpace = 0; outputSpace < targetInventory.getSizeInventory(); ++outputSpace) {
                     ItemStack tradeCount = targetInventory.getStackInSlot(outputSpace);
                     if(tradeCount == null) {
                        inputSpace += super.inventory[0].getMaxStackSize();
                     } else {
                        if(StackUtil.isStackEqual(tradeCount, super.inventory[0])) {
                           inputSpace += tradeCount.getMaxStackSize() - tradeCount.stackSize;
                        }

                        if(StackUtil.isStackEqual(tradeCount, super.inventory[1])) {
                           outputAvailable += tradeCount.stackSize;
                        }
                     }
                  }

                  outputSpace = super.inventory[3] == null?super.inventory[1].getMaxStackSize():super.inventory[3].getMaxStackSize() - super.inventory[3].stackSize;
                  int var15 = Math.min(Math.min(Math.min(super.inventory[2].stackSize / super.inventory[0].stackSize, inputSpace / super.inventory[0].stackSize), outputSpace / super.inventory[1].stackSize), outputAvailable / super.inventory[1].stackSize);
                  if(var15 > 0) {
                     int inputCount = super.inventory[0].stackSize * var15;
                     int outputCount = super.inventory[1].stackSize * var15;
                     super.inventory[2].stackSize -= inputCount;
                     if(super.inventory[2].stackSize == 0) {
                        super.inventory[2] = null;
                     }

                     ItemStack gs = StackUtil.getFromInventory(targetInventory, new ItemStack(super.inventory[1].itemID, outputCount, super.inventory[1].getItemDamage()));
                     if(gs != null) {
                        if(super.inventory[3] == null) {
                           super.inventory[3] = gs;
                        } else {
                           super.inventory[3].stackSize += gs.stackSize;
                        }
                     }

                     StackUtil.putInInventory(targetInventory, new ItemStack(super.inventory[0].itemID, inputCount, super.inventory[0].getItemDamage()));
                     this.totalTradeCount += var15;
                     tradePerformed = true;
                     IC2.network.initiateTileEntityEvent(this, 0, true);
                     this.onInventoryChanged();
                     break;
                  }
               }
            }
         }

         if(tradePerformed) {
            this.updateStock();
         }
      }

   }

   public void onLoaded() {
      super.onLoaded();
      if(IC2.platform.isSimulating()) {
         this.updateStock();
      }

   }

   public void updateStock() {
      this.stock = 0;
      Direction[] arr$ = directions;
      int len$ = arr$.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         Direction direction = arr$[i$];
         TileEntity target = direction.applyToTileEntity(this);
         if(target instanceof IInventory && (!(target instanceof TileEntityPersonalChest) || ((TileEntityPersonalChest)target).owner.equals(this.owner))) {
            IInventory targetInventory = (IInventory)target;
            if(targetInventory.getSizeInventory() >= 18) {
               for(int i = 0; i < targetInventory.getSizeInventory(); ++i) {
                  ItemStack stack = targetInventory.getStackInSlot(i);
                  if(StackUtil.isStackEqual(super.inventory[1], stack)) {
                     this.stock += stack.stackSize;
                  }
               }
            }
         }
      }

   }

   public boolean wrenchCanRemove(EntityPlayer entityPlayer) {
      return this.canAccess(entityPlayer);
   }

   public boolean canAccess(EntityPlayer player) {
      if(this.owner.equals("null")) {
         this.owner = player.username;
         IC2.network.updateTileEntityField(this, "owner");
         return true;
      } else {
         return this.owner.equalsIgnoreCase(player.username);
      }
   }

   public String getInvName() {
      return "Trade-O-Mat";
   }

   public ContainerIC2 getGuiContainer(EntityPlayer entityPlayer) {
      return (ContainerIC2)(this.canAccess(entityPlayer)?new ContainerTradeOMatOpen(entityPlayer, this):new ContainerTradeOMatClosed(entityPlayer, this));
   }

   public String getGuiClassName(EntityPlayer entityPlayer) {
      return this.canAccess(entityPlayer)?"block.personal.GuiTradeOMatOpen":"block.personal.GuiTradeOMatClosed";
   }

   public void onGuiClosed(EntityPlayer entityPlayer) {}

   public int getStartInventorySide(ForgeDirection side) {
      switch(TileEntityTradeOMat.NamelessClass1046912198.$SwitchMap$net$minecraftforge$common$ForgeDirection[side.ordinal()]) {
      case 1:
         return 3;
      default:
         return 2;
      }
   }

   public int getSizeInventorySide(ForgeDirection side) {
      return 1;
   }

   public void onNetworkEvent(int event) {
      switch(event) {
      case 0:
         IC2.audioManager.playOnce(this, PositionSpec.Center, "Machines/o-mat.ogg", true, IC2.audioManager.defaultVolume);
         break;
      default:
         IC2.platform.displayError("An unknown event type was received over multiplayer.\nThis could happen due to corrupted data or a bug.\n\n(Technical information: event ID " + event + ", tile entity below)\n" + "T: " + this + " (" + super.xCoord + "," + super.yCoord + "," + super.zCoord + ")");
      }

   }


   // $FF: synthetic class
   static class NamelessClass1046912198 {

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
