package ic2.core.block.machine.tileentity;

import ic2.core.ContainerIC2;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.Ic2Items;
import ic2.core.audio.AudioSource;
import ic2.core.audio.PositionSpec;
import ic2.core.block.generator.tileentity.TileEntityGeoGenerator;
import ic2.core.block.machine.ContainerPump;
import ic2.core.block.machine.tileentity.TileEntityElecMachine;
import ic2.core.util.StackUtil;
import java.util.ArrayList;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.ISidedInventory;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.FillBucketEvent;

public class TileEntityPump extends TileEntityElecMachine implements IHasGui, ISidedInventory {

   public int soundTicker;
   public short pumpCharge = 0;
   private AudioSource audioSource;


   public TileEntityPump() {
      super(2, 1, 200, 32);
      this.soundTicker = IC2.random.nextInt(64);
   }

   public String getInvName() {
      return "Pump";
   }

   public void updateEntity() {
      super.updateEntity();
      boolean needsInvUpdate = false;
      if(super.energy > 0 && !this.isPumpReady()) {
         --super.energy;
         ++this.pumpCharge;
      }

      if(super.energy <= super.maxEnergy) {
         needsInvUpdate = this.provideEnergy();
      }

      if(this.isPumpReady()) {
         needsInvUpdate = this.pump();
      }

      if(this.getActive() == this.isPumpReady() && super.energy > 0) {
         this.setActive(!this.getActive());
      }

      if(needsInvUpdate) {
         this.onInventoryChanged();
      }

   }

   public void onUnloaded() {
      if(IC2.platform.isRendering() && this.audioSource != null) {
         IC2.audioManager.removeSources(this);
         this.audioSource = null;
      }

      super.onUnloaded();
   }

   public boolean pump() {
      if(!this.canHarvest()) {
         return false;
      } else if(!this.isWaterBelow() && !this.isLavaBelow()) {
         if(super.inventory[0] != null && super.inventory[0].itemID == Item.bucketEmpty.itemID) {
            ItemStack var3 = new ItemStack(Item.bucketEmpty);
            MinecraftForge.EVENT_BUS.post(new FillBucketEvent((EntityPlayer)null, var3, super.worldObj, new MovingObjectPosition(super.xCoord, super.yCoord - 1, super.zCoord, 1, super.worldObj.getWorldVec3Pool().getVecFromPool((double)super.xCoord, (double)(super.yCoord - 1), (double)super.zCoord))));
            if(var3 != null && var3.itemID != Item.bucketEmpty.itemID) {
               ArrayList drops = new ArrayList();
               drops.add(var3);
               StackUtil.distributeDrop(this, drops);
               --super.inventory[0].stackSize;
               if(super.inventory[0].stackSize <= 0) {
                  super.inventory[0] = null;
               }

               this.pumpCharge = 0;
               return true;
            }
         }

         return false;
      } else {
         int customBucket = super.worldObj.getBlockId(super.xCoord, super.yCoord - 1, super.zCoord);
         super.worldObj.setBlockWithNotify(super.xCoord, super.yCoord - 1, super.zCoord, 0);
         if(customBucket == Block.waterMoving.blockID) {
            customBucket = Block.waterStill.blockID;
         }

         if(customBucket == Block.lavaMoving.blockID) {
            customBucket = Block.lavaStill.blockID;
         }

         return this.pumpThis(customBucket);
      }
   }

   public boolean isWaterBelow() {
      return (super.worldObj.getBlockId(super.xCoord, super.yCoord - 1, super.zCoord) == Block.waterMoving.blockID || super.worldObj.getBlockId(super.xCoord, super.yCoord - 1, super.zCoord) == Block.waterStill.blockID) && super.worldObj.getBlockMetadata(super.xCoord, super.yCoord - 1, super.zCoord) == 0;
   }

   public boolean isLavaBelow() {
      return (super.worldObj.getBlockId(super.xCoord, super.yCoord - 1, super.zCoord) == Block.lavaMoving.blockID || super.worldObj.getBlockId(super.xCoord, super.yCoord - 1, super.zCoord) == Block.lavaStill.blockID) && super.worldObj.getBlockMetadata(super.xCoord, super.yCoord - 1, super.zCoord) == 0;
   }

   public boolean pumpThis(int id) {
      if(id == Block.lavaStill.blockID && this.deliverLavaToGeo()) {
         this.pumpCharge = 0;
         return true;
      } else if(super.inventory[0] != null && super.inventory[0].itemID == Item.bucketEmpty.itemID) {
         if(id == Block.waterStill.blockID) {
            super.inventory[0].itemID = Item.bucketWater.itemID;
         }

         if(id == Block.lavaStill.blockID) {
            super.inventory[0].itemID = Item.bucketLava.itemID;
         }

         ArrayList var4 = new ArrayList();
         var4.add(super.inventory[0]);
         StackUtil.distributeDrop(this, var4);
         super.inventory[0] = null;
         this.pumpCharge = 0;
         return true;
      } else if(super.inventory[0] != null && super.inventory[0].itemID == Ic2Items.cell.itemID) {
         ItemStack drop = null;
         if(id == Block.waterStill.blockID) {
            drop = Ic2Items.waterCell.copy();
         }

         if(id == Block.lavaStill.blockID) {
            drop = Ic2Items.lavaCell.copy();
         }

         --super.inventory[0].stackSize;
         if(super.inventory[0].stackSize <= 0) {
            super.inventory[0] = null;
         }

         ArrayList drops = new ArrayList();
         drops.add(drop);
         StackUtil.distributeDrop(this, drops);
         this.pumpCharge = 0;
         return true;
      } else {
         this.pumpCharge = 0;
         return this.putInChestBucket(id);
      }
   }

   public boolean putInChestBucket(int id) {
      return this.putInChestBucket(super.xCoord, super.yCoord + 1, super.zCoord, id) || this.putInChestBucket(super.xCoord, super.yCoord - 1, super.zCoord, id) || this.putInChestBucket(super.xCoord + 1, super.yCoord, super.zCoord, id) || this.putInChestBucket(super.xCoord - 1, super.yCoord, super.zCoord, id) || this.putInChestBucket(super.xCoord, super.yCoord, super.zCoord + 1, id) || this.putInChestBucket(super.xCoord, super.yCoord, super.zCoord - 1, id);
   }

   public boolean putInChestBucket(int x, int y, int z, int id) {
      if(!(super.worldObj.getBlockTileEntity(x, y, z) instanceof TileEntityChest)) {
         return false;
      } else {
         TileEntityChest chest = (TileEntityChest)super.worldObj.getBlockTileEntity(x, y, z);

         for(int i = 0; i < chest.getSizeInventory(); ++i) {
            if(chest.getStackInSlot(i) != null && chest.getStackInSlot(i).itemID == Item.bucketEmpty.itemID) {
               if(id == Block.waterStill.blockID) {
                  chest.getStackInSlot(i).itemID = Item.bucketWater.itemID;
               }

               if(id == Block.lavaStill.blockID) {
                  chest.getStackInSlot(i).itemID = Item.bucketLava.itemID;
               }

               return true;
            }
         }

         return false;
      }
   }

   public void fountain() {
      if(super.worldObj.getWorldTime() % 10L == 0L) {
         --this.pumpCharge;
      }

      int y = 0;

      for(int x = 1; x < 4; ++x) {
         if(super.worldObj.getBlockId(super.xCoord, super.yCoord + x, super.zCoord) == 0 || super.worldObj.getBlockId(super.xCoord, super.yCoord + x, super.zCoord) == Block.waterMoving.blockID) {
            y = x;
         }
      }

      if(y != 0) {
         super.worldObj.setBlockAndMetadataWithNotify(super.xCoord, super.yCoord + y, super.zCoord, Block.waterMoving.blockID, 1);
      }

   }

   public void readFromNBT(NBTTagCompound nbttagcompound) {
      super.readFromNBT(nbttagcompound);
      this.pumpCharge = nbttagcompound.getShort("pumpCharge");
   }

   public void writeToNBT(NBTTagCompound nbttagcompound) {
      super.writeToNBT(nbttagcompound);
      nbttagcompound.setShort("pumpCharge", this.pumpCharge);
   }

   public boolean isPumpReady() {
      return this.pumpCharge >= 200;
   }

   public boolean canHarvest() {
      return !this.isPumpReady()?false:super.inventory[0] != null && (super.inventory[0].itemID == Ic2Items.cell.itemID || super.inventory[0].itemID == Item.bucketEmpty.itemID) || this.isBucketInChestAvaible();
   }

   public boolean isBucketInChestAvaible() {
      return this.isBucketInChestAvaible(super.xCoord, super.yCoord + 1, super.zCoord) || this.isBucketInChestAvaible(super.xCoord, super.yCoord - 1, super.zCoord) || this.isBucketInChestAvaible(super.xCoord + 1, super.yCoord, super.zCoord) || this.isBucketInChestAvaible(super.xCoord - 1, super.yCoord, super.zCoord) || this.isBucketInChestAvaible(super.xCoord, super.yCoord, super.zCoord + 1) || this.isBucketInChestAvaible(super.xCoord, super.yCoord, super.zCoord - 1);
   }

   public boolean isBucketInChestAvaible(int x, int y, int z) {
      if(!(super.worldObj.getBlockTileEntity(x, y, z) instanceof TileEntityChest)) {
         return false;
      } else {
         TileEntityChest chest = (TileEntityChest)super.worldObj.getBlockTileEntity(x, y, z);

         for(int i = 0; i < chest.getSizeInventory(); ++i) {
            if(chest.getStackInSlot(i) != null && chest.getStackInSlot(i).itemID == Item.bucketEmpty.itemID) {
               return true;
            }
         }

         return false;
      }
   }

   public boolean deliverLavaToGeo() {
      int lava = 3000;
      if(lava > 0 && super.worldObj.getBlockTileEntity(super.xCoord, super.yCoord + 1, super.zCoord) instanceof TileEntityGeoGenerator) {
         lava = ((TileEntityGeoGenerator)super.worldObj.getBlockTileEntity(super.xCoord, super.yCoord + 1, super.zCoord)).distributeLava(lava);
      }

      if(lava > 0 && super.worldObj.getBlockTileEntity(super.xCoord, super.yCoord - 1, super.zCoord) instanceof TileEntityGeoGenerator) {
         lava = ((TileEntityGeoGenerator)super.worldObj.getBlockTileEntity(super.xCoord, super.yCoord - 1, super.zCoord)).distributeLava(lava);
      }

      if(lava > 0 && super.worldObj.getBlockTileEntity(super.xCoord + 1, super.yCoord, super.zCoord) instanceof TileEntityGeoGenerator) {
         lava = ((TileEntityGeoGenerator)super.worldObj.getBlockTileEntity(super.xCoord + 1, super.yCoord, super.zCoord)).distributeLava(lava);
      }

      if(lava > 0 && super.worldObj.getBlockTileEntity(super.xCoord - 1, super.yCoord, super.zCoord) instanceof TileEntityGeoGenerator) {
         lava = ((TileEntityGeoGenerator)super.worldObj.getBlockTileEntity(super.xCoord - 1, super.yCoord, super.zCoord)).distributeLava(lava);
      }

      if(lava > 0 && super.worldObj.getBlockTileEntity(super.xCoord, super.yCoord, super.zCoord + 1) instanceof TileEntityGeoGenerator) {
         lava = ((TileEntityGeoGenerator)super.worldObj.getBlockTileEntity(super.xCoord, super.yCoord, super.zCoord + 1)).distributeLava(lava);
      }

      if(lava > 0 && super.worldObj.getBlockTileEntity(super.xCoord, super.yCoord, super.zCoord - 1) instanceof TileEntityGeoGenerator) {
         lava = ((TileEntityGeoGenerator)super.worldObj.getBlockTileEntity(super.xCoord, super.yCoord, super.zCoord - 1)).distributeLava(lava);
      }

      return lava < 2980;
   }

   public ContainerIC2 getGuiContainer(EntityPlayer entityPlayer) {
      return new ContainerPump(entityPlayer, this);
   }

   public String getGuiClassName(EntityPlayer entityPlayer) {
      return "block.machine.gui.GuiPump";
   }

   public void onGuiClosed(EntityPlayer entityPlayer) {}

   public void onNetworkUpdate(String field) {
      if(field.equals("active") && super.prevActive != this.getActive()) {
         if(this.audioSource == null) {
            this.audioSource = IC2.audioManager.createSource(this, PositionSpec.Center, "Machines/PumpOp.ogg", true, false, IC2.audioManager.defaultVolume);
         }

         if(this.getActive()) {
            if(this.audioSource != null) {
               this.audioSource.play();
            }
         } else if(this.audioSource != null) {
            this.audioSource.stop();
         }
      }

      super.onNetworkUpdate(field);
   }

   public int getStartInventorySide(ForgeDirection side) {
      switch(TileEntityPump.NamelessClass906471226.$SwitchMap$net$minecraftforge$common$ForgeDirection[side.ordinal()]) {
      case 1:
         return 1;
      default:
         return 0;
      }
   }

   public int getSizeInventorySide(ForgeDirection side) {
      return 1;
   }

   // $FF: synthetic class
   static class NamelessClass906471226 {

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
