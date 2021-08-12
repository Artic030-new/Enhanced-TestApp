package ic2.advancedmachines.client;

import codechicken.nei.MultiItemRange;
import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import ic2.advancedmachines.common.AdvancedMachines;

public class NEIAdvancedMachinesConfig implements IConfigureNEI {

   public void loadConfig() {
      try {
         this.addSubSet();
      } catch (Exception var2) {
         var2.printStackTrace();
      }

   }

   private void addSubSet() {
      MultiItemRange advancedMachines = new MultiItemRange();
      int blockID = AdvancedMachines.blockAdvancedMachine.blockID;
      advancedMachines.add(blockID, 0, 0);
      advancedMachines.add(blockID, 1, 1);
      advancedMachines.add(blockID, 2, 2);
      API.addSetRange("IC2.AdvancedMachines", advancedMachines);
   }

   public String getName() {
      return "AdvancedMachines";
   }

   public String getVersion() {
      return "1.0.0";
   }
}
