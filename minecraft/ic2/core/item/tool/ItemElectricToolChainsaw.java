package ic2.core.item.tool;

import ic2.core.IC2;
import ic2.core.IHitSoundOverride;
import ic2.core.Ic2Items;
import ic2.core.audio.AudioSource;
import ic2.core.audio.PositionSpec;
import ic2.core.item.ElectricItem;
import ic2.core.item.tool.ItemElectricTool;
import java.util.ArrayList;
import java.util.Iterator;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.EntityInteractEvent;

public class ItemElectricToolChainsaw extends ItemElectricTool implements IHitSoundOverride {

   public static boolean wasEquipped = false;
   public static AudioSource audioSource;


   public ItemElectricToolChainsaw(int id, int sprite) {
      super(id, sprite, EnumToolMaterial.IRON, 50);
      super.maxCharge = 10000;
      super.transferLimit = 100;
      super.tier = 1;
      super.efficiencyOnProperMaterial = 12.0F;
      super.co = 1;
      MinecraftForge.EVENT_BUS.register(this);
   }

   public void init() {
      super.mineableBlocks.add(Block.planks);
      super.mineableBlocks.add(Block.bookShelf);
      super.mineableBlocks.add(Block.wood);
      super.mineableBlocks.add(Block.chest);
      super.mineableBlocks.add(Block.leaves);
      super.mineableBlocks.add(Block.web);
      super.mineableBlocks.add(Block.blocksList[Ic2Items.crop.itemID]);
      if(Ic2Items.rubberLeaves != null) {
         super.mineableBlocks.add(Block.blocksList[Ic2Items.rubberLeaves.itemID]);
      }

   }

   public boolean hitEntity(ItemStack itemstack, EntityLiving entityliving, EntityLiving entityliving1) {
      if(ElectricItem.use(itemstack, super.operationEnergyCost, (EntityPlayer)entityliving1) && ElectricItem.use(itemstack, super.operationEnergyCost, (EntityPlayer)entityliving1)) {
         entityliving.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer)entityliving1), 10);
      } else {
         entityliving.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer)entityliving1), 1);
      }

      if(entityliving instanceof EntityCreeper && entityliving.getHealth() <= 0) {
         IC2.achievements.issueAchievement((EntityPlayer)entityliving1, "killCreeperChainsaw");
      }

      return false;
   }

   public boolean canHarvestBlock(Block block) {
      return block.blockMaterial == Material.wood?true:super.canHarvestBlock(block);
   }

   @ForgeSubscribe
   public void onEntityInteract(EntityInteractEvent event) {
      Entity entity = event.target;
      if(!entity.worldObj.isRemote) {
         EntityPlayer player = event.entityPlayer;
         ItemStack itemstack = player.inventory.mainInventory[player.inventory.currentItem];
         if(itemstack != null && itemstack.itemID == super.itemID && entity instanceof IShearable && ElectricItem.use(itemstack, super.operationEnergyCost * 2, player)) {
            IShearable target = (IShearable)entity;
            if(target.isShearable(itemstack, entity.worldObj, (int)entity.posX, (int)entity.posY, (int)entity.posZ)) {
               ArrayList drops = target.onSheared(itemstack, entity.worldObj, (int)entity.posX, (int)entity.posY, (int)entity.posZ, EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, itemstack));

               EntityItem ent;
               for(Iterator i$ = drops.iterator(); i$.hasNext(); ent.motionZ += (double)((Item.itemRand.nextFloat() - Item.itemRand.nextFloat()) * 0.1F)) {
                  ItemStack stack = (ItemStack)i$.next();
                  ent = entity.entityDropItem(stack, 1.0F);
                  ent.motionY += (double)(Item.itemRand.nextFloat() * 0.05F);
                  ent.motionX += (double)((Item.itemRand.nextFloat() - Item.itemRand.nextFloat()) * 0.1F);
               }
            }
         }

      }
   }

   public boolean onBlockStartBreak(ItemStack itemstack, int X, int Y, int Z, EntityPlayer player) {
      if(player.worldObj.isRemote) {
         return false;
      } else {
         int id = player.worldObj.getBlockId(X, Y, Z);
         if(Block.blocksList[id] != null && Block.blocksList[id] instanceof IShearable) {
            IShearable target = (IShearable)Block.blocksList[id];
            if(target.isShearable(itemstack, player.worldObj, X, Y, Z) && ElectricItem.use(itemstack, super.operationEnergyCost, player) && ElectricItem.use(itemstack, super.operationEnergyCost, player)) {
               ArrayList drops = target.onSheared(itemstack, player.worldObj, X, Y, Z, EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, itemstack));
               Iterator i$ = drops.iterator();

               while(i$.hasNext()) {
                  ItemStack stack = (ItemStack)i$.next();
                  float f = 0.7F;
                  double d = (double)(Item.itemRand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
                  double d1 = (double)(Item.itemRand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
                  double d2 = (double)(Item.itemRand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
                  EntityItem entityitem = new EntityItem(player.worldObj, (double)X + d, (double)Y + d1, (double)Z + d2, stack);
                  entityitem.delayBeforeCanPickup = 10;
                  player.worldObj.spawnEntityInWorld(entityitem);
               }

               player.addStat(StatList.mineBlockStatArray[id], 1);
            }
         }

         return false;
      }
   }

   public void onUpdate(ItemStack itemstack, World world, Entity entity, int i, boolean flag) {
      boolean isEquipped = flag && entity instanceof EntityLiving;
      if(IC2.platform.isRendering()) {
         if(isEquipped && !wasEquipped) {
            if(audioSource == null) {
               audioSource = IC2.audioManager.createSource(entity, PositionSpec.Hand, "Tools/Chainsaw/ChainsawIdle.ogg", true, false, IC2.audioManager.defaultVolume);
            }

            if(audioSource != null) {
               audioSource.play();
            }
         } else if(!isEquipped && audioSource != null) {
            audioSource.stop();
            audioSource.remove();
            audioSource = null;
            if(entity instanceof EntityLiving) {
               IC2.audioManager.playOnce(entity, PositionSpec.Hand, "Tools/Chainsaw/ChainsawStop.ogg", true, IC2.audioManager.defaultVolume);
            }
         } else if(audioSource != null) {
            audioSource.updatePosition();
         }

         wasEquipped = isEquipped;
      }

   }

   public String getHitSoundForBlock(int x, int y, int z) {
      String[] soundEffects = new String[]{"Tools/Chainsaw/ChainsawUseOne.ogg", "Tools/Chainsaw/ChainsawUseTwo.ogg"};
      return soundEffects[Item.itemRand.nextInt(soundEffects.length)];
   }

}
