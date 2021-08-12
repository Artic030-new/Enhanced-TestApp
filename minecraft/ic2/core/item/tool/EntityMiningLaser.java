package ic2.core.item.tool;

import ic2.core.ExplosionIC2;
import ic2.core.IC2;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityDragonPart;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.IThrowableEntity;

public class EntityMiningLaser extends Entity implements IThrowableEntity {

   public float range;
   public float power;
   public int blockBreaks;
   public boolean explosive;
   public static Block[] unmineableBlocks = new Block[]{Block.brick, Block.obsidian, Block.lavaMoving, Block.lavaStill, Block.waterMoving, Block.waterStill, Block.bedrock, Block.glass};
  // public static Entity[] invulnerableEntites = new Entity[] {boat, frame, paint, cart};
   public static final int netId = 141;
   public static final double laserSpeed = 1.0D;
   public EntityLiving owner;
   public boolean headingSet;
   public boolean smelt;
   private int ticksInAir;


   public EntityMiningLaser(World world, double x, double y, double z) {
      super(world);
      this.range = 0.0F;
      this.power = 0.0F;
      this.blockBreaks = 0;
      this.explosive = false;
      this.headingSet = false;
      this.smelt = false;
      this.ticksInAir = 0;
      this.setSize(0.8F, 0.8F);
      super.yOffset = 0.0F;
      this.setPosition(x, y, z);
   }

   public EntityMiningLaser(World world) {
      this(world, 0.0D, 0.0D, 0.0D);
   }

   public EntityMiningLaser(World world, EntityLiving entityliving, float range, float power, int blockBreaks, boolean explosive) {
      this(world, entityliving, range, power, blockBreaks, explosive, (double)entityliving.rotationYaw, (double)entityliving.rotationPitch);
   }

   public EntityMiningLaser(World world, EntityLiving entityliving, float range, float power, int blockBreaks, boolean explosive, boolean smelt) {
      this(world, entityliving, range, power, blockBreaks, explosive, (double)entityliving.rotationYaw, (double)entityliving.rotationPitch);
      this.smelt = smelt;
   }

   public EntityMiningLaser(World world, EntityLiving entityliving, float range, float power, int blockBreaks, boolean explosive, double yawDeg, double pitchDeg) {
      this(world, entityliving, range, power, blockBreaks, explosive, yawDeg, pitchDeg, entityliving.posY + (double)entityliving.getEyeHeight() - 0.1D);
   }

   public EntityMiningLaser(World world, EntityLiving entityliving, float range, float power, int blockBreaks, boolean explosive, double yawDeg, double pitchDeg, double y) {
      super(world);
      this.range = 0.0F;
      this.power = 0.0F;
      this.blockBreaks = 0;
      this.explosive = false;
      this.headingSet = false;
      this.smelt = false;
      this.ticksInAir = 0;
      this.owner = entityliving;
      this.setSize(0.8F, 0.8F);
      super.yOffset = 0.0F;
      double yaw = Math.toRadians(yawDeg);
      double pitch = Math.toRadians(pitchDeg);
      double x = entityliving.posX - Math.cos(yaw) * 0.16D;
      double z = entityliving.posZ - Math.sin(yaw) * 0.16D;
      double startMotionX = -Math.sin(yaw) * Math.cos(pitch);
      double startMotionY = -Math.sin(pitch);
      double startMotionZ = Math.cos(yaw) * Math.cos(pitch);
      this.setPosition(x, y, z);
      this.setLaserHeading(startMotionX, startMotionY, startMotionZ, 1.0D);
      this.range = range;
      this.power = power;
      this.blockBreaks = blockBreaks;
      this.explosive = explosive;
   }

   protected void entityInit() {}

   public void setLaserHeading(double motionX, double motionY, double motionZ, double speed) {
      double currentSpeed = (double)MathHelper.sqrt_double(motionX * motionX + motionY * motionY + motionZ * motionZ);
      super.motionX = motionX / currentSpeed * speed;
      super.motionY = motionY / currentSpeed * speed;
      super.motionZ = motionZ / currentSpeed * speed;
      super.prevRotationYaw = super.rotationYaw = (float)Math.toDegrees(Math.atan2(motionX, motionZ));
      super.prevRotationPitch = super.rotationPitch = (float)Math.toDegrees(Math.atan2(motionY, (double)MathHelper.sqrt_double(motionX * motionX + motionZ * motionZ)));
      this.headingSet = true;
   }

   public void setVelocity(double motionX, double motionY, double motionZ) {
      this.setLaserHeading(motionX, motionY, motionZ, 1.0D);
   }

   public void onUpdate() {
      super.onUpdate();
      if(IC2.platform.isSimulating() && (this.range < 1.0F || this.power <= 0.0F || this.blockBreaks <= 0)) {
         if(this.explosive) {
            this.explode();
         }

         this.setDead();
      } else {
         ++this.ticksInAir;
         Vec3 oldPosition = Vec3.createVectorHelper(super.posX, super.posY, super.posZ);
         Vec3 newPosition = Vec3.createVectorHelper(super.posX + super.motionX, super.posY + super.motionY, super.posZ + super.motionZ);
         MovingObjectPosition movingobjectposition = super.worldObj.rayTraceBlocks_do_do(oldPosition, newPosition, false, true);
         oldPosition = Vec3.createVectorHelper(super.posX, super.posY, super.posZ);
         if(movingobjectposition != null) {
            newPosition = Vec3.createVectorHelper(movingobjectposition.hitVec.xCoord, movingobjectposition.hitVec.yCoord, movingobjectposition.hitVec.zCoord);
         } else {
            newPosition = Vec3.createVectorHelper(super.posX + super.motionX, super.posY + super.motionY, super.posZ + super.motionZ);
         }

         Entity entity = null;
         List list = super.worldObj.getEntitiesWithinAABBExcludingEntity(this, super.boundingBox.addCoord(super.motionX, super.motionY, super.motionZ).expand(1.0D, 1.0D, 1.0D));
         double d = 0.0D;

         int xTile;
         for(xTile = 0; xTile < list.size(); ++xTile) {
            Entity var29 = (Entity)list.get(xTile);
            if(var29.canBeCollidedWith() && (var29 != this.owner || this.ticksInAir >= 5)) {
               float var31 = 0.3F;
               AxisAlignedBB var30 = var29.boundingBox.expand((double)var31, (double)var31, (double)var31);
               MovingObjectPosition var32 = var30.calculateIntercept(oldPosition, newPosition);
               if(var32 != null) {
                  double var33 = oldPosition.distanceTo(var32.hitVec);
                  if(var33 < d || d == 0.0D) {
                     entity = var29;
                     d = var33;
                  }
               }
            }
         }

         if(entity != null) {
            movingobjectposition = new MovingObjectPosition(entity);
         }
         
         if(movingobjectposition != null && IC2.platform.isSimulating() ) {
            if(this.explosive) {
               this.setDead();
               return;
            }
            
            if(movingobjectposition.entityHit != null) {
               xTile = (int)this.power;
               
            
              
               if(xTile > 0 && !(movingobjectposition.entityHit instanceof EntityBoat ) && !(movingobjectposition.entityHit instanceof EntityMinecart ) && !(movingobjectposition.entityHit instanceof EntityItemFrame ) && !(movingobjectposition.entityHit instanceof EntityPainting )) {
            	   
                  entity.setFire(xTile * (this.smelt?2:1));
                  if(movingobjectposition.entityHit.attackEntityFrom((new EntityDamageSourceIndirect("arrow", this, this.owner)).setProjectile(), xTile) && this.owner instanceof EntityPlayer && (movingobjectposition.entityHit instanceof EntityDragon && ((EntityDragon)movingobjectposition.entityHit).getHealth() <= 0 || movingobjectposition.entityHit instanceof EntityDragonPart && ((EntityDragonPart)movingobjectposition.entityHit).entityDragonObj instanceof EntityDragon && ((EntityLiving)((EntityDragonPart)movingobjectposition.entityHit).entityDragonObj).getHealth() <= 0)) {
                     IC2.achievements.issueAchievement((EntityPlayer)this.owner, "killDragonMiningLaser");
                  }
               }
               this.setDead();
               
            } else {
               xTile = movingobjectposition.blockX;
               int var291 = movingobjectposition.blockY;
               int var311 = movingobjectposition.blockZ;
               int var301 = super.worldObj.getBlockId(xTile, var291, var311);
               int var321 = super.worldObj.getBlockMetadata(xTile, var291, var311);
               boolean var331 = false;
               boolean removeBlock = true;
               boolean dropBlock = true;
               if(!this.canMine(var301)) {
                  this.setDead();
               } else if(IC2.platform.isSimulating()) {
                  float resis = 0.0F;
                  resis = Block.blocksList[var301].getExplosionResistance(this, super.worldObj, xTile, var291, var311, super.posX, super.posY, super.posZ) + 0.3F;
                  this.power -= resis / 10.0F;
                  if(this.power >= 0.0F) {
                     if(Block.blocksList[var301].blockMaterial == Material.tnt) {
                        Block.blocksList[var301].onBlockDestroyedByPlayer(super.worldObj, xTile, var291, var311, 1);
                     } else if(this.smelt) {
                        if(Block.blocksList[var301].blockMaterial == Material.wood) {
                           removeBlock = false;
                           dropBlock = false;
                        } else {
                           Iterator i$ = Block.blocksList[var301].getBlockDropped(super.worldObj, xTile, var291, var311, var321, 0).iterator();

                           while(i$.hasNext()) {
                              ItemStack isa = (ItemStack)i$.next();
                              ItemStack isb = FurnaceRecipes.smelting().getSmeltingResult(isa);
                              if(isb != null) {
                                 ItemStack is = isb.copy();
                                 if(!var331 && is.itemID != var301 && is.itemID < Block.blocksList.length && Block.blocksList[is.itemID] != null && Block.blocksList[is.itemID].blockID != 0) {
                                    var331 = true;
                                    super.worldObj.setBlockAndMetadataWithNotify(xTile, var291, var311, is.itemID, is.getItemDamage());
                                 } else {
                                    dropBlock = false;
                                    float var6 = 0.7F;
                                    double var7 = (double)(super.worldObj.rand.nextFloat() * var6) + (double)(1.0F - var6) * 0.5D;
                                    double var9 = (double)(super.worldObj.rand.nextFloat() * var6) + (double)(1.0F - var6) * 0.5D;
                                    double var11 = (double)(super.worldObj.rand.nextFloat() * var6) + (double)(1.0F - var6) * 0.5D;
                                    EntityItem var13 = new EntityItem(super.worldObj, (double)xTile + var7, (double)var291 + var9, (double)var311 + var11, is);
                                    var13.delayBeforeCanPickup = 10;
                                    super.worldObj.spawnEntityInWorld(var13);
                                 }

                                 this.power = 0.0F;
                              }
                           }
                        }
                     }

                     if(removeBlock) {
                        if(dropBlock) {
                           Block.blocksList[var301].dropBlockAsItemWithChance(super.worldObj, xTile, var291, var311, super.worldObj.getBlockMetadata(xTile, var291, var311), 0.9F, 0);
                        }

                        super.worldObj.setBlockWithNotify(xTile, var291, var311, 0);
                        if(super.worldObj.rand.nextInt(10) == 0 && Block.blocksList[var301].blockMaterial.getCanBurn()) {
                           super.worldObj.setBlockWithNotify(xTile, var291, var311, Block.fire.blockID);
                        }
                     }
                  }
               }
            }
         } else {
            this.power -= 0.5F;
         }

         this.setPosition(super.posX + super.motionX, super.posY + super.motionY, super.posZ + super.motionZ);
         this.range = (float)((double)this.range - Math.sqrt(super.motionX * super.motionX + super.motionY * super.motionY + super.motionZ * super.motionZ));
         if(this.isInWater()) {
            this.setDead();
         }
      }

   }

   public void writeEntityToNBT(NBTTagCompound nbttagcompound) {}

   public void readEntityFromNBT(NBTTagCompound nbttagcompound) {}

   public float getShadowSize() {
      return 0.0F;
   }

   public void explode() {
      if(IC2.platform.isSimulating()) {
         new ExplosionIC2(super.worldObj, (Entity)null, super.posX, super.posY, super.posZ, 5.0F, 0.85F, 0.55F);
      }

   }

   public boolean canMine(int blockId) {
      for(int i = 0; i < unmineableBlocks.length; ++i) {
         if(blockId == unmineableBlocks[i].blockID) {
            return false;
         }
      }

      return false;
   }

   public Entity getThrower() {
      return this.owner;
   }

   public void setThrower(Entity entity) {
      if(entity instanceof EntityLiving) {
         this.owner = (EntityLiving)entity;
      }

   }

}
