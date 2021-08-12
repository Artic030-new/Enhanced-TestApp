package ic2.core.item;

import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.item.BehaviorScrapboxDispense;
import ic2.core.item.ItemIC2;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.AbstractMap.SimpleEntry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

public class ItemScrapbox extends ItemIC2 {

   public static List dropList = new Vector();


   public ItemScrapbox(int id, int sprite) {
      super(id, sprite);
      BlockDispenser.dispenseBehaviorRegistry.putObject(this, new BehaviorScrapboxDispense());
   }

   public static void init() {
      if(IC2.suddenlyHoes) {
         addDrop(Item.hoeWood, 9001.0F);
      } else {
         addDrop(Item.hoeWood, 5.01F);
      }

      addDrop(Block.dirt, 5.0F);
      addDrop(Item.stick, 4.0F);
      addDrop((Block)Block.grass, 3.0F);
      addDrop(Block.gravel, 3.0F);
      addDrop(Block.netherrack, 2.0F);
      addDrop(Item.rottenFlesh, 2.0F);
      addDrop(Item.appleRed, 1.5F);
      addDrop(Item.bread, 1.5F);
      addDrop(Ic2Items.filledTinCan.getItem(), 1.5F);
      addDrop(Item.swordWood);
      addDrop(Item.shovelWood);
      addDrop(Item.pickaxeWood);
      addDrop(Block.slowSand);
      addDrop(Item.sign);
      addDrop(Item.leather);
      addDrop(Item.feather);
      addDrop(Item.bone);
      addDrop(Item.porkCooked, 0.9F);
      addDrop(Item.beefCooked, 0.9F);
      addDrop(Block.pumpkin, 0.9F);
      addDrop(Item.chickenCooked, 0.9F);
      addDrop(Item.minecartEmpty, 0.9F);
      addDrop(Item.redstone, 0.9F);
      addDrop(Ic2Items.rubber.getItem(), 0.8F);
      addDrop(Item.lightStoneDust, 0.8F);
      addDrop(Ic2Items.coalDust.getItem(), 0.8F);
      addDrop(Ic2Items.copperDust.getItem(), 0.8F);
      addDrop(Ic2Items.tinDust.getItem(), 0.8F);
      addDrop(Ic2Items.plantBall.getItem(), 0.7F);
      addDrop(Ic2Items.suBattery.getItem(), 0.7F);
      addDrop(Ic2Items.ironDust.getItem(), 0.7F);
      addDrop(Ic2Items.goldDust.getItem(), 0.7F);
      addDrop(Item.slimeBall, 0.6F);
      addDrop(Block.oreIron, 0.5F);
      addDrop(Item.helmetGold, 0.5F);
      addDrop(Block.oreGold, 0.5F);
      addDrop(Item.cake, 0.5F);
      addDrop(Item.diamond, 0.1F);
      addDrop(Item.emerald, 0.05F);
      ArrayList ores;
      if(Ic2Items.copperOre != null) {
         addDrop(Ic2Items.copperOre.getItem(), 0.7F);
      } else {
         ores = OreDictionary.getOres("oreCopper");
         if(!ores.isEmpty()) {
            addDrop(((ItemStack)ores.get(0)).copy(), 0.7F);
         }
      }

      if(Ic2Items.tinOre != null) {
         addDrop(Ic2Items.tinOre.getItem(), 0.7F);
      } else {
         ores = OreDictionary.getOres("oreTin");
         if(!ores.isEmpty()) {
            addDrop(((ItemStack)ores.get(0)).copy(), 0.7F);
         }
      }

   }

   public static void addDrop(Item item) {
      addDrop(new ItemStack(item), 1.0F);
   }

   public static void addDrop(Item item, float chance) {
      addDrop(new ItemStack(item), chance);
   }

   public static void addDrop(Block block) {
      addDrop(new ItemStack(block), 1.0F);
   }

   public static void addDrop(Block block, float chance) {
      addDrop(new ItemStack(block), chance);
   }

   public static void addDrop(ItemStack item) {
      addDrop(item, 1.0F);
   }

   public static void addDrop(ItemStack item, float chance) {
      dropList.add(new ItemScrapbox.Drop(item, chance));
   }

   public static ItemStack getDrop(World world) {
      if(!dropList.isEmpty()) {
         float dropChance = world.rand.nextFloat() * ((ItemScrapbox.Drop)dropList.get(dropList.size() - 1)).upperChanceBound;
         Iterator it = dropList.iterator();

         while(it.hasNext()) {
            ItemScrapbox.Drop drop = (ItemScrapbox.Drop)it.next();
            if(drop.upperChanceBound >= dropChance) {
               return drop.itemStack.copy();
            }
         }
      }

      return null;
   }

   public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer) {
      if(!entityplayer.capabilities.isCreativeMode) {
         --itemstack.stackSize;
      }

      ItemStack itemStack = getDrop(world);
      if(itemStack != null) {
         entityplayer.dropPlayerItem(itemStack);
      }

      return itemstack;
   }

   public static List getDropList() {
      Vector ret = new Vector();
      Iterator i$ = dropList.iterator();

      while(i$.hasNext()) {
         ItemScrapbox.Drop drop = (ItemScrapbox.Drop)i$.next();
         ret.add(new SimpleEntry(drop.itemStack, Float.valueOf(drop.upperChanceBound)));
      }

      return ret;
   }


   static class Drop {

      ItemStack itemStack;
      float upperChanceBound;


      Drop(ItemStack itemStack, float chance) {
         this.itemStack = itemStack;
         if(ItemScrapbox.dropList.isEmpty()) {
            this.upperChanceBound = chance;
         } else {
            this.upperChanceBound = ((ItemScrapbox.Drop)ItemScrapbox.dropList.get(ItemScrapbox.dropList.size() - 1)).upperChanceBound + chance;
         }

      }
   }
}
