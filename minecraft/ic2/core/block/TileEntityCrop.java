package ic2.core.block;

import ic2.api.BaseSeed;
import ic2.api.CropCard;
import ic2.api.Crops;
import ic2.api.TECrop;
import ic2.api.network.INetworkDataProvider;
import ic2.api.network.INetworkUpdateListener;
import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.block.crop.IC2Crops;
import ic2.core.item.ElectricItem;
import ic2.core.item.ItemCropSeed;
import ic2.core.item.tool.ItemCropnalyzer;
import ic2.core.util.StackUtil;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.EnumSkyBlock;

public class TileEntityCrop extends TECrop implements INetworkDataProvider, INetworkUpdateListener {

   public int growthPoints = 0;
   public boolean upgraded = false;
   public char ticker;
   public boolean dirty;
   public static char tickRate = 256;
   private boolean created;
   public byte humidity;
   public byte nutrients;
   public byte airQuality;


   public TileEntityCrop() {
      this.ticker = (char)IC2.random.nextInt(tickRate);
      this.dirty = true;
      this.created = false;
      this.humidity = -1;
      this.nutrients = -1;
      this.airQuality = -1;
   }

   public void readFromNBT(NBTTagCompound nbttagcompound) {
      super.readFromNBT(nbttagcompound);
      super.id = nbttagcompound.getShort("cropid");
      super.size = nbttagcompound.getByte("size");
      super.statGrowth = nbttagcompound.getByte("statGrowth");
      super.statGain = nbttagcompound.getByte("statGain");
      super.statResistance = nbttagcompound.getByte("statResistance");

      for(int var3 = 0; var3 < super.custumData.length; ++var3) {
         super.custumData[var3] = nbttagcompound.getShort("data" + var3);
      }

      this.growthPoints = nbttagcompound.getInteger("growthPoints");

      try {
         super.nutrientStorage = nbttagcompound.getInteger("nutrientStorage");
         super.waterStorage = nbttagcompound.getInteger("waterStorage");
      } catch (Throwable var31) {
         super.nutrientStorage = nbttagcompound.getByte("nutrientStorage");
         super.waterStorage = nbttagcompound.getByte("waterStorage");
      }

      this.upgraded = nbttagcompound.getBoolean("upgraded");
      super.scanLevel = nbttagcompound.getByte("scanLevel");
   }

   public void writeToNBT(NBTTagCompound nbttagcompound) {
      super.writeToNBT(nbttagcompound);
      nbttagcompound.setShort("cropid", super.id);
      nbttagcompound.setByte("size", super.size);
      nbttagcompound.setByte("statGrowth", super.statGrowth);
      nbttagcompound.setByte("statGain", super.statGain);
      nbttagcompound.setByte("statResistance", super.statResistance);

      for(int x = 0; x < super.custumData.length; ++x) {
         nbttagcompound.setShort("data" + x, super.custumData[x]);
      }

      nbttagcompound.setInteger("growthPoints", this.growthPoints);
      nbttagcompound.setInteger("nutrientStorage", super.nutrientStorage);
      nbttagcompound.setInteger("waterStorage", super.waterStorage);
      nbttagcompound.setBoolean("upgraded", this.upgraded);
      nbttagcompound.setByte("scanLevel", super.scanLevel);
   }

   public void updateEntity() {
      super.updateEntity();
      if(!this.created && !IC2.platform.isSimulating()) {
         IC2.network.requestInitialData(this);
         this.created = true;
      }

      ++this.ticker;
      if(this.ticker % tickRate == 0) {
         this.tick();
      }

      if(this.dirty) {
         this.dirty = false;
         super.worldObj.markBlockForUpdate(super.xCoord, super.yCoord, super.zCoord);
         super.worldObj.updateLightByType(EnumSkyBlock.Block, super.xCoord, super.yCoord, super.zCoord);
         if(IC2.platform.isSimulating()) {
            IC2.network.announceBlockUpdate(super.worldObj, super.xCoord, super.yCoord, super.zCoord);
            if(!IC2.platform.isRendering()) {
               Iterator i$ = this.getNetworkedFields().iterator();

               while(i$.hasNext()) {
                  String field = (String)i$.next();
                  IC2.network.updateTileEntityField(this, field);
               }
            }
         }
      }

   }

   public List getNetworkedFields() {
      Vector ret = new Vector(2);
      ret.add("id");
      ret.add("size");
      ret.add("upgraded");
      ret.add("custumData");
      return ret;
   }

   public void tick() {
      if(IC2.platform.isSimulating()) {
         if(this.ticker % (tickRate << 2) == 0) {
            this.humidity = this.updateHumidity();
         }

         if((this.ticker + tickRate) % (tickRate << 2) == 0) {
            this.nutrients = this.updateNutrients();
         }

         if((this.ticker + tickRate * 2) % (tickRate << 2) == 0) {
            this.airQuality = this.updateAirQuality();
         }

         if(super.id < 0 && (!this.upgraded || !this.attemptCrossing())) {
            if(IC2.random.nextInt(100) != 0 || this.hasEx()) {
               if(super.exStorage > 0 && IC2.random.nextInt(10) == 0) {
                  --super.exStorage;
               }

               return;
            }

            this.reset();
            super.id = (short)IC2Crops.weed.getId();
            super.size = 1;
         }

         this.crop().tick(this);
         if(this.crop().canGrow(this)) {
            this.growthPoints += this.calcGrowthRate();
            if(super.id > -1 && this.growthPoints >= this.crop().growthDuration(this)) {
               this.growthPoints = 0;
               ++super.size;
               this.dirty = true;
            }
         }

         if(super.nutrientStorage > 0) {
            --super.nutrientStorage;
         }

         if(super.waterStorage > 0) {
            --super.waterStorage;
         }

         if(this.crop().isWeed(this) && IC2.random.nextInt(50) - super.statGrowth <= 2) {
            this.generateWeed();
         }
      }

   }

   public void generateWeed() {
      int x = super.xCoord;
      int y = super.yCoord;
      int z = super.zCoord;
      switch(IC2.random.nextInt(4)) {
      case 0:
         ++x;
      case 1:
         --x;
      case 2:
         ++z;
      case 3:
         --z;
      }

      if(super.worldObj.getBlockTileEntity(x, y, z) instanceof TileEntityCrop) {
         TileEntityCrop var6 = (TileEntityCrop)super.worldObj.getBlockTileEntity(x, y, z);
         if(var6.id == -1 || !var6.crop().isWeed(var6) && IC2.random.nextInt(32) >= var6.statResistance && !var6.hasEx()) {
            byte newGrowth = super.statGrowth;
            if(var6.statGrowth > newGrowth) {
               newGrowth = var6.statGrowth;
            }

            if(newGrowth < 31 && IC2.random.nextBoolean()) {
               ++newGrowth;
            }

            var6.reset();
            var6.id = 0;
            var6.size = 1;
            var6.statGrowth = newGrowth;
         }
      } else if(super.worldObj.getBlockId(x, y, z) == 0) {
         int var61 = super.worldObj.getBlockId(x, y - 1, z);
         if(var61 == Block.dirt.blockID || var61 == Block.grass.blockID || var61 == Block.tilledField.blockID) {
            super.worldObj.setBlockWithNotify(x, y - 1, z, Block.grass.blockID);
            super.worldObj.setBlockAndMetadataWithNotify(x, y, z, Block.tallGrass.blockID, 1);
         }
      }

   }

   public boolean hasEx() {
      if(super.exStorage > 0) {
         super.exStorage -= 5;
         return true;
      } else {
         return false;
      }
   }

   public boolean attemptCrossing() {
      if(IC2.random.nextInt(3) != 0) {
         return false;
      } else {
         LinkedList crops = new LinkedList();
         this.askCropJoinCross(super.xCoord - 1, super.yCoord, super.zCoord, crops);
         this.askCropJoinCross(super.xCoord + 1, super.yCoord, super.zCoord, crops);
         this.askCropJoinCross(super.xCoord, super.yCoord, super.zCoord - 1, crops);
         this.askCropJoinCross(super.xCoord, super.yCoord, super.zCoord + 1, crops);
         if(crops.size() < 2) {
            return false;
         } else {
            int[] ratios = new int[256];

            int total;
            int count;
            for(total = 1; total < ratios.length; ++total) {
               if(CropCard.idExists(total) && CropCard.getCrop(total).canGrow(this)) {
                  for(count = 0; count < crops.size(); ++count) {
                     ratios[total] += this.calculateRatioFor(CropCard.getCrop(total), ((TileEntityCrop)crops.get(count)).crop());
                  }
               }
            }

            total = 0;

            for(count = 0; count < ratios.length; ++count) {
               total += ratios[count];
            }

            total = IC2.random.nextInt(total);

            for(count = 0; count < ratios.length; ++count) {
               if(ratios[count] > 0 && ratios[count] > total) {
                  total = count;
                  break;
               }

               total -= ratios[count];
            }

            this.upgraded = false;
            super.id = (short)total;
            this.dirty = true;
            super.size = 1;
            super.statGrowth = 0;
            super.statResistance = 0;
            super.statGain = 0;

            for(count = 0; count < crops.size(); ++count) {
               super.statGrowth += ((TileEntityCrop)crops.get(count)).statGrowth;
               super.statResistance += ((TileEntityCrop)crops.get(count)).statResistance;
               super.statGain += ((TileEntityCrop)crops.get(count)).statGain;
            }

            count = crops.size();
            super.statGrowth = (byte)(super.statGrowth / count);
            super.statResistance = (byte)(super.statResistance / count);
            super.statGain = (byte)(super.statGain / count);
            super.statGrowth = (byte)(super.statGrowth + (IC2.random.nextInt(1 + 2 * count) - count));
            if(super.statGrowth < 0) {
               super.statGrowth = 0;
            }

            if(super.statGrowth > 31) {
               super.statGrowth = 31;
            }

            super.statGain = (byte)(super.statGain + (IC2.random.nextInt(1 + 2 * count) - count));
            if(super.statGain < 0) {
               super.statGain = 0;
            }

            if(super.statGain > 31) {
               super.statGain = 31;
            }

            super.statResistance = (byte)(super.statResistance + (IC2.random.nextInt(1 + 2 * count) - count));
            if(super.statResistance < 0) {
               super.statResistance = 0;
            }

            if(super.statResistance > 31) {
               super.statResistance = 31;
            }

            return true;
         }
      }
   }

   public int calculateRatioFor(CropCard a, CropCard b) {
      if(a == b) {
         return 500;
      } else {
         int value = 0;
         int i = 0;

         int j;
         while(i < 5) {
            j = a.stat(i) - b.stat(i);
            if(j < 0) {
               j *= -1;
            }

            switch(j) {
            default:
               --value;
            case 0:
               value += 2;
            case 1:
               ++value;
            case 2:
               ++i;
            }
         }

         for(i = 0; i < a.attributes().length; ++i) {
            for(j = 0; j < b.attributes().length; ++j) {
               if(a.attributes()[i].equalsIgnoreCase(b.attributes()[j])) {
                  value += 5;
               }
            }
         }

         if(b.tier() < a.tier() - 1) {
            value -= 2 * (a.tier() - b.tier());
         }

         if(b.tier() - 3 > a.tier()) {
            value -= b.tier() - a.tier();
         }

         if(value < 0) {
            value = 0;
         }

         return value;
      }
   }

   public void askCropJoinCross(int x, int y, int z, LinkedList crops) {
      if(super.worldObj.getBlockTileEntity(x, y, z) instanceof TileEntityCrop) {
         TileEntityCrop sidecrop = (TileEntityCrop)super.worldObj.getBlockTileEntity(x, y, z);
         if(sidecrop.id > 0 && sidecrop.crop().canGrow(this) && sidecrop.crop().canCross(sidecrop)) {
            int base = 4;
            if(sidecrop.statGrowth >= 16) {
               ++base;
            }

            if(sidecrop.statGrowth >= 30) {
               ++base;
            }

            if(sidecrop.statResistance >= 28) {
               base += 27 - sidecrop.statResistance;
            }

            if(base >= IC2.random.nextInt(20)) {
               crops.add(sidecrop);
            }
         }
      }

   }

   public boolean leftclick(EntityPlayer player) {
      if(super.id < 0) {
         if(this.upgraded) {
            this.upgraded = false;
            this.dirty = true;
            if(IC2.platform.isSimulating()) {
               StackUtil.dropAsEntity(super.worldObj, super.xCoord, super.yCoord, super.zCoord, new ItemStack(Ic2Items.crop.getItem()));
            }

            return true;
         } else {
            return false;
         }
      } else {
         return this.crop().leftclick(this, player);
      }
   }

   public boolean pick(boolean manual) {
      if(super.id < 0) {
         return false;
      } else {
         boolean bonus = this.harvest(false);
         float firstchance = this.crop().dropSeedChance(this);

         int drop;
         for(drop = 0; drop < super.statResistance; ++drop) {
            firstchance *= 1.1F;
         }

         drop = 0;
         int x;
         if(bonus) {
            if(IC2.random.nextFloat() <= (firstchance + 1.0F) * 0.8F) {
               ++drop;
            }

            float var7 = this.crop().dropSeedChance(this) + (float)super.statGrowth / 100.0F;
            if(!manual) {
               var7 *= 0.8F;
            }

            for(x = 23; x < super.statGain; ++x) {
               var7 *= 0.95F;
            }

            if(IC2.random.nextFloat() <= var7) {
               ++drop;
            }
         } else if(IC2.random.nextFloat() <= firstchance * 1.5F) {
            ++drop;
         }

         ItemStack[] var71 = new ItemStack[drop];

         for(x = 0; x < drop; ++x) {
            var71[x] = this.crop().getSeeds(this);
         }

         this.reset();
         if(IC2.platform.isSimulating() && var71 != null && var71.length > 0) {
            for(x = 0; x < var71.length; ++x) {
               if(var71[x].itemID != Ic2Items.cropSeed.itemID) {
                  var71[x].stackTagCompound = null;
               }

               StackUtil.dropAsEntity(super.worldObj, super.xCoord, super.yCoord, super.zCoord, var71[x]);
            }
         }

         return true;
      }
   }

   public boolean rightclick(EntityPlayer player) {
      ItemStack current = player.getCurrentEquippedItem();
      if(current != null) {
         if(super.id < 0) {
            if(current.itemID == Ic2Items.crop.itemID && !this.upgraded) {
               if(!player.capabilities.isCreativeMode) {
                  --current.stackSize;
                  if(current.stackSize <= 0) {
                     player.inventory.mainInventory[player.inventory.currentItem] = null;
                  }
               }

               this.upgraded = true;
               this.dirty = true;
               return true;
            }

            if(this.applyBaseSeed(current, player)) {
               return true;
            }
         } 
         else if(current.itemID == Ic2Items.cropnalyzer.itemID) {
            if(IC2.platform.isSimulating()) {
            	
            	 int euConsume = ItemCropnalyzer.getEuConsume();
            	 int tier = ItemCropnalyzer.getTir();
            	 ElectricItem.discharge(current, --euConsume, tier, true, false);
            	 
            	 String desc = this.getScanned();
               
               if(desc == null && current.getItemDamage() < 26) {
                  desc = " §8---------------------~§7Название§8~----------------------\n§6Название растения: §a" + this.crop().name() + " §8-------------------~§7Характеристика§8~--------------------\n§2Скорость роста:§e " + super.statGrowth + " §dУрожайность:§e " + super.statGain + " §3Защита:§e " + super.statResistance + "\n§8--------------~§7Качество местности§8~----------------\n§6Уровень влажности:§e " + this.humidity + "§6 Уровень освещения:§e " + this.getLightLevel() + "§6 Качество воздуха:§e " + this.airQuality + "\n§8----------------~§7Особые параметры§8~---------------\n§6Единиц удобрения:§e " + super.nutrientStorage + "§6 Качество защиты:§e " + super.exStorage + "§6 Качество увлажнения:§e " + super.waterStorage;
               }
              
               else {
            	   desc = "Внимание! Нехватает энергии для осуществления операции.\nИли данная культура является изветной";
               }
               
               IC2.platform.messagePlayer(player, desc);
               
            }

            return true;
         }

         if(current.itemID == Item.bucketWater.itemID || current.itemID == Ic2Items.waterCell.getItem().itemID) {
            if(super.waterStorage < 20) {
               super.waterStorage = 20;
               return true;
            }

            return current.itemID == Item.bucketWater.itemID;
         }

         if(current.itemID == Item.seeds.itemID) {
            if(super.nutrientStorage <= 50) {
               super.nutrientStorage += 25;
               --current.stackSize;
               if(current.stackSize <= 0) {
                  player.inventory.mainInventory[player.inventory.currentItem] = null;
               }

               return true;
            }

            return false;
         }

         if(current.itemID == Item.dyePowder.itemID && current.getItemDamage() == 15 || current.itemID == Ic2Items.fertilizer.itemID) {
            if(this.applyFertilizer(true)) {
               --current.stackSize;
               if(current.stackSize <= 0) {
                  player.inventory.mainInventory[player.inventory.currentItem] = null;
               }

               return true;
            }

            return false;
         }

         if(current.itemID == Ic2Items.hydratingCell.itemID) {
            if(this.applyHydration(true, current)) {
               if(current.stackSize <= 0) {
                  player.inventory.mainInventory[player.inventory.currentItem] = null;
               }

               return true;
            }

            return false;
         }

         if(current.itemID == Ic2Items.weedEx.itemID && this.applyWeedEx(true)) {
            current.damageItem(1, player);
            if(current.getItemDamage() >= current.getMaxDamage()) {
               --current.stackSize;
            }

            if(current.stackSize <= 0) {
               player.inventory.mainInventory[player.inventory.currentItem] = null;
            }

            return true;
         }
      }

      return super.id < 0?false:this.crop().rightclick(this, player);
   }

   public boolean applyBaseSeed(ItemStack current, EntityPlayer player) {
      BaseSeed seed = CropCard.getBaseSeed(current);
      if(seed != null) {
         if(current.stackSize < seed.stackSize) {
            return false;
         }

         if(this.tryPlantIn(seed.id, seed.size, seed.statGrowth, seed.statGain, seed.statResistance, 1)) {
            if(current.getItem().hasContainerItem()) {
               current = current.getItem().getContainerItemStack(current);
            } else {
               current.stackSize -= seed.stackSize;
               if(current.stackSize <= 0) {
                  player.inventory.mainInventory[player.inventory.currentItem] = null;
               }
            }

            return true;
         }
      }

      return false;
   }

   public boolean tryPlantIn(int i, int si, int statGr, int statGa, int statRe, int scan) {
      if(super.id <= -1 && i > 0 && !this.upgraded) {
         if(!CropCard.getCrop(i).canGrow(this)) {
            return false;
         } else {
            this.reset();
            super.id = (short)i;
            super.size = (byte)si;
            super.statGrowth = (byte)statGr;
            super.statGain = (byte)statGa;
            super.statResistance = (byte)statRe;
            super.scanLevel = (byte)scan;
            return true;
         }
      } else {
         return false;
      }
   }

   public boolean applyFertilizer(boolean manual) {
      if(super.nutrientStorage >= 100) {
         return false;
      } else {
         super.nutrientStorage += manual?100:90;
         return true;
      }
   }

   public boolean applyHydration(boolean manual, ItemStack current) {
      if((manual || super.waterStorage < 180) && super.waterStorage < 200) {
         int apply = manual?200 - super.waterStorage:180 - super.waterStorage;
         if(apply + current.getItemDamage() > current.getMaxDamage()) {
            apply = current.getMaxDamage() - current.getItemDamage();
         }

         current.damageItem(apply, (EntityLiving)null);
         if(current.getItemDamage() >= current.getMaxDamage()) {
            --current.stackSize;
         }

         super.waterStorage += apply;
         return true;
      } else {
         return false;
      }
   }

   public boolean applyWeedEx(boolean manual) {
      if((super.exStorage < 100 || !manual) && super.exStorage < 150) {
         super.exStorage += 50;
         boolean trigger = super.worldObj.rand.nextInt(3) == 0;
         if(manual) {
            trigger = super.worldObj.rand.nextInt(5) == 0;
         }

         if(super.id > 0 && super.exStorage >= 75 && trigger) {
            switch(super.worldObj.rand.nextInt(5)) {
            case 0:
               if(super.statGrowth > 0) {
                  --super.statGrowth;
               }
            case 1:
               if(super.statGain > 0) {
                  --super.statGain;
               }
            default:
               if(super.statResistance > 0) {
                  --super.statResistance;
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean harvest(boolean manual) {
      if(super.id >= 0 && this.crop().canBeHarvested(this)) {
         float chance = this.crop().dropGainChance();

         int drop;
         for(drop = 0; drop < super.statGain; ++drop) {
            chance *= 1.03F;
         }

         chance -= IC2.random.nextFloat();

         for(drop = 0; chance > 0.0F; chance -= IC2.random.nextFloat()) {
            ++drop;
         }

         ItemStack[] re = new ItemStack[drop];

         int x;
         for(x = 0; x < drop; ++x) {
            re[x] = this.crop().getGain(this);
            if(re[x] != null && IC2.random.nextInt(100) <= super.statGain) {
               ++re[x].stackSize;
            }
         }

         super.size = this.crop().getSizeAfterHarvest(this);
         this.dirty = true;
         if(IC2.platform.isSimulating() && re != null && re.length > 0) {
            for(x = 0; x < re.length; ++x) {
               StackUtil.dropAsEntity(super.worldObj, super.xCoord, super.yCoord, super.zCoord, re[x]);
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public void onNeighbourChange() {
      if(super.id >= 0) {
         this.crop().onNeighbourChange(this);
      }

   }

   public boolean emitRedstone() {
      return super.id < 0?false:this.crop().emitRedstone(this);
   }

   public void onBlockDestroyed() {
      if(super.id >= 0) {
         this.crop().onBlockDestroyed(this);
      }

   }

   public int getEmittedLight() {
      return super.id < 0?0:this.crop().getEmittedLight(this);
   }

   public byte getHumidity() {
      if(this.humidity == -1) {
         this.humidity = this.updateHumidity();
      }

      return this.humidity;
   }

   public byte getNutrients() {
      if(this.nutrients == -1) {
         this.nutrients = this.updateNutrients();
      }

      return this.nutrients;
   }

   public byte getAirQuality() {
      if(this.airQuality == -1) {
         this.airQuality = this.updateAirQuality();
      }

      return this.airQuality;
   }

   public byte updateHumidity() {
      int value = Crops.getHumidityBiomeBonus(super.worldObj.getWorldChunkManager().getBiomeGenAt(super.xCoord, super.zCoord));
      if(super.worldObj.getBlockMetadata(super.xCoord, super.yCoord - 1, super.zCoord) >= 7) {
         value += 2;
      }

      if(super.waterStorage >= 5) {
         value += 2;
      }

      value += (super.waterStorage + 24) / 25;
      return (byte)value;
   }

   public byte updateNutrients() {
      int value = Crops.getNutrientBiomeBonus(super.worldObj.getWorldChunkManager().getBiomeGenAt(super.xCoord, super.zCoord));

      for(int i = 2; i < 5 && super.worldObj.getBlockId(super.xCoord, super.yCoord - i, super.zCoord) == Block.dirt.blockID; ++i) {
         ++value;
      }

      value += (super.nutrientStorage + 19) / 20;
      return (byte)value;
   }

   public byte updateAirQuality() {
      byte value = 0;
      int height = (super.yCoord - 64) / 15;
      if(height > 4) {
         height = 4;
      }

      if(height < 0) {
         height = 0;
      }

      int var6 = value + height;
      int fresh = 9;

      for(int x = super.xCoord - 1; x < super.xCoord + 1 && fresh > 0; ++x) {
         for(int z = super.zCoord - 1; z < super.zCoord + 1 && fresh > 0; ++z) {
            if(super.worldObj.isBlockOpaqueCube(x, super.yCoord, z) || super.worldObj.getBlockTileEntity(x, super.yCoord, z) instanceof TileEntityCrop) {
               --fresh;
            }
         }
      }

      var6 += fresh / 2;
      if(super.worldObj.canBlockSeeTheSky(super.xCoord, super.yCoord + 1, super.zCoord)) {
         var6 += 2;
      }

      return (byte)var6;
   }

   public byte updateMultiCulture() {
      LinkedList crops = new LinkedList();

      for(int x = -1; x < 1; ++x) {
         for(int z = -1; z < 1; ++z) {
            if(super.worldObj.getBlockTileEntity(x + super.xCoord, super.yCoord, z + super.zCoord) instanceof TileEntityCrop) {
               this.addIfNotPresent(((TileEntityCrop)super.worldObj.getBlockTileEntity(x + super.xCoord, super.yCoord, z + super.zCoord)).crop(), crops);
            }
         }
      }

      return (byte)(crops.size() - 1);
   }

   public void addIfNotPresent(CropCard crop, LinkedList crops) {
      for(int i = 0; i < crops.size(); ++i) {
         if(crop == crops.get(i)) {
            return;
         }
      }

      crops.add(crop);
   }

   public int calcGrowthRate() {
      int base = 3 + IC2.random.nextInt(7) + super.statGrowth;
      int need = (this.crop().tier() - 1) * 4 + super.statGrowth + super.statGain + super.statResistance;
      if(need < 0) {
         need = 0;
      }

      int have = this.crop().weightInfluences(this, (float)this.getHumidity(), (float)this.getNutrients(), (float)this.getAirQuality()) * 5;
      if(have >= need) {
         base = base * (100 + (have - need)) / 100;
      } else {
         int neg = (need - have) * 4;
         if(neg > 100 && IC2.random.nextInt(32) > super.statResistance) {
            this.reset();
            base = 0;
         } else {
            base = base * (100 - neg) / 100;
            if(base < 0) {
               base = 0;
            }
         }
      }

      return base;
   }

   public void calcTrampling() {}

   public CropCard crop() {
      return CropCard.getCrop(super.id);
   }

   public int getSprite() {
      return super.id < 0?(!this.upgraded?0:1):this.crop().getSpriteIndex(this);
   }

   public void onEntityCollision(Entity entity) {
      if(super.id >= 0 && this.crop().onEntityCollision(this, entity)) {
         ;
      }

   }

   public void reset() {
      super.id = -1;
      super.size = 0;
      super.custumData = new short[16];
      this.dirty = true;
      super.statGain = 0;
      super.statResistance = 0;
      super.statGrowth = 0;
      this.nutrients = -1;
      this.airQuality = -1;
      this.humidity = -1;
      this.growthPoints = 0;
      this.upgraded = false;
      super.scanLevel = 0;
   }

   public void updateState() {
      this.dirty = true;
   }

   public String getScanned() {
      return super.scanLevel > 0 && super.id >= 0?(super.scanLevel >= 4?this.crop().name() + " - Gr: " + super.statGrowth + " Ga: " + super.statGain + " Re: " + super.statResistance:this.crop().name()):null;
   }

   public boolean isBlockBelow(Block block) {
      for(int i = 1; i < 4; ++i) {
         int id = super.worldObj.getBlockId(super.xCoord, super.yCoord - i, super.zCoord);
         if(id == 0) {
            return false;
         }

         if(Block.blocksList[id] == block) {
            return true;
         }
      }

      return false;
   }

   public ItemStack generateSeeds(short plant, byte growth, byte gain, byte resis, byte scan) {
      return ItemCropSeed.generateItemStackFromValues(plant, growth, gain, resis, scan);
   }

   public void addLocal(String s1, String s2) {
      IC2.platform.addLocalization(s1, s2);
   }

   public void onNetworkUpdate(String field) {
      this.dirty = true;
   }

}
