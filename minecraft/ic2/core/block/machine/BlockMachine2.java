package ic2.core.block.machine;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.block.BlockMultiID;
import ic2.core.block.TileEntityBlock;
import ic2.core.block.machine.tileentity.TileEntityCropmatron;
import ic2.core.block.machine.tileentity.TileEntityTeleporter;
import ic2.core.block.machine.tileentity.TileEntityTesla;
import ic2.core.item.block.ItemMachine2;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class BlockMachine2 extends BlockMultiID {

   public BlockMachine2(int i) {
      super(i, Material.iron);
      this.setHardness(2.0F);
      this.setStepSound(Block.soundMetalFootstep);
      this.setCreativeTab(IC2.tabIC2);
      Ic2Items.teleporter = new ItemStack(this, 1, 0);
      Ic2Items.teslaCoil = new ItemStack(this, 1, 1);
      Ic2Items.cropmatron = new ItemStack(this, 1, 2);
      GameRegistry.registerBlock(this, ItemMachine2.class, "blockMachine2");
   }

   public String getTextureFile() {
      return "/ic2/sprites/block_machine2.png";
   }

   public int idDropped(int meta, Random random, int j) {
      switch(meta) {
      case 0:
         return Ic2Items.advancedMachine.itemID;
      default:
         return Ic2Items.machine.itemID;
      }
   }

   public int damageDropped(int meta) {
      switch(meta) {
      case 0:
         return Ic2Items.advancedMachine.getItemDamage();
      default:
         return Ic2Items.machine.getItemDamage();
      }
   }

   public TileEntityBlock createNewTileEntity(World world, int meta) {
      switch(meta) {
      case 0:
         return new TileEntityTeleporter();
      case 1:
         return new TileEntityTesla();
      case 2:
         return new TileEntityCropmatron();
      default:
         return new TileEntityBlock();
      }
   }

   public void randomDisplayTick(World world, int i, int j, int k, Random random) {
      world.getBlockMetadata(i, j, k);
   }

   @SideOnly(Side.CLIENT)
   public EnumRarity getRarity(ItemStack stack) {
      return stack.getItemDamage() == 0?EnumRarity.rare:EnumRarity.common;
   }
}
