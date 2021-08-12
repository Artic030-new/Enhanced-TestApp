package ic2.core.block.wiring;

import ic2.core.block.wiring.TileEntityElectricBlock;

public class TileEntityElectricMFE extends TileEntityElectricBlock {

   public TileEntityElectricMFE() {
      super(2, 128, 600000);
   }

   public String getInvName() {
      return "MFE";
   }
}
