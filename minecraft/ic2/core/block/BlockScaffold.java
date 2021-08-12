package ic2.core.block;

import cpw.mods.fml.common.registry.GameRegistry;
import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.block.BlockTex;
import ic2.core.item.block.ItemBlockRare;
import java.util.ArrayList;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class BlockScaffold extends BlockTex {

   public static int standardStrength = 2;
   public static int standardIronStrength = 5;
   public static int reinforcedStrength = 5;
   public static int reinforcedIronStrength = 12;
   public static int tickDelay = 1;
   public Material material;


   public BlockScaffold(int id, Material material) {
      super(id, material == Material.iron?132:116, material);
      this.setCreativeTab(IC2.tabIC2);
      this.material = material;
      if(material == Material.wood) {
         this.setHardness(0.5F);
         this.setResistance(0.2F);
         this.setBlockName("blockScaffold");
         this.setStepSound(Block.soundWoodFootstep);
         Ic2Items.scaffold = new ItemStack(this);
         GameRegistry.registerBlock(this, ItemBlockRare.class, "scaffold");
      } else if(material == Material.iron) {
         this.setHardness(0.8F);
         this.setResistance(10.0F);
         this.setBlockName("blockIronScaffold");
         this.setStepSound(Block.soundMetalFootstep);
         Ic2Items.ironScaffold = new ItemStack(this);
         GameRegistry.registerBlock(this, ItemBlockRare.class, "ironScaffold");
      }

   }

   public int getStandardStrength() {
      return this.material == Material.iron?standardIronStrength:standardStrength;
   }

   public int getReinforcedStrength() {
      return this.material == Material.iron?reinforcedIronStrength:reinforcedStrength;
   }

   public int getBlockTexture(IBlockAccess iblockaccess, int i, int j, int k, int side) {
      int meta = iblockaccess.getBlockMetadata(i, j, k);
      return side < 2?super.blockIndexInTexture + 1:(meta == this.getReinforcedStrength()?super.blockIndexInTexture + 2:super.blockIndexInTexture);
   }

   public int getBlockTextureFromSideAndMetadata(int side, int meta) {
      return side < 2?super.blockIndexInTexture + 1:super.blockIndexInTexture;
   }

   public boolean isOpaqueCube() {
      return false;
   }

   public boolean isBlockNormalCube(World world, int i, int j, int k) {
      return false;
   }

   public void onEntityCollidedWithBlock(World world, int i, int j, int k, Entity entity) {
      if(entity instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)entity;
         player.fallDistance = 0.0F;
         if(player.motionY < -0.15D) {
            player.motionY = -0.15D;
         }

         if(IC2.keyboard.isForwardKeyDown(player) && player.motionY < 0.2D) {
            player.motionY = 0.2D;
         }
      }

   }

   public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int i, int j, int k) {
      float factor = 1.0F;
      float f = factor / 16.0F;
      return AxisAlignedBB.getBoundingBox((double)((float)i + f), (double)j, (double)((float)k + f), (double)((float)i + factor - f), (double)((float)j + factor), (double)((float)k + factor - f));
   }

   public boolean isBlockSolidOnSide(World world, int i, int j, int k, ForgeDirection side) {
      return side == ForgeDirection.DOWN || side == ForgeDirection.UP;
   }

   public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int i, int j, int k) {
      return AxisAlignedBB.getBoundingBox((double)i, (double)j, (double)k, (double)(i + 1), (double)(j + 1), (double)(k + 1));
   }

   public ArrayList getBlockDropped(World world, int i, int j, int k, int meta, int fortune) {
      ArrayList tr = new ArrayList();
      tr.add(new ItemStack(this, 1));
      if(meta == this.getReinforcedStrength()) {
         if(this.material == Material.iron) {
            tr.add(new ItemStack(Ic2Items.ironFence.getItem(), 1));
         }

         if(this.material == Material.wood) {
            tr.add(new ItemStack(Item.stick, 2));
         }
      }

      return tr;
   }

   public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer entityplayer, int side, float a, float b, float c) {
      if(entityplayer.isSneaking()) {
         return false;
      } else {
         ItemStack sticks = entityplayer.inventory.getCurrentItem();
         if(sticks != null && (this.material != Material.wood || sticks.itemID == Item.stick.itemID && sticks.stackSize >= 2) && (this.material != Material.iron || sticks.itemID == Ic2Items.ironFence.itemID)) {
            if(world.getBlockMetadata(i, j, k) != this.getReinforcedStrength() && this.isPillar(world, i, j, k)) {
               if(this.material == Material.wood) {
                  sticks.stackSize -= 2;
               } else {
                  --sticks.stackSize;
               }

               if(entityplayer.getCurrentEquippedItem().stackSize <= 0) {
                  entityplayer.inventory.mainInventory[entityplayer.inventory.currentItem] = null;
               }

               world.setBlockMetadataWithNotify(i, j, k, this.getReinforcedStrength());
               world.markBlockRangeForRenderUpdate(i, j, k, i, j, k);
               IC2.network.announceBlockUpdate(world, i, j, k);
               return true;
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   public void onBlockClicked(World world, int i, int j, int k, EntityPlayer entityplayer) {
      if(entityplayer.getCurrentEquippedItem() != null && entityplayer.getCurrentEquippedItem().itemID == super.blockID) {
         while(world.getBlockId(i, j, k) == super.blockID) {
            ++j;
         }

         if(this.canPlaceBlockAt(world, i, j, k) && j < IC2.getWorldHeight(world)) {
            world.setBlockWithNotify(i, j, k, super.blockID);
            this.onPostBlockPlaced(world, i, j, k, 0);
            if(!entityplayer.capabilities.isCreativeMode) {
               --entityplayer.getCurrentEquippedItem().stackSize;
               if(entityplayer.getCurrentEquippedItem().stackSize <= 0) {
                  entityplayer.inventory.mainInventory[entityplayer.inventory.currentItem] = null;
               }
            }
         }
      }

   }

   public boolean canPlaceBlockAt(World world, int i, int j, int k) {
      return this.getStrengthFrom(world, i, j, k) <= -1?false:super.canPlaceBlockAt(world, i, j, k);
   }

   public boolean isPillar(World world, int i, int j, int k) {
      while(world.getBlockId(i, j, k) == super.blockID) {
         --j;
      }

      return world.isBlockNormalCube(i, j, k);
   }

   public void onNeighborBlockChange(World world, int i, int j, int k, int l) {
      this.updateSupportStatus(world, i, j, k);
   }

   public void onPostBlockPlaced(World world, int i, int j, int k, int l) {
      this.updateTick(world, i, j, k, (Random)null);
   }

   public void updateTick(World world, int i, int j, int k, Random random) {
      int ownStrength = world.getBlockMetadata(i, j, k);
      if(ownStrength >= this.getReinforcedStrength()) {
         if(!this.isPillar(world, i, j, k)) {
            ownStrength = this.getStrengthFrom(world, i, j, k);
            ItemStack drop = new ItemStack(Item.stick, 2);
            if(this.material == Material.iron) {
               drop = new ItemStack(Ic2Items.ironFence.getItem());
            }

            this.dropBlockAsItem_do(world, i, j, k, drop);
         }
      } else {
         ownStrength = this.getStrengthFrom(world, i, j, k);
      }

      if(ownStrength <= -1) {
         world.setBlockWithNotify(i, j, k, 0);
         this.dropBlockAsItem_do(world, i, j, k, new ItemStack(this));
      } else if(ownStrength != world.getBlockMetadata(i, j, k)) {
         world.setBlockMetadataWithNotify(i, j, k, ownStrength);
         world.markBlockRangeForRenderUpdate(i, j, k, i, j, k);
      }

   }

   public int getStrengthFrom(World world, int i, int j, int k) {
      int strength = 0;
      if(this.isPillar(world, i, j - 1, k)) {
         strength = this.getStandardStrength() + 1;
      }

      strength = this.compareStrengthTo(world, i, j - 1, k, strength);
      strength = this.compareStrengthTo(world, i + 1, j, k, strength);
      strength = this.compareStrengthTo(world, i - 1, j, k, strength);
      strength = this.compareStrengthTo(world, i, j, k + 1, strength);
      strength = this.compareStrengthTo(world, i, j, k - 1, strength);
      return strength - 1;
   }

   public int compareStrengthTo(World world, int i, int j, int k, int strength) {
      int s = 0;
      if(world.getBlockId(i, j, k) == super.blockID) {
         s = world.getBlockMetadata(i, j, k);
         if(s > this.getReinforcedStrength()) {
            s = this.getReinforcedStrength();
         }
      }

      return s > strength?s:strength;
   }

   public void updateSupportStatus(World world, int i, int j, int k) {
      world.scheduleBlockUpdate(i, j, k, super.blockID, tickDelay);
   }

}
