package cpw.mods.ironchest;

import cpw.mods.ironchest.BlockIronChest;
import cpw.mods.ironchest.ContainerIronChestBase;
import cpw.mods.ironchest.IronChest;
import cpw.mods.ironchest.IronChestType;
import cpw.mods.ironchest.ItemChestChanger;
import cpw.mods.ironchest.PacketHandler;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.packet.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

public class TileEntityIronChest extends TileEntity implements IInventory {

   private int ticksSinceSync;
   public float prevLidAngle;
   public float lidAngle;
   private int numUsingPlayers;
   private IronChestType type;
   public ItemStack[] chestContents;
   private ItemStack[] topStacks;
   private byte facing;
   private boolean inventoryTouched;
   private boolean hadStuff;

   public TileEntityIronChest() {
      this(IronChestType.IRON);
   }

   protected TileEntityIronChest(IronChestType type) {
      this.ticksSinceSync = -1;
      this.type = type;
      this.chestContents = new ItemStack[this.getSizeInventory()];
      this.topStacks = new ItemStack[8];
   }

   public ItemStack[] getContents() {
      return this.chestContents;
   }

   public int getSizeInventory() {
      return this.type.size;
   }

   public byte getFacing() {
      return this.facing;
   }

   public String getInvName() {
      return this.type.name();
   }

   public IronChestType getType() {
      return this.type;
   }

   public ItemStack getStackInSlot(int i) {
      this.inventoryTouched = true;
      return this.chestContents[i];
   }

   public void onInventoryChanged() {
      super.onInventoryChanged();
      this.sortTopStacks();
   }

   protected void sortTopStacks() {
      if(this.type.isTransparent() && (super.worldObj == null || !super.worldObj.isRemote)) {
         ItemStack[] tempCopy = new ItemStack[this.getSizeInventory()];
         boolean hasStuff = false;
         int compressedIdx = 0;

         int p;
         int i;
         label79:
         for(p = 0; p < this.getSizeInventory(); ++p) {
            if(this.chestContents[p] != null) {
               for(i = 0; i < compressedIdx; ++i) {
                  if(tempCopy[i].isItemEqual(this.chestContents[p])) {
                     tempCopy[i].stackSize += this.chestContents[p].stackSize;
                     continue label79;
                  }
               }

               tempCopy[compressedIdx++] = this.chestContents[p].copy();
               hasStuff = true;
            }
         }

         if(!hasStuff && this.hadStuff) {
            this.hadStuff = false;

            for(p = 0; p < this.topStacks.length; ++p) {
               this.topStacks[p] = null;
            }

            if(super.worldObj != null) {
               super.worldObj.markBlockForUpdate(super.xCoord, super.yCoord, super.zCoord);
            }

         } else {
            this.hadStuff = true;
            Arrays.sort(tempCopy, new Comparator<ItemStack>() {
               public int compare(ItemStack o1, ItemStack o2) {
                  return o1 == null?1:(o2 == null?-1:o2.stackSize - o1.stackSize);
               }
            });
            p = 0;

            for(i = 0; i < tempCopy.length; ++i) {
               if(tempCopy[i] != null && tempCopy[i].stackSize > 0) {
                  this.topStacks[p++] = tempCopy[i];
                  if(p == this.topStacks.length) {
                     break;
                  }
               }
            }

            for(i = p; i < this.topStacks.length; ++i) {
               this.topStacks[i] = null;
            }

            if(super.worldObj != null) {
               super.worldObj.markBlockForUpdate(super.xCoord, super.yCoord, super.zCoord);
            }

         }
      }
   }

   public ItemStack decrStackSize(int i, int j) {
      if(this.chestContents[i] != null) {
         ItemStack itemstack1;
         if(this.chestContents[i].stackSize <= j) {
            itemstack1 = this.chestContents[i];
            this.chestContents[i] = null;
            this.onInventoryChanged();
            return itemstack1;
         } else {
            itemstack1 = this.chestContents[i].splitStack(j);
            if(this.chestContents[i].stackSize == 0) {
               this.chestContents[i] = null;
            }

            this.onInventoryChanged();
            return itemstack1;
         }
      } else {
         return null;
      }
   }

   public void setInventorySlotContents(int i, ItemStack itemstack) {
      this.chestContents[i] = itemstack;
      if(itemstack != null && itemstack.stackSize > this.getInventoryStackLimit()) {
         itemstack.stackSize = this.getInventoryStackLimit();
      }

      this.onInventoryChanged();
   }

   public void readFromNBT(NBTTagCompound tagCompound) {
      super.readFromNBT(tagCompound);
      NBTTagList nbttaglist = tagCompound.getTagList("Items");
      this.chestContents = new ItemStack[this.getSizeInventory()];

      for(int i = 0; i < nbttaglist.tagCount(); ++i) {
         NBTTagCompound nbttagcompound1 = (NBTTagCompound)nbttaglist.tagAt(i);
         int j = nbttagcompound1.getByte("Slot") & 255;
         if(j >= 0 && j < this.chestContents.length) {
            this.chestContents[j] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
         }
      }

      this.facing = tagCompound.getByte("facing");
      this.sortTopStacks();
   }

   public void writeToNBT(NBTTagCompound tagCompound) {
      super.writeToNBT(tagCompound);
      NBTTagList nbttaglist = new NBTTagList();

      for(int i = 0; i < this.chestContents.length; ++i) {
         if(this.chestContents[i] != null) {
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();
            nbttagcompound1.setByte("Slot", (byte)i);
            this.chestContents[i].writeToNBT(nbttagcompound1);
            nbttaglist.appendTag(nbttagcompound1);
         }
      }

      tagCompound.setTag("Items", nbttaglist);
      tagCompound.setByte("facing", this.facing);
   }

   public int getInventoryStackLimit() {
      return 64;
   }

   public boolean isUseableByPlayer(EntityPlayer entityplayer) {
      return super.worldObj == null?true:(super.worldObj.getBlockTileEntity(super.xCoord, super.yCoord, super.zCoord) != this?false:entityplayer.getDistanceSq((double)super.xCoord + 0.5D, (double)super.yCoord + 0.5D, (double)super.zCoord + 0.5D) <= 64.0D);
   }

   public void updateEntity() {
      super.updateEntity();
      float f;
      if(super.worldObj != null && !super.worldObj.isRemote && this.numUsingPlayers != 0 && (this.ticksSinceSync + super.xCoord + super.yCoord + super.zCoord) % 200 == 0) {
         this.numUsingPlayers = 0;
         f = 5.0F;
         List f1 = super.worldObj.getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.getAABBPool().addOrModifyAABBInPool((double)((float)super.xCoord - f), (double)((float)super.yCoord - f), (double)((float)super.zCoord - f), (double)((float)(super.xCoord + 1) + f), (double)((float)(super.yCoord + 1) + f), (double)((float)(super.zCoord + 1) + f)));
         Iterator f2 = f1.iterator();

         while(f2.hasNext()) {
            EntityPlayer d2 = (EntityPlayer)f2.next();
            if(d2.openContainer instanceof ContainerIronChestBase) {
               ++this.numUsingPlayers;
            }
         }
      }

      if(super.worldObj != null && !super.worldObj.isRemote && this.ticksSinceSync < 0) {
         super.worldObj.addBlockEvent(super.xCoord, super.yCoord, super.zCoord, IronChest.ironChestBlock.blockID, 3, this.numUsingPlayers << 3 & 248 | this.facing & 7);
      }

      if(!super.worldObj.isRemote && this.inventoryTouched) {
         this.inventoryTouched = false;
         this.sortTopStacks();
      }

      ++this.ticksSinceSync;
      this.prevLidAngle = this.lidAngle;
      f = 0.1F;
      double var11;
      if(this.numUsingPlayers > 0 && this.lidAngle == 0.0F) {
         double var8 = (double)super.xCoord + 0.5D;
         var11 = (double)super.zCoord + 0.5D;
         super.worldObj.playSoundEffect(var8, (double)super.yCoord + 0.5D, var11, "random.chestopen", 0.5F, super.worldObj.rand.nextFloat() * 0.1F + 0.9F);
      }

      if(this.numUsingPlayers == 0 && this.lidAngle > 0.0F || this.numUsingPlayers > 0 && this.lidAngle < 1.0F) {
         float var9 = this.lidAngle;
         if(this.numUsingPlayers > 0) {
            this.lidAngle += f;
         } else {
            this.lidAngle -= f;
         }

         if(this.lidAngle > 1.0F) {
            this.lidAngle = 1.0F;
         }

         float var10 = 0.5F;
         if(this.lidAngle < var10 && var9 >= var10) {
            var11 = (double)super.xCoord + 0.5D;
            double d3 = (double)super.zCoord + 0.5D;
            super.worldObj.playSoundEffect(var11, (double)super.yCoord + 0.5D, d3, "random.chestclosed", 0.5F, super.worldObj.rand.nextFloat() * 0.1F + 0.9F);
         }

         if(this.lidAngle < 0.0F) {
            this.lidAngle = 0.0F;
         }
      }

   }

   public void receiveClientEvent(int i, int j) {
      if(i == 1) {
         this.numUsingPlayers = j;
      } else if(i == 2) {
         this.facing = (byte)j;
      } else if(i == 3) {
         this.facing = (byte)(j & 7);
         this.numUsingPlayers = (j & 248) >> 3;
      }

   }

   public void openChest() {
      if(super.worldObj != null) {
         ++this.numUsingPlayers;
         super.worldObj.addBlockEvent(super.xCoord, super.yCoord, super.zCoord, IronChest.ironChestBlock.blockID, 1, this.numUsingPlayers);
      }
   }

   public void closeChest() {
      if(super.worldObj != null) {
         --this.numUsingPlayers;
         super.worldObj.addBlockEvent(super.xCoord, super.yCoord, super.zCoord, IronChest.ironChestBlock.blockID, 1, this.numUsingPlayers);
      }
   }

   public void setFacing(byte chestFacing) {
      this.facing = chestFacing;
   }

   public TileEntityIronChest applyUpgradeItem(ItemChestChanger itemChestChanger) {
      if(this.numUsingPlayers > 0) {
         return null;
      } else if(!itemChestChanger.getType().canUpgrade(this.getType())) {
         return null;
      } else {
         TileEntityIronChest newEntity = IronChestType.makeEntity(itemChestChanger.getTargetChestOrdinal(this.getType().ordinal()));
         int newSize = newEntity.chestContents.length;
         System.arraycopy(this.chestContents, 0, newEntity.chestContents, 0, Math.min(newSize, this.chestContents.length));
         BlockIronChest block = IronChest.ironChestBlock;
         block.dropContent(newSize, this, super.worldObj, super.xCoord, super.yCoord, super.zCoord);
         newEntity.setFacing(this.facing);
         newEntity.sortTopStacks();
         newEntity.ticksSinceSync = -1;
         return newEntity;
      }
   }

   public ItemStack[] getTopItemStacks() {
      return this.topStacks;
   }

   public TileEntityIronChest updateFromMetadata(int l) {
      if(super.worldObj != null && super.worldObj.isRemote && l != this.type.ordinal()) {
         super.worldObj.setBlockTileEntity(super.xCoord, super.yCoord, super.zCoord, IronChestType.makeEntity(l));
         return (TileEntityIronChest)super.worldObj.getBlockTileEntity(super.xCoord, super.yCoord, super.zCoord);
      } else {
         return this;
      }
   }

   public Packet getDescriptionPacket() {
      return PacketHandler.getPacket(this);
   }

   public void handlePacketData(int typeData, int[] intData) {
      TileEntityIronChest chest = this;
      if(this.type.ordinal() != typeData) {
         chest = this.updateFromMetadata(typeData);
      }

      if(IronChestType.values()[typeData].isTransparent() && intData != null) {
         int pos = 0;
         if(intData.length < chest.topStacks.length * 3) {
            return;
         }

         for(int i = 0; i < chest.topStacks.length; ++i) {
            if(intData[pos + 2] != 0) {
               ItemStack is = new ItemStack(intData[pos], intData[pos + 2], intData[pos + 1]);
               chest.topStacks[i] = is;
            } else {
               chest.topStacks[i] = null;
            }

            pos += 3;
         }
      }

   }

   public int[] buildIntDataList() {
      if(this.type.isTransparent()) {
         int[] sortList = new int[this.topStacks.length * 3];
         int pos = 0;
         ItemStack[] arr$ = this.topStacks;
         int len$ = arr$.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            ItemStack is = arr$[i$];
            if(is != null) {
               sortList[pos++] = is.itemID;
               sortList[pos++] = is.getItemDamage();
               sortList[pos++] = is.stackSize;
            } else {
               sortList[pos++] = 0;
               sortList[pos++] = 0;
               sortList[pos++] = 0;
            }
         }

         return sortList;
      } else {
         return null;
      }
   }

   public ItemStack getStackInSlotOnClosing(int par1) {
      if(this.chestContents[par1] != null) {
         ItemStack var2 = this.chestContents[par1];
         this.chestContents[par1] = null;
         return var2;
      } else {
         return null;
      }
   }

   public void setMaxStackSize(int size) {}
}
