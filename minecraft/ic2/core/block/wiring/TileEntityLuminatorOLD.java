package ic2.core.block.wiring;

import ic2.api.Direction;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergyConductor;
import ic2.api.energy.tile.IEnergySink;
import ic2.core.ExplosionIC2;
import ic2.core.IC2;
import ic2.core.ITickCallback;
import ic2.core.Ic2Items;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class TileEntityLuminatorOLD extends TileEntity implements IEnergySink, IEnergyConductor {

   public int energy = 0;
   public int mode = 0;
   public boolean powered = false;
   public int ticker = 0;
   public int maxInput = 32;
   public boolean addedToEnergyNet = false;
   private boolean loaded = false;


   public void readFromNBT(NBTTagCompound nbttagcompound) {
      super.readFromNBT(nbttagcompound);
      this.energy = nbttagcompound.getShort("energy");
      this.mode = nbttagcompound.getShort("mode");
      this.powered = nbttagcompound.getBoolean("powered");
   }

   public void writeToNBT(NBTTagCompound nbttagcompound) {
      super.writeToNBT(nbttagcompound);
      nbttagcompound.setShort("energy", (short)this.energy);
      nbttagcompound.setShort("mode", (short)this.mode);
      nbttagcompound.setBoolean("poweredy", this.powered);
   }

   public void validate() {
      super.validate();
      if(!this.loaded) {
         if(!this.isInvalid() && super.worldObj != null) {
            if(IC2.platform.isSimulating()) {
               IC2.addSingleTickCallback(super.worldObj, new ITickCallback() {
                  public void tickCallback(World world) {
                     TileEntityLuminatorOLD.this.onLoaded();
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
      if(IC2.platform.isSimulating() && !this.addedToEnergyNet) {
         MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
         this.addedToEnergyNet = true;
      }

      this.loaded = true;
   }

   public void onUnloaded() {
      if(IC2.platform.isSimulating() && this.addedToEnergyNet) {
         MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
         this.addedToEnergyNet = false;
      }

   }

   public void updateEntity() {
      if(IC2.platform.isSimulating()) {
         ++this.ticker;
         if(this.ticker % 20 == 0) {
            if(this.ticker % 160 == 0) {
               System.out.println("Consume for Mode: " + this.mode);
               byte consume = 5;
               switch(this.mode) {
               case 1:
                  boolean var2 = true;
               case 2:
                  consume = 40;
               default:
                  if(consume > this.energy) {
                     this.energy = 0;
                     this.powered = false;
                     System.out.println("Out of energy");
                  } else {
                     System.out.println("Energized");
                     this.energy -= consume;
                     this.powered = true;
                  }

                  this.updateLightning();
               }
            }

            if(this.powered) {
               this.burnMobs();
            }
         }
      }

   }

   public float getLightLevel() {
      if(this.powered) {
         System.out.println("get powered");
      }

      System.out.println("get unpowered");
      return 0.9375F;
   }

   public void switchStrength() {
      this.mode = (this.mode + 1) % 3;
      this.updateLightning();
   }

   public void updateLightning() {
      System.out.println("Update Lightning");
      super.worldObj.updateLightByType(EnumSkyBlock.Sky, super.xCoord, super.yCoord, super.zCoord);
      super.worldObj.updateLightByType(EnumSkyBlock.Block, super.xCoord, super.yCoord, super.zCoord);
   }

   public boolean isAddedToEnergyNet() {
      return this.addedToEnergyNet;
   }

   public boolean acceptsEnergyFrom(TileEntity emitter, Direction direction) {
      return true;
   }

   public boolean emitsEnergyTo(TileEntity receiver, Direction direction) {
      return true;
   }

   public double getConductionLoss() {
      return 0.0D;
   }

   public int getInsulationEnergyAbsorption() {
      return this.maxInput;
   }

   public int getInsulationBreakdownEnergy() {
      return this.maxInput + 1;
   }

   public int getConductorBreakdownEnergy() {
      return this.maxInput + 1;
   }

   public void removeInsulation() {
      System.out.println("REmove Insulation");
      this.poof();
   }

   public void removeConductor() {
      System.out.println("REmove Confuctor");
      this.poof();
   }

   public int demandsEnergy() {
      return this.getMaxEnergy() - this.energy;
   }

   public int injectEnergy(Direction directionFrom, int amount) {
      if(amount > this.maxInput) {
         System.out.println("Injecting > " + this.maxInput);
         this.poof();
         return 0;
      } else {
         int need = amount;
         if(this.energy + amount >= this.getMaxEnergy() + this.maxInput) {
            need = this.getMaxEnergy() + this.maxInput - this.energy - 1;
         }

         this.energy += need;
         return amount - need;
      }
   }

   public int getMaxSafeInput() {
      return this.maxInput;
   }

   public int getMaxEnergy() {
      switch(this.mode) {
      case 1:
         return 20;
      case 2:
         return 80;
      default:
         return 10;
      }
   }

   public void poof() {
      super.worldObj.setBlockWithNotify(super.xCoord, super.yCoord, super.zCoord, 0);
      ExplosionIC2 explosion = new ExplosionIC2(super.worldObj, (Entity)null, 0.5D + (double)super.xCoord, 0.5D + (double)super.yCoord, 0.5D + (double)super.zCoord, 0.5F, 0.85F, 2.0F);
      explosion.doExplosion();
   }

   public void burnMobs() {
      int x = super.xCoord;
      int y = super.yCoord;
      int z = super.zCoord;
      boolean xplus = false;
      boolean xminus = false;
      boolean yplus = false;
      boolean yminus = false;
      boolean zplus = false;
      boolean zminus = false;
      if(super.worldObj.getBlockId(x + 1, y, z) == 0 || super.worldObj.getBlockId(x + 1, y, z) == Block.glass.blockID || super.worldObj.getBlockId(x + 1, y, z) == Ic2Items.reinforcedGlass.itemID) {
         xplus = true;
      }

      if(super.worldObj.getBlockId(x - 1, y, z) == 0 || super.worldObj.getBlockId(x - 1, y, z) == Block.glass.blockID || super.worldObj.getBlockId(x - 1, y, z) == Ic2Items.reinforcedGlass.itemID) {
         xminus = true;
      }

      if(super.worldObj.getBlockId(x, y + 1, z) == 0 || super.worldObj.getBlockId(x, y + 1, z) == Block.glass.blockID || super.worldObj.getBlockId(x, y + 1, z) == Ic2Items.reinforcedGlass.itemID) {
         yplus = true;
      }

      if(super.worldObj.getBlockId(x, y - 1, z) == 0 || super.worldObj.getBlockId(x, y - 1, z) == Block.glass.blockID || super.worldObj.getBlockId(x, y - 1, z) == Ic2Items.reinforcedGlass.itemID) {
         yminus = true;
      }

      if(super.worldObj.getBlockId(x, y, z + 1) == 0 || super.worldObj.getBlockId(x, y, z + 1) == Block.glass.blockID || super.worldObj.getBlockId(x, y, z + 1) == Ic2Items.reinforcedGlass.itemID) {
         zplus = true;
      }

      if(super.worldObj.getBlockId(x, y, z - 1) == 0 || super.worldObj.getBlockId(x, y, z - 1) == Block.glass.blockID || super.worldObj.getBlockId(x, y, z - 1) == Ic2Items.reinforcedGlass.itemID) {
         zminus = true;
      }

      byte xplusI = 0;
      byte xminusI = 0;
      byte yplusI = 0;
      byte yminusI = 0;
      byte zplusI = 0;
      byte zminusI = 0;
      if(xplus) {
         xplusI = 3;
      } else if(yplus || yminus || zplus || zminus) {
         xplusI = 1;
      }

      if(xminus) {
         xminusI = 3;
      } else if(yplus || yminus || zplus || zminus) {
         xminusI = 1;
      }

      if(yplus) {
         yplusI = 3;
      } else if(xplus || xminus || zplus || zminus) {
         yplusI = 1;
      }

      if(yminus) {
         yminusI = 3;
      } else if(xplus || xminus || zplus || zminus) {
         yminusI = 1;
      }

      if(zplus) {
         zplusI = 3;
      } else if(yplus || yminus || xplus || xminus) {
         zplusI = 1;
      }

      if(zminus) {
         zminusI = 3;
      } else if(yplus || yminus || xplus || xminus) {
         zminusI = 1;
      }

      int var27 = x - xminusI;
      int var30 = y - yminusI;
      int var31 = z - zminusI;
      int var26 = x + xplusI;
      int var28 = y + yplusI;
      int var29 = z + zplusI;
      AxisAlignedBB boundingBox = AxisAlignedBB.getBoundingBox(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
      List list1 = super.worldObj.getEntitiesWithinAABBExcludingEntity((Entity)null, boundingBox.addCoord((double)x, (double)y, (double)z).expand(3.0D, 3.0D, 3.0D));

      for(int l = 0; l < list1.size(); ++l) {
         Entity ent = (Entity)list1.get(l);
         if(ent instanceof EntityMob) {
            double ex = ent.posX;
            double ey = ent.posY;
            double ez = ent.posZ;
            if(ex >= (double)var27 && ex <= (double)(var26 + 1) && ey >= (double)var30 && ey <= (double)(var28 + 2) && ez >= (double)var31 && ez <= (double)(var29 + 1)) {
               ent.setFire(10);
            }
         }
      }

   }
}
