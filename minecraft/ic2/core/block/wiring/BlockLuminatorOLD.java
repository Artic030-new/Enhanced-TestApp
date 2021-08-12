package ic2.core.block.wiring;

import ic2.core.block.wiring.TileEntityLuminator;
import ic2.core.block.wiring.TileEntityLuminatorOLD;
import java.util.Random;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockLuminatorOLD extends BlockContainer {

   public BlockLuminatorOLD(int id, int sprite) {
      super(id, sprite, Material.glass);
   }

   public int quantityDropped(Random random) {
      return 0;
   }

   public int getRenderBlockPass() {
      return 0;
   }

   public boolean isOpaqueCube() {
      return false;
   }

   public float getBlockBrightness(IBlockAccess iblockaccess, int i, int j, int k) {
      TileEntityLuminatorOLD lumi = (TileEntityLuminatorOLD)iblockaccess.getBlockTileEntity(i, j, k);
      return lumi == null?super.getBlockBrightness(iblockaccess, i, j, k):lumi.getLightLevel();
   }

   public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer entityplayer, int side, float a, float b, float c) {
      TileEntityLuminatorOLD lumi = (TileEntityLuminatorOLD)world.getBlockTileEntity(i, j, k);
      lumi.switchStrength();
      return true;
   }

   public TileEntity createNewTileEntity(World world) {
      return new TileEntityLuminator();
   }

   public String getTextureFile() {
      return "/ic2/sprites/block_0.png";
   }
}
