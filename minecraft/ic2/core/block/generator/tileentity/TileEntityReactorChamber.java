package ic2.core.block.generator.tileentity;

import ic2.api.Direction;
import ic2.api.IReactorChamber;
import ic2.api.IWrenchable;
import ic2.core.IC2;
import ic2.core.ITickCallback;
import ic2.core.block.generator.tileentity.TileEntityNuclearReactor;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public abstract class TileEntityReactorChamber extends TileEntity implements IWrenchable, IInventory, IReactorChamber {

   private static final Direction[] directions = Direction.values();
   private boolean loaded = false;


   public void validate() {
      super.validate();
      if(!this.loaded) {
         if(!this.isInvalid() && super.worldObj != null) {
            if(IC2.platform.isSimulating()) {
               IC2.addSingleTickCallback(super.worldObj, new ITickCallback() {
                  public void tickCallback(World world) {
                     TileEntityReactorChamber.this.onLoaded();
                  }
               });
            } else {
               this.onLoaded();
            }
         } else {
            IC2.log.warning(this + " (" + super.xCoord + "," + super.yCoord + "," + super.zCoord + ") was not added, isInvalid=" + this.isInvalid() + ", worldObj=" + super.worldObj);
         }
      }

   }

   public void invalidate() {
      super.invalidate();
      if(this.loaded) {
         this.onUnloaded();
      }

   }

   public void onChunkUnload() {
      super.onChunkUnload();
      if(this.loaded) {
         this.onUnloaded();
      }

   }

   public void onLoaded() {
      this.loaded = true;
   }

   public void onUnloaded() {
      this.loaded = false;
   }

   public boolean canUpdate() {
      return false;
   }

   public boolean wrenchCanSetFacing(EntityPlayer entityPlayer, int side) {
      return false;
   }

   public short getFacing() {
      return (short)0;
   }

   public void setFacing(short facing) {}

   public boolean wrenchCanRemove(EntityPlayer entityPlayer) {
      return true;
   }

   public float getWrenchDropRate() {
      return 0.8F;
   }

   public ItemStack getWrenchDrop(EntityPlayer entityPlayer) {
      return new ItemStack(super.worldObj.getBlockId(super.xCoord, super.yCoord, super.zCoord), 1, super.worldObj.getBlockMetadata(super.xCoord, super.yCoord, super.zCoord));
   }

   public int getSizeInventory() {
      TileEntityNuclearReactor reactor = this.getReactor();
      return reactor == null?0:reactor.getSizeInventory();
   }

   public ItemStack getStackInSlot(int i) {
      TileEntityNuclearReactor reactor = this.getReactor();
      return reactor == null?null:reactor.getStackInSlot(i);
   }

   public ItemStack decrStackSize(int i, int j) {
      TileEntityNuclearReactor reactor = this.getReactor();
      return reactor == null?null:reactor.decrStackSize(i, j);
   }

   public void setInventorySlotContents(int i, ItemStack itemstack) {
      TileEntityNuclearReactor reactor = this.getReactor();
      if(reactor != null) {
         reactor.setInventorySlotContents(i, itemstack);
      }
   }

   public String getInvName() {
      TileEntityNuclearReactor reactor = this.getReactor();
      return reactor == null?"Nuclear Reactor":reactor.getInvName();
   }

   public int getInventoryStackLimit() {
      TileEntityNuclearReactor reactor = this.getReactor();
      return reactor == null?64:reactor.getInventoryStackLimit();
   }

   public boolean isUseableByPlayer(EntityPlayer entityplayer) {
      TileEntityNuclearReactor reactor = this.getReactor();
      return reactor == null?false:reactor.isUseableByPlayer(entityplayer);
   }

   public void openChest() {
      TileEntityNuclearReactor reactor = this.getReactor();
      if(reactor != null) {
         reactor.openChest();
      }
   }

   public void closeChest() {
      TileEntityNuclearReactor reactor = this.getReactor();
      if(reactor != null) {
         reactor.closeChest();
      }
   }

   public ItemStack getStackInSlotOnClosing(int var1) {
      TileEntityNuclearReactor reactor = this.getReactor();
      return reactor == null?null:reactor.getStackInSlotOnClosing(var1);
   }

   public TileEntityNuclearReactor getReactor() {
      Direction[] blk = directions;
      int len$ = blk.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         Direction value = blk[i$];
         TileEntity te = value.applyToTileEntity(this);
         if(te instanceof TileEntityNuclearReactor) {
            return (TileEntityNuclearReactor)te;
         }
      }

      Block var6 = Block.blocksList[super.worldObj.getBlockId(super.xCoord, super.yCoord, super.zCoord)];
      if(var6 != null) {
         var6.onNeighborBlockChange(super.worldObj, super.xCoord, super.yCoord, super.zCoord, var6.blockID);
      }

      return null;
   }

   public abstract int sendEnergy(int var1);

}
