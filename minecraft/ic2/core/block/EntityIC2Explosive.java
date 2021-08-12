package ic2.core.block;

import ic2.core.ExplosionIC2;
import ic2.core.IC2;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityIC2Explosive extends Entity {

   public DamageSource damageSource;
   public String igniter;
   public int fuse;
   public float explosivePower;
   public float dropRate;
   public float damageVsEntitys;
   public Block renderBlock;


   public EntityIC2Explosive(World world) {
      super(world);
      this.fuse = 4;
      this.explosivePower = 0.0F;
      this.dropRate = 0.0F;
      this.damageVsEntitys = 0.1F;
      this.renderBlock = Block.dirt;
      super.preventEntitySpawning = true;
      this.setSize(0.98F, 0.98F);
      super.yOffset = super.height / 2.0F;
   }

   public EntityIC2Explosive(World world, double d, double d1, double d2, int fuselength, float power, float rate, float damage, Block block, DamageSource damagesource) {
      this(world);
      this.setPosition(d, d1, d2);
      float f = (float)(Math.random() * Math.PI * 2.0D);
      super.motionX = (double)(-MathHelper.sin(f * 3.141593F / 180.0F) * 0.02F);
      super.motionY = 0.2D;
      super.motionZ = (double)(-MathHelper.cos(f * 3.141593F / 180.0F) * 0.02F);
      super.prevPosX = d;
      super.prevPosY = d1;
      super.prevPosZ = d2;
      this.fuse = fuselength;
      this.explosivePower = power;
      this.dropRate = rate;
      this.damageVsEntitys = damage;
      this.renderBlock = block;
      this.damageSource = damagesource;
   }

   public EntityIC2Explosive(World world, double d, double d1, double d2, int fuselength, float power, float rate, float damage, Block block) {
      this(world, d, d1, d2, fuselength, power, rate, damage, block, DamageSource.explosion);
   }

   protected void entityInit() {}

   protected boolean canTriggerWalking() {
      return true;
   }

   public boolean canBeCollidedWith() {
      return !super.isDead;
   }

   public void onUpdate() {
      super.prevPosX = super.posX;
      super.prevPosY = super.posY;
      super.prevPosZ = super.posZ;
      super.motionY -= 0.03999999910593033D;
      this.moveEntity(super.motionX, super.motionY, super.motionZ);
      super.motionX *= 0.9800000190734863D;
      super.motionY *= 0.9800000190734863D;
      super.motionZ *= 0.9800000190734863D;
      if(super.onGround) {
         super.motionX *= 0.699999988079071D;
         super.motionZ *= 0.699999988079071D;
         super.motionY *= -0.5D;
      }

      if(this.fuse-- <= 0) {
    	  
    	setDead();
         if(!IC2.platform.isSimulating()) {
            this.setDead();
         }
      } else {
         super.worldObj.spawnParticle("heart", super.posX, super.posY + 0.5D, super.posZ, 0.0D, 0.0D, 0.0D);
         super.worldObj.spawnParticle("lava", super.posX, super.posY + 0.5D, super.posZ, 0.0D, 0.0D, 0.0D);
         super.worldObj.spawnParticle("portal", super.posX, super.posY + 0.5D, super.posZ, 0.0D, 0.0D, 0.0D);
         super.worldObj.spawnParticle("slime", super.posX, super.posY + 0.5D, super.posZ, 0.0D, 0.0D, 0.0D);
         super.worldObj.spawnParticle("crit", super.posX, super.posY + 0.5D, super.posZ, 0.0D, 0.0D, 0.0D);
        
      }
      
      	
   }

   private void explode() {
      ExplosionIC2 explosion = new ExplosionIC2(super.worldObj, (Entity)null, super.posX, super.posY, super.posZ, this.explosivePower, this.dropRate, this.damageVsEntitys, this.damageSource, this.igniter);
      explosion.doExplosion();
   }

   protected void writeEntityToNBT(NBTTagCompound nbttagcompound) {
      nbttagcompound.setByte("Fuse", (byte)this.fuse);
   }

   protected void readEntityFromNBT(NBTTagCompound nbttagcompound) {
      this.fuse = nbttagcompound.getByte("Fuse");
   }

   public float getShadowSize() {
      return 0.0F;
   }

   public EntityIC2Explosive setIgniter(String igniter) {
      this.igniter = igniter;
      return this;
   }
}
