package ic2.core.item;

import ic2.core.Ic2Items;
import ic2.core.item.ItemIC2;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class ItemBooze extends ItemIC2 {
	
	/* Перевод by Artic030  изм. 19/07-2020*/

   public String[] solidRatio = new String[]{"Водянистый ", "Светлый ", "Лёгкий ", "", "Крепкий ", "Тёмный ", "Сытный ", "X"};
   public String[] hopsRatio = new String[]{"Простой ", "Безалкогольный ", "Традиционный ", "", "Тёмный ", "Полный ", "Чёрный ", "X"};
   public String[] timeRatioNames = new String[]{"Похлёбка", "Коктейль", "Пиво", "Эль", "Кровь Дракона", "Чёрная Дрянь"};
   public  String[] ic2BeerPrefixes = {"ый", "ое", "ая", "ий"};
   public int[] baseDuration = new int[]{300, 600, 900, 1200, 1600, 2000, 2400};
   public float[] baseIntensity = new float[]{0.4F, 0.75F, 1.0F, 1.5F, 2.0F};
   public static float rumStackability = 2.0F;
   public static int rumDuration = 600;
  
   public ItemBooze(int id, int index) {
      super(id, index);
      this.setMaxStackSize(1);
   }

   public int getIconFromDamage(int meta) {
      int type = getTypeOfValue(meta);
      return type == 1?super.iconIndex + getTimeRatioOfBeerValue(meta):(type == 2?super.iconIndex + 8:super.iconIndex);
   }

   public String getItemDisplayName(ItemStack itemstack) {
	   
      int meta = itemstack.getItemDamage();
      int type = getTypeOfValue(meta);
      String uy = ic2BeerPrefixes[0];
      String oe = ic2BeerPrefixes[1];
      String aya = ic2BeerPrefixes[2];
      String iy = ic2BeerPrefixes[3];

      if(type == 1) {
          int timeRatio = Math.min(getTimeRatioOfBeerValue(meta), this.timeRatioNames.length - 1);
          return timeRatio == this.timeRatioNames.length - 1?this.timeRatioNames[timeRatio] 
          : this.timeRatioNames[timeRatio].equals(this.timeRatioNames[2])? 
         		 this.solidRatio[getSolidRatioOfBeerValue(meta)].replaceAll(uy, oe).replaceAll(iy, oe) + this.hopsRatio[getHopsRatioOfBeerValue(meta)].replaceAll(uy, oe).replaceAll(iy, oe) + this.timeRatioNames[timeRatio]
          : this.timeRatioNames[timeRatio].equals(this.timeRatioNames[0])? 
         		 this.solidRatio[getSolidRatioOfBeerValue(meta)].replaceAll(uy, aya).replaceAll(iy, aya) + this.hopsRatio[getHopsRatioOfBeerValue(meta)].replaceAll(uy, aya) + this.timeRatioNames[timeRatio]
          :this.solidRatio[getSolidRatioOfBeerValue(meta)] + this.hopsRatio[getHopsRatioOfBeerValue(meta)] + this.timeRatioNames[timeRatio] ;
       }    else {
           return type == 2?"Ром":"Нулёвка";
       }
      }

   public ItemStack onFoodEaten(ItemStack itemstack, World world, EntityPlayer player) {
      int meta = itemstack.getItemDamage();
      int type = getTypeOfValue(meta);
      if(type == 0) {
         return new ItemStack(Ic2Items.mugEmpty.getItem());
      } else {
         int level;
         if(type == 1) {
            if(getTimeRatioOfBeerValue(meta) == 5) {
               return this.drinkBlackStuff(player);
            }

            int def = getSolidRatioOfBeerValue(meta);
            level = getHopsRatioOfBeerValue(meta);
            int duration = this.baseDuration[def];
            float intensity = this.baseIntensity[getTimeRatioOfBeerValue(meta)];
            player.getFoodStats().addStats(6 - level, (float)def * 0.15F);
            int max = (int)(intensity * (float)level * 0.5F);
            PotionEffect slow = player.getActivePotionEffect(Potion.digSlowdown);
            int level1 = -1;
            if(slow != null) {
               level1 = slow.getAmplifier();
            }

            this.amplifyEffect(player, Potion.digSlowdown, max, intensity, duration);
            if(level1 > -1) {
               this.amplifyEffect(player, Potion.damageBoost, max, intensity, duration);
               if(level1 > 0) {
                  this.amplifyEffect(player, Potion.moveSlowdown, max / 2, intensity, duration);
                  if(level1 > 1) {
                     this.amplifyEffect(player, Potion.resistance, max - 1, intensity, duration);
                     if(level1 > 2) {
                        this.amplifyEffect(player, Potion.confusion, 0, intensity, duration);
                        if(level1 > 3) {
                           player.addPotionEffect(new PotionEffect(Potion.harm.id, 1, player.worldObj.rand.nextInt(3)));
                        }
                     }
                  }
               }
            }
         }

         if(type == 2) {
            if(getProgressOfRumValue(meta) < 100) {
               this.drinkBlackStuff(player);
            } else {
               this.amplifyEffect(player, Potion.fireResistance, 0, rumStackability, rumDuration);
               PotionEffect def1 = player.getActivePotionEffect(Potion.resistance);
               level = -1;
               if(def1 != null) {
                  level = def1.getAmplifier();
               }

               this.amplifyEffect(player, Potion.resistance, 2, rumStackability, rumDuration);
               if(level >= 0) {
                  this.amplifyEffect(player, Potion.blindness, 0, rumStackability, rumDuration);
               }

               if(level >= 1) {
                  this.amplifyEffect(player, Potion.confusion, 0, rumStackability, rumDuration);
               }
            }
         }

         return new ItemStack(Ic2Items.mugEmpty.getItem());
      }
   }

   public void amplifyEffect(EntityPlayer player, Potion potion, int max, float intensity, int duration) {
      PotionEffect eff = player.getActivePotionEffect(potion);
      if(eff == null) {
         player.addPotionEffect(new PotionEffect(potion.id, duration, 0));
      } else {
         int newdur = eff.getDuration();
         int maxnewdur = (int)((float)duration * (1.0F + intensity * 2.0F) - (float)newdur) / 2;
         if(maxnewdur < 0) {
            maxnewdur = 0;
         }

         if(maxnewdur < duration) {
            duration = maxnewdur;
         }

         newdur += duration;
         int newamp = eff.getAmplifier();
         if(newamp < max) {
            ++newamp;
         }

         player.addPotionEffect(new PotionEffect(potion.id, newdur, newamp));
      }

   }

   public ItemStack drinkBlackStuff(EntityPlayer player) {
      switch(player.worldObj.rand.nextInt(6)) {
      case 1:
         player.addPotionEffect(new PotionEffect(Potion.confusion.id, 1200, 0));
         break;
      case 2:
         player.addPotionEffect(new PotionEffect(Potion.blindness.id, 2400, 0));
         break;
      case 3:
         player.addPotionEffect(new PotionEffect(Potion.poison.id, 2400, 0));
         break;
      case 4:
         player.addPotionEffect(new PotionEffect(Potion.poison.id, 200, 2));
         break;
      case 5:
         player.addPotionEffect(new PotionEffect(Potion.harm.id, 1, player.worldObj.rand.nextInt(4)));
      }

      return new ItemStack(Ic2Items.mugEmpty.getItem());
   }

   public int getMaxItemUseDuration(ItemStack itemstack) {
      return 32;
   }

   public EnumAction getItemUseAction(ItemStack itemstack) {
      return EnumAction.drink;
   }

   public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer player) {
      player.setItemInUse(itemstack, this.getMaxItemUseDuration(itemstack));
      return itemstack;
   }

   public static int getTypeOfValue(int value) {
      return skipGetOfValue(value, 0, 2);
   }

   public static int getAmountOfValue(int value) {
      return getTypeOfValue(value) == 0?0:skipGetOfValue(value, 2, 5) + 1;
   }

   public static int getSolidRatioOfBeerValue(int value) {
      return skipGetOfValue(value, 7, 3);
   }

   public static int getHopsRatioOfBeerValue(int value) {
      return skipGetOfValue(value, 10, 3);
   }

   public static int getTimeRatioOfBeerValue(int value) {
      return skipGetOfValue(value, 13, 3);
   }

   public static int getProgressOfRumValue(int value) {
      return skipGetOfValue(value, 7, 7);
   }

   private static int skipGetOfValue(int value, int bitshift, int take) {
      value >>= bitshift;
      take = (int)Math.pow(2.0D, (double)take) - 1;
      return value & take;
   }

}
