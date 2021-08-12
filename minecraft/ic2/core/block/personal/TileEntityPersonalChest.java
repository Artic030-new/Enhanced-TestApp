package ic2.core.block.personal;

import ic2.core.ContainerIC2;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.block.machine.tileentity.TileEntityMachine;
import ic2.core.block.personal.ContainerPersonalChest;
import ic2.core.block.personal.IPersonalBlock;
import java.util.List;
import java.util.Vector;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.ISidedInventory;

public class TileEntityPersonalChest extends TileEntityMachine implements IPersonalBlock, ISidedInventory, IHasGui {

   private int ticksSinceSync;
   private int numUsingPlayers;
   public float lidAngle;
   public float prevLidAngle;
   public String owner = "null";


   public TileEntityPersonalChest() {
      super(54);
   }

   public void readFromNBT(NBTTagCompound nbttagcompound) {
      super.readFromNBT(nbttagcompound);
      this.owner = nbttagcompound.getString("owner");
   }

   public void writeToNBT(NBTTagCompound nbttagcompound) {
      super.writeToNBT(nbttagcompound);
      nbttagcompound.setString("owner", this.owner);
   }

   public String getInvName() {
      return "Personal Safe";
   }

   public boolean canUpdate() {
      return true;
   }

   public void updateEntity() {
      if(++this.ticksSinceSync % 20 * 4 == 0 && IC2.platform.isSimulating()) {
         this.syncNumUsingPlayers();
      }

      this.prevLidAngle = this.lidAngle;
      float var1 = 0.1F;
      double var4;
      if(this.numUsingPlayers > 0 && this.lidAngle == 0.0F) {
         double var8 = (double)super.xCoord + 0.5D;
         var4 = (double)super.zCoord + 0.5D;
         super.worldObj.playSoundEffect(var8, (double)super.yCoord + 0.5D, var4, "random.chestopen", 0.5F, super.worldObj.rand.nextFloat() * 0.1F + 0.9F);
      }

      if(this.numUsingPlayers == 0 && this.lidAngle > 0.0F || this.numUsingPlayers > 0 && this.lidAngle < 1.0F) {
         float var81 = this.lidAngle;
         if(this.numUsingPlayers > 0) {
            this.lidAngle += var1;
         } else {
            this.lidAngle -= var1;
         }

         if(this.lidAngle > 1.0F) {
            this.lidAngle = 1.0F;
         }

         float var3 = 0.5F;
         if(this.lidAngle < var3 && var81 >= var3) {
            var4 = (double)super.xCoord + 0.5D;
            double var6 = (double)super.zCoord + 0.5D;
            super.worldObj.playSoundEffect(var4, (double)super.yCoord + 0.5D, var6, "random.chestclosed", 0.5F, super.worldObj.rand.nextFloat() * 0.1F + 0.9F);
         }

         if(this.lidAngle < 0.0F) {
            this.lidAngle = 0.0F;
         }
      }

   }

   public void openChest() {
      ++this.numUsingPlayers;
      this.syncNumUsingPlayers();
   }

   public void closeChest() {
      --this.numUsingPlayers;
      this.syncNumUsingPlayers();
   }

   public void receiveClientEvent(int event, int data) {
      if(event == 1) {
         this.numUsingPlayers = data;
      }

   }

   private void syncNumUsingPlayers() {
      super.worldObj.addBlockEvent(super.xCoord, super.yCoord, super.zCoord, super.worldObj.getBlockId(super.xCoord, super.yCoord, super.zCoord), 1, this.numUsingPlayers);
   }

   public List getNetworkedFields() {
      Vector ret = new Vector(1);
      ret.add("owner");
      ret.addAll(super.getNetworkedFields());
      return ret;
   }

   public boolean wrenchCanRemove(EntityPlayer entityPlayer) {
      if(!this.canAccess(entityPlayer)) {
         return false;
      } else {
         for(int i = 0; i < super.inventory.length; ++i) {
            if(super.inventory[i] != null) {
               IC2.platform.messagePlayer(entityPlayer, "Can\'t wrench non-empty safe");
               return false;
            }
         }

         return true;
      }
   }

   public boolean canAccess(EntityPlayer player) {
      if(this.owner.equals("null")) {
         this.owner = player.username;
         IC2.network.updateTileEntityField(this, "owner");
         return true;
      } else {
         if(IC2.platform.isSimulating()) {
            MinecraftServer server = MinecraftServer.getServer();
            if(player.username.equals(server.getServerOwner()) || server.getConfigurationManager().areCommandsAllowed(player.username)) {
               return true;
            }
         }

         if(this.owner.equalsIgnoreCase(player.username)) {
            return true;
         } else {
            if(IC2.platform.isSimulating()) {
               IC2.platform.messagePlayer(player, "This safe is owned by " + this.owner);
            }

            return false;
         }
      }
   }

   public int getStartInventorySide(ForgeDirection side) {
      return 0;
   }

   public int getSizeInventorySide(ForgeDirection side) {
      return 0;
   }

   public ContainerIC2 getGuiContainer(EntityPlayer entityPlayer) {
      return new ContainerPersonalChest(entityPlayer, this);
   }

   public String getGuiClassName(EntityPlayer entityPlayer) {
      return "block.personal.GuiPersonalChest";
   }

   public void onGuiClosed(EntityPlayer entityPlayer) {}
}
