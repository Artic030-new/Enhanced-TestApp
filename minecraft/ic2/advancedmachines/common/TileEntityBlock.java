package ic2.advancedmachines.common;

import ic2.api.IWrenchable;
import ic2.api.network.INetworkDataProvider;
import ic2.api.network.INetworkTileEntityEventListener;
import ic2.api.network.NetworkHelper;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public abstract class TileEntityBlock extends TileEntity implements IWrenchable, INetworkDataProvider, INetworkTileEntityEventListener {

   protected boolean created = false;
   public boolean active = false;
   public short facing = 5;
   public boolean prevActive = false;
   public short prevFacing = 0;
   public static List networkedFields;

   public TileEntityBlock() {
      if(networkedFields == null) {
         networkedFields = new ArrayList();
         networkedFields.add("active");
         networkedFields.add("facing");
      }

   }

   public void readFromNBT(NBTTagCompound tagCompound) {
      super.readFromNBT(tagCompound);
      this.prevFacing = this.facing = tagCompound.getShort("facing");
   }

   public void writeToNBT(NBTTagCompound tagCompound) {
      super.writeToNBT(tagCompound);
      tagCompound.setShort("facing", this.facing);
   }

   public void updateEntity() {
      if(!this.created) {
         this.created = true;
         NetworkHelper.requestInitialData(this);
         NetworkHelper.announceBlockUpdate(super.worldObj, super.xCoord, super.yCoord, super.zCoord);
      }

   }

   public boolean getActive() {
      return this.active;
   }

   public void setActive(boolean flag) {
      this.active = flag;
      if(this.prevActive != this.active) {
         NetworkHelper.updateTileEntityField(this, "active");
      }

      this.prevActive = flag;
   }

   public void setActiveWithoutNotify(boolean var1) {
      this.active = var1;
      this.prevActive = var1;
   }

   public short getFacing() {
      return this.facing;
   }

   public boolean wrenchCanSetFacing(EntityPlayer player, int facingToSet) {
      return facingToSet >= 2 && facingToSet != this.facing;
   }

   public void setFacing(short var1) {
      this.facing = var1;
      NetworkHelper.updateTileEntityField(this, "facing");
      this.prevFacing = var1;
      NetworkHelper.announceBlockUpdate(super.worldObj, super.xCoord, super.yCoord, super.zCoord);
   }

   public boolean wrenchCanRemove(EntityPlayer player) {
      return true;
   }

   public float getWrenchDropRate() {
      return 1.0F;
   }

   public ItemStack getWrenchDrop(EntityPlayer player) {
      return new ItemStack(super.worldObj.getBlockId(super.xCoord, super.yCoord, super.zCoord), 1, super.worldObj.getBlockMetadata(super.xCoord, super.yCoord, super.zCoord));
   }

   public List getNetworkedFields() {
      return networkedFields;
   }

   public void onNetworkEvent(int event) {}
}
