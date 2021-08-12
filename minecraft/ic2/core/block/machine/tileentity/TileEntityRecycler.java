package ic2.core.block.machine.tileentity;

import ic2.api.Ic2Recipes;
import ic2.core.Ic2Items;
import ic2.core.block.machine.tileentity.TileEntityElectricMachine;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

public class TileEntityRecycler extends TileEntityElectricMachine {

   public static List blacklist = new Vector();


   public TileEntityRecycler() {
      super(3, 1, 45, 32);
   }

   public static void init(Configuration config) {
      addBlacklistItem(Block.thinGlass);
      addBlacklistItem(Item.stick);
      addBlacklistItem(Item.snowball);
      addBlacklistItem(Ic2Items.scaffold);
      Property prop = config.get("general", "recyclerBlacklist", getRecyclerBlacklistString());
      prop.comment = "List of blocks and items which should not be turned into scrap by the recycler. Comma separated, format is id-metadata";
      setRecyclerBlacklistFromString(prop.value);
   }

   public void operate() {
      if(this.canOperate()) {
         boolean itemBlacklisted = getIsItemBlacklisted(super.inventory[0]);
         --super.inventory[0].stackSize;
         if(super.inventory[0].stackSize <= 0) {
            super.inventory[0] = null;
         }

         if(super.worldObj.rand.nextInt(recycleChance()) == 0 && !itemBlacklisted) {
            if(super.inventory[2] == null) {
               super.inventory[2] = Ic2Items.scrap.copy();
            } else {
               ++super.inventory[2].stackSize;
            }
         }

      }
   }

   public boolean canOperate() {
      return super.inventory[0] == null?false:super.inventory[2] == null || super.inventory[2].isItemEqual(Ic2Items.scrap) && super.inventory[2].stackSize < Ic2Items.scrap.getMaxStackSize();
   }

   public ItemStack getResultFor(ItemStack itemstack, boolean adjustInput) {
      return null;
   }

   public String getInvName() {
      return "Recycler";
   }

   public String getGuiClassName(EntityPlayer entityPlayer) {
      return "block.machine.gui.GuiRecycler";
   }

   public static int recycleChance() {
      return 8;
   }

   public String getStartSoundFile() {
      return "Machines/RecyclerOp.ogg";
   }

   public String getInterruptSoundFile() {
      return "Machines/InterruptOne.ogg";
   }

   public float getWrenchDropRate() {
      return 0.85F;
   }

   public static void addBlacklistItem(Item item) {
      addBlacklistItem(new ItemStack(item));
   }

   public static void addBlacklistItem(Block block) {
      addBlacklistItem(new ItemStack(block));
   }

   public static void addBlacklistItem(ItemStack item) {
      blacklist.add(item);
   }

   public static boolean getIsItemBlacklisted(ItemStack itemStack) {
      Iterator i$ = blacklist.iterator();

      ItemStack blackItem;
      do {
         if(!i$.hasNext()) {
            return false;
         }

         blackItem = (ItemStack)i$.next();
      } while(!itemStack.isItemEqual(blackItem) && (blackItem.getItemDamage() != -1 || blackItem.itemID != itemStack.itemID));

      return true;
   }

   private static String getRecyclerBlacklistString() {
      StringBuilder ret = new StringBuilder();
      boolean first = true;
      Iterator i$ = Ic2Recipes.getRecyclerBlacklist().iterator();

      while(i$.hasNext()) {
         ItemStack entry = (ItemStack)i$.next();
         if(entry != null) {
            if(first) {
               first = false;
            } else {
               ret.append(", ");
            }

            ret.append(entry.itemID);
            if(entry.getItemDamage() != 0) {
               ret.append("-");
               ret.append(entry.getItemDamage());
            }
         }
      }

      return ret.toString();
   }

   private static void setRecyclerBlacklistFromString(String str) {
      String[] strParts = str.trim().split("\\s*,\\s*");
      String[] arr$ = strParts;
      int len$ = strParts.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         String strPart = arr$[i$];
         String[] idMeta = strPart.split("\\s*-\\s*");
         if(idMeta[0].length() != 0) {
            int blockId = Integer.parseInt(idMeta[0]);
            int metaData = -1;
            if(idMeta.length == 2) {
               metaData = Integer.parseInt(idMeta[1]);
            }

            ItemStack is = new ItemStack(blockId, 1, metaData);
            Ic2Recipes.addRecyclerBlacklistItem(is);
         }
      }

   }

}
