package ic2.core.block;

import ic2.core.IC2;
import ic2.core.block.BlockTex;
import ic2.core.block.EntityIC2Explosive;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public abstract class BlockIC2Explosive extends BlockTex {

   public boolean canExplodeByHand = false;


   public BlockIC2Explosive(int id, int sprite, boolean manual) {
      super(id, sprite, Material.tnt);
      this.canExplodeByHand = manual;
   }

   public int getBlockTextureFromSide(int i) {
      return i == 0?super.blockIndexInTexture:(i == 1?super.blockIndexInTexture + 1:super.blockIndexInTexture + 2);
   }

   public void onBlockAdded(World world, int i, int j, int k) {
      super.onBlockAdded(world, i, j, k);
      if(world.isBlockIndirectlyGettingPowered(i, j, k)) {
         this.removeBlockByPlayer(world, (EntityPlayer)null, i, j, k);
      }

   }

   public void onNeighborBlockChange(World world, int i, int j, int k, int l) {
      if(l > 0 && Block.blocksList[l].canProvidePower() && world.isBlockIndirectlyGettingPowered(i, j, k)) {
         this.removeBlockByPlayer(world, (EntityPlayer)null, i, j, k);
      }

   }

   public int quantityDropped(Random random) {
      return 0;
   }

   public void onBlockDestroyedByExplosion(World world, int i, int j, int k) {
      EntityIC2Explosive entitytntprimed = this.getExplosionEntity(world, (float)i + 0.5F, (float)j + 0.5F, (float)k + 0.5F, (String)null);
      entitytntprimed.fuse = world.rand.nextInt(entitytntprimed.fuse / 4) + entitytntprimed.fuse / 8;
      world.spawnEntityInWorld(entitytntprimed);
   }

   public boolean removeBlockByPlayer(World world, EntityPlayer player, int i, int j, int k) {
      if(!IC2.platform.isSimulating()) {
         return false;
      } else {
         int l = world.getBlockMetadata(i, j, k);
         world.setBlockWithNotify(i, j, k, 0);
         if(player != null && (l & 1) == 0 && !this.canExplodeByHand) {
            this.dropBlockAsItem_do(world, i, j, k, new ItemStack(super.blockID, 1, 0));
         } else {
            this.onIgnite(world, player, i, j, k);
            EntityIC2Explosive entitytntprimed = this.getExplosionEntity(world, (float)i + 0.5F, (float)j + 0.5F, (float)k + 0.5F, player == null?null:player.username);
            world.spawnEntityInWorld(entitytntprimed);
            world.playSoundAtEntity(entitytntprimed, "random.fuse", 1.0F, 1.0F);
         }

         return false;
      }
   }

   public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9) {
      if(par5EntityPlayer.getCurrentEquippedItem() != null && par5EntityPlayer.getCurrentEquippedItem().itemID == Item.flintAndSteel.itemID) {
         par1World.setBlockMetadataWithNotify(par2, par3, par4, 1);
         this.removeBlockByPlayer(par1World, par5EntityPlayer, par2, par3, par4);
         return true;
      } else {
         return super.onBlockActivated(par1World, par2, par3, par4, par5EntityPlayer, par6, par7, par8, par9);
      }
   }

   public abstract EntityIC2Explosive getExplosionEntity(World var1, float var2, float var3, float var4, String var5);

   public void onIgnite(World world, EntityPlayer player, int x, int y, int z) {}
}
