package ic2.core.block.machine.tileentity;

import ic2.api.Direction;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySink;
import ic2.core.IC2;
import ic2.core.IC2DamageSource;
import ic2.core.block.TileEntityBlock;
import ic2.core.item.armor.ItemArmorHazmat;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.MinecraftForge;

public class TileEntityTesla extends TileEntityBlock implements IEnergySink {

   public int energy = 0;
   public int ticker = 0;
   public int maxEnergy = 10000;
   public int maxInput = 128;
   public boolean addedToEnergyNet = false;


   public void readFromNBT(NBTTagCompound nbttagcompound) {
      super.readFromNBT(nbttagcompound);
      this.energy = nbttagcompound.getShort("energy");
   }

   public void writeToNBT(NBTTagCompound nbttagcompound) {
      super.writeToNBT(nbttagcompound);
      nbttagcompound.setShort("energy", (short)this.energy);
   }

   public void onLoaded() {
      super.onLoaded();
      if(IC2.platform.isSimulating()) {
         MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
         this.addedToEnergyNet = true;
      }

   }

   public void onUnloaded() {
      if(IC2.platform.isSimulating() && this.addedToEnergyNet) {
         MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
         this.addedToEnergyNet = false;
      }

      super.onUnloaded();
   }

   public boolean canUpdate() {
      return IC2.platform.isSimulating();
   }

   public void updateEntity() {
      super.updateEntity();
      if(IC2.platform.isSimulating() && this.redstoned()) {
         if(this.energy >= getCost()) {
            int damage = this.energy / getCost();
            --this.energy;
            if(this.ticker++ % 32 == 0 && this.shock(damage)) {
               this.energy = 0;
            }

         }
      }
   }

   public boolean shock(int damage) {
      boolean shock = false;
      Class entity = EntityLiving.class;
      
      List list1 = super.worldObj.getEntitiesWithinAABB(entity, AxisAlignedBB.getBoundingBox((double)(super.xCoord - 4), (double)(super.yCoord - 4), (double)(super.zCoord - 4), (double)(super.xCoord + 5), (double)(super.yCoord + 5), (double)(super.zCoord + 5)));

      for(int l = 0; l < list1.size(); ++l) {
         EntityLiving victim = (EntityLiving)list1.get(l);
         if(!ItemArmorHazmat.hasCompleteHazmat(victim)) {
            shock = true;
           if (!(victim instanceof EntityPlayer))
           {
            victim.attackEntityFrom(IC2DamageSource.electricity, damage);
           }
         }
      }

      return shock == false;
   }

   public boolean redstoned() {
      return super.worldObj.isBlockIndirectlyGettingPowered(super.xCoord, super.yCoord, super.zCoord) || super.worldObj.isBlockIndirectlyGettingPowered(super.xCoord, super.yCoord, super.zCoord);
   }

   public static int getCost() {
      return 400;
   }

   public boolean isAddedToEnergyNet() {
      return this.addedToEnergyNet;
   }

   public boolean acceptsEnergyFrom(TileEntity emitter, Direction direction) {
      return true;
   }

   public int demandsEnergy() {
      return this.maxEnergy - this.energy;
   }

   public int injectEnergy(Direction directionFrom, int amount) {
      if(amount > this.maxInput) {
         IC2.explodeMachineAt(super.worldObj, super.xCoord, super.yCoord, super.zCoord);
         return 0;
      } else {
         int need = amount;
         if(this.energy + amount >= this.maxEnergy + this.maxInput) {
            need = this.maxEnergy + this.maxInput - this.energy - 1;
         }

         this.energy += need;
         return amount - need;
      }
   }

   public int getMaxSafeInput() {
      return this.maxInput;
   }
  
}
