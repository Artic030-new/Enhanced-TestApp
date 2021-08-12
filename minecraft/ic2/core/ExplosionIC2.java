package ic2.core;

import ic2.api.ExplosionWhitelist;
import ic2.core.IC2;
import ic2.core.IC2DamageSource;
import ic2.core.IC2Potion;
import ic2.core.item.armor.ItemArmorHazmat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class ExplosionIC2 {

   private Random ExplosionRNG;
   private World worldObj;
   private int mapHeight;
   public double explosionX;
   public double explosionY;
   public double explosionZ;
   public Entity exploder;
   public float power;
   public float explosionDropRate;
   public float explosionDamage;
   public DamageSource damageSource;
   public String igniter;
   public List entitiesInRange;
   public Map vecMap;
   public Map destroyedBlockPositions;
   private int lastChunkX;
   private int lastChunkZ;
   private Chunk lastChunk;
   private final double dropPowerLimit;
   private final double damageAtDropPowerLimit;
   private final double accelerationAtDropPowerLimit;
   private final int secondaryRayCount;


   public ExplosionIC2(World world, Entity entity, double d, double d1, double d2, float power, float drop, float entitydamage, DamageSource damagesource) {
      this.ExplosionRNG = new Random();
      this.entitiesInRange = new ArrayList();
      this.vecMap = new HashMap();
      this.destroyedBlockPositions = new HashMap();
      this.lastChunkX = Integer.MAX_VALUE;
      this.lastChunkZ = Integer.MAX_VALUE;
      this.dropPowerLimit = 8.0D;
      this.damageAtDropPowerLimit = 32.0D;
      this.accelerationAtDropPowerLimit = 0.7D;
      this.secondaryRayCount = 0;
      this.worldObj = world;
      this.mapHeight = IC2.getWorldHeight(world);
      this.exploder = entity;
      this.power = 0;
      this.explosionDropRate = drop;
      this.explosionDamage = entitydamage;
      this.explosionX = d;
      this.explosionY = d1;
      this.explosionZ = d2;
      this.damageSource = damagesource;
   }

   public ExplosionIC2(World world, Entity entity, double d, double d1, double d2, float power, float drop, float entitydamage) {
      this(world, entity, d, d1, d2, power, drop, entitydamage, DamageSource.explosion);
   }

   public ExplosionIC2(World world, Entity entity, double d, double d1, double d2, float power, float drop, float entitydamage, DamageSource damagesource, String igniter) {
      this(world, entity, d, d1, d2, power, drop, entitydamage, damagesource);
      this.igniter = igniter;
   }

   public void doExplosion() {
	   
      if(this.power > 0.0F) {
      
         double maxDistance = (double)this.power / 0.4D;
         List entities = this.worldObj.getEntitiesWithinAABBExcludingEntity((Entity)null, AxisAlignedBB.getBoundingBox(this.explosionX - maxDistance, this.explosionY - maxDistance, this.explosionZ - maxDistance, this.explosionX + maxDistance, this.explosionY + maxDistance, this.explosionZ + maxDistance));
         boolean entitiesAreInRange = !entities.isEmpty();
         if(entitiesAreInRange) {
            Iterator steps = entities.iterator();

            while(steps.hasNext()) {
               Entity blocksToDrop = (Entity)steps.next();
               if(blocksToDrop instanceof EntityLiving || blocksToDrop instanceof EntityItem) {
            	   
                  this.entitiesInRange.add(new SimpleEntry(Integer.valueOf((int)((blocksToDrop.posX - this.explosionX) * (blocksToDrop.posX - this.explosionX) + (blocksToDrop.posY - this.explosionY) * (blocksToDrop.posY - this.explosionY) + (blocksToDrop.posZ - this.explosionZ) * (blocksToDrop.posZ - this.explosionZ))), blocksToDrop));
               }
            }

            Collections.sort(this.entitiesInRange, new Comparator() {
               public int compare(Entry a, Entry b) {
                  return ((Integer)a.getKey()).intValue() - ((Integer)b.getKey()).intValue();
               }

			@Override
			public int compare(Object arg0, Object arg1) {
				// TODO Auto-generated method stub
				return 0;
			}
            });
         }

         int var36 = (int)Math.ceil(3.141592653589793D / Math.atan(1.0D / maxDistance));

         double entry;
         for(int var37 = 0; var37 < 2 * var36; ++var37) {
            for(int i$ = 0; i$ < var36; ++i$) {
               entry = 6.283185307179586D / (double)var36 * (double)var37;
               double i$1 = 3.141592653589793D / (double)var36 * (double)i$;
               this.shootRay(this.explosionX, this.explosionY, this.explosionZ, entry, i$1, (double)this.power, entitiesAreInRange && var37 % 8 == 0 && i$ % 8 == 0);
            }
         }

         int entry2;
         int var45;
         if(this.damageSource == IC2DamageSource.nuke) {
            Iterator var39 = this.worldObj.getEntitiesWithinAABB(EntityLiving.class, AxisAlignedBB.getBoundingBox(this.explosionX - 100.0D, this.explosionY - 100.0D, this.explosionZ - 100.0D, this.explosionX + 100.0D, this.explosionY + 100.0D, this.explosionZ + 100.0D)).iterator();

            while(var39.hasNext()) {
               EntityLiving var41 = (EntityLiving)var39.next();
               if(!ItemArmorHazmat.hasCompleteHazmat(var41)) {
                  entry = var41.getDistance(this.explosionX, this.explosionY, this.explosionZ);
                  var45 = (int)(120.0D * (100.0D - entry));
                  entry2 = (int)(80.0D * (30.0D - entry));
                  if(var45 >= 0) {
                     var41.addPotionEffect(new PotionEffect(Potion.hunger.id, var45, 0));
                  }

                  if(entry2 >= 0) {
                     var41.addPotionEffect(new PotionEffect(IC2Potion.radiation.id, entry2, 0));
                  }
               }
            }
         }

         IC2.network.initiateExplosionEffect(this.worldObj, this.explosionX, this.explosionY, this.explosionZ);
         HashMap var38 = new HashMap();
         Iterator var40 = this.destroyedBlockPositions.entrySet().iterator();

         Entry var42;
         while(var40.hasNext()) {
            var42 = (Entry)var40.next();
            int xZposition = ((ChunkPosition)var42.getKey()).x;
            var45 = ((ChunkPosition)var42.getKey()).y;
            entry2 = ((ChunkPosition)var42.getKey()).z;
            int itemWithMeta = this.getBlockId(xZposition, var45, entry2);
            if(itemWithMeta != 0) {
               if(((Boolean)var42.getValue()).booleanValue()) {
                  double count = (double)((float)xZposition + this.worldObj.rand.nextFloat());
                  double entityitem = (double)((float)var45 + this.worldObj.rand.nextFloat());
                  double effectZ = (double)((float)entry2 + this.worldObj.rand.nextFloat());
                  double d3 = count - this.explosionX;
                  double d4 = entityitem - this.explosionY;
                  double d5 = effectZ - this.explosionZ;
                  double effectDistance = (double)MathHelper.sqrt_double(d3 * d3 + d4 * d4 + d5 * d5);
                  d3 /= effectDistance;
                  d4 /= effectDistance;
                  d5 /= effectDistance;
                  double d7 = 0.5D / (effectDistance / (double)this.power + 0.1D);
                  d7 *= (double)(this.worldObj.rand.nextFloat() * this.worldObj.rand.nextFloat() + 0.3F);
                  d3 *= d7;
                  d4 *= d7;
                  d5 *= d7;
                  this.worldObj.spawnParticle("explode", (count + this.explosionX) / 2.0D, (entityitem + this.explosionY) / 2.0D, (effectZ + this.explosionZ) / 2.0D, d3, d4, d5);
                  this.worldObj.spawnParticle("smoke", count, entityitem, effectZ, d3, d4, d5);
                  Block block = Block.blocksList[itemWithMeta];
                  int meta = this.worldObj.getBlockMetadata(xZposition, var45, entry2);
                  Iterator i$2 = block.getBlockDropped(this.worldObj, xZposition, var45, entry2, meta, 0).iterator();

                  while(i$2.hasNext()) {
                     ItemStack itemStack = (ItemStack)i$2.next();
                     if(this.worldObj.rand.nextFloat() <= this.explosionDropRate) {
                        ExplosionIC2.XZposition xZposition1 = new ExplosionIC2.XZposition(xZposition / 2, entry2 / 2);
                        if(!var38.containsKey(xZposition1)) {
                           var38.put(xZposition1, new HashMap());
                        }

                        Map map = (Map)var38.get(xZposition1);
                        ExplosionIC2.ItemWithMeta itemWithMeta1 = new ExplosionIC2.ItemWithMeta(itemStack.itemID, itemStack.getItemDamage());
                        if(!map.containsKey(itemWithMeta1)) {
                           map.put(itemWithMeta1, new ExplosionIC2.DropData(itemStack.stackSize, var45));
                        } else {
                           map.put(itemWithMeta1, ((ExplosionIC2.DropData)map.get(itemWithMeta1)).add(itemStack.stackSize, var45));
                        }
                     }
                  }
               }

               this.worldObj.setBlockWithNotify(xZposition, var45, entry2, 0);
               Block.blocksList[itemWithMeta].onBlockDestroyedByExplosion(this.worldObj, xZposition, var45, entry2);
            }
         }

         var40 = var38.entrySet().iterator();

         while(var40.hasNext()) {
            var42 = (Entry)var40.next();
            ExplosionIC2.XZposition var43 = (ExplosionIC2.XZposition)var42.getKey();
            Iterator var44 = ((Map)var42.getValue()).entrySet().iterator();

            while(var44.hasNext()) {
               Entry var47 = (Entry)var44.next();
               ExplosionIC2.ItemWithMeta var46 = (ExplosionIC2.ItemWithMeta)var47.getKey();

               int stackSize;
               for(int var48 = ((ExplosionIC2.DropData)var47.getValue()).n; var48 > 0; var48 -= stackSize) {
                  stackSize = Math.min(var48, 64);
                  EntityItem var49 = new EntityItem(this.worldObj, (double)((float)var43.x + this.worldObj.rand.nextFloat()) * 2.0D, (double)((ExplosionIC2.DropData)var47.getValue()).maxY + 0.5D, (double)((float)var43.z + this.worldObj.rand.nextFloat()) * 2.0D, new ItemStack(var46.itemId, stackSize, var46.metaData));
                  var49.delayBeforeCanPickup = 10;
                  this.worldObj.spawnEntityInWorld(var49);
               }
            }
         }

      }
   }

   private void shootRay(double x, double y, double z, double phi, double theta, double power, boolean killEntities) {
      double deltaX = Math.sin(theta) * Math.cos(phi);
      double deltaY = Math.cos(theta);
      double deltaZ = Math.sin(theta) * Math.sin(phi);
      int step = 0;

      while(true) {
         int blockId = this.getBlockId((int)x, (int)y, (int)z);
         double absorption = 0.5D;
         if(blockId > 0) {
            absorption += ((double)Block.blocksList[blockId].getExplosionResistance(this.exploder, this.worldObj, (int)x, (int)y, (int)z, this.explosionX, this.explosionY, this.explosionZ) + 4.0D) * 0.3D;
         }

         if(absorption > 1000.0D && !ExplosionWhitelist.isBlockWhitelisted(Block.blocksList[blockId])) {
            absorption = 0.5D;
         } else {
            if(absorption > power) {
               break;
            }

            if(blockId > 0) {
               ChunkPosition i = new ChunkPosition((int)x, (int)y, (int)z);
               if(power > 8.0D) {
                  this.destroyedBlockPositions.put(i, Boolean.valueOf(false));
               } else if(!this.destroyedBlockPositions.containsKey(i)) {
                  this.destroyedBlockPositions.put(i, Boolean.valueOf(true));
               }
            }
         }

         int var36;
         if(killEntities && (step + 4) % 8 == 0 && power >= 0.25D) {
            int i1;
            if(step != 4) {
               i1 = step * step - 25;
               int entity = 0;
               int dx = this.entitiesInRange.size() - 1;

               do {
                  var36 = (entity + dx) / 2;
                  int distance = ((Integer)((Entry)this.entitiesInRange.get(var36)).getKey()).intValue();
                  if(distance < i1) {
                     entity = var36 + 1;
                  } else if(distance > i1) {
                     dx = var36 - 1;
                  } else {
                     dx = var36;
                  }
               } while(entity < dx);
            } else {
               var36 = 0;
            }

            int distanceMax = step * step + 25;

            for(i1 = var36; i1 < this.entitiesInRange.size() && ((Integer)((Entry)this.entitiesInRange.get(var36)).getKey()).intValue() < distanceMax; ++i1) {
               Entity var37 = (Entity)((Entry)this.entitiesInRange.get(var36)).getValue();
               if((var37.posX - x) * (var37.posX - x) + (var37.posY - y) * (var37.posY - y) + (var37.posZ - z) * (var37.posZ - z) <= 25.0D) {
                  var37.attackEntityFrom(this.damageSource, (int)(32.0D * power / 8.0D));
                  if(var37 instanceof EntityPlayer) {
                     EntityPlayer var38 = (EntityPlayer)var37;
                     if(this.damageSource == IC2DamageSource.nuke && this.igniter != null && var38.username.equals(this.igniter) && var38.getHealth() <= 0) {
                        IC2.achievements.issueAchievement(var38, "dieFromOwnNuke");
                     }
                  }

                  double var39 = var37.posX - this.explosionX;
                  double dy = var37.posY - this.explosionY;
                  double dz = var37.posZ - this.explosionZ;
                  double distance1 = Math.sqrt(var39 * var39 + dy * dy + dz * dz);
                  var37.motionX += var39 / distance1 * 0.7D * power / 8.0D;
                  var37.motionY += dy / distance1 * 0.7D * power / 8.0D;
                  var37.motionZ += dz / distance1 * 0.7D * power / 8.0D;
                  if(!var37.isEntityAlive()) {
                     this.entitiesInRange.remove(i1);
                     --i1;
                  }
               }
            }
         }

         if(absorption > 10.0D) {
            for(var36 = 0; var36 < 5; ++var36) {
               this.shootRay(x, y, z, this.ExplosionRNG.nextDouble() * 2.0D * 3.141592653589793D, this.ExplosionRNG.nextDouble() * 3.141592653589793D, absorption * 0.4D, false);
            }
         }

         power -= absorption;
         x += deltaX;
         y += deltaY;
         z += deltaZ;
         if(y <= 0.0D || y >= (double)this.mapHeight) {
            break;
         }

         ++step;
      }

   }

   private int getBlockId(int x, int y, int z) {
      int chunkX = x >> 4;
      int chunkZ = z >> 4;
      if(this.lastChunkX != chunkX || this.lastChunkZ != chunkZ) {
         this.lastChunkX = chunkX;
         this.lastChunkZ = chunkZ;
         this.lastChunk = this.worldObj.getChunkFromChunkCoords(chunkX, chunkZ);
      }

      return this.lastChunk.getBlockID(x & 15, y, z & 15);
   }

   static class DropData {

      int n;
      int maxY;


      DropData(int n, int y) {
         this.n = n;
         this.maxY = y;
      }

      public ExplosionIC2.DropData add(int n, int y) {
         this.n += n;
         if(y > this.maxY) {
            this.maxY = y;
         }

         return this;
      }
   }

   static class XZposition {

      int x;
      int z;


      XZposition(int x, int z) {
         this.x = x;
         this.z = z;
      }

      public boolean equals(Object obj) {
         if(!(obj instanceof ExplosionIC2.XZposition)) {
            return false;
         } else {
            ExplosionIC2.XZposition xZposition = (ExplosionIC2.XZposition)obj;
            return xZposition.x == this.x && xZposition.z == this.z;
         }
      }

      public int hashCode() {
         return this.x * 31 ^ this.z;
      }
   }

   static class ItemWithMeta {

      int itemId;
      int metaData;


      ItemWithMeta(int itemId, int metaData) {
         this.itemId = itemId;
         this.metaData = metaData;
      }

      public boolean equals(Object obj) {
         if(!(obj instanceof ExplosionIC2.ItemWithMeta)) {
            return false;
         } else {
            ExplosionIC2.ItemWithMeta itemWithMeta = (ExplosionIC2.ItemWithMeta)obj;
            return itemWithMeta.itemId == this.itemId && itemWithMeta.metaData == this.metaData;
         }
      }

      public int hashCode() {
         return this.itemId * 31 ^ this.metaData;
      }
   }
}
