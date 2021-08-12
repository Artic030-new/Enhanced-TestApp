package ic2.api.energy.tile;

import ic2.api.Direction;
import ic2.api.energy.tile.IEnergyTile;
import net.minecraft.tileentity.TileEntity;

public interface IEnergyAcceptor extends IEnergyTile {

   boolean acceptsEnergyFrom(TileEntity var1, Direction var2);
}
