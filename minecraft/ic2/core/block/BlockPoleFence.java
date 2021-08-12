package ic2.core.block;

import cpw.mods.fml.common.registry.GameRegistry;
import ic2.api.IMetalArmor;
import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.block.BlockTex;
import ic2.core.item.block.ItemBlockRare;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class BlockPoleFence extends BlockTex {

   public BlockPoleFence(int id, int sprite) {
      super(id, sprite, Material.iron);
      this.setHardness(1.5F);
      this.setResistance(5.0F);
      this.setStepSound(Block.soundMetalFootstep);
      this.setBlockName("blockFenceIron");
      this.setCreativeTab(IC2.tabIC2);
      Ic2Items.ironFence = new ItemStack(this);
      GameRegistry.registerBlock(this, ItemBlockRare.class, "ironFence");
   }

   public boolean isOpaqueCube() {
      return false;
   }

   public boolean renderAsNormalBlock() {
      return false;
   }

   public boolean isBlockNormalCube(World world, int i, int j, int k) {
      return false;
   }

   public int getRenderType() {
      return IC2.platform.getRenderId("fence");
   }

   public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int i, int j, int k) {
      return super.blockMaterial == Material.iron && this.isPole(world, i, j, k)?AxisAlignedBB.getBoundingBox((double)((float)i + 0.375F), (double)j, (double)((float)k + 0.375F), (double)((float)i + 0.625F), (double)((float)j + 1.0F), (double)((float)k + 0.625F)):AxisAlignedBB.getBoundingBox((double)i, (double)j, (double)k, (double)(i + 1), (double)((float)j + 1.5F), (double)(k + 1));
   }

   public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int i, int j, int k) {
      return super.blockMaterial == Material.iron && this.isPole(world, i, j, k)?AxisAlignedBB.getBoundingBox((double)((float)i + 0.375F), (double)j, (double)((float)k + 0.375F), (double)((float)i + 0.625F), (double)((float)j + 1.0F), (double)((float)k + 0.625F)):AxisAlignedBB.getBoundingBox((double)i, (double)j, (double)k, (double)(i + 1), (double)(j + 1), (double)(k + 1));
   }

   public boolean isPole(World world, int i, int j, int k) {
      return world.getBlockId(i - 1, j, k) != super.blockID && world.getBlockId(i + 1, j, k) != super.blockID && world.getBlockId(i, j, k - 1) != super.blockID && world.getBlockId(i, j, k + 1) != super.blockID;
   }

   public void onEntityCollidedWithBlock(World world, int i, int j, int k, Entity entity) {
      if(super.blockMaterial == Material.iron && this.isPole(world, i, j, k) && entity instanceof EntityPlayer) {
         boolean powered = world.getBlockMetadata(i, j, k) > 0;
         boolean metalShoes = false;
         EntityPlayer player = (EntityPlayer)entity;
         ItemStack shoes = player.inventory.armorInventory[0];
         if(shoes != null) {
            int id = shoes.itemID;
            if(id == Item.bootsSteel.itemID || id == Item.bootsGold.itemID || id == Item.bootsChain.itemID || shoes.getItem() instanceof IMetalArmor && ((IMetalArmor)shoes.getItem()).isMetalArmor(shoes, player)) {
               metalShoes = true;
            }
         }

         if(powered && metalShoes) {
            world.setBlockMetadata(i, j, k, world.getBlockMetadata(i, j, k) - 1);
            player.motionY += 0.07500000298023224D;
            if(player.motionY > 0.0D) {
               player.motionY *= 1.0299999713897705D;
               player.fallDistance = 0.0F;
            }

            if(player.isSneaking()) {
               if(player.motionY > 0.30000001192092896D) {
                  player.motionY = 0.30000001192092896D;
               }
            } else if(player.motionY > 1.5D) {
               player.motionY = 1.5D;
            }
         } else if(player.isSneaking()) {
            if(player.motionY < -0.25D) {
               player.motionY *= 0.8999999761581421D;
            } else {
               player.fallDistance = 0.0F;
            }
         }

      }
   }
}
