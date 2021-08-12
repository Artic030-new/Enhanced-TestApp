package ic2.core.block.machine.tileentity;

import ic2.api.Ic2Recipes;
import ic2.core.Ic2Items;
import ic2.core.block.machine.tileentity.TileEntityElectricMachine;
import ic2.core.block.machine.tileentity.TileEntityPump;
import java.util.List;
import java.util.Vector;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class TileEntityCompressor extends TileEntityElectricMachine {

   public TileEntityPump validPump;
   public static List recipes = new Vector();


   public TileEntityCompressor() {
      super(3, 2, 400, 32);
   }

   public static void init() {
      Ic2Recipes.addCompressorRecipe(Ic2Items.plantBall, Ic2Items.compressedPlantBall);
      Ic2Recipes.addCompressorRecipe(Ic2Items.hydratedCoalDust, Ic2Items.hydratedCoalClump);
      Ic2Recipes.addCompressorRecipe(new ItemStack(Block.netherrack, 3), new ItemStack(Block.netherBrick));
      Ic2Recipes.addCompressorRecipe(new ItemStack(Block.sand), new ItemStack(Block.sandStone));
      Ic2Recipes.addCompressorRecipe(new ItemStack(Item.snowball), new ItemStack(Block.ice));
      Ic2Recipes.addCompressorRecipe(Ic2Items.waterCell, new ItemStack(Item.snowball));
      Ic2Recipes.addCompressorRecipe(Ic2Items.mixedMetalIngot, Ic2Items.advancedAlloy);
      Ic2Recipes.addCompressorRecipe(Ic2Items.carbonMesh, Ic2Items.carbonPlate);
      Ic2Recipes.addCompressorRecipe(Ic2Items.coalBall, Ic2Items.compressedCoalBall);
      Ic2Recipes.addCompressorRecipe(Ic2Items.coalChunk, new ItemStack(Item.diamond));
      Ic2Recipes.addCompressorRecipe(Ic2Items.constructionFoam, Ic2Items.constructionFoamPellet);
      Ic2Recipes.addCompressorRecipe(Ic2Items.cell, Ic2Items.airCell);
   }

   public ItemStack getResultFor(ItemStack itemStack, boolean adjustInput) {
      return Ic2Recipes.getCompressorOutputFor(itemStack, adjustInput);
   }

   public boolean canOperate() {
      return this.getValidPump() == null?super.canOperate():super.inventory[2] == null || super.inventory[2].isItemEqual(new ItemStack(Item.snowball)) && super.inventory[2].stackSize < Item.snowball.getItemStackLimit();
   }

   public void operate() {
      if(this.canOperate()) {
         ItemStack processResult = null;
         if(super.inventory[0] != null) {
            processResult = this.getResultFor(super.inventory[0], false);
         }

         if(processResult == null) {
            TileEntityPump pump = this.getValidPump();
            if(pump == null) {
               return;
            }

            pump.pumpCharge = 0;
            super.worldObj.setBlockWithNotify(pump.xCoord, pump.yCoord - 1, pump.zCoord, 0);
            if(super.inventory[2] == null) {
               super.inventory[2] = new ItemStack(Item.snowball);
            } else {
               ++super.inventory[2].stackSize;
            }
         } else {
            super.operate();
         }

      }
   }

   public TileEntityPump getValidPump() {
      if(this.validPump != null && this.validPump.isPumpReady() && this.validPump.isWaterBelow()) {
         return this.validPump;
      } else {
         TileEntityPump pump;
         if(super.worldObj.getBlockTileEntity(super.xCoord, super.yCoord - 1, super.zCoord) instanceof TileEntityPump) {
            pump = (TileEntityPump)super.worldObj.getBlockTileEntity(super.xCoord, super.yCoord - 1, super.zCoord);
            if(pump.isPumpReady() && pump.isWaterBelow()) {
               return this.validPump = pump;
            }
         }

         if(super.worldObj.getBlockTileEntity(super.xCoord + 1, super.yCoord, super.zCoord) instanceof TileEntityPump) {
            pump = (TileEntityPump)super.worldObj.getBlockTileEntity(super.xCoord + 1, super.yCoord, super.zCoord);
            if(pump.isPumpReady() && pump.isWaterBelow()) {
               return this.validPump = pump;
            }
         }

         if(super.worldObj.getBlockTileEntity(super.xCoord - 1, super.yCoord, super.zCoord) instanceof TileEntityPump) {
            pump = (TileEntityPump)super.worldObj.getBlockTileEntity(super.xCoord - 1, super.yCoord, super.zCoord);
            if(pump.isPumpReady() && pump.isWaterBelow()) {
               return this.validPump = pump;
            }
         }

         if(super.worldObj.getBlockTileEntity(super.xCoord, super.yCoord, super.zCoord + 1) instanceof TileEntityPump) {
            pump = (TileEntityPump)super.worldObj.getBlockTileEntity(super.xCoord, super.yCoord, super.zCoord + 1);
            if(pump.isPumpReady() && pump.isWaterBelow()) {
               return this.validPump = pump;
            }
         }

         if(super.worldObj.getBlockTileEntity(super.xCoord, super.yCoord, super.zCoord - 1) instanceof TileEntityPump) {
            pump = (TileEntityPump)super.worldObj.getBlockTileEntity(super.xCoord, super.yCoord, super.zCoord - 1);
            if(pump.isPumpReady() && pump.isWaterBelow()) {
               return this.validPump = pump;
            }
         }

         return null;
      }
   }

   public String getInvName() {
      return "Compressor";
   }

   public String getGuiClassName(EntityPlayer entityPlayer) {
      return "block.machine.gui.GuiCompressor";
   }

   public String getStartSoundFile() {
      return "Machines/CompressorOp.ogg";
   }

   public String getInterruptSoundFile() {
      return "Machines/InterruptOne.ogg";
   }

   public float getWrenchDropRate() {
      return 0.85F;
   }

}
