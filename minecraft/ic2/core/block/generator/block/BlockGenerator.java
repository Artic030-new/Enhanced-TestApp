package ic2.core.block.generator.block;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.block.BlockMultiID;
import ic2.core.block.TileEntityBlock;
import ic2.core.block.generator.tileentity.TileEntityGenerator;
import ic2.core.block.generator.tileentity.TileEntityGeoGenerator;
import ic2.core.block.generator.tileentity.TileEntityNuclearReactor;
import ic2.core.block.generator.tileentity.TileEntityNuclearReactorElectric;
import ic2.core.block.generator.tileentity.TileEntitySolarGenerator;
import ic2.core.block.generator.tileentity.TileEntityWaterGenerator;
import ic2.core.block.generator.tileentity.TileEntityWindGenerator;
import ic2.core.item.block.ItemGenerator;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class BlockGenerator extends BlockMultiID {

   public static Class tileEntityNuclearReactorClass = TileEntityNuclearReactorElectric.class;


   public BlockGenerator(int i) {
      super(i, Material.iron);
      this.setHardness(3.0F);
      this.setStepSound(Block.soundMetalFootstep);
      this.setCreativeTab(IC2.tabIC2);
      Ic2Items.generator = new ItemStack(this, 1, 0);
      Ic2Items.geothermalGenerator = new ItemStack(this, 1, 1);
      Ic2Items.waterMill = new ItemStack(this, 1, 2);
      Ic2Items.solarPanel = new ItemStack(this, 1, 3);
      Ic2Items.windMill = new ItemStack(this, 1, 4);
      Ic2Items.nuclearReactor = new ItemStack(this, 1, 5);
      GameRegistry.registerBlock(this, ItemGenerator.class, "blockGenerator");
   }

   public String getTextureFile() {
      return "/ic2/sprites/block_generator.png";
   }

   public int idDropped(int meta, Random random, int j) {
      switch(meta) {
      default:
         return super.blockID;
      }
   }

   public int damageDropped(int meta) {
      switch(meta) {
      case 2:
         return 2;
      default:
         return 0;
      }
   }

   public int quantityDropped(Random random) {
      return 1;
   }

   public TileEntityBlock createNewTileEntity(World world, int meta) {
      try {
         switch(meta) {
         case 0:
            return new TileEntityGenerator();
         case 1:
            return new TileEntityGeoGenerator();
         case 2:
            return new TileEntityWaterGenerator();
         case 3:
            return new TileEntitySolarGenerator();
         case 4:
            return new TileEntityWindGenerator();
         case 5:
            return (TileEntityBlock)tileEntityNuclearReactorClass.newInstance();
         default:
            return null;
         }
      } catch (Exception var4) {
         throw new RuntimeException(var4);
      }
   }

   public void randomDisplayTick(World world, int i, int j, int k, Random random) {
      if(IC2.platform.isRendering()) {
         int meta = world.getBlockMetadata(i, j, k);
         if(meta == 0 && isActive(world, i, j, k)) {
            TileEntityBlock var14 = (TileEntityBlock)world.getBlockTileEntity(i, j, k);
            short var15 = var14.getFacing();
            float f = (float)i + 0.5F;
            float f1 = (float)j + 0.0F + random.nextFloat() * 6.0F / 16.0F;
            float f2 = (float)k + 0.5F;
            float f3 = 0.52F;
            float f4 = random.nextFloat() * 0.6F - 0.3F;
            switch(var15) {
            case 2:
               world.spawnParticle("smoke", (double)(f + f4), (double)f1, (double)(f2 - f3), 0.0D, 0.0D, 0.0D);
               world.spawnParticle("flame", (double)(f + f4), (double)f1, (double)(f2 - f3), 0.0D, 0.0D, 0.0D);
               break;
            case 3:
               world.spawnParticle("smoke", (double)(f + f4), (double)f1, (double)(f2 + f3), 0.0D, 0.0D, 0.0D);
               world.spawnParticle("flame", (double)(f + f4), (double)f1, (double)(f2 + f3), 0.0D, 0.0D, 0.0D);
               break;
            case 4:
               world.spawnParticle("smoke", (double)(f - f3), (double)f1, (double)(f2 + f4), 0.0D, 0.0D, 0.0D);
               world.spawnParticle("flame", (double)(f - f3), (double)f1, (double)(f2 + f4), 0.0D, 0.0D, 0.0D);
               break;
            case 5:
               world.spawnParticle("smoke", (double)(f + f3), (double)f1, (double)(f2 + f4), 0.0D, 0.0D, 0.0D);
               world.spawnParticle("flame", (double)(f + f3), (double)f1, (double)(f2 + f4), 0.0D, 0.0D, 0.0D);
            }
         } else if(meta == 5) {
            int puffs = ((TileEntityNuclearReactor)world.getBlockTileEntity(i, j, k)).heat / 1000;
            if(puffs <= 0) {
               return;
            }

            puffs = world.rand.nextInt(puffs);

            int n;
            for(n = 0; n < puffs; ++n) {
               world.spawnParticle("smoke", (double)((float)i + random.nextFloat()), (double)((float)j + 0.95F), (double)((float)k + random.nextFloat()), 0.0D, 0.0D, 0.0D);
            }

            puffs -= world.rand.nextInt(4) + 3;

            for(n = 0; n < puffs; ++n) {
               world.spawnParticle("flame", (double)((float)i + random.nextFloat()), (double)((float)j + 1.0F), (double)((float)k + random.nextFloat()), 0.0D, 0.0D, 0.0D);
            }
         }

      }
   }

   public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer entityplayer, int side, float a, float b, float c) {
      return entityplayer.getCurrentEquippedItem() != null && entityplayer.getCurrentEquippedItem().isItemEqual(Ic2Items.reactorChamber)?false:super.onBlockActivated(world, i, j, k, entityplayer, side, a, b, c);
   }

   @SideOnly(Side.CLIENT)
   public EnumRarity getRarity(ItemStack stack) {
      return stack.getItemDamage() == 5?EnumRarity.uncommon:EnumRarity.common;
   }

}
