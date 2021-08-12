package ic2.core.block;

import ic2.core.Ic2Items;
import ic2.core.item.ItemBooze;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileEntityBarrel extends TileEntity {

   public int type = 0;
   public int boozeAmount = 0;
   public int age = 0;
   public boolean detailed = true;
   public int treetapSide = 0;
   public int hopsCount = 0;
   public int wheatCount = 0;
   public int solidRatio = 0;
   public int hopsRatio = 0;
   public int timeRatio = 0;


   public void set(int value) {
      this.type = ItemBooze.getTypeOfValue(value);
      if(this.type > 0) {
         this.boozeAmount = ItemBooze.getAmountOfValue(value);
      }

      if(this.type == 1) {
         this.detailed = false;
         this.hopsRatio = ItemBooze.getHopsRatioOfBeerValue(value);
         this.solidRatio = ItemBooze.getSolidRatioOfBeerValue(value);
         this.timeRatio = ItemBooze.getTimeRatioOfBeerValue(value);
      }

      if(this.type == 2) {
         this.detailed = true;
         this.age = this.timeNedForRum(this.boozeAmount) * ItemBooze.getProgressOfRumValue(value) / 100;
      }

   }

   public void readFromNBT(NBTTagCompound nbttagcompound) {
      super.readFromNBT(nbttagcompound);
      this.type = nbttagcompound.getByte("type");
      this.boozeAmount = nbttagcompound.getByte("waterCount");
      this.age = nbttagcompound.getInteger("age");
      this.treetapSide = nbttagcompound.getByte("treetapSide");
      this.detailed = nbttagcompound.getBoolean("detailed");
      if(this.type == 1) {
         if(this.detailed) {
            this.hopsCount = nbttagcompound.getByte("hopsCount");
            this.wheatCount = nbttagcompound.getByte("wheatCount");
         }

         this.solidRatio = nbttagcompound.getByte("solidRatio");
         this.hopsRatio = nbttagcompound.getByte("hopsRatio");
         this.timeRatio = nbttagcompound.getByte("timeRatio");
      }

   }

   public void writeToNBT(NBTTagCompound nbttagcompound) {
      super.writeToNBT(nbttagcompound);
      nbttagcompound.setByte("type", (byte)this.type);
      nbttagcompound.setByte("waterCount", (byte)this.boozeAmount);
      nbttagcompound.setInteger("age", this.age);
      nbttagcompound.setByte("treetapSide", (byte)this.treetapSide);
      nbttagcompound.setBoolean("detailed", this.detailed);
      if(this.type == 1) {
         if(this.detailed) {
            nbttagcompound.setByte("hopsCount", (byte)this.hopsCount);
            nbttagcompound.setByte("wheatCount", (byte)this.wheatCount);
         }

         nbttagcompound.setByte("solidRatio", (byte)this.solidRatio);
         nbttagcompound.setByte("hopsRatio", (byte)this.hopsRatio);
         nbttagcompound.setByte("timeRatio", (byte)this.timeRatio);
      }

   }

   public void updateEntity() {
      if(!this.isEmpty() && this.treetapSide < 2) {
         ++this.age;
         if(this.type == 1 && this.timeRatio < 5) {
            int x = this.timeRatio;
            if(x == 4) {
               x += 2;
            }

            if((double)this.age >= 24000.0D * Math.pow(3.0D, (double)x)) {
               this.age = 0;
               ++this.timeRatio;
            }
         }
      }

   }

   public boolean isEmpty() {
      return this.type == 0 || this.boozeAmount <= 0;
   }

   public boolean rightclick(EntityPlayer player) {
      ItemStack cur = player.getCurrentEquippedItem();
      if(cur == null) {
         return false;
      } else if(cur.itemID == Item.bucketWater.itemID) {
         if(this.detailed && this.boozeAmount < 32 && this.type <= 1) {
            this.type = 1;
            cur.itemID = Item.bucketEmpty.itemID;
            ++this.boozeAmount;
            return true;
         } else {
            return false;
         }
      } else {
         int wantgive;
         if(cur.itemID == Ic2Items.waterCell.itemID) {
            if(this.detailed && this.type <= 1) {
               this.type = 1;
               wantgive = cur.stackSize;
               if(player.isSneaking()) {
                  wantgive = 1;
               }

               if(wantgive > 32 - this.boozeAmount) {
                  wantgive = 32 - this.boozeAmount;
               }

               if(wantgive <= 0) {
                  return false;
               } else {
                  this.boozeAmount += wantgive;
                  cur.stackSize -= wantgive;
                  if(cur.stackSize <= 0) {
                     player.inventory.mainInventory[player.inventory.currentItem] = null;
                  }

                  return true;
               }
            } else {
               return false;
            }
         } else if(cur.itemID == Item.wheat.itemID) {
            if(this.detailed && this.type <= 1) {
               this.type = 1;
               wantgive = cur.stackSize;
               if(player.isSneaking()) {
                  wantgive = 1;
               }

               if(wantgive > 64 - this.wheatCount) {
                  wantgive = 64 - this.wheatCount;
               }

               if(wantgive <= 0) {
                  return false;
               } else {
                  this.wheatCount += wantgive;
                  cur.stackSize -= wantgive;
                  if(cur.stackSize <= 0) {
                     player.inventory.mainInventory[player.inventory.currentItem] = null;
                  }

                  this.alterComposition();
                  return true;
               }
            } else {
               return false;
            }
         } else if(cur.itemID == Ic2Items.hops.itemID) {
            if(this.detailed && this.type <= 1) {
               this.type = 1;
               wantgive = cur.stackSize;
               if(player.isSneaking()) {
                  wantgive = 1;
               }

               if(wantgive > 64 - this.hopsCount) {
                  wantgive = 64 - this.hopsCount;
               }

               if(wantgive <= 0) {
                  return false;
               } else {
                  this.hopsCount += wantgive;
                  cur.stackSize -= wantgive;
                  if(cur.stackSize <= 0) {
                     player.inventory.mainInventory[player.inventory.currentItem] = null;
                  }

                  this.alterComposition();
                  return true;
               }
            } else {
               return false;
            }
         } else if(cur.itemID != Item.reed.itemID) {
            return false;
         } else if(this.age <= 600 && (this.type <= 0 || this.type == 2)) {
            this.type = 2;
            wantgive = cur.stackSize;
            if(player.isSneaking()) {
               wantgive = 1;
            }

            if(wantgive > 32 - this.boozeAmount) {
               wantgive = 32 - this.boozeAmount;
            }

            if(wantgive <= 0) {
               return false;
            } else {
               this.boozeAmount += wantgive;
               cur.stackSize -= wantgive;
               if(cur.stackSize <= 0) {
                  player.inventory.mainInventory[player.inventory.currentItem] = null;
               }

               return true;
            }
         } else {
            return false;
         }
      }
   }

   public void alterComposition() {
      if(this.timeRatio == 0) {
         this.age = 0;
      }

      if(this.timeRatio == 1) {
         if(super.worldObj.rand.nextBoolean()) {
            this.timeRatio = 0;
         } else if(super.worldObj.rand.nextBoolean()) {
            this.timeRatio = 5;
         }
      }

      if(this.timeRatio == 2 && super.worldObj.rand.nextBoolean()) {
         this.timeRatio = 5;
      }

      if(this.timeRatio > 2) {
         this.timeRatio = 5;
      }

   }

   public boolean drainLiquid(int amount) {
      if(this.isEmpty()) {
         return false;
      } else if(amount > this.boozeAmount) {
         return false;
      } else {
         this.enforceUndetailed();
         if(this.type == 2) {
            int progress = this.age * 100 / this.timeNedForRum(this.boozeAmount);
            this.boozeAmount -= amount;
            this.age = progress / 100 * this.timeNedForRum(this.boozeAmount);
         } else {
            this.boozeAmount -= amount;
         }

         if(this.boozeAmount <= 0) {
            if(this.type == 1) {
               this.hopsCount = 0;
               this.wheatCount = 0;
               this.hopsRatio = 0;
               this.solidRatio = 0;
               this.timeRatio = 0;
            }

            this.type = 0;
            this.detailed = true;
            this.boozeAmount = 0;
         }

         return true;
      }
   }

   public void enforceUndetailed() {
      if(this.detailed) {
         this.detailed = false;
         if(this.type == 1) {
            float hops = this.wheatCount > 0?(float)this.hopsCount / (float)this.wheatCount:10.0F;
            if(this.hopsCount <= 0 && this.wheatCount <= 0) {
               hops = 0.0F;
            }

            float solid = this.boozeAmount > 0?(float)(this.hopsCount + this.wheatCount) / (float)this.boozeAmount:10.0F;
            if(hops <= 0.25F) {
               this.hopsRatio = 0;
            }

            if(hops > 0.25F && hops <= 0.33333334F) {
               this.hopsRatio = 1;
            }

            if(hops > 0.33333334F && hops <= 0.5F) {
               this.hopsRatio = 2;
            }

            if(hops > 0.5F && hops < 2.0F) {
               this.hopsRatio = 3;
            }

            if(hops >= 2.0F && hops < 3.0F) {
               this.hopsRatio = 4;
            }

            if(hops >= 3.0F && hops < 4.0F) {
               this.hopsRatio = 5;
            }

            if(hops >= 4.0F && hops < 5.0F) {
               this.hopsRatio = 6;
            }

            if(hops >= 5.0F) {
               this.timeRatio = 5;
            }

            if(solid <= 0.41666666F && solid > 0.41666666F && solid <= 0.5F) {
               this.solidRatio = 1;
            }

            if(solid > 0.5F && solid < 1.0F) {
               this.solidRatio = 2;
            }

            if(solid == 1.0F) {
               this.solidRatio = 3;
            }

            if(solid > 1.0F && solid < 2.0F) {
               this.solidRatio = 4;
            }

            if(solid >= 2.0F && solid < 2.4F) {
               this.solidRatio = 5;
            }

            if(solid >= 2.4F && solid < 4.0F) {
               this.solidRatio = 6;
            }

            if(solid >= 4.0F) {
               this.timeRatio = 5;
            }
         }

      }
   }

   public boolean useTreetapOn(EntityPlayer player, int side) {
      ItemStack cur = player.getCurrentEquippedItem();
      if(cur != null && cur.itemID == Ic2Items.treetap.itemID && cur.getItemDamage() == 0 && side > 1) {
         this.treetapSide = side;
         this.update();
         if(!player.capabilities.isCreativeMode) {
            --player.inventory.mainInventory[player.inventory.currentItem].stackSize;
            if(player.inventory.mainInventory[player.inventory.currentItem].stackSize == 0) {
               player.inventory.mainInventory[player.inventory.currentItem] = null;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public void update() {
      super.worldObj.markBlockForUpdate(super.xCoord, super.yCoord, super.zCoord);
   }

   public int calculateMetaValue() {
      if(this.isEmpty()) {
         return 0;
      } else {
         byte value;
         int value1;
         if(this.type == 1) {
            this.enforceUndetailed();
            value = 0;
            value1 = value | this.timeRatio;
            value1 <<= 3;
            value1 |= this.hopsRatio;
            value1 <<= 3;
            value1 |= this.solidRatio;
            value1 <<= 5;
            value1 |= this.boozeAmount - 1;
            value1 <<= 2;
            value1 |= this.type;
            return value1;
         } else if(this.type == 2) {
            this.enforceUndetailed();
            value = 0;
            int progress = this.age * 100 / this.timeNedForRum(this.boozeAmount);
            if(progress > 100) {
               progress = 100;
            }

            value1 = value | progress;
            value1 <<= 5;
            value1 |= this.boozeAmount - 1;
            value1 <<= 2;
            value1 |= this.type;
            return value1;
         } else {
            return 0;
         }
      }
   }

   public int timeNedForRum(int amount) {
      return (int)((double)(1200 * amount) * Math.pow(0.95D, (double)(amount - 1)));
   }
}
