package ic2.api;

import ic2.api.BaseSeed;
import ic2.api.TECrop;
import java.util.HashMap;
import java.util.Iterator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public abstract class CropCard {

   private static final CropCard[] cropCardList = new CropCard[256];
   public static TECrop nameReference;
   private static HashMap baseseeds = new HashMap();


   public abstract String name();

   public String discoveredBy() {
      return "Alblaka";
   }

   public String desc(int i) {
      String[] att = this.attributes();
      if(att != null && att.length != 0) {
         String s;
         if(i == 0) {
            s = att[0];
            if(att.length >= 2) {
               s = s + ", " + att[1];
               if(att.length >= 3) {
                  s = s + ",";
               }
            }

            return s;
         } else if(att.length < 3) {
            return "";
         } else {
            s = att[2];
            if(att.length >= 4) {
               s = s + ", " + att[3];
            }

            return s;
         }
      } else {
         return "";
      }
   }

   public abstract int tier();

   public abstract int stat(int var1);

   public abstract String[] attributes();

   public abstract int getSpriteIndex(TECrop var1);

   public String getTextureFile() {
      return "/ic2/sprites/crops_0.png";
   }

   public int growthDuration(TECrop crop) {
      return this.tier() * 200;
   }

   public abstract boolean canGrow(TECrop var1);

   public int weightInfluences(TECrop crop, float humidity, float nutrients, float air) {
      return (int)(humidity + nutrients + air);
   }

   public boolean canCross(TECrop crop) {
      return crop.size >= 3;
   }

   public boolean rightclick(TECrop crop, EntityPlayer player) {
      return crop.harvest(true);
   }

   public abstract boolean canBeHarvested(TECrop var1);

   public float dropGainChance() {
      float base = 1.0F;

      for(int i = 0; i < this.tier(); ++i) {
         base = (float)((double)base * 0.95D);
      }

      return base;
   }

   public abstract ItemStack getGain(TECrop var1);

   public byte getSizeAfterHarvest(TECrop crop) {
      return (byte)1;
   }

   public boolean leftclick(TECrop crop, EntityPlayer player) {
      return crop.pick(true);
   }

   public float dropSeedChance(TECrop crop) {
      if(crop.size == 1) {
         return 0.0F;
      } else {
         float base = 0.5F;
         if(crop.size == 2) {
            base /= 2.0F;
         }

         for(int i = 0; i < this.tier(); ++i) {
            base = (float)((double)base * 0.8D);
         }

         return base;
      }
   }

   public ItemStack getSeeds(TECrop crop) {
      return crop.generateSeeds(crop.id, crop.statGrowth, crop.statGain, crop.statResistance, crop.scanLevel);
   }

   public void onNeighbourChange(TECrop crop) {}

   public boolean emitRedstone(TECrop crop) {
      return false;
   }

   public void onBlockDestroyed(TECrop crop) {}

   public int getEmittedLight(TECrop crop) {
      return 0;
   }

   public boolean onEntityCollision(TECrop crop, Entity entity) {
      return entity instanceof EntityLiving?((EntityLiving)entity).isSprinting():false;
   }

   public void tick(TECrop crop) {}

   public boolean isWeed(TECrop crop) {
      return crop.size >= 2 && (crop.id == 0 || crop.statGrowth >= 24);
   }

   public final int getId() {
      for(int i = 0; i < cropCardList.length; ++i) {
         if(this == cropCardList[i]) {
            return i;
         }
      }

      return -1;
   }

   public static int cropCardListLength() {
      return cropCardList.length;
   }

   public static final CropCard getCrop(int id) {
      if(id >= 0 && id < cropCardList.length) {
         if(cropCardList[id] == null) {
            System.out.println("[IndustrialCraft] Something tried to access non-existant cropID #" + id + "!!!");
            return cropCardList[0];
         } else {
            return cropCardList[id];
         }
      } else {
         return cropCardList[0];
      }
   }

   public static final boolean idExists(int id) {
      return id >= 0 && id < cropCardList.length && cropCardList[id] != null;
   }

   public static final short registerCrop(CropCard crop) {
      for(short x = 0; x < cropCardList.length; ++x) {
         if(cropCardList[x] == null) {
            cropCardList[x] = crop;
            nameReference.addLocal("item.cropSeed" + x + ".name", crop.name() + " Seeds");
            return x;
         }
      }

      return (short)-1;
   }

   public static final boolean registerCrop(CropCard crop, int i) {
      if(i >= 0 && i < cropCardList.length) {
         if(cropCardList[i] == null) {
            cropCardList[i] = crop;
            nameReference.addLocal("item.cropSeed" + i + ".name", crop.name() + " Seeds");
            return true;
         } else {
            System.out.println("[IndustrialCraft] Cannot add crop:" + crop.name() + " on ID #" + i + ", slot already occupied by crop:" + cropCardList[i].name());
            return false;
         }
      } else {
         return false;
      }
   }

   public static boolean registerBaseSeed(ItemStack stack, int id, int size, int growth, int gain, int resistance) {
      Iterator i$ = baseseeds.keySet().iterator();

      ItemStack key;
      do {
         if(!i$.hasNext()) {
            baseseeds.put(stack, new BaseSeed(id, size, growth, gain, resistance, stack.stackSize));
            return true;
         }

         key = (ItemStack)i$.next();
      } while(key.itemID != stack.itemID || key.getItemDamage() != stack.getItemDamage());

      return false;
   }

   public static BaseSeed getBaseSeed(ItemStack stack) {
      if(stack == null) {
         return null;
      } else {
         Iterator i$ = baseseeds.keySet().iterator();

         ItemStack key;
         do {
            do {
               if(!i$.hasNext()) {
                  return null;
               }

               key = (ItemStack)i$.next();
            } while(key.itemID != stack.itemID);
         } while(key.getItemDamage() != -1 && key.getItemDamage() != stack.getItemDamage());

         return (BaseSeed)baseseeds.get(key);
      }
   }

}
