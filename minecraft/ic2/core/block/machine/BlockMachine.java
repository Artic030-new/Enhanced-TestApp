package ic2.core.block.machine;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.block.BlockMultiID;
import ic2.core.block.TileEntityBlock;
import ic2.core.block.machine.tileentity.TileEntityCanner;
import ic2.core.block.machine.tileentity.TileEntityCompressor;
import ic2.core.block.machine.tileentity.TileEntityElecFurnace;
import ic2.core.block.machine.tileentity.TileEntityElectrolyzer;
import ic2.core.block.machine.tileentity.TileEntityExtractor;
import ic2.core.block.machine.tileentity.TileEntityInduction;
import ic2.core.block.machine.tileentity.TileEntityIronFurnace;
import ic2.core.block.machine.tileentity.TileEntityMacerator;
import ic2.core.block.machine.tileentity.TileEntityMagnetizer;
import ic2.core.block.machine.tileentity.TileEntityMatter;
import ic2.core.block.machine.tileentity.TileEntityMiner;
import ic2.core.block.machine.tileentity.TileEntityPump;
import ic2.core.block.machine.tileentity.TileEntityRecycler;
import ic2.core.block.machine.tileentity.TileEntityTerra;
import ic2.core.item.block.ItemMachine;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockMachine extends BlockMultiID {

   public BlockMachine(int i) {
      super(i, Material.iron);
      this.setHardness(2.0F);
      this.setStepSound(Block.soundMetalFootstep);
      this.setCreativeTab(IC2.tabIC2);
      Ic2Items.machine = new ItemStack(this, 1, 0);
      Ic2Items.advancedMachine = new ItemStack(this, 1, 12);
      Ic2Items.ironFurnace = new ItemStack(this, 1, 1);
      Ic2Items.electroFurnace = new ItemStack(this, 1, 2);
      Ic2Items.macerator = new ItemStack(this, 1, 3);
      Ic2Items.extractor = new ItemStack(this, 1, 4);
      Ic2Items.compressor = new ItemStack(this, 1, 5);
      Ic2Items.canner = new ItemStack(this, 1, 6);
      Ic2Items.miner = new ItemStack(this, 1, 7);
      Ic2Items.pump = new ItemStack(this, 1, 8);
      Ic2Items.magnetizer = new ItemStack(this, 1, 9);
      Ic2Items.electrolyzer = new ItemStack(this, 1, 10);
      Ic2Items.recycler = new ItemStack(this, 1, 11);
      Ic2Items.inductionFurnace = new ItemStack(this, 1, 13);
      Ic2Items.massFabricator = new ItemStack(this, 1, 14);
      Ic2Items.terraformer = new ItemStack(this, 1, 15);
      GameRegistry.registerBlock(this, ItemMachine.class, "blockMachine");
   }

   public String getTextureFile() {
      return "/ic2/sprites/block_machine.png";
   }

   public int idDropped(int meta, Random random, int j) {
      switch(meta) {
      default:
         return super.blockID;
      }
   }

   public int damageDropped(int meta) {
      switch(meta) {
      case 1:
         return meta;
      case 2:
         return meta;
      case 3:
      case 4:
      case 5:
      case 6:
      case 7:
      case 8:
      case 10:
      case 11:
      default:
         return 0;
      case 9:
         return meta;
      case 12:
         return 12;
      case 13:
         return 12;
      case 14:
         return 12;
      case 15:
         return 12;
      }
   }

   public TileEntityBlock createNewTileEntity(World world, int meta) {
      switch(meta) {
      case 1:
         return new TileEntityIronFurnace();
      case 2:
         return new TileEntityElecFurnace();
      case 3:
         return new TileEntityMacerator();
      case 4:
         return new TileEntityExtractor();
      case 5:
         return new TileEntityCompressor();
      case 6:
         return new TileEntityCanner();
      case 7:
         return new TileEntityMiner();
      case 8:
         return new TileEntityPump();
      case 9:
         return new TileEntityMagnetizer();
      case 10:
         return new TileEntityElectrolyzer();
      case 11:
         return new TileEntityRecycler();
      case 12:
      default:
         return new TileEntityBlock();
      case 13:
         return new TileEntityInduction();
      case 14:
         return new TileEntityMatter();
      case 15:
         return new TileEntityTerra();
      }
   }

   public void randomDisplayTick(World world, int i, int j, int k, Random random) {
      if(IC2.platform.isRendering()) {
         int meta = world.getBlockMetadata(i, j, k);
         float f2;
         float fmod;
         float f1mod;
         float f2mod;
         if(meta == 1 && isActive(world, i, j, k)) {
            TileEntity f = world.getBlockTileEntity(i, j, k);
            short f1 = f instanceof TileEntityBlock?((TileEntityBlock)f).getFacing():0;
            f2 = (float)i + 0.5F;
            float z = (float)j + 0.0F + random.nextFloat() * 6.0F / 16.0F;
            fmod = (float)k + 0.5F;
            f1mod = 0.52F;
            f2mod = random.nextFloat() * 0.6F - 0.3F;
            switch(f1) {
            case 2:
               world.spawnParticle("smoke", (double)(f2 + f2mod), (double)z, (double)(fmod - f1mod), 0.0D, 0.0D, 0.0D);
               world.spawnParticle("flame", (double)(f2 + f2mod), (double)z, (double)(fmod - f1mod), 0.0D, 0.0D, 0.0D);
               break;
            case 3:
               world.spawnParticle("smoke", (double)(f2 + f2mod), (double)z, (double)(fmod + f1mod), 0.0D, 0.0D, 0.0D);
              
               break;
            case 4:
               world.spawnParticle("smoke", (double)(f2 - f1mod), (double)z, (double)(fmod + f2mod), 0.0D, 0.0D, 0.0D);
               world.spawnParticle("flame", (double)(f2 - f1mod), (double)z, (double)(fmod + f2mod), 0.0D, 0.0D, 0.0D);
               break;
            case 5:
               world.spawnParticle("smoke", (double)(f2 + f1mod), (double)z, (double)(fmod + f2mod), 0.0D, 0.0D, 0.0D);
               world.spawnParticle("flame", (double)(f2 + f1mod), (double)z, (double)(fmod + f2mod), 0.0D, 0.0D, 0.0D);
            }
         }

         if(meta == 3 && isActive(world, i, j, k)) {
            float var14 = (float)i + 1.0F;
            float var15 = (float)j + 1.0F;
            f2 = (float)k + 1.0F;

            for(int var16 = 0; var16 < 4; ++var16) {
               fmod = -0.2F - random.nextFloat() * 0.6F;
               f1mod = 0.1F + random.nextFloat() * 0.4F;
               f2mod = -0.2F - random.nextFloat() * 0.6F;
               world.spawnParticle("smoke", (double)(var14 + fmod), (double)(var15 + f1mod - 0.5F), (double)(f2 + f2mod), 0.0D, 0.0D, 0.0D);
               world.spawnParticle("crit", (double)(var14 + fmod), (double)(var15 + f1mod), (double)(f2 + f2mod), 0.0D, 0.0D, 0.0D);
               world.spawnParticle("smoke", (double)(var14 + fmod), (double)(var15 + f1mod + 0.25F), (double)(f2 + f2mod), 0.0D, 0.0D, 0.0D);

            }
         }

      }
   }

   @SideOnly(Side.CLIENT)
   public EnumRarity getRarity(ItemStack stack) {
      return stack.getItemDamage() == 14?EnumRarity.rare:(stack.getItemDamage() != 15 && stack.getItemDamage() != 13 && stack.getItemDamage() != 12?EnumRarity.common:EnumRarity.uncommon);
   }
}
