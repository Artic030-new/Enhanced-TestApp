package ic2.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;

public class PointExplosion {

   private Random ExplosionRNG = new Random();
   private World worldObj;
   public int explosionX;
   public int explosionY;
   public int explosionZ;
   public Entity exploder;
   public float explosionSize;
   public float explosionDropRate;
   public float explosionDamage;
   public Set destroyedBlockPositions = new HashSet();


   public PointExplosion(World world, Entity entity, int x, int y, int z, float power, float drop, float entitydamage) {
      this.worldObj = world;
      this.exploder = entity;
      this.explosionSize = power;
      this.explosionDropRate = drop;
      this.explosionDamage = entitydamage;
      this.explosionX = x;
      this.explosionY = y;
      this.explosionZ = z;
      if(this.explosionX < 0) {
         --this.explosionX;
      }

      if(this.explosionZ < 0) {
         --this.explosionZ;
      }

   }

   public void doExplosionA(int lowX, int lowY, int lowZ, int highX, int highY, int highZ) {
      int k;
      int i1;
      int k1;
      int l1;
      for(k = this.explosionX - lowX; k <= this.explosionX + highX; ++k) {
         for(i1 = this.explosionY - lowY; i1 <= this.explosionY + highY; ++i1) {
            for(k1 = this.explosionZ - lowZ; k1 <= this.explosionZ + highZ; ++k1) {
               l1 = this.worldObj.getBlockId(k, i1, k1);
               float i2 = 0.0F;
               if(l1 > 0) {
                  i2 = Block.blocksList[l1].getExplosionResistance(this.exploder, this.worldObj, k, i1, k1, (double)this.explosionX, (double)this.explosionY, (double)this.explosionZ);
               }

               if(this.explosionSize >= i2 / 10.0F) {
                  this.destroyedBlockPositions.add(new ChunkPosition(k, i1, k1));
               }
            }
         }
      }

      this.explosionSize *= 2.0F;
      k = MathHelper.floor_double((double)this.explosionX - (double)this.explosionSize - 1.0D);
      i1 = MathHelper.floor_double((double)this.explosionX + (double)this.explosionSize + 1.0D);
      k1 = MathHelper.floor_double((double)this.explosionY - (double)this.explosionSize - 1.0D);
      l1 = MathHelper.floor_double((double)this.explosionY + (double)this.explosionSize + 1.0D);
      int var33 = MathHelper.floor_double((double)this.explosionZ - (double)this.explosionSize - 1.0D);
      int j2 = MathHelper.floor_double((double)this.explosionZ + (double)this.explosionSize + 1.0D);
      List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this.exploder, AxisAlignedBB.getBoundingBox((double)k, (double)k1, (double)var33, (double)i1, (double)l1, (double)j2));
      Vec3 vec3d = Vec3.createVectorHelper((double)this.explosionX, (double)this.explosionY, (double)this.explosionZ);

      for(int k2 = 0; k2 < list.size(); ++k2) {
         Entity entity = (Entity)list.get(k2);
         double d4 = entity.getDistance((double)this.explosionX, (double)this.explosionY, (double)this.explosionZ) / (double)this.explosionSize;
         if(d4 <= 1.0D) {
            double d6 = entity.posX - (double)this.explosionX;
            double d8 = entity.posY - (double)this.explosionY;
            double d10 = entity.posZ - (double)this.explosionZ;
            double d11 = (double)MathHelper.sqrt_double(d6 * d6 + d8 * d8 + d10 * d10);
            d6 /= d11;
            d8 /= d11;
            d10 /= d11;
            double d12 = (double)this.worldObj.getBlockDensity(vec3d, entity.boundingBox);
            double d13 = (1.0D - d4) * d12;
            entity.attackEntityFrom(DamageSource.explosion, (int)(((d13 * d13 + d13) / 2.0D * 8.0D * (double)this.explosionSize + 1.0D) * (double)this.explosionDamage));
            entity.motionX += d6 * d13;
            entity.motionY += d8 * d13;
            entity.motionZ += d10 * d13;
         }
      }

   }

   public void doExplosionB(boolean flag) {
      this.worldObj.playSoundEffect((double)this.explosionX, (double)this.explosionY, (double)this.explosionZ, "random.explode", 4.0F, (1.0F + (this.worldObj.rand.nextFloat() - this.worldObj.rand.nextFloat()) * 0.2F) * 0.7F);
      ArrayList arraylist = new ArrayList();
      arraylist.addAll(this.destroyedBlockPositions);

      for(int i = arraylist.size() - 1; i >= 0; --i) {
         ChunkPosition chunkposition = (ChunkPosition)arraylist.get(i);
         int j = chunkposition.x;
         int k = chunkposition.y;
         int l = chunkposition.z;
         int i1 = this.worldObj.getBlockId(j, k, l);
         if(flag) {
            double d = (double)((float)j + this.worldObj.rand.nextFloat());
            double d1 = (double)((float)k + this.worldObj.rand.nextFloat());
            double d2 = (double)((float)l + this.worldObj.rand.nextFloat());
            double d3 = d - (double)this.explosionX;
            double d4 = d1 - (double)this.explosionY;
            double d5 = d2 - (double)this.explosionZ;
            double d6 = (double)MathHelper.sqrt_double(d3 * d3 + d4 * d4 + d5 * d5);
            d3 /= d6;
            d4 /= d6;
            d5 /= d6;
            double d7 = 0.5D / (d6 / (double)this.explosionSize + 0.1D);
            d7 *= (double)(this.worldObj.rand.nextFloat() * this.worldObj.rand.nextFloat() + 0.3F);
            d3 *= d7;
            d4 *= d7;
            d5 *= d7;
            this.worldObj.spawnParticle("explode", (d + (double)this.explosionX * 1.0D) / 2.0D, (d1 + (double)this.explosionY * 1.0D) / 2.0D, (d2 + (double)this.explosionZ * 1.0D) / 2.0D, d3, d4, d5);
            this.worldObj.spawnParticle("smoke", d, d1, d2, d3, d4, d5);
         }

         if(i1 > 0) {
            Block.blocksList[i1].dropBlockAsItemWithChance(this.worldObj, j, k, l, this.worldObj.getBlockMetadata(j, k, l), this.explosionDropRate, 0);
            this.worldObj.setBlockWithNotify(j, k, l, 0);
            Block.blocksList[i1].onBlockDestroyedByExplosion(this.worldObj, j, k, l);
         }
      }

   }
}
