package ic2.core.item.tool;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.item.ElectricItem;
import ic2.core.item.armor.ItemArmorNanoSuit;
import ic2.core.item.armor.ItemArmorQuantumSuit;
import ic2.core.item.tool.ItemElectricTool;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

public class ItemNanoSaber extends ItemElectricTool {

   public boolean active;
   public int soundTicker = 0;
   public static int ticker = 0;
   public static Random shinyrand = new Random();


   public ItemNanoSaber(int id, int sprite, boolean a) {
      super(id, sprite, EnumToolMaterial.IRON, 10);
      super.maxCharge = '\u9c40';
      super.transferLimit = 128;
      super.tier = 2;
      this.active = a;
   }

   public void init() {
      super.mineableBlocks.add(Block.web);
   }

   public float getStrVsBlock(ItemStack itemstack, Block block) {
      if(this.active) {
         ++this.soundTicker;
         if(this.soundTicker % 4 == 0) {
            IC2.platform.playSoundSp(this.getRandomSwingSound(), 1.0F, 1.0F);
         }

         return 4.0F;
      } else {
         return 1.0F;
      }
   }

   public boolean hitEntity(ItemStack itemstack, EntityLiving entityliving, EntityLiving entityliving1) {
      if(!this.active) {
         return true;
      } else {
         if(IC2.platform.isSimulating() && (!(entityliving1 instanceof EntityPlayer) || !MinecraftServer.getServer().isPVPEnabled())) {
            EntityPlayer player = null;
            if(entityliving1 instanceof EntityPlayer) {
               player = (EntityPlayer)entityliving1;
            }

            if(entityliving instanceof EntityPlayer) {
               EntityPlayer enemy = (EntityPlayer)entityliving;

               for(int i = 0; i < 4; ++i) {
                  if(enemy.inventory.armorInventory[i] != null && enemy.inventory.armorInventory[i].getItem() instanceof ItemArmorNanoSuit) {
                     int amount = enemy.inventory.armorInventory[i].getItem() instanceof ItemArmorQuantumSuit?30000:4800;
                     ElectricItem.discharge(enemy.inventory.armorInventory[i], amount, super.tier, true, false);
                     if(!ElectricItem.canUse(enemy.inventory.armorInventory[i], 1)) {
                        enemy.inventory.armorInventory[i] = null;
                     }

                     drainSaber(itemstack, 2, player);
                  }
               }
            }

            drainSaber(itemstack, 5, player);
         }

         if(IC2.platform.isRendering()) {
            IC2.platform.playSoundSp(this.getRandomSwingSound(), 1.0F, 1.0F);
         }

         return true;
      }
   }

   public String getRandomSwingSound() {
      switch(IC2.random.nextInt(3)) {
      case 1:
         return "nanosabreSwingOne";
      case 2:
         return "nanosabreSwingTwo";
      default:
         return "nanosabreSwing";
      }
   }

   public boolean onBlockStartBreak(ItemStack itemstack, int i, int j, int k, EntityPlayer player) {
      if(this.active) {
         drainSaber(itemstack, 10, player);
      }

      return false;
   }

   public int getDamageVsEntity(Entity entity) {
      return this.active?20:4;
   }

   public boolean isFull3D() {
      return true;
   }

   public boolean canHarvestBlock(Block block) {
      return block.blockID == Block.web.blockID;
   }

   public static void drainSaber(ItemStack saber, int damage, EntityPlayer player) {
      if(!ElectricItem.use(saber, damage * 8, player)) {
         saber.itemID = Ic2Items.nanoSaber.itemID;
      }

   }

   public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer) {
      if(!IC2.platform.isSimulating()) {
         return itemstack;
      } else {
         if(this.active) {
            itemstack.itemID = Ic2Items.nanoSaber.itemID;
         } else if(ElectricItem.canUse(itemstack, 16)) {
            itemstack.itemID = Ic2Items.enabledNanoSaber.itemID;
            world.playSoundAtEntity(entityplayer, "nanosabrePower", 1.0F, 1.0F);
         }

         return itemstack;
      }
   }

   public static void timedLoss(EntityPlayer player) {
      ++ticker;
      if(ticker % 16 == 0) {
         ItemStack[] inv = player.inventory.mainInventory;
         int i;
         if(ticker % 64 == 0) {
            for(i = 9; i < inv.length; ++i) {
               if(inv[i] != null && inv[i].itemID == Ic2Items.enabledNanoSaber.itemID) {
                  drainSaber(inv[i], 64, player);
               }
            }
         }

         for(i = 0; i < 9; ++i) {
            if(inv[i] != null && inv[i].itemID == Ic2Items.enabledNanoSaber.itemID) {
               drainSaber(inv[i], 16, player);
            }
         }
      }

   }

   public int getIconFromDamage(int i) {
      return this.active && shinyrand.nextBoolean()?super.iconIndex + 1:super.iconIndex;
   }

   @SideOnly(Side.CLIENT)
   public EnumRarity getRarity(ItemStack stack) {
      return EnumRarity.uncommon;
   }

   @SideOnly(Side.CLIENT)
   public void getSubItems(int i, CreativeTabs tabs, List itemList) {
      if(!this.active) {
         super.getSubItems(i, tabs, itemList);
      }

   }

}
