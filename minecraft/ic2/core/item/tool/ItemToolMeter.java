package ic2.core.item.tool;

import ic2.api.energy.tile.IEnergyConductor;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import ic2.core.EnergyNet;
import ic2.core.IC2;
import ic2.core.item.ItemIC2;
import ic2.core.util.StackUtil;
import java.text.DecimalFormat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class ItemToolMeter extends ItemIC2 {

   public ItemToolMeter(int i, int index) {
      super(i, index);
      super.maxStackSize = 1;
      this.setMaxDamage(0);
   }

   public boolean onItemUseFirst(ItemStack itemstack, EntityPlayer entityplayer, World world, int i, int j, int k, int l, float hitX, float hitY, float hitZ) {
      TileEntity tileEntity = world.getBlockTileEntity(i, j, k);
      if((tileEntity instanceof IEnergySource || tileEntity instanceof IEnergyConductor || tileEntity instanceof IEnergySink) && IC2.platform.isSimulating()) {
         NBTTagCompound nbtData = StackUtil.getOrCreateNbtData(itemstack);
         long currentTotalEnergyEmitted = EnergyNet.getForWorld(world).getTotalEnergyEmitted(tileEntity);
         long currentTotalEnergySunken = EnergyNet.getForWorld(world).getTotalEnergySunken(tileEntity);
         long currentMeasureTime = world.getWorldTime();
         if(nbtData.getInteger("lastMeasuredTileEntityX") == i && nbtData.getInteger("lastMeasuredTileEntityY") == j && nbtData.getInteger("lastMeasuredTileEntityZ") == k) {
            long measurePeriod = currentMeasureTime - nbtData.getLong("lastMeasureTime");
            if(measurePeriod < 1L) {
               measurePeriod = 1L;
            }

            double deltaEmitted = (double)(currentTotalEnergyEmitted - nbtData.getLong("lastTotalEnergyEmitted")) / (double)measurePeriod;
            double deltaSunken = (double)(currentTotalEnergySunken - nbtData.getLong("lastTotalEnergySunken")) / (double)measurePeriod;
            DecimalFormat powerFormat = new DecimalFormat("0.##");
            IC2.platform.messagePlayer(entityplayer, "Measured power [EU/t]: " + powerFormat.format(deltaSunken) + " in " + powerFormat.format(deltaEmitted) + " out " + powerFormat.format(deltaSunken - deltaEmitted) + " gain" + " (avg. over " + measurePeriod + " ticks)");
         } else {
            nbtData.setInteger("lastMeasuredTileEntityX", i);
            nbtData.setInteger("lastMeasuredTileEntityY", j);
            nbtData.setInteger("lastMeasuredTileEntityZ", k);
            IC2.platform.messagePlayer(entityplayer, "Starting new measurement");
         }

         nbtData.setLong("lastTotalEnergyEmitted", currentTotalEnergyEmitted);
         nbtData.setLong("lastTotalEnergySunken", currentTotalEnergySunken);
         nbtData.setLong("lastMeasureTime", currentMeasureTime);
         return true;
      } else {
         return false;
      }
   }
}
