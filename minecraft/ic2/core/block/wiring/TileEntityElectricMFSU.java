package ic2.core.block.wiring;

import ic2.core.block.wiring.TileEntityElectricBlock;

public class TileEntityElectricMFSU extends TileEntityElectricBlock {

   public TileEntityElectricMFSU() {
      super(3, 512, 10000000);
   }

   public String getInvName() {
      return "MFSU";
   }
}
