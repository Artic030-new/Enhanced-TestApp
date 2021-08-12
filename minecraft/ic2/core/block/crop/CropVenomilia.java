package ic2.core.block.crop;

import ic2.api.CropCard;
import ic2.api.TECrop;
import ic2.core.IC2;
import ic2.core.Ic2Items;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;

public class CropVenomilia extends CropCard {

   public String name() {
      return "Venomilia";
   }

   public String discoveredBy() {
      return "raGan";
   }

   public int tier() {
      return 3;
   }

   public int stat(int n) {
      switch(n) {
      case 0:
         return 3;
      case 1:
         return 1;
      case 2:
         return 3;
      case 3:
         return 3;
      case 4:
         return 3;
      default:
         return 0;
      }
   }

   public String[] attributes() {
      return new String[]{"Purple", "Flower", "Tulip", "Poison"};
   }

   public int getSpriteIndex(TECrop crop) {
      return crop.size <= 3?crop.size + 11:(crop.size == 4?23:25);
   }

   public boolean canGrow(TECrop crop) {
      return crop.size <= 4 && crop.getLightLevel() >= 12 || crop.size == 5;
   }

   public boolean canBeHarvested(TECrop crop) {
      return crop.size >= 4;
   }

   public ItemStack getGain(TECrop crop) {
      return crop.size == 5?new ItemStack(Ic2Items.grinPowder.getItem(), 1):(crop.size >= 4?new ItemStack(Item.dyePowder, 1, 5):null);
   }

   public byte getSizeAfterHarvest(TECrop crop) {
      return (byte)3;
   }

   public int growthDuration(TECrop crop) {
      return crop.size >= 3?600:400;
   }

   public boolean rightclick(TECrop crop, EntityPlayer player) {
      if(!player.isSneaking()) {
         this.onEntityCollision(crop, player);
      }

      return crop.harvest(true);
   }

   public boolean leftclick(TECrop crop, EntityPlayer player) {
      if(!player.isSneaking()) {
         this.onEntityCollision(crop, player);
      }

      return crop.pick(true);
   }

   public boolean onEntityCollision(TECrop crop, Entity entity) {
      if(crop.size == 5 && entity instanceof EntityLiving) {
         if(entity instanceof EntityPlayer && ((EntityPlayer)entity).isSneaking() && IC2.random.nextInt(50) != 0) {
            return super.onEntityCollision(crop, entity);
         }

         ((EntityLiving)entity).addPotionEffect(new PotionEffect(19, (IC2.random.nextInt(10) + 5) * 20, 0));
         crop.size = 4;
         crop.updateState();
      }

      return super.onEntityCollision(crop, entity);
   }

   public boolean isWeed(TECrop crop) {
      return crop.size == 5 && crop.statGrowth >= 8;
   }
}
