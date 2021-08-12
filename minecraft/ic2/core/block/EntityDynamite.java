package ic2.core.block;

import ic2.core.IC2;
import ic2.core.PointExplosion;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IProjectile;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityDynamite extends Entity implements IProjectile {

   public boolean sticky;
   public static final int netId = 142;
   public int stickX;
   public int stickY;
   public int stickZ;
   public int fuse;
   private boolean inGround;
   public EntityLiving owner;
   private int ticksInGround;
   private int ticksInAir;


   public EntityDynamite(World world, double x, double y, double z) {
      super(world);
      this.sticky = false;
      this.fuse = 100;
      this.inGround = false;
      this.ticksInAir = 0;
      this.setSize(0.5F, 0.5F);
      this.setPosition(x, y, z);
      super.yOffset = 0.0F;
   }

   public EntityDynamite(World world, double x, double y, double z, boolean sticky) {
      this(world, x, y, z);
      this.sticky = sticky;
   }

   public EntityDynamite(World world) {
      this(world, 0.0D, 0.0D, 0.0D);
   }

   public EntityDynamite(World world, EntityLiving entityliving) {
      super(world);
      this.sticky = false;
      this.fuse = 100;
      this.inGround = false;
      this.ticksInAir = 0;
      this.owner = entityliving;
      this.setSize(0.5F, 0.5F);
      this.setLocationAndAngles(entityliving.posX, entityliving.posY + (double)entityliving.getEyeHeight(), entityliving.posZ, entityliving.rotationYaw, entityliving.rotationPitch);
      super.posX -= (double)(MathHelper.cos(super.rotationYaw / 180.0F * 3.141593F) * 0.16F);
      super.posY -= 0.10000000149011612D;
      super.posZ -= (double)(MathHelper.sin(super.rotationYaw / 180.0F * 3.141593F) * 0.16F);
      this.setPosition(super.posX, super.posY, super.posZ);
      super.yOffset = 0.0F;
      super.motionX = (double)(-MathHelper.sin(super.rotationYaw / 180.0F * 3.141593F) * MathHelper.cos(super.rotationPitch / 180.0F * 3.141593F));
      super.motionZ = (double)(MathHelper.cos(super.rotationYaw / 180.0F * 3.141593F) * MathHelper.cos(super.rotationPitch / 180.0F * 3.141593F));
      super.motionY = (double)(-MathHelper.sin(super.rotationPitch / 180.0F * 3.141593F));
      this.setThrowableHeading(super.motionX, super.motionY, super.motionZ, 1.0F, 1.0F);
   }

   protected void entityInit() {}

   public void setThrowableHeading(double d, double d1, double d2, float f, float f1) {
      float f2 = MathHelper.sqrt_double(d * d + d1 * d1 + d2 * d2);
      d /= (double)f2;
      d1 /= (double)f2;
      d2 /= (double)f2;
      d += super.rand.nextGaussian() * 0.007499999832361937D * (double)f1;
      d1 += super.rand.nextGaussian() * 0.007499999832361937D * (double)f1;
      d2 += super.rand.nextGaussian() * 0.007499999832361937D * (double)f1;
      d *= (double)f;
      d1 *= (double)f;
      d2 *= (double)f;
      super.motionX = d;
      super.motionY = d1;
      super.motionZ = d2;
      float f3 = MathHelper.sqrt_double(d * d + d2 * d2);
      super.prevRotationYaw = super.rotationYaw = (float)(Math.atan2(d, d2) * 180.0D / 3.1415927410125732D);
      super.prevRotationPitch = super.rotationPitch = (float)(Math.atan2(d1, (double)f3) * 180.0D / 3.1415927410125732D);
      this.ticksInGround = 0;
   }

   public void setVelocity(double d, double d1, double d2) {
      super.motionX = d;
      super.motionY = d1;
      super.motionZ = d2;
      if(super.prevRotationPitch == 0.0F && super.prevRotationYaw == 0.0F) {
         float f = MathHelper.sqrt_double(d * d + d2 * d2);
         super.prevRotationYaw = super.rotationYaw = (float)(Math.atan2(d, d2) * 180.0D / 3.1415927410125732D);
         super.prevRotationPitch = super.rotationPitch = (float)(Math.atan2(d1, (double)f) * 180.0D / 3.1415927410125732D);
         super.prevRotationPitch = super.rotationPitch;
         super.prevRotationYaw = super.rotationYaw;
         this.setLocationAndAngles(super.posX, super.posY, super.posZ, super.rotationYaw, super.rotationPitch);
         this.ticksInGround = 0;
      }

   }

   public void onUpdate() {
      super.onUpdate();
      if(super.prevRotationPitch == 0.0F && super.prevRotationYaw == 0.0F) {
         float vec3d = MathHelper.sqrt_double(super.motionX * super.motionX + super.motionZ * super.motionZ);
         super.prevRotationYaw = super.rotationYaw = (float)(Math.atan2(super.motionX, super.motionZ) * 180.0D / 3.1415927410125732D);
         super.prevRotationPitch = super.rotationPitch = (float)(Math.atan2(super.motionY, (double)vec3d) * 180.0D / 3.1415927410125732D);
      }

      if(this.fuse-- <= 0) {
         if(IC2.platform.isSimulating()) {
            this.setDead();
            this.explode();
         } else {
            this.setDead();
         }
      } else if(this.fuse < 100 && this.fuse % 2 == 0) {
         super.worldObj.spawnParticle("smoke", super.posX, super.posY + 0.5D, super.posZ, 0.0D, 0.0D, 0.0D);
      }

      if(this.inGround) {
         ++this.ticksInGround;
         if(this.ticksInGround >= 200) {
            this.setDead();
         }

         if(this.sticky) {
            this.fuse -= 3;
            super.motionX = 0.0D;
            super.motionY = 0.0D;
            super.motionZ = 0.0D;
            if(super.worldObj.getBlockId(this.stickX, this.stickY, this.stickZ) != 0) {
               return;
            }
         }
      }

      ++this.ticksInAir;
      Vec3 var9 = Vec3.createVectorHelper(super.posX, super.posY, super.posZ);
      Vec3 vec3d1 = Vec3.createVectorHelper(super.posX + super.motionX, super.posY + super.motionY, super.posZ + super.motionZ);
      MovingObjectPosition movingobjectposition = super.worldObj.rayTraceBlocks_do_do(var9, vec3d1, false, true);
      var9 = Vec3.createVectorHelper(super.posX, super.posY, super.posZ);
      vec3d1 = Vec3.createVectorHelper(super.posX + super.motionX, super.posY + super.motionY, super.posZ + super.motionZ);
      float f2;
      float f3;
      float f5;
      if(movingobjectposition != null) {
         vec3d1 = Vec3.createVectorHelper(movingobjectposition.hitVec.xCoord, movingobjectposition.hitVec.yCoord, movingobjectposition.hitVec.zCoord);
         f2 = (float)(movingobjectposition.hitVec.xCoord - super.posX);
         f3 = (float)(movingobjectposition.hitVec.yCoord - super.posY);
         f5 = (float)(movingobjectposition.hitVec.zCoord - super.posZ);
         float i1 = MathHelper.sqrt_double((double)(f2 * f2 + f3 * f3 + f5 * f5));
         this.stickX = movingobjectposition.blockX;
         this.stickY = movingobjectposition.blockY;
         this.stickZ = movingobjectposition.blockZ;
         super.posX -= (double)f2 / (double)i1 * 0.05000000074505806D;
         super.posY -= (double)f3 / (double)i1 * 0.05000000074505806D;
         super.posZ -= (double)f5 / (double)i1 * 0.05000000074505806D;
         super.posX += (double)f2;
         super.posY += (double)f3;
         super.posZ += (double)f5;
         super.motionX *= (double)(0.75F - super.rand.nextFloat());
         super.motionY *= -0.30000001192092896D;
         super.motionZ *= (double)(0.75F - super.rand.nextFloat());
         this.inGround = true;
      } else {
         super.posX += super.motionX;
         super.posY += super.motionY;
         super.posZ += super.motionZ;
         this.inGround = false;
      }

      f2 = MathHelper.sqrt_double(super.motionX * super.motionX + super.motionZ * super.motionZ);
      super.rotationYaw = (float)(Math.atan2(super.motionX, super.motionZ) * 180.0D / 3.1415927410125732D);

      for(super.rotationPitch = (float)(Math.atan2(super.motionY, (double)f2) * 180.0D / 3.1415927410125732D); super.rotationPitch - super.prevRotationPitch < -180.0F; super.prevRotationPitch -= 360.0F) {
         ;
      }

      while(super.rotationPitch - super.prevRotationPitch >= 180.0F) {
         super.prevRotationPitch += 360.0F;
      }

      while(super.rotationYaw - super.prevRotationYaw < -180.0F) {
         super.prevRotationYaw -= 360.0F;
      }

      while(super.rotationYaw - super.prevRotationYaw >= 180.0F) {
         super.prevRotationYaw += 360.0F;
      }

      super.rotationPitch = super.prevRotationPitch + (super.rotationPitch - super.prevRotationPitch) * 0.2F;
      super.rotationYaw = super.prevRotationYaw + (super.rotationYaw - super.prevRotationYaw) * 0.2F;
      f3 = 0.98F;
      f5 = 0.04F;
      if(this.isInWater()) {
         this.fuse += 2000;

         for(int var10 = 0; var10 < 4; ++var10) {
            float f6 = 0.25F;
            super.worldObj.spawnParticle("bubble", super.posX - super.motionX * (double)f6, super.posY - super.motionY * (double)f6, super.posZ - super.motionZ * (double)f6, super.motionX, super.motionY, super.motionZ);
         }

         f3 = 0.75F;
      }

      super.motionX *= (double)f3;
      super.motionY *= (double)f3;
      super.motionZ *= (double)f3;
      super.motionY -= (double)f5;
      this.setPosition(super.posX, super.posY, super.posZ);
   }

   public void writeEntityToNBT(NBTTagCompound nbttagcompound) {
      nbttagcompound.setByte("inGround", (byte)(this.inGround?1:0));
   }

   public void readEntityFromNBT(NBTTagCompound nbttagcompound) {
      this.inGround = nbttagcompound.getByte("inGround") == 1;
   }

   public float getShadowSize() {
      return 0.0F;
   }

   public void explode() {
      PointExplosion explosion = new PointExplosion(super.worldObj, (Entity)null, (int)super.posX, (int)super.posY, (int)super.posZ, 1.0F, 1.0F, 0.8F);
      explosion.doExplosionA(1, 1, 1, 1, 1, 1);
      explosion.doExplosionB(true);
   }
}
