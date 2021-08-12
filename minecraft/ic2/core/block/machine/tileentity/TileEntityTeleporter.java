package ic2.core.block.machine.tileentity;

import ic2.api.Direction;
import ic2.api.IEnergyStorage;
import ic2.api.network.INetworkTileEntityEventListener;
import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.audio.AudioPosition;
import ic2.core.audio.AudioSource;
import ic2.core.audio.PositionSpec;
import ic2.core.block.TileEntityBlock;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.chunk.Chunk;

public class TileEntityTeleporter extends TileEntityBlock implements INetworkTileEntityEventListener {

   private static final Direction[] directions = Direction.values();
   public boolean targetSet = false;
   public int targetX;
   public int targetY;
   public int targetZ;
   private AudioSource audioSource = null;
   private static final int EventTeleport = 0;


   public void readFromNBT(NBTTagCompound nbttagcompound) {
      super.readFromNBT(nbttagcompound);
      this.targetSet = nbttagcompound.getBoolean("targetSet");
      this.targetX = nbttagcompound.getInteger("targetX");
      this.targetY = nbttagcompound.getInteger("targetY");
      this.targetZ = nbttagcompound.getInteger("targetZ");
   }

   public void writeToNBT(NBTTagCompound nbttagcompound) {
      super.writeToNBT(nbttagcompound);
      nbttagcompound.setBoolean("targetSet", this.targetSet);
      nbttagcompound.setInteger("targetX", this.targetX);
      nbttagcompound.setInteger("targetY", this.targetY);
      nbttagcompound.setInteger("targetZ", this.targetZ);
   }

   public boolean canUpdate() {
      return true;
   }

   public void updateEntity() {
      super.updateEntity();
      if(IC2.platform.isSimulating()) {
         if(super.worldObj.isBlockIndirectlyGettingPowered(super.xCoord, super.yCoord, super.zCoord) && this.targetSet) {
            boolean prevWorldChunkLoadOverride = super.worldObj.findingSpawnPoint;
            super.worldObj.findingSpawnPoint = true;
            Chunk chunk = super.worldObj.getChunkProvider().provideChunk(this.targetX >> 4, this.targetZ >> 4);
            super.worldObj.findingSpawnPoint = prevWorldChunkLoadOverride;
            if(chunk != null && chunk.getBlockID(this.targetX & 15, this.targetY, this.targetZ & 15) == Ic2Items.teleporter.itemID && chunk.getBlockMetadata(this.targetX & 15, this.targetY, this.targetZ & 15) == Ic2Items.teleporter.getItemDamage()) {
               this.setActive(true);
               List entitiesNearby = super.worldObj.getEntitiesWithinAABB(Entity.class, AxisAlignedBB.getBoundingBox((double)(super.xCoord - 1), (double)super.yCoord, (double)(super.zCoord - 1), (double)(super.xCoord + 2), (double)(super.yCoord + 3), (double)(super.zCoord + 2)));
               if(!entitiesNearby.isEmpty()) {
                  double minDistanceSquared = Double.MAX_VALUE;
                  Entity closestEntity = null;
                  Iterator i$ = entitiesNearby.iterator();

                  while(i$.hasNext()) {
                     Entity entity = (Entity)i$.next();
                     if(entity.ridingEntity == null) {
                        double distSquared = ((double)super.xCoord - entity.posX) * ((double)super.xCoord - entity.posX) + ((double)(super.yCoord + 1) - entity.posY) * ((double)(super.yCoord + 1) - entity.posY) + ((double)super.zCoord - entity.posZ) * ((double)super.zCoord - entity.posZ);
                        if(distSquared < minDistanceSquared) {
                           minDistanceSquared = distSquared;
                           closestEntity = entity;
                        }
                     }
                  }

                  this.teleport(closestEntity);
               }
            } else {
               this.targetSet = false;
               this.setActive(false);
            }
         } else {
            this.setActive(false);
         }
      }

      if(IC2.platform.isRendering() && this.getActive()) {
         this.spawnBlueParticles(2, super.xCoord, super.yCoord, super.zCoord);
      }

   }

   public void onUnloaded() {
      if(IC2.platform.isRendering() && this.audioSource != null) {
         IC2.audioManager.removeSources(this);
         this.audioSource = null;
      }

      super.onUnloaded();
   }

   public void teleport(Entity user) {
      double distance = Math.sqrt((double)((super.xCoord - this.targetX) * (super.xCoord - this.targetX) + (super.yCoord - this.targetY) * (super.yCoord - this.targetY) + (super.zCoord - this.targetZ) * (super.zCoord - this.targetZ)));
      int weight = this.getWeightOf(user);
      if(weight != 0) {
         int energyCost = (int)((double)weight * Math.pow(distance + 10.0D, 0.7D) * 5.0D);
         if(energyCost <= this.getAvailableEnergy()) {
            this.consumeEnergy(energyCost);
            if(user instanceof EntityPlayerMP) {
               ((EntityPlayerMP)user).setPositionAndUpdate((double)this.targetX + 0.5D, (double)this.targetY + 1.5D + user.getYOffset(), (double)this.targetZ + 0.5D);
            } else {
               user.setPositionAndRotation((double)this.targetX + 0.5D, (double)this.targetY + 1.5D + user.getYOffset(), (double)this.targetZ + 0.5D, user.rotationYaw, user.rotationPitch);
            }

            IC2.network.initiateTileEntityEvent(this, 0, true);
            if(user instanceof EntityPlayer && distance >= 1000.0D) {
               IC2.achievements.issueAchievement((EntityPlayer)user, "teleportFarAway");
            }

         }
      }
   }

   public void spawnBlueParticles(int n, int x, int y, int z) {
      for(int i = 0; i < n; ++i) {
         super.worldObj.spawnParticle("reddust", (double)((float)x + super.worldObj.rand.nextFloat()), (double)((float)(y + 1) + super.worldObj.rand.nextFloat()), (double)((float)z + super.worldObj.rand.nextFloat()), -1.0D, 0.0D, 1.0D);
         super.worldObj.spawnParticle("reddust", (double)((float)x + super.worldObj.rand.nextFloat()), (double)((float)(y + 2) + super.worldObj.rand.nextFloat()), (double)((float)z + super.worldObj.rand.nextFloat()), -1.0D, 0.0D, 1.0D);
      }

   }

   public void consumeEnergy(int energy) {
      LinkedList energySources = new LinkedList();
      Direction[] drain = directions;
      int it = drain.length;

      for(int energySource = 0; energySource < it; ++energySource) {
         Direction direction = drain[energySource];
         TileEntity target = direction.applyToTileEntity(this);
         if(target instanceof IEnergyStorage) {
            IEnergyStorage energySource1 = (IEnergyStorage)target;
            if(energySource1.isTeleporterCompatible(direction.getInverse()) && energySource1.getStored() > 0) {
               energySources.add(energySource1);
            }
         }
      }

      while(energy > 0) {
         int var9 = (energy + energySources.size() - 1) / energySources.size();
         Iterator var10 = energySources.iterator();

         while(var10.hasNext()) {
            IEnergyStorage var11 = (IEnergyStorage)var10.next();
            if(var9 > energy) {
               var9 = energy;
            }

            if(var11.getStored() <= var9) {
               energy -= var11.getStored();
               var11.setStored(0);
               var10.remove();
            } else {
               energy -= var9;
               var11.addEnergy(-var9);
            }
         }
      }

   }

   public int getAvailableEnergy() {
      int energy = 0;
      Direction[] arr$ = directions;
      int len$ = arr$.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         Direction direction = arr$[i$];
         TileEntity target = direction.applyToTileEntity(this);
         if(target instanceof IEnergyStorage) {
            IEnergyStorage storage = (IEnergyStorage)target;
            if(storage.isTeleporterCompatible(direction.getInverse())) {
               energy += storage.getStored();
            }
         }
      }

      return energy;
   }

   public int getWeightOf(Entity user) {
      int weight = 0;

      for(Entity ce = user; ce != null; ce = ce.riddenByEntity) {
         int i;
         if(ce instanceof EntityItem) {
            ItemStack living = ((EntityItem)ce).getEntityItem();
            weight += 100 * living.stackSize / living.getMaxStackSize();
         } else if(!(ce instanceof EntityAnimal) && !(ce instanceof EntityMinecart) && !(ce instanceof EntityBoat)) {
            if(ce instanceof EntityPlayer) {
               weight += 1000;
               if(IC2.enableTeleporterInventory) {
                  InventoryPlayer var7 = ((EntityPlayer)ce).inventory;

                  for(i = 0; i < var7.mainInventory.length; ++i) {
                     if(var7.mainInventory[i] != null) {
                        weight += 100 * var7.mainInventory[i].stackSize / var7.mainInventory[i].getMaxStackSize();
                     }
                  }
               }
            } else if(ce instanceof EntityGhast) {
               weight += 2500;
            } else if(ce instanceof EntityDragon) {
               weight += 10000;
            } else if(ce instanceof EntityCreature) {
               weight += 500;
            }
         } else {
            weight += 100;
         }

         if(IC2.enableTeleporterInventory && ce instanceof EntityLiving) {
            EntityLiving var8 = (EntityLiving)ce;

            for(i = ce instanceof EntityPlayer?1:0; i <= 4; ++i) {
               ItemStack item = var8.getCurrentItemOrArmor(i);
               if(item != null) {
                  weight += 100 * item.stackSize / item.getMaxStackSize();
               }
            }
         }
      }

      return weight;
   }

   public void setTarget(int x, int y, int z) {
      this.targetSet = true;
      this.targetX = x;
      this.targetY = y;
      this.targetZ = z;
      IC2.network.updateTileEntityField(this, "targetX");
      IC2.network.updateTileEntityField(this, "targetY");
      IC2.network.updateTileEntityField(this, "targetZ");
   }

   public List getNetworkedFields() {
      Vector ret = new Vector(3);
      ret.add("targetX");
      ret.add("targetY");
      ret.add("targetZ");
      ret.addAll(super.getNetworkedFields());
      return ret;
   }

   public void onNetworkUpdate(String field) {
      if(field.equals("active") && super.prevActive != this.getActive()) {
         if(this.audioSource == null) {
            this.audioSource = IC2.audioManager.createSource(this, PositionSpec.Center, "Machines/Teleporter/TeleChargedLoop.ogg", true, false, IC2.audioManager.defaultVolume);
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

   public void onNetworkEvent(int event) {
      switch(event) {
      case 0:
         IC2.audioManager.playOnce(this, PositionSpec.Center, "Machines/Teleporter/TeleUse.ogg", true, IC2.audioManager.defaultVolume);
         IC2.audioManager.playOnce(new AudioPosition(super.worldObj, (float)this.targetX + 0.5F, (float)this.targetY + 0.5F, (float)this.targetZ + 0.5F), PositionSpec.Center, "Machines/Teleporter/TeleUse.ogg", true, IC2.audioManager.defaultVolume);
         this.spawnBlueParticles(20, super.xCoord, super.yCoord, super.zCoord);
         this.spawnBlueParticles(20, this.targetX, this.targetY, this.targetZ);
         break;
      default:
         IC2.platform.displayError("An unknown event type was received over multiplayer.\nThis could happen due to corrupted data or a bug.\n\n(Technical information: event ID " + event + ", tile entity below)\n" + "T: " + this + " (" + super.xCoord + "," + super.yCoord + "," + super.zCoord + ")");
      }

   }

}
