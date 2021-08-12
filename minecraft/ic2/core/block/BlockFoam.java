package ic2.core.block;

import cpw.mods.fml.common.registry.GameRegistry;
import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.block.BlockTex;
import ic2.core.item.block.ItemBlockRare;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockFoam extends BlockTex {

   public BlockFoam(int id, int sprite) {
      super(id, sprite, Material.cloth);
      this.setTickRandomly(true);
      this.setHardness(0.01F);
      this.setResistance(10.0F);
      this.setBlockName("blockFoam");
      this.setStepSound(Block.soundClothFootstep);
      this.setCreativeTab(IC2.tabIC2);
      Ic2Items.constructionFoam = new ItemStack(this);
      GameRegistry.registerBlock(this, ItemBlockRare.class, "constructionFoam");
   }

   public int tickRate() {
      return 500;
   }

   public int quantityDropped(Random r) {
      return 0;
   }

   public boolean isOpaqueCube() {
      return false;
   }

   public boolean isBlockNormalCube(World world, int i, int j, int k) {
      return true;
   }

   public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int i, int j, int k) {
      return null;
   }

   public boolean isBlockSolid(IBlockAccess world, int x, int y, int z, int side) {
      return false;
   }

   public void updateTick(World world, int i, int j, int k, Random random) {
      if(IC2.platform.isSimulating()) {
         if(world.getBlockLightValue(i, j, k) * 6 >= world.rand.nextInt(1000)) {
            world.setBlockAndMetadataWithNotify(i, j, k, Ic2Items.constructionFoamWall.itemID, 7);
            IC2.network.announceBlockUpdate(world, i, j, k);
         } else {
            world.scheduleBlockUpdate(i, j, k, super.blockID, this.tickRate());
         }

      }
   }

   public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer entityplayer, int side, float a, float b, float c) {
      ItemStack cur = entityplayer.getCurrentEquippedItem();
      if(cur != null && cur.itemID == Block.sand.blockID) {
         world.setBlockAndMetadataWithNotify(i, j, k, Ic2Items.constructionFoamWall.itemID, 7);
         IC2.network.announceBlockUpdate(world, i, j, k);
         if(!entityplayer.capabilities.isCreativeMode) {
            --cur.stackSize;
            if(cur.stackSize <= 0) {
               entityplayer.inventory.mainInventory[entityplayer.inventory.currentItem] = null;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean canPlaceBlockAt(World world, int i, int j, int k) {
      int id = world.getBlockId(i, j, k);
      return id == 0 || id == Block.fire.blockID || world.getBlockMaterial(i, j, k).isLiquid();
   }

   public ItemStack createStackedBlock(int i) {
      return null;
   }
}
