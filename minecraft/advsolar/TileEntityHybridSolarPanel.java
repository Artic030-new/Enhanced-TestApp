package advsolar;

import advsolar.AdvancedSolarPanel;
import advsolar.TileEntitySolarPanel;

public class TileEntityHybridSolarPanel extends TileEntitySolarPanel {

   public TileEntityHybridSolarPanel() {
      super("Гибридная солнечная панель", 3, AdvancedSolarPanel.hGenDay, AdvancedSolarPanel.hGenNight, AdvancedSolarPanel.hOutput, AdvancedSolarPanel.hStorage);
   }

   public String getInvName() {
      return "Hyb Solar Panel";
   }
}
