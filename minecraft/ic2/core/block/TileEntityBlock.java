package ic2.core.block;

import ic2.api.IWrenchable;
import ic2.api.network.INetworkDataProvider;
import ic2.api.network.INetworkUpdateListener;
import ic2.core.IC2;
import ic2.core.ITickCallback;
import java.util.List;
import java.util.Vector;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class TileEntityBlock extends TileEntity implements INetworkDataProvider, INetworkUpdateListener, IWrenchable {

   private boolean active = false;
   private short facing = 0;
   public boolean prevActive = false;
   public short prevFacing = 0;
   public boolean loaded = false;


   public void validate() {
      super.validate();
      if(!this.loaded) {
         if(!this.isInvalid() && super.worldObj != null) {
            if(IC2.platform.isSimulating()) {
               IC2.addSingleTickCallback(super.worldObj, new ITickCallback() {
                  public void tickCallback(World world) {
                     TileEntityBlock.this.onLoaded();
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
      if(this.loaded) {
         this.onUnloaded();
      }

      super.invalidate();
   }

   public void onChunkUnload() {
      if(this.loaded) {
         this.onUnloaded();
      }

      super.onChunkUnload();
   }

   public void onLoaded() {
      if(!IC2.platform.isSimulating()) {
         IC2.network.requestInitialData(this);
      }

      this.loaded = true;
   }

   public void onUnloaded() {
      this.loaded = false;
   }

   public void readFromNBT(NBTTagCompound nbttagcompound) {
      super.readFromNBT(nbttagcompound);
      this.prevFacing = this.facing = nbttagcompound.getShort("facing");
   }

   public void writeToNBT(NBTTagCompound nbttagcompound) {
      super.writeToNBT(nbttagcompound);
      nbttagcompound.setShort("facing", this.facing);
   }

   public boolean canUpdate() {
      return false;
   }

   public boolean getActive() {
      return this.active;
   }

   public void setActive(boolean active) {
      this.active = active;
      if(this.prevActive != active) {
         IC2.network.updateTileEntityField(this, "active");
      }

      this.prevActive = active;
   }

   public void setActiveWithoutNotify(boolean active) {
      this.active = active;
      this.prevActive = active;
   }

   public short getFacing() {
      return this.facing;
   }

   public List getNetworkedFields() {
      Vector ret = new Vector(2);
      ret.add("active");
      ret.add("facing");
      return ret;
   }

   public void onNetworkUpdate(String field) {
      if(field.equals("active") && this.prevActive != this.active || field.equals("facing") && this.prevFacing != this.facing) {
         int blockId = super.worldObj.getBlockId(super.xCoord, super.yCoord, super.zCoord);
         if(blockId < Block.blocksList.length && Block.blocksList[blockId] != null) {
            Block block = Block.blocksList[blockId];
            boolean newActive = this.active;
            short newFacing = this.facing;
            this.active = this.prevActive;
            this.facing = this.prevFacing;
            int[] textureIndex = new int[6];

            int side;
            for(side = 0; side < 6; ++side) {
               textureIndex[side] = IC2.platform.getBlockTexture(block, super.worldObj, super.xCoord, super.yCoord, super.zCoord, side);
            }

            this.active = newActive;
            this.facing = newFacing;

            for(side = 0; side < 6; ++side) {
               int newTextureIndex = IC2.platform.getBlockTexture(block, super.worldObj, super.xCoord, super.yCoord, super.zCoord, side);
               if(textureIndex[side] != newTextureIndex && IC2.textureIndex.get(blockId, textureIndex[side]) != IC2.textureIndex.get(blockId, newTextureIndex)) {
                  super.worldObj.markBlockForUpdate(super.xCoord, super.yCoord, super.zCoord);
                  break;
               }
            }
         } else {
            System.out.println("[IC2] Invalid TE at " + super.xCoord + "/" + super.yCoord + "/" + super.zCoord + ", no corresponding block");
         }

         this.prevActive = this.active;
         this.prevFacing = this.facing;
      }

   }

   public boolean wrenchCanSetFacing(EntityPlayer entityPlayer, int side) {
      return false;
   }

   public void setFacing(short facing) {
      this.facing = facing;
      if(this.prevFacing != facing) {
         IC2.network.updateTileEntityField(this, "facing");
      }

      this.prevFacing = facing;
   }

   public boolean wrenchCanRemove(EntityPlayer entityPlayer) {
      return true;
   }

   public float getWrenchDropRate() {
      return 1.0F;
   }

   public ItemStack getWrenchDrop(EntityPlayer entityPlayer) {
      return new ItemStack(super.worldObj.getBlockId(super.xCoord, super.yCoord, super.zCoord), 1, super.worldObj.getBlockMetadata(super.xCoord, super.yCoord, super.zCoord));
   }

   public void onBlockBreak(int a, int b) {}
}
