package ic2.core.block.wiring;

import ic2.core.block.wiring.TileEntityElectricBlock;

public class TileEntityElectricBatBox extends TileEntityElectricBlock {

   public TileEntityElectricBatBox() {
      super(1, 32, '\u9c40');
   }

   public String getInvName() {
      return "BatBox";
   }
}
