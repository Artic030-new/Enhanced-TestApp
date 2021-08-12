package ic2.core.block;

import cpw.mods.fml.common.registry.GameRegistry;
import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.block.WorldGenRubTree;
import ic2.core.item.block.ItemBlockRare;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSapling;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class BlockRubSapling extends BlockSapling {

   public BlockRubSapling(int id, int sprite) {
      super(id, sprite);
      this.setHardness(0.0F);
      this.setStepSound(Block.soundGrassFootstep);
      this.setBlockName("blockRubSapling");
      this.setCreativeTab(IC2.tabIC2);
      Ic2Items.rubberSapling = new ItemStack(this);
      GameRegistry.registerBlock(this, ItemBlockRare.class, "rubberSapling");
   }

   public String getTextureFile() {
      return "/ic2/sprites/block_0.png";
   }

   public int getBlockTextureFromSideAndMetadata(int i, int j) {
      return super.blockIndexInTexture;
   }

   public void updateTick(World world, int i, int j, int k, Random random) {
      if(IC2.platform.isSimulating()) {
         if(!this.canBlockStay(world, i, j, k)) {
            this.dropBlockAsItem(world, i, j, k, world.getBlockMetadata(i, j, k), 0);
            world.setBlockWithNotify(i, j, k, 0);
         } else {
            if(world.getBlockLightValue(i, j + 1, k) >= 9 && random.nextInt(30) == 0) {
               this.growTree(world, i, j, k, random);
            }

         }
      }
   }

   public void growTree(World world, int i, int j, int k, Random random) {
      (new WorldGenRubTree()).grow(world, i, j, k, random);
   }

   public int damageDropped(int i) {
      return 0;
   }

   public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer entityplayer, int side, float a, float b, float c) {
      if(!IC2.platform.isSimulating()) {
         return false;
      } else {
         ItemStack equipped = entityplayer.getCurrentEquippedItem();
         if(equipped == null) {
            return false;
         } else {
            if(equipped.getItem() == Item.dyePowder && equipped.getItemDamage() == 15) {
               this.growTree(world, i, j, k, world.rand);
               if(!entityplayer.capabilities.isCreativeMode) {
                  --equipped.stackSize;
               }

               entityplayer.swingItem();
            }

            return false;
         }
      }
   }

   public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs, List par3List) {
      par3List.add(new ItemStack(par1, 1, 0));
   }
}
