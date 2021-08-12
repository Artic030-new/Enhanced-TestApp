package ic2.core.block.generator.tileentity;

import ic2.core.ContainerIC2;
import ic2.core.IC2;
import ic2.core.block.generator.container.ContainerSolarGenerator;
import ic2.core.block.generator.tileentity.TileEntityBaseGenerator;
import java.util.Random;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenDesert;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.ISidedInventory;

public class TileEntitySolarGenerator extends TileEntityBaseGenerator implements ISidedInventory {

   public static Random randomizer = new Random();
   public int ticker;
   public boolean sunIsVisible = false;


   public TileEntitySolarGenerator() {
      super(1, 1, 1);
      this.ticker = randomizer.nextInt(this.tickRate());
   }

   public void onLoaded() {
      super.onLoaded();
      this.updateSunVisibility();
   }

   public int gaugeFuelScaled(int i) {
      return i;
   }

   public boolean gainEnergy() {
      if(this.ticker++ % this.tickRate() == 0) {
         this.updateSunVisibility();
      }

      if(!this.sunIsVisible) {
         return false;
      } else {
         double gen = (double)IC2.energyGeneratorSolar / 100.0D;
         if(gen >= 1.0D || this.ticker % (100 - IC2.energyGeneratorSolar) == 0) {
            super.storage = (short)(super.storage + (int)Math.ceil(gen));
         }

         return true;
      }
   }

   public boolean gainFuel() {
      return false;
   }

   public void updateSunVisibility() {
      this.sunIsVisible = isSunVisible(super.worldObj, super.xCoord, super.yCoord + 1, super.zCoord);
   }

   public static boolean isSunVisible(World world, int x, int y, int z) {
      return world.isDaytime() && !world.provider.hasNoSky && world.canBlockSeeTheSky(x, y, z) && (world.getWorldChunkManager().getBiomeGenAt(x, z) instanceof BiomeGenDesert || !world.isRaining() && !world.isThundering());
   }

   public boolean needsFuel() {
      return true;
   }

   public String getInvName() {
      return "Solar Panel";
   }

   public ContainerIC2 getGuiContainer(EntityPlayer entityPlayer) {
      return new ContainerSolarGenerator(entityPlayer, this);
   }

   public String getGuiClassName(EntityPlayer entityPlayer) {
      return "block.generator.gui.GuiSolarGenerator";
   }

   public int tickRate() {
      return 128;
   }

   public boolean delayActiveUpdate() {
      return true;
   }

   public int getStartInventorySide(ForgeDirection side) {
      return 0;
   }

   public int getSizeInventorySide(ForgeDirection side) {
      return 1;
   }

}
