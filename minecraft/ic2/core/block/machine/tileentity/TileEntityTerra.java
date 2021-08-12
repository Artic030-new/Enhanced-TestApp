package ic2.core.block.machine.tileentity;

import ic2.api.Direction;
import ic2.api.ITerraformingBP;
import ic2.core.IC2;
import ic2.core.audio.AudioSource;
import ic2.core.audio.PositionSpec;
import ic2.core.block.machine.tileentity.TileEntityElecMachine;
import ic2.core.util.StackUtil;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;

public class TileEntityTerra extends TileEntityElecMachine {

   public int failedAttempts = 0;
   public int lastX = -1;
   public int lastY = -1;
   public int lastZ = -1;
   public AudioSource audioSource;
   public int inactiveTicks = 0;


   public TileEntityTerra() {
      super(1, 0, 100000, 512);
   }

   public String getInvName() {
      return "Terraformer";
   }

   public void updateEntity() {
      super.updateEntity();
      boolean newActive = false;
      if(super.inventory[0] != null && super.inventory[0].getItem() instanceof ITerraformingBP) {
         ITerraformingBP tfbp = (ITerraformingBP)super.inventory[0].getItem();
         if(super.energy >= tfbp.getConsume()) {
            newActive = true;
            int x = super.xCoord;
            int z = super.zCoord;
            boolean range = true;
            int var6;
            if(this.lastY > -1) {
               var6 = tfbp.getRange() / 10;
               x = this.lastX - super.worldObj.rand.nextInt(var6 + 1) + super.worldObj.rand.nextInt(var6 + 1);
               z = this.lastZ - super.worldObj.rand.nextInt(var6 + 1) + super.worldObj.rand.nextInt(var6 + 1);
            } else {
               if(this.failedAttempts > 4) {
                  this.failedAttempts = 4;
               }

               var6 = tfbp.getRange() * (this.failedAttempts + 1) / 5;
               x = x - super.worldObj.rand.nextInt(var6 + 1) + super.worldObj.rand.nextInt(var6 + 1);
               z = z - super.worldObj.rand.nextInt(var6 + 1) + super.worldObj.rand.nextInt(var6 + 1);
            }

            if(tfbp.terraform(super.worldObj, x, z, super.yCoord)) {
               super.energy -= tfbp.getConsume();
               this.failedAttempts = 0;
               this.lastX = x;
               this.lastZ = z;
               this.lastY = super.yCoord;
            } else {
               super.energy -= tfbp.getConsume() / 10;
               ++this.failedAttempts;
               this.lastY = -1;
            }
         }
      }

      if(newActive) {
         this.inactiveTicks = 0;
         this.setActive(true);
      } else if(!newActive && this.getActive() && this.inactiveTicks++ > 30) {
         this.setActive(false);
      }

   }

   public void onUnloaded() {
      if(IC2.platform.isRendering() && this.audioSource != null) {
         IC2.audioManager.removeSources(this);
         this.audioSource = null;
      }

      super.onUnloaded();
   }

   public int injectEnergy(Direction directionFrom, int amount) {
      if(amount > 512) {
         IC2.explodeMachineAt(super.worldObj, super.xCoord, super.yCoord, super.zCoord);
         return 0;
      } else if(super.energy + amount > super.maxEnergy) {
         int unused = super.energy + amount - super.maxEnergy;
         super.energy = super.maxEnergy;
         return unused;
      } else {
         super.energy += amount;
         return 0;
      }
   }

   public boolean ejectBlueprint() {
      if(super.inventory[0] == null) {
         return false;
      } else {
         if(IC2.platform.isSimulating()) {
            StackUtil.dropAsEntity(super.worldObj, super.xCoord, super.yCoord, super.zCoord, super.inventory[0]);
            super.inventory[0] = null;
         }

         return true;
      }
   }

   public void insertBlueprint(ItemStack tfbp) {
      this.ejectBlueprint();
      super.inventory[0] = tfbp;
   }

   public static int getFirstSolidBlockFrom(World world, int x, int z, int y) {
      while(y > 0) {
         if(world.isBlockOpaqueCube(x, y, z)) {
            return y;
         }

         --y;
      }

      return -1;
   }

   public static int getFirstBlockFrom(World world, int x, int z, int y) {
      while(y > 0) {
         if(world.getBlockId(x, y, z) != 0) {
            return y;
         }

         --y;
      }

      return -1;
   }

   public static boolean switchGround(World world, Block from, Block to, int x, int y, int z, boolean upwards) {
      int id;
      if(upwards) {
         ++y;
         id = y;

         while(true) {
            int id1 = world.getBlockId(x, y - 1, z);
            if(id1 == 0 || Block.blocksList[id1] != from) {
               if(id == y) {
                  return false;
               } else {
                  world.setBlockWithNotify(x, y, z, to.blockID);
                  return true;
               }
            }

            --y;
         }
      } else {
         while(true) {
            id = world.getBlockId(x, y, z);
            if(id == 0 || Block.blocksList[id] != to) {
               id = world.getBlockId(x, y, z);
               if(id != 0 && Block.blocksList[id] == from) {
                  world.setBlockWithNotify(x, y, z, to.blockID);
                  return true;
               } else {
                  return false;
               }
            }

            --y;
         }
      }
   }

   public static BiomeGenBase getBiomeAt(World world, int x, int z) {
      return world.getChunkFromBlockCoords(x, z).getBiomeGenForWorldCoords(x & 15, z & 15, world.getWorldChunkManager());
   }

   public static void setBiomeAt(World world, int x, int z, BiomeGenBase biome) {
      Chunk chunk = world.getChunkFromBlockCoords(x, z);
      byte[] array = chunk.getBiomeArray();
      array[(z & 15) << 4 | x & 15] = (byte)(biome.biomeID & 255);
      chunk.setBiomeArray(array);
   }

   public void onNetworkUpdate(String field) {
      if(field.equals("active") && super.prevActive != this.getActive()) {
         if(this.audioSource == null) {
            this.audioSource = IC2.audioManager.createSource(this, PositionSpec.Center, "Terraformers/TerraformerGenericloop.ogg", true, false, IC2.audioManager.defaultVolume);
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
}
